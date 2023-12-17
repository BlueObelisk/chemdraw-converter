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


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Node;
/**
 * Chemdraw page
 * currently only a container
 * @author pm286
 *
 */
public class CDXFontTable extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXFontTable.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x0100;
    public final static String NAME = "FontTable";
    public final static String CDXNAME = "fonttable";

/*--
  <fonttable>
   <font id="3" charset="1252" name="Arial"/>
  </fonttable>
--*/

    public CDXFontTable() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXFontTable(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXFontTable(CDXFontTable old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..fonttable.."+"]";
    }
};



