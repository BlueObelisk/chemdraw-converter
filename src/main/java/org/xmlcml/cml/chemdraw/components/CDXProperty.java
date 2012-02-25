package org.xmlcml.cml.chemdraw.components;

import java.util.Hashtable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**
* CDXProperties seem to have at least 3 names:
* fullName (Node_Table)
* CDXML name (NodeTable)
* alternative CDXML name (NodeType)
* although the last two may simply be differences in documentation
*
* there is therefore no guarantee that this all works
*
* a property also contains a CDXDataType
*
* it may also have an extended data type (CDXComplexType for things like tables, etc.) which have
* enumerated properties. We are gradually including these, but we aren't comprehensive
*
* object ID is treated specially (see CDXObject and the parser)
*
*/
public class CDXProperty implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXProperty.class);
    static {
    	LOG.setLevel(Level.INFO);
    }

	protected static Hashtable<String, CDXProperty> propTable = null;
	protected static Hashtable<String, CDXProperty> cdxTable = new Hashtable<String, CDXProperty>();

    static void makeProperty(int code, String fullName, String cdxName,
    String dataTypeS) {
        CDXProperty prop = new CDXProperty(code, fullName, cdxName, dataTypeS);
        createAndIndex(prop);
    }

    static void makeProperty(int code, String fullName, String cdxName,
    String dataTypeS, IntValue[] enumeration, String alias) {
        CDXProperty prop = new CDXProperty(code, fullName, cdxName, dataTypeS);
        prop.setEnumeration(enumeration);
        if (alias != null && !alias.equals("")) {
            prop.setAlias(alias);
        }
        createAndIndex(prop);
    }

    static void makeProperty(int code, String fullName, String cdxName,
    String dataTypeS, Double scale) {
        CDXProperty prop = new CDXProperty(code, fullName, cdxName, dataTypeS);
        prop.setScale(scale);
        createAndIndex(prop);
    }

    static void makeProperty(int code, String fullName, String cdxName,
    String dataTypeS, IntValue[] enumeration) {
        makeProperty(code, fullName, cdxName, dataTypeS, enumeration, "");
    }

    static void createAndIndex(CDXProperty prop) {
        propTable.put(""+prop.code, prop);
        if (cdxTable == null) {
            cdxTable = new Hashtable<String, CDXProperty>();
        }
        cdxTable.put(prop.getCDXName(), prop);
    }

    // members
	protected int code;
	private String fullName;
	private String cdxName;
	private String alias;
	protected String dataTypeS;
    protected IntValue[] enumeration;
    protected Double scale = null;

	protected byte[] bytes;

    protected CDXDataType dataType;

    /**
     * 
     *
     */
    public CDXProperty() {
    }

    /**
     * 
     * @param prop
     */
    public CDXProperty(CDXProperty prop) {
        this(prop.code, prop.fullName, prop.cdxName, prop.dataTypeS);
        this.enumeration = prop.enumeration;
        this.scale = prop.scale;
        this.alias = prop.alias;
    }

    /**
     * 
     * @param code
     * @param name
     * @param cdxName
     * @param dataTypeS
     */
	public CDXProperty (int code, String name, String cdxName, String dataTypeS) {
        init(code, name, cdxName, dataTypeS);
    }

	private void init(int code, String name, String cdxName, String dataTypeS) {
		this.code = code;
		this.fullName = name;
		this.cdxName = cdxName;
		this.dataTypeS = dataTypeS;
	}

	private static CDXProperty getProperty(String propS) {
		return (CDXProperty) propTable.get(propS);
	}

	private static CDXProperty getPropertyByCDXName(String cdxName) {
		return (CDXProperty) cdxTable.get(cdxName);
	}

	static CDXProperty createProperty(String propS) {
        CDXProperty prop = getProperty(propS);
        return (prop == null) ? null: new CDXProperty(prop);
    }

	static CDXProperty createPropertyByCDXName(String cdxName) {
        CDXProperty prop = getPropertyByCDXName(cdxName);
        return new CDXProperty(prop);
	}

	String getDataTypeString() {
        return (dataType == null) ? null : dataType.toString();
    }

    // use scale or enum to change values in object
	void substituteValues() {
        if (cdxName.equals("DoublePosition")) {
        }

        if (enumeration != null) {
            if (dataType.num == null || !(dataType.num instanceof Integer)) {
                LOG.error("Expected integer value");
            } else {
                int idx = ((Integer) dataType.num).intValue();
                String newValue = null;
                for (int i = 0; i < enumeration.length; i++) {
                    if (enumeration[i].ii == idx) {
                        newValue = enumeration[i].s;
                        break;
                    }
                }
                if (newValue == null) {
                    LOG.error("Cannot find enum: "+idx+" for "+cdxName);
                } else {
                    dataType.s = newValue;
                    LOG.debug("enum: "+newValue);
                }
            }
        } else if (scale != null) {
            double ss = scale.doubleValue();
            if (dataType.num == null) {
                LOG.error("Expected number for scale");
            } else if (dataType.num instanceof Integer) {
                Integer ii = ((Integer) dataType.num);
                int is = ii.intValue();
                dataType.num = new Integer((int)((double)is * ss));
                dataType.s = ""+ii;
            } else if (dataType.num instanceof Long) {
                Long ll = ((Long) dataType.num);
                long ls = ll.longValue();
                dataType.num = new Long((long)((double)ls * ss));
                dataType.s = ""+ll;
            } else if (dataType.num instanceof Double) {
                Double dd = ((Double) dataType.num);
                double dx = dd.doubleValue();
                dataType.num = new Double(dx * ss);
                dataType.s = ""+dx;
            }
        } else {
        }
    }

	public void processAlias() {
        if (this.alias != null) {
            this.cdxName = this.alias;
        }
    }

	String setBytes(byte[] bytes) {
		if (false) {
            ;
		} else if (dataTypeS.equals("CDXBoolean")) {
            dataType = new _CDXBoolean(bytes, this);
		} else if (dataTypeS.equals("CDXBooleanImplied")) {
            dataType = new _CDXBooleanImplied(bytes, this);
		} else if (dataTypeS.equals("CDXCurvePoints")) {
            dataType = new _CDXCurvePoints(bytes, this);
		} else if (dataTypeS.equals("CDXColorTable")) {
            dataType = new _CDXColorTable(bytes, this);
		} else if (dataTypeS.equals("CDXCoordinate")) {
            dataType = new _CDXCoordinate(bytes, this);
		} else if (dataTypeS.equals("CDXDate")) {
            dataType = new _CDXDate(bytes, this);
		} else if (dataTypeS.equals("CDXElementList")) {
            dataType = new _CDXElementList(bytes, this);
		} else if (dataTypeS.equals("CDXFontStyle")) {
            dataType = new _CDXFontStyle(bytes, this);
		} else if (dataTypeS.equals("CDXFontTable")) {
            dataType = new _CDXFontTable(bytes, this);
		} else if (dataTypeS.equals("CDXObjectID")) {
            dataType = new _CDXObjectID(bytes, this);
		} else if (dataTypeS.equals("CDXObjectIDArray")) {
            dataType = new _CDXObjectIDArray(bytes, this);
		} else if (dataTypeS.equals("CDXObjectIDArrayWithCounts")) {
            dataType = new _CDXObjectIDArrayWithCounts(bytes, this);
		} else if (dataTypeS.equals("CDXPoint2D")) {
            dataType = new _CDXPoint2D(bytes, this);
		} else if (dataTypeS.equals("CDXPoint3D")) {
            dataType = new _CDXPoint3D(bytes, this);
		} else if (dataTypeS.equals("CDXRectangle")) {
            dataType = new _CDXRectangle(bytes, this);
		} else if (dataTypeS.equals("CDXRepresentsProperty")) {
            dataType = new _CDXRepresentsProperty(bytes, this);
		} else if (dataTypeS.equals("CDXString")) {
            dataType = new _CDXString(bytes, this);
		} else if (dataTypeS.equals("FLOAT64")) {
            dataType = new _FLOAT64(bytes, this);
		} else if (dataTypeS.equals("INT8")) {
            dataType = new _INT8(bytes, this);
		} else if (dataTypeS.equals("INT16")) {
            dataType = new _INT16(bytes, this);
		} else if (dataTypeS.equals("INT16ListWithCounts")) {
            dataType = new _INT16ListWithCounts(bytes, this);
		} else if (dataTypeS.equals("INT32")) {
            dataType = new _INT32(bytes, this);
		} else if (dataTypeS.equals("UINT8")) {
            dataType = new _UINT8(bytes, this);
		} else if (dataTypeS.equals("UINT16")) {
            dataType = new _UINT16(bytes, this);
		} else if (dataTypeS.equals("UINT32")) {
            dataType = new _UINT32(bytes, this);
		} else if (dataTypeS.equals("Unformatted")) {
            dataType = new _Unformatted(bytes, this);
		} else {
			throw new RuntimeException("Unknown data type: "+dataTypeS);
		}

		String ss = dataType.toString();
		LOG.debug(">"+ss);
		return ss;
	}

	public String getCDXName() {
		String nn = (cdxName.equals("not used")) ? fullName :cdxName;
		return nn;
	}

//	private String getFullName() {
//		return "FN "+fullName;
//	}

// use with care; only for generic properties
	void setNames(String name) {
		cdxName = name;
        fullName = name;
	}

