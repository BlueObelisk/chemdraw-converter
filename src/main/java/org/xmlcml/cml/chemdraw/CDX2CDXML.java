package org.xmlcml.cml.chemdraw;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.cml.chemdraw.components.CDXParser;
import org.xmlcml.cml.chemdraw.components.ChemdrawRuntimeException;

/**
 * Converts a CDX file to a CDXML file.
 * relies on format on Chemdraw website.
 * some primitives (especially pure graphics) may not be
 * fully supported
 * 
This code is open source under the Artistic License
see http://www.opensource.org for conditions
@author P.Murray-Rust, 2001-2008
*/


public class CDX2CDXML {

	static Logger LOG = Logger.getLogger(CDX2CDXML.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final int ROWSIZE = 16;
	private static final int BLOCKSIZE = 8 * ROWSIZE;
	private CDXObject parsedObject;
	private CDXParser parser;
	
	/**
     */
	public CDX2CDXML() {
        init();
	}

	private void init() {
		parser = new CDXParser();
	}
	
	/**
	 * @param is
	 * @throws IOException
	 * @throws CDXException
	 */
	public void parseCDX(InputStream is) throws IOException {
		byte[] bytes = IOUtils.toByteArray(is);
		this.parseCDX(bytes);
    }
	
	public CDXParser getParser() {
		return parser;
	}

	public void parseCDX(byte[] bytes) {
		LOG.debug("bytes: "+bytes.length);
		parseAllowingForCorruptInput(bytes);
		parsedObject = parser.getParsedObject();
	}

	private void parseAllowingForCorruptInput(byte[] bytes) {
		boolean finished = false;
		int count = 12;
		while (!finished && count-- > 0) {
			try {
				parser.parseCDX(bytes);
				finished = true;
			} catch (ChemdrawRuntimeException cre) {
				bytes = exciseBlocksUntilNoLongerCorrupt(bytes, cre);
			} catch (Exception e) {
				throw new RuntimeException("cannot parse", e);
			}
		}
	}

	private byte[] exciseBlocksUntilNoLongerCorrupt(byte[] bytes,
			ChemdrawRuntimeException cre) {
		int byteCount = parser.getByteCount();
		byteCount = roundToBlock(byteCount, BLOCKSIZE);
//				debugBytesBothways("before", bytes, byteCount, BLOCKSIZE);
		// the 4 is empirical
		bytes = exciseBlock(bytes, byteCount, 4);
		System.err.println("Excised block");
//		debugBytesBothways("after", bytes, byteCount, 2*BLOCKSIZE);
//				throw new RuntimeException("finish");
		return bytes;
	}

	public CDXObject getCDXMLObject() {
		return parsedObject;
	}

//===================
	private int roundToBlock(int byteCount, int blocksize) {
		return (byteCount / blocksize) * blocksize;
	}
	private void debugBytesBothways(String msg, byte[] bytes, int byteCount, int deltaBytes) {
		System.out.println(">>>>>>>>>>"+msg+">>>>>>>>>>>>>>");
		System.out.println(debugBytesFoward(bytes, byteCount-deltaBytes, deltaBytes));
		System.out.println("====================================");
		System.out.println(debugBytesFoward(bytes, byteCount, deltaBytes));
		System.out.println("<<<<<<<<<<"+msg+"<<<<<<<<<<<<<");
	}
	private static String debugBytesFoward(byte[] bytes, int start, int deltaBytes) {
		StringBuilder sb = new StringBuilder();
		if (start % ROWSIZE != 0) {
			throw new RuntimeException("bad start "+start);
		}
		if (deltaBytes % ROWSIZE != 0) {
			throw new RuntimeException("bad deltaBytes "+start);
		}
		int nrows = deltaBytes / ROWSIZE;
		for (int irow = 0; irow < nrows; irow++) {
			byte[] rowBytes = copyBytes(bytes, start, ROWSIZE);
			sb.append(toHexString(start)+":  ");
			sb.append(toString(rowBytes));
			sb.append("\n");
			start += ROWSIZE;
		}
		return sb.toString();
	}
	private static String toString(byte[] rowByte) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rowByte.length; i++) {
			if (i > 0 && i % 8 == 0) {
				sb.append(CMLConstants.S_SPACE);
			}
			sb.append(toHexString(rowByte[i]));
			sb.append(CMLConstants.S_SPACE);
		}
		return sb.toString();
	}
	
	private static String toHexString(byte b) {
		String s = Integer.toHexString((int)b);
		StringBuilder sb = new StringBuilder(s);
		if (sb.length() == 8) {
			sb.delete(0, 6);
		} else if (sb.length() == 1) {
			sb.insert(0, '0');
		}
		return sb.toString();
	}
	
	private static String toHexString(int i) {
		String s = Integer.toHexString(i);
		StringBuilder sb = new StringBuilder(s);
		if (sb.length() == 8) {
			sb.delete(0, 4);
		} else if (sb.length() == 1) {
			sb.insert(0, "000");
		} else if (sb.length() == 2) {
			sb.insert(0, "00");
		} else if (sb.length() == 3) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}
	private static byte[] copyBytes(byte[] bytes, int start, int rowsize) {
		byte[] rowBytes = new byte[rowsize];
		for (int i = 0; i < rowsize; i++) {
			rowBytes[i] = bytes[start+i];
		}
		return rowBytes;
	}
	private static byte[] exciseBlock(byte[] bytes, int byteCount, int blocksToExcise) {
		int leftover = bytes.length % BLOCKSIZE;
		if (leftover != 0) {
			throw new RuntimeException("bytes not multiple of blocksize "+bytes.length+" / "+leftover);
		}
		int startBlock = BLOCKSIZE * (byteCount / BLOCKSIZE);
		if (bytes.length - startBlock < BLOCKSIZE) {
			throw new RuntimeException("Final block error: "+bytes.length+ " - " +startBlock +" < "+ BLOCKSIZE);
		}
		int second = startBlock + blocksToExcise * BLOCKSIZE;
		int newLength = bytes.length - blocksToExcise * BLOCKSIZE;
		byte[] newBytes = new byte[bytes.length - blocksToExcise * BLOCKSIZE];
		System.out.println("start "+startBlock+"/"+bytes.length+"/"+newBytes.length+"/"+newLength);
		System.arraycopy(bytes, 0, newBytes, 0, startBlock);
		System.arraycopy(bytes, second, newBytes, startBlock, bytes.length - second);
		System.out.println("======================================BYTES "+newBytes.length);
		return newBytes;
	}
	
	private static byte[] exciseBlock(byte[] bytes, int byteCount) {
		int leftover = bytes.length % BLOCKSIZE;
		if (leftover != 0) {
			throw new RuntimeException("bytes not multiple of blocksize "+bytes.length+" / "+leftover);
		}
		int startBlock = BLOCKSIZE * (byteCount / BLOCKSIZE);
		if (bytes.length - startBlock < BLOCKSIZE) {
			throw new RuntimeException("Final block error: "+bytes.length+ " - " +startBlock +" < "+ BLOCKSIZE);
		}
		int second = startBlock + BLOCKSIZE;
		byte[] newBytes = new byte[bytes.length - BLOCKSIZE];
		System.arraycopy(bytes, 0, newBytes, 0, startBlock);
		System.arraycopy(bytes, second, newBytes, startBlock, bytes.length - second);
		System.out.println("======================================BYTES "+newBytes.length);
		return newBytes;
	}
	
};





