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
public class CDXColorTable extends CDXObject {

    static Logger logger = Logger.getLogger(CDXColorTable.class.getName());
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x0300;
    public final static String NAME = "ColorTable";
    public final static String CDXNAME = "colortable";

/*--
  <colortable>
   <color r="1" g="1" b="1"/>
   <color r="0" g="0" b="0"/>
   <color r="1" g="0" b="0"/>
   <color r="1" g="1" b="0"/>
   <color r="0" g="1" b="0"/>
   <color r="0" g="1" b="1"/>
   <color r="0" g="0" b="1"/>
   <color r="1" g="0" b="1"/>
  </colortable>
--*/
    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };


    public CDXColorTable() {
		super(CDXNAME);
        setCodeName();
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXColorTable(this);
    }
    /**
     * copy constructor
     * @param old
     */

    public CDXColorTable(CDXColorTable old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..colortable.."+"]";
    }
};