//	private CDXDataType getDataType() {
//        return dataType;
//    }
//
//	private void setDataType(CDXDataType dataType) {
//        this.dataType = dataType;
//    }

	void setValue(String s) {
		if (dataTypeS.equals("CDXString")) {
            setBytes(s.getBytes());
		} else if (dataTypeS.equals("Unformatted")) {
            setBytes(s.getBytes());
        } else {
            throw new RuntimeException("Cannot set string value for property with dataType: "+dataTypeS);
        }
    }

	void setValue(int i) {
        byte[] bb;
        if (dataTypeS.equals("UINT8")) {
            bb = new byte[1];
            bb[0] = CDXUtil.setUINT8(i);
        } else if (dataTypeS.equals("UINT16")) {
            bb = CDXUtil.setUINT16(i);
        } else {
            throw new RuntimeException("Cannot set int value for property with dataType: "+dataTypeS);
        }
        setBytes(bb);
    }

	void setValue(long l) {
        byte[] bb;
        if (dataTypeS.equals("UINT32")) {
            bb = CDXUtil.setUINT32(l);
        } else if (dataTypeS.equals("CDXObjectID")) {
            bb = CDXUtil.setUINT32(l);
        } else {
            throw new RuntimeException("Cannot set long value for property with dataType: "+dataTypeS);
        }
        setBytes(bb);
    }

	void setValue(double d) {
        byte[] bb;
        if (dataTypeS.equals("FLOAT64")) {
            bb = CDXUtil.setFLOAT64(d);
        } else {
            throw new RuntimeException("Cannot set double value for property with dataType: "+dataTypeS);
        }
        setBytes(bb);
    }

	private void setScale(Double scale) {
        this.scale = scale;
    }

	private void setAlias(String alias) {
        this.alias = alias;
    }

    private void setEnumeration( IntValue[] e) {
        this.enumeration = new IntValue[e.length];
        for (int i = 0; i < e.length; i++) {
            enumeration[i] = new IntValue(e[i]);
        }
    }

    // fix this?
    /**
     * @return s
     */
	public String toString() {
		return getCDXName();
	}


	static void makeProperties() {
        if (propTable == null) {
            propTable = new Hashtable<String, CDXProperty>();
            makeProperties0();
        }
    }

	private static void makeProperties0() {
// 0x0000 Marks end of object.
        makeProperty(0x0000, "EndObject", "EndObject", "CDXString");
        makeProperty(0x0001, "CreationUserName", "CreationUserName", "CDXString");
        makeProperty(0x0002, "CreationDate", "CreationDate", "CDXDate");
        makeProperty(0x0003, "CreationProgram", "CreationProgram", "CDXString");
        makeProperty(0x0004, "ModificationUserName", "ModificationUserName", "CDXString");
        makeProperty(0x0005, "ModificationDate", "ModificationDate", "CDXDate");
        makeProperty(0x0006, "ModificationProgram", "ModificationProgram", "CDXString");
// FIXME // 0x0007 Table of contents. (obsolete) 
//        makeProperty(0x0007, "Unused1", "Unused1", "");
        makeProperty(0x0008, "Name", "Name", "CDXString");
        makeProperty(0x0009, "Comment", "Comment", "CDXString");
        makeProperty(0x000A, "ZOrder", "Z", "INT16");
        makeProperty(0x000B, "RegistryNumber", "RegistryNumber", "CDXString");
        makeProperty(0x000C, "RegistryAuthority", "RegistryAuthority", "CDXString");
// FIXME // 0x000D Indicates that this object (the reference object) is an alias to an object elsewhere in the document (the target object). The attributes and contained objects should be taken from the target object. (obsolete)
//        makeProperty(0x000D, "Unused2", "Unused2", "");
        makeProperty(0x000E, "RepresentsProperty", "RepresentsProperty", "CDXRepresentsProperty");
        makeProperty(0x000F, "IgnoreWarnings", "IgnoreWarnings", "CDXBooleanImplied");
        makeProperty(0x0010, "ChemicalWarning", "Warning", "CDXString");
        makeProperty(0x0011, "Visible", "Visible", "CDXBoolean");
        makeProperty(0x0012, "SupersededBy", "SupersededBy", "CDXString"); // shuold be ObjectID
        makeProperty(0x0013, "Unknown13", "Unknopwn13", "CDXString");
        makeProperty(0x0100, "FontTable", "fonttable", "CDXFontTable");

    	// General properties.
/*        
    	kCDXProp_CreationUserName,				// 0x0001 The name of the creator (program user's name) of the document. (CDXString)
    	kCDXProp_CreationDate,					// 0x0002 The time of object creation. (CDXDate)
    	kCDXProp_CreationProgram,				// 0x0003 The name of the program, including version and platform, that created the associated CDX object. ChemDraw 4.0 uses ChemDraw 4.0 as the value of CreationProgram. (CDXString)
    	kCDXProp_ModificationUserName,			// 0x0004 The name of the last modifier (program user's name) of the document. (CDXString)
    	kCDXProp_ModificationDate,				// 0x0005 Time of the last modification. (CDXDate)
    	kCDXProp_ModificationProgram,			// 0x0006 The name of the program, including version and platform, of the last program to perform a modification. ChemDraw 4.0 uses ChemDraw 4.0 as the value of CreationProgram. (CDXString)
    	kCDXProp_Unused1,						// 0x0007 Table of contents. (obsolete)
    	kCDXProp_Name,							// 0x0008 Name of an object. (CDXString)
    	kCDXProp_Comment,						// 0x0009 An arbitrary string intended to be meaningful to a user. (CDXString)
    	kCDXProp_ZOrder,						// 0x000A Back-to-front ordering index in 2D drawing. (INT16)
    	kCDXProp_RegistryNumber,				// 0x000B A registry or catalog number of a molecule object. (CDXString)
    	kCDXProp_RegistryAuthority,				// 0x000C A string that specifies the authority which issued a registry or catalog number. Some examples of registry authorities are CAS, Beilstein, Aldrich, and Merck. (CDXString)
    	kCDXProp_Unused2,						// 0x000D Indicates that this object (the reference object) is an alias to an object elsewhere in the document (the target object). The attributes and contained objects should be taken from the target object. (obsolete)
    	kCDXProp_RepresentsProperty,			// 0x000E Indicates that this object represents some property in some other object. (CDXRepresentsProperty)
    	kCDXProp_IgnoreWarnings,				// 0x000F Signifies whether chemical warnings should be suppressed on this object. (CDXBooleanImplied)
    	kCDXProp_ChemicalWarning,				// 0x0010 A warning concerning possible chemical problems with this object. (CDXString)
    	kCDXProp_Visible,						// 0x0011 The object is visible if non-zero. (CDXBoolean)
*/
        
/*--
2DPosition Property
CDXML Name: p
CDX Constant Name: kCDXProp_2DPosition
CDX Constant Value: 0x0200
Data Size: CDXPoint2D
Property of objects: kCDXObj_Text, kCDXObj_Node, kCDXObj_Splitter

First written/read in: ChemDraw 4.0
Required? Until ChemDraw 6.0

Description:


The 2D location (in the order of vertical and horizontal locations) of an object.

The precise meaning of this attribute varies depending on the type of object. For instance, it is the center of the characters representing the element for Element nodes. On the other hand it is the left-most text baseline position for left-aligned caption objects.

If this property is absent:


For Text objects, ChemDraw will try to calculate a position based on the kCDXProp_BoundingBox, if present. Otherwise, if the text object is an atom label, ChemDraw will try to calculate a position based on the associated Node object. Otherwise, if the text is a subobject of a Named Alternative Group, its location will be calculated based on the position of that object. Failing all of those, the text will be placed at (0, 0), which likely will not be desired.

For Node objects, the absence of this property indicates that the creation program might have known the chemical properties of the file (how the atoms and bonds are connected, for example), but was not able to select a specific display style. In such a case, ChemDraw will automatically run its Clean Up Structure command on the relevent node(s) to generate reasonable position(s). If some nodes do have this property and some do not, only the ones without it will be repositioned, and their new positions will take into account the positions of the other nodes. This would be a very reasonable approach, for example, if a program that normally handled only 3D data wanted to create a 2D CDX/CDXML file: it could simply omit all of the Node positions, and let ChemDraw come up with something reasonable.

If positions are omitted from asymmetric nodes, or from nodes connected to asymmetric bonds, the appropriate kCDXProp_Atom_Geometry, kCDXProp_Atom_BondOrdering, and kCDXProp_Bond_BondOrdering properties should be present.

--*/
/*        
        	// Fonts.
        	kCDXProp_FontTable = 0x0100,			// 0x0100 A list of fonts used in the document. (CDXFontTable)

        	// Coordinates.
        	kCDXProp_2DPosition = 0x0200,			// 0x0200 The 2D location (in the order of vertical and horizontal locations) of an object. (CDXPoint2D)
        	kCDXProp_3DPosition,					// 0x0201 The 3D location (in the order of X-, Y-, and Z-locations in right-handed coordinate system) of an object in CDX coordinate units. The precise meaning of this attribute varies depending on the type of object. (CDXPoint3D)
        	kCDXProp_2DExtent,						// 0x0202 The width and height of an object in CDX coordinate units. The precise meaning of this attribute varies depending on the type of object. (CDXPoint2D)
        	kCDXProp_3DExtent,						// 0x0203 The width, height, and depth of an object in CDX coordinate units (right-handed coordinate system). The precise meaning of this attribute varies depending on the type of object. (CDXPoint3D)
        	kCDXProp_BoundingBox,					// 0x0204 The smallest rectangle that encloses the graphical representation of the object. (CDXRectangle)
        	kCDXProp_RotationAngle,					// 0x0205 The angular orientation of an object in degrees * 65536. (INT32)
        	kCDXProp_BoundsInParent,				// 0x0206 The bounds of this object in the coordinate system of its parent (used for pages within tables). (CDXRectangle)
        	kCDXProp_3DHead,						// 0x0207 The 3D location (in the order of X-, Y-, and Z-locations in right-handed coordinate system) of the head of an object in CDX coordinate units. The precise meaning of this attribute varies depending on the type of object. (CDXPoint3D)
        	kCDXProp_3DTail,						// 0x0208 The 3D location (in the order of X-, Y-, and Z-locations in right-handed coordinate system) of the tail of an object in CDX coordinate units. The precise meaning of this attribute varies depending on the type of object. (CDXPoint3D)
        	kCDXProp_TopLeft,						// 0x0209 The location of the top-left corner of a quadrilateral object, possibly in a rotated or skewed frame. (CDXPoint2D)
        	kCDXProp_TopRight,						// 0x020A The location of the top-right corner of a quadrilateral object, possibly in a rotated or skewed frame. (CDXPoint2D)
        	kCDXProp_BottomRight,					// 0x020B The location of the bottom-right corner of a quadrilateral object, possibly in a rotated or skewed frame. (CDXPoint2D)
        	kCDXProp_BottomLeft,					// 0x020C The location of the bottom-left corner of a quadrilateral object, possibly in a rotated or skewed frame. (CDXPoint2D)
*/
        makeProperty(0x0200, "2DPosition", "p", "CDXPoint2D");
        makeProperty(0x0201, "3DPosition", "xyz", "CDXPoint3D");
//The width and height of an object in CDX coordinate units. The precise meaning of this attribute varies depending on the type of object.

        makeProperty(0x0202, "2DExtent", "extent", "CDXPoint2D");
        makeProperty(0x0203, "3DExtent", "extent3D", "CDXPoint3D");
/*--
The smallest rectangle that encloses the graphical representation of the object.

If this property is absent:


If possible, a reasonable bounding box will be guessed. For example, if a bounding box is omitted for an atom label (in ChemDraw 6.0 and later), the label will be positioned based on the location of its atom. Some objects (such as atoms) are defined fully by their 2D Position, and no bounding box is necessary. Note that this propery is required for pictures and spectra, since no guess can be made about the size of those objects.
--*/
        makeProperty(0x0204, "BoundingBox", "BoundingBox", "CDXRectangle");
//The angular orientation of an object in degrees * 65536.

        makeProperty(0x0205, "RotationAngle", "RotationAngle", "INT32", new Double(1./65536.));

// kCDXProp_BoundsInParent,	// 0x0206 The bounds of this object in the coordinate system of its parent (used for pages within tables). (CDXRectangle)
        makeProperty(0x0206, "BoundsInParent", "BoundsInParent", "CDXRectangle");
        makeProperty(0x0207, "3DHead", "Head3D", "CDXPoint3D");
        makeProperty(0x0208, "3DTail", "Tail3D", "CDXPoint3D");
        makeProperty(0x0209, "TopLeft", "TopLeft", "CDXPoint2D");
        makeProperty(0x020A, "TopRight", "TopRight", "CDXPoint2D");
        makeProperty(0x020B, "BottomRight", "BottomRight", "CDXPoint2D");
        makeProperty(0x020C, "BottomLeft", "BottomLeft", "CDXPoint2D");
        makeProperty(0x020D, "3DCenter", "Center3D", "CDXPoint3D");
        makeProperty(0x020E, "3DMajorAxisEnd", "MajorAxisEnd3D", "CDXPoint3D");
        makeProperty(0x020F, "3DMinorAxisEnd", "MinorAxisEnd3D", "CDXPoint3D");
/*
	// Colors.
	kCDXProp_ColorTable = 0x0300,			// 0x0300 The color palette used throughout the document. (CDXColorTable)
	kCDXProp_ForegroundColor,				// 0x0301 The foreground color of an object represented as the two-based index into the object's color table. (UINT16)
	kCDXProp_BackgroundColor,				// 0x0302 The background color of an object represented as the two-based index into the object's color table. (INT16)
        	
 */        
        /*--
The color palette used throughout the document. Color indexes 0 and 1 always correspond to black and white and are not saved in the color table. The first and second RGB values (color indexes 2 and 3) are the default background and foreground colors, and other colors are numbered sequentially.

If no color table is present in the object or any of it's ancestors, only the standard color indexes 0 through 3 may be used. In that case, the background color (2) is assumed to be white, and the foreground color (3) is assumed to be black.
--*/
        makeProperty(0x0300, "ColorTable", "colortable", "CDXColorTable");
/*--
The foreground color of an object represented as the two-based index into the object's color table.

A value indicating the 2-based index of a color in the color table (a value of 2 indicates the first item, a value of 3 indicates the second item, etc.).


If this property is absent:


A value of 3 is assumed, that being defined as the default foreground color in the color table.

--*/
        makeProperty(0x0301, "ForegroundColor", "color", "UINT16"/*, 3*/);
        makeProperty(0x0302, "BackgroundColor", "bgcolor", "INT16"/*, 2*/);

/*
	// Atom properties.
	kCDXProp_Node_Type = 0x0400,			// 0x0400 The type of a node object. (INT16)
	kCDXProp_Node_LabelDisplay,				// 0x0401 The characteristics of node label display. (INT8)
	kCDXProp_Node_Element,					// 0x0402 The atomic number of the atom representing this node. (INT16)
	kCDXProp_Atom_ElementList,				// 0x0403 A list of atomic numbers. (CDXElementList)
	kCDXProp_Atom_Formula,					// 0x0404 The composition of a node representing a fragment whose composition is known, but whose connectivity is not. For example, C<sub>4</sub>H<sub>9</sub> represents a mixture of the 4 butyl isomers. (CDXFormula)
	
 */        
        IntValue[] NODE_TABLE = new IntValue[]    {
               new IntValue(0, "Unspecified"),
               new IntValue(1, "Element"),
               new IntValue(2, "ElementList"),
               new IntValue(3, "ElementListNickname"),
               new IntValue(4, "Nickname"),
               new IntValue(5, "Fragment"),
               new IntValue(6, "Formula"),
               new IntValue(7, "GenericNickname"),
               new IntValue(8, "AnonymousAlternativeGroup"),
               new IntValue(9, "NamedAlternativeGroup"),
               new IntValue(10, "MultiAttachment"),
               new IntValue(11, "VariableAttachment"),
               new IntValue(12, "ExternalConnectionPoint"),
               new IntValue(13, "LinkNode")
           };
        makeProperty(0x0400, "Node_Table", "NodeTable", "INT16", NODE_TABLE, "NodeType");

        IntValue[] LABEL_TABLE = new IntValue[] {
            new IntValue(0, "Auto"),
            new IntValue(1, "Left"),
            new IntValue(2, "Center"),
            new IntValue(3, "Right"),
            new IntValue(4, "Above"),
            new IntValue(5, "Below"),
        };
        makeProperty(0x0401, "Node_LabelDisplay", "LabelDisplay", "INT8", LABEL_TABLE);
        makeProperty(0x0402, "Node_Element", "Element", "INT16");
        makeProperty(0x0403, "Atom_ElementList", "ElementList", "CDXElementList");
        makeProperty(0x0404, "Atom_Formula", "Formula", "CDXFormula");

/*
	kCDXProp_Atom_Isotope = 0x0420,			// 0x0420 The absolute isotopic mass of an atom (2 for deuterium, 14 for carbon-14). (INT16)
	kCDXProp_Atom_Charge,					// 0x0421 The atomic charge of an atom. (INT8)
	kCDXProp_Atom_Radical,					// 0x0422 The atomic radical attribute of an atom. (UINT8)
	kCDXProp_Atom_RestrictFreeSites,		// 0x0423 Indicates that up to the specified number of additional substituents are permitted on this atom. (UINT8)
	kCDXProp_Atom_RestrictImplicitHydrogens,// 0x0424 Signifies that implicit hydrogens are not allowed on this atom. (CDXBooleanImplied)
	kCDXProp_Atom_RestrictRingBondCount,	// 0x0425 The number of ring bonds attached to an atom. (INT8)
	kCDXProp_Atom_RestrictUnsaturatedBonds,	// 0x0426 Indicates whether unsaturation should be present or absent. (INT8)
	kCDXProp_Atom_RestrictRxnChange,		// 0x0427 If present, signifies that the reaction change of an atom must be as specified. (CDXBooleanImplied)
	kCDXProp_Atom_RestrictRxnStereo,		// 0x0428 The change of stereochemistry of an atom during a reaction. (INT8)
	kCDXProp_Atom_AbnormalValence,			// 0x0429 Signifies that an abnormal valence for an atom is permitted. (CDXBooleanImplied)
	kCDXProp_Unused3,						// 0x042A 
	kCDXProp_Atom_NumHydrogens,				// 0x042B The number of (explicit) hydrogens in a labeled atom consisting of one heavy atom and (optionally) the symbol H (e.g., CH<sub>3</sub>). (UINT16)
	kCDXProp_Unused4,						// 0x042C 
	kCDXProp_Unused5,						// 0x042D 
	kCDXProp_Atom_HDot,						// 0x042E Signifies the presence of an implicit hydrogen with stereochemistry specified equivalent to an explicit H atom with a wedged bond. (CDXBooleanImplied)
	kCDXProp_Atom_HDash,					// 0x042F Signifies the presence of an implicit hydrogen with stereochemistry specified equivalent to an explicit H atom with a hashed bond. (CDXBooleanImplied)
 */        
        makeProperty(0x0420, "Atom_Isotope", "Isotope", "INT16");
        makeProperty(0x0421, "Atom_Charge", "Charge", "INT8");
        makeProperty(0x0422, "Atom_Radical", "Radical", "UINT8",
            new IntValue[] {
                new IntValue(0, "None"),
                new IntValue(1, "Singlet"),
                new IntValue(2, "Doublet"),
                new IntValue(3, "Triplet"),
            }
            );
        makeProperty(0x0423, "Atom_RestrictFreeSites", "FreeSites", "UINT8");
        makeProperty(0x0424, "Atom_RestrictImplicitHydrogens", "ImplicitHydrogens", "CDXBooleanImplied");
        makeProperty(0x0425, "Atom_RestrictRingBondCount", "RingBondCount", "INT8",
            new IntValue[] {
                new IntValue(-1, "Unspecified"),
                new IntValue(0, "NoRingBonds"),
                new IntValue(1, "AsDrawn"),
                new IntValue(2, "SimpleRing"),
                new IntValue(3, "Fusion"),
                new IntValue(3, "Fusion"),
            }
            );
        makeProperty(0x0426, "Atom_RestrictUnsaturatedBonds", "UnsaturatedBonds", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "MustBeAbsent"),
                new IntValue(2, "MustBePresent"),
            }
        );
        makeProperty(0x0427, "Atom_RestrictRxnChange", "RxnChange", "CDXBooleanImplied");
        makeProperty(0x0428, "Atom_RestrictRxnStereo", "RxnStereo", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "Inversion"),
                new IntValue(2, "Retention"),
            }
        );
        makeProperty(0x0429, "Atom_AbnormalValence", "AbnormalValence", "CDXBooleanImplied");
