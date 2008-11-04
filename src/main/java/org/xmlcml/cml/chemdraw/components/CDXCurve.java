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
public class CDXCurve extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXCurve.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8008;
    public final static String NAME = "Curve";
    public final static String CDXNAME = "curve";
/*--
--*/
    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };


    public CDXCurve() {
		super(CDXNAME);
        setCodeName();
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXCurve(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXCurve(CDXCurve old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..curve.."+"]";
    }
};



