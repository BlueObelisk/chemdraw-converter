/**
 * Copyright (C) 2001 Peter Murray-Rust (pm286@cam.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.cml.chemdraw;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMolecule;

/** an object to hold CML state in the CDX context.
*/
public class CDXRawToCMLCreator implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXRawToCMLCreator.class);

    private CMLMolecule molecule;
    private boolean addCDXAttributes;
    double scale2 = 1.0;

    /**
     */
	public CDXRawToCMLCreator() {
		LOG.debug("NEW CDXRAW2CML ");
		
	}

//	/**
//	 * @param cmlNode
//	 */
//	private void setCMLParent(Node cmlNode) {
//        this.cmlParent = cmlNode;
//    }
//
//	private Node getCMLParent() {
//        return this.cmlParent;
//    }

	void setMolecule(CMLMolecule molecule) {
        this.molecule = molecule;
    }

	CMLMolecule getMolecule() {
		if (molecule == null) {
			molecule = new CMLMolecule();
			LOG.debug("created new molecule: "+molecule.hashCode());
			
		}
        return molecule;
    }

//	private void setFlatten(boolean f) {
//        this.flatten = f;
//    }

//	private boolean getFlatten() {
//        return this.flatten;
//    }
//
//	private void setReorient(boolean f) {
//        this.reorient = f;
//    }
//
//	private boolean getReorient() {
//        return this.reorient;
//    }

//	private void setScale2(double s) {
//        this.scale2 = s;
//    }
//
//	private double getScale2() {
//        return this.scale2;
//    }

//	private void setAddCDXAttributes(boolean f) {
//        this.addCDXAttributes = f;
//    }

	boolean getAddCDXAttributes() {
        return this.addCDXAttributes;
    }

