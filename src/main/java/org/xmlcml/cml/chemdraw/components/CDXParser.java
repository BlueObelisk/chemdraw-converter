/**
 * Copyright (C) 2001 Peter Murray-Rust (pm286@cam.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.cml.chemdraw.components;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**
 * @author P.Murray-Rust, 2001-2004
 **/

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
    public int getByteCount() {
		return byteCount;
	}


	private BlockManager blockManager;

//	private boolean misread = false;
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

	public void parseCDX(byte[] bytes) throws IOException {
		this.bytes = bytes;
		parseCDX();
	}

    /** read data from an input stream.
    * @param is the InputStream
    * @throws IOException
    * @throws CDXException
    */
	public void parseCDX(InputStream is) throws IOException {
		bytes = IOUtils.toByteArray(is);
		parseCDX();
	}

    /** read data from an input stream.
    * @param is the InputStream
    * @throws IOException
    * @throws CDXException
    */
	private void parseCDX() throws IOException {
        makeBlocks(bytes);
		objectStack = new Stack<CDXObject>();
		emptyStack = false;
    	parseBlocks();
    }



	private void makeBlocks(byte[] bytes) {
        blockManager = new BlockManager();
        blockManager.setBytes(bytes);
    }

    private void parseBlocks() {
    	LOG.debug("parseBlocks");
// new containing object
		parsedObject = new CDXList();
		parsedObject.setParser(this);
        byteCount = 0;
        lastHeader = 0;

// there is no indication of the lengtn of the buffer, so continue till no more objects are found
// I think this is 00 00 (null object)
//        readall:
		while (true) {
// find next VjCD0100
            if (!readHeader()) {
                break;
            }
            LOG.debug("Header: "+byteCount+"/"+Integer.toHexString(byteCount));
// no explicit CDXML object so create one
			startElement(new CDXML(), 0, null);
//			misread = false;
			depth = 0;
			while (byteCount < bytes.length) {
                // since there is no explicit "startNewObject" we read byte stream
                // and decide whether the next bytes signal an object or property
				try {
					readPropertyOrObject();
				} catch (ArrayIndexOutOfBoundsException aioobe) {
					LOG.error("Array problem: "+aioobe);
				}
                if (byteCount == bytes.length) {
                    LOG.debug("Reached end? ");
                    break;
                }
			}
            endElement();
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
            	LOG.trace("ran off end: "+byteCount);
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
                    LOG.trace("non-zero byte ("+b+") in CDX header (16 zeros expected) at: "+byteCount+"/"+Integer.toHexString(byteCount));
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
//			misread = true;
			emptyStack = true;
		} else {
			parsedObject = (CDXObject) objectStack.pop();
		}
	}

	private void readPropertyOrObject() {
		LOG.trace("=== READ PROPERTY OR OBJECT ==");
		if (depth > MAXDEPTH) {
			LOG.error("Excessive depth - probable misread; attempt recovery");
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
		LOG.debug("processProperty");
		int iProp = CDXUtil.getUINT16(bb);
		String propS = "" + iProp;
		boolean unknown = false;
		CDXProperty prop = CDXProperty.createProperty(propS);
		LOG.debug("PROP: "+propS+" "+"("+CDXUtil.toXHex(iProp)+")"+((prop == null) ? null : prop.getFullName()));
		if (prop == null) {
			LOG.error(
					"UNKNOWN PROP : "+propS+"("+CDXUtil.toXHex(iProp)+")"+
					") at byte "+byteCount+"/"+Integer.toHexString(byteCount)+" in "+bb.length);
			throw new ChemdrawRuntimeException("UNKNOWN PROP");

		} else {
            LOG.trace("PROPERTY ... "+prop.getCDXName());
		}
		if (prop != null) {
			prop.processAlias();
		}
		byte[] b = new byte[2];
		b[0] = bytes[byteCount++];
		b[1] = bytes[byteCount++];
		int length = CDXUtil.getUINT16(b);
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
                throw new RuntimeException("Abort ChemDraw parsing");
            }
			bs[i] = bytes[byteCount++];
		}
		LOG.trace("BYTE "+byteCount+" ("+CDXUtil.toXHex(byteCount)+")");
		if (prop == null) {
			return;
		}
		LOG.trace("Reading Property: "+prop.getCDXName()+"/"+Integer.toHexString(iProp));
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
			byteCount = lastHeader+16;
			throw new RuntimeException("Cannot recover from misparse");
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.error("Premature EOF? "+byteCount+" recover to next header");
			byteCount = lastHeader+16;
			throw new RuntimeException("Premature EOF?");
		} catch (Exception cde) {
			LOG.error("misread? "+value+"/"+propS+"("+Integer.toHexString(iProp)+") /"+
					prop.getCDXName()+"/"+cde+" at byteCount: "+byteCount+"; recover to next header");
			byteCount = lastHeader+16;
			throw new RuntimeException("Cannot recover from misparse");
		}
		if (unknown) {
			LOG.trace("Read unknown Property: "+prop.getCDXName());
		}
		LOG.trace("ByteCount "+byteCount);
	}

	private void processObject(byte[] bb) {
		LOG.debug("processObject");
//		MYFINE = Level.INFO;
		int iObj = CDXUtil.getUINT16(bb);
		CDXObject obj = CDXObject.newCDXObject(iObj);
		if (obj == null) {
			LOG.error("UNKNOWN OBJ: "+iObj+" "+Integer.toHexString(iObj)+"; try recovery to next header");
//			misread = true;
			byteCount = lastHeader+16;
		} else if (obj.codeName.equals("unknown")) {
			LOG.trace("UNKNOWN: "+iObj);
		} else {
		}

        // get the ID
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = bytes[byteCount++];
		}
		if (obj == null) return;
		int id = (int) CDXUtil.getUINT32(b);
		startElement(obj, id, b);
		if (parsedObject.codeName.cdxName.equals("unknown")) {
			LOG.trace("UNKN "+iObj);
		}
		LOG.debug("Element: "+parsedObject.codeName.cdxName);
		readPropertyOrObject();
	}


	public CDXObject getParsedObject() {
		return parsedObject;
	}

};
