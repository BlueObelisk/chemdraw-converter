package org.xmlcml.cml.chemdraw.components;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**
 * 
 * @author pm286
 *
 */
public abstract class CDXDataType implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXDataType.class);
    static {
    	LOG.setLevel(Level.INFO);
    }

    // may be useful
    CDXProperty prop;
    String s;
    private byte[] bytes;
// some types are numeric and can be used for enumeration or values
    Number num;
// may contain a complex type
//    CDXComplexType complexType;
    /**
     * @param bytes
     * @param prop
     */
    public CDXDataType(byte[] bytes, CDXProperty prop) {
        this.prop = prop;
        this.bytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            this.bytes[i] = bytes[i];
        }
    }
    
    static String styleRunString(byte[] bytes, int offset) {
//   	 conflict with documentation; I get FL = 8
//       int fontLength = bytes.length;
       int fontIndex = CDXUtil.getUINT16(bytes, offset);
       offset += 2;
       // store temporarily as font-face-size-color
       String s = ""+fontIndex;
       int typeFace = CDXUtil.getUINT16(bytes, offset);
       s += " "+typeFace;
       offset += 2;
//   	 I think this is a UINT16, not 32 as in doc
//   	 also the scale appears to be /20
//   	        long lsize = Util.getUINT32(bytes, offset);
       int size = CDXUtil.getINT16(bytes, offset);
       size = size / 20;
       s += " "+size;
       offset += 2;
       int color = CDXUtil.getUINT16(bytes, offset);
       s += " "+color;
       return s;
   }


    /**
     * @return s 
     */
    public String toString() {
        return s;
    }
}

