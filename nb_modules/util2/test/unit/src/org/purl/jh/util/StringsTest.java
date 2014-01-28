package org.purl.jh.util;

import org.purl.jh.util.str.StringPair;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.str.Cap;
import org.purl.jh.util.str.Caps;
import org.purl.jh.util.str.Strings;


/**
 *
 * @author Jiri
 */
public class StringsTest extends TestCase {
    
    public StringsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {

        junit.framework.TestSuite suite = new junit.framework.TestSuite(StringsTest.class);
        
        return suite;
    }
    
// =============================================================================
// Static variables
// =============================================================================
    
    public void testStatic() {
        List<String> list = Arrays.asList("a", "ax", "b", "aax", "aaaaax", "bax", "bx", "x", "aa");

        Collections.sort(list, Strings.tailLexOrder);
        assertEquals(Cols.toString(list), "[a, aa, b, x, ax, aax, aaaaax, bax, bx]"); 
        
        Collections.sort(list, Strings.tailLexOrderRL);
        assertEquals(Cols.toString(list), "[aa, a, b, aaaaax, aax, bax, ax, bx, x]"); 
    }
    
// =============================================================================
//
// =============================================================================
    
    

    public void testRepeatChar() {
        assertEquals(Strings.repeatChar('a', 0), "");
        assertEquals(Strings.repeatChar('a', 1), "a");
        assertEquals(Strings.repeatChar('a', 5), "aaaaa");
    }

    public void testSpaces() {
        assertEquals(Strings.spaces(0), "");
        assertEquals(Strings.spaces(1), " ");
        assertEquals(Strings.spaces(5), "     ");
    }

    public void testRemoveEnding() {
        fail("The test case is a prototype.");
    }
    

    public void testConcat() {
        assertEquals(Strings.concat("ABCD", "EFGH", "IJKL"), "ABCDEFGHIJKL");
        assertEquals(Strings.concat("ABCD", 'e', "IJKL"), "ABCDeIJKL");
        assertEquals(Strings.concat("AB", "CD", "EF", "GH", "IJ", "KL"), "ABCDEFGHIJKL");
    }
    
    
// -----------------------------------------------------------------------------
// Spliting
// -----------------------------------------------------------------------------
    
    public void testSplit() {
    }

    public void testSplitL() {
    }

    public void testSplitIntoTwo(){
        assertEquals(Strings.splitIntoTwo("aaa^bbb",     '^'), new StringPair("aaa", "bbb"));
        assertEquals(Strings.splitIntoTwo("aaa^bbb^ccc", '^'), new StringPair("aaa", "bbb^ccc"));
        assertEquals(Strings.splitIntoTwo("aaa^",        '^'), new StringPair("aaa", ""));
        assertEquals(Strings.splitIntoTwo("aaa",         '^'), new StringPair("aaa", ""));
    }
    
    public void testDoubleSplit() {
        assertEquals(Cols.toString(Strings.doubleSplit("a/b/c:d/e/f", ':', '/')), "[[a, b, c], [d, e, f]]");
        assertEquals(Cols.toString(Strings.doubleSplit("a,b,c;d,e,f", ';', ',')), "[[a, b, c], [d, e, f]]");
        assertEquals(Cols.toString(Strings.doubleSplit("a/b/c", ':', '/')), "[[a, b, c]]");
        assertEquals(Cols.toString(Strings.doubleSplit("a:d", ':', '/')), "[[a], [d]]");
        assertEquals(Cols.toString(Strings.doubleSplit("a", ':', '/')), "[[a]]");
    }
    
// -----------------------------------------------------------------------------
// Accessing from the back
// -----------------------------------------------------------------------------
    
    public void testLastChar() {
        assertEquals(Strings.lastChar("abcd"), 'd');
        assertEquals(Strings.lastChar("d"), 'd');
        //@todo assertException(Strings.lastChar(""));
    }

