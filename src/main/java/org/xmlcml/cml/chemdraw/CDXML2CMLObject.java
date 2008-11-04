package org.xmlcml.cml.chemdraw;

import static org.xmlcml.cml.base.CMLConstants.CMLXSD_ID;
import static org.xmlcml.cml.base.CMLConstants.CML_XPATH;
import static org.xmlcml.cml.chemdraw.CDXConstants.ATT_BOUNDING_BOX;
import static org.xmlcml.cml.chemdraw.CDXConstants.ATT_FONTSIZE;
import static org.xmlcml.cml.chemdraw.CDXConstants.ATT_POINT;
import static org.xmlcml.cml.chemdraw.CDXConstants.ATT_YDELTA;
import static org.xmlcml.cml.chemdraw.CDXConstants.CDX_NAMESPACE;
import static org.xmlcml.cml.chemdraw.CDXConstants.CDX_PREFIX;
import static org.xmlcml.cml.chemdraw.CDXConstants.CDX_YDELTA;
import static org.xmlcml.cml.chemdraw.CDXConstants.MAX_ATOM_LABEL_FONT_SIZE;
import static org.xmlcml.cml.chemdraw.CDXConstants.MAX_MOLECULE_LABEL_FONT_SIZE;
import static org.xmlcml.cml.chemdraw.CDXConstants.MAX_MOLECULE_TO_LABEL_YDELTA;
import static org.xmlcml.cml.chemdraw.CDXConstants.MIN_MOLECULE_LABEL_FONT_SIZE;
import static org.xmlcml.cml.chemdraw.CDXConstants.MIN_MOLECULE_TO_LABEL_YDELTA;
import static org.xmlcml.cml.chemdraw.CDXConstants.TEMP_TEXT;
import static org.xmlcml.euclid.EuclidConstants.S_EMPTY;
import static org.xmlcml.euclid.EuclidConstants.S_SPACE;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.base.CMLElement.CoordinateType;
import org.xmlcml.cml.chemdraw.components.CDXColorTable;
import org.xmlcml.cml.chemdraw.components.CDXFontTable;
import org.xmlcml.cml.chemdraw.components.CDXList;
import org.xmlcml.cml.chemdraw.components.CDXML;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.cml.chemdraw.components.CDXPage;
import org.xmlcml.cml.chemdraw.components.CDXText;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMoleculeList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.tools.AtomSetTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Util;

/**
 * attempts to convert a CDXML file for CML.
 * since CDX has many graphics primitives that CML does not some
 * graphics semantics may be lost. Similarly CDX does not support various chemical
 * semantics in CML and heuristics are used.
 * 
 * @author pm286
 *
 */
public class CDXML2CMLObject {

	final static Logger LOG = Logger.getLogger(CDXML2CMLObject.class);
	static {
		LOG.setLevel(Level.INFO);
	}
	
    final static double BOND_LENGTH = 2.0;
    private CDXObject rootCDXObject;
	private CDXList cdxList;
	private CDXObject page;
//	private CDXML cdxml;
    private CMLCml cmlCml = null;
    
    private boolean cleanMolecules = true;
    private boolean flatten = true;
    private boolean rescale = true;
    private boolean removeCDXAttributes = true;
    
    public boolean isRemoveCDXAttributes() {
		return removeCDXAttributes;
	}

	public void setRemoveCDXAttributes(boolean removeCDXAttributes) {
		this.removeCDXAttributes = removeCDXAttributes;
	}

	public CDXML2CMLObject() {
    	
    }
    