/* process all molecules which are children of atoms.
 * convert the meaningful atoms (i.e. not stubs and links)
 * to normal toplevel atoms.

Typical CDX with fragment groups
<?xml version="1.0" encoding="UTF-8"?>
  <molecule>
      <atomArray>
        <atom id="a1113" elementType="C" x2="17.0622" y2="-38.0114"/>
        <atom id="a1114" elementType="C" x2="18.1252" y2="-39.8526"/>
        <atom id="a1115" elementType="O" hydrogenCount="0" x2="20.1819" y2="-39.3138">
          <scalar dictRef="cdx:text">O</scalar>
        </atom>
        <atom id="a1094" elementType="C" x2="22.226" y2="-39.8526"/>
        <atom id="a1095" elementType="C" x2="21.1631" y2="-38.0114"/>
        <atom id="a115" elementType="O" hydrogenCount="0" x2="19.1063" y2="-38.5501">
          <scalar dictRef="cdx:text">O</scalar>
        </atom>
// this is a dummy and will be replaced by its first granchild atom (a1096)
        <atom id="a116" elementType="C" x2="17.0622" y2="-35.8855">
          <scalar dictRef="cdx:text">OMe</scalar>
          <molecule>
            <atomArray>
// the first atom is the link atom; it replaces its grandparent (a116)
              <atom id="a1096" elementType="O" hydrogenCount="0" x2="18.1368" y2="-36.4999">
                <scalar dictRef="cdx:text">O</scalar>
              </atom>
              <atom id="a122" elementType="C" x2="20.2902" y2="-37.7432"/>
// the LAST atom seems to be the dummy
              <atom id="a123" elementType="C" x2="-10.9059" y2="-21.4857"/>
            </atomArray>
            <bondArray>
// the FIRST bond seems to contain the dummy atom as the FIRST atom
              <bond atomRefs2="a123 a1096" id="a123_a1096" order="1"/>
              <bond atomRefs2="a1096 a122" id="a1096_a122" order="1"/>
            </bondArray>
          </molecule>
        </atom>
        <atom id="a126" elementType="C" x2="15.2211" y2="-39.0744"/>
        <atom id="a127" elementType="C" x2="18.1252" y2="-41.9786">
          <scalar dictRef="cdx:text">OMe</scalar>
          <molecule>
            <atomArray>
              <atom id="a129" elementType="O" hydrogenCount="0" x2="19.1998" y2="-40.1106">
                <scalar dictRef="cdx:text">O</scalar>
              </atom>
              <atom id="a130" elementType="C" x2="21.3532" y2="-41.3538"/>
              <atom id="a131" elementType="C" x2="-11.0862" y2="-20.4523"/>
            </atomArray>
            <bondArray>
              <bond atomRefs2="a131 a129" id="a131_a129" order="1"/>
              <bond atomRefs2="a129 a130" id="a129_a130" order="1"/>
            </bondArray>
          </molecule>
        </atom>
        <atom id="a134" elementType="C" x2="15.9992" y2="-39.7588"/>
        <atom id="a135" elementType="C" x2="23.9062" y2="-39.1875"/>
        <atom id="a136" elementType="C" x2="23.25" y2="-38.4375"/>
        <atom id="a137" elementType="O" hydrogenCount="1" x2="26.1549" y2="-39.8484">
          <scalar dictRef="cdx:text">OH</scalar>
        </atom>
        <atom id="a138" elementType="C" x2="25.2922" y2="-37.7242">
          <scalar dictRef="cdx:text">OTBS</scalar>
          <molecule>
            <atomArray>
              <atom id="a6745" elementType="O" hydrogenCount="0" x2="25.2018" y2="-37.5284">
                <scalar dictRef="cdx:text">O</scalar>
              </atom>
              <atom id="a6746" elementType="Si" hydrogenCount="0" x2="27.0421" y2="-38.5909">
                <scalar dictRef="cdx:text">Si</scalar>
              </atom>
              <atom id="a6747" elementType="C" x2="28.1069" y2="-36.7465"/>
              <atom id="a6748" elementType="C" x2="28.8824" y2="-39.6534"/>
              <atom id="a6749" elementType="C" x2="25.9819" y2="-40.4271"/>
              <atom id="a6750" elementType="C" x2="26.2585" y2="-35.6793"/>
              <atom id="a1169" elementType="C" x2="29.1695" y2="-34.9062"/>
              <atom id="a6751" elementType="C" x2="29.9391" y2="-37.8043"/>
// dummy
              <atom id="a6752" elementType="C" x2="-10.5937" y2="-21.5625"/>
            </atomArray>
            <bondArray>
// dummy - locant
              <bond atomRefs2="a6752 a6745" id="a6752_a6745" order="1"/>
              <bond atomRefs2="a6745 a6746" id="a6745_a6746" order="1"/>
              <bond atomRefs2="a6746 a6747" id="a6746_a6747" order="1"/>
              <bond atomRefs2="a6746 a6748" id="a6746_a6748" order="1"/>
              <bond atomRefs2="a6746 a6749" id="a6746_a6749" order="1"/>
              <bond atomRefs2="a6747 a6750" id="a6747_a6750" order="1"/>
              <bond atomRefs2="a6747 a1169" id="a6747_a1169" order="1"/>
              <bond atomRefs2="a6747 a6751" id="a6747_a6751" order="1"/>
            </bondArray>
          </molecule>
        </atom>
      </atomArray>
      <bondArray>
        <bond atomRefs2="a1113 a1114" id="a1113_a1114" order="1"/>
        <bond atomRefs2="a1114 a1115" id="a1114_a1115" order="1">
          <bondStereo>H</bondStereo>
        </bond>
        <bond atomRefs2="a1115 a1094" id="a1115_a1094" order="1"/>
        <bond atomRefs2="a1095 a1094" id="a1095_a1094" order="1">
          <bondStereo>H</bondStereo>
        </bond>
        <bond atomRefs2="a1095 a115" id="a1095_a115" order="1"/>
        <bond atomRefs2="a115 a1113" id="a115_a1113" order="1"/>
        <bond atomRefs2="a1113 a116" id="a1113_a116" order="1"/>
        <bond atomRefs2="a1113 a126" id="a1113_a126" order="1"/>
        <bond atomRefs2="a1114 a127" id="a1114_a127" order="1"/>
        <bond atomRefs2="a1114 a134" id="a1114_a134" order="1"/>
        <bond atomRefs2="a1094 a135" id="a1094_a135" order="1"/>
        <bond atomRefs2="a1095 a136" id="a1095_a136" order="1"/>
        <bond atomRefs2="a135 a137" id="a135_a137" order="1"/>
        <bond atomRefs2="a136 a138" id="a136_a138" order="1"/>
      </bondArray>
    </molecule>

--*/
//	private static void expandAtoms(MoleculeTool moleculeTool) {
//        if (moleculeTool != null) {
//            moleculeTool.setUpdateAtoms(true);
//            moleculeTool.getAtoms();
//            moleculeTool.setUpdateBonds(true);
//            moleculeTool.getBonds();
////            CMLAtom atom = moleculeTool.getAtomById("a138");
////            LOG.debug("AA "+atom.getId());
//// these atoms should be the top level of atoms, not subgroups
//            LOG.debug("Expanding atoms with subgroups... ");
//            CMLAtom[] topAtoms = moleculeTool.getAtoms();
//            for (int i = 0; i < topAtoms.length; i++) {
//                NodeList subMoleculeList = topAtoms[i].getElementsByTagName("molecule");
//                if (subMoleculeList.getLength() == 1) {
//                    LOG.debug("TA "+topAtoms[i].getId());
//                    expandAtom(topAtoms[i], (CMLMolecule) subMoleculeList.item(0), moleculeTool);
//                }
//            }
//        }
//    }

