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
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLReactionScheme;

import nu.xom.Node;
import nu.xom.Nodes;
/**
 * 
 * @author pm286
 *
 */
public class CDXReactionScheme extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXReactionScheme.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x800D;
    public final static String NAME = "ReactionScheme";
    public final static String CDXNAME = "scheme";

    public CDXReactionScheme() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXReactionScheme(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXReactionScheme(CDXReactionScheme old) {
    	super(old);
    }

    public void process2CML(CMLElement cmlNode) {
/*
- <scheme id="5925">
  <step ReactionStepReactants="5704" ReactionStepProducts="5817" ReactionStepArrows="5916" ReactionStepObjectsAboveArrow="5869" id="5926" /> 
  <step ReactionStepReactants="5817" ReactionStepProducts="5765" ReactionStepArrows="5917" ReactionStepObjectsAboveArrow="5903" id="5927" /> 
  </scheme>
 */
    	
    	Nodes steps = this.query("*[local-name()='"+CDXReactionStep.CDXNAME+"']");
    	if (steps.size() > 0) {
            CMLReactionScheme reactionScheme = new CMLReactionScheme();
            cmlNode.appendChild(reactionScheme);
            this.copyAttributesTo(reactionScheme);
            for (int i = 0; i < steps.size(); i++) {
            	((CDXReactionStep) steps.get(i)).process2CML(reactionScheme);
            }
    	}
    }

};