    public void convertParsedXMLToCML(Element cdxml) {
    	XMLToCDXMLConverter xml2cdxmlConverter = new XMLToCDXMLConverter();
        rootCDXObject = (CDXObject) xml2cdxmlConverter.convertToCDXObject(cdxml);
//   		 <cdxList id="null">
//   		 <CDXML CreationProgram="ChemDraw 7.0.1" Name="Microsoft Word - C:\Chemistry\_PHD\MyThesis\PHD_Thesis_JHarter2002_v22_5.doc" Magnification="666" WindowPosition="24.9749 51.4484" WindowSize="549.4499 370.1294" PrintMargins="28.3464 28.3464 28.3464 28.3464" ShowAtomQuery="true" ShowAtomStereo="false" ShowAtomNumber="false" ShowBondQuery="true" ShowBondStereo="false" LabelLineHeight="0" CaptionLineHeight="1" ChainAngle="7864320" BondLength="12.1992" BoldWidth="1.8141" LineWidth="0.8503" MarginWidth="1.25" HashSpacing="1.75" CaptionJustification="Left" LabelJustification="Auto" BondSpacing="200" BoundingBox="138.9999 60.9799 393.6269 106.9499" LabelFont="3" LabelFace="96" LabelSize="8" LabelColor="3" CaptionFont="3" CaptionFace="1" CaptionSize="6" CaptionColor="3" id="0">
//   		  <page BoundingBox="0 0 538.507 785.107" WidthPages="1" HeightPages="1" HeaderPosition="35.9999" FooterPosition="35.9999" id="156">
//   		   <fragment BoundingBox="138.9999 67.0011 214.1269 91.6999" id="19">

 		try {
			if (!(rootCDXObject instanceof CDXList)) {
 	        	 throw new RuntimeException("expected cdxList as root element");
 	        }
 	        cdxList = (CDXList) rootCDXObject;
 	        cdxList.setChemDrawConverterRecursively(this);
 	        cdxml = null;
 		 	if (cdxList.getChildElements().size() == 0) {
 		 		LOG.warn("cdxList has no children");
 		 	}
 		 	for (int j = 0; j < cdxList.getChildElements().size(); j++) {
	 		 	CDXObject obj = (CDXObject) cdxList.getChildElements().get(j);
	 		 	if (obj instanceof CDXML) {
	 		 		cdxml = (CDXML) cdxList.getChildElements().get(0);
	 		 	} else if ("object".equals(obj.getLocalName())) {
	 		 		LOG.error("*********** Uninterpreted object in cdxList");
	 		 	 } else {
	 		 		 throw new RuntimeException("unexpected child of cdxList: "+obj.getLocalName());
	 		 	 }
 	         }
 		 	 if (cdxml == null) {
 		 		LOG.error("Cannot find CDXML element");
 		 	 } else {
	 		 	 convertCDXML(cdxml);
 		 	 }
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new RuntimeException(e);
 		}
	}
    
    public CMLElement getCML() {
    	return cmlCml;
    }
    
	private void convertCDXML(Element cdxml) {
		int pageCount = 0;
		// check for errors and non-page structures
		 for (int i = 0; i < cdxml.getChildElements().size(); i++) {
			 CDXObject child = (CDXObject) cdxml.getChildElements().get(i);
			 if (child instanceof CDXColorTable) {
				 // skip
			 } else  if (child instanceof CDXFontTable) {
				 // skip
		     } else if (child instanceof CDXPage) {
		    	 if (pageCount > 0) {
		    		 throw new RuntimeException("Only one page allowed");
		    	 }
		    	 pageCount++;
		     } else if (child instanceof CDXML) {
		    	LOG.error("**************Cannot process nested CDXML");
			 } else  if (child.getLocalName().equals("object")) {
				LOG.error("Unexpected CDXML child 'object'");
		     } else {
				 throw new RuntimeException("Unexpected CDXML child: "+child.getLocalName());
		     }
		 }
		 // find a single page
		 page = null;
		 for (int i = 0; i < cdxml.getChildElements().size(); i++) {
			 CDXObject obj = (CDXObject) cdxml.getChildElements().get(i);
			 if (obj instanceof CDXPage) {
				 page = (CDXObject) cdxml.getChildElements().get(0);
			 } else if (obj instanceof CDXFontTable) {
				 // skip
			 } else if (obj instanceof CDXColorTable) {
				 // skip
			 } else {
				LOG.warn("Skipped non-page child of CDXML: "+obj);
			 }
		 }
		 if (page != null) {
		     tidyToCML();
		 } else {
//		 		 		 LOG.warn("EMPTY page");
		 }
	}
    
	private void tidyToCML() {
		// result is a <cml> object
		cmlCml = new CMLCml();
		cmlCml.addNamespaceDeclaration(CDX_PREFIX, CDX_NAMESPACE);
		expandFontInfo(page);
		// main explorations of content
		page.process2CML(cmlCml);
		// tidying
		CDXML2CMLObject.addLabelsToMolecules(cmlCml);
		addHydrogenAtomsToMolecules();
		this.processReactions();
		cleanRedundantHierarchy();
		transformRGroups();
		addLabelsToAtoms();
		 
		if (removeCDXAttributes) {
			removeCDXAttributes();
		}
		if (cleanMolecules) {
			flattenGroupingElements();
			removeAtomsWithChildren();
		    removeAtomsWithoutElementTypeOrCoordinates();
			cleanExternalConnectionPoints();
			removeLabelsReactionsAndReactionSchemes();
		}
		flipAndRescaleMolecules();
		if (LOG.isDebugEnabled()) {
			CMLUtil.debug(cmlCml, "==cmlCML==");
		}
	}
    
