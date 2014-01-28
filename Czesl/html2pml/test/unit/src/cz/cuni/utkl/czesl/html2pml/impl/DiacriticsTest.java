/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.DelString;
import cz.cuni.utkl.czesl.html2pml.impl.Diacritics;
import java.text.Normalizer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jirka
 */
public class DiacriticsTest {
    
    public DiacriticsTest() {
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
    public void testNormalize() {
    }

    @Test
    public void testStandardizeDiacritics() {
        test("ä", "[a\"]");
        test("ä", "[a“]");
        test("ű", "[u:]");
        test("ç", "[c,]");
        test("ą", "[a;]");
        test("ż", "[z.]");
        test("â", "[a^]");
        test("ñ", "[n~]");
        test("Ł", "[L/]");
        test("è", "[e`]");
        test("ā", "[a-]");
        test("å", "[ao]");

        // not fully condensed
        test("ä\u0327\u0303", "[a\",~]");
        test("ä\u0327\u0327\u0303", "[a\",~,]");
    }
    
    private void test(String aExpected, String aToStandardize) {
        DelString toStandardize = new DelString(aToStandardize);
        new Diacritics().process(toStandardize);

        assertEquals(aExpected, toStandardize.clean() );
    }

    @Test
    public void testProcess() {
    }
    
}
