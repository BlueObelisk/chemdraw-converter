package org.xmlcml.cml.chemdraw.components;


import nu.xom.Attribute;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDXConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.euclid.RealArray;

/**
 * Docs are missing on site
 * 
 * @author pm286
 *
 */
public class CDXArrow extends CDXObject {

	/**
	 * 
	 */
    static Logger LOG = Logger.getLogger(CDXArrow.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8027;
    public final static String NAME = "Arrow";
    public final static String CDXNAME = "arrow";

	public CDXArrow() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXArrow(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXArrow(CDXArrow old) {
    	super(old);
    }



    void createTypes() {
    }

    public void process2CML(CMLElement element) {
    	element.appendChild(createArrowArray(this));
    }

	public static CMLArray createArrowArray(CDXObject object) {
		RealArray realArray = new RealArray(object.getAttributeValue(BoundingBox.TAG));
    	CMLArray array = new CMLArray(realArray);
    	CMLUtil.copyAttributes(object, array);
    	array.addAttribute(new Attribute("type", "Arrow"));
        array.setDictRef("cdxml:arrowType");
        // avoids empty content
//        String content = object.getAttributeValue(BoundingBox.TAG, CDXConstants.CDX_NAMESPACE);
//        if (content == null) {
//            array.setXMLContent("dummy");
//        } else {
////        	RealArray realArray = new RealArray(content);
////            arrow = new CMLArray(realArray);
//        }
    	
		return array;
	}

};