	/**
	 * @param nodes
	 */
	private void flattenGroupingElement(Nodes nodes) {
		for (int i = 0; i < nodes.size(); i++) {
			 CMLElement element = (CMLElement)nodes.get(i);
			 CMLUtil.transferChildrenToParent(element);
			 element.detach();
		 }
	}

     
     /** child elements screw up some CML viewers
      */
     private void removeAtomsWithChildren() {
    	 if (cmlCml != null) {
	    	 Nodes atoms = cmlCml.query("//cml:atom/*", CML_XPATH);
	    	 for (int i = 0; i < atoms.size(); i++) {
	    		 atoms.get(i).detach();
	    	 }
    	 }
     }
     
     /** atoms without elementType or coordinates
      */
     private void removeAtomsWithoutElementTypeOrCoordinates() {
    	 if (cmlCml != null) {
	    	 Nodes atoms = cmlCml.query(
	    			 "//cml:atom[not(@elementType) or not(@x2) or not(@y2)]", CML_XPATH);
	    	 for (int i = 0; i < atoms.size(); i++) {
	    		 atoms.get(i).detach();
	    	 }
    	 }
     }
     
 	/** top level elements screw some viewers
 	 */
     private void removeLabelsReactionsAndReactionSchemes() {
    	 if (cmlCml != null) {
	    	 Nodes nodes = cmlCml.query(
	    			 "//cml:label | " +
	    			 "//cml:reaction |" +
	    			 "//cml:reactionScheme" +
	    			 "", CML_XPATH);
	    	 for (int i = 0; i < nodes.size(); i++) {
	    		 nodes.get(i).detach();
	    	 }
    	 }
     }
     
     private void flipAndRescaleMolecules() {
    	 if (cmlCml != null) {
	    	 Nodes molecules = cmlCml.query("//cml:molecule", CML_XPATH);
	    	 for (int i = 0; i < molecules.size(); i++) {
	    		 scale((CMLMolecule)molecules.get(i));
	    	 }
    	 }
     }
     
     private void scale(CMLMolecule molecule) {
    	 // only treat top level molecules
    	 if (molecule.query(".//cml:molecule", CML_XPATH).size() == 0) {
    		 MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
    		 try {
	    		 double bb = moleculeTool.getAverageBondLength(CoordinateType.TWOD);
	    		 double scale = (rescale) ? BOND_LENGTH / bb : 1.0;
	    		 // this flips y-coordinates
	    		 Transform2 transform = new Transform2(
	    				 new double[]{
	    				 scale, 0.0,   0.0,
	    				 0.0,  -scale, 0.0,
	    				 0.0,   0.0,   1.0
	    				 }
				 );
	    		 moleculeTool.transform(transform);
    		 } catch (RuntimeException cmle) {
    			 // no coordinates
    		 }
    	 }
     }

     private void processReactions() {
    	 Nodes reactionNodes = cmlCml.query("//cml:reaction", CML_XPATH);
    	 for (int i = 0; i < reactionNodes.size(); i++) {
    		 CMLReaction reaction = (CMLReaction) reactionNodes.get(i);
    		 ChemDrawReactionConverter chemDrawReactionConverter = new ChemDrawReactionConverter(reaction, cmlCml);
    		 chemDrawReactionConverter.processAfterParsing();
    	 }
    	 if (LOG.isDebugEnabled()) {
    		 CMLUtil.debug(cmlCml, "cmlCml");
    	 }
     }
     
     private void addHydrogenAtomsToMolecules() {
    	 Nodes nodes = cmlCml.query("//cml:molecule", CML_XPATH);
    	 for (int i = 0; i < nodes.size(); i++) {
    		 MoleculeTool.getOrCreateTool((CMLMolecule)nodes.get(i)).
    		     adjustHydrogenCountsToValency(HydrogenControl.REPLACE_HYDROGEN_COUNT);
    	 }
     }

