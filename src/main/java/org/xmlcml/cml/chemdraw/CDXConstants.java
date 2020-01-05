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
package org.xmlcml.cml.chemdraw; 

import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.chemdraw.components.BoundingBox;


/**
 * 
 * @author pm286
 *
 */
public interface CDXConstants extends CMLConstants {
// I think this is the default but it isn't clearly mentioned
	/** */
	float MAG = 8;
	/** */
	float SCALE2D = 65536;
	/** NO IDEA WHETHER THIS WORKS*/
	float SCALE3D = 65536;
	/** */
	float SCALE2DMAG = 65536 * MAG;

	/** */
    int BLOCKSIZE = 0x100;   

	/** */
    int NZEROS = 16;
	/** */
	int BYTESIZE = 1000000;
	/** */
	int MAXDEPTH = 10; // to avoid recursion in misreads

	/**	 */
	String CDX_PREFIX = "cdx";
	/**	 */
	String CDX_NAMESPACE = "http://www.xml-cml/namespaces/cdx";
	
	/** left bracket for fonts */
	String FLBRAK = "[[";
	/** right bracket for fonts */
	String FRBRAK = "]]";
	/** left escape for non-ASCII */
	String LESCAPE = "{{";
	/** right escape for non-ASCII */
	String RESCAPE = "}}";
	/** left escape in regex */
	String LESCAPEREGEX = "\\{\\{";
	/** right escape in regex */
	String RESCAPEREGEX = "\\}\\}";
	
	/** degree */
	String ESCAPE_DEGREE = LESCAPE+"176"+RESCAPE;
	/** degree in regex */
	String REGEX_DEGREE = LESCAPEREGEX+"176"+RESCAPEREGEX;
	/** */

	String TEMP_TEXT = "temp_Text";
	// these will be heuristic
	/** largest allowed atom label size */
	int MAX_ATOM_LABEL_FONT_SIZE = 5;
	/** smallest allowed atom label size */
	int MIN_ATOM_LABEL_FONT_SIZE = 5;
	/** largest allowed molecule label size */
	int MAX_MOLECULE_LABEL_FONT_SIZE = 13;
	/** smallest allowed molecule label size */
	int MIN_MOLECULE_LABEL_FONT_SIZE = 9;
	/** smallest allowed reaction label size */
	int MAX_REACTION_LABEL_FONT_SIZE = 9;
	/** smallest allowed reaction label size */
	int MIN_REACTION_LABEL_FONT_SIZE = 6;
	/** largest allowed difference between top of molBB and bottom of labelBB */
	int MAX_MOLECULE_TO_LABEL_YDELTA = 30;
	/** largest allowed difference between top of molBB and bottom of labelBB */
	int MIN_MOLECULE_TO_LABEL_YDELTA = -20;
	
	/** */
	String ATT_BOUNDING_BOX = BoundingBox.TAG;
	/** */
	String ATT_YDELTA = "ydelta";
	/** */
	String CDX_YDELTA = CDX_PREFIX+S_COLON+ATT_YDELTA;
	/** */
	String ATT_FONTSIZE = "size";
	/** */
	String ATT_POINT = "p";
}
