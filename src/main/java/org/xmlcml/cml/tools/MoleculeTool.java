/* Copyright 2011 Peter Murray-Rust et. al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.cml.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.molutil.ChemicalElement.AS;

/**
 * additional tools for molecule. not fully developed
 *
 * @author pmr
 */
public class MoleculeTool {

	static final Logger LOG = Logger.getLogger(MoleculeTool.class.getName());

	/**
	 * get the average atom-atom bond distance.
	 *
	 * This is primarily for scaling purposes and should not be taken too
	 * seriously.
	 *
	 * @param type
	 * @param omitHydrogens coordinates
	 * @return average distance in origianl units. If no bonds returns negative.
	 */
	public static double getAverageBondLength(CMLMolecule molecule, CoordinateType type, boolean omitHydrogens) {
		double bondSum = 0.0;
		int nBonds = 0;
		for (CMLBond bond : molecule.getBonds()) {
			if (omitHydrogens && (
					"H".equals(bond.getAtom(0).getElementType()) ||
					"H".equals(bond.getAtom(1).getElementType()))
					) {
				continue;
			}
			try {
				double length = bond.calculateBondLength(type);
				if (!Double.isNaN(length)) {
					LOG.trace("len "+length);
					bondSum += length;
					nBonds++;
				}
			} catch (RuntimeException e) {
				// no coordinates
			}
		}
		return (nBonds == 0 || Double.isNaN(bondSum) || Real.isZero(bondSum, Real.EPS)) 
		    ? Double.NaN : bondSum / ((double) nBonds);
	}

	/**
	 * transform
	 * @param t2
	 */
	public static void transform(CMLMolecule molecule, Transform2 t2) {
		for (CMLAtom atom : molecule.getAtoms()) {
			if (atom.hasCoordinates(CoordinateType.TWOD)) {
				Real2 dd = new Real2(atom.getX2(), atom.getY2());
				dd.transformBy(t2);
				atom.setXY2(dd);
			}
		}
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 * ignore if hydrogenCount attribute is set
	 * Uses algorithm: nH = 8 - group - sumbondorder + formalCharge, where group
	 * is 0-8 in first two rows
	 *
	 * @param atom
	 * @param control specifies whether H are explicit or in hydrogenCount
	 */
	public static void adjustHydrogenCountsToValency(CMLMolecule molecule, CMLAtom atom,
			CMLMolecule.HydrogenControl control) {
		if (atom.getHydrogenCountAttribute() == null) {
			int group = getHydrogenValencyGroup(atom);
			if (group == -1) {
				return;
			} else if (group == -2) {
				return;
			}
			// hydrogen and metals
			if (group < 4) {
				return;
			}
			int sumBo = getSumNonHydrogenBondOrder(molecule, atom);
			int fc = (atom.getFormalChargeAttribute() == null) ? 0 : atom
					.getFormalCharge();
			int nh = 8 - group - sumBo + fc;
			// non-octet species
			if (group == 4 && fc == 1) {
				nh -= 2;
			}
			atom.setHydrogenCount(nh);
			expandImplicitHydrogens(molecule, atom, control);
		}
	}

	/**
	 * a simple lookup for common atoms.
	 *
	 * examples are C, N, O, F, Si, P, S, Cl, Br, I if atom has electronegative
	 * ligands, (O, F, Cl...) returns -1
	 *
	 * @param atom
	 * @return group
	 */
	public static int getHydrogenValencyGroup(CMLAtom atom) {
		final int[] group = { 1, 4, 5, 6, 7, 4, 5, 6, 7, 7, 7 };
		final int[] eneg0 = { 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1 };
		final int[] eneg1 = { 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1 };
		int elNum = -1;
		try {
			String elType = atom.getElementType();
			elNum = CMLAtom.getCommonElementSerialNumber(elType);
			if (elNum == -1) {
				return -1;
			}
			if (eneg0[elNum] == 0) {
				return group[elNum];
			}
			// if atom is susceptible to enegative ligands, exit if they are
			// present
			List<CMLAtom> ligandList = atom.getLigandAtoms();
			for (CMLAtom lig : ligandList) {
				int ligElNum = CMLAtom.getCommonElementSerialNumber(lig
						.getElementType());
				if (ligElNum == -1 || eneg1[ligElNum] == 1) {
					return -2;
				}
			}
		} catch (Exception e) {
			LOG.error("BUG " + e);
		}
		int g = (elNum == -1) ? -1 : group[elNum];
		return g;
	}

	/**
	 * Expand implicit hydrogen atoms.
	 *
	 * This needs looking at
	 *
	 * CMLMolecule.NO_EXPLICIT_HYDROGENS 
	 * CMLMolecule.USE_HYDROGEN_COUNT // no
	 * action
	 *
	 * @param atom
	 * @param control
	 * @throws RuntimeException
	 */
	public static void expandImplicitHydrogens(CMLMolecule molecule, CMLAtom atom,
			CMLMolecule.HydrogenControl control) throws RuntimeException {
		if (control.equals(CMLMolecule.HydrogenControl.USE_HYDROGEN_COUNT)) {
			return;
		}
		if (atom.getHydrogenCountAttribute() == null
				|| atom.getHydrogenCount() == 0) {
			return;
		}
		int hydrogenCount = atom.getHydrogenCount();
		int currentHCount = 0;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (ligand.getElementType().equals(AS.H.value)) {
				currentHCount++;
			}
		}
		// FIXME. This needs rethinking
		if (control.equals(CMLMolecule.HydrogenControl.NO_EXPLICIT_HYDROGENS)
				&& currentHCount != 0) {
			return;
		}
		String id = atom.getId();
		for (int i = 0; i < hydrogenCount - currentHCount; i++) {
			CMLAtom hatom = new CMLAtom(id + "_h" + (i + 1));
			molecule.addAtom(hatom);
			hatom.setElementType(AS.H.value);
			CMLBond bond = new CMLBond(atom, hatom);
			molecule.addBond(bond);
			bond.setOrder("1");
		}
	}

