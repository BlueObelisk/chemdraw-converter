package org.xmlcml.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.tests.XOMTestCase;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.base.CMLConstants;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Util;

/**
 * 
 * <p>
 * superclass for manage common methods for unit tests
 * </p>
 * 
 * @author Peter Murray-Rust
 * @version 5.0
 * 
 */
public final class TestUtils implements CMLConstants {

    /** logger */
    public final static Logger logger = Logger.getLogger(TestUtils.class);

    /** root of tests.*/
    public final static String BASE_RESOURCE = "org/xmlcml/cml/base";
    
    /**
     * tests 2 XML objects for equality using canonical XML.
     * uses XOMTestCase.assertEquals. This treats different prefixes as different
     * and compares floats literally. 
     * @param message
     * @param refNode
     *            first node
     * @param testNode
     *            second node
     */
    public static void assertEqualsCanonically(
            String message, Node refNode, Node testNode) {
        try {
            XOMTestCase.assertEquals(message, refNode, testNode);
        } catch (ComparisonFailure e) {
            reportXMLDiff(message, e.getMessage(), refNode, testNode);
        } catch (AssertionFailedError e) {
            reportXMLDiff(message, e.getMessage(), refNode, testNode);
        }
    }

    /** compares two XML nodes and checks float near-equivalence 
     * (can also be used for documents without floats)
     * usesTestUtils.assertEqualsCanonically and only uses PMR code if fails
     * @param message
     * @param refNode
     * @param testNode
     * @param eps
     */
    public static void assertEqualsIncludingFloat(
    		String message, Node refNode, Node testNode, boolean stripWhite, double eps) {
        if (stripWhite && refNode instanceof Element && testNode instanceof Element) {
            refNode = stripWhite((Element)refNode);
            testNode = stripWhite((Element) testNode);
        }
        try {
        	assertEqualsIncludingFloat(message, refNode, testNode, eps);
        } catch (RuntimeException e) {
        	reportXMLDiffInFull(message, e.getMessage(), refNode, testNode);
        }
    }

	public static void assertEqualsIncludingFloat(String message, String expectedS,
			Node testNode, boolean stripWhite, double eps) {
		assertEqualsIncludingFloat(message, TestUtils.parseValidString(expectedS), testNode, stripWhite, eps);
	}

	private static void assertEqualsIncludingFloat(String message, Node refNode,
			Node testNode, double eps) {
		try {
			Assert.assertEquals(message+": classes", testNode.getClass(), refNode.getClass());
	    	if (refNode instanceof Text) {
	    		tstStringDoubleEquality(message, refNode.getValue(), testNode.getValue(), eps);
	    	} else if (refNode instanceof Comment) {
	    		Assert.assertEquals(message+" comment", refNode.getValue(), testNode.getValue());
	    	} else if (refNode instanceof ProcessingInstruction) {
	    		Assert.assertEquals(message+" pi", (ProcessingInstruction) refNode, (ProcessingInstruction) testNode);
	    	} else if (refNode instanceof Element){
		    	int refNodeChildCount = refNode.getChildCount();
		    	int testNodeChildCount = testNode.getChildCount();
		    	Assert.assertEquals("number of children", testNodeChildCount, refNodeChildCount);
		    	for (int i = 0; i < refNodeChildCount; i++) {
		    		assertEqualsIncludingFloat(message, refNode.getChild(i), testNode.getChild(i), eps);
		    	}
		    	Element refElem = (Element) refNode;
		    	Element testElem = (Element) testNode;
		    	Assert.assertEquals(message+" name", refElem.getLocalName(), testElem.getLocalName());
		    	Assert.assertEquals(message+" namespace", refElem.getNamespaceURI(), testElem.getNamespaceURI());
		    	Assert.assertEquals(message+" attributes on "+refElem.getClass(), refElem.getAttributeCount(), testElem.getAttributeCount());
		    	for (int i = 0; i < refElem.getAttributeCount(); i++) {
		    		Attribute refAtt = refElem.getAttribute(i);
		    		String attName = refAtt.getLocalName();
		    		String attNamespace = refAtt.getNamespaceURI();
		    		Attribute testAtt = testElem.getAttribute(attName, attNamespace);
		    		if (testAtt == null) {
//				    	CMLUtil.debug((Element)refNode, "XXXXXXXXXXX");
//				    	CMLUtil.debug((Element)testNode, "TEST");
		    			Assert.fail(message+" attribute on ref not on test: "+attName);
		    		}
		    		tstStringDoubleEquality(message, refAtt.getValue(), testAtt.getValue(), eps);
		    	}
	    	} else {
	    		Assert.fail(message + "cannot deal with XMLNode: "+refNode.getClass());
	    	}
		} catch (Throwable t) {
			throw new RuntimeException(""+t);
		}
	}
	private static void tstStringDoubleEquality(String message, String refValue, String testValue,
			double eps) {
		Error ee = null;
		try {
			try {
				double testVal = new Double(testValue).doubleValue();
				double refVal = new Double(refValue).doubleValue();
				Assert.assertEquals(message+" doubles ", refVal, testVal, eps);
			} catch (NumberFormatException e) {
				Assert.assertEquals(message+" String ", refValue, testValue);
			}
		} catch (ComparisonFailure e) {
			ee = e;
		} catch (AssertionError e) {
			ee = e;
		}
		if (ee != null) {
			throw new RuntimeException(""+ee);
		}
	}
    /**
     * tests 2 XML objects for equality using canonical XML.
     * uses XOMTestCase.assertEquals. This treats different prefixes as different
     * and compares floats literally. 
     * @param message
     * @param refNode first node
     * @param testNode second node
     * @param stripWhite if true remove w/s nodes
     */
    public static void assertEqualsCanonically(
            String message, Element refNode, Element testNode, boolean stripWhite) {
    	assertEqualsCanonically(message, refNode, testNode, stripWhite, true);
    }
    
