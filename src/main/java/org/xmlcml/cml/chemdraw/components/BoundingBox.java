package org.xmlcml.cml.chemdraw.components;

import java.util.StringTokenizer;

public /**
 * 
 * @author pm286
 *
 */
class BoundingBox {
	public final static String TAG = "BoundingBox";
	
    double x0;
    double x1;
    double y0;
    double y1;

    /**
     */
    public BoundingBox() {
    }

    /**
     * 
     * @param bbox
     */
    public BoundingBox(BoundingBox bbox) {
        this(bbox.x0, bbox.x1, bbox.y0, bbox.y1);
    }

    /**
     * 
     * @param x0
     * @param x1
     * @param y0
     * @param y1
     */
    public BoundingBox(double x0, double x1, double y0, double y1) {
        setFloatValue(x0, x1, y0, y1);
    }

    private void setFloatValue(double x0, double x1, double y0, double y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    void setFloatValue(String rectS) throws IllegalArgumentException {
		StringTokenizer st = new StringTokenizer(rectS);
		if (st.countTokens() != 4) {
			throw new IllegalArgumentException("Rectangle string must have 4 elements: "+rectS);
		}
		try {
			x0 = new Double(st.nextToken()).doubleValue();
			y0 = new Double(st.nextToken()).doubleValue();
			x1 = new Double(st.nextToken()).doubleValue();
			y1 = new Double(st.nextToken()).doubleValue();
            setFloatValue(x0, x1, y0, y1);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Bad float in rectangle string: "+rectS);
		}
	}

//    /** gets index of longest axis.
//    * @return 1=X, 2=Y (X if equal)
//    */
//    private int getLongestAxisIndex() {
//        return ((x1 - x0) >= (y1 - y0)) ? 1 : 2;
//    }

//    /** gets length of longest axis.
//    * @param i 1 = X, 2 = Y
//    * @return length of axis
//    * @throws IllegalArgumentException
//    */
//    private double getAxisLength(int i) throws IllegalArgumentException {
//        if (i < 1 || i > 2) {
//            throw new IllegalArgumentException("Bad axis: "+i);
//        }
//        return (i == 1) ? (x1 - x0) : (y1 - y0);
//    }

//    /** transform.
//    *
//    *@param rotate180 rotate by 180deg (before translate)
//    *@param dx translate in x
//    *@param dy translate in y
//    */
//    private void transform(boolean rotate180, double dx, double dy) {
//        if (rotate180) {
//            double temp = x0;
//            x0 = x1;
//            x1 = temp;
//            temp = y0;
//            y0 = y1;
//            y1 = temp;
//        }
//        x0 += dx;
//        x1 += dx;
//        y0 += dy;
//        y1 += dy;
//    }

    /**
     * @return s
     */
    public String getString() {
        String s = "";
        s += "x1 = "+x0+", ";
        s += "x2 = "+x1+", ";
        s += "y1 = "+y0+", ";
        s += "y2 = "+y1;
        return s;
    }
};
