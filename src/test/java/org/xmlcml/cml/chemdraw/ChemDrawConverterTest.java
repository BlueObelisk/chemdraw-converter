package org.xmlcml.cml.chemdraw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.cml.testutil.JumboTestUtils;



public class ChemDrawConverterTest {

	static Logger LOG = Logger.getLogger(ChemDrawConverterTest.class);
	
	String TEST_RESOURCES = "src/test/resources";

	@Test
	public void testChemDrawConverter0() {
		File in = new File(TEST_RESOURCES+"/cdx/dimethylaminesimple.cdx");
		File outRef = new File(TEST_RESOURCES+"/cdxml/dimethylaminesimple.cdx.xml");
		CDX2CDXML cd = new CDX2CDXML();
		try {
			cd.parseCDX(new FileInputStream(in));
		} catch (Exception e) {
			Assert.fail("failed to read file "+e.getMessage());
		}
		CDXObject parsedObject = cd.getCDXMLObject();
		// from CDX docs (I have had to add and remove some attributes)
		Element ref = CMLUtil.parseXML(
"<?xml version='1.0' encoding='UTF-8' ?>"+
//"<!DOCTYPE CDXML SYSTEM 'http://www.camsoft.com/xml/cdxml.dtd'>"+
"<CDXML>"+
"<page>"+
"<fragment>"+
"<n id ='5' p='148.5 164.25' Element='7'></n>"+
"<n id='6' p='177.4777 172.0145'/>"+
"<b B='5' E='6'/>"+
"<n id='4' p='119.5222 172.0145'/>"+
"<b B='5' E='4' BS='N'/>"+
"</fragment>"+
"</page>"+
"</CDXML>"
);
		Element test = parsedObject.getChildElements().get(0);
		stripIds(test);
		stripIds(ref);
		JumboTestUtils.assertEqualsIncludingFloat("parsed CDX", ref, parsedObject.getChildElements().get(0), true, 0.02);
	}
	
	@Test
	public void testChemDrawConverter80() {
		File in = new File(TEST_RESOURCES+"/cdx/80_cdx/80.cdx");
		File outRef = new File(TEST_RESOURCES+"/cdxml/80.cdx.xml");
		CDX2CDXML cd = new CDX2CDXML();
		try {
			cd.parseCDX(new FileInputStream(in));
		} catch (Exception e) {
			Assert.fail("failed to read file "+e.getMessage());
		}
		CDXObject parsedObject = cd.getCDXMLObject();
		CDXML2CMLProcessor cdxml2cmlProcessor = new CDXML2CMLProcessor();
		cdxml2cmlProcessor.convertParsedXMLToCML(parsedObject);
		CMLElement cml = cdxml2cmlProcessor.getCML();
	}

	@Test
	public void testCDXML2CML() throws Exception {
		File in = new File(TEST_RESOURCES+"/cdxml/C00006.cdxml");
		File out = new File(TEST_RESOURCES+"/cdxml/C00006.cml");
		File ref = new File(TEST_RESOURCES+"/cdxml/C00006.ref.cml");
		CDXML2CMLProcessor processor = new CDXML2CMLProcessor();
		try {
			Element cdxml = CMLUtil.parseQuietlyToCMLDocument(new FileInputStream(in)).getRootElement();
			processor.convertParsedXMLToCML(cdxml);
		} catch (Exception e) {
			Assert.fail("failed to read file "+e.getMessage());
		}
		CMLElement cml = processor.getCML();
		CMLUtil.debug(cml, new FileOutputStream(out), 0);
		JumboTestUtils.assertEqualsIncludingFloat(
				"cdxml", 
				CMLUtil.parseQuietlyIntoCML(ref),
				CMLUtil.parseQuietlyIntoCML(out),
				true, 0.000000001);
	}

	private void stripIds(Element test) {
		Nodes elements = test.query("//*");
		for (int i = 0; i < elements.size(); i++) {
			stripId((Element) elements.get(i));
		}
	}

	

	private void stripId(Element element) {
		if (element.getLocalName().equals("n")) {
		} else {
			Attribute id = element.getAttribute("id");
			if (id != null) {
				id.detach();
			}
		}
	}


	@Test
//	@Ignore
	public void testChemDrawConverter() {
		File in = new File(TEST_RESOURCES+"/cdx/r19.cdx");
		File outRef = new File(TEST_RESOURCES+"/cdxml/r19Ref.cdx.xml");
		CDX2CDXML cd = new CDX2CDXML();
		try {
			cd.parseCDX(new FileInputStream(in));
		} catch (Exception e) {
			Assert.fail("failed to read file "+e.getMessage());
		}
		CDXObject parsedObject = cd.getCDXMLObject();
		Element ref = null;
		try {
			ref = new Builder().build(new FileInputStream(outRef)).getRootElement();
		} catch (Exception e) {
			Assert.fail("bug in ref: "+e.getMessage());
		}
		JumboTestUtils.assertEqualsCanonically("parsed CDX", ref, parsedObject, true);
	}
	
	@Test
	@Ignore
	public void testChemDrawConverter3D() {
		File in = new File(TEST_RESOURCES+"/cdx3d/oleObject171.cdx");
		File out = new File(TEST_RESOURCES+"/cdxml/oleObject171.cdx.xml");
		File outRef = new File(TEST_RESOURCES+"/cdxml/oleObject171Ref.cdx.xml");
		LOG.debug("out "+out.getAbsolutePath());
		CDX2CDXML cd = new CDX2CDXML();
		try {
			cd.parseCDX(new FileInputStream(in));
		} catch (Exception e) {
			Assert.fail("failed to read file "+e.getMessage());
		}
		LOG.debug("outRef "+outRef.getAbsolutePath());
//		Element ref = null;
		try {
			/*ref = */new Builder().build(new FileInputStream(outRef)).getRootElement();
		} catch (Exception e) {
			Assert.fail("bug in ref: "+e.getMessage());
		}
		// at present this is an invalid file
//		TestUtils.assertEqualsCanonically("parsed CDX", ref, parsedObject, true);
	}
}