//        makeProperty(0x042A, "Unused3", "Unused3", "");
        makeProperty(0x042B, "Atom_NumHydrogens", "NumHydrogens", "UINT16");
//      makeProperty(0x042C, "Unused5", "Unused4", "");
//      makeProperty(0x042D, "Unused5", "Unused5", "");
        makeProperty(0x042E, "Atom_HDot", "HDot", "CDXBooleanImplied");
        makeProperty(0x042F, "Atom_HDash", "HDash", "CDXBooleanImplied");
        /*

    	
    	kCDXProp_Atom_Geometry,					// 0x0430 The geometry of the bonds about this atom. (INT8)
    	kCDXProp_Atom_BondOrdering,				// 0x0431 An ordering of the bonds to this node, used for stereocenters, fragments, and named alternative groups with more than one attachment. (CDXObjectIDArray)
    	kCDXProp_Node_Attachments,				// 0x0432 For multicenter attachment nodes or variable attachment nodes, a list of IDs of the nodes which are multiply or variably attached to this node. (CDXObjectIDArrayWithCounts)
    	kCDXProp_Atom_GenericNickname,			// 0x0433 The name of the generic nickname. (CDXString)
    	kCDXProp_Atom_AltGroupID,				// 0x0434 The ID of the alternative group object that describes this node. (CDXObjectID)
    	kCDXProp_Atom_RestrictSubstituentsUpTo,	// 0x0435 Indicates that substitution is restricted to no more than the specified value. (UINT8)
    	kCDXProp_Atom_RestrictSubstituentsExactly,	// 0x0436 Indicates that exactly the specified number of substituents must be present. (UINT8)
    	kCDXProp_Atom_CIPStereochemistry,		// 0x0437 The node's absolute stereochemistry according to the Cahn-Ingold-Prelog system. (INT8)
    	kCDXProp_Atom_Translation,				// 0x0438 Provides for restrictions on whether a given node may match other more- or less-general nodes. (INT8)
    	kCDXProp_Atom_AtomNumber,				// 0x0439 Atom number, as text. (CDXString)
    	kCDXProp_Atom_ShowQuery,				// 0x043A Show the query indicator if non-zero. (CDXBoolean)
    	kCDXProp_Atom_ShowStereo,				// 0x043B Show the stereochemistry indicator if non-zero. (CDXBoolean)
    	kCDXProp_Atom_ShowAtomNumber,			// 0x043C Show the atom number if non-zero. (CDXBoolean)
    	kCDXProp_Atom_LinkCountLow,				// 0x043D Low end of repeat count for link nodes. (INT16)
    	kCDXProp_Atom_LinkCountHigh,			// 0x043E High end of repeat count for link nodes. (INT16)
    	kCDXProp_Atom_IsotopicAbundance,		// 0x043F Isotopic abundance of this atom's isotope. (INT8)
*/    	
        
        makeProperty(0x0430, "Atom_Geometry", "Geometry", "INT8",
            new IntValue[] {
               new IntValue(0, "Unknown"),
               new IntValue(1, "1"),
               new IntValue(2, "Linear"),
               new IntValue(3, "Bent"),
               new IntValue(4, "TrigonalPlanar"),
               new IntValue(5, "TrigonalPyramidal"),
               new IntValue(6, "SquarePlanar"),
               new IntValue(7, "Tetrahedral"),
               new IntValue(8, "TrigonalBipyramidal"),
               new IntValue(9, "SquarePyramidal"),
               new IntValue(10, "5"),
               new IntValue(11, "Octahedral"),
               new IntValue(12, "6"),
               new IntValue(13, "7"),
               new IntValue(14, "8"),
               new IntValue(15, "9"),
               new IntValue(16, "10"),
            }
            );
        makeProperty(0x0431, "Atom_BondOrdering", "BondOrdering", "CDXObjectIDArray");
        makeProperty(0x0432, "Node_Attachments", "Attachments", "CDXObjectIDArrayWithCounts");
        makeProperty(0x0433, "Atom_GenericNickname", "GenericNickname", "CDXString");
        makeProperty(0x0434, "Atom_AltGroupID", "AltGroupID", "CDXObjectID");
        makeProperty(0x0435, "Atom_RestrictSubstituentsUpTo", "SubstituentsUpTo", "UINT8");
        makeProperty(0x0436, "Atom_RestrictSubstituentsExactly", "SubstituentsExactly", "UINT8");
        makeProperty(0x0437, "Atom_CIPStereochemistry", "AS", "INT8",
            new IntValue[] {
                new IntValue(0, "U"),
                new IntValue(1, "N"),
                new IntValue(2, "R"),
                new IntValue(3, "S"),
                new IntValue(4, "r"),
                new IntValue(5, "s"),
                new IntValue(6, "u"),
            }
            );
        makeProperty(0x0438, "Atom_Translation", "Translation", "INT8",
            new IntValue[] {
                new IntValue(0, "Equal"),
                new IntValue(1, "Broad"),
                new IntValue(2, "Narrow"),
                new IntValue(3, "Any"),
            }
        );
        makeProperty(0x0439, "Atom_AtomNumber", "AtomNumber", "CDXString");
        makeProperty(0x043A, "Atom_ShowQuery", "ShowAtomQuery", "CDXBoolean");
        makeProperty(0x043B, "Atom_ShowStereo", "ShowAtomStereo", "CDXBoolean");
        makeProperty(0x043C, "Atom_ShowAtomNumber", "ShowAtomNumber", "CDXBoolean");
        makeProperty(0x043D, "Atom_LinkCountLow", "LinkCountLow", "INT16");
        makeProperty(0x043E, "Atom_LinkCountHigh", "LinkCountHigh", "INT16");
        makeProperty(0x043F, "Atom_IsotopicAbundance", "IsotopicAbundance", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "Any"),
                new IntValue(2, "Natural"),
                new IntValue(3, "Enriched"),
                new IntValue(4, "Deficient"),
            }
        );
/*        
    	kCDXProp_Atom_ExternalConnectionType,	// 0x0440 Type of external connection, for atoms of type kCDXNodeType_ExternalConnectionPoint. (INT8)

*/        

        makeProperty(0x0440, "Atom_ExternalConnectionType", "ExternalConnectionType", "INT8");
        makeProperty(0x0441, "Atom_GenericList", "GenericList", "CDXGenericList");
        makeProperty(0x0442, "Atom_ShowTerminalCarbonLabels", "ShowTerminalCarbonLabels", "CDXBooleanImplied");
//    		Signifies whether terminal carbons (carbons with zero or one bond) should display a text label with the element symbol and appropriate hydrogens.
        makeProperty(0x0443, "Atom_ShowNonTerminalCarbonLabels", "ShowNonTerminalCarbonLabels", "CDXBooleanImplied");
//    		Signifies whether non-terminal carbons (carbons with more than one bond) should display a text label with the element symbol and appropriate hydrogens.
        makeProperty(0x0444, "Atom_HideImplicitHydrogens", "HideImplicitHydrogens", "CDXBooleanImplied");
//    		Signifies whether implicit hydrogens should be displayed on otherwise-atomic atom labels (NH2 versus N).
        makeProperty(0x0445, "Atom_ShowEnhancedStereo", "ShowAtomEnhancedStereo", "CDXBoolean");
//    	Show the enhanced stereochemistry indicator if non-zero.
        makeProperty(0x0446, "Atom_EnhancedStereoType", "EnhancedStereoType", "UINT8");
//    	The type of enhanced stereochemistry present on this atom. This is an enumerated property.
        makeProperty(0x0447, "Atom_EnhancedStereoGroupNum", "EnhancedStereoGroupNum", "UINT16");
//    	The group number associated with Or and And enhanced stereochemistry types.        
        makeProperty(0x0448, "NewProp448", "NewProp448", "Unformatted");
