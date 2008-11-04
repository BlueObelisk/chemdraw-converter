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
public class CDXBracketedGroup extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXBracketedGroup.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8017;
    public final static String NAME = "BracketedGroup";
    public final static String CDXNAME = "bracketedgroup";

/*--
  <page 
  BoundingBox="0 0 538.507 785.107" 
  WidthPages="1" 
  HeightPages="1" 
  HeaderPosition="35.9999" 
  FooterPosition="35.9999"
  id="156">
--*/
    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };


	public CDXBracketedGroup() {
		super(CDXNAME);
        setCodeName();
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXBracketedGroup(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXBracketedGroup(CDXBracketedGroup old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..bracketedgroup.."+"]";
    }
};



