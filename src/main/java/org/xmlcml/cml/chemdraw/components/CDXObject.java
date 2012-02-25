package org.xmlcml.cml.chemdraw.components;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.IllegalCharacterDataException;
import nu.xom.NamespaceConflictException;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDXConstants;
import org.xmlcml.cml.chemdraw.CDXML2CMLProcessor;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;

/**
 * holds CDX stuff as XML
 * @author pm286
 *
 */
public class CDXObject extends Element implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXObject.class);
    static {
    	LOG.setLevel(Level.INFO);
    }

	static Hashtable<String, CDXObject> objTable;
	static Hashtable<String, CDXObject> nameTable;

    private CDXParser parser;
    void setParser(CDXParser parser) {
    	this.parser = parser;
    }
    
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXObject(this);

    }
    
/**---
Object 	Value 	Name 	CDXML Name
Document 	0x8000 	kCDXObj_Document 	CDXML
The top-level CDX object. It contains all CDX attributes and objects.
Page 	0x8001 	kCDXObj_Page 	page
A drawing space that can contain other objects.
Group 	0x8002 	kCDXObj_Group 	group
A logical collection of objects.
Fragment 	0x8003 	kCDXObj_Fragment 	fragment
A collection of nodes and their connectivity (bonds).
Node 	0x8004 	kCDXObj_Node 	n
The basic building block of chemical objects, usually referring to a single atom
Bond 	0x8005 	kCDXObj_Bond 	b
A connection between two Node objects.
Text 	0x8006 	kCDXObj_Text 	t
An arbitrary block of (possibly styled) text.
Graphic 	0x8007 	kCDXObj_Graphic 	graphic
A (generally non-chemical) graphic object such as a line, arc, circle, or rectangle.
Bracketed Group 	0x8017 	kCDXObj_BracketedGroup 	bracketedgroup
A collection of objects surrounded by brackets (or braces or parentheses).
Bracket Attachment 	0x8018 	kCDXObj_BracketAttachment 	bracketattachment
A linkage that connects a Bracketed Group to some object outside that group.
Crossing Bond 	0x8019 	kCDXObj_CrossingBond 	crossingbond
A Bond that connects a Bracketed Group to a Node outside that group.
Curve 	0x8008 	kCDXObj_Curve 	curve
A Bézier curve.
Embedded Object 	0x8009 	kCDXObj_EmbeddedObject 	embeddedobject
A Macintosh PICT, a Windows Metafile, or a Windows OLE Object.
Table 	0x8016 	kCDXObj_Table 	table
A grid-like arrangement of drawing spaces.
Named Alternative Group 	0x800A 	kCDXObj_NamedAlternativeGroup 	altgroup
A container object holding fragments that represent alternative substituents for a query.
Template Grid 	0x800B 	kCDXObj_TemplateGrid 	templategrid
A TemplateGrid indicates how multiple CDX page objects should be arranged in a Template document.
Registry Number 	0x800C 	kCDXObj_RegistryNumber 	regnum
A registry or catalog number, along with the name of the authority which issued the number.
Reaction Scheme 	0x800D 	kCDXObj_ReactionScheme 	scheme
A description of a single- or multi-step reaction, containing one or more Reaction Steps.
Reaction Step 	0x800E 	kCDXObj_ReactionStep 	step
A description of one step in a reaction. It contains the constituents, conditions, and products.
Spectrum 	0x8010 	kCDXObj_Spectrum 	spectrum
An NMR, MS, IR or other sort of spectral plot.
Object Tag 	0x8011 	kCDXObj_ObjectTag 	objecttag
Arbitrarily named property, one or more of which can be attached to any ChemDraw object.
Sequence 	0x8013 	kCDXObj_Sequence 	sequence
One member of an ordered series; the contents of its Text object may change as other objects are added to or removed from the series.
Cross-Reference 	0x8014 	kCDXObj_CrossReference 	crossreference
A link to some Sequence object.
Border 	0x8020 	kCDXObj_Border 	border
A collection of information describing one edge of an object.
Geometry 	0x8021 	kCDXObj_Geometry 	geometry
A geometrical relationship between one or more objects.
Constraint 	0x8022 	kCDXObj_Constraint 	constraint
A distance or angle constraint between one or more objects.
TLC Plate 	0x8023 	kCDXObj_TLCPlate 	tlcplate
A rectangular object representing a Thin Layer Chromatography (TLC) plate.
TLC Lane 	0x8024 	kCDXObj_TLCLane 	tlclane
A logical object representing a series of spots arranged vertically on a TLC plate.
TLC Spot 	0x8025 	kCDXObj_TLCSpot 	tlcspot
A single spot on a TLC plate.
Arrow 0x8027 kCDXObj_Arrow arrow 
A line or arc, optionally with arrowheads on one or both ends 
Splitter 	0x8015 	kCDXObj_Splitter 	splitter
An object that divides the page into horizontal bands
Chemical Property 	0x8026 	kCDXObj_ChemicalProperty 	chemicalproperty
A (physical) chemical property associated with a collection of objects, usually atoms and bonds.
Color Table 	0x0300 	kCDXProp_ColorTable 	colortable
The color palette used throughout the document. Color indexes 0 and 1 always correspond to black and white and are not saved in the color table. The first and second RGB values (color indexes 2 and 3) are the default background and foreground colors, and other colors are numbered sequentially.
Color 	n/a 	n/a 	color
An RGB color.
Font Table 	0x0100 	kCDXProp_FontTable 	fonttable
A list of fonts used in the document.
Font 	n/a 	n/a 	font
A logical font definition.
Style 	n/a 	n/a 	s
A string of text in exactly one style.
Represents Property 	0x000e 	kCDXProp_RepresentsProperty 	represent
An object used to indicate that its containing object has chemical meaning that is also represented in another object.
--*/
    static void makeObjects() {
        if (objTable == null) {
            objTable = new Hashtable<String, CDXObject>();
        }
        if (nameTable == null) {
            nameTable = new Hashtable<String, CDXObject>();
        }
/*        
    	// Add new objects here
    	kCDXObj_UnknownObject = 0x8FFF

*/        
        
        try {
/*
    	kCDXObj_Document = kCDXTag_Object,	// 0x8000
    	kCDXObj_Page,						// 0x8001
    	kCDXObj_Group,						// 0x8002
    	kCDXObj_Fragment,					// 0x8003
    	kCDXObj_Node,						// 0x8004
    	kCDXObj_Bond,						// 0x8005
    	kCDXObj_Text,						// 0x8006
    	kCDXObj_Graphic,					// 0x8007
    	kCDXObj_Curve,						// 0x8008
    	kCDXObj_EmbeddedObject,				// 0x8009
    	kCDXObj_NamedAlternativeGroup,		// 0x800a
    	kCDXObj_TemplateGrid,				// 0x800b
    	kCDXObj_RegistryNumber,				// 0x800c
    	kCDXObj_ReactionScheme,				// 0x800d
    	kCDXObj_ReactionStep,				// 0x800e
    	kCDXObj_ObjectDefinition,			// 0x800f ??
 */        	
            makeObject(0x8000, new CDXML());
            makeObject(0x8001, new CDXPage());              //0x8001
            makeObject(0x8002, new CDXGroup());             //0x8002
            makeObject(0x8003, new CDXFragment());          //0x8003
            makeObject(0x8004, new CDXNode());              //0x8004
            makeObject(0x8005, new CDXBond());              //0x8005
            makeObject(0x8006, new CDXText());              //0x8006
            makeObject(0x8007, new CDXGraphic());           //0x8007
            makeObject(0x8008, new CDXCurve());
            makeObject(new CDXObject(0x8009, "EmbeddedO", "embeddedo"));
            makeObject(new CDXObject(0x800A, "NamedAlternativeGroup", "altgroup"));
            makeObject(new CDXObject(0x800B, "TemplateGrid", "templategrid"));
            makeObject(new CDXObject(0x800C, "RegistryNumber", "regnum"));
            makeObject(0x800D, new CDXReactionScheme());    //0x800D
            makeObject(0x800E, new CDXReactionStep());      //0x800E
//        	kCDXObj_ObjectDefinition,			// 0x800f
                                                            //0x800F
/*
    	// Objects.
    	kCDXObj_Spectrum,					// 0x8010
    	kCDXObj_ObjectTag,					// 0x8011
    	kCDXObj_OleClientItem,				// 0x8012	// obsolete
    	kCDXObj_Sequence,                   // 0x8013
    	kCDXObj_CrossReference,             // 0x8014
    	kCDXObj_Splitter,				    // 0x8015
    	kCDXObj_Table,					    // 0x8016
    	kCDXObj_BracketedGroup,				// 0x8017
    	kCDXObj_BracketAttachment,			// 0x8018
    	kCDXObj_CrossingBond,				// 0x8019
    	kCDXObj_Border,						// 0x8020
    	kCDXObj_Geometry,					// 0x8021
    	kCDXObj_Constraint,					// 0x8022
    	kCDXObj_TLCPlate,					// 0x8023
    	kCDXObj_TLCLane,					// 0x8024
    	kCDXObj_TLCSpot,					// 0x8025
    	kCDXObj_ChemicalProperty,		    // 0x8026  // ???
    	kCDXObj_Arrow,   					// 0x8027
 */            
            makeObject(new CDXObject(0x8010, "Spectrum", "spectrum"));
            makeObject(0x8011, new CDXObjectTag());			//0x8011
            // 0x8012 is osolete
            makeObject(new CDXObject(0x8013, "Sequence", "sequence"));
            makeObject(new CDXObject(0x8014, "CrossReference", "crossreference"));
            makeObject(new CDXObject(0x8015, "Splitter", "splitter"));
            makeObject(new CDXObject(0x8016, "Table", "table"));
            makeObject(0x8017, new CDXBracketedGroup());
            makeObject(0x8018, new CDXBracketAttachment());
            makeObject(new CDXObject(0x8019, "CrossingBond", "crossingbond"));

            makeObject(new CDXObject(0x8020, "Border", "border"));
            makeObject(0x8021, new CDXGeometry());
            makeObject(new CDXObject(0x8022, "Constraint", "constraint"));
            makeObject(new CDXObject(0x8023, "TLCPlate", "tlcplate"));
            makeObject(new CDXObject(0x8024, "TLCLane", "tlclane"));
            makeObject(new CDXObject(0x8025, "TLCSpot", "tlcspot"));
            makeObject(new CDXObject(0x8026, "ChemicalProperty", "chemicalproperty"));
            makeObject(0x8027, new CDXArrow());
            
            makeObject(0x0100, new CDXFontTable());
            makeObject(0x0300, new CDXColorTable());
            
            makeObject(new CDXObject(0x000e, "kCDXProp_RepresentsProperty", "represent"));
            
    // these have no standard numbers or names - I have made these up
            makeObject(new CDXObject(0x8454, "Style", "s")); // the text style
            makeObject(new CDXObject(0x8456, "Color", "color"));
            makeObject(new CDXObject(0x8458, "Font", "font"));
    // non-standard CDX element (PMR) as documentElement
            makeObject(0x8099, new CDXList());              //0x8099
        } catch (Throwable t) {
            t.printStackTrace();
            LOG.error("Cannot initialize "+t);
        }
	};


	private static CDXObject getObject(int code) {
        CDXObject cdxObject = objTable.get(""+code);
        LOG.debug("name: "+((cdxObject == null) ? null : cdxObject.codeName.cdxName));
        if (cdxObject == null || cdxObject.codeName.cdxName.equals("unknown")) {
        	LOG.debug("unknown: "+code+"/"+Integer.toHexString(code));
        }
        return (cdxObject == null) ? cdxObject: cdxObject;
    }

	private static void makeObject(CDXObject object) {
        objTable.put(""+object.codeName.code, object);
        String name = object.codeName.cdxName;
        if (name == null) {
        	throw new RuntimeException("NULL NAME "+object.getClass().getName()+" .. "+((Element) object).getLocalName());
        } else {
        	nameTable.put(name, object);
        }
    };

    private static void makeObject(int code, CDXObject obj) {
        makeObject(obj);
        if (code != obj.codeName.code) {
        	new Exception().printStackTrace();
            LOG.error("Codes do not agree: "+code+"/"+Integer.toHexString(code)+" != "
            		+obj.codeName.code+"/"+Integer.toHexString(obj.codeName.code)+"("+obj.getClass().getName()+")");
        }
    };

    static int CODE = 0x9999;
    static String NAME = "unknown";
    static String CDXNAME = "unknown";

    protected CodeName codeName;
    
