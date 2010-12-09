package org.xmlcml.cml.chemdraw;

import java.util.HashMap;

import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.components.CDXArrow;
import org.xmlcml.cml.chemdraw.components.CDXBond;
import org.xmlcml.cml.chemdraw.components.CDXBracketAttachment;
import org.xmlcml.cml.chemdraw.components.CDXBracketedGroup;
import org.xmlcml.cml.chemdraw.components.CDXColorTable;
import org.xmlcml.cml.chemdraw.components.CDXCurve;
import org.xmlcml.cml.chemdraw.components.CDXFontTable;
import org.xmlcml.cml.chemdraw.components.CDXFragment;
import org.xmlcml.cml.chemdraw.components.CDXGeometry;
import org.xmlcml.cml.chemdraw.components.CDXGraphic;
import org.xmlcml.cml.chemdraw.components.CDXGroup;
import org.xmlcml.cml.chemdraw.components.CDXList;
import org.xmlcml.cml.chemdraw.components.CDXML;
import org.xmlcml.cml.chemdraw.components.CDXNode;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.cml.chemdraw.components.CDXObjectTag;
import org.xmlcml.cml.chemdraw.components.CDXPage;
import org.xmlcml.cml.chemdraw.components.CDXReactionScheme;
import org.xmlcml.cml.chemdraw.components.CDXReactionStep;
import org.xmlcml.cml.chemdraw.components.CDXText;

public class XMLToCDXMLConverter {
	private static Logger LOG = Logger.getLogger(XMLToCDXMLConverter.class);
	
	static {
		LOG.setLevel(Level.INFO);
	}
	
	static Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	static {
		String base = new XMLToCDXMLConverter().getClass().getPackage().getName();
		try {
		    classMap.put(CDXArrow.CDXNAME, CDXArrow.class);
		    classMap.put(CDXBond.CDXNAME, CDXBond.class);
		    classMap.put(CDXBracketAttachment.CDXNAME, CDXBracketAttachment.class);
		    classMap.put(CDXBracketedGroup.CDXNAME, CDXBracketedGroup.class);
		    classMap.put(CDXColorTable.CDXNAME, CDXColorTable.class);
		    classMap.put(CDXCurve.CDXNAME, CDXCurve.class);
		    classMap.put(CDXFontTable.CDXNAME, CDXFontTable.class);
		    classMap.put(CDXFragment.CDXNAME, CDXFragment.class);
		    classMap.put(CDXGeometry.CDXNAME, CDXGeometry.class);
		    classMap.put(CDXGraphic.CDXNAME, CDXGraphic.class);
		    classMap.put(CDXGroup.CDXNAME, CDXGroup.class);
		    classMap.put(CDXList.CDXNAME, CDXList.class);
		    classMap.put(CDXML.CDXNAME, CDXML.class);
		    classMap.put(CDXNode.CDXNAME, CDXNode.class);
		    classMap.put(CDXObjectTag.CDXNAME, CDXObjectTag.class);
		    classMap.put(CDXPage.CDXNAME, CDXPage.class);
		    classMap.put(CDXReactionScheme.CDXNAME, CDXReactionScheme.class);
		    classMap.put(CDXReactionStep.CDXNAME, CDXReactionStep.class);
		    classMap.put(CDXText.CDXNAME, CDXText.class);	
		    // subsidiary classes
		    classMap.put("color", Element.class);	
		    classMap.put("font", Element.class);	
		    classMap.put("object", Element.class);
		    
		    //Known to be ignorable
		    classMap.put("splitter", Element.class);
		} catch (Exception e) {
			throw new RuntimeException("Cannot set up classMap in: "+base);
		}
	}
	
	public XMLToCDXMLConverter() {
	}
	
	public Element convertToCDXObject(Element cdxml) {
		Element element = getCDXObject(cdxml);
		return element;
	}

	private Element getCDXObject(Element oldElement) {
		String localName = oldElement.getLocalName();
		Class<?> theClass = classMap.get(localName);
		if (theClass == null) {
			throw new RuntimeException("******************Cannot find class for: "+localName);
		}
		String className = "";
		Element newElement = null;
		try {
			if (CDXObject.class.isAssignableFrom(theClass)) {
				newElement = (Element) theClass.newInstance();
			} else {
				LOG.debug("non CDX element "+localName);
				newElement = new Element(localName);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot create object from: "+localName+ " to "+className+" because "+e.getMessage());
		}
		for (int i = 0; i < oldElement.getAttributeCount(); i++) {
			newElement.addAttribute(new Attribute(oldElement.getAttribute(i)));
		}
		for (int i = 0; i < oldElement.getChildCount(); i++) {
			Node node = oldElement.getChild(i);
			if (node instanceof Element) {
				Element child = getCDXObject((Element) node);
				newElement.appendChild(child);
			} else if (node instanceof ProcessingInstruction) {
				ProcessingInstruction pi = new ProcessingInstruction((ProcessingInstruction) node);
				newElement.appendChild(pi);
			} else if (node instanceof Comment) {
				Comment comment = new Comment((Comment) node);
				newElement.appendChild(comment);
			} else if (node instanceof Text) {
				Text text = new Text((Text) node);
				newElement.appendChild(text);
			} else {
				throw new RuntimeException("Unsupported XML object: "+node);
			}
		}
		return newElement;
	}
}
