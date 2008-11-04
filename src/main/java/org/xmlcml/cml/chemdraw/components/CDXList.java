package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/** a simple container for CDXML objects.
*/
public class CDXList extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXList.class);
	static {
		LOG.setLevel(Level.INFO);
	}


    public final static int CODE = 0x8099;
    public final static String NAME = "cdxList";
    public final static String CDXNAME = "cdxList";

    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };

	public CDXList() {
        super(CODE, NAME, CDXNAME);
	}
	
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXList(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXList(CDXList old) {
    	super(old);
    }

};