// XML attributes
	protected String id;
	protected BoundingBox bbox;
//    private Style style;
	private CDXPoint2D p;
//    private int z = -99999999;
    private Vector<CDXProperty> propVector;

//	@SuppressWarnings("unused")
	private CDXML2CMLProcessor cdxmlObject = null;

    // only used for representatives
    protected CDXObject(int code, String name, String cdxName) {
    	super(cdxName);
        codeName = new CodeName(code, name, cdxName);
    }

    /**
     * copy constructor. copies attributes, children and properties using the
     * copyFoo() routines (q.v.)
     * 
     * @param element
     */
    public CDXObject(CDXObject element) {
        super(element.getLocalName());
        this.codeName = element.codeName;
        CMLElement.copyAttributesFromTo(element, this);
        CMLElement.copyChildrenFromTo(element, this);
    }

    /** only used for trivial objects (font, s, color)
     * 
     * @param tagName
     */
	private CDXObject (String tagName) {
		super(tagName);
        init();
	}

	private void init() {
    }

	static CDXObject newCDXObject(int code) {
        CDXObject obj = null;
        CDXObject refObj = getObject(code);
        String name = (refObj == null) ? "object" : refObj.codeName.cdxName;
		if (false) {
            ;
		} else if (code == CDXArrow.CODE) {
			obj = new CDXArrow();
		} else if (code == CDXBond.CODE) {
			obj = new CDXBond();
		} else if (code == CDXBracketAttachment.CODE) {
			obj = new CDXBracketAttachment();
		} else if (code == CDXBracketedGroup.CODE) {
			obj = new CDXBracketedGroup();
		} else if (code == CDXColorTable.CODE) {
			obj = new CDXColorTable();
		} else if (code == CDXCurve.CODE) {
			obj = new CDXCurve();
		} else if (code == CDXML.CODE) {
			obj = new CDXML();
		} else if (code == CDXFontTable.CODE) {
			obj = new CDXFontTable();
		} else if (code == CDXFragment.CODE) {
			obj = new CDXFragment();
		} else if (code == CDXGeometry.CODE) {
			obj = new CDXGeometry();
		} else if (code == CDXGraphic.CODE) {
			obj = new CDXGraphic();
		} else if (code == CDXGroup.CODE) {
			obj = new CDXGroup();
		} else if (code == CDXNode.CODE) {
			obj = new CDXNode();
		} else if (code == CDXObjectTag.CODE) {
			obj = new CDXObjectTag();
		} else if (code == CDXPage.CODE) {
			obj = new CDXPage();
		} else if (code == CDXReactionScheme.CODE) {
			obj = new CDXReactionScheme();
		} else if (code == CDXReactionStep.CODE) {
			obj = new CDXReactionStep();
		} else if (code == CDXText.CODE) {
			obj = new CDXText();
		} else {
			obj = new CDXObject(name);
            throw new RuntimeException("Unknown CDX name, code = "+name+", "+code+"/"+Integer.toHexString(code));
        }
        return obj;
	}

	/** copy attributes from CDX Object.
	 * add CDX namespace
	 * @param element
	 */
	protected void copyAttributesTo(CMLElement element) {
		if (element == null) {
			throw new RuntimeException("null element to copy to");
		}
        String id = element.getId();
        for (int i = 0; i < this.getAttributeCount(); i++) {
            Attribute att = this.getAttribute(i);
            String attName = att.getLocalName();
            String attVal = att.getValue();
            if (!attName.equals(CMLXSD_ID)) {
	            Attribute newAtt = new Attribute(CDX_PREFIX+S_COLON+attName, CDX_NAMESPACE, attVal);
	            element.addAttribute(newAtt);
            } else if (attName.equals(CMLXSD_ID) && id == null) {
            	element.setId(attVal);
            }
        }
	}

	public void setChemDrawConverterRecursively(CDXML2CMLProcessor cdxmlObject) {
		this.cdxmlObject = cdxmlObject;
		Elements childElements = this.getChildElements();
		for (int i = 0; i < childElements.size(); i++) {
			if (childElements.get(i) instanceof CDXObject) {
				((CDXObject)childElements.get(i)).setChemDrawConverterRecursively(cdxmlObject);
			}
		}
	}
	
	protected void writeLog(Level level, String s) {
		LOG.debug(s);
	}
	
	void addProperty(CDXProperty prop) {
        if (propVector == null) {
            propVector = new Vector<CDXProperty>();
        }
        propVector.addElement(prop);
        String name = prop.getCDXName();
        try {
        	setAttribute(name, prop.getDataTypeString());
        } catch (IllegalCharacterDataException ice) {
        	if (name.equals("MacPrintInfo")) {
        		LOG.warn(ice.getMessage());
        	} else {
        		throw ice;
        	}
        }
    }

	private void processAttributes() {
        if (propVector != null) {
//            HashMap map = new HashMap();
            for (int i = 0; i < propVector.size(); i++) {
                CDXProperty prop = propVector.elementAt(i);
                String name = prop.getCDXName();
                if (name != null) {
	                String value = prop.getDataTypeString();
	                if (value != null) {
		                if (Character.isDigit(name.charAt(0))) {
		                    name = "_"+name;
		                }
		                try {
		                	this.setAttribute(name, value);
		                } catch (IllegalCharacterDataException ice) {
		                	if (name.equals("MacPrintInfo")) {
		                	} else {
		                		throw ice;
		                	}
		                }
	                } else if (name.equals("LabelFont")) {
	                } else if (name.equals("LabelFace")) {
	                } else if (name.equals("LabelSize")) {
	                } else if (name.equals("LabelColor")) {
	                } else if (name.equals("CaptionFont")) {
	                } else if (name.equals("CaptionColor")) {
	                } else if (name.equals("CaptionSize")) {
	                	
	                } else {
	                	throw new RuntimeException("null attVal for "+name);
	                }
                } 
            }
        }
    }

