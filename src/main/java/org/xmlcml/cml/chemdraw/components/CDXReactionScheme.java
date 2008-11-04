package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };

    public CDXReactionScheme() {
        super(CODE, NAME, CDXNAME);
        setCodeName();
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

//    protected void process2CML(CMLElement cmlNode) {
//        CMLReactionScheme reactionScheme = new CMLReactionScheme();
//        cmlNode.appendChild(reactionScheme);
//        processChildren2CML(reactionScheme);
//        this.copyAttributesTo(reactionScheme);
//    }

};



