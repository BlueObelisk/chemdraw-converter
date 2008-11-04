package org.xmlcml.cml.chemdraw;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.cml.chemdraw.components.CDXParser;

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
		LOG.setLevel(Level.INFO);
	}
	private CDXObject parsedObject;
	
	/**
     */
	public CDX2CDXML() {
	}
	
	/**
	 * @param is
	 * @throws IOException
	 * @throws CDXException
	 */
	public void parseCDX(InputStream is) throws Exception {
        CDXParser parser = new CDXParser();
		parser.parseCDX(is);
		parsedObject = parser.getParsedObject();
    }

	public CDXObject getCDXMLObject() {
		return parsedObject;
	}

};





