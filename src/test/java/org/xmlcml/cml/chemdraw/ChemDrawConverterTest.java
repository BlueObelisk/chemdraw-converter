package org.xmlcml.cml.chemdraw;

import java.io.File;
import java.io.FileInputStream;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cml.chemdraw.CDX2CDXML;
import org.xmlcml.cml.chemdraw.components.CDXObject;
import org.xmlcml.util.TestUtils;


public class ChemDrawConverterTest {

	static Logger LOG = Logger.getLogger(ChemDrawConverterTest.class);
	
	String TEST_RESOURCES = "src/test/resources";

	@Test
	@Ignore
	public void testChemDrawConverter() {
		File in = new File(TEST_RESOURCES+"/cdx/r19.cdx");
//		File out = new File(TEST_RESOURCES+"/cdxml/r19.cdx.xml");
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
		TestUtils.assertEqualsCanonically("parsed CDX", ref, parsedObject, true);
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
//		CDXObject parsedObject = cd.getCDXMLObject();
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
