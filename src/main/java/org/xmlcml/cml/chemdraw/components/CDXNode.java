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


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.molutil.ChemicalElement;

import nu.xom.Elements;
import nu.xom.Node;

/** A node or an atom.
* the purpose of a non-atom node is not very clear, probable a container for more atoms
*/
public class CDXNode extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXNode.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8004;
    public final static String NAME = "Node";
    public final static String CDXNAME = "n";

    int DEFAULTELEMENT = 6;
    int element = -999;    // the default
    int charge = 0;
    int DEFAULTHYDROGEN = -999;
    int numHydrogens = DEFAULTHYDROGEN;

    public CDXNode() {
        super(CODE, NAME, CDXNAME);
	}
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXNode(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXNode(CDXNode old) {
    	super(old);
    }


    /** indicates the type of node.
    * I think if its absent it's an atom...
    *
    <pre>
0 Unspecified A node of unspecified type. This may be used in cases where the node has unknown chemical significance. It may also be used when a node has an uninterpretable label.
1 Element A node consisting either of one chemical element or of one heavy element and attached hydrogens. The node property kCDXProp_Node_Element records the atomic number of the node's element. If the element property is missing, the atom is assumed to be a carbon.

2 ElementList An element list node (e.g. [O,S]) representing a node with alternative elements. The kCDXProp_Atom_ElementList attribute must be present as well, to provide the actual list of elements.
3 ElementListNickname A special type of element list node representing a group of elements of a common attribute as a nickname instead of representing each element in a list. For example, DARC uses HAL to represent [F,Cl,Br,I], MX to represent any metal. The kCDXProp_Atom_ElementList property must be present. A Text object that represents the node label is required for the node.
4 Nickname A molecular fragment represented by a single symbol, such as Ph. For example, Ph commonly represents a monosubstituted phenyl ring, C6H5. The Node should contain a Fragment object that contains the set of nodes and bonds that define the nickname. A Text object that represents the node label is required for the node.

A node may be of Nickname type only if it can be represented by a single symbol. A node with a label of "OPh" should be considered a Fragment rather than a Nickname

If a label is theoretically interpretable, but that interpretation is not also present in the file (as a Fragment object), the node should be specified as Unknown type instead.

As a practical matter, there is very little difference between a Nickname and a Fragment. CDX-reading programs may simply treat a Nickname as a special case of a Fragment.

5 Fragment An interpretable label, such as CH(CH2OH)2, which may include elements, nicknames, or named alternative groups. A Text object that represents the node label is required for the node. A Fragment object defining the content of the fragment is also required. If there is more than one bond to this node, a kCDXProp_Atom_BondOrdering attribute should also be given, specifying the order of the bonds to correspond to the ordered external connection nodes in the fragment.

If a label is theoretically interpretable, but that interpretation is not also present in the file (as a Fragment object), the node should be specified as Unknown type instead.

6 Formula A labeled node such as C5H10O, representing any or all of the possible isomers. A Text object that represents the node label is required for the node. A kCDXProp_Atom_Formula property must be present, defining the formula for the fragment.
7 GenericNickname A large or infinite set of alternative fragments, which may be defined by example. For example, DARC uses CHK to represent acyclic hydrocarbons. A Text object that represents the node label is required for the node. A kCDXProp_Atom_GenericNickname property must be present, giving the name of the generic nickname.
8 AnonymousAlternativeGroup A set of alternative fragments defined by enumeration, such as CH3, CH2OH, Ph. A Text object that represents the node label is required for the node. This node should contain two or more fragment objects.
9 NamedAlternativeGroup A set of fragments grouped together and given a name. A Text object that represents the node label (equivalent to the alternative group name) is required for the node. A kCDXProp_Atom_AltGroupID property must be present. If there is more than one bond to this node, a kCDXProp_Atom_BondOrdering property should be present. See the discussion of the Named Alternative Group object for more details and examples.
10 MultiAttachment The endpoint of a bonding to a set of atoms, such as in p-allyl and p-aryl bonding. The node must contain a kCDXProp_Node_Attachments property that describes the nodes in the set.
A multicenter attachment node might also have bonds to other nodes which are not part of the multicenter attachment. For example, in Ferrocene, two multicenter attachment nodes are used, one at the center of each ring Each multicenter attachment node contains the IDs of the five carbons in the Cp ring, but the connection to the iron is represented with a normal bond.

A multicenter attachment node can also have charge and radical attributes, which are treated as being distributed over the attached nodes.

11 VariableAttachment A node representing alternative positional isomers. The kCDXProp_Node_Attachments property contains the IDs of the variable attachment positions. For example, to find ortho-, meta-, or para-cresol, you might draw the following. The kCDXProp_Node_Attachments attribute would have the IDs of the five ring atoms to which the methyl group might be attached. Note that the methyl group is not in this list; the bond to the methyl group is represented as a normal bond to this node.
12 ExternalConnectionPoint An external connection point node is used in defining fragments for fragment nicknames and named alternative groups. This node indicates a point at which a fragment is connected to its parent. An external connection point node is represented in ChemDraw with a small filled diamond.
An external connection point normally has a single bond of order 1, but it may have more than one bond or a multiple bond to represent certain special cases. Note, however, that use of multiple bonds in either the use or definition of an alternative group can lead to ambiguities in the stereochemistry of the resulting query. For this reason, except in the simplest cases, it is preferable to define attachment points with a single connection.

13 LinkNode A node containing a single element or generic nickname, repeated some number of times in a chain, as in [CH2]1-5 (which indicates an alkyl chain of at most 5 carbons). The node must also contain kCDXProp_Atom_LinkCountLow and kCDXProp_Atom_LinkCountHigh properties, indicating the low and high ends of the repeat range.


If this property is absent:

The node is treated as Element type.
</pre>
	@return int
    */

	private int getElement() {
        if (element < 0) {
            String elementS = this.getAttributeValue("Element");
            element = (elementS == null || elementS.equals("")) ? DEFAULTELEMENT : Integer.parseInt(elementS);
        }
        return element;
	}

	private int getCharge() {
        String chargeS = this.getAttributeValue("Charge");
        charge = (chargeS == null || chargeS.equals("")) ? 0 : Integer.parseInt(chargeS);
        return charge;
	}

	private int getNumHydrogens() {
        String numHydrogensS = this.getAttributeValue("NumHydrogens");
        numHydrogens = (numHydrogensS == null || numHydrogensS.equals("")) ? DEFAULTHYDROGEN : Integer.parseInt(numHydrogensS);
        return numHydrogens;
	}

    /** set atom stereochemistry.
    * believed to be CIP
    * uses the scheme:
    *<pre>
        0 U Undetermined
        1 N Determined to be symmetric
        2 R Asymmetric: (R)
        3 S Asymmetric: (S)
        4 r Pseudoasymmetric: (r)
        5 s Pseudoasymmetric: (s)
        6 u Unspecified: The node is asymmetric, but lacks a hash/wedge so absolute configuration cannot be determined
    </pre>
    */
//	private String getAtomStereochemistry() {
//        return this.getAttributeValue("AS");
//	}

//	private String getCDXNodeType() {
//        String nodeTypeS = this.getAttributeValue("NodeType");
//        return (nodeTypeS == null) ? "" : nodeTypeS;
//	}

    
    void process2CML(CMLMolecule molecule) {
// node maps to atom
//        String nodeType = this.getCDXNodeType();

        /*-- geometry
        0 Unknown Unknown
        1 1 1 ligand
        2 Linear 2 ligands: linear
        3 Bent 2 ligands: bent
        4 TrigonalPlanar 3 ligands: trigonal planar
        5 TrigonalPyramidal 3 ligands: trigonal pyramidal
        6 SquarePlanar 4 ligands: square planar
        7 Tetrahedral 4 ligands: tetrahedral
        8 TrigonalBipyramidal 5 ligands: trigonal bipyramidal
        9 SquarePyramidal 5 ligands: square pyramidal
        10 5 5 ligands: unspecified
        11 Octahedral 6 ligands: octahedral
        12 6 6 ligands: unspecified
        13 7 7 ligands: unspecified
        14 8 8 ligands: unspecified
        15 9 9 ligands: unspecified
        16 10 10 ligands: unspecified
        --*/
// create as atom whatever the value of nodeTable
        String atomId = "a"+this.getId();
        CMLAtom atom = molecule.getAtomById(atomId);
        if (atom == null) {
        	atom = new CMLAtom(atomId);
        	molecule.addAtom(atom);
        } else {
        	// atom was created by bond
        }
        int charge = this.getCharge();
        if (charge != 0) {
            atom.setFormalCharge(""+charge);
        }
        int elementNum = this.getElement();
// no element, assume carbon
        if (elementNum < 0) {
            elementNum = DEFAULTELEMENT;
        }
        ChemicalElement element = ChemicalElement.getElement(elementNum);
        if (element == null) {
            throw new RuntimeException("Unknown atomic number: "+elementNum);
        }
        atom.setElementType(element.getSymbol());
        int hydrogenCount = this.getNumHydrogens();
        if (hydrogenCount != DEFAULTHYDROGEN) {
            atom.setHydrogenCount(""+hydrogenCount);
        }
        /*--
        String stereo = this.getAtomStereochemistry();
        if (!stereo.equals("Unknown")) {
            CMLScalar st = new CMLScalar();
            st.setDictRef("cdx:atomStereo");
            st.setElementContent(""+stereo);
            atom.appendChild(st);
        }
        --*/
        CDXPoint2D p2 = this.getPoint2D();
        if (p2 != null) {
            atom.setX2(p2.getX2());
            atom.setY2(p2.getY2());
            LOG.trace("COORDS "+atom.getId());
        } else {
//        	LOG.warn("atom has no coordinates");
//            LOG.warn("atom "+atomId+" has no coordinates");
        }
        addCDXAttribute(atom, this, "NodeType");
        Elements childs = this.getChildElements();
        if (childs.size() ==  0) {
        	//
        } else if (childs.size() == 1 && childs.get(0) instanceof CDXText) {
    		processTextChild(atom, (CDXText) childs.get(0));
        } else if (childs.size() == 1) {
        	LOG.error("Unprocessed element: "+childs.get(0).getLocalName());
        } else if (childs.size() == 2 &&
    		childs.get(0) instanceof CDXText &&
    		childs.get(1) instanceof CDXFragment) {
        	processSubFragment(atom, (CDXText)childs.get(0), (CDXFragment) childs.get(1));
        } else if (childs.size() == 2 &&
    		childs.get(1) instanceof CDXText &&
    		childs.get(0) instanceof CDXFragment) {
        	processSubFragment(atom, (CDXText)childs.get(1), (CDXFragment) childs.get(0));
    	} else {
//    		LOG.warn("*************unusual node children");
//    		CMLUtil.debug(this);
    		for (int i = 0; i < childs.size(); i++) {
    			CDXObject obj = (CDXObject) childs.get(i);
    			if (obj instanceof CDXFragment) {
    				((CDXFragment)obj).process2CML(molecule);
    			} else if (obj instanceof CDXText) {
    				((CDXText)obj).process2CML(molecule);
    			} else {
    				LOG.warn("*************unusual node child "+obj.getLocalName());
    			}
    		}
    	}
        this.copyAttributesTo(atom);
    }

	/**
	 * @param atom
	 * @param childs
	 * @throws RuntimeException
	 */
	private void processSubFragment(CMLAtom atom, CDXText text, CDXFragment fragment) throws RuntimeException {
		//          <n Z="134" p="203.5019 88.4499" NodeType="Fragment" AS="N" id="32">
		//    	      <t temp_Text="[[0 3 96 8 3]]OEt" LabelAlignment="Left" p="200.3769 91.5999" BoundingBox="200.3769 85.2999 214.1269 91.6999" LabelJustification="Left" id="0"/>
		//     	      <fragment BoundingBox="21.7626 -207.9003 225.6203 89.8167" id="157">
		//     	        <n Z="114" p="203.4629 82.3503" AS="U" Element="8" NumHydrogens="0" id="73">
		//     	          <t temp_Text="[[0 3 96 8 3]]O" p="200.3379 85.5003" BoundingBox="200.3379 79.2003 206.5379 85.6003" LabelJustification="Left" id="0"/>
		//     	        </n>
		//     	        <n Z="115" p="224.5073 82.3503" AS="U" id="74"/>
		//     	        <n Z="116" p="213.9851 88.4253" AS="U" id="75"/>
		//     	        <n Z="117" p="25.6129 -204.0499" NodeType="ExternalConnectionPoint" AS="U" id="79"/>
		//     	        <b Z="118" B="79" E="73" BS="U" id="76"/>
		//     	        <b Z="119" B="73" E="75" BS="U" id="77"/>
		//     	        <b Z="120" B="74" E="75" BS="U" id="78"/>
		//     	      </fragment>
		//        	</n>
		//     	    <b Z="135" B="28" E="32" BS="N" id="33"/>
		        	
		text.addLabelToCMLElement(atom);
		CMLMolecule subMolecule = new CMLMolecule();
		fragment.process2CML(subMolecule);
		atom.appendChild(subMolecule);
	}

	/**
	 * @param atom
	 * @param childs
	 * @throws RuntimeException
	 */
	private void processTextChild(CMLAtom atom, CDXText text) throws RuntimeException {
		String labelText = text.getLabelTextFromTextTempAndSquareBrackets();
		if (labelText != null && !labelText.trim().equals(S_EMPTY)) {
			String eltype = atom.getElementType();
			if (labelText.equals(eltype) || 
				("H".equals(eltype) && labelText.equals("D"))
				) {
				// matched
	        } else if ("GenericNickname".equals(this.getAttributeValue("NodeType"))) {
//	        	<n Z="348" p="149.6459 89.2224" NodeType="GenericNickname" AS="U" id="279">
//	        	  <t temp_Text="[[0 3 96 8 3]]R" p="146.5209 92.3724" BoundingBox="146.5209 86.0724 152.3209 92.3724" LabelJustification="Left" id="0"/>
//	        	</n>
	        	text.addLabelToCMLElement(atom);
			} else if (ChemicalElement.getChemicalElement(labelText) != null) {
				CMLUtil.debug(this, "CDXNODE1");
				throw new RuntimeException("Mismatched elements: "+labelText+"/"+eltype);
			} else {
//				LOG.info("ADDED label to atom: "+atom.getId());
	        	text.addLabelToCMLElement(atom);
			}
		}
	}
	

};



