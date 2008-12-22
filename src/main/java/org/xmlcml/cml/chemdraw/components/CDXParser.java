package org.xmlcml.cml.chemdraw.components;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**
This code is open source under the Artistic License
see http://www.opensource.org for conditions
@author P.Murray-Rust, 2001-2004
*/

public class CDXParser implements CDXConstants {

    private static Logger LOG = Logger.getLogger(CDXParser.class);
    static {
//    	LOG.setLevel(Level.DEBUG);
    	LOG.setLevel(Level.INFO);
    }

//  VjCD0100 - chemdraw signature
	private static byte[] HEADER = {
		0x56, 0x6A, 0x43, 0x44, 0x30, 0x31, 0x30, 0x30,
		0x04, 0x03, 0x02, 0x01,
	};

	private int depth;
	private byte[] bytes = new byte[0];
	private Stack<CDXObject> objectStack;
	private CDXObject parsedObject;
	int byteCount = 0;
    private BlockManager blockManager;

	private boolean misread = false;
	private boolean emptyStack = false;
	private int lastHeader;
	private static int BLOCKLEN = 256;
	private static int MAXTRIES = 3;

	/**
	 * @param chemDrawConverter
	 */
	public CDXParser() {
        init();
	}


	private void init() {
        CDXObject.makeObjects();
        CDXProperty.makeProperties();
	}

    /** read data from an input stream.
    * @param is the InputStream
    * @throws IOException
    * @throws CDXException
    */
	public void parseCDX(InputStream is) throws IOException {
        readBytes(is);
        int ntries = MAXTRIES;
        while (ntries > 0) {
            makeBlocks(bytes);
    		objectStack = new Stack<CDXObject>();
    		emptyStack = false;
        	parseBlocks();
        	if (misread) {
	        	if (ntries == 0) {
	        		LOG.error("Cannot recover after "+MAXTRIES+" tries");
	        		break;
	        	}
	        	kludgeByRemovingBytes();
	        	ntries--;
        	} else {
        		break;
        	}
        }
        LOG.warn("misread"+misread);
//        CMLUtil.debug(currentObject);
    }

	/** attempt to hack OLE structure by removing
	 * inserted blocks.
	 *
	 */
	private void kludgeByRemovingBytes() {
		LOG.debug("kludgeByRemovingBytes");
		// trim to start of "line"
		// find end of "good block"
		byteCount = (byteCount/BLOCKLEN + 1)*BLOCKLEN ;
		// skip 2 bad blocks
//		int pointer = byteCount+BLOCKLEN+BLOCKLEN;
		int pointer = byteCount+BLOCKLEN+BLOCKLEN;
		LOG.warn("...Skipped OLE block: "+byteCount+" to "+pointer);
		int uncopied = bytes.length - pointer;
		int newlength = byteCount + uncopied;
		byte[] newBytes = new byte[newlength]; 
		System.arraycopy(bytes, 0, newBytes, 0, byteCount);
		System.arraycopy(bytes, pointer, newBytes, byteCount, uncopied);
		bytes = newBytes;
        byteCount = 0;
	}


	private void readBytes(InputStream is) throws IOException {
        byte[] byteBuff = new byte[BYTESIZE];
		DataInputStream dis = new DataInputStream(is);

//		objectStack = new Stack<CDXObject>();
// read in all data first
		bytes = new byte[0];
		try {
			while (true) {
				int len = dis.read(byteBuff, 0, BYTESIZE);
				if (len == -1) break;
				int ll = bytes.length + len;
				byte[] temp = new byte[ll];
				System.arraycopy(bytes, 0, temp, 0, bytes.length);
				System.arraycopy(byteBuff, 0, temp, bytes.length, len);
				bytes = temp;
			}
            LOG.log(Level.INFO, "Read: "+bytes.length+" bytes");
            if (bytes.length % 64 == 0) {
                LOG.log(Level.DEBUG, "Read: "+bytes.length / 64 + " 64-bit blocks");
            } else {
//            	throw new RuntimeException("File should be multiple of 512 bytes, I think; found: "+bytes.length);
            	LOG.warn("File should be multiple of 512 bytes, I think; found: "+bytes.length);
            }
		} catch (EOFException eof) {
            LOG.error("Unexpected EOF");
		}
		dis.close();
    }
	
	@SuppressWarnings("unused")
	private void print(byte[] bytes) {
		int j = 0;
		int k = 0;
		print:
		while (true) {
			String s = "";
			for (int i = 0; i < 16; i++) {
				byte b = bytes[j++];
				int ii = b;
				if ( b < 0) {
					ii += 256;
				}
				String ss = Integer.toHexString(ii);
				if (ss.length() == 1) {
					ss = "0"+ss;
				}
				s += ss+" ";
				if (j %8 == 0) {
					s += " ";
				}
				if (j >= bytes.length) {
					break print;
				}
			}
			System.out.println(s);
			k++;
			if (k % 16 == 0) {
				System.out.println();
			}
		}
	}

