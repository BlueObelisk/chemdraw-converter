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


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
Bracket Attachment Object
CDXML Name: bracketattachment 
CDX Constant Name: kCDXObj_BracketAttachment 
CDX Constant Value: 0x8018 
Contained by objects: kCDXObj_BracketedGroup 
  
First written/read in: ChemDraw 7.0 (written only) 

Description:


An individual bracket (or brace or parenthesis) that, together with others, defines a Bracketed Group.

Each Bracket Attachment may contain zero or more Crossing Bonds, indicating which bonds cross from the inside to the outside of the bracket. Although the cdx specification places no limits on the number of Crossing Bonds associated with each Bracket Attachment, the presence of more than two Crossing Bonds can be ambiguous or unclear, and so it is recommended that two Crossing Bonds per Bracket Attachment be considered a practical maximum.

There are no required properties or objects.


Subobjects:
Value Name CDXML Name  
0x8019 kCDXObj_CrossingBond crossingbond 
 A Bond that connects a Bracketed Group to a Node outside that group. 


Properties:
Value Name CDXML Name Type 
 
n/a n/a id UINT16 
 A unique identifier for an object, used when other objects refer to it.  
 
0x0A2B kCDXProp_Bracket_GraphicID GraphicID CDXObjectID 
 The ID of a graphical object (bracket, brace, or parenthesis) associated with a Bracket Attachment.  


 * currently only a container
 * @author pm286
 *
 */
public class CDXBracketAttachment extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXBracketAttachment.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8018;
    public final static String NAME = "BracketAttachment";
    public final static String CDXNAME = "bracketattachment";

	public CDXBracketAttachment() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXBracketAttachment(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXBracketAttachment(CDXBracketAttachment old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..bracketattachment.."+"]";
    }
};



