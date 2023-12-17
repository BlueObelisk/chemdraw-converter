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

import nu.xom.Element;
/**
 * Chemdraw page
 * currently only a container
 * @author pm286
 *
 */
public class CDXCurve extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXCurve.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8008;
    public final static String NAME = "Curve";
    public final static String CDXNAME = "curve";
/*--
--*/

    public CDXCurve() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Element
     */
    public Element copy() {
        return new CDXCurve(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXCurve(CDXCurve old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..curve.."+"]";
    }
};



