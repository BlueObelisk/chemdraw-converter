package org.xmlcml.cml.chemdraw.components;

import java.util.StringTokenizer;

import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLReaction;
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

    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };

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
        setCodeName();
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


};
