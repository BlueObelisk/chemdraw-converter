package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * 
 * @author pm286
 *
 */
public class CDXML extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXML.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8000;
    public final static String NAME = "CDXML";
    public final static String CDXNAME = "CDXML";

    public CDXML() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXML(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXML(CDXML old) {
    	super(old);
    }


};