     /**
      * "carbons" with label children are actually not C.
      * Try to guess them
      */
     private void transformRGroups() {
    	 Nodes atomNodes = cmlCml.query("//cml:atom[@elementType='C' and cml:label]", CML_XPATH);
    	 for (int i = 0; i < atomNodes.size(); i++) {
    		 CMLAtom atom = (CMLAtom)atomNodes.get(i);
    		 CMLLabel label = atom.getLabelElements().get(0);
    		 String labelS = label.getCMLValue();
    		 LOG.debug("LAB... "+labelS);
    		 if (labelS.equals("R")) {
    			 atom.setElementType("R");
				 label.detach();
    		 } else if (labelS.equals("C")){
    		 } else if (labelS.equals("C-")){
    		 } else if (labelS.equals("C+")){
    		 } else {
    			 atom.setElementType("R");
    		 }
    	 }
	 }
     
     private void addLabelsToAtoms() {
    	 Nodes labelNodes = cmlCml.query("/cml:cml/cml:label", CML_XPATH);
    	 for (int i = 0; i < labelNodes.size(); i++) {
    		 CMLLabel label = (CMLLabel)labelNodes.get(i);
    		 String fontSizeS = label.getAttributeValue(ATT_FONTSIZE, CDX_NAMESPACE);
    		 if (fontSizeS != null) {
    			 int fontSize = Integer.parseInt(fontSizeS);
    			 if (fontSize <= MAX_ATOM_LABEL_FONT_SIZE) {
    				 CMLAtom atom = getNearestAtom(label);
    				 if (atom != null) {
    					 label.detach();
    					 atom.addLabel(label);
    				 }
    			 }
    		 }
    	 }
	 }

     private CMLAtom getNearestAtom(CMLLabel label) {
    	 CMLAtom closestAtom = null;
    	 String p = label.getAttributeValue(ATT_POINT, CDX_NAMESPACE);
    	 if (p != null) {
    		 try {
    			 double[] dd = org.xmlcml.euclid.Util.splitToDoubleArray(p);
    			 Real2 point = new Real2(dd);
    			 Nodes moleculeNodes = cmlCml.query("//cml:molecule", CML_XPATH);
    			 double closestDist = Double.MAX_VALUE;
    			 for (int i = 0; i < moleculeNodes.size(); i++) {
    				 CMLMolecule molecule = (CMLMolecule) moleculeNodes.get(i);
    				 CMLAtom atom = AtomSetTool.getOrCreateTool(molecule).getNearestAtom(point);
    				 if (atom != null) {
    					 double dist = atom.getXY2().getDistance(point);
    					 if (dist < closestDist) {
    						 closestDist = dist;
    						 closestAtom = atom;
    					 }
    				 }
    			 }
    		 } catch (Exception e) {
    			 //
    		 }
    	 }
    	 return closestAtom;
	 }
     
     private void cleanRedundantHierarchy() {
    	 // top cml node
    	 Nodes nodes = cmlCml.query(
    			 "/cml:cml", CML_XPATH);
    	 if (nodes.size() != 1) {
    		 throw new RuntimeException("need exactly one toplevel cml");
    	 }
    	 CMLCml cmlCml = (CMLCml) nodes.get(0);
    	 // remove moleculeList with only 1 child
    	 nodes = cmlCml.query(
    			 "//cml:moleculeList[count(cml:molecule)=1]", CML_XPATH);
    	 flattenGroupingElement(nodes);
    	 // remove empty moleculeList
    	 nodes = cmlCml.query(
    			 "//cml:moleculeList[count(cml:molecule)=0]", CML_XPATH);
    	 for (int i = 0; i < nodes.size(); i++) {
    		 nodes.get(i).detach();
    	 }
    	 // flatten single top-level list (/cml/list)
    	 nodes = cmlCml.query(
    			 "cml:cml[count(cml:list)=1 and count(*)=1]/cml:list", CML_XPATH);
    	 flattenGroupingElement(nodes);
    	 // remove empty lists
    	 nodes = cmlCml.query(
    			 "//cml:list[count(*)=0]", CML_XPATH);
    	 for (int i = 0; i < nodes.size(); i++) {
    		 nodes.get(i).detach();
    	 }
    	 // put top molecules under moleculeList
    	 nodes = cmlCml.query(
    			 "./cml:molecule", CML_XPATH);
    	 if (nodes.size() > 1) {
	    	 CMLMoleculeList moleculeList = new CMLMoleculeList();
	    	 cmlCml.appendChild(moleculeList);
	    	 for (int i = 0; i < nodes.size(); i++) {
	    		 nodes.get(i).detach();
	    		 moleculeList.addMolecule((CMLMolecule) nodes.get(i));
	    	 }
    	 }
     }
	
