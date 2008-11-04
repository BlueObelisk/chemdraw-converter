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

PMR - rectangles can have left > right. top > bottom(e.g. for backwards arrows)
Normalised rectangles always have left <= right; top <= bottom
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

//	private void setFloatValue(String rectS) throws IllegalArgumentException {
//		StringTokenizer st = new StringTokenizer(rectS);
//		if (st.countTokens() != 4) {
//			throw new IllegalArgumentException("Bad rectangle string: "+rectS);
//		}
//		try {
//			xx0 = new Double(st.nextToken()).doubleValue();
//			yy0 = new Double(st.nextToken()).doubleValue();
//			xx1 = new Double(st.nextToken()).doubleValue();
//			yy1 = new Double(st.nextToken()).doubleValue();
//            x0 = (int) (xx0 / SCALE2D);
//            y0 = (int) (yy0 / SCALE2D);
//            x1 = (int) (xx1 / SCALE2D);
//            y1 = (int) (yy1 / SCALE2D);
//		} catch (NumberFormatException nfe) {
//			throw new IllegalArgumentException("Bad rectangle string: "+rectS);
//		}
//	}

	String getAttributeValue() {
		return ""
            +Util.trimFloat(xx0)+" "+Util.trimFloat(yy0)+" "
            +Util.trimFloat(xx1)+" "+Util.trimFloat(yy1);
	}

///** rectangle with x0 <= x1, y0 <= y1	*/
//	private CDXRectangle getNormalizedRectangle() {
//		return new CDXRectangle(
//			Math.min(x0, x1), Math.max(x0, x1),
//			Math.min(y0, y1), Math.max(y0, y1));
//	}

//	private CDXPoint2D getCentroid() {
//		return new CDXPoint2D((x0 + x1)/2, (y0 + y1)/2);
//	}

/* inside includes "on the edge" */
//	private boolean isInside(CDXPoint2D pt) {
//		CDXRectangle norm = getNormalizedRectangle();
//		return
//			pt.x >= norm.x0 && pt.x <= norm.x1 &&
//			pt.y >= norm.y0 && pt.y <= norm.y1;
//	}

/* includes "on the edge"; returns null if no intersection */
//	private CDXRectangle intersection(CDXRectangle r) {
//		CDXRectangle norm = getNormalizedRectangle();
//		CDXRectangle rNorm = r.getNormalizedRectangle();
//		int x0max = Math.max(norm.x0, rNorm.x0);
//		int x1min = Math.min(norm.x1, rNorm.x1);
//		int y0max = Math.max(norm.y0, rNorm.y0);
//		int y1min = Math.min(norm.y1, rNorm.y1);
//		if (x0max > x1min || y0max > y1min) return null;
//		return new CDXRectangle(x0max, x1min, y0max, y1min);
//	}

/* is another Rectangle in this direction? if intersects()
return false. Direction can be combinations (ANYWHERE returns
true if not intersects()) */
//	private boolean isInDirection(CDXRectangle r, int direction) {
//		CDXRectangle norm = getNormalizedRectangle();
//		CDXRectangle rNorm = r.getNormalizedRectangle();
//		if (norm.intersection(rNorm) != null) return false;
//		if ((direction & ANYWHERE) == ANYWHERE) {
//			LOG.warn("ANYWHERE NYI, sorry");
//			return false;
//		} else if ((direction & LEFT) == LEFT) {
////			double
//		}
//		return true;
//	}

/* distance to rectangle in given direction; either X or Y
(not cartesian). if they have common edges, returns 0; if greater
intersection returns -1. Negative distances represent
no complete rectangle in that direction*/
//	private int distance(CDXRectangle r, int direction) {
//		CDXRectangle norm = getNormalizedRectangle();
//		CDXRectangle rNorm = r.getNormalizedRectangle();
//		if (direction == LEFT) {
//			return norm.x0 - rNorm.x1;
//		} else if (direction == RIGHT) {
//			return rNorm.x0 - norm.x1;
//		} else if (direction == ABOVE) {
//			return norm.y0 - rNorm.y1;
//		} else if (direction == BELOW) {
//			return rNorm.y0 - norm.y1;
//		} else {
//			LOG.warn("Unknown direction");
//			return -1;
//		}
//	}

/* some bounding boxes are identical. We can do integer comparison */
//	private boolean equals(CDXRectangle r) {
//		CDXRectangle norm = getNormalizedRectangle();
//		CDXRectangle rNorm = r.getNormalizedRectangle();
//		return
//			rNorm.x0 == norm.x0 && rNorm.x1 == norm.x1 &&
//			rNorm.y0 == norm.y0 && rNorm.y1 == norm.y1;
//	}

	/**
	 * @return s
	 */
    public String toString() {
        return ""+Util.trimFloat(xx0)+"/"+Util.trimFloat(yy0)+","+Util.trimFloat(xx1)+"/"+Util.trimFloat(yy1);
    }

 };

