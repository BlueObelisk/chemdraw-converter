/* Copyright 2011 Peter Murray-Rust et. al.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.cml.tools;

import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Real2;

/**
 * Additional tools for atomset. Not fully developed.
 * 
 * @author pmr
 */
public class AtomSetTool {

	static final Logger LOG = Logger.getLogger(AtomSetTool.class.getName());

	/**
	 * find atom closest to point. uses 2D coordinates skips atoms without 2D
	 * coords
	 * 
	 * @param point
	 * @return atom or null
	 */
	public static CMLAtom getNearestAtom(CMLMolecule atomSet, Real2 point) {
		CMLAtom closestAtom = null;
		double maxDist = 999999.;
		List<CMLAtom> thisAtoms = atomSet.getAtoms();
		for (int i = 0; i < thisAtoms.size(); i++) {
			CMLAtom thisAtom = thisAtoms.get(i);
			Real2 thisXY2 = thisAtom.getXY2();
			if (thisXY2 != null) {
				double dist = thisXY2.getDistance(point);
				if (dist < maxDist) {
					maxDist = dist;
					closestAtom = thisAtom;
				}
			}
		}
		return closestAtom;
	}

}