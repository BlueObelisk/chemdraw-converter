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
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.chemdraw.CDXML2CMLProcessor;
/**
 * Chemdraw page
 * currently only a container
 * @author pm286
 *
 */
public class CDXPage extends CDXObject {

	
    static Logger LOG = Logger.getLogger(CDXPage.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8001;
    public final static String NAME = "Page";
    public final static String CDXNAME = "page";

/*--
  <page 
  BoundingBox="0 0 538.507 785.107" 
  WidthPages="1" 
  HeightPages="1" 
  HeaderPosition="35.9999" 
  FooterPosition="35.9999"
  id="156">
--*/

	public CDXPage() {
        super(CODE, NAME, CDXNAME);
	}
    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXPage(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXPage(CDXObject old) {
    	super(old);
    }


    public void process2CML(CMLElement element) {
    	processContents(element);
    	
//    	Nodes groups = this.query("group");
//    	if (groups.size() > 0) {
//    		CMLList list = new CMLList();
//    		element.appendChild(list);
//    		LOG.info(" Found "+groups.size()+" flattened groups");
//    		for (int i = 0; i < groups.size(); i++) {
//    			CDXObject group = (CDXGroup) groups.get(i);
//        		group.process2CML(list);
//    		}
//    	} else {
//    		LOG.warn("No group children of page");
//    		processContents(element);
//    	}
    }
    
	/**
	 * @param element
	 * @throws RuntimeException
	 */
	void processGroups(CMLElement element) throws RuntimeException {
		Nodes nodes = this.query("./group");
		LOG.info("Group count: "+nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			CDXGroup group = (CDXGroup) nodes.get(i);
//			CMLList cmlList = new CMLList();
//			element.appendChild(cmlList);
			for (int j = 0; j < group.getChildElements().size(); j++) {
				CDXObject obj = (CDXObject) group.getChildElements().get(j);
				if (obj instanceof CDXPage) {
					LOG.warn("Page nested in group");
				} else if (obj instanceof CDXGroup) {
					LOG.warn("Group nested in group");
				}
				obj.process2CML(element);
				element.appendChild(obj);
//				}
			}
			group.detach();
		}
	}
	
	/**
	 * @param element
	 * @throws RuntimeException
	 */
	protected void processContents(CMLElement element) throws RuntimeException {
		// the order of these matters and depends on heuristics
		// as objects are found they are (usually) removed from the CDXObject
		// at the end nothing should be left
		
		// un-nest groups
		flattenTopLevelGroups(element);
		// groups are recursive containers for pages I think    	
	  	processGroups(element);
		// find any graphics    	
	  	processGraphics(element);
		// find any raw text (not in fragment    	
	  	processTexts(element);
		// these should be molecules, labels and fragments. Which is which is a mess
		// as they are processed they are removed from the CDXPage    	
    	processFragmentsContainingUnspecfiedNodes(element);
		// now fragments
    	processFragmentsContainingFragmentNodes(element);
		// now molecules    	
    	processMoleculeFragments(element);
		// collect molecules in list	  	
	  	CDXML2CMLProcessor.createMoleculeList(element);
		// move labels to molecules if possible    	
	  	CDXML2CMLProcessor.addLabelsToMolecules(element);
		// reactions depend on text and graphics having been processed
	  	processReactions(element);
	  	// should be nothing left except unknown or unprocessable elements
	  	processLeftOvers(element);
	}
	
	private void flattenTopLevelGroups(CMLElement element) {
    	Nodes groups = this.query("//*[not(self::group)]/group");
		LOG.info("Found "+groups.size()+" groups to flatten");
		for (int i = 0; i < groups.size(); i++) {
			CDXGroup group = (CDXGroup) groups.get(i);
    		group.flatten(this);
		}
//		this.debug("PAGE");
	}
	/**
	 * @return s
	 */
	public String getString() {
	    return "["+"..page.."+"]";
	}

};