    /**
     * tests 2 XML objects for equality using canonical XML.
     * 
     * @param message
     * @param refNode first node
     * @param testNode second node
     * @param stripWhite if true remove w/s nodes
     */
    private static void assertEqualsCanonically(
            String message, Element refNode, Element testNode, boolean stripWhite, boolean reportError) throws Error {
        if (stripWhite) {
            refNode = stripWhite(refNode);
            testNode = stripWhite(testNode);
        }
        Error ee = null;
        try {
            XOMTestCase.assertEquals(message, refNode, testNode);
        } catch (ComparisonFailure e) {
        	ee = e;
        } catch (AssertionFailedError e) {
        	ee = e;
        }
        if (ee != null) {
        	if (reportError) {
                reportXMLDiffInFull(message, ee.getMessage(), refNode, testNode);
        	} else {
        		throw (ee);
        	}
        }
    }

	private static Element stripWhite(Element refNode) {
		refNode = new Element(refNode);
		CMLUtil.removeWhitespaceNodes(refNode);
		return refNode;
	}
    
    public static void reportXMLDiff(String message, String errorMessage,
            Node refNode, Node testNode) {
        Assert.fail(message+" ~ "+errorMessage);
    }

    public static void reportXMLDiffInFull(String message, String errorMessage,
            Node refNode, Node testNode) {
        try {
	        System.err.println("==========XMLDIFF reference=========");
	        CMLUtil.debug((Element) refNode, System.err, 2); 
	        System.err.println("------------test---------------------");
	        CMLUtil.debug((Element) testNode, System.err, 2); 
	        System.err.println("=============="+message+"===================");
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        Assert.fail(message+" ~ "+errorMessage);
    }


    /**
     * tests 2 XML objects for non-equality using canonical XML.
     * uses XOMTestCase.assertEquals. This treats different prefixes as different
     * and compares floats literally. 
     * @param message
     * @param node1
     *            first node
     * @param node2
     *            second node
     */
    public static void assertNotEqualsCanonically(String message, Node node1,
            Node node2) {
        try {
            XOMTestCase.assertEquals(message, node1, node2);
            String s1 = CMLUtil.getCanonicalString(node1);
            String s2 = CMLUtil.getCanonicalString(node2);
            Assert.fail(message + "nodes should be different " + s1 + " != "
                    + s2);
        } catch (ComparisonFailure e) {
        } catch (AssertionFailedError e) {
        }
    }


    /** test the writeHTML method of element.
     * 
     * @param element to test
     * @param expected HTML string
     */ 
    public static void assertWriteHTML(CMLElement element, String expected) {
        StringWriter sw = new StringWriter();
        try {
            element.writeHTML(sw);
            sw.close();
        } catch (IOException e) {
            Assert.fail("should not throw " + e);
        }
        String s = sw.toString();
        Assert.assertEquals("HTML output ", expected, s);
    }

    /** convenience method to parse test string.
	 * 
	 * @param s xml string (assumed valid)
	 * @return root element
	 */
	public static Element parseValidString(String s) {
	    Element element = null;
	    if (s == null) {
	    	throw new RuntimeException("NULL VALID JAVA_STRING");
	    }
	    try {
	        element = new CMLBuilder().parseString(s);
	    } catch (Exception e) {
	    	e.printStackTrace();
	        System.err.println("ERROR "+e+e.getMessage()+"..."+s.substring(0, Math.min(100, s.length())));
	        Util.BUG(e);
	    }
	    return element;
	}

	/** convenience method to parse test file.
	 * uses resource
	 * @param filename relative to classpath
	 * @return root element
	 */
	public static Element parseValidFile(String filename) {
	    Element root = null;
	    try {
	        URL url =  Util.getResource(filename);
	        CMLBuilder builder = new CMLBuilder();
	        root =  builder.build(
	                new File(url.toURI())).getRootElement();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return root;
	}

	/**
	 * used by Assert routines. copied from Assert
	 * 
	 * @param message
	 *            prepends if not null
	 * @param expected
	 * @param actual
	 * @return message
	 */
	public static String getAssertFormat(String message, Object expected,
	        Object actual) {
	    String formatted = "";
	    if (message != null) {
	        formatted = message + CMLConstants.S_SPACE;
	    }
	    return formatted + "expected:<" + expected + "> but was:<" + actual
	            + ">";
	}
    

}
