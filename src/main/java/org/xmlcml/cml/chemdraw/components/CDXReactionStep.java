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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLSpectator;
import org.xmlcml.cml.tools.ReactionTool;
/**
 * 
 * @author pm286
 *
 */
public class CDXReactionStep extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXReactionStep.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x800E;
    public final static String NAME = "ReactionStep";
    public final static String CDXNAME = "step";

//	private CMLReaction reaction;
//    private String[] reactionStepAtomMap = new String[0];
//    private String[] reactionStepReactants = new String[0];
//    private String[] reactionStepProducts = new String[0];
//    private String[] reactionStepArrows = new String[0];
//    private String[] reactionStepPlusses = new String[0];
//    private String[] reactionObjectsAboveArrow = new String[0];
//    private String[] reactionObjectsBelowArrow = new String[0];
    
    public CDXReactionStep() {
        super(CODE, NAME, CDXNAME);
	}
	
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXReactionStep(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXReactionStep(CDXObject old) {
    	super(old);
    }


/*--
Reaction Step Object
CDXML Name: step
CDX Constant Name: kCDXObj_ReactionStep
CDX Constant Value: 0x800E
Contained by objects: kCDXObj_Page, kCDXObj_Group, kCDXObj_ReactionScheme

First written/read in: ChemDraw 4.1

Description:


A Reaction Step describes one step in a reaction.

Technically, this object has no required objects or properties, but it is pretty useless without any reactants or products.


Subobjects:
(none)


Properties:
Value Name CDXML Name Type

n/a n/a id UINT16
 A unique identifier for an object, used when other objects refer to it.

0x0C00 kCDXProp_ReactionStep_Atom_Map ReactionStepAtomMap CDXObjectIDArray
 Represents pairs of mapped atom IDs; each pair is a reactant atom mapped to to a product atom.

0x0C01 kCDXProp_ReactionStep_Reactants ReactionStepReactants CDXObjectIDArray
 An order list of reactants present in the Reaction Step.

0x0C02 kCDXProp_ReactionStep_Products ReactionStepProducts CDXObjectIDArray
 An order list of products present in the Reaction Step.

0x0C03 kCDXProp_ReactionStep_Plusses ReactionStepPlusses CDXObjectIDArray
 An ordered list of pluses used to separate components of the Reaction Step.

0x0C04 kCDXProp_ReactionStep_Arrows ReactionStepArrows CDXObjectIDArray
 An ordered list of arrows used to separate components of the Reaction Step.

0x0C05 kCDXProp_ReactionStep_ObjectsAboveArrow ReactionStepObjectsAboveArrow CDXObjectIDArray
 An order list of objects above the arrow in the Reaction Step.

0x0C06 kCDXProp_ReactionStep_ObjectsBelowArrow ReactionStepObjectsBelowArrow CDXObjectIDArray
 An order list of objects below the arrow in the Reaction Step.

--*/
    void getProductsAndReactants() {
//        reactionStepAtomMap = processAttributes("ReactionStepAtomMap");
//        reactionStepReactants = processAttributes("ReactionStepReactants");
//        reactionStepProducts = processAttributes("ReactionStepProducts");
//        reactionStepArrows = processAttributes("ReactionStepArrows");
//        reactionStepPlusses = processAttributes("ReactionStepPlusses");
//        reactionObjectsAboveArrow = processAttributes("ReactionObjectsAboveArrow");
//        reactionObjectsBelowArrow = processAttributes("ReactionObjectsBelowArrow");
    }

    // may be useful
    @SuppressWarnings("unused")
	private String[] processAttributes(String attName) {
        String s[] = new String[0];
        String att = this.getAttributeValue(attName);
        String prefix = (attName.equals("reactionStepArrows") ? "g" : "m");
        if (att != null) {
            int i = 0;
            StringTokenizer st = new StringTokenizer(att, " ");
            s = new String[st.countTokens()];
            while (st.hasMoreTokens()) {
                s[i++] = prefix + st.nextToken();
                LOG.info(s[i-1]);
            }
        }
        return s;
    }

	CMLReaction convertToCMLReaction() {
//		<page BoundingBox="0 0 538.507 785.107" WidthPages="1" HeightPages="1" 
//		  HeaderPosition="35.9999" FooterPosition="35.9999" id="156">
//		  <graphic Z="136" GraphicType="Line" LineType="Solid" ArrowType="FullHead" 
//  		HeadSize="1000" BoundingBox="304.1129 83.4499 228.6129 83.4499" id="34"/>
//		  <t Z="137" temp_Text="[[0 3 1 6 3]]DCM, 0C, 0.5h" LineHeight="1" 
//		    p="238.1129 90.9499" BoundingBox="238.1129 85.9549 283.3629 92.4499" 
//		    Warning="ChemDraw can't interpret this label." id="36"/>
//		  <step ReactionStepReactants="19 99 102 97" ReactionStepProducts="90 37 69" 
//		    ReactionStepArrows="34" id="162"/>
//		</page>
		
		CMLReaction reaction = new CMLReaction();
		this.copyAttributesTo(reaction);
		return reaction;
	}

    public void process2CML(CMLElement cmlNode) {
    	/*
    	- <scheme id="5925">
    	  <step ReactionStepReactants="5704" ReactionStepProducts="5817" ReactionStepArrows="5916" 
    	    ReactionStepObjectsAboveArrow="5869" id="5926" /> 
    	  <step ReactionStepReactants="5817" ReactionStepProducts="5765" ReactionStepArrows="5917" 
    	    ReactionStepObjectsAboveArrow="5903" id="5927" /> 
    	  </scheme>
    	 */
        	// POINTS to... (this seems messy)
    /*
    - <group BoundingBox="258.9141 153.5068 312.4141 193.0068" id="5868">
      <t p="282.8641 169.1231" BoundingBox="263.6641 153.5068 302.0641 193.0068" Z="311" 
      Warning="ChemDraw can't interpret this label." CaptionJustification="Center" CaptionLineHeight="395" 
      Justification="Center" LineHeight="395" LineStarts="5 12" temp_Text="[[0 3 1 11 3]]acid solvent" id="5869" /> 
      <graphic BoundingBox="312.4141 173.479 258.9141 173.479" Z="309" LineType="Dashed" GraphicType="Line" ArrowType="FullHead"
       HeadSize="1000" id="5916" /> 
      <arrow BoundingBox="258.9141 170.8506 312.4141 175.3573" Z="309" LineType="Dashed" FillType="None" 
      ArrowheadType="Solid" ArrowheadHead="Full" HeadSize="1000" ArrowheadCenterSize="875" ArrowheadWidth="250" 
      Head3D="312.41 173.47 0" Tail3D="258.91 173.47 0" Center3D="399.41 220.47 0" MajorAxisEnd3D="452.91 220.47 0" 
      MinorAxisEnd3D="399.41 273.97 0" id="5923" /> 
      </group>
      <t p="171.151 231.7501" BoundingBox="171.151 221.5501 183.351 234.4501" Z="312" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]85" id="5871" /> 
      <t p="391.651 231.7501" BoundingBox="391.651 221.5501 403.851 234.4501" Z="313" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]80" id="5872" /> 
      <t p="598.401 231.7501" BoundingBox="598.401 221.5501 610.601 234.4501" Z="314" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]86" id="5873" /> 
      <t p="492 169.1231" BoundingBox="492 153.5068 547.4499 173.2568" Z="315" 
      Warning="ChemDraw can't interpret this label." CaptionLineHeight="395" LineHeight="395" 
      temp_Text="[[0 3 1 11 3]]conditions" id="5903" /> 
      <graphic BoundingBox="558.3986 173.229 484.3876 173.229" Z="310" LineType="Dashed" GraphicType="Line" 
      ArrowType="FullHead" HeadSize="1000" id="5917" /> 
      <arrow BoundingBox="484.3876 170.6006 558.3986 175.1073" Z="310" LineType="Dashed" FillType="None" 
      ArrowheadType="Solid" ArrowheadHead="Full" HeadSize="1000" ArrowheadCenterSize="875" ArrowheadWidth="250" 
      Head3D="558.39 173.22 0" Tail3D="484.38 173.22 0" Center3D="839.84 220.22 0" MajorAxisEnd3D="913.85 220.22 0" 
      MinorAxisEnd3D="839.84 274.72 0" id="5924" />  
      */
    /*
            makeProperty(0x0C00, "ReactionStep_Atom_Map", "ReactionStepAtomMap", "CDXObjectIDArray");
            makeProperty(0x0C01, "ReactionStep_Reactants", "ReactionStepReactants", "CDXObjectIDArray");
            makeProperty(0x0C02, "ReactionStep_Products", "ReactionStepProducts", "CDXObjectIDArray");
            makeProperty(0x0C03, "ReactionStep_Plusses", "ReactionStepPlusses", "CDXObjectIDArray");
            makeProperty(0x0C04, "ReactionStep_Arrows", "ReactionStepArrows", "CDXObjectIDArray");
            makeProperty(0x0C05, "ReactionStep_ObjectsAboveArrow", "ReactionStepObjectsAboveArrow", "CDXObjectIDArray");
            makeProperty(0x0C06, "ReactionStep_ObjectsBelowArrow", "ReactionStepObjectsBelowArrow", "CDXObjectIDArray");
            makeProperty(0x0C07, "ReactionStep_Atom_Map_Manual", "ReactionStepAtomMapManual", "CDXObjectIDArray");
            makeProperty(0x0C08, "ReactionStep_Atom_Map_Auto", "ReactionStepAtomMapAuto", "CDXObjectIDArray");
     */
        	Attribute reactionStepReactants = null;
        	Attribute reactionStepProducts = null;
        	Attribute reactionStepArrows = null;
        	Attribute reactionStepObjectsAboveArrow = null;
        	Attribute reactionStepObjectsBelowArrow = null;
        	for (int i = 0; i < this.getAttributeCount(); i++) {
        		Attribute attribute = this.getAttribute(i);
        		if (attribute.getLocalName().equals("ReactionStepReactants")) {
        			reactionStepReactants = attribute;
        		} else if (attribute.getLocalName().equals("ReactionStepProducts")) {
    				reactionStepProducts = attribute;
    			} else if (attribute.getLocalName().equals("ReactionStepArrows")) {
    				reactionStepArrows = attribute;
    			} else if (attribute.getLocalName().equals("ReactionStepObjectsAboveArrow")) {
    				reactionStepObjectsAboveArrow = attribute;
    			} else if (attribute.getLocalName().equals("ReactionStepObjectsBelowArrow")) {
    				reactionStepObjectsBelowArrow = attribute;
    			} else if (attribute.getLocalName().equals("ReactionStepPlusses") ||
    				attribute.getLocalName().equals("ReactionStepAtomMap") ||
    				attribute.getLocalName().equals("ReactionStepAtomMapManual") ||
    				attribute.getLocalName().equals("ReactionStepAtomMapAuto")) {
    				LOG.error("Cannot process "+attribute.getLocalName());
    			} else if (attribute.getLocalName().equals("id")) {
    				// skip
    			} else {
    				throw new RuntimeException("Unknown attribute on step: "+attribute.getLocalName());
    			}
        	}

        	// set up references
        	CMLReaction reaction = new CMLReaction();
        	cmlNode.appendChild(reaction);
        	ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
        	List<CMLMolecule> reactantMoleculeRefs = getMoleculeRefsFromId(reactionStepReactants);
        	for (CMLMolecule reactantMoleculeRef : reactantMoleculeRefs) {
        		reactionTool.addReactant(reactantMoleculeRef);
        	}
        	List<CMLMolecule> productMoleculeRefs = getMoleculeRefsFromId(reactionStepProducts);
        	for (CMLMolecule productMoleculeRef : productMoleculeRefs) {
        		reactionTool.addProduct(productMoleculeRef);
        	}
        	// not sure what to do with this. Perhaps grab graphics attributes
        	CDXGraphic arrowsGraphic = getArrowsTargetNoop(reactionStepArrows);
        	
        	addSpectatorRef(reactionStepObjectsAboveArrow, reaction);
        	addSpectatorRef(reactionStepObjectsBelowArrow, reaction);
        	
    }

    public static void processReactionStep(CMLReaction reaction) {
    	/*
    	- <scheme id="5925">
    	  <step ReactionStepReactants="5704" ReactionStepProducts="5817" ReactionStepArrows="5916" 
    	    ReactionStepObjectsAboveArrow="5869" id="5926" /> 
    	  <step ReactionStepReactants="5817" ReactionStepProducts="5765" ReactionStepArrows="5917" 
    	    ReactionStepObjectsAboveArrow="5903" id="5927" /> 
    	  </scheme>
    	 */
        	// POINTS to... (this seems messy)
    /*
    - <group BoundingBox="258.9141 153.5068 312.4141 193.0068" id="5868">
      <t p="282.8641 169.1231" BoundingBox="263.6641 153.5068 302.0641 193.0068" Z="311" 
      Warning="ChemDraw can't interpret this label." CaptionJustification="Center" CaptionLineHeight="395" 
      Justification="Center" LineHeight="395" LineStarts="5 12" temp_Text="[[0 3 1 11 3]]acid solvent" id="5869" /> 
      <graphic BoundingBox="312.4141 173.479 258.9141 173.479" Z="309" LineType="Dashed" GraphicType="Line" ArrowType="FullHead"
       HeadSize="1000" id="5916" /> 
      <arrow BoundingBox="258.9141 170.8506 312.4141 175.3573" Z="309" LineType="Dashed" FillType="None" 
      ArrowheadType="Solid" ArrowheadHead="Full" HeadSize="1000" ArrowheadCenterSize="875" ArrowheadWidth="250" 
      Head3D="312.41 173.47 0" Tail3D="258.91 173.47 0" Center3D="399.41 220.47 0" MajorAxisEnd3D="452.91 220.47 0" 
      MinorAxisEnd3D="399.41 273.97 0" id="5923" /> 
      </group>
      <t p="171.151 231.7501" BoundingBox="171.151 221.5501 183.351 234.4501" Z="312" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]85" id="5871" /> 
      <t p="391.651 231.7501" BoundingBox="391.651 221.5501 403.851 234.4501" Z="313" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]80" id="5872" /> 
      <t p="598.401 231.7501" BoundingBox="598.401 221.5501 610.601 234.4501" Z="314" 
      Warning="ChemDraw can't interpret this label." LineHeight="1" temp_Text="[[0 3 1 11 3]]86" id="5873" /> 
      <t p="492 169.1231" BoundingBox="492 153.5068 547.4499 173.2568" Z="315" 
      Warning="ChemDraw can't interpret this label." CaptionLineHeight="395" LineHeight="395" 
      temp_Text="[[0 3 1 11 3]]conditions" id="5903" /> 
      <graphic BoundingBox="558.3986 173.229 484.3876 173.229" Z="310" LineType="Dashed" GraphicType="Line" 
      ArrowType="FullHead" HeadSize="1000" id="5917" /> 
      <arrow BoundingBox="484.3876 170.6006 558.3986 175.1073" Z="310" LineType="Dashed" FillType="None" 
      ArrowheadType="Solid" ArrowheadHead="Full" HeadSize="1000" ArrowheadCenterSize="875" ArrowheadWidth="250" 
      Head3D="558.39 173.22 0" Tail3D="484.38 173.22 0" Center3D="839.84 220.22 0" MajorAxisEnd3D="913.85 220.22 0" 
      MinorAxisEnd3D="839.84 274.72 0" id="5924" />  
      */
    /*
            makeProperty(0x0C00, "ReactionStep_Atom_Map", "ReactionStepAtomMap", "CDXObjectIDArray");
            makeProperty(0x0C01, "ReactionStep_Reactants", "ReactionStepReactants", "CDXObjectIDArray");
            makeProperty(0x0C02, "ReactionStep_Products", "ReactionStepProducts", "CDXObjectIDArray");
            makeProperty(0x0C03, "ReactionStep_Plusses", "ReactionStepPlusses", "CDXObjectIDArray");
            makeProperty(0x0C04, "ReactionStep_Arrows", "ReactionStepArrows", "CDXObjectIDArray");
            makeProperty(0x0C05, "ReactionStep_ObjectsAboveArrow", "ReactionStepObjectsAboveArrow", "CDXObjectIDArray");
            makeProperty(0x0C06, "ReactionStep_ObjectsBelowArrow", "ReactionStepObjectsBelowArrow", "CDXObjectIDArray");
            makeProperty(0x0C07, "ReactionStep_Atom_Map_Manual", "ReactionStepAtomMapManual", "CDXObjectIDArray");
            makeProperty(0x0C08, "ReactionStep_Atom_Map_Auto", "ReactionStepAtomMapAuto", "CDXObjectIDArray");
     */
        	ReactionTool reactionTool = ReactionTool.getOrCreateTool(reaction);
        	resolveRefs(reactionTool.getReactantMolecules());
        	resolveRefs(reactionTool.getProductMolecules());
        	resolveSpectatorRefs(reactionTool.getSpectators());
        }

	private static void resolveRefs(List<CMLMolecule> molecules) {
		for (CMLMolecule molecule : molecules) {
			// this is a mess
			String ref = molecule.getAttributeValue("ref");
			ref = CDXUtil.ensureNumericID(ref);
//			Nodes moleculeNodes = molecule.query("//*[@id='"+ref+"' or @*[local-name()='group' and .='"+ref+"']]");
//			Nodes moleculeNodes = molecule.query("//cml:molecule[@*[local-name()='group' and .='"+ref+"']]", CMLConstants.CML_XPATH);
			Nodes moleculeNodes = molecule.query("//cml:molecule[@*[local-name()='group' and contains(concat(' ', ., ' '), ' "+ref+" ')]]", CMLConstants.CML_XPATH);
			if (moleculeNodes.size() == 0) {
//				((CMLElement)molecule.query("/*").get(0)).debug("DOC");
				LOG.warn("Cannot find molecule ref: "+ref);
				molecule.setTitle("UNRESOLVED");
			} else {
				CMLElement element = (CMLElement) moleculeNodes.get(0);
				CMLMolecule oldMolecule = (CMLMolecule)element;
				molecule.setTitle("RESOLVED");
				
	//			newMolecule.detach();
	//			molecule.getParent().replaceChild(molecule, newMolecule);
			}
		}
	}
    
	private void addSpectatorRef(Attribute attribute, CMLReaction reaction) {
		String refValue = getObjectsTextRef(attribute);
		if (refValue != null) {
			// there may be several values
			String[] refs = refValue.split(" ");
			for (String ref : refs) {
				CMLSpectator spectator = new CMLSpectator();
				spectator.addAttribute(new Attribute("ref", ref));
				spectator.addAttribute(new Attribute("type", attribute.getLocalName()));
				reaction.addSpectator(spectator);
			}
		}
	}

	// currently a no-op
	private CDXGraphic getArrowsTargetNoop(Attribute attribute) {
//		String id    = getXMLID(attribute);
//    	Nodes arrowsNodes = this.query("//*[@id='"+id+"']");
//    	CDXObject arrowsObject = (arrowsNodes.size() == 1) ? (CDXObject) arrowsNodes.get(0) : null;
//    	if (arrowsObject == null || !(arrowsObject instanceof CDXGraphic)) {
//    		CMLUtil.debug((Element)this.query("/*").get(0), "STEP? "+id);
//    		throw new RuntimeException("Cannot find target of arrow "+id);
//    	}
//    	return (CDXGraphic) arrowsObject;
		return null;
	}
	private String getObjectsTextRef(Attribute attribute) {
		return getXMLID(attribute);
	}

	private static void resolveSpectatorRefs(List<CMLSpectator> spectators) {
		for (CMLSpectator spectator : spectators) {
			String ref = CDXUtil.ensureNumericID(spectator.getAttributeValue("ref"));
			String[] refs = ref.split(" ");
//			for 
			Nodes nodes = spectator.query("//*[@id='"+ref+"']");
			if (nodes.size() == 0) {
				throw new RuntimeException("Cannot find spectator ref: "+ref);
			}
			String text = null;
			if (nodes.get(0) instanceof CMLLabel) {
				CMLLabel label = (CMLLabel) nodes.get(0);
				text = label.getCMLValue();
			}
			spectator.setTitle(text);
		}
	}
    

	private List<CMLMolecule> getMoleculeRefsFromId(Attribute reactionStepAttribute) {
		String idd = getXMLID(reactionStepAttribute);
		List<CMLMolecule> moleculeList = new ArrayList<CMLMolecule>();
    	if (idd != null) {
    		String[] ids = idd.split(" ");
    		for (String id : ids) {
    			CMLMolecule molecule = new CMLMolecule();
    			molecule.setRef(id);
				moleculeList.add(molecule);
    		}
    	}
    	return moleculeList;
	}

	private static String getXMLID(Attribute attribute) {
		return (attribute == null) ? null :
    		CDXUtil.ensureXMLID(attribute.getValue());
	}

};
