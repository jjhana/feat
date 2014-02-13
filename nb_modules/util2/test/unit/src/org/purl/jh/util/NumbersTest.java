/*
 * NumbersTest.java
 * JUnit based test
 *
 * Created on September 25, 2004, 12:22 PM
 */

package org.purl.jh.util;

import junit.framework.TestCase;
import junit.framework.*;

/**
 *
 * @author Jiri
 */
public class NumbersTest extends TestCase {
    
    public NumbersTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {

        junit.framework.TestSuite suite = new junit.framework.TestSuite(NumbersTest.class);
        
        return suite;
    }

    /**
     * Test of roman method, of class morph.util.Numbers.
     */
    public void testRoman() {
        assertEquals(Numbers.roman("I"),   1);
        assertEquals(Numbers.roman("II"),  2);
        assertEquals(Numbers.roman("III"), 3);
        assertEquals(Numbers.roman("IIII"),4);
        assertEquals(Numbers.roman("IV"),  4);
        assertEquals(Numbers.roman("V"),   5);
        assertEquals(Numbers.roman("VI"),  6);
        assertEquals(Numbers.roman("VIII"),8);
        assertEquals(Numbers.roman("X"),   10);
        assertEquals(Numbers.roman("XIV"), 14);
        assertEquals(Numbers.roman("XVII"),  17);
        assertEquals(Numbers.roman("XVIII"), 18);
        assertEquals(Numbers.roman("XIX"),   19);
        assertEquals(Numbers.roman("XX"),    20);
        assertEquals(Numbers.roman("XXI"),   21);
        assertEquals(Numbers.roman("MCMXCV"), 1995);
        assertEquals(Numbers.roman("MMMMCMXCIX"), 4999);

        assertEquals(Numbers.roman("i"),   1);
        assertEquals(Numbers.roman("ii"),  2);
        assertEquals(Numbers.roman("iii"), 3);
        assertEquals(Numbers.roman("iiii"),4);
        assertEquals(Numbers.roman("iv"),  4);
        assertEquals(Numbers.roman("v"),   5);
        assertEquals(Numbers.roman("vi"),  6);
        assertEquals(Numbers.roman("viii"),8);
        assertEquals(Numbers.roman("x"),   10);
        assertEquals(Numbers.roman("xiv"), 14);
        assertEquals(Numbers.roman("xvii"),  17);
        assertEquals(Numbers.roman("xviii"), 18);
        assertEquals(Numbers.roman("xix"),   19);
        assertEquals(Numbers.roman("xx"),    20);
        assertEquals(Numbers.roman("xxi"),   21);
        assertEquals(Numbers.roman("mcmxcv"), 1995);
        assertEquals(Numbers.roman("mmmmcmxcix"), 4999);
        
        assertEquals(Numbers.roman("IIIII"), -1);
        assertEquals(Numbers.roman("VV"),    -1);
        assertEquals(Numbers.roman("VX"),    -1);
        assertEquals(Numbers.roman("IXIX"),  -1);
        assertEquals(Numbers.roman("IXXIX"), -1);
        assertEquals(Numbers.roman("IXIIX"), -1);
        assertEquals(Numbers.roman("IIX"),   -1);
        assertEquals(Numbers.roman("IXX"),   -1);
        assertEquals(Numbers.roman("ABC"),   -1);
        assertEquals(Numbers.roman("XABC"),  -1);
        assertEquals(Numbers.roman(""),       -1);

        assertEquals(Numbers.roman("IiIII"), -1);
        assertEquals(Numbers.roman("Vv"),    -1);

    
    }

    /**
     * Test of findPos method, of class morph.util.Numbers.
     */
    public void testFindPos() {

        
        // TODO add your test code below by replacing the default call to fail.
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
