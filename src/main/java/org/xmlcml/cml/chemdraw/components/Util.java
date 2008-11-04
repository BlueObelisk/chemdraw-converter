package org.xmlcml.cml.chemdraw.components; 

import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;


// these are a bit clunky since I was not sure what the formats are
// It should be possible to use the little-endian architecture
/**
 * 
 */
public class Util implements CDXConstants {
	private static Logger LOG = Logger.getLogger(Util.class);
	/**
	 * @param b
	 * @return int
	 */
	public static int getUINT8(byte b) {
		return (b < 0) ? (int)b + 0xf00: (int) b;
	}

    // not checked
	/**
	 * @param l
	 * @return byte
	 * @throws IllegalArgumentException
	 */
	public static byte setUINT8(int l) throws IllegalArgumentException {
        byte bb = (byte) ((l > 0x7F) ? l - 0x100 : l);
		return bb;
	}
    
	/**
	 * @param b
	 * @return int
	 */
	public static int getINT8(byte b) {
		return (int) b;
	}

    // not checked
	/**
	 * @param l
	 * @return byte
	 * @throws IllegalArgumentException
	 */
	public static byte setINT8(int l) throws IllegalArgumentException {
        byte bb = (byte) (l % 0x100);
		return bb;
	}
    
	/**
	 * @param b
	 * @return int
	 * @throws IllegalArgumentException
	 */
	public static int getUINT16(byte[] b) throws IllegalArgumentException {
		return getUINT16(b, 0);
	}

	/**
	 * @param b
	 * @param offset
	 * @return int
	 * @throws IllegalArgumentException
	 */
	public static int getUINT16(byte[] b, int offset) throws IllegalArgumentException {
        // actual length might differ from that declared
		if (b.length == offset+1) {
            return getUINT8(b[offset]);
//		} else if (b.length != offset+2) {
//			throw new IllegalArgumentException("getUINT16 arg length: "+b.length+"/"+offset);
		} else {
            int ii = 0;
            for (int i = 1+offset; i >= offset; i--) {
                ii *= 256;
                int bb = (b[i] < 0x00) ? (int)b[i] + 0x100 : (int) b[i];
                ii += bb;
            }
            return ii;
        }
	}

    // not checked
	/**
	 * @param l
	 * @return byte
	 * @throws IllegalArgumentException
	 */
	public static byte[] setUINT16(int l) throws IllegalArgumentException {
        byte bb[] = new byte[2];
        int i = l / 0x100;
        bb[1] = (byte) ((i > 0x7F) ? i - 0x100 : i);
        l = l % 0x100;
        bb[0] = (byte) ((l > 0x7F) ? l - 0x100 : l);
		return bb;
	}
    
	static int getINT16(byte[] b) {
		return getINT16(b, 0);
	}

	static int getINT16(byte[] b, int offset) {
		int sh = getUINT16(b, offset);
		return (sh > 0x8000) ? sh - 0xffff - 1 : sh;
	}

	static int getUINT16(byte b0, byte b1) {
		byte[] bb = new byte[2];
		bb[0] = b0;
		bb[1] = b1;
		return getUINT16(bb);
	}

	static int getINT16(byte b0, byte b1) {
		byte[] bb = new byte[2];
		bb[0] = b0;
		bb[1] = b1;
		return getINT16(bb);
	}

	static long getUINT32(byte[] b, int offset) throws IllegalArgumentException {
        // actual length might differ from that declared
        if (b.length == 1+offset) {
            return getUINT8(b[offset]);
        } else if(b.length == 2+offset) {
            return getUINT16(b, offset);
		} else if (b.length < 4+offset) {
			throw new IllegalArgumentException("getUINT32 arg length: "+b.length+"/"+offset);
		} else {
            int[] bb = new int[4];
            for (int i = 0; i < 4; i++) {
                byte bbx = b[i+offset];
                bb[i] = (bbx < 0x00) ? (int)(bbx + (int)0x100) : (int) bbx;
            }
            long ii = bb[0] + 0x100 * bb[1] + 0x10000 * bb[2] + 0x1000000 * bb[3];
            return ii;
        }
	}

    // not checked
	static byte[] setUINT32(long l) throws IllegalArgumentException {
        byte bb[] = new byte[4];
        long i = l / 0x1000000;
        bb[3] = (byte) ((i > 0x7F) ? i - 0x100 : i);
        l = l % 0x1000000;
        i = l / 0x10000;
        bb[2] = (byte) ((i > 0x7F) ? i - 0x100 : i);
        l = l % 0x10000;
        i = l / 0x100;
        bb[1] = (byte) ((i > 0x7F) ? i - 0x100 : i);
        l = l % 0x100;
        bb[0] = (byte) ((l > 0x7F) ? l - 0x100 : l);
		return bb;
	}
    