    public void testCharAtB() {
        assertEquals(Strings.charAtB("abcd",0), 'd');
        assertEquals(Strings.charAtB("abcd",1), 'c');
        assertEquals(Strings.charAtB("abcd",3), 'a');
        //@todo assertException(Strings.charAtB("abcd",4));
        assertEquals(Strings.charAtB("d",0), 'd');
        //@todo assertException(Strings.lastChar(""));
    }
    
    
    public void testSubstringB() {
     assertEquals( Strings.substringB("hamburger",0, 0), "hamburger");
     assertEquals( Strings.substringB("hamburger",0, 1), "hamburge");
     assertEquals( Strings.substringB("hamburger",1, 2), "amburg");
    }
    
    
// -----------------------------------------------------------------------------
// Head, Tail
// -----------------------------------------------------------------------------
    
    public void testReplaceHead() {
        assertEquals(Strings.replaceHead("abcde", "ab",    "xyz"), "xyzcde"); 
        assertEquals(Strings.replaceHead("abcde", "abcde", "xyz"), "xyz"); 
        assertEquals(Strings.replaceHead("abcde", "ee",    "xyz"), "xyzcde"); 
    }

    public void testReplaceTail() {
        assertEquals(Strings.replaceTail("abcde", "de",    "xyz"), "abcxyz"); 
        assertEquals(Strings.replaceTail("abcde", "abcde", "xyz"), "xyz"); 
        assertEquals(Strings.replaceTail("abcde", "ee",    "xyz"), "abcxyz"); 
    }

    public void testRemovePosHead() {
        assertEquals(Strings.removePossHead("abcde", "ab"), "cde");
        assertEquals(Strings.removePossHead("abcde", "fg"), "abcde");
        assertEquals(Strings.removePossHead("abcde", ""),   "abcde");
        assertEquals(Strings.removePossHead("", "de"),      "");
    }
    
    
    public void testRemoveTail() {
        assertEquals(Strings.removeTail("abcde", 0), "abcde");
        assertEquals(Strings.removeTail("abcde", 1), "abcd");
        assertEquals(Strings.removeTail("abcde", 3), "ab");
        assertEquals(Strings.removeTail("abcde", 5), "");
    }
    
    public void testRemovePosTail() {
        assertEquals(Strings.removePossTail("abcde", "de"), "abc");
        assertEquals(Strings.removePossTail("abcde", "fg"), "abcde");
        assertEquals(Strings.removePossTail("abcde", ""),   "abcde");
        assertEquals(Strings.removePossTail("", "de"),      "");
    }
    
    
// -----------------------------------------------------------------------------
// Inserting, deleting
// -----------------------------------------------------------------------------
    
    public void testInsert() {
        assertEquals(Strings.insert("abcd", 0, 'x'), "xabcd" );
        assertEquals(Strings.insert("abcd", 1, 'x'), "axbcd" );
        assertEquals(Strings.insert("abcd", 2, 'x'), "abxcd" );
        assertEquals(Strings.insert("abcd", 4, 'x'), "abcdx" );
    }
    
    public void testInsertB() {
        assertEquals(Strings.insertB("abcd", 0, 'x'), "abcdx" );
        assertEquals(Strings.insertB("abcd", 1, 'x'), "abcxd" );
        assertEquals(Strings.insertB("abcd", 2, 'x'), "abxcd" );
        assertEquals(Strings.insertB("abcd", 4, 'x'), "xabcd" );
    }
    
    public void testDelete() {
        assertEquals(Strings.delete("abcdefg", 0, 2), "defg");
        assertEquals(Strings.delete("abcdefg", 2, 4), "abfg");
        assertEquals(Strings.delete("abcdefg", 2, 2), "abdefg");
        assertEquals(Strings.delete("abcdefg", 2, 6), "ab");
        assertEquals(Strings.delete("abcdefg", 0, 6), "");
    }

    public void testDeleteCharTest() {
        assertEquals(Strings.deleteChar("abcdefg", 0), "bcdefg");
        assertEquals(Strings.deleteChar("abcdefg", 2), "abdefg");
        assertEquals(Strings.deleteChar("abcdefg", 6), "abcdef");
    }
    