	private void makeBlocks(byte[] bytes) {
        blockManager = new BlockManager();
        blockManager.setBytes(bytes);
    }

    private void parseBlocks() {
    	LOG.debug("parseBlocks");
// new containing object
		parsedObject = new CDXList();
//		currentObject = chemDrawConverter.rootCDXObject;
		parsedObject.setParser(this);
        byteCount = 0;
        misread = false;
        lastHeader = 0;

// there is no indication of the lengtn of the buffer, so continue till no more objects are found
// I think this is 00 00 (null object)
        readall:
		while (true) {
// find next VjCD0100
            if (!readHeader()) {
                break;
            }
            LOG.debug("Header: "+byteCount+"/"+Integer.toHexString(byteCount));
// no explicit CDXML object so create one
			startElement(new CDXML(), 0, null);
			misread = false;
			depth = 0;
			while (byteCount < bytes.length) {
                // since there is no explicit "startNewObject" we read byte stream
                // and decide whether the next bytes signal an object or property
				try {
					readPropertyOrObject();
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					LOG.error("Array problem: "+aioobe);
				}
				
				if (misread) {
					LOG.warn("misread...");
					break readall;
				}
                if (byteCount == bytes.length) {
                    LOG.debug("Reached end? ");
                    break;
                }
			}
            endElement();
//            CMLUtil.debug(currentObject);
		}
        if (emptyStack) {
        	misread = false;
        }
        if (misread) {
        	LOG.warn("Broke out..."+bytes.length);
        }
	}

    private boolean readHeader() {
    	LOG.debug("readHeader");
        boolean ok = true;
		byte currentByte;
        int headerCount = 0;
        while (true) {
// run off end ?
            if (byteCount >= bytes.length) {
            	LOG.warn("ran off end: "+byteCount);
                ok = false;
                break;
            }
            currentByte = bytes[byteCount++];
            byte headerByte = HEADER[headerCount++];
// read header
            if (currentByte == headerByte) {
                if (headerCount == HEADER.length) {
                    lastHeader = byteCount;
                    headerCount = 0;
                    break;
                }
            } else {
                headerCount = 0;
            }
        }
// read 16 zeros; may be version-dependent
// in fact version 8.0 seems to be 0 0 0 0  0 0 0 0  0 0 0 80  0 0 0 0
        int start = byteCount;
        for (int i = 0; i < 16; i++) {
            if (byteCount >= bytes.length) {
                ok = false;
                break;
            }
            byte b = bytes[byteCount++];
            if (b != 0 && !(b == -128 && byteCount - start == 12)) {
                    LOG.warn("non-zero byte ("+b+") in CDX header (16 zeros expected) at: "+byteCount+"/"+Integer.toHexString(byteCount));
//                    ok = false;
//                    break;
            }
        }
        if (byteCount != lastHeader) {
//            logger.log(Level.INFO, "Skipped: "+ (byteCount - saveCount)+"/"+ Integer.toHexString(byteCount - saveCount)+ " bytes; bytecount: "+byteCount+"/"+Integer.toHexString(byteCount));
        }
        return ok;
    }

    private CDXObject startElement(CDXObject object, int id, byte[] bytes) {
        LOG.debug("Start CDXElement "+byteCount+"/"+Integer.toHexString(byteCount));
// id is special
		object.setId(id);
        if (bytes != null) {
            CDXProperty prop = CDXProperty.createPropertyByCDXName("id");
            prop.setBytes(bytes);
            object.addProperty(prop);
        }
// manage hierarchy
		if (parsedObject != null) {
			objectStack.push(parsedObject);
			parsedObject.appendChild(object);
		} else {
			LOG.error("NULL obj BUG");
		}
		parsedObject = object;
		parsedObject.setParser(this);
		return parsedObject;
	}

	void endElement() {
		LOG.debug("endElement");
// any processing required at end of element? This may later be deferred to clean up
        parsedObject.endElement();
// manage hierarchy
		if (objectStack.isEmpty()) {
			LOG.debug("Empty stack...");
			misread = true;
			emptyStack = true;
		} else {
			parsedObject = (CDXObject) objectStack.pop();
		}
	}

