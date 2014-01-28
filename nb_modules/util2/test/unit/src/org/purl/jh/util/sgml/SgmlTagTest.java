/*
 * SgmlTagTest.java
 * JUnit based test
 *
 * Created on September 10, 2005, 11:05 PM
 */

package org.purl.jh.util.sgml;

import junit.framework.*;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Jiri
 */
public class SgmlTagTest extends TestCase {
    
    public SgmlTagTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(SgmlTagTest.class);
        
        return suite;
    }

    public void testGetCore() {
    }

    public void testGetAttribute() {
    }

    public void testSetAttribute() {
        SgmlTag tag1 = new SgmlTag("<MMl src=\"a\" id=\"b\">");
        SgmlTag tag2 = new SgmlTag("<l>");
        assertEquals(tag1.setAttribute("src", "x").toString(), "<MMl src=\"x\" id=\"b\">");
        assertEquals(tag2.setAttribute("src", "x").toString(), "<l src=\"x\">");
    }

    public void testRemoveAttribute() {
        SgmlTag tag1 = new SgmlTag("<MMl src=\"a\" id=\"b\">");
        SgmlTag tag2 = new SgmlTag("<l>");
        assertEquals(tag1.removeAttribute("src").toString(), "<MMl id=\"b\">");
        assertEquals(tag2.removeAttribute("src").toString(), "<l>");
    }

    public void testSetCore() {
        SgmlTag tag1 = new SgmlTag("<MMl src=\"a\" id=\"b\">");
        SgmlTag tag2 = new SgmlTag("<l>");
        assertEquals(tag1.setCore("A").toString(), "<A src=\"a\" id=\"b\">");
        assertEquals(tag2.setCore("B").toString(), "<B>");
    }

    public void testToString() {
    }

    public void testConstructor() {
    }

    public void testConstructorx() {
    }
    
}
