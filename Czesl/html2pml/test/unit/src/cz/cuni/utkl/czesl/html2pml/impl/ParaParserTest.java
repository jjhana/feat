/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.Doc;
import cz.cuni.utkl.czesl.html2pml.impl.ParaParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.jh.util.CountingLogger;
import static org.junit.Assert.*;

/**
 *
 * @author jirka
 */
public class ParaParserTest {
    
    public ParaParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testBasicValidations() {
        
    }

    @Test
    public void testSimplifyingConversion() {
        assertEquals("abc def {email address}<pr> xx", simplifyingConversion("abc def {email address}<co> xx"));
        assertEquals("abc def{email address}<co> xx", simplifyingConversion("abc def{email address}<co> xx"));
        
        assertEquals("{email address}<pr> xx", simplifyingConversion("{email address}<co> xx"));
    }

    private String simplifyingConversion(String aInput) {
        ParaParser pp = new ParaParser(new Doc(), aInput, 0, aInput.length(), CountingLogger.getLogger("tmp"));
        pp.simplifyingConversion();
        return pp.getText().clean();
    }
    
    
    

    @Test
    public void testParse() throws Exception {
    }

    @Test
    public void testAssertFatal() {
    }

    @Test
    public void testAssertW_3args() {
    }

    @Test
    public void testAssertW_4args() {
    }

    @Test
    public void testWarning() {
    }

    @Test
    public void testAssertErr_3args() {
    }

    @Test
    public void testAssertErr_4args() {
    }

    @Test
    public void testErrCtx() {
    }
}
