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
CDXPoint2D:
In CDX files, a CDXPoint2D is an x- and a y-CDXCoordinate stored as a pair
of INT32s, y coordinate followed by x coordinate.

In CDXML files, a CDXPoint2D is a stored as a pair of numeric values, x
coordinate followed by y coordinate. Note that this ordering is different
than in CDX files!

Example: 1 inch (72 points) to the right, and 2 inches down:
CDX: 00 00 90 00 00 00 48 00
CDXML: "72 144"

(PMR) The implicit scale is 2**16;
CDXML scale appears to be 2 units = 1 pt = 1/72 inch
--*/

public class CDXPoint2D implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXPoint2D.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	int x;
	int y;
    double xx;
    double yy;

    /**
     */
	public CDXPoint2D() {
	}

	/**
	 * 
	 * @param point
	 * @throws IllegalArgumentException
	 */
	public CDXPoint2D (String point) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(point);
		if (st.countTokens() != 2) {
			throw new IllegalArgumentException("Bad point string: "+point+"/");
		}
		try {
// swap X and Y
			int y0 = Integer.parseInt(st.nextToken());
			int x0 = Integer.parseInt(st.nextToken());
			init(x0, y0);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public CDXPoint2D (int x, int y) {
		init(x, y);
	}

	private void init(int x, int y) {
// suspect this really needs a bounding box calculation
		this.x = x;
		this.y = y;
// CDXML coords at present
        yy = ((double) y) / ((double) SCALE2D);
        xx = ((double) x) / ((double) SCALE2D);
// 2 decimal points (I think)
        xx = ((int) (10000. * xx)) / 10000.;
        yy = ((int) (10000. * yy)) / 10000.;
	}

	void setFloatValue(String point) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(point);
		if (st.countTokens() != 2) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
		try {
			xx = new Double(st.nextToken()).doubleValue();
			yy = new Double(st.nextToken()).doubleValue();
            x = (int) (xx / SCALE2D);
            y = (int) (yy / SCALE2D);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
	}

	String getAttributeValue() {
		return ""+CDXUtil.trimFloat(xx)+" "+CDXUtil.trimFloat(yy);
	}

	double getX2() {
        return xx;
    }

	double getY2() {
        return yy;
    }

	/**
	 * @return s
	 */
    public String toString() {
//        return ""+xx+"/"+yy;
        return ""+CDXUtil.trimFloat(xx)+"/"+CDXUtil.trimFloat(yy);
    }

};

