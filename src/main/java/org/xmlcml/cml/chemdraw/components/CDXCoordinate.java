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
import org.xmlcml.cml.chemdraw.CDXConstants;

/**--
CDXCoordinate:
Still experimental
--*/

public class CDXCoordinate implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXCoordinate.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	int x;
    double xx;

    /**
     * 
     *
     */
	public CDXCoordinate() {
	}

	/**
	 * @param point
	 * @throws IllegalArgumentException
	 */
	public CDXCoordinate (String point) throws IllegalArgumentException {
		try {
			int x0 = Integer.parseInt(point);
			init(x0);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad coordinate string: "+point);
		}
	}

	/**
	 * 
	 * @param x
	 */
	public CDXCoordinate (int x) {
		init(x);
	}

	private void init(int x) {
		this.x = x;
        xx = ((double) x) / ((double) SCALE2D);
// 2 decimal points (I think)
        xx = ((int) (10000. * xx)) / 10000.;
	}

	String getAttributeValue() {
		return ""+CDXUtil.trimFloat(xx);
	}

//    private double getX2() {
//        return xx;
//    }

	/**
	 * @return string
	 */
    public String toString() {
        return ""+CDXUtil.trimFloat(xx);
    }

};

