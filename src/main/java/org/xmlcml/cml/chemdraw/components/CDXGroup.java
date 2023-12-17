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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
/**
 * Chemdraw page
 * currently only a container
 * @author pm286
 *
 */
public class CDXGroup extends CDXObject {

	
    static Logger logger = Logger.getLogger(CDXGroup.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8002;
    public final static String NAME = "Group";
    public final static String CDXNAME = "group";

/*--
  <page 
  BoundingBox="0 0 538.507 785.107" 
  WidthPages="1" 
  HeightPages="1" 
  HeaderPosition="35.9999" 
  FooterPosition="35.9999"
  id="156">
--*/

	public CDXGroup() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXGroup(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXGroup(CDXGroup old) {
    	super(old);
    }

    /**
     * @return s
     */
    public String getString() {
        return "["+"..group.."+"]";
    }
    
	public void flatten(CDXObject ancestor) {
		Elements childElements = this.getChildElements();
		Attribute groupAttribute = createCombinedGroupAttribute();
		for (int j = 0; j < childElements.size(); j++) {
			Element childElement = childElements.get(j);
			childElement.addAttribute(new Attribute(groupAttribute));
			if (!(childElement instanceof CDXGroup)) {
				childElement.detach();
				ancestor.appendChild(childElement);
			} else {
				((CDXGroup)childElement).flatten(ancestor);
			}
		}
//		this.debug("GROUP");
//		this.detach();
	}
	
	private Attribute createCombinedGroupAttribute() {
		Attribute groupAttribute = this.getAttribute("group");
		String idValue = this.getAttributeValue("id");
		if (groupAttribute == null) {
			groupAttribute = new Attribute("group", idValue);
		} else {
			groupAttribute = new Attribute("group", groupAttribute.getValue()+" "+idValue);
		}
		return groupAttribute;
	}


	// concatenate all ids on ancestor groups
	private void addCombinedAttribute(Attribute groupAttribute) {
		String attValue = groupAttribute.getValue()+" "+this.getId();
		this.addAttribute(new Attribute("group", attValue));
	}
}