/*
        	// Molecule properties.
        	kCDXProp_Mole_Racemic = 0x0500,			// 0x0500 Indicates that the molecule is a racemic mixture. (CDXBoolean)
        	kCDXProp_Mole_Absolute,					// 0x0501 Indicates that the molecule has known absolute configuration. (CDXBoolean)
        	kCDXProp_Mole_Relative,					// 0x0502 Indicates that the molecule has known relative stereochemistry, but unknown absolute configuration. (CDXBoolean)
        	kCDXProp_Mole_Formula,					// 0x0503 The molecular formula representation of a molecule object. (CDXFormula)
        	kCDXProp_Mole_Weight,					// 0x0504 The average molecular weight of a molecule object. (FLOAT64)
        	kCDXProp_Frag_ConnectionOrder,			// 0x0505 An ordered list of attachment points within a fragment. (CDXObjectIDArray)

 */        
    	makeProperty(0x0500, "Mole_Racemic", "Racemic", "CDXBoolean");
        makeProperty(0x0501, "Mole_Absolute", "Absolute", "CDXBoolean");
        makeProperty(0x0502, "Mole_Relative", "Relative", "CDXBoolean");
        makeProperty(0x0503, "Mole_Formula", "Formula", "CDXFormula");
        makeProperty(0x0504, "Mole_Weight", "Weight", "FLOAT64");
        makeProperty(0x0505, "Frag_ConnectionOrder", "ConnectionOrder", "CDXObjectIDArray");
/*
        	// Bond properties.
        	kCDXProp_Bond_Order = 0x0600,			// 0x0600 The order of a bond object. (INT16)
        	kCDXProp_Bond_Display,					// 0x0601 The display type of a bond object. (INT16)
        	kCDXProp_Bond_Display2,					// 0x0602 The display type for the second line of a double bond. (INT16)
        	kCDXProp_Bond_DoublePosition,			// 0x0603 The position of the second line of a double bond. (INT16)
        	kCDXProp_Bond_Begin,					// 0x0604 The ID of the CDX node object at the first end of a bond. (CDXObjectID)
        	kCDXProp_Bond_End,						// 0x0605 The ID of the CDX node object at the second end of a bond. (CDXObjectID)
        	kCDXProp_Bond_RestrictTopology,			// 0x0606 Indicates the desired topology of a bond in a query. (INT8)
        	kCDXProp_Bond_RestrictRxnParticipation,	// 0x0607 Specifies that a bond is affected by a reaction. (INT8)
        	kCDXProp_Bond_BeginAttach,				// 0x0608 Indicates where within the Bond_Begin node a bond is attached. (UINT8)
        	kCDXProp_Bond_EndAttach,				// 0x0609 Indicates where within the Bond_End node a bond is attached. (UINT8)
        	kCDXProp_Bond_CIPStereochemistry,		// 0x060A The bond's absolute stereochemistry according to the Cahn-Ingold-Prelog system. (INT8)
        	kCDXProp_Bond_BondOrdering,				// 0x060B Ordered list of attached bond IDs. (CDXObjectIDArray)
        	kCDXProp_Bond_ShowQuery,				// 0x060C Show the query indicator if non-zero. (CDXBoolean)
        	kCDXProp_Bond_ShowStereo,				// 0x060D Show the stereochemistry indicator if non-zero. (CDXBoolean)
        	kCDXProp_Bond_CrossingBonds,			// 0x060E Unordered list of IDs of bonds that cross this one (either above or below). (CDXObjectIDArray)
        	kCDXProp_Bond_ShowRxn,					// 0x060F Show the reaction-change indicator if non-zero. (CDXBoolean)

 */
        
        makeProperty(0x0600, "Bond_Order", "Order", "INT16",
            new IntValue[] {
                new IntValue(0xFFFF, "Unspecified"),
                new IntValue(0x0001, "1"),
                new IntValue(0x0002, "2"),
                new IntValue(0x0004, "3"),
                new IntValue(0x0008, "4"),
                new IntValue(0x0010, "5"),
                new IntValue(0x0020, "6"),
                new IntValue(0x0040, "0.5"),
                new IntValue(0x0080, "1.5"),
                new IntValue(0x0100, "2.5"),
                new IntValue(0x0200, "3.5"),
                new IntValue(0x0400, "4.5"),
                new IntValue(0x0800, "5.5"),
                new IntValue(0x1000, "dative"),
                new IntValue(0x2000, "ionic"),
                new IntValue(0x4000, "hydrogen"),
                new IntValue(0x8000, "threecenter"),
            }
            );
        makeProperty(0x0601, "Bond_Display", "Display", "INT16",
            new IntValue[] {
                new IntValue(0, "Solid"),
                new IntValue(1, "Dash"),
                new IntValue(2, "Hash"),
                new IntValue(3, "WedgedHashBegin"),
                new IntValue(4, "WedgedHashEnd"),
                new IntValue(5, "Bold"),
                new IntValue(6, "WedgeBegin"),
                new IntValue(7, "WedgeEnd"),
                new IntValue(8, "Wavy"),
                new IntValue(9, "HollowWedgeBegin"),
                new IntValue(10, "HollowWedgeEnd"),
                new IntValue(11, "WavyWedgeBegin"),
                new IntValue(12, "WavyWedgeEnd"),
                new IntValue(13, "Dot"),
                new IntValue(14, "DashDot"),
            }
            );
// error?        makeProperty(0x0602, "Bond_Display2", "Display2", "INT16");
/*--
The display type for the second line of a double bond.

ChemDraw supports only Dashed types for the second line of a double bond (unsupport values are provided for future compatibility).

See also kCDXProp_Bond_Display.

This is an enumerated property. Acceptible values are shown in the following list: Value CDXML Name Description
0 Solid Solid bond
1 Dash Dashed bond
2 Hash Hashed bond
3 WedgedHashBegin Wedged hashed bond with the narrow end on the "begin" atom
4 WedgedHashEnd Wedged hashed bond with the narrow end on the "end" atom
5 Bold Bold bond
6 WedgeBegin Wedged solid bond with the narrow end on the "begin" atom
7 WedgeEnd Wedged solid bond with the narrow end on the "end" atom
8 Wavy Wavy bond
9 HollowWedgeBegin Wedged hollow bond with the narrow end on the "begin" atom
10 HollowWedgeEnd Wedged hollow bond with the narrow end on the "end" atom
11 WavyWedgeBegin Wedged wavy bond with the narrow end on the "begin" atom
12 WavyWedgeEnd Wedged wavy bond with the narrow end on the "end" atom
13 Dot Dotted bond
14 DashDot Dashed-and-dotted bond

--*/
        makeProperty(0x0602, "Bond_Display2", "Display2", "INT8",
            new IntValue[] {
                new IntValue(0, "Solid"),
                new IntValue(1, "Dash"),
                new IntValue(2, "Hash"),
                new IntValue(3, "WedgedHashBegin"),
                new IntValue(4, "WedgedHashEnd"),
                new IntValue(5, "Bold"),
                new IntValue(6, "WedgeBegin"),
                new IntValue(7, "WedgeEnd"),
                new IntValue(8, "Wavy"),
                new IntValue(9, "HollowWedgeBegin"),
                new IntValue(10, "HollowWedgeEnd"),
                new IntValue(11, "WavyWedgeBegin"),
                new IntValue(12, "WavyWedgeEnd"),
                new IntValue(13, "Dot"),
                new IntValue(14, "DashDot"),
            }
            );
/*--
This is an enumerated property. Acceptible values are shown in the following list: Value CDXML Name Description
0 Center Double bond is centered, but was positioned automatically by the program
1 Right Double bond is on the right (viewing from the "begin" atom to the "end" atom), but was positioned automatically by the program
2 Left Double bond is on the left (viewing from the "begin" atom to the "end" atom), but was positioned automatically by the program
256 Center Double bond is centered, and was positioned manually by the user
257 Right Double bond is on the right (viewing from the "begin" atom to the "end" atom), and was positioned manually by the user
258 Left Double bond is on the left (viewing from the "begin" atom to the "end" atom), and was positioned manually by the user
--*/
        makeProperty(0x0603, "Bond_DoublePosition", "DoublePosition", "INT16",
            new IntValue[] {
                new IntValue(0, "Center"),
                new IntValue(1, "Right"),
                new IntValue(2, "Left"),
                new IntValue(256, "Center"),
                new IntValue(257, "Right"),
                new IntValue(258, "Left"),
            }
        );
        makeProperty(0x0604, "Bond_Begin", "B", "CDXObjectID");
        makeProperty(0x0605, "Bond_End", "E", "CDXObjectID");
/*--
Value CDXML Name Description
0 Unspecified Ring/chain status of the bond is unspecified
1 Ring Bond must be in a ring
2 Chain Bond must not be in a ring
3 RingOrChain Bond may be in either a ring or a chain
--*/
        makeProperty(0x0606, "Bond_RestrictTopology", "Topology", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "Ring"),
                new IntValue(2, "Chain"),
                new IntValue(3, "RingOrChain"),
            }
        );
/*--
0 Unspecified Bond involvement in reacting center is not specified
1 ReactionCenter Bond is part of reacting center but not made/broken nor order changed
2 MakeOrBreak Bond is made or broken in reaction
3 ChangeType Bond's order changes in reaction
4 MakeAndChange Bond is made or broken, or its order changes in the reaction
5 NotReactionCenter Bond is not part of reacting center
6 NoChange Bond does not change in course of reaction, but it is part of the reacting center
7 Unmapped The structure was partially mapped, but the reaction involvement of this bond was not determined

--*/
        makeProperty(0x0607, "Bond_RestrictRxnParticipation", "RxnParticipation", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "ReactionCenter"),
                new IntValue(2, "MakeOrBreak"),
                new IntValue(3, "ChangeType"),
                new IntValue(4, "MakeAndChange"),
                new IntValue(5, "NotReactionCenter"),
                new IntValue(6, "NoChange"),
                new IntValue(7, "Unmapped"),
            }
        );
        makeProperty(0x0608, "Bond_BeginAttach", "BeginAttach", "UINT8");
        makeProperty(0x0609, "Bond_EndAttach", "EndAttach", "UINT8");
        makeProperty(0x060A, "Bond_CIPStereochemistry", "BS", "INT8",
            new IntValue[] {
                new IntValue(0, "U"),
                new IntValue(1, "N"),
                new IntValue(2, "E"),
                new IntValue(3, "Z"),
            }
            );
        makeProperty(0x060B, "Bond_BondOrdering", "BondCircularOrdering", "CDXObjectIDArray");
        makeProperty(0x060C, "Bond_ShowQuery", "ShowBondQuery", "CDXBoolean");
        makeProperty(0x060D, "Bond_ShowStereo", "ShowBondStereo", "CDXBoolean");
        makeProperty(0x060E, "Bond_CrossingBonds", "CrossingBonds", "CDXObjectIDArray");
        makeProperty(0x060F, "Bond_ShowRxn", "ShowBondRxn", "CDXBoolean");

/*
        	// Text properties.
        	kCDXProp_Text = 0x0700,					// 0x0700 The text of a text object. (CDXString)
        	kCDXProp_Justification,					// 0x0701 The horizontal justification of a text object. (INT8)
        	kCDXProp_LineHeight,					// 0x0702 The line height of a text object. (UINT16)
        	kCDXProp_WordWrapWidth,					// 0x0703 The word-wrap width of a text object. (INT16)
        	kCDXProp_LineStarts,					// 0x0704 The number of lines of a text object followed by that many values indicating the zero-based text position of each line start. (INT16ListWithCounts)
        	kCDXProp_LabelAlignment,				// 0x0705 The alignment of the text with respect to the node position. (INT8)
        	kCDXProp_LabelLineHeight,				// 0x0706 Text line height for atom labels (INT16)
        	kCDXProp_CaptionLineHeight,				// 0x0707 Text line height for non-atomlabel text objects (INT16)
        	kCDXProp_InterpretChemically,			// 0x0708 Signifies whether to the text label should be interpreted chemically (if possible). (CDXBooleanImplied)

 */
        makeProperty(0x0700, "Text", TEMP_TEXT, "CDXString");
        makeProperty(0x0701, "Justification", "Justification", "INT8",
            new IntValue[] {
                new IntValue(-1, "Right"),
                new IntValue(0, "Left"),
                new IntValue(1, "Center"),
                new IntValue(2, "Full"),
                new IntValue(3, "Above"),
                new IntValue(4, "Below"),
                new IntValue(5, "Auto"),
            }
            );
        makeProperty(0x0702, "LineHeight", "LineHeight", "UINT16");
        makeProperty(0x0703, "WordWrapWidth", "WordWrapWidth", "INT16");
        makeProperty(0x0704, "LineStarts", "LineStarts", "INT16ListWithCounts");
        makeProperty(0x0705, "LabelAlignment", "LabelAlignment", "INT8",
            new IntValue[] {
                new IntValue(0, "Auto"),
                new IntValue(1, "Left"),
                new IntValue(2, "Center"),
                new IntValue(3, "Right"),
                new IntValue(4, "Above"),
                new IntValue(5, "Below"),
                new IntValue(6, "Best"),
            }
            );
        makeProperty(0x0706, "LabelLineHeight", "LabelLineHeight", "INT16");
        makeProperty(0x0707, "CaptionLineHeight", "CaptionLineHeight", "INT16");
        makeProperty(0x0708, "InterpretChemically", "InterpretChemically", "CDXBoolean");
 