// subclass for special treatment
// and use super.endElement()
	void endElement() {
//        getChemDrawDocument();
        if (propVector != null) {
            CDXProperty[] propArray = propVector.toArray(new CDXProperty[0]);
            for (int i = 0; i < propArray.length; i++) {
                CDXProperty prop = propArray[i];
                String name = prop.getCDXName().trim();
                String value = prop.getDataTypeString();
// this really requires the properties to be objects...
                if (false) {
                    ;
                } else if (name.equals(BoundingBox.TAG)) {
                    processBoundingBox(value);
                } else if (name.equals("p")) {
                    processPoint(value);
                } else if (name.equals("Text")) {
                    processText(value);
                    propVector.remove(prop);
                } else if (name.equals("fonttable")) {
                    processFontTable(value);
                    propVector.remove(prop);
                } else if (name.equals("colortable")) {
                    processColorTable(value);
                    propVector.remove(prop);
    // split FooStyle into sub components
                } else if (name.trim().endsWith("Style")) {
                    processStyle(name, value);
                    propVector.remove(prop);
                }
            }
            processAttributes();
        }
        id = ""+this.getAttributeValue("id");
        this.setAttribute("id", id);
        this.removeAttribute("Text");
        this.removeAttribute("fonttable");
        this.removeAttribute("colortable");
        this.removeAttribute("fontStyle");
        this.removeAttribute("LabelStyle");
        this.removeAttribute("CaptionStyle");
        cleanBugs();
    }

	private void cleanBugs() {
//		Nodes cdxNodes = this.query("//*[namespace-uri()='"+CDXConstants.CDX_NAMESPACE+"']");
		Nodes cdxNodes = this.query("//*[local-name()='"+CDXGeometry.CDXNAME+"']");
		for (int i = 0; i < cdxNodes.size(); i++) {
			if (cdxNodes.get(i) instanceof CDXObject) {
				((CDXObject) cdxNodes.get(i)).fixBugs();
			}
		}
	}

	/** subclass when required
	 * 
	 */
	protected void fixBugs() {
	}

	private void processBoundingBox(String value) {
        BoundingBox bbox = new BoundingBox();
        bbox.setFloatValue(value);
        setBoundingBox(bbox);
    }

	private void processPoint(String value) {
        CDXPoint2D point = new CDXPoint2D();
        point.setFloatValue(value);
        setPoint2D(point);
    }