     private void cleanExternalConnectionPoints() {
    	 if (cmlCml != null) {
    		 Nodes molecules = cmlCml.query(
    				 "//cml:molecule", CML_XPATH);
    		 if (molecules.size() == 0) {
    			 return;
    		 }
    		 CMLMolecule molecule = (CMLMolecule) molecules.get(0);
	    	 Nodes atoms = cmlCml.query(
	    			 "//cml:atom[@*[local-name()='NodeType' and .='ExternalConnectionPoint']]", CML_XPATH);
	    	 for (int i = 0; i < atoms.size(); i++) {
	    		 atoms.get(i).detach();
	    	 }
/** 
	   <atom id="a558" elementType="C" cdx:NodeType="Fragment" 
	       x2="25.035752216501713" y2="26.953796780405963" 
	       xmlns:cdx="http://www.xml-cml/namespaces/cdx"/>
	   <atom id="a560" elementType="N" hydrogenCount="1" 
	     x2="25.075671620016394" y2="27.255753645645697"/>
*/
	    	 atoms = cmlCml.query(
	    			 "//cml:atom[@*[local-name()='NodeType' and .='Fragment']]", CML_XPATH);
	    	 for (int i = 0; i < atoms.size(); i++) {
	    		 // fragment id
	    		 CMLAtom fragment = (CMLAtom) atoms.get(i);
	    		 String fragmentId = fragment.getAttributeValue(CMLXSD_ID);
	    		 // following sibling id
	    		 ParentNode atomArray = fragment.getParent();
	    		 int iatom = atomArray.indexOf(fragment);
	    		 CMLAtom nextAtom = null;
	    		 try {
	    			 nextAtom = (CMLAtom) atomArray.getChild(iatom+1);
	    		 } catch (ArrayIndexOutOfBoundsException aaiobe) {
	    			 LOG.error("Cannot find neighboring atom "+iatom);
	    			 continue;
	    		 }
//	    		 String nextId = nextAtom.getAttributeValue(CMLXSD_ID);
	    		 List<CMLBond> bonds = molecule.getBonds();
	    		 for (CMLBond bond : bonds) {
	    			 String[] atomRefs2 = bond.getAtomRefs2();
	    			 int otherAtomNo = -1;
	    			 if (atomRefs2[0].equals(fragmentId)) {
	    				 otherAtomNo = 1;
	    			 } else if (atomRefs2[1].equals(fragmentId)) {
	    				 otherAtomNo = 0;
	    			 }
	    			 if (otherAtomNo >= 0) {
	    				 CMLAtom rootAtom = bond.getOtherAtom(fragment);
	    				 String order = bond.getOrder();
	    				 CMLBondStereo bondStereo = bond.getBondStereo();
	    				 molecule.deleteBond(bond);
	    				 molecule.deleteAtom(fragment);
	    				 CMLAtom atom0 = rootAtom;
	    				 CMLAtom atom1 = nextAtom;
	    				 if (otherAtomNo == 1) {
		    				 atom1 = rootAtom;
		    				 atom0 = nextAtom;
	    				 }
	    				 bond = new CMLBond(atom0, atom1);
	    				 bond.setOrder(order);
	    				 if (bondStereo != null) {
	    					 bond.addBondStereo(bondStereo);
	    				 }
	    				 molecule.addBond(bond);
	    				 break;
	    			 }
	    		 }
	    	 }
    	 }
     }
     
 	 private void removeCDXAttributes() {
    	 if (cmlCml != null) {
	    	 Nodes nodes = cmlCml.query(
	    			 "//*/@B | " +
	    			 "//*/@BondCircularOrdering | " +
	    			 "//*/@BS | " +
	    			 "//*/@Display | " +
	    			 "//*/@Display2 | " +
	    			 "//*/@DoublePosition | " +
	    			 "//*/@E | " +
	    			 "//*/@Order | " +
	    			 "//*/@Z" +
	    			 "", CML_XPATH);
	    	 for (int i = 0; i < nodes.size(); i++) {
	    		 nodes.get(i).detach();
	    	 }
    	 }
 	 }
     
