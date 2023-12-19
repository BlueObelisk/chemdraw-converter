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

import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**--
CDXRectangle:
In CDX files, rectangles are stored as four CDXCoordinate values,
representing, in order: top, left, bottom, and right edges of the rectangle.

In CDXML files, rectangles are stored as four CDXCoordinate values,
representing, in order: left, top, right, and bottom edges of the rectangle.
Note that this ordering is different than in CDX files!

Example: top: 1 inch, left: 2 inches, bottom: 3 inches, right: 4 inches:
CDX: 00 00 48 00 00 00 90 00 00 00 D8 00 00 00 20 01
CDXML: "144 72 288 216"

(PMR) The implict scale is 2**16;
CDXML scale appears to be 2 units = 1 pt = 1/72 inch

PMR - rectangles can have left &gt; right. top &gt; bottom(e.g. for backwards arrows)
Normalised rectangles always have left &lt;= right; top &lt;= bottom
--*/

public class CDXRectangle implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXRectangle.class);
	static {
		LOG.setLevel(Level.INFO);
	}

/** directions for neighbouring objects */
	public final static int ABOVE = 1;
	/** */
	public final static int BELOW = 2;
	/** */
	public final static int VERTICAL = ABOVE + BELOW;
	/** */
	public final static int LEFT = 4;
	/** */
	public final static int RIGHT = 8;
	/** */
	public final static int HORIZONTAL = LEFT + RIGHT;
	/** */
	public final static int ANYWHERE = VERTICAL + HORIZONTAL;

	int x0;
	int x1;
	int y0;
	int y1;
	double xx0;
	double xx1;
	double yy0;
	double yy1;

	/**
	 */
	public CDXRectangle() {
	}

	/**
	 * 
	 * @param rectS
	 * @throws IllegalArgumentException
	 */
	public CDXRectangle (String rectS) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(rectS);
		if (st.countTokens() != 4) {
			throw new IllegalArgumentException("Bad rect string: "+rectS);
		}
		try {
			int y0 = Integer.parseInt(st.nextToken());
			int x0 = Integer.parseInt(st.nextToken());
			int y1 = Integer.parseInt(st.nextToken());
			int x1 = Integer.parseInt(st.nextToken());
			init(x0, x1, y0, y1);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad rect string: "+rectS);
		}
	}

	/**
	 * 
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 */
	public CDXRectangle (int x0, int x1, int y0, int y1) {
		init(x0, x1, y0, y1);
	}

	private void init(int x0a, int x1a, int y0a, int y1a) {

		this.x0 = x0a;
		this.x1 = x1a;
		this.y0 = y0a;
		this.y1 = y1a;
// CDXML coords not yet screen coords
        yy0 = ((double) y0) / ((double) SCALE2D);
        yy1 = ((double) y1) / ((double) SCALE2D);
        xx0 = ((double) x0) / ((double) SCALE2D);
        xx1 = ((double) x1) / ((double) SCALE2D);
// 2 decimal points (I think)
        xx0 = ((int) (10000. * xx0)) / 10000.;
        xx1 = ((int) (10000. * xx1)) / 10000.;
        yy0 = ((int) (10000. * yy0)) / 10000.;
        yy1 = ((int) (10000. * yy1)) / 10000.;
	}

	String getAttributeValue() {
		return ""
            +CDXUtil.trimFloat(xx0)+" "+CDXUtil.trimFloat(yy0)+" "
            +CDXUtil.trimFloat(xx1)+" "+CDXUtil.trimFloat(yy1);
	}

	/**
	 * @return s
	 */
    public String toString() {
        return ""+CDXUtil.trimFloat(xx0)+"/"+CDXUtil.trimFloat(yy0)+","+CDXUtil.trimFloat(xx1)+"/"+CDXUtil.trimFloat(yy1);
    }

 };