/*
        	// Document properties.
        	kCDXProp_MacPrintInfo = 0x0800,			// 0x0800 The 120 byte Macintosh TPrint data associated with the CDX document object. Refer to Macintosh Toolbox manual for detailed description. (Unformatted)
        	kCDXProp_WinPrintInfo,					// 0x0801 The Windows DEVMODE structure associated with the CDX document object. (Unformatted)
        	kCDXProp_PrintMargins,					// 0x0802 The outer margins of the Document. (CDXRectangle)
        	kCDXProp_ChainAngle,					// 0x0803 The default chain angle setting in degrees * 65536. (INT32)
        	kCDXProp_BondSpacing,					// 0x0804 The spacing between segments of a multiple bond, measured relative to bond length. (INT16)
        	kCDXProp_BondLength,					// 0x0805 The default bond length. (CDXCoordinate)
        	kCDXProp_BoldWidth,						// 0x0806 The default bold bond width. (CDXCoordinate)
        	kCDXProp_LineWidth,						// 0x0807 The default line width. (CDXCoordinate)
        	kCDXProp_MarginWidth,					// 0x0808 The default amount of space surrounding atom labels. (CDXCoordinate)
        	kCDXProp_HashSpacing,					// 0x0809 The default spacing between hashed lines used in wedged hashed bonds. (CDXCoordinate)
        	kCDXProp_LabelStyle,					// 0x080A The default style for atom labels. (CDXFontStyle)
        	kCDXProp_CaptionStyle,					// 0x080B The default style for non-atomlabel text objects. (CDXFontStyle)
        	kCDXProp_CaptionJustification,			// 0x080C The horizontal justification of a caption (non-atomlabel text object) (INT8)
        	kCDXProp_FractionalWidths,				// 0x080D Signifies whether to use fractional width information when drawing text. (CDXBooleanImplied)
        	kCDXProp_Magnification,					// 0x080E The view magnification factor (INT16)
        	kCDXProp_WidthPages,					// 0x080F The width of the document in pages. (INT16)


 */        
        makeProperty(0x0800, "MacPrintInfo", "MacPrintInfo", "Unformatted");
        makeProperty(0x0801, "WinPrintInfo", "WinPrintInfo", "Unformatted");
        makeProperty(0x0802, "PrintMargins", "PrintMargins", "CDXRectangle");
        makeProperty(0x0803, "ChainAngle", "ChainAngle", "INT32", new Double(1./65536.));
        makeProperty(0x0804, "BondSpacing", "BondSpacing", "INT16", new Double(1/10.));
        makeProperty(0x0805, "BondLength", "BondLength", "CDXCoordinate");
        makeProperty(0x0806, "BoldWidth", "BoldWidth", "CDXCoordinate");
        makeProperty(0x0807, "LineWidth", "LineWidth", "CDXCoordinate");
        makeProperty(0x0808, "MarginWidth", "MarginWidth", "CDXCoordinate");
        makeProperty(0x0809, "HashSpacing", "HashSpacing", "CDXCoordinate");
        makeProperty(0x080A, "LabelStyle", "LabelStyle", "CDXFontStyle");
        makeProperty(0x080B, "CaptionStyle", "CaptionStyle", "CDXFontStyle");
        makeProperty(0x080C, "CaptionJustification", "CaptionJustification", "INT8",
            new IntValue[] {
                new IntValue(-1, "Right"),
                new IntValue(0, "Left"),
                new IntValue(1, "Center"),
                new IntValue(2, "Full"),
                new IntValue(3, "Above"),
                new IntValue(4, "Below"),
                new IntValue(5, "Auto"),
            }
            );
        makeProperty(0x080D, "FractionalWidths", "FractionalWidths", "CDXBooleanImplied");
        makeProperty(0x080E, "Magnification", "Magnification", "INT16", new Double(1./10.));
        makeProperty(0x080F, "WidthPages", "WidthPages", "INT16");
/*
        	kCDXProp_HeightPages,					// 0x0810 The height of the document in pages. (INT16)
        	kCDXProp_DrawingSpaceType,				// 0x0811 The type of drawing space used for this document. (INT8)
        	kCDXProp_Width,							// 0x0812 The width of an object in CDX coordinate units, possibly in a rotated or skewed frame. (CDXCoordinate)
        	kCDXProp_Height,						// 0x0813 The height of an object in CDX coordinate units, possibly in a rotated or skewed frame. (CDXCoordinate)
        	kCDXProp_PageOverlap,					// 0x0814 The amount of overlap of pages when a poster is tiled. (CDXCoordinate)
        	kCDXProp_Header,						// 0x0815 The text of the header. (CDXString)
        	kCDXProp_HeaderPosition,				// 0x0816 The vertical offset of the header baseline from the top of the page. (CDXCoordinate)
        	kCDXProp_Footer,						// 0x0817 The text of the footer. (CDXString)
        	kCDXProp_FooterPosition,				// 0x0818 The vertical offset of the footer baseline from the bottom of the page. (CDXCoordinate)
        	kCDXProp_PrintTrimMarks,				// 0x0819 If present, trim marks are to printed in the margins. (CDXBooleanImplied)
        	kCDXProp_LabelStyleFont,				// 0x081A The default font family for atom labels. (INT16)
        	kCDXProp_CaptionStyleFont,				// 0x081B The default font style for captions (non-atom-label text objects). (INT16)
        	kCDXProp_LabelStyleSize,				// 0x081C The default font size for atom labels. (INT16)
        	kCDXProp_CaptionStyleSize,				// 0x081D The default font size for captions (non-atom-label text objects). (INT16)
        	kCDXProp_LabelStyleFace,				// 0x081E The default font style for atom labels. (INT16)
        	kCDXProp_CaptionStyleFace,				// 0x081F The default font face for captions (non-atom-label text objects). (INT16)
 */        
        makeProperty(0x0810, "HeightPages", "HeightPages", "INT16");
        makeProperty(0x0811, "DrawingSpaceTable", "DrawingSpace", "INT8",
            new IntValue[] {
                new IntValue(0, "Pages"),
                new IntValue(1, "Poster"),
            },
            "DrawingSpaceType"
            );
        makeProperty(0x0812, "DrawingSpaceWidth", "Width", "CDXCoordinate");
        makeProperty(0x0813, "DrawingSpaceHeight", "Height", "CDXCoordinate");
        makeProperty(0x0814, "PageOverlap", "PageOverlap", "CDXCoordinate");
        makeProperty(0x0815, "Header", "Header", "CDXString");
        makeProperty(0x0816, "HeaderPosition", "HeaderPosition", "CDXCoordinate");
        makeProperty(0x0817, "Footer", "Footer", "CDXString");
        makeProperty(0x0818, "FooterPosition", "FooterPosition", "CDXCoordinate");
        makeProperty(0x0819, "PrintTrimMarks", "PrintTrimMarks", "CDXBooleanImplied");
        makeProperty(0x081A, "LabelStyleFont", "LabelFont", "INT16");
        makeProperty(0x081B, "CaptionStyleFont", "CaptionFont", "INT16");
        makeProperty(0x081C, "LabelStyleSize", "LabelSize", "INT16");
        makeProperty(0x081D, "CaptionStyleSize", "CaptionSize", "INT16");
        makeProperty(0x081E, "LabelStyleFace", "LabelFace", "INT16");
        makeProperty(0x081F, "CaptionStyleFace", "CaptionFace", "INT16");
/*
        	kCDXProp_LabelStyleColor,				// 0x0820 The default color for atom labels (INT16)
        	kCDXProp_CaptionStyleColor,				// 0x0821 The default color for captions (non-atom-label text objects). (INT16)
        	kCDXProp_BondSpacingAbs,				// 0x0822 The absolute distance between segments of a multiple bond. (CDXCoordinate)
        	kCDXProp_LabelJustification,			// 0x0823 The default justification for atom labels. (INT8)
        	kCDXProp_FixInplaceExtent,				// 0x0824 Defines a size for OLE In-Place editing. (CDXPoint2D)
        	kCDXProp_Side,							// 0x0825 A specific side of an object (rectangle). (INT16)
        	kCDXProp_FixInplaceGap,					// 0x0826 Defines a padding for OLE In-Place editing. (CDXPoint2D)
 */        
        makeProperty(0x0820, "LabelStyleColor", "LabelColor", "INT16");
        makeProperty(0x0821, "CaptionStyleColor", "CaptionColor", "INT16");
        makeProperty(0x0822, "BondSpacingAbs", "BondSpacingAbs", "CDXCoordinate");
        makeProperty(0x0823, "LabelJustification", "LabelJustification", "INT8",
            new IntValue[] {
                new IntValue(-1, "Right"),
                new IntValue(0, "Left"),
                new IntValue(1, "Center"),
                new IntValue(2, "Full"),
                new IntValue(3, "Above"),
                new IntValue(4, "Below"),
                new IntValue(5, "Auto"),
            }
            );
        makeProperty(0x0824, "FixInplaceExtent", "FixInPlaceExtent", "CDXPoint2D");
        makeProperty(0x0825, "Side", "Side", "UINT16",
            new IntValue[] {
                new IntValue(0, "undefined"),
                new IntValue(1, "top"),
                new IntValue(2, "left"),
                new IntValue(3, "bottom"),
                new IntValue(4, "right"),
            }
        );
        makeProperty(0x0826, "FixInplaceGap", "FixInPlaceGap", "CDXPoint2D");
        makeProperty(0x0827, "CartridgeData", "CartridgeData", "Unformatted");
        makeProperty(0x0828, "NewProp828", "NewProp828", "Unformatted");
        makeProperty(0x0829, "NewProp829", "NewProp829", "Unformatted");
        makeProperty(0x082a, "NewProp82a", "NewProp82a", "Unformatted");
/*
        	// Window properties.
        	kCDXProp_Window_IsZoomed = 0x0900,		// 0x0900 Signifies whether the main viewing window is zoomed (maximized). (CDXBooleanImplied)
        	kCDXProp_Window_Position,				// 0x0901 The top-left position of the main viewing window. (CDXPoint2D)
        	kCDXProp_Window_Size,					// 0x0902 Height and width of the document window. (CDXPoint2D)
        	
 */
        makeProperty(0x0900, "Window_IsZoomed", "WindowIsZoomed", "CDXBooleanImplied");
        makeProperty(0x0901, "Window_Position", "WindowPosition", "CDXPoint2D");
        makeProperty(0x0902, "Window_Size", "WindowSize", "CDXPoint2D");

/*
        	// Graphic object properties.
        	kCDXProp_Graphic_Type = 0x0A00,			// 0x0A00 The type of graphical object. (INT16)
        	kCDXProp_Line_Type,						// 0x0A01 The type of a line object. (INT16)
        	kCDXProp_Arrow_Type,					// 0x0A02 The type of arrow object, which represents line, arrow, arc, rectangle, or orbital. (INT16)
        	kCDXProp_Rectangle_Type,				// 0x0A03 The type of a rectangle object. (INT16)
        	kCDXProp_Oval_Type,						// 0x0A04 The type of an arrow object that represents a circle or ellipse. (INT16)
        	kCDXProp_Orbital_Type,					// 0x0A05 The type of orbital object. (INT16)
        	kCDXProp_Bracket_Type,					// 0x0A06 The type of symbol object. (INT16)
        	kCDXProp_Symbol_Type,					// 0x0A07 The type of symbol object. (INT16)
        	kCDXProp_Curve_Type,					// 0x0A08 The type of curve object. (INT16)
        	
 */        
        makeProperty(0x0A00, "Graphic_Table", "GraphicTable", "INT16",
            new IntValue[] {
                new IntValue(0, "Undefined"),
                new IntValue(1, "Line"),
                new IntValue(2, "Arc"),
                new IntValue(3, "Rectangle"),
                new IntValue(4, "Oval"),
                new IntValue(5, "Orbital"),
                new IntValue(6, "Bracket"),
                new IntValue(7, "Symbol"),
            },
            "GraphicType"
            );
