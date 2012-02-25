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

(PMR) The implict scale is 2**16;
CDXML scale appears to be 2 units = 1 pt = 1/72 inch
--*/
/**
CDXPoint3D: 
In CDX files, a CDXPoint3D is an x- and a y-CDXCoordinate stored as a pair of INT32s, 
z coordinate followed by y coordinate followed by x coordinate. 

In CDXML files, a CDXPoint2D is a stored as a pair of numeric values, x coordinate 
followed by y coordinate followed by z coordinate. Note that this ordering is different than in CDX files!

Example: 1 inch (72 points) to the right, 2 inches down, and 3 inches deep:
CDX: 00 00 d8 00 00 00 90 00 00 00 48 00 
CDXML: "72 144 216" 
 */

public class CDXPoint3D implements CDXConstants {

    static Logger LOG = Logger.getLogger(CDXPoint3D.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	int x;
	int y;
	int z;
    double xx;
    double yy;
    double zz;

    /**
     */
	public CDXPoint3D() {
	}

	/**
	 * 
	 * @param point
	 * @throws IllegalArgumentException
	 */
	public CDXPoint3D (String point) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(point);
		if (st.countTokens() != 3) {
			throw new IllegalArgumentException("Bad point string: "+point+"/");
		}
		try {
// swap X and Y and Z
			int z0 = Integer.parseInt(st.nextToken());
			int y0 = Integer.parseInt(st.nextToken());
			int x0 = Integer.parseInt(st.nextToken());
			init(x0, y0, z0);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public CDXPoint3D (int x, int y, int z) {
		init(x, y, z);
	}

	private void init(int x, int y, int z) {
// suspect this really needs a bounding box calculation
		this.x = x;
		this.y = y;
		this.z = z;
// CDXML coords at present
        zz = ((double) z) / ((double) SCALE3D);
        yy = ((double) y) / ((double) SCALE3D);
        xx = ((double) x) / ((double) SCALE3D);
// 2 decimal points (I think)
        xx = ((int) (100. * xx)) / 100.;
        yy = ((int) (100. * yy)) / 100.;
        zz = ((int) (100. * zz)) / 100.;
	}

	void setFloatValue(String point) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(point);
		if (st.countTokens() != 3) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
		try {
			xx = new Double(st.nextToken()).doubleValue();
			yy = new Double(st.nextToken()).doubleValue();
			zz = new Double(st.nextToken()).doubleValue();
            x = (int) (xx / SCALE3D);
            y = (int) (yy / SCALE3D);
            z = (int) (zz / SCALE3D);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad point string: "+point);
		}
	}

	String getAttributeValue() {
		return ""+CDXUtil.trimFloat(zz)+" "+CDXUtil.trimFloat(yy)+" "+CDXUtil.trimFloat(xx);
	}

	double getX3() {
        return xx;
    }

	double getY3() {
        return yy;
    }

	double getZ3() {
        return zz;
    }

	/**
	 * @return s
	 */
    public String toString() {
        return ""+CDXUtil.trimFloat(xx)+"/"+CDXUtil.trimFloat(yy)+"/"+CDXUtil.trimFloat(zz);
    }

};

