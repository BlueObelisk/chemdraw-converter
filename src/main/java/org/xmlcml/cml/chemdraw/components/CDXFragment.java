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
package org.xmlcml.cml.chemdraw.components;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Vector2;

import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
/**
 * 
 * @author pm286
 *
 */
public class CDXFragment extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXFragment.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8003;
    public final static String NAME = "Fragment";
    public final static String CDXNAME = "fragment";
	private static final double SCALE_GROUPS = 0.3;

    public CDXFragment() {
        super(CODE, NAME, CDXNAME);
	}
	
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXFragment(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXFragment(CDXFragment old) {
    	super(old);
    }

    
	void process2CML(CMLMolecule molecule) {
//      <fragment BoundingBox="219.0299 57.7405 273.6799 87.9999" id="159">
//      <n Z="59" p="261.6174 60.8905" AS="U" Element="7" NumHydrogens="2" id="107">
//       <t temp_Text="[[0 3 96 8 3]]NH2" LabelAlignment="Left" p="258.7424 64.0405" BoundingBox="258.7424 57.7405 273.6799 65.7405" LabelJustification="Left" id="0"/>
//      </n>
//      <n Z="60" p="255.5424 71.4127" AS="U" id="108"/>
//      <n Z="61" p="261.6174 81.9349" AS="U" Element="8" NumHydrogens="0" id="109">
//       <t temp_Text="[[0 3 96 8 3]]O" Justification="Auto" p="258.4924 85.0849" BoundingBox="258.4924 78.7849 264.6924 85.1849" id="0"/>
//      </n>
//      <n Z="62" p="243.3924 71.4127" AS="U" Element="7" NumHydrogens="2" id="110">
//       <t temp_Text="[[0 3 96 8 3]]NH2" Justification="Right" LabelAlignment="Right" p="246.2674 74.5627" BoundingBox="231.3299 68.2627 246.2674 76.2627" LabelJustification="Right" id="0"/>
//      </n>
//      <n Z="63" p="231.2424 71" AS="U" Element="8" NumHydrogens="2" id="111">
//       <t temp_Text="[[0 3 96 8 3]]OH2" Justification="Right" LabelAlignment="Right" p="234.3674 74.1499" BoundingBox="219.0299 67.8499 234.3674 75.8499" LabelJustification="Right" id="0"/>
//      </n>
//      <n Z="64" p="231.2424 83.1499" AS="U" Element="8" NumHydrogens="2" id="112">
//       <t temp_Text="[[0 3 96 8 3]]OH2" Justification="Right" LabelAlignment="Right" p="234.3674 86.2999" BoundingBox="219.0299 79.9999 234.3674 87.9999" LabelJustification="Right" id="0"/>
//      </n>
//      <b Z="65" B="107" E="108" BS="U" id="113"/>
//      <b Z="66" Order="2" B="108" E="109" BS="U" id="114"/>
//      <b Z="67" B="108" E="110" BS="U" id="115"/>
//     </fragment>
	   	Elements childElements = this.getChildElements();
	   	for (int i = 0; i < childElements.size(); i++) {
	   		CDXObject child = (CDXObject) childElements.get(i);
	   		if (child instanceof CDXNode) {
	   			((CDXNode)child).process2CML(molecule);
	   		} else if (child instanceof CDXBond) {
	   			((CDXBond)child).process2CML(molecule);
	   		} else if (child instanceof CDXGraphic) {
	   			LOG.debug("Skipped graphic");
	   			((CDXGraphic)child).process2CML(molecule);
	   		} else if (child instanceof CDXText) {
	   			((CDXText)child).process2CML(molecule);
			} else {
				LOG.error("Cannot parse fragment child: "+child);
			}
	    }
	   	// flatten subgroups
	   	flattenSubGroups(molecule);
	}

	void flattenSubGroups(CMLMolecule molecule) {

//	    <atom id="a28" elementType="C" x2="192.9371" y2="82.3503"/>
//	    <atom id="a30" elementType="O" hydrogenCount="0" x2="192.9371" y2="70.1511"/>
//	    <atom id="a32" elementType="C" x2="203.5019" y2="88.4499" cdx:NodeType="Fragment">
//	      <label value="OEt"/>
//	      <molecule hydrogenCount="0" id="a79" elementType="C" x2="25.6129" y2="-204.0499" cdx:NodeType="ExternalConnectionPoint">
//	        <atomArray>
//	          <atom id="a73" elementType="O" hydrogenCount="0" x2="203.4629" y2="82.3503"/>
//	          <atom id="a74" elementType="C" x2="224.5073" y2="82.3503"/>
//	          <atom id="a75" elementType="C" x2="213.9851" y2="88.4253"/>
//	          <atom id="a79" elementType="C" x2="25.6129" y2="-204.0499" cdx:NodeType="ExternalConnectionPoint"/>
//	        </atomArray>
//	        <bondArray>
//	          <bond atomRefs2="a79 a73" order="1" Z="118" B="79" E="73" BS="U" id="76"/>
//	          <bond atomRefs2="a73 a75" order="1" Z="119" B="73" E="75" BS="U" id="77"/>
//	          <bond atomRefs2="a74 a75" order="1" Z="120" B="74" E="75" BS="U" id="78"/>
//	        </bondArray>
//	      </molecule>
//	    </atom>
//	  </atomArray>
//	  <bondArray>
//	    <bond atomRefs2="a18 a20" order="1" Z="123" B="18" E="20" BS="N" id="21"/>
//	    <bond atomRefs2="a20 a22" order="2" Z="125" Order="2" DoublePosition="Right" B="20" E="22" BS="U" BondCircularOrdering="0 21 0 25" id="23"/>
//	    <bond atomRefs2="a22 a24" order="1" Z="127" B="22" E="24" BS="N" id="25"/>
//	    <bond atomRefs2="a24 a26" order="2" Z="129" Order="2" DoublePosition="Right" B="24" E="26" BS="U" BondCircularOrdering="0 25 0 29" id="27"/>
//	    <bond atomRefs2="a26 a28" order="1" Z="131" B="26" E="28" BS="N" id="29"/>
//	    <bond atomRefs2="a28 a30" order="2" Z="133" Order="2" B="28" E="30" BS="U" id="31"/>
//	    <bond atomRefs2="a28 a32" order="1" Z="135" B="28" E="32" BS="N" id="33"/>
//	  </bondArray>
		List<CMLAtom> atomList = molecule.getAtoms();
		for (CMLAtom replacedAtom : atomList) {
			Nodes subMolecules = replacedAtom.query("cml:molecule", CML_XPATH);
			if (subMolecules.size() == 0) {
				continue;
			} else if (subMolecules.size() > 1) {
				throw new RuntimeException("too many molecule children");
			}
			List<CMLAtom> parentReplacedAtoms = replacedAtom.getLigandAtoms();
			CMLAtom replacedAtomParent = null;
			if (parentReplacedAtoms.size() == 0) {
				LOG.warn("too few ligands of replaced atom");
			} else if (parentReplacedAtoms.size() > 1) {
				replacedAtomParent = parentReplacedAtoms.get(0);
				LOG.warn("too many ligands of replaced atom: "+replacedAtom.getId());
			} else {
				replacedAtomParent = parentReplacedAtoms.get(0);
			}
			
			CMLMolecule subMolecule = (CMLMolecule) subMolecules.get(0);
			Nodes atoms = subMolecule.query(
					"cml:atomArray/cml:atom[@*[local-name()='NodeType' and .='ExternalConnectionPoint']]", CML_XPATH);
			CMLAtom extensionAtom = null;
			if (atoms.size() == 0) {
				LOG.warn("too few extension points");
			} else if (atoms.size() > 1) {
				LOG.warn("too many extension points: "+atoms.size());
			} 
			if (atoms.size() > 0) {
				extensionAtom = (CMLAtom) atoms.get(0);
				List<CMLAtom> extensionLigandAtoms = extensionAtom.getLigandAtoms();
				if (extensionLigandAtoms.size() == 0 || extensionLigandAtoms.size() > 1) {
					throw new RuntimeException("too few/many ligands of extension points");
				}
				CMLAtom replacingAtom = extensionLigandAtoms.get(0);
				replaceAtomsAndBonds(molecule, replacedAtom, replacedAtomParent, subMolecule, extensionAtom, replacingAtom);
			}
		}
	}
	
	private void replaceAtomsAndBonds(CMLMolecule molecule, CMLAtom replacedAtom, CMLAtom replacedAtomParent,
			CMLMolecule subMolecule, CMLAtom extensionAtom, CMLAtom replacingAtom) {
		moveLabelFromStaticAtomToGroup(replacedAtom, replacingAtom);
		scaleAndMoveGroup(replacedAtomParent, subMolecule, replacingAtom);
		CMLBond replacedBond = transferAtomsAndBondsFromGroup(molecule,
				replacedAtom, replacedAtomParent, subMolecule, extensionAtom,
				replacingAtom);
		createAndAddNewBond(molecule, replacedAtomParent, replacingAtom,
				replacedBond);
	}

	private void createAndAddNewBond(CMLMolecule molecule,
			CMLAtom replacedAtomParent, CMLAtom replacingAtom,
			CMLBond replacedBond) {
		if (replacedBond != null) {
			CMLBondStereo bondStereo = replacedBond.getBondStereo();
			String order = replacedBond.getOrder();
			
			if (replacedAtomParent != null) {
		//		make new bond and copy properties
				CMLBond newBond = new CMLBond();
				newBond.setAtomRefs2(new String[]{replacedAtomParent.getId(), replacingAtom.getId()});
				if (bondStereo != null) {
					newBond.addBondStereo(bondStereo);
				}
				newBond.setOrder(order);
				molecule.addBond(newBond);
			}
		}
	}

	private CMLBond transferAtomsAndBondsFromGroup(CMLMolecule molecule,
			CMLAtom replacedAtom, CMLAtom replacedAtomParent,
			CMLMolecule subMolecule, CMLAtom extensionAtom,
			CMLAtom replacingAtom) {
		CMLBond replacedBond = molecule.getBond(replacedAtom, replacedAtomParent);
		CMLBond extensionBond = subMolecule.getBond(extensionAtom, replacingAtom);
		// delete everything from subMolecule
		// delete bonds first
		List<CMLBond> subMoleculeBonds = subMolecule.getBonds();
		List<CMLAtom> subMoleculeAtoms = subMolecule.getAtoms();
		for (CMLBond subMoleculeBond : subMoleculeBonds) {
			subMolecule.deleteBond(subMoleculeBond);
		}
		for (CMLAtom subMoleculeAtom : subMoleculeAtoms) {
			subMolecule.deleteAtom(subMoleculeAtom);
		}

		// replace bonds in target
		molecule.deleteBond(replacedBond);
		molecule.deleteAtom(replacedAtom);
		// transfer atoms
		for (CMLAtom subMoleculeAtom : subMoleculeAtoms) {
			if (subMoleculeAtom.equals(extensionAtom)) {
				continue;
			}
			molecule.addAtom(subMoleculeAtom);
		}
		// and bonds
		for (CMLBond subMoleculeBond : subMoleculeBonds) {
			if (subMoleculeBond.equals(extensionBond)) {
				continue;
			}
			molecule.addBond(subMoleculeBond);
		}
		return replacedBond;
	}

	private void moveLabelFromStaticAtomToGroup(CMLAtom replacedAtom,
			CMLAtom replacingAtom) {
		Nodes labelNodes = replacedAtom.query("./cml:label", CMLConstants.CML_XPATH);
		CMLLabel label = (labelNodes.size() >= 1) ? (CMLLabel) labelNodes.get(0) : null;
		if (label != null) {
			label.detach();
			replacingAtom.addLabel(label);
		}
	}

	private void scaleAndMoveGroup(CMLAtom replacedAtomParent,
			CMLMolecule subMolecule, CMLAtom replacingAtom) {
		if (replacedAtomParent != null) {
			Real2 replacedXY2 = replacedAtomParent.getXY2();
			Real2 replacingXY2 = replacingAtom.getXY2();
			Vector2 bondVector = new Vector2(replacedXY2.subtract(replacingXY2));
			bondVector = new Vector2(bondVector.multiplyBy(SCALE_GROUPS));
			Real2 newXY2 = replacingXY2.plus(bondVector);
			subMolecule.multiply2DCoordsBy(SCALE_GROUPS);
			Real2 newReplacingXY2 = replacingAtom.getXY2();
			Vector2 translateXY2 = new Vector2(newXY2.subtract(newReplacingXY2));
			subMolecule.translate2D(translateXY2);
		}
	}
};