	private void readPropertyOrObject() {
		LOG.trace("=== READ PROPERTY OR OBJECT ==");
// set by premature EOF
		if (misread) {
			return;
		}
		if (depth > MAXDEPTH) {
			LOG.error("Excessive depth - probable misread; attempt recovery");
			misread = true;
			byteCount = lastHeader+16;
			return;
		}
		depth++;
		byte[] b = new byte[2];
		b[0] = bytes[byteCount++];
		b[1] = bytes[byteCount++];
		if (false) {
// finished reading object
		} else if (b[0] == 0 && b[1] == 0) {
			endElement();
		} else if ((b[1] & 0x80) == 0) {
			processProperty(b);
		} else {
			processObject(b);
		}
		depth--;
	}

	private void processProperty(byte[] bb) {
		LOG.trace("processProperty");
		int iProp = Util.getUINT16(bb);
		String propS = "" + iProp;
		boolean unknown = false;
		CDXProperty prop = CDXProperty.createProperty(propS);
		if (prop == null) {
//			chemDrawConverter.LOG.error(
//					"UNKNOWN PROP (assume misread): "+propS+"(x"+Integer.toHexString(iProp)+
//					") at byte "+byteCount+"/"+Integer.toHexString(byteCount));
			LOG.error( 
					"UNKNOWN PROP (probably OLE block): "+propS+"(x"+Integer.toHexString(iProp)+
					") at byte "+byteCount+"/"+Integer.toHexString(byteCount));
//			misread = true;
			byteCount = lastHeader+16;
//			return;
		} else {
            LOG.trace("PROPERTY ... "+prop.getCDXName());
		}
		if (prop != null) {
			prop.processAlias();
		}
		byte[] b = new byte[2];
		b[0] = bytes[byteCount++];
		b[1] = bytes[byteCount++];
		int length = Util.getUINT16(b);
    	LOG.trace("Reading Property of length: "+length);
// sometimes length is zero, so skip the rest. This may be a misread but I don't know
		if (length == 0) {
			return;
		}
		byte[] bs = new byte[length];
		for (int i = 0; i < length; i++) {
            if (byteCount >= bytes.length) {
                LOG.error("?Premature EOF after "+byteCount+ "bytes; reading "+length);
                prop = null;
                break;
            }
			bs[i] = bytes[byteCount++];
		}
		LOG.trace("B "+byteCount);
		if (prop == null) {
			return;
		}
//		if (unknown) {
		LOG.trace("Reading Property: "+prop.getCDXName()+"/"+Integer.toHexString(iProp));
//		}
        String value = "";
		try {
			if ("objecttag".equals(parsedObject.codeName.cdxName)) {
				LOG.trace("objecttag");
			}
			value = prop.setBytes(bs);
			LOG.trace("VALUE "+value);
			parsedObject.addProperty(prop);
            prop.substituteValues();
		} catch (IllegalArgumentException iae) {
			LOG.error("misread? "+value+"/"+propS+"("+Integer.toHexString(iProp)+") /"+
					prop.getCDXName()+"/"+iae+" at byteCount: "+byteCount+"; recover to next header");
			misread = true;
			byteCount = lastHeader+16;
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("Premature EOF? "+byteCount+" recover to next header");
			misread = true;
			byteCount = lastHeader+16;
		} catch (Exception cde) {
			LOG.error("misread? "+value+"/"+propS+"("+Integer.toHexString(iProp)+") /"+
					prop.getCDXName()+"/"+cde+" at byteCount: "+byteCount+"; recover to next header");
			misread = true;
			byteCount = lastHeader+16;
		}
		if (unknown) {
			LOG.warn("Read unknown Property: "+prop.getCDXName());
		}
		LOG.trace("ByteCount "+byteCount);
	}

	private void processObject(byte[] bb) {
		LOG.debug("processObject");
//		MYFINE = Level.INFO;
		int iObj = Util.getUINT16(bb);
		CDXObject obj = CDXObject.newCDXObject(iObj);
		if (obj == null) {
			LOG.error("UNKNOWN OBJ: "+iObj+" "+Integer.toHexString(iObj)+"; try recovery to next header");
			misread = true;
			byteCount = lastHeader+16;
		} else if (obj.codeName.equals("unknown")) {
			LOG.warn("UNKNOWN: "+iObj);
		} else {
		}

        // get the ID
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = bytes[byteCount++];
		}
		if (obj == null) return;
		int id = (int) Util.getUINT32(b);
		startElement(obj, id, b);
		if (parsedObject.codeName.cdxName.equals("unknown")) {
			LOG.warn("UNKN "+iObj);
		}
		LOG.debug("Element: "+parsedObject.codeName.cdxName);
		readPropertyOrObject();
	}


	public CDXObject getParsedObject() {
		return parsedObject;
	}

};





