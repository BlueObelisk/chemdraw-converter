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
public class CDXFontTable extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXFontTable.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x0100;
    public final static String NAME = "FontTable";
    public final static String CDXNAME = "fonttable";

/*--
  <fonttable>
   <font id="3" charset="1252" name="Arial"/>
  </fonttable>
--*/

    public CDXFontTable() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXFontTable(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXFontTable(CDXFontTable old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..fonttable.."+"]";
    }
};