     /** child elements screw up some CML viewers
      */
     private void flattenGroupingElements() {
    	 if (cmlCml != null) {
	    	 Nodes nodes = cmlCml.query("//cml:moleculeList", CML_XPATH);
	    	 flattenGroupingElement(nodes);
	    	 nodes = cmlCml.query("//cml:list", CML_XPATH);
	    	 flattenGroupingElement(nodes);
    	 }
     }

     
     private void expandFontInfo(CDXObject page) {
    	 Nodes texts = page.query(".//t[@"+TEMP_TEXT+"]");
    	 for (int i = 0; i < texts.size(); i++) {
    		 ((CDXText)texts.get(i)).addFontInfoFromTempText();
    	 }
     }

 	public static void addLabelsToMolecules(CMLElement scopeElement) {
 		// labels are normally "underneath" molecules. Since coordinates run 
 		// vertically "downwards" the coord look like:
 		// --------------------------------> +X
 		// |
 		// |    |-----------------|
 		// |    |                 |
 		// |    |    molecule     |
 		// |    |                 |
 		// |    |-----------------|
 		// |
 		// |        |-------|
 		// |        | label |
 		// |        |-------|
 		// |        
 		// V 
 		// +Y
 		
	//		  <label value="172" BoundingBox="351.2457 97.8999 366.2457 106.9499" id="69"/>
		Nodes moleculeListNodes = scopeElement.query("cml:moleculeList", CML_XPATH);
		if (moleculeListNodes.size() > 0) {
			CMLMoleculeList moleculeList = (CMLMoleculeList) moleculeListNodes.get(0);
			Nodes labelNodes = scopeElement.query("cml:label", CML_XPATH);
			for (CMLMolecule molecule : moleculeList.getMoleculeElements()) {
				if (molecule.getRef() == null || S_EMPTY == molecule.getRef()) {
					CDXML2CMLObject.moveLabelsToMolecules(scopeElement, labelNodes, molecule);
				}
			}
		}
	}

	public static void createMoleculeList(CMLElement element) {
		Nodes molecules = element.query("cml:molecule", CMLConstants.CML_XPATH);
		CMLMoleculeList moleculeList = new CMLMoleculeList();
		element.appendChild(moleculeList);
		for (int i = 0; i < molecules.size(); i++) {
			CMLMolecule molecule = (CMLMolecule) molecules.get(i);
			molecule.detach();
			moleculeList.addMolecule(molecule);
		}
	}

	/**
	 * @param element
	 * @param labelNodes
	 * @param molecule
	 * @throws EuclidRuntimeException
	 * @throws NumberFormatException
	 */
	static void moveLabelsToMolecules(CMLElement element, Nodes labelNodes, CMLMolecule molecule) {
		// only take original molecules
		if (!"cdx:fragment".equals(molecule.getRole())) {
			Real2Range moleculeBB = CDXML2CMLObject.getNormalizedBoundingBox(molecule);
			if (moleculeBB != null) {
				double molYMax = moleculeBB.getYRange().getMax();
				List<CMLLabel> labels = CDXML2CMLObject.getVerticalLabels(
					element, labelNodes, moleculeBB, 100, -100,
					MIN_MOLECULE_LABEL_FONT_SIZE,
					MAX_MOLECULE_LABEL_FONT_SIZE
				);
				labels = CDXML2CMLObject.sortLabelsByY(labels);
				for (CMLLabel label : labels) {
					Real2Range labelBB = CDXML2CMLObject.getNormalizedBoundingBox(label);
					double deltaY = labelBB.getYRange().getMin() - molYMax;
					if (deltaY < MIN_MOLECULE_TO_LABEL_YDELTA ||
						deltaY > MAX_MOLECULE_TO_LABEL_YDELTA) {
						continue;
					}
					label.detach();
					molecule.addLabel(label);
				}
			} else {
				LOG.debug("Null bounding box");
			}
		}
	}

	/**
	 * @param labels
	 * @return list of labels
	 * @throws NumberFormatException
	 */
	protected static List<CMLLabel> sortLabelsByY(List<CMLLabel> labels) throws NumberFormatException {
		// sort by labels
		List<CMLLabel> labelList1 = new ArrayList<CMLLabel>();
		while (labels.size() > 0) {
			double dd = -Double.MAX_VALUE;
			CMLLabel label1 = null;
			for (CMLLabel label : labels) {
				String ys = label.getAttributeValue(ATT_YDELTA, CDX_NAMESPACE);
				if (ys != null) {
					double y = new Double(ys).doubleValue();
					if (y > dd) {
						dd = y;
						label1 = label;
					}
				} else {
					label.debug();
				}
			}
			labelList1.add(label1);
			labels.remove(label1);
		}
		return labelList1;
	}

