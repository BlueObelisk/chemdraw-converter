package org.xmlcml.cml.chemdraw.components;


import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.CDX2CDXML;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLMolecule;

/**
 * 
 * @author pm286
 *
 */
public class CDXBond extends CDXObject {

    static Logger LOG = Logger.getLogger(CDXBond.class);
    static {
    	LOG.setLevel(Level.INFO);
    }

    public final static int CODE = 0x8005;
    public final static String NAME = "Bond";
    public final static String CDXNAME = "b";

	public CDXBond() {
        super(CODE, NAME, CDXNAME);
	}

    /**
     * copy node .
     * @return Node
     */
    public Node copy() {
        return new CDXBond(this);
    }
    
    /**
     * copy constructor
     * @param old
     */
    public CDXBond(CDXBond old) {
    	super(old);
    }

//    private double width;

    private String getDisplay() {
        String s = this.getAttributeValue("Display");
        return (s == null) ? "" : s;
    }

//    private String getOrder() {
//        String s = this.getAttributeValue("Order");
//        return (s == null || s.equals("")) ? "1" : s;
//    }
//
//    private double getWidth() {
//        width = 0.6;
//        return width;
//    }

    
    protected void process2CML(CMLMolecule molecule) {
        /*-- display
        0 Solid Solid bond
        1 Dash Dashed bond
        2 Hash Hashed bond
        3 WedgedHashBegin Wedged hashed bond with the narrow end on the "begin" atom
        4 WedgedHashEnd Wedged hashed bond with the narrow end on the "end" atom
        5 Bold Bold bond
        6 WedgeBegin Wedged solid bond with the narrow end on the "begin" atom
        7 WedgeEnd Wedged solid bond with the narrow end on the "end" atom
        8 Wavy Wavy bond
        9 HollowWedgeBegin Wedged hollow bond with the narrow end on the "begin" atom
        10 HollowWedgeEnd Wedged hollow bond with the narrow end on the "end" atom
        11 WavyWedgeBegin Wedged wavy bond with the narrow end on the "begin" atom
        12 WavyWedgeEnd Wedged wavy bond with the narrow end on the "end" atom
        13 Dot Dotted bond
        14 DashDot Dashed-and-dotted bond
        --*/
        String display = this.getDisplay();

        CMLBond bond = null;
        String b = this.getAttributeValue("B");
        String e = this.getAttributeValue("E");
        if (b == null || e == null) {
        	LOG.error("Bond has no atoms: "+this.getAttributeValue("id"));
        	return;
        }
        String atomRef1 = "a"+b;
        String atomRef2 = "a"+e;
        LOG.debug("bond..."+" ("+atomRef1+"/"+atomRef2+")");
        // if narrow is at end, swap atoms
        String newDisplay = newWedgeBondDisplay(display);
        if (!newDisplay.equals(display)) {
            String temp = atomRef1;
            atomRef1 = atomRef2;
            atomRef2 = temp;
            display = newDisplay;
        }
        // sometimes bonds are declared before atoms.
        // create bond, and process atoms later...
        try {
        	CMLAtom atom1 = getOrCreateAndAddAtom(molecule, atomRef1);
        	CMLAtom atom2 = getOrCreateAndAddAtom(molecule, atomRef2);
            if (molecule.getBond(atom1, atom2) != null) {
            	LOG.warn(" duplicate bond: "+atomRef1+"/"+atomRef2);
            } else if (atomRef1.equals(atomRef2)) {
            	CMLUtil.debug(this, "CDXBOND");
            	LOG.error("identical atoms in bond: "+atomRef1);
            } else {
            	bond = new CMLBond(atom1, atom2);
                molecule.addBond(bond);
            }
        } catch (Exception ex){
        	ex.printStackTrace();
        }
        if (bond != null) {
            if (display.equals("WedgedHashBegin") ||
            		display.equals("WedgeBegin")) {
                CMLBondStereo bondStereo = new CMLBondStereo();
                bondStereo.setXMLContent((display.equals("WedgedHashBegin")) ?
                		CMLBond.HATCH : CMLBond.WEDGE);
                bond.appendChild(bondStereo);
            } else if (display.equals("Hash")) {
                CMLBondStereo bondStereo = new CMLBondStereo();
                bondStereo.setXMLContent(CMLBond.QUERY_HASH);
                bond.appendChild(bondStereo);
            } else if (display.equals("Bold")) {
                CMLBondStereo bondStereo = new CMLBondStereo();
                bondStereo.setXMLContent(CMLBond.QUERY_BOLD);
                bond.appendChild(bondStereo);
            }
            String order = this.getAttributeValue("Order");
            order = transformOrder(order);
            bond.setOrder(order);
            addCDXAttribute(bond, this, "EndAttach");
            if (this.query("*").size() > 0) {
            	throw new RuntimeException("Unexpected bond children");
            }
            this.copyAttributesTo(bond);
        }
    }
    
    private static String transformOrder(String order) {
    	String oo = "1";
    	if (order == null) {
    	} else if (order.equals(S_EMPTY)) {
    	} else if (order.equals("1.5")) {
    		oo = CMLBond.AROMATIC;
    	} else {
    		oo = order;
    	}
    	return oo;
    }
    
    private CMLAtom getOrCreateAndAddAtom(CMLMolecule molecule, String atomRef) {
    	CMLAtom atom = molecule.getAtomById(atomRef);
    	if (atom == null) {
    		LOG.debug("bond requires undeclared atom: "+atomRef);
    		atom = new CMLAtom(atomRef);
    		molecule.addAtom(atom);
    	}
    	return atom;
    }

/*
        1 Dash Dashed bond
        2 Hash Hashed bond
        3 WedgedHashBegin Wedged hashed bond with the narrow end on the "begin" atom
        4 WedgedHashEnd Wedged hashed bond with the narrow end on the "end" atom
        5 Bold Bold bond
        6 WedgeBegin Wedged solid bond with the narrow end on the "begin" atom
        7 WedgeEnd Wedged solid bond with the narrow end on the "end" atom
        8 Wavy Wavy bond
        9 HollowWedgeBegin Wedged hollow bond with the narrow end on the "begin" atom
        10 HollowWedgeEnd Wedged hollow bond with the narrow end on the "end" atom
        11 WavyWedgeBegin Wedged wavy bond with the narrow end on the "begin" atom
        12 WavyWedgeEnd Wedged wavy bond with the narrow end on the "end" atom
        13 Dot Dotted bond
        14 DashDot Dashed-and-dotted bond
 */
    private String newWedgeBondDisplay(String display) {
        String newDisplay = display;
        if (display.equals("WedgedHashEnd")) {
            newDisplay = "WedgedHashBegin";
//        } else if (display.equals("Hash")) {
//        	newDisplay = "Hash";
        } else if (display.equals("WedgeEnd")) {
            newDisplay = "WedgeBegin";
        } else if (display.equals("HollowWedgeEnd")) {
            newDisplay = "HollowWedgeBegin";
        } else if (display.equals("WavyWedgeEnd")) {
            newDisplay = "WavyWedgeBegin";
        } else if (display.equals("Bold")) {
        	newDisplay = "Bold";
        }
        return newDisplay;
    }
};