//???        makeProperty(0x0A01, "Line_Table", "LineTable", "INT16");
        makeProperty(0x0A01, "Line_Table", "LineTable", "INT8",
            new IntValue[] {
                new IntValue(0, "Solid"),
                new IntValue(1, "Dashed"),
                new IntValue(2, "Bold"),
                new IntValue(4, "Wavy"),
            },
            "LineType"
            );
//???        makeProperty(0x0A02, "Arrow_Table", "ArrowTable", "INT16");
        makeProperty(0x0A02, "Arrow_Table", "ArrowTable", "INT8",
            new IntValue[] {
                new IntValue(0, "NoHead"),
                new IntValue(1, "HalfHead"),
                new IntValue(2, "FullHead"),
                new IntValue(4, "Resonance"),
                new IntValue(8, "Equilibrium"),
                new IntValue(16, "Hollow"),
                new IntValue(32, "RetroSynthetic"),
            },
            "ArrowType"
            );
        makeProperty(0x0A03, "Rectangle_Table", "RectangleTable", "INT16",
            new IntValue[] {
                new IntValue(0, "Plain"),
                new IntValue(1, "RoundEdge"),
                new IntValue(2, "Shadow"),
                new IntValue(4, "Shaded"),
                new IntValue(8, "Filled"),
                new IntValue(16, "Dashed"),
                new IntValue(32, "Bold"),
            },
            "RectangleType"
            );
        makeProperty(0x0A04, "Oval_Table", "OvalTable", "INT16",
            new IntValue[] {
                new IntValue(0, "Unknown"),
                new IntValue(1, "Circle"),
                new IntValue(2, "Shaded"),
                new IntValue(4, "Filled"),
                new IntValue(8, "Dashed"),
                new IntValue(16, "Bold"),
                new IntValue(32, "Shadowed"),
            },
            "OvalType"
            );
        makeProperty(0x0A05, "Orbital_Table", "OrbitalTable", "INT16",
            new IntValue[] {
                new IntValue(0, "s"),
                new IntValue(1, "oval"),
                new IntValue(2, "lobe"),
                new IntValue(3, "p"),
                new IntValue(4, "hybridPlus"),
                new IntValue(5, "hybridMinus"),
                new IntValue(6, "dz2Plus"),
                new IntValue(7, "dz2Minus"),
                new IntValue(8, "dxy dxy"),
                new IntValue(256, "sShaded"),
                new IntValue(257, "ovalShaded"),
                new IntValue(258, "lobeShaded"),
                new IntValue(259, "pShaded"),
                new IntValue(512, "sFilled"),
                new IntValue(513, "ovalFilled"),
                new IntValue(514, "lobeFilled"),
                new IntValue(515, "pFilled"),
                new IntValue(516, "hybridPlusFilled"),
                new IntValue(517, "hybridMinusFilled"),
                new IntValue(518, "dz2PlusFilled"),
                new IntValue(519, "dz2MinusFilled"),
                new IntValue(520, "dxyFilled"),
                },
                "OrbitalType"
                );
        makeProperty(0x0A06, "Bracket_Table", "BracketTable", "INT16",
            new IntValue[] {
                new IntValue(0, "RoundPair"),
                new IntValue(1, "SquarePair"),
                new IntValue(2, "CurlyPair"),
                new IntValue(3, "Square"),
                new IntValue(4, "Curly"),
                new IntValue(5, "Round"),
            },
            "BracketType"
            );
        makeProperty(0x0A07, "Symbol_Table", "SymbolTable", "INT16",
            new IntValue[] {
                new IntValue(0, "LonePair"),
                new IntValue(1, "Electron"),
                new IntValue(2, "RadicalCation"),
                new IntValue(3, "RadicalAnion"),
                new IntValue(4, "CirclePlus"),
                new IntValue(5, "CircleMinus"),
                new IntValue(6, "Dagger"),
                new IntValue(7, "DoubleDagger"),
                new IntValue(8, "Plus"),
                new IntValue(9, "Minus"),
                new IntValue(10, "Racemic"),
                new IntValue(11, "Absolute"),
                new IntValue(12, "Relative"),
            },
            "SymbolType"
            );
/*--
Value CDXML Name Description
0x0001 1 Closed
0x0002 2 Dashed
0x0004 4 Bold
0x0008 8 Arrow at End
0x0010 16 Arrow at Start
0x0020 32 Half-arrow at End
0x0040 64 Half-arrow at Start
0x0080 128 Filled
0x0100 256 Shaded
0x0200 512 Doubled
0x0000 0 Plain

--*/
        makeProperty(0x0A08, "Curve_Table", "CurveTable", "INT16",
            new IntValue[] {
                new IntValue(0x0001, "1"),
                new IntValue(0x0002, "2"),
                new IntValue(0x0004, "4"),
                new IntValue(0x0008, "8"),
                new IntValue(0x0010, "16"),
                new IntValue(0x0018, "24"),
                new IntValue(0x0020, "32"),
                new IntValue(0x0040, "64"),
                new IntValue(0x0080, "128"),
                new IntValue(0x0100, "256"),
                new IntValue(0x0200, "512"),
                new IntValue(0x0000, "0"),
            },
            "CurveType"
            );

/*
        	kCDXProp_Arrow_HeadSize = 0x0A20,		// 0x0A20 The size of the arrow's head. (INT16)
        	kCDXProp_Arc_AngularSize,				// 0x0A21 The size of an arc (in degrees * 10, so 90 degrees = 900). (INT16)
        	kCDXProp_Bracket_LipSize,				// 0x0A22 The size of a bracket. (INT16)
        	kCDXProp_Curve_Points,					// 0x0A23 The B&eacute;zier curve's control point locations. (CDXCurvePoints)
        	kCDXProp_Bracket_Usage,					// 0x0A24 The syntactical chemical meaning of the bracket (SRU, mer, mon, xlink, etc). (INT8)
        	kCDXProp_Polymer_RepeatPattern,			// 0x0A25 The head-to-tail connectivity of objects contained within the bracket. (INT8)
        	kCDXProp_Polymer_FlipType,				// 0x0A26 The flip state of objects contained within the bracket. (INT8)
        	kCDXProp_BracketedObjects,				// 0x0A27 The set of objects contained in a BracketedGroup. (CDXObjectIDArray)
        	kCDXProp_Bracket_RepeatCount,			// 0x0A28 The number of times a multiple-group BracketedGroup is repeated. (INT16)
        	kCDXProp_Bracket_ComponentOrder,		// 0x0A29 The component order associated with a BracketedGroup. (INT16)
        	kCDXProp_Bracket_SRULabel,				// 0x0A2A The label associated with a BracketedGroup that represents an SRU. (CDXString)
        	kCDXProp_Bracket_GraphicID,				// 0x0A2B The ID of a graphical object (bracket, brace, or parenthesis) associated with a Bracket Attachment. (CDXObjectID)
        	kCDXProp_Bracket_BondID,				// 0x0A2C The ID of a bond that crosses a Bracket Attachment. (CDXObjectID)
        	kCDXProp_Bracket_InnerAtomID,			// 0x0A2D The ID of the node located within the Bracketed Group and attached to a bond that crosses a Bracket Attachment. (CDXObjectID)
        	kCDXProp_Curve_Points3D,				// 0x0A2E The B&eacute;zier curve's control point locations. (CDXCurvePoints3D)

 */        
        makeProperty(0x0A20, "Arrow_HeadSize", "HeadSize", "INT16");
        makeProperty(0x0A21, "Arc_AngularSize", "AngularSize", "INT16", new Double(1./10.));
        makeProperty(0x0A22, "Bracket_LipSize", "LipSize", "INT16");
        makeProperty(0x0A23, "Curve_Points", "CurvePoints", "CDXCurvePoints");
        makeProperty(0x0A24, "Bracket_Usage", "BracketUsage", "INT8",
            new IntValue[] {
                new IntValue(0,"Unspecified"),
                new IntValue(1,"Unused1"),
                new IntValue(2,"Unused2"),
                new IntValue(3,"SRU"),
                new IntValue(4,"Monomer"),
                new IntValue(5,"Mer"),
                new IntValue(6,"Copolymer"),
                new IntValue(7,"CopolymerAlternating"),
                new IntValue(8,"CopolymerRandom"),
                new IntValue(9,"CopolymerBlock"),
                new IntValue(10,"Crosslink"),
                new IntValue(11,"Graft"),
                new IntValue(12,"Modification"),
                new IntValue(13,"Component"),
                new IntValue(14,"MixtureUnordered"),
                new IntValue(15,"MixtureOrdered"),
                new IntValue(16,"MultipleGroup"),
                new IntValue(17,"Generic"),
                new IntValue(18,"Anypolymer"),
            }
            );
/*--
0 HeadToTail One end of the repeating unit is connected to the other end of the adjacent repeating unit
1 HeadToHead One end of the repeating unit is connected to the same end of the adjacent repeating unit
2 EitherUnknown A mixture of the above, or an unknown repeat pattern
--*/
        makeProperty(0x0A25, "Polymer_RepeatPattern", "PolymerRepeatPattern", "INT8",
            new IntValue[] {
                new IntValue(0, "HeadToTail"),
                new IntValue(1, "HeadToHead"),
                new IntValue(2, "EitherUnknown"),
            }
        );
        makeProperty(0x0A26, "Polymer_FlipTable", "PolymerFlipTable", "INT8",
            new IntValue[] {
                new IntValue(0, "Unspecified"),
                new IntValue(1, "NoFlip"),
                new IntValue(2, "Flip"),
            }
        );
        makeProperty(0x0A27, "BracketedObjects", "BracketedObjectIDs", "CDXObjectIDArray");
        makeProperty(0x0A28, "Bracket_RepeatCount", "RepeatCount", "FLOAT64");
        makeProperty(0x0A29, "Bracket_ComponentOrder", "ComponentOrder", "INT16");
        makeProperty(0x0A2A, "Bracket_SRULabel", "SRULabel", "CDXString");
        makeProperty(0x0A2B, "Bracket_GraphicID", "GraphicID", "CDXObjectID");
        makeProperty(0x0A2C, "Bracket_BondID", "BondID", "CDXObjectID");
        makeProperty(0x0A2D, "Bracket_InnerAtomID", "InnerAtomID", "CDXObjectID");
        makeProperty(0x0A2E, "Curve_Points3D", "CurvePoints3D", "CDXCurvePoints3D");
        makeProperty(0x0A2F, "Arrowhead_Type", "ArrowheadType", "INT16",
        		// these values are guessed
        		new IntValue[]{
            	new IntValue(0, "Unknown"),
            	new IntValue(1, "Solid"),
        		}
        		
    		);
/*
0x0A30 kCDXProp_Arrowhead_CenterSize HeadCenterSize UINT16 
 The size of the arrow's head from the tip to the back of the head.  
0x0A31 kCDXProp_Arrowhead_Width HeadWidth UINT16 
 The half-width of the arrow's head.  
0x0A32 kCDXProp_ShadowSize ShadowSize UINT16 
 The size of the object's shadow.  
0x0A33 kCDXProp_Arrow_ShaftSpacing ArrowShaftSpacing UINT16 
 The width of the space between a multiple-component arrow shaft, as in an equilibrium arrow.  
0x0A34 kCDXProp_Arrow_EquilibriumRatio ArrowEquilibriumRatio UINT16 
 The ratio of the length of the left component of an equilibrium arrow (viewed from the end to the start) to the right component.  
0x0A35 kCDXProp_Arrow_ArrowHead_Head ArrowHeadHead INT16 
 The type of arrowhead at the head of the arrow. This is an enumerated property.  
0x0A36 kCDXProp_Arrow_ArrowHead_Tail ArrowHeadTail INT16 
 The type of arrowhead at the tail of the arrow. This is an enumerated property.  
0x0A37 kCDXProp_Fill_Type FillType INT16 
 The type of the fill, for objects that can be filled. This is an enumerated property.  
0x0A38 kCDXProp_Curve_Spacing CurveSpacing UINT16 
 The width of the space between a a Doubled curve.  
0x0A38 kCDXProp_Closed Closed CDXBoolean  ????? 0x0A39??
 Signifies whether object is closed.  
0x0A3A kCDXProp_Arrow_Dipole Dipole CDXBoolean 
 Signifies whether the arrow is a dipole arrow.  
0x0A3B kCDXProp_Arrow_NoGo NoGo INT8 
 Signifies whether arrow is a no-go arrow, and the type of no-go (crossed-through or hashed-out) if so. This is an enumerated property.  
0x0A3C kCDXProp_CornerRadius CornerRadius INT16 
 The radius of the rounded corner of a rounded rectangle.  
0x0A3D kCDXProp_Frame_Type FrameType INT16 
 The type of frame on an object. This is an enumerated property.  
 */
        
		makeProperty(0x0A30, "Arrowhead_CenterSize", "ArrowheadCenterSize", "UINT16");
		makeProperty(0x0A31, "Arrowhead_Width", "ArrowheadWidth", "UINT16");
		makeProperty(0x0A32, "ShadowSize", "ShadowSize", "UINT16");
		makeProperty(0x0A33, "Arrow_ShaftSpacing", "ArrowShaftSpacing", "UINT16");
		makeProperty(0x0A34, "Arrow_EquilibriumRatio", "ArrowEquilibriumRatio", "UINT16");
		makeProperty(0x0A35, "Arrow_ArrowHead_Head", "ArrowheadHead", "INT16",
				// guessed
				new IntValue[] {
				new IntValue(0, "None"),
				new IntValue(2, "Full"),
				}
			);
		makeProperty(0x0A36, "Arrow_ArrowHead_Tail", "ArrowHeadTail", "INT16");
		makeProperty(0x0A37, "Fill_Type", "FillType", "INT16",
	            new IntValue[] {
                new IntValue(0, "Unknown"),
                new IntValue(1, "None"),
            }
				);
		makeProperty(0x0A38, "Curve_Spacing", "CurveSpacing", "UINT16");
		makeProperty(0x0A39, "Closed", "Closed", "CDXBoolean"); ///????? 0x0A39??
		makeProperty(0x0A3A, "Arrow_Dipole", "Dipole", "CDXBoolean");
		makeProperty(0x0A3B, "Arrow_NoGo", "NoGo", "INT8");
		makeProperty(0x0A3C, "CornerRadius", "CornerRadius", "INT16");
		makeProperty(0x0A3D, "Frame_Type", "FrameType", "INT16");