/*--
*** lineStarts only affect newlines, not font changes ***
LineStarts Property
CDXML Name: LineStarts
CDX Constant Name: kCDXProp_LineStarts
CDX Constant Value: 0x0704
Data Size: INT16ListWithCounts
Property of objects: kCDXObj_Text

First written/read in: ChemDraw 4.0
Required? No

Description:


The number of lines of a text object followed by that many values indicating the zero-based text position of each line start.

If this property is absent:


The line starts are inferred solely from the presence of end-of-line (0x0D) characters in the text.

--*/
	private void processText(String value) {
        int idx = value.lastIndexOf("]]");
        Vector<int[]> v = new Vector<int[]>();
// parse each font run and stire as integers
        while (value.startsWith(FLBRAK)) {
            idx = value.indexOf(FRBRAK);
            String vv = value.substring(2, idx);
            value = value.substring(idx+2);
            StringTokenizer st = new StringTokenizer(vv, " ");
            int[] temp = new int[5];
            for (int i = 0; i < 5; i++) {
                temp[i] = Integer.parseInt(st.nextToken());
            }
            v.add(temp);
        }
        int nrun = v.size();
// record starts
        int[] start = new int[nrun];
        for (int i = 0; i < nrun; i++) {
            start[i] = v.elementAt(i)[0];
        }
// generate children
        for (int i = 0; i < nrun; i++) {
            int[] temp = v.elementAt(i);
//            int startChar = temp[0];
            int font = temp[1];
            int face = temp[2];
            int size = temp[3];
            int color = temp[4];
            CDXObject s = new CDXObject("s");
            this.appendChild(s);
            processFont(font, face, size, color, s);
            String vv = "";
            if (i < nrun-1) {
                vv = value.substring(start[i], start[i+1]);
            } else {
                vv = value.substring(start[i]);
            }
            s.appendChild(new Text(vv));
        }
    }

	private void processFont(int font, int face, int size, int color,
			CDXObject s) {
		CDXProperty fontProp = CDXProperty.createPropertyByCDXName("fontindex");
		fontProp.setNames("font");
		s.addProperty(fontProp);
		fontProp.setValue(font);
		s.setAttribute("font", ""+font);
		setFontFace(face, s);
		setFontSize(size, s);
		setFontColor(color, s);
	}

	private void setFontFace(int face, CDXObject s) {
		if (face != 0) {
		    CDXProperty faceProp = CDXProperty.createPropertyByCDXName("fontface");
		    faceProp.setNames("face");
		    s.addProperty(faceProp);
		    faceProp.setValue(face);
		}
	}

	private void setFontSize(int size, CDXObject s) {
		if (size != 0) {
		    CDXProperty sizeProp = CDXProperty.createPropertyByCDXName("fontsize");
		    sizeProp.setNames("size");
		    s.addProperty(sizeProp);
		    sizeProp.setValue(size);
		    s.setAttribute("size", ""+size);
		}
	}

	private void setFontColor(int color, CDXObject s) {
		if (color != 0) {
		    CDXProperty colorProp = CDXProperty.createPropertyByCDXName("fontcolor");
		    colorProp.setNames("color");
		    s.addProperty(colorProp);
		    colorProp.setValue(color);
		    s.setAttribute("color", ""+color);
		}
	}

	private void processFontTable(String value) {
        CDXObject fontTable = new CDXFontTable();
//        CDXObject fonttable = new CDXObject("fonttable");
        this.appendChild(fontTable);
        StringTokenizer st = new StringTokenizer(value, ";");
        while (st.hasMoreTokens()) {
            CDXObject font = new CDXObject("font");
            fontTable.appendChild(font);
            String ss = st.nextToken();
            StringTokenizer st1 = new StringTokenizer(ss, "/");
            try {
                CDXProperty idProp = CDXProperty.createPropertyByCDXName("id");
                idProp.setValue((long) Long.parseLong(st1.nextToken()));
                font.addProperty(idProp);
                CDXProperty charsetProp = CDXProperty.createPropertyByCDXName("charset");
                charsetProp.setValue(st1.nextToken());
                font.addProperty(charsetProp);
                CDXProperty fontProp = CDXProperty.createPropertyByCDXName("name");
                fontProp.setValue(st1.nextToken());
                font.addProperty(fontProp);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.warn("bad string? :"+e);
            }
        }
    }

	private void processColorTable(String value) {
//        CDXObject fonttable = new CDXObject("colortable");
        CDXObject colorTable = new CDXColorTable();
        this.appendChild(colorTable);
        StringTokenizer st = new StringTokenizer(value, ";");
        while (st.hasMoreTokens()) {
            CDXObject color = new CDXObject("color");
            colorTable.appendChild(color);
            String ss = st.nextToken();
            StringTokenizer st1 = new StringTokenizer(ss, " ");
            try {
                CDXProperty rProp = CDXProperty.createPropertyByCDXName("r");
                rProp.setValue((double) Integer.parseInt(st1.nextToken()) / 65535.);
                color.addProperty(rProp);
                CDXProperty gProp = CDXProperty.createPropertyByCDXName("g");
                gProp.setValue((double) Integer.parseInt(st1.nextToken()) / 65535.);
                color.addProperty(gProp);
                CDXProperty bProp = CDXProperty.createPropertyByCDXName("b");
                bProp.setValue((double) Integer.parseInt(st1.nextToken()) / 65535.);
                color.addProperty(bProp);
            } catch (Exception e) {
                e.printStackTrace();
                LOG.warn("bad string? :"+e);
            }
        }
    }

	private void processStyle(String name, String value) {
        String foo = name.substring(0, name.length()-"Style".length());
        StringTokenizer st = new StringTokenizer(value, " ");
        int font = Integer.parseInt(st.nextToken());
        int face = Integer.parseInt(st.nextToken());
        int size = Integer.parseInt(st.nextToken());
        int color = Integer.parseInt(st.nextToken());
        try {
            CDXProperty fontProp = CDXProperty.createPropertyByCDXName("fontindex");
            fontProp.setNames(foo+"Font");
            this.addProperty(fontProp);
            fontProp.setValue(font);
//            if (face != 0) {
                CDXProperty faceProp = CDXProperty.createPropertyByCDXName("fontface");
                faceProp.setNames(foo+"Face");
                this.addProperty(faceProp);
                faceProp.setValue(face);
//            }
            if (size != 0) {
                CDXProperty sizeProp = CDXProperty.createPropertyByCDXName("fontsize");
                sizeProp.setNames(foo+"Size");
                this.addProperty(sizeProp);
                sizeProp.setValue(size);
            }
            if (color != 0) {
                CDXProperty colorProp = CDXProperty.createPropertyByCDXName("fontcolor");
                colorProp.setNames(foo+"Color");
                this.addProperty(colorProp);
                colorProp.setValue(color);
            }
        } catch (Exception e) {
            LOG.warn(""+e);
        }
    }