	static long getUINT32(byte[] b) throws IllegalArgumentException {
		return getUINT32(b, 0);
	}


	static int getINT32(byte[] b, int offset) {
		long sh = getUINT32(b, offset);
		return (sh > 0x80000000) ? (int)(sh - 0x100000000L) : (int)sh;
	}
	static int getINT32(byte[] b) {
		return getINT32(b, 0);
	}

	static int getINT32(byte b0, byte b1, byte b2, byte b3) {
		byte[] bb = new byte[4];
		bb[0] = b0;
		bb[1] = b1;
		bb[2] = b2;
		bb[3] = b3;
		return getINT32(bb);
	}

    static double getFLOAT64(byte[] bytes, int offset ) {
        long accum = 0;
        for (int shiftBy = 0; shiftBy < 64; shiftBy += 8 ) {
        // must cast to long or shift done modulo 32
            accum |= ( (long)( bytes[offset++] & 0xff ) ) << shiftBy;
        }
        return Double.longBitsToDouble(accum);
    }
/*--
FLOAT64:
Unsigned floating-point value of 64-bit size, corresponding to a double value.

In CDX files, byte order is Windows byte order (little-endian). Floating-point numbers are written in CDXML files as an alphanumeric string representing the number
FLOAT64 

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
    static byte[] setFLOAT64(double d) {
        long l = Double.doubleToLongBits(d);
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (l % 0x100);
            l >>= 8;
        }
        return b;
    }
    
    // subset of chars below 127 and escaped above
    static String getEscapedAsciiString(byte[] bytes, int offset) {
        StringBuffer sb = new StringBuffer();
        int ff00 = Integer.parseInt("ff00", 16);
        for (int i = offset; i < bytes.length; i++) {
            char ch = (char) bytes[i];
            if (
                Character.isWhitespace(ch) || 
                (ch > 32 && ch < 127)
//                Character.isDigit(ch) || 
//                Character.isLetter(ch) || 
//                ch == '-' ||
//                ch == '_' ||
//                ch == '.'
                ) {
                sb.append(ch);
            } else if ((int)ch > ff00) {
            	// 128-255 seem to be prepended by ff
            	ch -= ff00;
                sb.append(LESCAPE+(int)ch+RESCAPE);
            } else {
                sb.append(LESCAPE+(int)ch+RESCAPE);
            }
        }
        return sb.toString();
    }
        
    // subset of chars below 127 - omit others
    static String getAsciiString(byte[] bytes, int offset) {
        StringBuffer sb = new StringBuffer();
        for (int i = offset; i < bytes.length; i++) {
            char ch = (char) bytes[i];
            // make bytes positive
//            boolean ok = false;
            if (ch < 0) ch += 0x100;
            if (
                Character.isWhitespace(ch) || 
                (ch > 32 && ch < 127)
//                Character.isDigit(ch) || 
//                Character.isLetter(ch) || 
//                ch == '-' ||
//                ch == '_' ||
//                ch == '.'
                ) {
                sb.append(ch);
            } else {
            }
        }
        return sb.toString();
    }
        
//    private static String escapeXML(String s) {
//        String ss = null;
//        if (s != null) {
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < s.length(); i++) {
//                char c = s.charAt(i);
//                if (c == '"') {
//                    sb.append("&quot;");
//                } else if (c == '&') {
//                    sb.append("&amp;");
//                } else if (c == '<') {
//                    sb.append("&lt;");
//                } else {
//                    sb.append(c);
//                }
//            }
//            ss = sb.toString();
//        }
//        return ss;
//	}

//    private static String error(String s) {
//		System.err.println(s);
//		return s;
//	}

    /** trims trailing zeros and points.
    */
	public static String trimFloat(double d) {
        StringBuffer sb = new StringBuffer();
        sb.append(d);
        int l = sb.length();
        if (sb.indexOf(".") != -1) {
            while (true) {
                l = sb.length();
                if (sb.charAt(l-1) == '0') {
                    sb.deleteCharAt(l-1);
                } else {
                    break;
                }
            }
            l = sb.length();
            if (sb.charAt(l-1) == '.') {
                sb.deleteCharAt(l-1);
            }
        }
        return sb.toString();
    }
    
	/**
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
        testFloat();
    }
    
    private static void testFloat() {
        double[] d = {
            0.0,
            10.0,
            255.0,
            256.0,
            1000.0,
            100000.0,
            0.5,
            0.001,
            -123.45,
        };
        for (int i = 0; i < d.length; i++) {
            byte[] b = Util.setFLOAT64(d[i]);
            String s = ""+d[i]+" => ";
            for (int j = 0; j < 8; j++) {
                String ss = Integer.toHexString(b[j]);
                s += " "+((ss.length() == 8) ? ss.substring(6,8) : ss);
            }
            double dd = Util.getFLOAT64(b, 0);
            LOG.trace(s+" => "+dd);
        }
    }
};