/*
        	// Embedded pictures.
        	kCDXProp_Picture_Edition = 0x0A60,		// 0x0A60 The section information (SectionHandle) of the Macintosh Publish & Subscribe edition embedded in the CDX picture object. (Unformatted)
        	kCDXProp_Picture_EditionAlias,			// 0x0A61 The alias information of the Macintosh Publish & Subscribe edition embedded in the CDX picture object. (Unformatted)
        	kCDXProp_MacPICT,						// 0x0A62 A Macintosh PICT data object. (Unformatted)
        	kCDXProp_WindowsMetafile,				// 0x0A63 A Microsoft Windows Metafile object. (Unformatted)
        	kCDXProp_OLEObject,						// 0x0A64 An OLE object. (Unformatted)
        	kCDXProp_EnhancedMetafile,				// 0x0A65 A Microsoft Windows Enhanced Metafile object. (Unformatted)

 */
        makeProperty(0x0A60, "Picture_Edition", "Edition", "Unformatted");
        makeProperty(0x0A61, "Picture_EditionAlias", "EditionAlias", "Unformatted");
        makeProperty(0x0A62, "MacPICT", "MacPICT", "Unformatted");
        makeProperty(0x0A63, "WindowsMetafile", "WindowsMetafile", "Unformatted");
        makeProperty(0x0A64, "OLEObject", "OLEObject", "Unformatted");
        makeProperty(0x0A65, "EnhancedMetafile", "EnhancedMetafile", "Unformatted");
        makeProperty(0x0A6E, "GIF", "GIF", "Unformatted");
        makeProperty(0x0A6F, "TIFF", "TIFF", "Unformatted");
        
        makeProperty(0x0A70, "PNG", "PNG", "Unformatted");
        makeProperty(0x0A71, "JPEG", "JPEG", "Unformatted");
        makeProperty(0x0A72, "BMP", "BMP", "Unformatted");
/*
        	// Spectrum properties
        	kCDXProp_Spectrum_XSpacing = 0x0A80,	// 0x0A80 The spacing in logical units (ppm, Hz, wavenumbers) between points along the X-axis of an evenly-spaced grid. (FLOAT64)
        	kCDXProp_Spectrum_XLow,					// 0x0A81 The first data point for the X-axis of an evenly-spaced grid. (FLOAT64)
        	kCDXProp_Spectrum_XType,				// 0x0A82 The type of units the X-axis represents. (INT16)
        	kCDXProp_Spectrum_YType,				// 0x0A83 The type of units the Y-axis represents. (INT16)
        	kCDXProp_Spectrum_XAxisLabel,			// 0x0A84 A label for the X-axis. (CDXString)
        	kCDXProp_Spectrum_YAxisLabel,			// 0x0A85 A label for the Y-axis. (CDXString)
        	kCDXProp_Spectrum_DataPoint,			// 0x0A86 The Y-axis values for the spectrum. It is an array of double values corresponding to X-axis values. (FLOAT64)
        	kCDXProp_Spectrum_Class,				// 0x0A87 The type of spectrum represented. (INT16)
        	kCDXProp_Spectrum_YLow,					// 0x0A88 Y value to be used to offset data when storing XML. (FLOAT64)
        	kCDXProp_Spectrum_YScale,				// 0x0A89 Y scaling used to scale data when storing XML. (FLOAT64)

 */        
        makeProperty(0x0A80, "Spectrum_XSpacing", "XSpacing", "FLOAT64");
        makeProperty(0x0A81, "Spectrum_XLow", "XLow", "FLOAT64");
        makeProperty(0x0A82, "Spectrum_XTable", "XTable", "INT16",
            new IntValue[] {
                new IntValue(0, "Unknown"),
                new IntValue(1, "Wavenumbers"),
                new IntValue(2, "Microns"),
                new IntValue(3, "Hertz"),
                new IntValue(4, "MassUnits"),
                new IntValue(5, "PartsPerMillion"),
                new IntValue(6, "Other"),
            },
            "SpectrumXType"
        );
        makeProperty(0x0A83, "Spectrum_YTable", "YTable", "INT16",
            new IntValue[] {
                new IntValue(0, "Unknown"),
                new IntValue(1, "Absorbance"),
                new IntValue(2, "Transmittance"),
                new IntValue(3, "PercentTransmittance"),
                new IntValue(4, "Other"),
                new IntValue(5, "ArbitraryUnits"),
            },
            "SpectrumYType"
        );
        makeProperty(0x0A84, "Spectrum_XAxisLabel", "XAxisLabel", "CDXString");
        makeProperty(0x0A85, "Spectrum_YAxisLabel", "YAxisLabel", "CDXString");
        makeProperty(0x0A86, "Spectrum_DataPoint", "not used", "FLOAT64");
        makeProperty(0x0A87, "Spectrum_Class", "Class", "INT16",
            new IntValue[] {
                new IntValue(0, "Unknown"),
                new IntValue(1, "Chromatogram"),
                new IntValue(2, "Infrared"),
                new IntValue(3, "UVVis"),
                new IntValue(4, "XRayDiffraction"),
                new IntValue(5, "MassSpectrum"),
                new IntValue(6, "NMR"),
                new IntValue(7, "Raman"),
                new IntValue(8, "Fluorescence"),
                new IntValue(9, "Atomic"),
            }
            );
        makeProperty(0x0A88, "Spectrum_YLow", "YLow", "FLOAT64");
        makeProperty(0x0A89, "Spectrum_YScale", "YScale", "FLOAT64");
/*
        	// TLC properties
        	kCDXProp_TLC_OriginFraction = 0x0AA0,	// 0x0AA0 The distance of the origin line from the bottom of a TLC Plate, as a fraction of the total height of the plate. (FLOAT64)
        	kCDXProp_TLC_SolventFrontFraction,		// 0x0AA1 The distance of the solvent front from the top of a TLC Plate, as a fraction of the total height of the plate. (FLOAT64)
        	kCDXProp_TLC_ShowOrigin,				// 0x0AA2 Show the origin line near the base of the TLC Plate if non-zero. (CDXBoolean)
        	kCDXProp_TLC_ShowSolventFront,			// 0x0AA3 Show the solvent front line near the top of the TLC Plate if non-zero. (CDXBoolean)
        	kCDXProp_TLC_ShowBorders,				// 0x0AA4 Show borders around the edges of the TLC Plate if non-zero. (CDXBoolean)
        	kCDXProp_TLC_ShowSideTicks,				// 0x0AA5 Show tickmarks up the side of the TLC Plate if non-zero. (CDXBoolean)
        	kCDXProp_TLC_Rf = 0x0AB0,				// 0x0AB0 The Retention Factor of an individual spot. (FLOAT64)
        	kCDXProp_TLC_Tail,						// 0x0AB1 The length of the "tail" of an individual spot. (CDXCoordinate)
        	kCDXProp_TLC_ShowRf,					// 0x0AB2 Show the spot's Retention Fraction (Rf) value if non-zero. (CDXBoolean)

 */        
        makeProperty(0x0AA0, "TLC_OriginFraction", "OriginFraction", "FLOAT64");
        makeProperty(0x0AA1, "TLC_SolventFrontFraction", "SolventFrontFraction", "FLOAT64");
        makeProperty(0x0AA2, "TLC_ShowOrigin", "ShowOrigin", "CDXBoolean");
        makeProperty(0x0AA3, "TLC_ShowSolventFront", "ShowSolventFront", "CDXBoolean");
        makeProperty(0x0AA4, "TLC_ShowBorders", "ShowBorders", "CDXBoolean");
        makeProperty(0x0AA5, "TLC_ShowSideTicks", "ShowSideTicks", "CDXBoolean");
        
        makeProperty(0x0AB0, "TLC_Rf", "Rf", "FLOAT64");
        makeProperty(0x0AB1, "TLC_Tail", "Tail", "CDXCoordinate");
        makeProperty(0x0AB2, "TLC_ShowRf", "ShowRf", "FLOAT64");
/*
        	// Alternate Group properties
        	kCDXProp_NamedAlternativeGroup_TextFrame = 0x0B00,	// 0x0B00 The bounding box of upper portion of the Named Alternative Group, containing the name of the group. (CDXRectangle)
        	kCDXProp_NamedAlternativeGroup_GroupFrame,			// 0x0B01 The bounding box of the lower portion of the Named Alternative Group, containing the definition of the group. (CDXRectangle)
        	kCDXProp_NamedAlternativeGroup_Valence,				// 0x0B02 The number of attachment points in each alternative in a named alternative group. (INT16)

 */        
        makeProperty(0x0B00, "NamedAlternativeGroup_TextFrame", "TextFrame", "CDXRectangle");
        makeProperty(0x0B01, "NamedAlternativeGroup_GroupFrame", "GroupFrame", "CDXRectangle");
        makeProperty(0x0B02, "NamedAlternativeGroup_Valence", "Valence", "INT16");