    public void  testDeleteCharB() {
        assertEquals(Strings.deleteCharB("abcdefg", 0), "abcdef");
        assertEquals(Strings.deleteCharB("abcdefg", 2), "abcdfg");
        assertEquals(Strings.deleteCharB("abcdefg", 6), "bcdefg");
    }
    
// -----------------------------------------------------------------------------    
// Testing inclusion
// -----------------------------------------------------------------------------    
    
    public void testCharIn() {
        assertTrue( Strings.charIn("abcd", 0, "xyaz"));
        assertFalse(Strings.charIn("abcd", 1, "xyaz"));
        assertTrue( Strings.charIn("a", 0, "xyaz"));
        assertFalse(Strings.charIn("b", 0, "xyaz"));
        assertFalse(Strings.charIn("a", 0, ""));
    }

    public void testCharInB() {
        assertTrue( Strings.charInB("abcd", 0, "xydz"));
        assertFalse(Strings.charInB("abcd", 1, "xydz"));
        assertTrue( Strings.charInB("a", 0, "xyaz"));
        assertFalse(Strings.charInB("b", 0, "xyaz"));
        assertFalse(Strings.charInB("a", 0, ""));
    }

    public void testAllCharsIn() {
        assertTrue(Strings.allCharsIn("abcd", "axbcdy"));
        assertTrue(Strings.allCharsIn("a", "axbcdy"));
        assertTrue(Strings.allCharsIn("a", "a"));
        assertTrue(Strings.allCharsIn("", "a"));

        assertFalse(Strings.allCharsIn("abcd", ""));
        assertFalse(Strings.allCharsIn("abcdw", "axbcdy"));
    }

    public void testSomeCharsIn() {
        assertTrue(Strings.someCharsShared("abcd", "axy"));
        assertTrue(Strings.someCharsShared("a", "axy"));

        assertFalse(Strings.someCharsShared("abcd", ""));
        assertFalse(Strings.someCharsShared("", "axy"));
        assertFalse(Strings.someCharsShared("abcd", "xyz"));
    }
    
    
    public void testInString() {
        assertTrue(Strings.inString('a', "abcd"));
        assertTrue(Strings.inString('d', "abcd"));
        assertFalse(Strings.inString('z', "abcd"));
    }

    
// =============================================================================
// Special  <editor-fold desc="Format">"
// =============================================================================
    
    public void testTrim() {
        assertEquals(Strings.trim("abcd",  2), "ab");
        assertEquals(Strings.trim("abcd",  4), "abcd");
        assertEquals(Strings.trim("abcd", 10), "abcd");
        assertEquals(Strings.trim("abcd",  0), "");
        assertEquals(Strings.trim("", 2), "");
        assertEquals(Strings.trim("", 0), "");
    }    

    public void testBreakIntoLines() {
        //List<String> result = Strings.breakIntoLines(aString, aWidth);
        fail("The test case is a prototype.");
    }

    public void testFormat() {
        //String result = Strings.format(aString, aFirstInd, aInd, aWidth);
        fail("The test case is a prototype.");
    }
    
// =============================================================================
// Special  <editor-fold desc="Special">"
// =============================================================================

    public void testFindUniqueId() {
        List<String> existingIds = Arrays.asList("a", "b", "a1", "a.1", "a.2", "a.4");

        assertEquals("c",     Strings.findUniqueId("c", existingIds, "."));
        assertEquals("a2",    Strings.findUniqueId("a", existingIds, ""));
        assertEquals("a.3",   Strings.findUniqueId("a", existingIds, "."));
        assertEquals("a.3",   Strings.findUniqueId("a.3", existingIds, "."));
        assertEquals("a.4.1", Strings.findUniqueId("a.4", existingIds, "."));
    }