//	private static void expandAtom(CMLAtom replacedAtom, CMLMolecule subMolecule, CMLMolecule parentMolecule) {
//        List<CMLAtom> replacedAtomLigands = replacedAtom.getLigandAtoms();
//        List<CMLAtom> subAtoms = subMolecule.getAtoms();
//        CMLAtomSet subAtomSet = new CMLAtomSet(subAtoms.toArray(new CMLAtom[0]));
//
//// get bonds before we start deleting atoms
//// transfer bonds to current (top) molecule
//// don't transfer first one as it is a dummy
//        List<CMLBond> subBonds = subMolecule.getBonds();
//
//// implicit semantics in chemdraw are horrible.
//// we guess the last atom is a dummy
//// it has only one ligand which is the replacing atom
//// Dummy atom; I think this just defines a vector? it may not even have coords
//        CMLAtom dummyAtom = subAtoms.get(subAtoms.size()-1);
//// assume exactly onw ligand and that it is the replacing atom
//        List<CMLAtom> dummyAtomLigands = dummyAtom.getLigandAtoms();
//        if (dummyAtomLigands.size() != 1) {
//            throw new RuntimeException("Expected only one ligand of dummy atom");
//        }
//// I think this atom replaces the old replaced atom
//        CMLAtom replacingAtom = dummyAtomLigands.get(0);
//
//// set atoms to belong to grandparent molecule
//// don't transfer the final one; it is a dummy and delete later
//        for (int i = 0; i < subAtoms.size()-1; i++) {
//            LOG.debug("Transferring: "+subAtoms.get(i).getId());
////            subAtomTool.transferToMolecule(parentMolecule);
//        }
////        LOG.debug("sub bonds1 "+subBonds.length);
//        subMolecule.deleteAtom(subAtoms.get(subAtoms.size()-1));
//
//
//// preserve coordinates of replaced atom
//        double oldx2 = replacedAtom.getX2();
//        double oldy2 = replacedAtom.getY2();
//        Real2 oldxy2 = new Real2(oldx2, oldy2);
//        double newx2 = replacingAtom.getX2();
//        double newy2 = replacingAtom.getY2();
//
//        double dx2 = oldx2 - newx2;
//        double dy2 = oldy2 - newy2;
//        Real2 delta = new Real2(dx2, dy2);
//        subAtomSet.translate2D(delta);
//        newx2 = replacingAtom.getX2();
//        newy2 = replacingAtom.getY2();
//
//// align vectors of overlapping atoms
//        CMLAtom oldLigand = replacedAtom.getLigandAtoms().get(0);
//        logger.log(Level.INFO,
//        "replaced atom: "+replacedAtom.getElementType()+"/"+replacedAtom.getId()+"/"+replacedAtom.getX2()+"/"+replacedAtom.getY2()+
//        "; old ligand: "+oldLigand.getElementType()+"/"+oldLigand.getId()+"/"+oldLigand.getX2()+"/"+oldLigand.getY2());
//        Vector2 oldLigandVector =
//            new Vector2(oldx2 - oldLigand.getX2(),
//                        oldy2 - oldLigand.getY2());
//        CMLAtom newLigand = replacingAtom.getLigandAtoms().get(0);
//        logger.log(Level.INFO,
//        "replacing atom: "+replacingAtom.getElementType()+"/"+replacingAtom.getId()+"/"+replacingAtom.getX2()+"/"+replacingAtom.getY2()+
//        "; new ligand: "+newLigand.getElementType()+"/"+newLigand.getId()+"/"+newLigand.getX2()+"/"+newLigand.getY2());
//        Vector2 newLigandVector =
//            new Vector2(newLigand.getX2() - newx2,
//                        newLigand.getY2() - newy2);
//        //newLigandVector.negative();
//        Transform2 t2 = null;
//        try {
////            Transform2 rotMatrix = new Transform2(oldLigandVector, newLigandVector);
//            Transform2 rotMatrix = new Transform2(newLigandVector, oldLigandVector);
//            t2 = new Transform2(rotMatrix, oldxy2);
//            LOG.debug("T2 "+t2);
//        } catch (Exception e) {
//            LOG.error("Transform error: "+e);
//        }
//        subAtomSet.transform(t2);
//
//// get first ligand of old and new atoms.
//
////        replacingAtom.setX2(oldx2);
////        replacingAtom.setY2(oldy2);
//
//// join replacingAtom to ligands of replacedAtom and remove the latter
//// set new bond orders to old (deleted) bond order
////        CMLAtom[] replacedAtomLigands = replacedAtomTool.getLigandList();
//        for (int i = 0; i < replacedAtomLigands.size(); i++) {
//            CMLBond oldBond = parentMolecule.getBond(replacedAtom, replacedAtomLigands.get(i));
//// keep track of old bond stereo
//            CMLBondStereo oldBondStereo = oldBond.getBondStereoElements().get(0);
//            parentMolecule.deleteBond(replacedAtom, replacedAtomLigands.get(i));
//// order of atoms matters to keep stereo correct
//            CMLBond newBond = new CMLBond(replacedAtomLigands.get(i), replacingAtom);
//            parentMolecule.addBond(newBond);
//// transfer order and stereo
//            newBond.setOrder(oldBond.getOrder());
//            if (oldBondStereo != null) {
//                newBond.appendChild(oldBondStereo);
//            }
//        }
//// transfer label ...
//        CMLLabel topLabel = replacedAtom.getLabelElements().get(0);
//        if (topLabel != null) {
//        	topLabel.detach();
////            replacedAtom.removeLabel(topLabel);
//// remove any existing label from replacing atom
//            CMLLabel subLabel = replacingAtom.getLabelElements().get(0);
//            if (subLabel != null) {
//                subLabel.detach();
////                replacingAtom.removeLabel(subLabel);
//            }
//// annotate
//            topLabel.setDictRef("cdx:contraction");
//            replacingAtom.appendChild(topLabel);
//        }
//// ... and atom parity
//        CMLAtomParity atomParity = replacedAtom.getAtomParityElements().get(0);
//        if (atomParity != null) {
//        	atomParity.detach();
////            replacedAtom.removeAtomParity(atomParity);
//            replacingAtom.appendChild(atomParity);
//        }
//
//        LOG.debug("delete atom "+replacedAtom.getId());
//        parentMolecule.deleteAtom(replacedAtom);
//
//// process bonds
//        if (subBonds.size() == 0) {
//            LOG.warn("No subBonds");
//        } else {
//
//// guess first bond is always to the dummy
//// this should be the real link atom; it substitutes the atom in the top molecule
//            if (!replacingAtom.equals(subBonds.get(0).getOtherAtom(dummyAtom))) {
//                throw new RuntimeException("replacing atom ("+replacingAtom.getId()+") is not second in first bond ("+subBonds.get(0).getId()+")");
//            }
//
//// transfer bonds to main molecule
//// skip first bond which is a dummy
//            for (int i = 1; i < subBonds.size(); i++) {
//            	throw new RuntimeException("FIX");
////                subBonds.get(i).setMolecule(parentMolecule);
////                try {
////                    parentMolecule.transferBond(subBonds.get(i));
////                subMoleculeTool.deleteBond(subBonds[i]);
////                    LOG.debug("Transferred bond: "+ subBonds.get(i).getId());
////                } catch (Exception e) {
////                    LOG.warn("Exception: "+ e + subBonds.get(i).getId());
////                }
//            }
//        }
//    }
};



