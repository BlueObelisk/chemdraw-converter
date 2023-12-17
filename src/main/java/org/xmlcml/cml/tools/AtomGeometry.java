package org.xmlcml.cml.tools;

import java.util.ArrayList;
import java.util.List;

public enum AtomGeometry {
	 DEFAULT(0),
	 ANY(1),
	 LINEAR(2),
	 TRIGONAL(3),
	 TETRAHEDRAL(4)
	 ;
	 private int intValue;
	 private static List<AtomGeometry> atomGeometryList;
	 static {
		 atomGeometryList = new ArrayList<AtomGeometry>();
		 for (AtomGeometry ag : AtomGeometry.values()) {
			 atomGeometryList.add(ag);
		 }
	 }
	 private AtomGeometry(int intValue) {
		 this.intValue = intValue;
	 }
	 public int getIntValue() {
		 return intValue;
	 }
	public static AtomGeometry getGeometry(int size) {
		return (size < 0 || size >= atomGeometryList.size()) ? null : atomGeometryList.get(size);
	}
	 
}
