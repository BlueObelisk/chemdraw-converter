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
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDXConstants;

/**--
Geometry Object
CDXML Name: geometry 
CDX Constant Name: kCDXObj_Geometry 
CDX Constant Value: 0x8021 
Contained by objects: kCDXObj_Page 
  
First written/read in: ChemDraw 8.0 

Description:


A geometrical relationship between one or more objects. The type of the relationship is specified by the kCDXProp_GeometricFeature property, and the objects that specify the geometry are listed within the kCDXProp_BasisObjects property. It is acceptable (and quite common) for one Geometry object to be defined in terms of other Geometry objects, but circular dependencies are forbidden.

If present, an Object Tag with the name "deviation" will contain a graphic representation of the RMS deviation for Geometries with a kCDXProp_GeometricFeature equal to kCDXGeometricFeature_LineFromPoints, kCDXGeometricFeature_PlaneFromPoints, or kCDXGeometricFeature_PlaneFromPointLine.


Subobjects:
Value Name CDXML Name  
0x8011 kCDXObj_ObjectTag objecttag 
 Arbitrarily named property, one or more of which can be attached to any ChemDraw object. 


Properties:
Value Name CDXML Name Type 
 
n/a n/a id UINT16 
 A unique identifier for an object, used when other objects refer to it.  
 
0x0008 kCDXProp_Name Name CDXString 
 Required for objecttags. Name of an object.  
 
0x0301 kCDXProp_ForegroundColor color UINT16 
 The foreground color of an object represented as the two-based index into the object's color table.  
 
0x0805 kCDXProp_BondLength BondLength  CDXCoordinate 
 The default bond length.  
 
0x0807 kCDXProp_LineWidth LineWidth  CDXCoordinate 
 The default line width.  
 
0x081A kCDXProp_LabelStyleFont LabelFont INT16 
 The default font family for atom labels.  
 
0x081C kCDXProp_LabelStyleSize LabelSize INT16 
 The default font size for atom labels.  
 
0x081E kCDXProp_LabelStyleFace LabelFace INT16 
 The default font style for atom labels.  
 
0x0820 kCDXProp_LabelStyleColor LabelColor INT16 
 The default color for atom labels  
 
0x0B80 kCDXProp_GeometricFeature GeometricFeature INT8 
 The type of the geometrical feature (point, line, plane, etc.). This is an enumerated property.  
 
0x0B81 kCDXProp_RelationValue RelationValue FLOAT64 
 The numeric relationship (if any) among the basis objects used to define this object.  
 
0x0B82 kCDXProp_BasisObjects BasisObjects CDXObjectIDArray 
 Required for geometries and constraints. An ordered list of objects used to define this object.  
 
0x0B88 kCDXProp_PointIsDirected PointIsDirected CDXBooleanImplied 
 For a point based on a normal, signifies whether it is in a specific direction relative to the reference point.  


--*/

public class CDXGeometry  extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXGeometry.class);
	static {
		LOG.setLevel(Level.INFO);
	}

    public final static int CODE = 0x8021;
    public final static String NAME = "Geometry";
    public final static String CDXNAME = "geometry";

	/**
	 */
	public CDXGeometry() {
        super(CODE, NAME, CDXNAME);
	}

	protected void fixBugs() {
		/* should be 
          <geometry id="5931" BoundingBox="484.39 170.6 558.4 175.11" Z="310" 
          LineType="Dashed" FillType="None" ArrowheadHead="Full" ArrowheadType="Solid" 
          HeadSize="1000" ArrowheadCenterSize="875" ArrowheadWidth="250" Head3D="558.4 173.23 0" Tail3D="484.39 173.23 0" Center3D="839.84 220.23 0" MajorAxisEnd3D="913.86 220.23 0" MinorAxisEnd3D="839.84 274.73 0" Dipole="no" IgnoreWarnings="no" Visible="yes" /> 
		 */
		if (this.getAttributeValue("ArrowheadHead") != null ||
			this.getAttributeValue("ArrowheadType") != null ||
			this.getAttributeValue("ArrowheadCentreSize") != null) {
			CDXArrow arrow = new CDXArrow();
			CMLUtil.copyAttributes(this, arrow);
			this.getParent().replaceChild(this, arrow);
		}
	}


	/**
	 * @return s
	 */
//    public String toString() {
//        return ""+Util.trimFloat(xx0)+"/"+Util.trimFloat(yy0)+","+Util.trimFloat(xx1)+"/"+Util.trimFloat(yy1);
//    }

 };

