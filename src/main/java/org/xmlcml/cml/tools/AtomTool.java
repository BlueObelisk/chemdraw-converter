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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomSet;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Range;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Point3;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.euclid.Vector3;
import org.xmlcml.molutil.ChemicalElement;
import org.xmlcml.molutil.Molutils;
import org.xmlcml.molutil.ChemicalElement.AS;

import nu.xom.Attribute;

/**
 * Additional tools for atom. Not fully developed.
 * 
 * @author pmr
 */
public class AtomTool {

	static final Logger LOG = Logger.getLogger(AtomTool.class.getName());

	/**
	 * Adds or delete hydrogen atoms to satisfy valence.
	 * <p>
	 * Ignore if hydrogenCount attribute is set.
	 * <p>
	 * Uses algorithm: nH = 8 - group - sumbondorder + formalCharge, where group
	 * is 0-8 in first two rows
	 *
	 * @param atom
	 * @param control specifies whether H are explicit or in hydrogenCount
	 */
	public static void adjustHydrogenCountsToValency(CMLAtom atom, CMLMolecule molecule, HydrogenControl control) {
		if (atom.getHydrogenCountAttribute() == null) {
			int group = getHydrogenValencyGroup(atom);
			// these states cannot have hydrogen
			if (group == -1) {
				return;
			} else if (group == -2) {
				return;
			}
			// hydrogen and metals
			if (group < 4) {
				return;
			}
			int sumBo = getSumNonHydrogenBondOrder(atom);
			int fc = (atom.getFormalChargeAttribute() == null ? 0 :
				atom.getFormalCharge());
			int nh = 8 - group - sumBo + fc;
			// non-octet species
			if (group == 4 && fc == 1) {
				nh -= 2;
			}
			// negative counts are meaningless
			if (nh < 0) {
				nh = 0;
			}
			atom.setHydrogenCount(nh);
		}
		expandImplicitHydrogens(atom, molecule, control);
	}

    static String[] elems  = {AS.H.value, AS.C.value, AS.N.value, AS.O.value, AS.F.value, AS.Si.value, AS.P.value, AS.S.value, AS.Cl.value, AS.Br.value, AS.I.value};
    static int[]    group  = { 1,   4,   5,   6,   7,   4,    5,   6,   7,    7,    7};
    static int[]    eneg0  = { 0,   0,   0,   0,   1,   0,    0,   1,   1,    1,    1};
    static int[]    eneg1  = { 0,   0,   0,   1,   1,   0,    0,   0,   1,    1,    1};
    /** a simple lookup for common atoms.
    *
    * examples are C, N, O, F, Si, P, S, Cl, Br, I
    * if atom has electronegative ligands, (O, F, Cl...) returns -1
    *
    */
    public static int getHydrogenValencyGroup(CMLAtom atom) {
        int elNum = -1;
        try {
            String elType = atom.getElementType();
            elNum = getElemNumb(elType);
            if (elNum == -1) {
                return -1;
            }
            if (eneg0[elNum] == 0) {
                return group[elNum];
            }
            List<CMLAtom> ligands = atom.getLigandAtoms();
    // if atom is susceptible to enegative ligands, exit if they are present
            for (CMLAtom ligand : ligands) {
                int ligElNum = getElemNumb(ligand.getElementType());
                if (ligElNum == -1 || eneg1[ligElNum] == 1) {
                    return -2;
                }
            }
        } catch (Exception e) {
            LOG.error("BUG "+e);
        }
        int g = (elNum == -1) ? -1 : group[elNum];
        return g;
    }

    private static int getElemNumb(String elemType) {
        for (int i = 0; i < elems.length; i++) {
            if (elems[i].equals(elemType)) {
                return i;
            }
        }
        return -1;
    }

