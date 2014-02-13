/*
 * UtilTest.java
 * JUnit based test
 *
 * Created on September 16, 2004, 8:43 PM
 */

package org.purl.jh.util;

import junit.framework.TestCase;
import junit.framework.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author Jiri
 */
public class UtilTest extends TestCase {
    
    public UtilTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {

        junit.framework.TestSuite suite = new junit.framework.TestSuite(UtilTest.class);
        
        return suite;
    }

    /**
     * Test of fassert method, of class morph.util.Util.
     */
    public void testFassert() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of inString method, of class morph.util.Util.
     */
    public void testInString() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of lastChar method, of class morph.util.Util.
     */
    public void testLastChar() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of repeatChar method, of class morph.util.Util.
     */
    public void testRepeatChar() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of spaces method, of class morph.util.Util.
     */
    public void testSpaces() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of removeEnding method, of class morph.util.Util.
     */
    public void testRemoveEnding() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of charIn method, of class morph.util.Util.
     */
    public void testCharIn() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of lastCharIn method, of class morph.util.Util.
     */
    public void testLastCharIn() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of sepp method, of class morph.util.Util.
     */
    public void testSepp() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of preprocessLine method, of class morph.util.Util.
     */
    public void testPreprocessLine() {
        assertEquals(Util.preprocessLine("abc/*aaaa*/def"),           "abcdef");
        assertEquals(Util.preprocessLine("abc/*aaaa*/def/*bbb*/ghi"), "abcdefghi");
        assertEquals(Util.preprocessLine("abc/*aaaa*/"),              "abc");
        assertEquals(Util.preprocessLine("/*aaaa*/def"),              "def");
        assertEquals(Util.preprocessLine("/*aaaa*/"),                 "");
        assertEquals(Util.preprocessLine("abc/*aaaa*//*aaaa*/def"),   "abcdef");
        assertEquals(Util.preprocessLine("abc/*aaaa*//*aaaa*/"),      "abc");
        assertEquals(Util.preprocessLine("/*aaaa*//*aaaa*/def"),      "def");
        assertEquals(Util.preprocessLine("/*aaaa*//*aaaa*/"),         "");
    }

    /**
     * Test of getNumber method, of class morph.util.Util.
     */
    public void testGetNumber() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of getString method, of class morph.util.Util.
     */
    public void testGetString() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of collectionToString method, of class morph.util.Util.
     */
    public void testCollectionToString() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of collectionToStringNl method, of class morph.util.Util.
     */
    public void testCollectionToStringNl() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of log2 method, of class morph.util.Util.
     */
    public void testLog2() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of log10 method, of class morph.util.Util.
     */
    public void testLog10() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of log method, of class morph.util.Util.
     */
    public void testLog() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of positive method, of class morph.util.Util.
     */
    public void testPositive() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of perc method, of class morph.util.Util.
     */
    public void testPerc() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of formatPercentages method, of class morph.util.Util.
     */
    public void testFormatPercentages() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of formatFrac method, of class morph.util.Util.
     */
    public void testFormatFrac() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of round3 method, of class morph.util.Util.
     */
    public void testRound3() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of setUnixLineSeparators method, of class morph.util.Util.
     */
    public void testSetUnixLineSeparators() {

        
        // TODO add your test code below by replacing the default call to fail.
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