class _CDXBoolean extends CDXDataType {
    private boolean bool;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXBoolean(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        bool = CDXUtil.getINT8(bytes[0]) != 0;
        s = "" + bool;
    }
};
class _CDXBooleanImplied extends CDXDataType {
    boolean boolImpl;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXBooleanImplied(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        boolImpl = true;
        s = "yes";
    }
};
class _CDXCurvePoints extends CDXDataType {
    int[] xCurve;
    int[] yCurve;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXCurvePoints(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int nCurves = CDXUtil.getUINT16(bytes[0], bytes[1]);
        xCurve = new int[nCurves];
        yCurve = new int[nCurves];
        int nBytes = 2;
        s = "";
        for (int i = 0; i < nCurves; i++) {
            yCurve[i] = CDXUtil.getINT32(bytes[nBytes++], bytes[nBytes++], bytes[nBytes++], bytes[nBytes++]);
            xCurve[i] = CDXUtil.getINT32(bytes[nBytes++], bytes[nBytes++], bytes[nBytes++], bytes[nBytes++]);
            s += " "+xCurve[i]+" "+yCurve[i];
        }
    }
};
/*--
CDX Color Table Data Type
In CDX files, this data type consists of a series of a UINT16 count, followed by that many UINT16 triples. Each of those triples consists of the red, green, and blue components (in that order) of the color value, scaled to a range of 0...65535

In CDXML files, this data type is represented by a colortable object.

Examples: CDX: 08 00 FF FF FF FF FF FF
00 00 00 00 00 00 FF FF
00 00 00 00 FF FF FF FF
00 00 00 00 FF FF 00 00
00 00 FF FF FF FF 00 00
00 00 FF FF FF FF 00 00
FF FF
CDXML: <colortable>
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
class _CDXColorTable extends CDXDataType {
    int[][] colorTable;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXColorTable(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int nEntries = CDXUtil.getUINT16(bytes[0], bytes[1]);
        if (bytes.length != 6 * nEntries + 2) {
            throw new RuntimeException("CDXColorTable wrong length");
        }
        colorTable = new int[nEntries][];
        int byteCount = 2;
        for (int i = 0; i < nEntries; i++) {
            colorTable[i] = new int[3];
            for (int j = 0; j < 3; j++) {
                colorTable[i][j] = CDXUtil.getUINT16(bytes[byteCount++], bytes[byteCount++]);
            }
        }
        // save temporarily by separating each table by ;
        s = "";
        for (int i = 0; i < nEntries; i++) {
            if (i > 0) {
                s += ";";
            }
            for (int j = 0; j < 3; j++) {
                if (j > 0) s += " ";
                s += colorTable[i][j];
            }
        }
    }
};
class _CDXCoordinate extends CDXDataType {
    int coor2;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXCoordinate(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        coor2 = CDXUtil.getINT32(bytes);
        s = new CDXCoordinate(coor2).getAttributeValue();
        num = new Integer(coor2);
    }
};
class _CDXDate extends CDXDataType {
    int[] date;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXDate(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        if (bytes.length != 14) {
            throw new RuntimeException("Bad Date length: "+bytes.length);
        }
        date = new int[7];
        int nByte = 0;
        for (int i = 0; i < 7; i++) {
            date[i] = CDXUtil.getINT16(bytes[nByte++], bytes[nByte++]);
        }
        s = "" + date[0]+":"+ date[1]+":"+ date[2]+":"+ date[3]+":"+ date[4]+":"+ date[5]+":"+ date[6]+":";
    }
};
class _CDXElementList extends CDXDataType {
    boolean excludeElems;
    int[] element;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXElementList(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        excludeElems = false;
        int nElem = CDXUtil.getINT16(bytes[0], bytes[1]);
        if (nElem < 0) {
            excludeElems = true;
            nElem = -nElem;
        }
        if (bytes.length != nElem*2 + 2) {
            throw new RuntimeException("Bad ElementList length: "+bytes.length);
        }
        element = new int[nElem];
        int nByte = 2;
        s = "";
        for (int i = 0; i < nElem; i++) {
            element[i] = CDXUtil.getINT16(bytes[nByte++], bytes[nByte++]);
            if (i >0) s += " ";
            s += element[i];
        }
    }
};
/*--
Font style run:
In CDX files, a font style is a 10-byte struct that consists of:
Bytes Type Contents
0-1 UINT16 A zero-based index to a font table. There must be at least one font table within an object or its container objects.
2-3 UINT16 Font type: 0x00 :  plain
0x01 :  bold
0x02 :  italic
0x04 :  underline
0x08 :  outline
0x10 :  shadow
0x20 :  subscript
0x40 :  superscript
0x60 :  formula (style in which subscript or superscript style is selected appropriately depending on the formula label)

Note that the subscript, superscript, and formula styles are mutually exclusive. The other styles may be combined by OR-ing the type codes. For example, bold italic is 0x03 (= 0x01 | 0x02).

The outline and shadow styles are only useful on Macintosh computers.

4-7 CDXCoordinate Font size
8-9 UINT16 Font color

--*/
class _CDXFontStyle extends CDXDataType {
	/**
	 * 
	 * @param bytes
	 * @param prop
	 */
    public _CDXFontStyle(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        s = styleRunString(bytes, 0);
    }

    
};
/*--
CDX Font Table Data Type
CDXFontTable:
In CDX files, a the font table is a variable-length struct that consists of:
Bytes Type Contents
0-1 UINT16 A value indicating the originating platform. Currently limited to the values 0x0000 for Macintosh and 0x0001 for Windows.

2-3 UINT16 Style run count: The number of font listings to follow
n... struct For each font listing: 0-1 UINT16 The ID of this font. This must be unique, but need not be sequential
2-3 UINT16 The character set of this font. Acceptible values are shown as part of the discussion of the CDXML charset property
4-5 UINT16 The length of the name of this font
6...n Unformatted The actual name of this font


In CDXML files, this data type is represented by a fonttable object.

Examples: CDX: 00 02 00 03 00 E4 04 05   ........
00 41 72 69 61 6C 04 00   .Arial..
E4 04 0F 00 54 69 6D 65   ....Time
73 20 4E 65 77 20 52 6F   s New Ro
6D 61 6E                  man
CDXML: <fonttable>
<font id="3" charset="iso-8859-1" name="Arial"/>
<font id="4" charset="iso-8859-1" name="Times New Roman"/>
</fonttable>

PMR - font will be preliminary returned as a string:
font1;font2;...
where font=id/charset/name
--*/
class _CDXFontTable extends CDXDataType {
    byte[] bb;
    byte[] bf;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXFontTable(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int ftLength = bytes.length;
        LOG.debug("ftlength "+ftLength);
        bb = new byte[2];
        int nstart = 0;
        for (int i =0 ; i < 2; i++) {
            bb[i] = bytes[nstart + i];
        }
        nstart += 2;
        int platform = CDXUtil.getUINT16(bb);
        LOG.debug("Platform "+platform);
        for (int i = 0; i < 2; i++) {
            bb[i] = bytes[nstart + i];
        }
        nstart += 2;
        s = "";
        int nFont = CDXUtil.getUINT16(bb);
        LOG.debug("nFont "+nFont);
        for (int j = 0; j < nFont; j++) {
            if (j > 0) {
                s += ";";
            }
            for (int i = 0; i < 2; i++) {
                bb[i] = bytes[nstart + i];
            }
            int id = CDXUtil.getUINT16(bb);
            s += id+"/";
            LOG.debug("id "+id);
            nstart += 2;
            for (int i = 0; i < 2; i++) {
                bb[i] = bytes[nstart + i];
            }
            int charset = CDXUtil.getUINT16(bb);
            s += charset+"/";
            LOG.debug("charset "+charset);
            nstart += 2;
            for (int i = 0; i < 2; i++) {
                bb[i] = bytes[nstart + i];
            }
            int fontlen = CDXUtil.getUINT16(bb);
            LOG.debug("fontlen "+fontlen);
            nstart += 2;
            bf = new byte[fontlen];
            for (int i = 0; i < fontlen; i++) {
                bf[i] = bytes[nstart + i];
            }
            String font = "";
            try {
                font = CDXUtil.getAsciiString(bf, 0);
                LOG.debug("font "+font);
            } catch (Exception e) {
                LOG.warn("Bad font: "+e);
            }
            nstart += fontlen;
            s += font;
        }
    }
};
class _CDXObjectID extends CDXDataType {
    long u32;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXObjectID(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        u32 = CDXUtil.getUINT32(bytes);
        s = "" + u32;
    }
};
class _CDXObjectIDArray extends CDXDataType {
    long[] idArray;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXObjectIDArray(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        if (bytes.length % 4 != 0) {
            throw new RuntimeException("CDXObjectIDArray must use 4-byte ids: "+bytes.length);
        }
        int idCount = bytes.length/4;
        idArray = new long[idCount];
        for (int i = 0; i < idCount; i++) {
            byte[] bb = new byte[4];
            for (int j = 0; j < 4; j++) {
                bb[j] = bytes[4*i + j];
            }
            idArray[i] = CDXUtil.getUINT32(bb);
        }
        s = "";
        for (int i = 0; i < idCount; i++) {
            if (i > 0) s += " ";
            s += idArray[i];
        }
    }
};
class _CDXObjectIDArrayWithCounts extends CDXDataType {
    long[] idArray;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXObjectIDArrayWithCounts(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int idCount = CDXUtil.getINT16(bytes[0], bytes[1]);
        if (bytes.length != idCount*4 + 2) {
            throw new RuntimeException("CDXObjectIDArrayWithCounts bad length "+bytes.length+"/"+idCount);
        }
//        int nBytes = 2;
        idArray = new long[idCount];
        for (int i = 0; i < idCount; i++) {
            byte[] bb = new byte[4];
            for (int j = 0; j < 4; j++) {
                bb[j] = bytes[4*i + j];
            }
            idArray[i] = CDXUtil.getUINT32(bb);
        }
        s = "";
        for (int i = 0; i < idCount; i++) {
            if (i > 0) s += " ";
            s += idArray[i];
        }
    }
};
class _CDXPoint2D extends CDXDataType {
    int ix2;
    int iy2;
    CDXPoint2D point;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXPoint2D(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int nBytes=0;
// note order!
        iy2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        ix2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        point = new CDXPoint2D(ix2, iy2);
        s = point.getAttributeValue();
    }
};
class _CDXPoint3D extends CDXDataType {
    int ix3;
    int iy3;
    int iz3;
    CDXPoint3D point;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _CDXPoint3D(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        if (bytes.length != 12) {
        	throw new RuntimeException("CDXPoint3D bad length: "+bytes.length);
        }
        int nBytes=0;
// note order!
        iz3 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        iy3 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        ix3 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        point = new CDXPoint3D(ix3, iy3, iz3);
        s = point.getAttributeValue();
    }
};
class _CDXRectangle extends CDXDataType {
    int itop2, ileft2, iright2, ibottom2;
    CDXRectangle rect;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _CDXRectangle(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int nBytes=0;
        itop2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        ileft2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        ibottom2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        iright2 = CDXUtil.getINT32(bytes[nBytes++],bytes[nBytes++],bytes[nBytes++],bytes[nBytes++]);
        rect = new CDXRectangle(ileft2, iright2, itop2, ibottom2);
        s = rect.getAttributeValue();
    }
};
/*--
RepresentsProperty Property
CDXML Name: RepresentsProperty
CDX Constant Name: kCDXProp_RepresentsProperty
CDX Constant Value: 0x000E
Data Size: CDXRepresentsProperty
Property of objects:

First written/read in: ChemDraw 4.0 / (not read)
Required? No

Description:


Indicates that this object represents some property in some other object.

If this property is absent:


This property is not read by ChemDraw. It is written as a courtesy only. There is no consequence to omitting it.

--*/
class _CDXRepresentsProperty extends CDXDataType {
	/**
	 * 
	 * @param bytes
	 * @param prop
	 */
    public _CDXRepresentsProperty(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        s = "[RepresentsProperty not parsed]";
    }
};
/*--
CDXString:
In CDX files, a CDXString is a variable-length struct that consists of:
Bytes Type Contents
0-1 UINT16 Style run count: The number of style runs for this text.
For each style run:
0-1 UINT16 The character at which this style starts
2-11 Style run A struct describing this style run


n... Text The string's text written out. The string's length is implicit and determined by subtracting the length of the first two items from the total length of the CDXText object


The first two bytes of a CDXString are a UINT16 indicating the number of font style runs. If the number of font style runs is zero, the string is taken to be ISO Latin 1 with no particular font or size specified. In some cases, this may imply the use of a default text style specified elsewhere. If the number of font style runs is not zero, it is followed by that many number of font style runs (see below), which is then followed by string text.

--*/
class _CDXString extends CDXDataType {
	/**
	 * 
	 * @param bytes
	 * @param prop
	 */
    public _CDXString(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int offset = 0;
        int nFontRuns = CDXUtil.getUINT16(bytes, offset);
        // I have found an example in objecttag where font runs is not mentioned
        // KLUDGE
        if (nFontRuns < 0 || nFontRuns > 100) {
        	LOG.warn("no font runs given");
        	s = new String(bytes);
        } else {
	        offset += 2;
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < nFontRuns; i++) {
	            int startChar = CDXUtil.getUINT16(bytes, offset);
	            offset += 2;
	// pack info temporarily into concatenated chars
	            sb.append(FLBRAK+startChar+" "+_CDXFontStyle.styleRunString(bytes, offset)+FRBRAK);
	            offset += 8;
	        }
	        sb.append(CDXUtil.getEscapedAsciiString(bytes, offset));
	        s = sb.toString();
        }
    }
};
/*--
FLOAT64:
Unsigned floating-point value of 64-bit size, corresponding to a double value.

In CDX files, byte order is Windows byte order (little-endian). Floating-point numbers are written in CDXML files as an alphanumeric string representing the number
FLOAT64
0.0
10.0
255.0
256.0
1000.0
100000.0
0.5
0.001
-123.45

0.0      00 00 00 00 00 00 00 00
10.0     00 00 00 00 00 00 24 40  .... .... 0010 0100 0100 0000
255.0    00 00 00 00 00 E0 6F 40  1110 0000 0110 1111 0100 0000
256.0    00 00 00 00 00 00 70 40  .... .... 0111 0000 0100 0000
1000.0   00 00 00 00 00 40 8F 40  0100 0000 1000 1111 0100 0000
100000.0 00 00 00 00 00 6A F8 40  0110 1010 1111 1000 0100 0000
0.5      00 00 00 00 00 00 E0 3F  .... .... 1110 0000 0011 1111
0.001    FC A9 F1 D2 4D 62 50 3F  0110 0010 0101 0000 0011 1111
-123.45  CD CC CC CC CC DC 5E C0


--*/
class _FLOAT64 extends CDXDataType {
    double f64;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _FLOAT64(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        f64 = CDXUtil.getFLOAT64(bytes, 0);
        s = CDXUtil.trimFloat(f64);
        num = new Double(f64);
    }
};
class _INT8 extends CDXDataType {
    int int8;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _INT8(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int8 = CDXUtil.getINT8(bytes[0]);
        s = "" + int8;
        num = new Integer(int8);
    }
};
class _INT16 extends CDXDataType {
    int int16;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _INT16(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int16 = CDXUtil.getINT16(bytes);
        s = "" + int16;
        num = new Integer(int16);
    }
};
class _INT16ListWithCounts extends CDXDataType {
    int[] int16List;
    /**
     * 
     * @param bytes
     * @param prop
     * @throws CDXException
     */
    public _INT16ListWithCounts(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int nBytes = 0;
        int nInt = CDXUtil.getUINT16(bytes[nBytes++], bytes[nBytes++]);
        if (nInt*2 + 2 != bytes.length) {
            LOG.error("INT16ListWithCounts bad length"+nInt+"/"+bytes.length);
        }
        int16List = new int[nInt];
        for (int i = 0; i < nInt; i++) {
            int16List[i] = CDXUtil.getUINT16(bytes[nBytes++], bytes[nBytes++]);
        }
        s = "";
        for (int i = 0; i < nInt; i++) {
            if (i > 0) s += " ";
            s += int16List[i];
        }
    }
};
class _INT32 extends CDXDataType {
    int int32;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _INT32(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        int32 = CDXUtil.getINT32(bytes);
        s = "" + int32;
        num = new Integer(int32);
    }
};
class _UINT8 extends CDXDataType {
    long u8;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _UINT8(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        u8 = CDXUtil.getUINT8(bytes[0]);
        s = "" + u8;
        num = new Long(u8);
    }
};
class _UINT16 extends CDXDataType {
    long u16;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _UINT16(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        u16 = CDXUtil.getUINT16(bytes);
        s = "" + u16;
        num = new Long(u16);
    }
};
class _UINT32 extends CDXDataType {
    long u32;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _UINT32(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        u32 = CDXUtil.getUINT32(bytes);
        s = "" + u32;
        num = new Long(u32);
    }
};
class _Unformatted extends CDXDataType {
    byte[] unformatted;
    /**
     * 
     * @param bytes
     * @param prop
     */
    public _Unformatted(byte[] bytes, CDXProperty prop) {
        super(bytes, prop);
        s = CDXUtil.getEscapedAsciiString(bytes, 0);
    }
};

