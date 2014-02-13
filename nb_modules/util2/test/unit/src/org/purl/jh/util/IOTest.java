/*
 * IOTest.java
 * JUnit based test
 *
 * Created on January 6, 2005, 4:45 PM
 */

package org.purl.jh.util;

import junit.framework.*;
import java.io.*;
import org.purl.jh.util.io.IO;

/**
 *
 * @author Jiri
 */
public class IOTest extends TestCase {
    
    public IOTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(IOTest.class);
        
        return suite;
    }

    public void testTranslateEncAbbr() {
    }

    public void testCreateFilePrintWriter() {
    }

    public void testOpenPrintWriterBattery() {
    }

    public void testCloseBattery() {
    }

    public void testCreateLNFileReader() {
    }

//    public void testRemoveExtension() {
//        assertEquals( IO.removePossExtension(new File("file.x.txt"), "txt"),   new File("file.x") ); 
//        assertEquals( IO.removePossExtension(new File("file.x.txt"), "x"),     new File("file.x.txt") ); 
//        assertEquals( IO.removePossExtension(new File("file.x.txt"), "x.txt"), new File("file") ); 
//    }

    public void testGetExtension() {
    }

    public void testAddBeforeExtension() {
    }

    public void testAddExtension() {
    }

    public void testReplaceExtension() {
    }

    public void testReplaceDir() {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
