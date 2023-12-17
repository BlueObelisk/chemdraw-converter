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
public class CDXObjectTag extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXObjectTag.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8011;
    public final static String NAME = "ObjectTag";
    public final static String CDXNAME = "objecttag";
    
    public CDXObjectTag() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXObjectTag(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXObjectTag(CDXObjectTag old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..objecttag.."+"]";
    }
};