//	protected static String getAttribute(String name, String value) {
//		return " "+name+"=\""+value+"\"";
//	}
//
	private void setBoundingBox(BoundingBox bbox) {
		this.bbox = bbox;
	}

//	private  BoundingBox getBoundingBox() {
//		return bbox;
//	}

	void setId(int id) {
		this.id = ""+id;
        this.setAttribute("id", ""+id);
	}

	String getId() {
        if (id == null || id.trim().equals("")) {
            id = this.getAttributeValue("id");
        }
        return this.id;
	}

//	private void setZ(int z) {
//		this.z = z;
//	}
//
//	private int getZ() {
//		return this.z;
//	}

	private void setPoint2D(CDXPoint2D p) {
		this.p = p;
	}

	CDXPoint2D getPoint2D() {
        if (p == null) {
            String pS = this.getAttributeValue("p");
            if (pS == null) {
//            	LOG.error("null value for point");
            } else {
                processPoint(pS);
            }
        }
		return p;
	}

    /** process CDXObjects.
    * ideally should use subclassed elements, but this requires
    * CML classes in chemdraw code
    */
	public void process2CML(CMLElement cmlNode) {
// default don't keep this node in the tree
        if (!(this instanceof CDXObject)) {
        	LOG.error("non-CDX object: "+this);
        } else {
// just process children and remember current cmlParent
            processChildren2CML(cmlNode);
        }
    }

	protected void addCDXAttribute(Element to, Element from, String attName) {
        String s = from.getAttributeValue(attName);
        if (s != null && !s.trim().equals("")) {
            setAttribute(to, "cdx:"+attName, s);
        }
    }

	private void processChildren2CML(Node cmlNode) {
        if (cmlNode instanceof Document) {
            this.process2CML((CMLElement)((Document)cmlNode).getRootElement());
        } else if (cmlNode instanceof CMLElement){
            for (int i = 0; i < this.getChildCount(); i++) {
                Node node = this.getChild(i);
                if (node instanceof CDXObject) {
                    ((CDXObject) this.getChild(i)).process2CML((CMLElement)cmlNode);
    // skip text
                } else if (node instanceof Text) {
                } else {
                    LOG.error("Unknown node type: "+node.getClass().getName());
                }
            }
        } else {
        	throw new RuntimeException("bad type of node: "+cmlNode);
        }
    }

	void processFontTable() {
        LOG.info("Ignored fonttable");
    }

	void processColorTable() {
        LOG.info("Ignored colortable");
    }