	/**
	 * Sums the formal orders of all bonds from atom to non-hydrogen ligands.
	 * <p>
	 * Uses 1, 2, 3, A orders and creates the nearest integer. Thus 2 aromatic
	 * bonds sum to 3 and 3 sum to 4. Bonds without order are assumed to be
	 * single.
	 * 
	 * @exception RuntimeException null atom in argument
	 * @return Sum of bond orders; may be 0 for isolated atom or atom with only
	 *         H ligands
	 */
	public static int getSumNonHydrogenBondOrder(CMLAtom atom) throws RuntimeException {
		float sumBo = 0.0f;
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		List<CMLBond> ligandBondList = atom.getLigandBonds();
		for (int i = 0; i < ligandList.size(); i++) {
			CMLAtom ligand = ligandList.get(i);
			if (AS.H.equals(ligand.getElementType())) {
				continue;
			}
			CMLBond bond = ligandBondList.get(i);
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
	 * Expands implicit hydrogen atoms.
	 * <p>
	 * TODO this needs looking at.
	 * <p>
	 * CMLMolecule.NO_EXPLICIT_HYDROGENS 
	 * <p>
	 * CMLMolecule.USE_HYDROGEN_COUNT // no
	 * action
	 *
	 * @param atom
	 * @param control
	 * @throws RuntimeException
	 */
	public static void expandImplicitHydrogens(CMLAtom atom, CMLMolecule molecule, HydrogenControl control) throws RuntimeException {
		if (HydrogenControl.USE_HYDROGEN_COUNT.equals(control)) {
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
		//FIXME this might need rethinking; may not correctly handle sulfur stereochemistry or wiggly bonds
		if (HydrogenControl.NO_EXPLICIT_HYDROGENS.equals(control) && currentHCount != 0) {
			return;
		}
		String id = atom.getId();
		List<CMLBond> bonds = atom.getLigandBonds();
		/*boolean hasHatch = false;
		boolean hasWedge = false;
		boolean hasDouble = false;*/
		Angle wedgeAngle = null;
		Angle hatchAngle = null;
		double bondLength = 0;
		List<Angle> otherAngles = new ArrayList<Angle>();
		for (CMLBond bond : bonds) {
			if (bond.getBondStereo() != null && bond.getBondStereo().getValue().equals(CMLBondStereo.WEDGE) && bond.getAtom(0) == atom) {
				//hasWedge = true;
				wedgeAngle = new Angle(Math.atan2(bond.getAtom(1).getY2() - atom.getY2(), bond.getAtom(1).getX2() - atom.getX2()), org.xmlcml.euclid.Angle.Units.RADIANS);
				bondLength = (bond.getAtom(1).getY2() - atom.getY2()) * (bond.getAtom(1).getY2() - atom.getY2()) + (bond.getAtom(1).getX2() - atom.getX2()) * (bond.getAtom(1).getX2() - atom.getX2());
			} else if (bond.getBondStereo() != null && bond.getBondStereo().getValue().equals(CMLBondStereo.HATCH) && bond.getAtom(0) == atom) {
				//hasHatch = true;
				hatchAngle = new Angle(Math.atan2(bond.getAtom(1).getY2() - atom.getY2(), bond.getAtom(1).getX2() - atom.getX2()), org.xmlcml.euclid.Angle.Units.RADIANS);
				bondLength = (bond.getAtom(1).getY2() - atom.getY2()) * (bond.getAtom(1).getY2() - atom.getY2()) + (bond.getAtom(1).getX2() - atom.getX2()) * (bond.getAtom(1).getX2() - atom.getX2());
			}/* else if (bond.getOrder().equals(CMLBond.DOUBLE_D)) {
				hasDouble = true;
			}*/ else if (bond.getAtom(0) == atom) {
				otherAngles.add(new Angle(Math.atan2(bond.getAtom(1).getY2() - atom.getY2(), bond.getAtom(1).getX2() - atom.getX2()), org.xmlcml.euclid.Angle.Units.RADIANS));
			} else {
				otherAngles.add(new Angle(Math.atan2(bond.getAtom(0).getY2() - atom.getY2(), bond.getAtom(0).getX2() - atom.getX2()), org.xmlcml.euclid.Angle.Units.RADIANS));
			}
		}
		bondLength =  Math.sqrt(bondLength);
		for (int i = 0; i < hydrogenCount - currentHCount; i++) {
			CMLAtom hatom = new CMLAtom(id + "_h" + (i + 1));
			molecule.addAtom(hatom);
			hatom.setElementType(AS.H.value);
			//hatom.setXY2(atom.getXY2().plus(new Real2(1, 1)));
			CMLBond bond = new CMLBond(atom, hatom);
			molecule.addBond(bond);
			bond.setOrder(CMLBond.SINGLE_S);
			if (hatchAngle == null ^ wedgeAngle == null) {
				CMLBondStereo s = new CMLBondStereo();
				s.setXMLContent(hatchAngle != null ? CMLBondStereo.WEDGE : CMLBondStereo.HATCH);
				bond.setBondStereo(s);
				Angle baseBondAngle = (hatchAngle != null ? hatchAngle : (wedgeAngle != null ? wedgeAngle : otherAngles.get(2)));
				Angle diff1 = baseBondAngle.subtract(otherAngles.get(0));
				Angle diff2 = baseBondAngle.subtract(otherAngles.get(1));
				diff1.setRange(Range.UNLIMITED);
				diff2.setRange(Range.UNLIMITED);
				Angle newAngle = (Math.abs(diff1.getRadian()) < Math.abs(diff2.getRadian()) ? otherAngles.get(0).plus(baseBondAngle).multiplyBy(0.5) : otherAngles.get(1).plus(baseBondAngle).multiplyBy(0.5));
				positionAtomRelatively(atom, hatom, newAngle, bondLength);
			} else if (hatchAngle == null && wedgeAngle == null) {
				if (atom.getXY2() != null) {
					hatom.setXY2(atom.getXY2().plus(new Real2(1, 1)));
				}
			} else if (hatchAngle != null && wedgeAngle != null) {
				otherAngles.add(wedgeAngle);
				otherAngles.add(hatchAngle);
				Collections.sort(otherAngles, new Comparator<Angle>(){

					public int compare(Angle o1, Angle o2) {
						return (o1.lessThan(o2) ? -1 : 1);
					}
				
				});
				Angle largestAngle = new Angle(0);
				Angle newAngle = null;
				for (int angle = 0; angle < otherAngles.size() - 1; angle++) {
					if ((otherAngles.get(angle) == wedgeAngle && otherAngles.get(angle + 1) == hatchAngle) || (otherAngles.get(angle) == hatchAngle && otherAngles.get(angle + 1) == wedgeAngle)) {
						continue;
					}
					Angle diff = otherAngles.get(angle + 1).subtract(otherAngles.get(angle));
					if (diff.greaterThan(largestAngle)) {
						largestAngle = diff;
						newAngle = otherAngles.get(angle).plus(otherAngles.get(angle + 1)).multiplyBy(0.5);
					}
				}
				Angle diff = otherAngles.get(0).subtract(otherAngles.get(otherAngles.size() - 1)).plus(new Angle(360, org.xmlcml.euclid.Angle.Units.DEGREES));
				if (diff.greaterThan(largestAngle)) {
					largestAngle = diff;
					newAngle = otherAngles.get(otherAngles.size() - 1).plus(otherAngles.get(0)).multiplyBy(0.5).plus(new Angle(180, org.xmlcml.euclid.Angle.Units.DEGREES));
				}
				positionAtomRelatively(atom, hatom, newAngle, bondLength);
			}
		}
	}

	private static void positionAtomRelatively(CMLAtom atom, CMLAtom hatom, Angle newAngle, double bondLength) {
		double xDiff;
		double yDiff;
		if (newAngle.isEqualTo(Math.PI, 0.0000001)) {
			xDiff = -bondLength;
			yDiff = 0;
		} else if (newAngle.isEqualTo(Math.PI / 2, 0.0000001)) {
			xDiff = 0;
			yDiff = bondLength;
		} else if (newAngle.isEqualTo(0, 0.0000001)) {
			xDiff = bondLength;
			yDiff = 0;
		} else if (newAngle.isEqualTo(-Math.PI / 2, 0.0000001)) {
			xDiff = 0;
			yDiff = -bondLength;
		} else if (newAngle.isEqualTo(-Math.PI, 0.0000001)) {
			xDiff = -bondLength;
			yDiff = 0;
		} else {
			double ratio = Math.tan(newAngle.getRadian());
			xDiff = Math.sqrt((bondLength * bondLength) / (1 + ratio * ratio));
			if (newAngle.greaterThan(Math.PI / 2) && newAngle.lessThan((3 * Math.PI) / 2)) {
				xDiff *= -1;
			}
			yDiff = xDiff * ratio;
		}
		hatom.setXY2(atom.getXY2().plus(new Real2(xDiff, yDiff)));
	}

    // ---------------------to be looked at:-----------------------------------------
    // these functions seem to deal mostly with chirality (& associated)
    // there also appears to be a lot of duplicate functionality
    /**
     * add calculated coordinates for hydrogens.
     * 
     * @param atom
     * @param type
     * @param bondLength
     */
    public void addCalculatedCoordinatesForHydrogens(CMLAtom atom, CoordinateType type, double bondLength) {
    	if (CoordinateType.TWOD.equals(type)) {
    		calculateAndAddHydrogenCoordinates(atom, bondLength);
    	} else if (CoordinateType.CARTESIAN.equals(type)) {
    		throw new RuntimeException("CARTESIAN H coords nyi");
    	} else {
    		throw new RuntimeException("THREED H coords nyi");
    	}
    }
    /**
     * 
     * @param bondLength
     */
    public static void calculateAndAddHydrogenCoordinates(CMLAtom atom, double bondLength) {
    	List<CMLAtom> ligandHydrogenList = atom.getLigandHydrogenAtoms();
    	List<CMLAtom> ligandList = atom.getLigandAtoms();
    	List<CMLAtom> nonHydrogenLigandHydrogenList = new ArrayList<CMLAtom>();
    	for (CMLAtom ligand : ligandList) {
    		if (!AS.H.equals(ligand.getElementType())) {
    			nonHydrogenLigandHydrogenList.add(ligand);
    		}
    	}
    	List<Vector2> vectorList = new ArrayList<Vector2>();
    	try {
    		vectorList = addCoords(atom, nonHydrogenLigandHydrogenList, ligandHydrogenList, bondLength);
    	} catch (Exception e) {
    		LOG.error("Cannot add Hydrogen ", e);
    	}
    	if (vectorList.size() == 0) {
    	} else if (vectorList.size() != ligandHydrogenList.size()) {
    		LOG.error("vectorList ("+vectorList.size()+") != ligandHydrogenList ("+ligandHydrogenList.size()+")");
    	} else {
	    	Real2 xy2 = atom.getXY2();
	    	for (int i = 0; i < ligandHydrogenList.size(); i++) {
	    		ligandHydrogenList.get(i).setXY2(xy2.plus(vectorList.get(i)));
	    	}
    	}
	}

	private static Transform2 PI120 = new Transform2(new Angle(Math.PI * 2./3.));
	private static Transform2 PI90 = new Transform2(new Angle(Math.PI * 0.5));
	private static Transform2 PI270 = new Transform2(new Angle(Math.PI * 1.5));
	private static final Transform2 ROT90 = new Transform2(new Angle(Math.PI/2.));

	private static void addCoords(CMLAtom atom, List<Vector3> vector3List, List<CMLAtom> hydrogenLigandList) {
		Point3 atomxyz3 = atom.getXYZ3();
		for (int i = 0; i < vector3List.size(); i++) {
			Point3 xyz3 = atomxyz3.plus(vector3List.get(i));
			hydrogenLigandList.get(i).setXYZ3(xyz3);
		}
	}

    private static List<Vector2> addCoords(CMLAtom atom, List<CMLAtom> ligandList, List<CMLAtom> hydrogenList, double bondLength) {
    	List<Vector2> vectorList = new ArrayList<Vector2>();
    	if (hydrogenList.size() == 0) {
    		// nothing to do
    	} else if (ligandList.size() == 0) {
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(0, bondLength));
    		} else if (hydrogenList.size() == 2) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(0, -bondLength));
    		} else if (hydrogenList.size() == 3) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(bondLength * Math.sqrt(0.75), -bondLength *0.5));
    			vectorList.add(new Vector2(-bondLength * Math.sqrt(0.75), -bondLength *0.5));
    		} else if (hydrogenList.size() == 4) {
    			vectorList.add(new Vector2(0, bondLength));
    			vectorList.add(new Vector2(0, -bondLength));
    			vectorList.add(new Vector2(bondLength, 0));
    			vectorList.add(new Vector2(-bondLength, 0));
    		}

    	} else if (ligandList.size() == 1) {
    		Vector2 ligandVector = new Vector2(ligandList.get(0).getXY2().subtract(atom.getXY2()));
    		ligandVector = new Vector2(ligandVector.getUnitVector().multiplyBy(-bondLength));
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(ligandVector));
    		} else if (hydrogenList.size() == 2) {
    			Vector2 vector = new Vector2(ligandVector.multiplyBy(-1.0));
    			vector.transformBy(PI120);
    			vectorList.add(new Vector2(vector));
    			vector.transformBy(PI120);
    			vectorList.add(new Vector2(vector));
    		} else if (hydrogenList.size() == 3) {
    			Vector2 vector = new Vector2(ligandVector);
    			vectorList.add(new Vector2(vector));
    			vector.transformBy(PI90);
    			vectorList.add(new Vector2(vector));
    			vector = new Vector2(ligandVector);
    			vector.transformBy(PI270);
    			vectorList.add(new Vector2(vector));
    		} else {
    		}
    	} else if (ligandList.size() == 2) {
    		Vector2 ligandVector0 = new Vector2(ligandList.get(0).getXY2().subtract(atom.getXY2()));
    		ligandVector0 = new Vector2(ligandVector0.getUnitVector());
    		Vector2 ligandVector1 = new Vector2(ligandList.get(1).getXY2().subtract(atom.getXY2()));
    		ligandVector1 = new Vector2(ligandVector1.getUnitVector());
    		Angle angle = ligandVector0.getAngleMadeWith(ligandVector1);
    		angle.setRange(Angle.Range.SIGNED);
			Vector2 bisectVector = null;
    		boolean nearlyLinear = Math.abs(angle.getRadian()) > 0.9 * Math.PI;
        	if (nearlyLinear) {
    			bisectVector = new Vector2(ligandVector0.getUnitVector());
    			bisectVector.transformBy(ROT90);
    			bisectVector.multiplyBy(bondLength);
    		} else {
    			bisectVector = new Vector2(ligandVector0.plus(ligandVector1));
    			bisectVector = new Vector2(bisectVector.getUnitVector());
    			bisectVector = new Vector2(bisectVector.multiplyBy(-bondLength));
    		}
			if (hydrogenList.size() == 1) {
    			Vector2 vector = new Vector2(bisectVector);
    			vector = new Vector2(vector.multiplyBy(1.0));
    			vectorList.add(vector);
    		} else if (hydrogenList.size() == 2) {
        		if (nearlyLinear) {
        			vectorList.add(new Vector2(bisectVector));
        			vectorList.add(new Vector2(bisectVector.multiplyBy(-1.)));
        		} else {
        			Angle halfAngle = new Angle(Math.PI*0.5 - Math.abs(angle.getRadian()*0.5));
        			Transform2 t2 = new Transform2(halfAngle);
        			Vector2 vector = new Vector2(bisectVector);
        			vector.transformBy(t2);
	    			vectorList.add(vector);
        			t2 = new Transform2(halfAngle.multiplyBy(-1.0));
        			vector = new Vector2(bisectVector);
        			vector.transformBy(t2);
	    			vectorList.add(vector);
        		}
    		} else {
    		}
    	} else if (ligandList.size() == 3) {
    		Vector2[] vectors = new Vector2[3];
    		Vector2 bisectVector = null;
    		for (int i = 0; i < 3; i++) {
	    		vectors[i] = new Vector2(ligandList.get(i).getXY2().subtract(atom.getXY2()));
	    		bisectVector = (bisectVector == null) ? vectors[i] : new Vector2(bisectVector.plus(vectors[i]));
    		}
    		bisectVector = new Vector2(bisectVector.multiplyBy(-1.0));
    		// short vector
    		try {
    			bisectVector = new Vector2(bisectVector.getUnitVector().multiplyBy(vectors[0].getLength()*0.7));
    			// must not overlap too badly
    			for (int i = 0; i < 3; i++) {
    				Angle angle = bisectVector.getAngleMadeWith(vectors[i]);
    				angle.setRange(Range.SIGNED);
    				double angleR = Math.abs(angle.getRadian());;
    				if (angleR < 0.2) {
    					bisectVector = new Vector2(vectors[(i+1) % 3]);
    					bisectVector = new Vector2(bisectVector.multiplyBy(-1.0));
    					break;
    				}
    			}
    		} catch (EuclidRuntimeException e) {
				bisectVector = vectors[0];
				bisectVector = new Vector2(bisectVector);
    		}
    		if (hydrogenList.size() == 1) {
    			vectorList.add(new Vector2(bisectVector));
    		} else {
    		}
    	} else {
    		// skip
    	}
    	return vectorList;

    }

    /** assume atom has correct count of hydrogens
     * overwrite all existing coordinates
     */
	public static void addCalculated3DCoordinatesForExistingHydrogens(CMLAtom atom) {
		Double length = 1.6; // default for unusual atoms
		if (ChemicalElement.AS.C.equals(atom.getElementType())) {
			length = 1.08;
		} else if (ChemicalElement.AS.N.equals(atom.getElementType())) {
			length = 1.03;
		} else if (ChemicalElement.AS.O.equals(atom.getElementType())) {
			length = 0.96;
		}
		if (length != null) {
			addCalculated3DCoordinatesForExistingHydrogens(atom, length);
			removeHydrogenCountAttribute(atom);
		}
	}

	public static void removeHydrogenCountAttribute(CMLAtom atom) {
		Attribute att = atom.getAttribute("hydrogenCount");
		if (att != null) {
			att.detach();
		}
	}

	private final static double TWOPI3  = Math.PI*2.0/3.0;
	private final static double COS2PI3 = Math.cos(TWOPI3);
	private final static double SIN2PI3 = Math.sin(TWOPI3);
	private final static double ROOT3   = Math.sqrt(3.0);
	private final static double TETANG  = 2*Math.atan(Math.sqrt(2.0));
	private final static double TETANG0  = Math.PI - TETANG;
	private final static double TWOPI30  = Math.PI-TWOPI3;

    /** assume atom has correct count of hydrogens
     * overwrite all existing coordinates
     */
	public static void addCalculated3DCoordinatesForExistingHydrogens(CMLAtom atom, double length) {
		List<CMLAtom> nonHydrogenLigandList = getNonHydrogenLigandList(atom);
		List<CMLAtom> hydrogenLigandList = getHydrogenLigandList(atom);
		int nonhCount = nonHydrogenLigandList.size();
		int hCount = hydrogenLigandList.size();
		int coordNumber = nonhCount + hCount;
		List<Vector3> vector3List = new ArrayList<Vector3>();
		if (nonhCount == 0) {
			vector3List = addCoords0(atom, nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 1) {
			addCoords1(atom, nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 2) {
			addCoords2(atom, nonHydrogenLigandList, hydrogenLigandList, length);
		} else if (nonhCount == 3) {
			addCoords3(atom, nonHydrogenLigandList, hydrogenLigandList, length);
		} else {
			// cannot add hydrogens
		}
		addCoords(atom, vector3List, hydrogenLigandList);
	}

	private static double DTORAD = Math.PI / 180.;

	private static List<Vector3> addCoords0(CMLAtom atom, List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		Vector3 vector0 = new Vector3(0.0, 0.0, length);
		String elementType = atom.getElementType();
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			vector3List.add(vector0);
		} else if (hydrogenLigandList.size() == 2) {
			vector3List.add(vector0);
			double angle = Math.PI;
			if (ChemicalElement.AS.O.equals(elementType)) {
				angle = 104 * DTORAD;
			}
			vector3List.add(new Vector3(0.0, length * Math.sin(angle), length * Math.cos(angle)));
		} else if (hydrogenLigandList.size() == 3) {
			vector3List.add(vector0);
			vector3List.add(new Vector3(0.0, length*COS2PI3, -length*SIN2PI3));
			vector3List.add(new Vector3(0.0, length*COS2PI3, length*SIN2PI3));
		} else if (hydrogenLigandList.size() == 4) {
			vector3List.add(new Vector3(length/ROOT3, length/ROOT3, length/ROOT3));
			vector3List.add(new Vector3(-length/ROOT3, length/ROOT3, -length/ROOT3));
			vector3List.add(new Vector3(length/ROOT3, -length/ROOT3, -length/ROOT3));
			vector3List.add(new Vector3(-length/ROOT3, -length/ROOT3, length/ROOT3));
		}
		return vector3List;
	}
	
	private static List<Vector3> addCoords1(CMLAtom atom, List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		String elementType = atom.getElementType();
		CMLAtomSet atomSet = null;
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			AtomGeometry atomGeometry = AtomGeometry.LINEAR;
			if (ChemicalElement.AS.O.equals(elementType)) {
				atomGeometry = AtomGeometry.TETRAHEDRAL;
			}
			if (ChemicalElement.AS.N.equals(elementType)) {
				atomGeometry = AtomGeometry.TETRAHEDRAL;
			}
			atomSet = calculate3DCoordinatesForLigands(atom, atomGeometry, length,  TWOPI30);
		} else if (hydrogenLigandList.size() == 2) {
			atomSet = calculate3DCoordinatesForLigands(atom, AtomGeometry.TRIGONAL, length, TWOPI30);
		} else if (hydrogenLigandList.size() == 3) {
			atomSet = calculate3DCoordinatesForLigands(atom, AtomGeometry.TETRAHEDRAL, length, TETANG0);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atom, atomSet);
		}
		return vector3List;
	}
	
	private static List<Vector3> addCoords2(CMLAtom atom, List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		CMLAtomSet atomSet = null;
		List<Vector3> vector3List = new ArrayList<Vector3>();
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			atomSet = calculate3DCoordinatesForLigands(atom, AtomGeometry.TRIGONAL, length, TWOPI3);
		} else if (hydrogenLigandList.size() == 2) {
			atomSet = calculate3DCoordinatesForLigands(atom, AtomGeometry.TETRAHEDRAL, length, 2*TETANG);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atom, atomSet);
		}
		return vector3List;
	}
	
	private static List<Vector3> addCoords3(CMLAtom atom, List<CMLAtom> nonHydrogenLigandList,
			List<CMLAtom> hydrogenLigandList, double length) {
		List<Vector3> vector3List = new ArrayList<Vector3>();
		CMLAtomSet atomSet = null;
		if (hydrogenLigandList.size() == 0) {
			// nothing to add
		} else if (hydrogenLigandList.size() == 1) {
			atomSet = calculate3DCoordinatesForLigands(atom, AtomGeometry.TETRAHEDRAL, length, TETANG0);
		}
		if (atomSet != null) {
			vector3List = getVectorList(atom, atomSet);
		}
		return vector3List;
	}
	
	private static List<Vector3> getVectorList(CMLAtom atom, CMLAtomSet atomSet) {
		List<Vector3> vectorList = new ArrayList<Vector3>();
		for (CMLAtom atom1 : atomSet.getAtoms()) {
			Point3 xyz31 = atom1.getXYZ3();
			Vector3 vector3 = xyz31.subtract(atom.getXYZ3());
			vectorList.add(vector3);
		}
		return vectorList;
	}

	/**
	 * Gets the nonHydrogenLigandList attribute of the AtomImpl object
	 *
	 * @return The nonHydrogenLigandList value
	 */
	public static List<CMLAtom> getNonHydrogenLigandList(CMLAtom atom) {
		List<CMLAtom> newLigandList = new ArrayList<CMLAtom>();
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligand : ligandList) {
			if (!AS.H.equals(ligand.getElementType())) {
				newLigandList.add(ligand);
			}
		}
		return newLigandList;
	}

	/**
	 * 
	 * @return hydrogen ligands
	 */
	public static List<CMLAtom> getHydrogenLigandList(CMLAtom atom) {
		List<CMLAtom> ligands = atom.getLigandAtoms();
		List<CMLAtom> hatoms = new ArrayList<CMLAtom>();
		for (CMLAtom ligand : ligands) {
			if (AS.H.equals(ligand.getElementType())) {
				hatoms.add(ligand);
			}
		}
		return hatoms;
	}

	/**
	 * Adds 3D coordinates for singly-bonded ligands of this.
	 *
	 * <pre>
	 *       this is labelled A.
	 *       Initially designed for hydrogens. The ligands of A are identified
	 *       and those with 3D coordinates used to generate the new points. (This
	 *       allows structures with partially known 3D coordinates to be used, as when
	 *       groups are added.)
	 *       &quot;Bent&quot; and &quot;non-planar&quot; groups can be formed by taking a subset of the
	 *       calculated points. Thus R-NH2 could use 2 of the 3 points calculated
	 *       from (1,iii)
	 *       nomenclature: &quot;this&quot; is point to which new ones are &quot;attached&quot;.
	 *           this may have ligands B, C...
	 *           B may have ligands J, K..
	 *           points X1, X2... are returned
	 *       The cases (see individual routines, which use idealised geometry by default):
	 *       (0) zero ligands of A. The resultant points are randomly oriented:
	 *          (i) 1 points  required; +x,0,0
	 *          (ii) 2 points: use +x,0,0 and -x,0,0
	 *          (iii) 3 points: equilateral triangle in xy plane
	 *          (iv) 4 points x,x,x, x,-x,-x, -x,x,-x, -x,-x,x
	 *       (1a) 1 ligand(B) of A which itself has a ligand (J)
	 *          (i) 1 points  required; vector along AB vector
	 *          (ii) 2 points: 2 vectors in ABJ plane, staggered and eclipsed wrt J
	 *          (iii) 3 points: 1 staggered wrt J, the others +- gauche wrt J
	 *       (1b) 1 ligand(B) of A which has no other ligands. A random J is
	 *       generated and (1a) applied
	 *       (2) 2 ligands(B, C) of this
	 *          (i) 1 points  required; vector in ABC plane bisecting AB, AC. If ABC is
	 *              linear, no points
	 *          (ii) 2 points: 2 vectors at angle ang, whose resultant is 2i
	 *       (3) 3 ligands(B, C, D) of this
	 *          (i) 1 points  required; if A, B, C, D coplanar, no points.
	 *             else vector is resultant of BA, CA, DA
	 *
	 *       The method identifies the ligands without coordinates, calculates them
	 *       and adds them. It assumes that the total number of ligands determines the
	 *       geometry. This can be overridden by the geometry parameter. Thus if there
	 *       are three ligands and TETRAHEDRAL is given a pyramidal geometry is created
	 *
	 *       Inappropriate cases throw exceptions.
	 *
	 *       fails if atom itself has no coordinates or &gt;4 ligands
	 *       see org.xmlcml.molutils.Molutils for more details
	 *
	 * </pre>
	 *
	 * @param atom
	 * @param geometry
	 *            from: Molutils.DEFAULT, Molutils.ANY, Molutils.LINEAR,
	 *            Molutils.TRIGONAL, Molutils.TETRAHEDRAL
	 * @param length
	 *            A-X length
	 * @param angle
	 *            B-A-X angle (used for some cases)
	 * @return atomSet with atoms which were calculated. If request could not be
	 *         fulfilled (e.g. too many atoms, or strange geometry) returns
	 *         empty atomSet (not null)
	 * @throws RuntimeException
	 */
	public static CMLAtomSet calculate3DCoordinatesForLigands(CMLAtom atom, AtomGeometry geometry, double length, double angle) throws RuntimeException {
		Point3 thisPoint;
		// create sets of atoms with and without ligands
		CMLAtomSet noCoordsLigandsAS = new CMLAtomSet();
		if (atom.getX3Attribute() == null) {
			return noCoordsLigandsAS;
		} else {
			thisPoint = atom.getXYZ3();
		}
		CMLAtomSet coordsLigandsAS = new CMLAtomSet();
	
		// atomSet containing atoms without coordinates
		List<CMLAtom> ligandList = atom.getLigandAtoms();
		for (CMLAtom ligandAtom : ligandList) {
			if (ligandAtom.getX3Attribute() == null) {
				noCoordsLigandsAS.addAtom(ligandAtom);
			} else {
				coordsLigandsAS.addAtom(ligandAtom);
			}
		}
		int nWithoutCoords = noCoordsLigandsAS.size();
		int nWithCoords = coordsLigandsAS.size();
		if (geometry.equals(AtomGeometry.DEFAULT)) {
			geometry = AtomGeometry.getGeometry(atom.getLigandAtoms().size());
		}
	
		// too many ligands at present
		if (nWithCoords > 3) {
			CMLAtomSet emptyAS = new CMLAtomSet();
			// FIXME??
			return emptyAS;
			// nothing needs doing
		} else if (nWithoutCoords == 0) {
			return noCoordsLigandsAS;
		}
	
		List<Point3> newPoints = null;
		List<CMLAtom> coordAtoms = coordsLigandsAS.getAtoms();
		List<CMLAtom> noCoordAtoms = noCoordsLigandsAS.getAtoms();
		if (nWithCoords == 0) {
			newPoints = Molutils.calculate3DCoordinates0(thisPoint,
				geometry.getIntValue(), length);
		} else if (nWithCoords == 1) {
			// ligand on A
			CMLAtom bAtom = (CMLAtom) coordAtoms.get(0);
			// does B have a ligand (other than A)
			CMLAtom jAtom = null;
			List<CMLAtom> bLigandList = bAtom.getLigandAtoms();
			for (CMLAtom bLigand : bLigandList) {
				// FIXME: had to comment this out. Impossible to do. This is going to give bugs :(
				// if (!bLigand.equals(moleculeTool)) {
				//	jAtom = bLigand;
				//	break;
				//}
			}
			newPoints = Molutils.calculate3DCoordinates1(thisPoint, bAtom
					.getXYZ3(), (jAtom != null) ? jAtom.getXYZ3() : null,
							geometry.getIntValue(), length, angle);
		} else if (nWithCoords == 2) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			newPoints = Molutils.calculate3DCoordinates2(thisPoint, bPoint,
					cPoint, geometry.getIntValue(), length, angle);
		} else if (nWithCoords == 3) {
			Point3 bPoint = ((CMLAtom) coordAtoms.get(0)).getXYZ3();
			Point3 cPoint = ((CMLAtom) coordAtoms.get(1)).getXYZ3();
			Point3 dPoint = ((CMLAtom) coordAtoms.get(2)).getXYZ3();
			newPoints = new ArrayList<Point3>(1);
			newPoints.add(Molutils.calculate3DCoordinates3(thisPoint,
					bPoint, cPoint, dPoint, length));
		}
		int np = Math.min(noCoordsLigandsAS.size(), newPoints.size());
		for (int i = 0; i < np; i++) {
			((CMLAtom) noCoordAtoms.get(i)).setXYZ3(newPoints.get(i));
		}
		return noCoordsLigandsAS;
	}

}