package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * Chemdraw page
 * currently only a container
 * @author pm286
 *
 */
public class CDXObjectTag extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXObjectTag.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8011;
    public final static String NAME = "ObjectTag";
    public final static String CDXNAME = "objecttag";
    
    public CDXObjectTag() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXObjectTag(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXObjectTag(CDXObjectTag old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..objecttag.."+"]";
    }
};



