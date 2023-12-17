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

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.molutil.ChemicalElement;

/**
 * additional tools for geometry. not fully developed
 * 
 * @author pmr
 */
public class GeometryTool {

	static final Logger LOG = Logger.getLogger(GeometryTool.class.getName());

   	/**
     * Add calculated coordinates for hydrogen atoms.
     * 
     * We shall add a better selection soon.
     * 
     * @param type 2D or 3D
     * @param control
     */
    public static void addCalculatedCoordinatesForHydrogens(CMLMolecule molecule, CoordinateType type, HydrogenControl control) {
        if (type.equals(CoordinateType.CARTESIAN)) {
            addCalculated3DCoordinatesForHydrogens(molecule, control);
        } else if (type.equals(CoordinateType.TWOD)) {
            addCalculated2DCoordinatesForHydrogens(molecule, control);
        } else {
            throw new RuntimeException(
                    "Add calculated coordinates for hydrogens: control not recognised: " + type); //$NON-NLS-1$
        }
    }

    /**
     * Add calculated 3D coordinates for singly bonded atoms without coordinates
     * (intended for hydrogens).
     * 
     * @param control
     * @throws RuntimeException
     */
	public static void addCalculated3DCoordinatesForHydrogens(CMLMolecule molecule, HydrogenControl control) {
		// TODO
		CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
		if (molecules.size() > 0) {
			for (CMLMolecule childMol : molecules) {
				addCalculated3DCoordinatesForHydrogens(childMol, control);
			}
		} else {
			MoleculeTool.addCalculated3DCoordinatesForExistingHydrogens(molecule);
		}
	}

    /**
     * Add calculated 2D coordinates for hydrogen atoms.
     * @param control
     */
    @Deprecated
    public static void addCalculated2DCoordinatesForHydrogens(CMLMolecule molecule, HydrogenControl control) {
         CMLElements<CMLMolecule> molecules = molecule.getMoleculeElements();
         if (molecules.size() > 0) {
             for (CMLMolecule childMolecule : molecules) {
                 addCalculated2DCoordinatesForHydrogens(childMolecule, control);
             }
         } else {
        	 boolean omitHydrogens = true;
             if (molecule.hasCoordinates(CoordinateType.TWOD, omitHydrogens)) {
                 double bondLength = MoleculeTool.getAverageBondLength(molecule, CoordinateType.TWOD, omitHydrogens) * 0.75;
                 if (!Double.isNaN(bondLength)) {
                     for (CMLAtom atom : molecule.getAtoms()) {
                         if (!ChemicalElement.AS.H.equals(atom.getElementType())) {
                             AtomTool.calculateAndAddHydrogenCoordinates(atom, bondLength );
                         }
                     }
                 }
             }
         }
    }

}