package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLReactionScheme;
/**
 * 
 * @author pm286
 *
 */
public class CDXReactionScheme extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXReactionScheme.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x800D;
    public final static String NAME = "ReactionScheme";
    public final static String CDXNAME = "scheme";

    public CDXReactionScheme() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXReactionScheme(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXReactionScheme(CDXReactionScheme old) {
    	super(old);
    }

    public void process2CML(CMLElement cmlNode) {
/*
- <scheme id="5925">
  <step ReactionStepReactants="5704" ReactionStepProducts="5817" ReactionStepArrows="5916" ReactionStepObjectsAboveArrow="5869" id="5926" /> 
  <step ReactionStepReactants="5817" ReactionStepProducts="5765" ReactionStepArrows="5917" ReactionStepObjectsAboveArrow="5903" id="5927" /> 
  </scheme>
 */
    	
    	Nodes steps = this.query("*[local-name()='"+CDXReactionStep.CDXNAME+"']");
    	if (steps.size() > 0) {
            CMLReactionScheme reactionScheme = new CMLReactionScheme();
            cmlNode.appendChild(reactionScheme);
            this.copyAttributesTo(reactionScheme);
            for (int i = 0; i < steps.size(); i++) {
            	((CDXReactionStep) steps.get(i)).process2CML(reactionScheme);
            }
    	}
    }

};