    public void testSplitId() {
        assertEquals(p("c", 1),     Strings.splitId("c1"));
        assertEquals(p("c", 12),    Strings.splitId("c12"));
        assertEquals(p("abc", 12),  Strings.splitId("abc12"));
        assertEquals(p("", 12),     Strings.splitId("12"));
        assertEquals(p("abc", null),    Strings.splitId("abc"));
        assertEquals(p("", null),       Strings.splitId(""));
    }
    
    private PairC<String,Integer> p(String aStr, Integer aCounter) {
        return new PairC<String,Integer>(aStr, aCounter);
    }

    public void testFindNextId() {
        List<String> existingIds = Arrays.asList("a", "b", "a1", "a.1", "a.2", "a.4");

        assertEquals("ac2",     Strings.findNextId("ac1", existingIds));
        assertEquals("ac13",     Strings.findNextId("ac12", existingIds));
        assertEquals("ac1",     Strings.findNextId("ac", existingIds));
        assertEquals("13",     Strings.findNextId("12", existingIds));
        assertEquals("1",     Strings.findNextId("", existingIds));
        
        assertEquals("a.3",     Strings.findNextId("a.1", existingIds));
        assertEquals("a.13",     Strings.findNextId("a.12", existingIds));
        assertEquals("a.5",     Strings.findNextId("a.4", existingIds));
        assertEquals("a2",     Strings.findNextId("a", existingIds));
    }

    /**
     * Test of setCharAt method, of class Strings.
     */
    public void testSetCharAt() {
        System.out.println("setCharAt");
        String aStr = "";
        int aIdx = 0;
        char aChar = ' ';
        String expResult = "";
        String result = Strings.setCharAt(aStr, aIdx, aChar);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitLL method, of class Strings.
     */
    public void testSplitLL() {
        System.out.println("splitLL");
        String aString = "";
        Pattern aPattern = null;
        List<String> expResult = null;
        List<String> result = Strings.splitLL(aString, aPattern);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitIntoTwoB method, of class Strings.
     */
    public void testSplitIntoTwoB() {
        System.out.println("splitIntoTwoB");
        String aString = "";
        char aSeparator = ' ';
        StringPair expResult = null;
        StringPair result = Strings.splitIntoTwoB(aString, aSeparator);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitIntoPairs method, of class Strings.
     */
    public void testSplitIntoPairs() {
        System.out.println("splitIntoPairs");
        String aStr = "";
        String aSep1 = "";
        char aSep2 = ' ';
        List<StringPair> expResult = null;
        List<StringPair> result = Strings.splitIntoPairs(aStr, aSep1, aSep2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of indexOfPlus2 method, of class Strings.
     */
    public void testIndexOfPlus2() {
        System.out.println("indexOfPlus2");
        String aStr = "";
        String aSubStr = "";
        char aCh1 = ' ';
        char aCh2 = ' ';
        int expResult = 0;
        int result = Strings.indexOfPlus2(aStr, aSubStr, aCh1, aCh2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeHead method, of class Strings.
     */
    public void testRemoveHead() {
        System.out.println("removeHead");
        String aString = "";
        String aHead = "";
        String expResult = "";
        String result = Strings.removeHead(aString, aHead);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePossHead method, of class Strings.
     */
    public void testRemovePossHead() {
        System.out.println("removePossHead");
        String aString = "";
        String aPossibleHead = "";
        String expResult = "";
        String result = Strings.removePossHead(aString, aPossibleHead);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTail method, of class Strings.
     */
    public void testGetTail() {
        System.out.println("getTail");
        String aString = "";
        int aLen = 0;
        String expResult = "";
        String result = Strings.getTail(aString, aLen);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePossTail method, of class Strings.
     */
    public void testRemovePossTail() {
        System.out.println("removePossTail");
        String aString = "";
        String aPossibleTail = "";
        String expResult = "";
        String result = Strings.removePossTail(aString, aPossibleTail);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of capitalization method, of class Strings.
     */
    public void testCapitalization() {
        System.out.println("capitalization");
        String aString = "";
        Cap expResult = null;
        Cap result = Caps.capitalization(aString);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }    
}