	protected static List<CMLLabel> getVerticalLabels(
		Element parent, Nodes labelNodes, Real2Range boundingBox, 
		double topYDelta, double bottomYDelta, int minFont, int maxFont) {
		List<CMLLabel> labelList = new ArrayList<CMLLabel>();
		if (boundingBox == null) {
			throw new RuntimeException("Null bounding box");
		}
		RealRange targetXRange = boundingBox.getXRange();
		RealRange targetYRange = boundingBox.getYRange();
		for (int i = 0; i < labelNodes.size(); i++) {
			CMLLabel label = (CMLLabel) labelNodes.get(i);
			int fontSize = getFontSize(label);
			if (fontSize < minFont || fontSize > maxFont) {
				continue;
			}
			Real2Range labelBoundingBox = CDXML2CMLObject.getNormalizedBoundingBox(label);
			if (labelBoundingBox != null) {
				RealRange labelXRange = labelBoundingBox.getXRange();
				RealRange labelYRange = labelBoundingBox.getYRange();
				RealRange commonXRange = labelXRange.intersectionWith(targetXRange);
				RealRange commonYRange = labelYRange.intersectionWith(targetYRange);
				if (commonXRange != null) {
					double yAbove = targetYRange.getMin() - labelYRange.getMax();
					double yBelow = targetYRange.getMax() - labelYRange.getMin();
					if (commonYRange != null || 
							(yAbove > 0 && yAbove < topYDelta)) {
						label.addAttribute(new Attribute(CDX_YDELTA, CDX_NAMESPACE, ""+yAbove));
						labelList.add(label);
					} else if (commonYRange != null ||  
							(yBelow < 0 && yBelow > bottomYDelta)) {
						label.addAttribute(new Attribute(CDX_YDELTA, CDX_NAMESPACE, ""+yBelow));
						labelList.add(label);
					} else {
						LOG.warn("MISSED");
					}
				} else {
				}
			} else {
				LOG.warn("NULL LABEL "+label.getCMLValue());
			}
		}
		return labelList;
	}
	
	private static int getFontSize(CMLLabel label) {
		String fontS = label.getAttributeValue("size", CDX_NAMESPACE);
		int size = (fontS == null) ? -1 : Integer.parseInt(fontS);
		if (fontS == null) {
			label.debug("FONT");
			throw new RuntimeException("all text should have font size");
		}
		return size;
	}
	

	/** get bounding box
	 * normalize so that xmin < xmax and ymin < ymax
	 * @param label
	 * @throws EuclidRuntimeException
	 */
	static Real2Range getNormalizedBoundingBox(CMLElement element) throws EuclidRuntimeException {
		String boundingBoxS = element.getAttributeValue(ATT_BOUNDING_BOX, CDX_NAMESPACE);
		Real2Range r2r = null;
		if (boundingBoxS != null) {
			double[] bb = Util.splitToDoubleArray(boundingBoxS, S_SPACE);
			r2r = new Real2Range(
				new RealRange(bb[0], bb[2], true),
				new RealRange(bb[1], bb[3], true));
		}
		return r2r;
	}

  	/**
  	 * @return the flatten
  	 */
  	public boolean isFlatten() {
  		return flatten;
  	}

  	/**
  	 * @param flatten the flatten to set
  	 */
  	public void setFlatten(boolean flatten) {
  		this.flatten = flatten;
  	}

	/**
	 * @return the rescale
	 */
	public boolean isRescale() {
		return rescale;
	}

	/**
	 * @param rescale the rescale to set
	 */
	public void setRescale(boolean rescale) {
		this.rescale = rescale;
	}

 	/**
 	 * @return the cleanMolecules
 	 */
 	public boolean isCleanMolecules() {
 		return cleanMolecules;
 	}

 	/**
 	 * @param cleanMolecules the cleanMolecules to set
 	 */
 	public void setCleanMolecules(boolean cleanMolecules) {
 		this.cleanMolecules = cleanMolecules;
 	}
 	

}