//    /** add original CDX attributes to CML element.
//    */
//	private void addAttributesTo(CMLElement ab) {
//        for (int i = 0; i < this.getAttributeCount(); i++) {
//        	Attribute att = this.getAttribute(i);
//            ab.setAttribute(att.getLocalName(), att.getValue());
//        }
//    }
    
    // convenience methods to convert from old version
    protected void removeAttribute(String attName) {
    	Attribute att = this.getAttribute(attName);
    	if (att != null) {
    		this.removeAttribute(att);
    	}
    }
    
    protected void setAttribute(String attName, String attVal) {
    	if (attName == null) {
//	        LOG.error(" >>>>> null attName ("+Integer.toHexString(parser.byteCount)+") ");
//    		throw new RuntimeException("Null attName");
    	} else if (attVal == null) {
//	        LOG.error(" >>>>>> null attValue ("+Integer.toHexString(parser.byteCount)+")in "+attName);
//    		throw new RuntimeException("Null attValue");
    	} else {
	    	try {
		    	setAttribute(this, attName, attVal);
			} catch (nu.xom.IllegalCharacterDataException ice) {
		        LOG.debug("bad characters ("+Integer.toHexString(parser.byteCount)+")in "+attName+" ... "+attVal+" ... "+ice);
			}
    	}
    }
    
    private static void setAttribute(Element elem, String attName, String attVal) {
    	try {
    		int ipfx = attName.indexOf(S_COLON);
    		Attribute att = null;
    		if (ipfx > 0) {
//    			String prefix = attName.substring(0, attName.indexOf(S_COLON));
    			att = new Attribute(attName, CDX_NAMESPACE, attVal);
    		} else {
				att = new Attribute(attName, attVal);
    		}
	    	elem.addAttribute(att);
    	} catch (NamespaceConflictException ne) {
    		CMLUtil.debug(elem, "CDXOBJECT");
    		throw new RuntimeException("bad attribute: "+ne+" ... "+attName);
    	}
    }
    

    public void debug(String msg) {
    	CMLUtil.debug(this, msg);
    }
	/**
	 * 
	 * @return string
	 */
	public String getString() {
        String s = "CDXObject ";
        if (codeName != null) {
            s += codeName.code;
            s += "/"+codeName.name;
            s += "/"+codeName.cdxName;
        }
        return s;
    }

	/**
	 * @param element
	 */
	void processMoleculeFragments(CMLElement element) {
		//    	      <fragment BoundingBox="138.9999 67.0011 214.1269 91.6999" id="19">
		//    	        <n Z="121" p="140.1129 88.4499" AS="N" id="18"/>
		//    	        <n Z="122" p="150.6777 82.3503" AS="N" id="20"/>
		//    	        <b Z="123" B="18" E="20" BS="N" id="21"/>
		//    	        <n Z="124" p="161.2426 88.4499" AS="N" id="22"/>
		//	   	Nodes moleculeFragments = this.query("./fragment[not(n[@NodeType])]");
	   	Nodes moleculeFragments = this.query("./fragment[count(*) > count(n[@NodeType])]");
	  	for (int i = 0; i < moleculeFragments.size(); i++) {
	  		CDXObject moleculeFragment = (CDXObject) moleculeFragments.get(i);
			CMLMolecule molecule = new CMLMolecule();
			element.appendChild(molecule);
			((CDXFragment)moleculeFragment).process2CML(molecule);
			moleculeFragment.copyAttributesTo(molecule);
	  		copyParentIdToMolecule(moleculeFragment, molecule);
			moleculeFragment.detach();
	  	}
	}

	private void copyParentIdToMolecule(CDXObject moleculeFragment,
			CMLMolecule molecule) {
		String parentId = ((Element)moleculeFragment.getParent()).getAttributeValue("id");
//		String parentId = ((Element)moleculeFragment).getAttributeValue("id");
		molecule.setId(parentId);
	}

	/**
	 * @param element
	 * @throws RuntimeException
	 */
	void processFragmentsContainingFragmentNodes(CMLElement element) throws RuntimeException {
		//        <fragment BoundingBox="241.6999 68.4799 290.4249 74.9749" id="97">
		//          <n Z="165" p="244 71" NodeType="Fragment" AS="N" id="96">
		//            <t temp_Text="[[0 3 97 6 3]]H2NCONH2H2O2" LineHeight="1" LabelAlignment="Left" p="241.6999 73.4749" BoundingBox="241.6999 68.4799 290.4249 74.9749" LabelLineHeight="1" LabelJustification="Left" id="0"/>
		//            <fragment BoundingBox="219.0299 57.7405 273.6799 87.9999" id="159">
		Nodes fragmentFragments = this.query("./fragment[count(*)=1 and" +
				" count(n) = 1 and" +
				" n[(@NodeType='Fragment' or @NodeType='AnonymousAlternativeGroup') and" +
				" count(*)=2 and" +
				" count(t)=1 and" +
				" count(fragment)=1]]");
		for (int i = 0; i < fragmentFragments.size(); i++) {
			CDXObject fragmentFragment = (CDXObject) fragmentFragments.get(i);
			CMLMolecule molecule = new CMLMolecule();
			molecule.setRole("cdx:fragment");
			element.appendChild(molecule);
			addLabelToMolecule(fragmentFragment, molecule);
			processSubFragment(fragmentFragment, molecule);
			molecule.setId(fragmentFragment.getAttributeValue("id"));
	  		copyParentIdToMolecule(fragmentFragment, molecule);
			fragmentFragment.detach();
		}
	}

	private void addLabelToMolecule(CDXObject fragmentFragment,
			CMLMolecule molecule) {
		CDXText cdxText = getNodeTextGrandChild(fragmentFragment);
		cdxText.addLabelToCMLElement(molecule);
	}

	private CDXText getNodeTextGrandChild(CDXObject fragmentFragment) {
		CDXText cdxText = (CDXText) fragmentFragment.query("./n/t").get(0);
		return cdxText;
	}

	private void processSubFragment(CDXObject fragmentFragment, CMLMolecule molecule) {
		CDXFragment subFragment = (CDXFragment) fragmentFragment.query("./n/fragment").get(0);
		subFragment.process2CML(molecule);
		subFragment.copyAttributesTo(molecule);
	}

	/**
	 * @param cmlElement
	 * @throws RuntimeException
	 */
	void processFragmentsContainingUnspecfiedNodes(CMLElement cmlElement) throws RuntimeException {
			//        <fragment BoundingBox="167.6457 96.5499 182.6457 105.5999" id="90">
			//        <n Z="164" p="169.7551 100.0749" NodeType="Unspecified" AS="N" id="91">
			//          <t temp_Text="[[0 3 1 9 3]]171" LineHeight="1" LabelAlignment="Left" p="167.6457 103.5999" BoundingBox="167.6457 96.5499 182.6457 105.5999" Warning="ChemDraw can't interpret this label." LabelLineHeight="1" LabelJustification="Left" id="0"/>
			//        </n>
			//      </fragment>
	    	Nodes labelFragments = this.query("./fragment[count(*)=1 and" +
	    			" count(n) = 1 and" +
	    			" n[@NodeType='Unspecified' and" +
	    			" count(*)=1 and" +
	    			" count(t)=1]]");
	    	for (int i = 0; i < labelFragments.size(); i++) {
	    		CDXObject labelFragment = (CDXObject) labelFragments.get(i);
	    		CDXText cdxText = getNodeTextGrandChild(labelFragment);
				cdxText.addLabelToCMLElement(cmlElement);
	    		labelFragment.detach();
	    	}
		}

	/**
	 * @param element
	 * @throws RuntimeException
	 */
	void processGraphics(CMLElement element) throws RuntimeException {
		Nodes nodes = this.query("./graphic");
		for (int i = 0; i < nodes.size(); i++) {
			((CDXGraphic)nodes.get(i)).process2CML(element);
			nodes.get(i).detach();
		}
	}

	void processTexts(CMLElement element) {
		Nodes nodes = this.query("./t");
		for (int i = 0; i < nodes.size(); i++) {
			CDXText text = (CDXText) nodes.get(i);
			// don't bother with the Chemdraw warning
			if (text.query("*").size() == 0) {
//			if (child.query("*").size() == 0 &&
//					child.query("@Warning[starts-with(.,'ChemDraw can')]").size()==1) {
//    				child.query("@Warning[.='ChemDraw can't interpret this label."+"'").size()==1) {
			// probably a caption
//      <t Z="103" temp_Text="[[0 3 1 6 3]]Racemic" LineHeight="1" 
//        p="152.5 89" BoundingBox="152.5 84.0049 178.9734 90.5" 
//        Warning="ChemDraw can't interpret this label." id="84"/>
				text.addLabelToCMLElement(element);
			} else {
				text.process2CML(element);
				LOG.warn("text child of page is unexpected");
			}
			text.detach();
		}
	}

	/**
	 * @param element
	 * @throws RuntimeException
	 */
	void processReactions(CMLElement element) {
		Nodes nodes = this.query("./step");
		for (int i = 0; i < nodes.size(); i++) {
			CDXObject child = (CDXObject) nodes.get(i);
			CMLReaction reaction = ((CDXReactionStep)child).convertToCMLReaction();
			child.detach();
			element.appendChild(reaction);
    	}
	}


	/**
	 * @param element
	 * @throws RuntimeException
	 */
	void processLeftOvers(CMLElement element) throws RuntimeException {
		Elements childElements = this.getChildElements();
		boolean curveErrors = false;
		boolean bracketErrors = false;
    	for (int i = 0; i < childElements.size(); i++) {
    		if (!(childElements.get(i) instanceof CDXObject))  {
    			LOG.error("Cannot process: "+childElements.get(i));
    			continue;
    		}
    		CDXObject child = (CDXObject) childElements.get(i);
    		if (false) {
    		} else if (child instanceof CDXBracketedGroup) {
    			if (!bracketErrors) {
    				LOG.error("Cannot yet deal with bracketed group");
    			}
    			bracketErrors = true;
    		} else if (child instanceof CDXCurve) {
    			if (!curveErrors) {
    				LOG.error("Cannot yet deal with curve");
    			}
    			curveErrors = true;
    		} else if (child instanceof CDXFragment) {
    			LOG.error("******** UNPROCESSED FRAGMENT **********");
    			((CDXFragment)child).process2CML(element);
    		} else if (child instanceof CDXReactionScheme) {
    			((CDXReactionScheme)child).process2CML(element);
    		} else if (child instanceof CDXArrow) {
    			((CDXArrow)child).process2CML(element);
    		} else {
    			LOG.error("******************Unknown or unexpected child of page: "+child.getLocalName());
    		}
    	}
	}
};
