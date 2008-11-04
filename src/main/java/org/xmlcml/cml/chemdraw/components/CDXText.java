package org.xmlcml.cml.chemdraw.components;


import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLLabel;
/**
 * 
 * @author pm286
 *
 */
public class CDXText extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXText.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8006;
    public final static String NAME = "Text";
    public final static String CDXNAME = "t";

/*--
Text="a) X=Cl, 25% b) X=OH, 80%"
LineHeight="1"
LineStarts="13 25"
p="15796356 5862751"
BoundingBox="5538348 15796356 6449298 18525930"
Warning="ChemDraw can't interpret this label."
--*/
    protected CodeName setCodeName() {
        codeName = new CodeName(CODE, NAME, CDXNAME);
        return codeName;
    };

    private String text = null;
//    private String warning = null;
//    private int[] lineStarts = null;
//    private String[] textStarts;
//    private int nLines = 0;

    public CDXText() {
		super(CDXNAME);
        setCodeName();
	}
	
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXText(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXText(CDXText old) {
    	super(old);
    }


//	private void setLineStarts(String t) {
//		StringTokenizer st = new StringTokenizer(t);
//		nLines = st.countTokens();
//		lineStarts = new int[nLines];
//		textStarts = new String[nLines];
//		for (int i = 0; i < nLines; i++) {
//			lineStarts[i] = Integer.parseInt(st.nextToken());
//		}
//		splitText();
//	}

//	private String getLineStarts() {
//		if (lineStarts == null) {
//            String ss = this.getAttributeValue("LineStarts");
//            if (ss != null) {
//                setLineStarts(ss);
//            }
//        }
//		String s = "";
//		for (int i = 0; i < lineStarts.length; i++) {
//			if (i > 0) s += " ";
//			s += lineStarts[i];
//		}
//		return s;
//	}
//
//	private void setText(String t) {
//		text = new String(t);
//		splitText();
//	}
//
//	private String getText() {
//		return text;
//	}

//	private String[] getTextStarts() {
//		return textStarts;
//	}

//    /** tidy node.
//    * turn temp_Text attribute into s child.
//    * of form [[1 2 3 4 5]] or [[1 2 3 4 5]][[6 7 8 9 10]]
//    * this is messy; there can be more than one font; take the last one
//    */
//	private void tidy() {
//        String content = "";
//        String t = this.getAttributeValue(TEMP_TEXT);
//        if (t != null && !t.equals("")) {
//            int idx1 = t.lastIndexOf("]]");
//            if (idx1 == -1) {
//                content = t;
//            } else {
//                int idx0 = t.lastIndexOf(FRBRAK+FLBRAK);
//                idx0 = (idx0 == -1) ? 2 : idx0 + 4;
//                this.removeAttribute(TEMP_TEXT);
//                CDXObject s = new CDXObject("s");
//                this.appendChild(s);
//                content = t.substring(idx1+2);
//                String fonts = t.substring(idx0, idx1);
//                String[] ss = fonts.split(" ");
//                if (ss.length == 5) {
//                    s.setAttribute("att1", ""+ss[0]);
//                    s.setAttribute("att2", ""+ss[1]);
//                    s.setAttribute("face", ""+ss[2]);
//                    s.setAttribute("size", ""+ss[3]);
//                    s.setAttribute("font", ""+ss[4]);
//                    s.appendChild(new Text(content));
//                } else {
//                    LOG.error("expected 5 font style attributes");
//                }
//            }
//        }
//    }
    
    /** return ints for font
     * order is startChar fontIndex typeface? size color
     * @param s of form [[1 2 3 4 5]]
     * @return 5 ints
     */
    static int[] unpackStyleRunString(String s) {
    	int[] ii = null;
    	if (s != null) {
    		String ss = s;
    		if (ss.startsWith(FLBRAK) && ss.endsWith(FRBRAK)) {
    			ss = ss.substring(FLBRAK.length(), ss.length()-FRBRAK.length());
    			ii = org.xmlcml.euclid.Util.splitToIntArray(ss, S_SPACE);
    			if (ii.length != 5) {
    				throw new RuntimeException("Bad font string; "+s);
    			}
    		} else {
    			throw new RuntimeException("Bad font string: "+s);
    		}
    	}
    	return ii;
    }
    
    /**
     * @param s
     * @return fontindex
     */
    static int getFontIndexFromTempText(String s) {
    	return unpackStyleRunString(s)[1];
    }
    
    /**
     * @param s
     * @return typeFace (as integer)
     */
    static int getTypeFaceFromTempText(String s) {
    	return unpackStyleRunString(s)[2];
    }
    
    /**
     * @param s
     * @return size
     */
    static int getSizeFromTempText(String s) {
    	return unpackStyleRunString(s)[3];
    }
    
    /**
     * @param s
     * @return color index
     */
    static int getColorIndexFromTempText(String s) {
    	return unpackStyleRunString(s)[4];
    }

    /** tidy node.
    * turn temp_Text attribute into attributes
    * of form [[1 2 3 4 5]] or [[1 2 3 4 5]][[6 7 8 9 10]]
    * this is messy; there can be more than one font; take the last one
    */
	public void addFontInfoFromTempText() {
        String t = this.getAttributeValue(TEMP_TEXT);
        if (t != null && !t.equals("")) {
            int idx1 = t.lastIndexOf(FRBRAK);
            if (idx1 != -1) {
            	// take last one
                int idx0 = t.lastIndexOf(FRBRAK+FLBRAK);
                t = (idx0 == -1) ? t : t.substring(idx0+FRBRAK.length());
                idx1 = t.lastIndexOf(FRBRAK);
                t = t.substring(0, idx1+FRBRAK.length());
                addAttribute(new Attribute(
            		"fontIndex", ""+getFontIndexFromTempText(t)));
                addAttribute(new Attribute(
            		"typeFace", ""+getTypeFaceFromTempText(t)));
                addAttribute(new Attribute(
            		"size", ""+getSizeFromTempText(t)));
                addAttribute(new Attribute(
            		"color", ""+getColorIndexFromTempText(t)));
            }
        }
    }
		
    // concatenates text in all "s" children
	private String getTextValue() {
        String s = "";
        for (int i = 0; i < getChildCount(); i++) {
            Node child = getChild(i);
            if (child instanceof Element) {
	            if (((Element)child).getLocalName().equals("s")) {
	                s += ((Element)child.getChild(0)).getValue();
	            }
            }
        }
        return s;
    }

	// might be useful
    public void process2CML(CMLElement cmlNode) {
        CMLLabel label = new CMLLabel();
        label.setDictRef("cdx:label");
        label.setCMLValue(this.getTextValue());
        this.copyAttributesTo(label);
        Element ss = (this.getChildCount() == 0) ? null : (Element) this.getChild(0);
        if (ss != null) {
        	((CDXObject)ss).copyAttributesTo(label);
        }
        try {
            cmlNode.appendChild(label);
//            processChildren2CML(label);
            this.copyAttributesTo(label);
        } catch (Exception de) {
            de.printStackTrace();
            LOG.error("Cannot add label "+cmlNode.getLocalName());
        }
    }

	/**
	 * @param node
	 * @return label
	 * @throws RuntimeException
	 */
	CMLLabel createLabelFromText() throws RuntimeException {
		String vv = getLabelText();
		CMLLabel label = new CMLLabel();
		label.setCMLValue(vv);
		this.copyAttributesTo(label);
		return label;
	}

	/**
	 * @return text after [[..]]
	 */
	String getLabelText() {
		String vv = this.query("./@temp_Text").get(0).getValue();
		// can be several of these
		while (vv.startsWith("[[")) {
			int idx = vv.indexOf("]]");
			if (idx != -1) {
				vv = vv.substring(idx+2);
			} else {
				break;
			}
		}
		return vv;
	}

    /**
     * @return s
     */
    public String getString() {
        return "["+text+"]";
    }
};


