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
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.chemdraw.CDX2CDXML;
import org.xmlcml.cml.chemdraw.CDXConstants;
import org.xmlcml.cml.element.CMLArray;
import org.xmlcml.euclid.RealArray;

/** tries to interpret CDX graphics
*/
/*--
The type of arrow object, which represents line, arrow, arc, rectangle, or orbital.

This is an enumerated property. Acceptible values are shown in the following list:
Value CDXML Name Description
0 NoHead NoHead
1 HalfHead HalfHead
2 FullHead FullHead
4 Resonance Resonance
8 Equilibrium Equilibrium
16 Hollow Hollow
32 RetroSynthetic RetroSynthetic

If this property is absent The arrow is treated as headless.
--*/

public class CDXGraphic extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXGraphic.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8007;
    public final static String NAME = "Graphic";
    public final static String CDXNAME = "graphic";

	private String arrowType = "";
	// may be used later
	String lineType = "";
	String graphicType = "";

	public CDXGraphic() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXGraphic(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXGraphic(CDXGraphic old) {
    	super(old);
    }


	private String getGraphicType() {
        String s = getAttributeValue("GraphicType");
        return (s == null) ? "" : s;
    }

	private String getArrowType() {
        arrowType = getAttributeValue("ArrowType");
        arrowType = (arrowType == null) ? S_EMPTY : arrowType;
        return arrowType;
    }

	private String getLineType() {
        String s = getAttributeValue("LineType");
        return (s == null) ? "" : s;
    }


    void createTypes() {
        graphicType = getGraphicType();
        arrowType = getArrowType();
        lineType = getLineType();
    }

    static int NDASH = 12;

    public void process2CML(CMLElement element) {
        getArrowType();
        if (arrowType.equals("FullHead") ||
            arrowType.equals("Equilibrium") ||
            arrowType.equals("Hollow") ||      // deprecated if used for Retro
            arrowType.equals("Retrosynthetic")
            ) {
        	
            CMLArray arrow = CDXArrow.createArrowArray(this);
            element.appendChild(arrow);
        } else {
        	LOG.debug("Unknown arrow: "+arrowType);
// there are not normally any children
        }
    }

};