/*--
        	// Geometry and Constraint properties
        	kCDXProp_GeometricFeature = 0x0B80,		// 0x0B80 The type of the geometrical feature (point, line, plane, etc.). (INT8)
        	kCDXProp_RelationValue,					// 0x0B81 The numeric relationship (if any) among the basis objects used to define this object. (INT8)
        	kCDXProp_BasisObjects,					// 0x0B82 An ordered list of objects used to define this object. (CDXObjectIDArray)
        	kCDXProp_ConstraintType,				// 0x0B83 The constraint type (distance or angle). (INT8)
        	kCDXProp_ConstraintMin,					// 0x0B84 The minimum value of the constraint (FLOAT64)
        	kCDXProp_ConstraintMax,					// 0x0B85 The maximum value of the constraint (FLOAT64)
        	kCDXProp_IgnoreUnconnectedAtoms,		// 0x0B86 Signifies whether unconnected atoms should be ignored within the exclusion sphere. (CDXBooleanImplied)
        	kCDXProp_DihedralIsChiral,				// 0x0B87 Signifies whether a dihedral is signed or unsigned. (CDXBooleanImplied)
        	kCDXProp_PointIsDirected,				// 0x0B88 For a point based on a normal, signifies whether it is in a specific direction relative to the reference point. (CDXBooleanImplied)

0 	Undefined 	Geometric feature type is undefined
1 	PointFromPointPointDistance 	A point defined by two points and a distance. kCDXProp_BasisObjects should be present and contain an ordered list of two other points. This new point will be positioned at a given distance (may be negative) from the first of those points in the direction of the second of those points)
2 	PointFromPointPointPercentage 	A point defined by two points and a percentage. kCDXProp_BasisObjects should be present and contain an ordered list of two other points. This new point will be positioned at a given percentage of the distance (may be negative) from the first of those points in the direction of the second of those points)
3 	PointFromPointNormalDistance 	A point defined by a point, a normal line, and a distance. kCDXProp_BasisObjects should be present and contain an unordered list consisting of a point and a normal. This new point will be positioned at a given distance (may be negative) from the point in the direction of the indicated by the normal)
4 	LineFromPoints 	A best fit line defined by two or more points. kCDXProp_BasisObjects should be present and contain an unordered list consisting of at least two points. If more than two points are used to define this line, a maximum RMS deviation may also be specified
5 	PlaneFromPoints 	A best fit plane defined by three or more points. kCDXProp_BasisObjects should be present and contain an unordered list consisting of at least three points. If more than three points are used to define this line, a maximum RMS deviation may also be specified
6 	PlaneFromPointLine 	A plane defined by a point and a line. kCDXProp_BasisObjects should be present and contain an unordered list consisting of at a point and a line.
7 	CentroidFromPoints 	A centroid defined by points. kCDXProp_BasisObjects should be present and contain an unordered list consisting of at least one point
8 	NormalFromPointPlane 	A normal line defined by a point and a plane. kCDXProp_BasisObjects should be present and contain an unordered list consisting of at a point and a plane.
--*/
        makeProperty(0x0B80, "GeometricFeature", "GeometricFeature", "INT8",
            new IntValue[] {
                new IntValue(0,"Undefined"),
                new IntValue(1,"PointFromPointPointDistance"),
                new IntValue(2,"PointFromPointPointPercentage"),
                new IntValue(3,"PointFromPointNormalDistance"),
                new IntValue(4,"LineFromPoints"),
                new IntValue(5,"PlaneFromPoints"),
                new IntValue(6,"PlaneFromPointLine"),
                new IntValue(7,"CentroidFromPoints"),
                new IntValue(8,"NormalFromPointPlane"),
            }
        );
        makeProperty(0x0B81, "RelationValue", "RelationValue", "FLOAT64");
        makeProperty(0x0B82, "BasisObjects", "BasisObjects", "CDXObjectIDArray");
        makeProperty(0x0B83, "ConstraintTable", "ConstraintTable", "INT8",
            new IntValue[] {
                new IntValue(0,"Undefined"),
                new IntValue(1,"Distance"),
                new IntValue(2,"Anglee"),
                new IntValue(3,"ExclusionSphere"),
            }
        );
        makeProperty(0x0B84, "ConstraintMin", "ConstraintMin", "FLOAT64");
        makeProperty(0x0B85, "ConstraintMax", "ConstraintMax", "FLOAT64");
        makeProperty(0x0B86, "IgnoreUnconnectedAtoms", "IgnoreUnconnectedAtoms", "CDXBooleanImplied");
        makeProperty(0x0B87, "DihedralIsChiral", "DihedralIsChiral", "CDXBooleanImplied");
        makeProperty(0x0B88, "PointIsDirected", "PointIsDirected", "CDXBooleanImplied");
/*
 * 
 */        
        makeProperty(0x0BB0, "ChemicalPropertyType", "ChemicalPropertyType", "UINT32");
        makeProperty(0x0BB1, "ChemicalPropertyDisplayID", "ChemicalPropertyDisplayID", "CDXObjectID");
        makeProperty(0x0BB2, "ChemicalPropertyIsActive", "ChemicalPropertyIsActive", "CDXBoolean");
/*
        	// Reaction properties
        	kCDXProp_ReactionStep_Atom_Map = 0x0C00,// 0x0C00 Represents pairs of mapped atom IDs; each pair is a reactant atom mapped to to a product atom. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Reactants,		// 0x0C01 An order list of reactants present in the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Products,			// 0x0C02 An order list of products present in the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Plusses,			// 0x0C03 An ordered list of pluses used to separate components of the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Arrows,			// 0x0C04 An ordered list of arrows used to separate components of the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_ObjectsAboveArrow,// 0x0C05 An order list of objects above the arrow in the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_ObjectsBelowArrow,// 0x0C06 An order list of objects below the arrow in the Reaction Step. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Atom_Map_Manual,	// 0x0C07 Represents pairs of mapped atom IDs; each pair is a reactant atom mapped to to a product atom. (CDXObjectIDArray)
        	kCDXProp_ReactionStep_Atom_Map_Auto,	// 0x0C08 Represents pairs of mapped atom IDs; each pair is a reactant atom mapped to to a product atom. (CDXObjectIDArray)

 */        
        makeProperty(0x0C00, "ReactionStep_Atom_Map", "ReactionStepAtomMap", "CDXObjectIDArray");
        makeProperty(0x0C01, "ReactionStep_Reactants", "ReactionStepReactants", "CDXObjectIDArray");
        makeProperty(0x0C02, "ReactionStep_Products", "ReactionStepProducts", "CDXObjectIDArray");
        makeProperty(0x0C03, "ReactionStep_Plusses", "ReactionStepPlusses", "CDXObjectIDArray");
        makeProperty(0x0C04, "ReactionStep_Arrows", "ReactionStepArrows", "CDXObjectIDArray");
        makeProperty(0x0C05, "ReactionStep_ObjectsAboveArrow", "ReactionStepObjectsAboveArrow", "CDXObjectIDArray");
        makeProperty(0x0C06, "ReactionStep_ObjectsBelowArrow", "ReactionStepObjectsBelowArrow", "CDXObjectIDArray");
        makeProperty(0x0C07, "ReactionStep_Atom_Map_Manual", "ReactionStepAtomMapManual", "CDXObjectIDArray");
        makeProperty(0x0C08, "ReactionStep_Atom_Map_Auto", "ReactionStepAtomMapAuto", "CDXObjectIDArray");
/*
        	// CDObjectTag properties
        	kCDXProp_ObjectTag_Type = 0x0D00,		// 0x0D00 The tag's data type. (INT16)
        	kCDXProp_Unused6,						// 0x0D01 obsolete (obsolete)
        	kCDXProp_Unused7,						// 0x0D02 obsolete (obsolete)
        	kCDXProp_ObjectTag_Tracking,			// 0x0D03 The tag will participate in tracking if non-zero. (CDXBoolean)
        	kCDXProp_ObjectTag_Persistent,			// 0x0D04 The tag will be resaved to a CDX file if non-zero. (CDXBoolean)
        	kCDXProp_ObjectTag_Value,				// 0x0D05 The value is a INT32, FLOAT64 or unformatted string depending on the value of ObjectTag_Type. (varies)
        	kCDXProp_Positioning,					// 0x0D06 How the indicator should be positioned with respect to its containing object. (INT8)
        	kCDXProp_PositioningAngle,				// 0x0D07 Angular positioning, in radians * 65536. (INT32)
        	kCDXProp_PositioningOffset,				// 0x0D08 Offset positioning. (CDXPoint2D)

 */        
        makeProperty(0x0D00, "ObjectTag_Table", "TagTable", "INT16",
            new IntValue[] {
                new IntValue(0,"Unknown"),
                new IntValue(1,"Double"),
                new IntValue(2,"Long"),
                new IntValue(3,"String"),
            }
        );
        makeProperty(0x0D03, "ObjectTag_Tracking", "Tracking", "CDXBoolean");
        makeProperty(0x0D04, "ObjectTag_Persistent", "Persistent", "CDXBoolean");
        makeProperty(0x0D05, "ObjectTag_Value", "Value", "varies");
        makeProperty(0x0D06, "Positioning", "PositioningTable", "INT8",
            new IntValue[] {
                new IntValue(0,"auto"),
                new IntValue(1,"angle"),
                new IntValue(2,"offset"),
                new IntValue(3,"absolute"),
            }
            );
        makeProperty(0x0D07, "PositioningAngle", "PositioningAngle", "INT32", new Double(1./65536.));
        makeProperty(0x0D08, "PositioningOffset", "PositioningOffset", "CDXPoint2D");
/*
        	// CDSequence properties
        	kCDXProp_Sequence_Identifier = 0x0E00,	// 0x0E00 A unique (but otherwise random) identifier for a given Sequence object. (CDXString)

 */        
        makeProperty(0x0E00, "Sequence_Identifier", "SequenceIdentifier", "CDXString");
/*
        	// CDCrossReference properties
        	kCDXProp_CrossReference_Container = 0x0F00,	// 0x0F00 An external object containing (as an embedded object) the document containing the Sequence object being referenced. (CDXString)
        	kCDXProp_CrossReference_Document,		// 0x0F01 An external document containing the Sequence object being referenced. (CDXString)
        	kCDXProp_CrossReference_Identifier,		// 0x0F02 A unique (but otherwise random) identifier for a given Cross-Reference object. (CDXString)
        	kCDXProp_CrossReference_Sequence,		// 0x0F03 A value matching the SequenceIdentifier of the Sequence object to be referenced. (CDXString)

 */        
        makeProperty(0x0F00, "CrossReference_Container", "CrossReferenceContainer", "CDXString");
        makeProperty(0x0F01, "CrossReference_Document", "CrossReferenceDocument", "CDXString");
        makeProperty(0x0F02, "CrossReference_Identifier", "CrossReferenceIdentifier", "CDXString");
        makeProperty(0x0F03, "CrossReference_Sequence", "CrossReferenceSequence", "CDXString");
/*
        	// Miscellaneous properties.
        	kCDXProp_Template_PaneHeight = 0x1000,	// 0x1000 The height of the viewing window of a template grid. (CDXCoordinate)
        	kCDXProp_Template_NumRows,				// 0x1001 The number of rows of the CDX TemplateGrid object. (INT16)
        	kCDXProp_Template_NumColumns,			// 0x1002 The number of columns of the CDX TemplateGrid object. (INT16)
 */        
        makeProperty(0x1000, "Template_PaneHeight", "PaneHeight", "CDXCoordinate");
        makeProperty(0x1001, "Template_NumRows", "NumRows", "INT16");
        makeProperty(0x1002, "Template_NumColumns", "NumColumns", "INT16");
/*
        	kCDXProp_Group_Integral = 0x1100,		// 0x1100 The group is considered to be integral (non-subdivisible) if non-zero. (CDXBoolean)
 */        
        makeProperty(0x1100, "Group_Integral", "Integral", "CDXBoolean");
/*
        	kCDXProp_SplitterPositions = 0x1ff0,	// 0x1FF0 An array of vertical positions that subdivide a page into regions. (CDXObjectIDArray)
        	kCDXProp_PageDefinition,				// 0x1FF1 An array of vertical positions that subdivide a page into regions. (CDXObjectIDArray)
 */        
        makeProperty(0x1FF0, "SplitterPositions", "SplitterPositions", "CDXObjectIDArray");
        makeProperty(0x1FF1, "PageDefinition", "PageDefinition", "INT8",
            new IntValue[] {
                new IntValue(0, "Undefined"),
                new IntValue(1, "Center"),
                new IntValue(2, "TL4"),
                new IntValue(3, "IDTerm"),
                new IntValue(4, "FlushLeft"),
                new IntValue(5, "FlushRight"),
                new IntValue(6, "Reaction1"),
                new IntValue(7, "Reaction2"),
                new IntValue(8, "MulticolumnTL4"),
                new IntValue(9, "MulticolumnNonTL4"),
                new IntValue(10, "UserDefined"),
            }
            );
 
// NOT in documentation (?earlier versions?)
//        createAndIndex(""+0x6E00,  new CDXProperty(0x6E00, "6E00", "6E00", "UNK");
// pseudo properties; use dummy ids - we hope these are unique
        makeProperty(0xCDEF, "id", "id", "CDXObjectID");
        makeProperty(0xCDEE, "charset", "charset", "Unformatted");
        makeProperty(0xCDED, "font", "name", "Unformatted");
        makeProperty(0xCDEC, "r", "r", "FLOAT64");
        makeProperty(0xCDEB, "g", "g", "FLOAT64");
        makeProperty(0xCDEA, "b", "b", "FLOAT64");
        makeProperty(0xCDE9, "fontindex", "fontindex", "UINT16");
        makeProperty(0xCDE8, "fontsize",  "fontsize",  "UINT16");
        makeProperty(0xCDE7, "fontface",  "fontface",  "UINT16");
        makeProperty(0xCDE6, "fontcolor", "fontcolor", "UINT16");

/*
 */        
/** from header        

        	// User defined properties
        	// First 1024 tags are reserved for temporary tags used only during the runtime.
        	kCDXUser_TemporaryBegin = kCDXTag_UserDefined,
        	kCDXUser_TemporaryEnd = kCDXTag_UserDefined + 0x0400,

        };
*/        
	}

	/**
	 * @return the cdxName
	 */
	public String getCdxName() {
		return cdxName;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the dataType
	 */
	public CDXDataType getDataType() {
		return dataType;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}
};
//==== helper ===
class IntValue {
    int ii;
    String s;
    /**
     * 
     * @param ii
     * @param s
     */
    public IntValue(int ii, String s) {
        this.ii = ii;
        this.s = s;
    }
    /**
     * @param iv
     */
    public IntValue(IntValue iv) {
        this.ii = iv.ii;
        this.s = iv.s;
    }
}