	/**
	 * Sums the formal orders of all bonds from atom to non-hydrogen ligands.
	 *
	 * Uses 1,2,3,A orders and creates the nearest integer. Thus 2 aromatic
	 * bonds sum to 3 and 3 sum to 4. Bonds without order are assumed to be
	 * single
	 * @param molecule 
	 *
	 * @param atom
	 * @exception RuntimeException
	 *                null atom in argument
	 * @return sum of bond orders. May be 0 for isolated atom or atom with only
	 *         H ligands
	 */
	public static int getSumNonHydrogenBondOrder(CMLMolecule molecule, CMLAtom atom)
	throws RuntimeException {
		float sumBo = 0.0f;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (AS.H.equals(ligand.getElementType())) {
				continue;
			}
			CMLBond bond = molecule.getBond(atom, ligand);
			if (bond == null) {
				throw new RuntimeException(
						"Serious bug in getSumNonHydrogenBondOrder");
			}
			String bo = bond.getOrder();
			if (bo != null) {
				if (CMLBond.isSingle(bo)) {
					sumBo += 1.0;
				}
				if (CMLBond.isDouble(bo)) {
					sumBo += 2.0;
				}
				if (CMLBond.isTriple(bo)) {
					sumBo += 3.0;
				}
				if (bo.equals(CMLBond.AROMATIC)) {
					sumBo += 1.4;
				}
			} else {
				// if no bond order, assume single
				sumBo += 1.0;
			}
		}
		return Math.round(sumBo);
	}

	/**
	 * Add or delete hydrogen atoms to satisy valence.
	 *
	 * does not use CDK
	 *
	 * @param control
	 *            specifies whether H are explicit or in hydorgenCount
	 */
	public static void adjustHydrogenCountsToValency(CMLMolecule molecule, HydrogenControl control) {
		if (molecule.isMoleculeContainer()) {
			CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
			for (CMLMolecule mol : molecules) {
				adjustHydrogenCountsToValency(mol, control);
			}
		} else {
			List<CMLAtom> atoms = molecule.getAtoms();
			for (CMLAtom atom : atoms) {
				AtomTool.adjustHydrogenCountsToValency(atom, molecule, control);
			}
		}
	}

	/** calculates hydrogen coordinates for hydrogens already on atom
	 * assumes they are the correct count
	 */
	public static void addCalculated3DCoordinatesForExistingHydrogens(CMLMolecule molecule) {
		List<CMLAtom> atomList = molecule.getAtoms();
		for (CMLAtom atom : atomList) {
			AtomTool.addCalculated3DCoordinatesForExistingHydrogens(atom);
		}
	}

}
