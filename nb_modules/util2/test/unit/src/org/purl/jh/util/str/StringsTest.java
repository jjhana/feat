/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util.str;

import junit.framework.TestCase;
import org.purl.jh.util.UtilityBase;

/**
 *
 * @author Jirka
 */
public class StringsTest extends TestCase {
    
    public StringsTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of newLine method, of class Strings.
     */
    public void testNewLine() {
    }

    /**
     * Test of comparator method, of class Strings.
     */
    public void testComparator() {
    }

    /**
     * Test of toList method, of class Strings.
     */
    public void testToList() {
    }

    /**
     * Test of repeatChar method, of class Strings.
     */
    public void testRepeatChar() {
    }

    /**
     * Test of spaces method, of class Strings.
     */
    public void testSpaces() {
    }

    /**
     * Test of removeEnding method, of class Strings.
     */
    public void testRemoveEnding() {
    }

    /**
     * Test of concat method, of class Strings.
     */
    public void testConcat_3args_1() {
    }

    /**
     * Test of concat method, of class Strings.
     */
    public void testConcat_3args_2() {
    }

    /**
     * Test of concat method, of class Strings.
     */
    public void testConcat_StringArr() {
    }

    /**
     * Test of setCharAt method, of class Strings.
     */
    public void testSetCharAt() {
    }

    /**
     * Test of split method, of class Strings.
     */
    public void testSplit_String() {
    }

    /**
     * Test of splitL method, of class Strings.
     */
    public void testSplitL_String() {
    }

    /**
     * Test of split method, of class Strings.
     */
    public void testSplit_String_char() {
    }

    /**
     * Test of splitL method, of class Strings.
     */
    public void testSplitL_String_char() {
    }

    /**
     * Test of splitL method, of class Strings.
     */
    public void testSplitL_String_Pattern() {
    }

    /**
     * Test of splitLL method, of class Strings.
     */
    public void testSplitLL() {
    }

    /**
     * Test of splitIntoTwo method, of class Strings.
     */
    public void testSplitIntoTwo() {
    }

    /**
     * Test of splitIntoTwoB method, of class Strings.
     */
    public void testSplitIntoTwoB() {
    }

    /**
     * Test of doubleSplit method, of class Strings.
     */
    public void testDoubleSplit() {
    }

    /**
     * Test of splitIntoPairs method, of class Strings.
     */
    public void testSplitIntoPairs() {
    }

    /**
     * Test of indexOfPlus2 method, of class Strings.
     */
    public void testIndexOfPlus2_4args() {
    }

    /**
     * Test of indexOfPlus2 method, of class Strings.
     */
    public void testIndexOfPlus2_5args() {
    }

    /**
     * Test of lastChar method, of class Strings.
     */
    public void testLastChar() {
    }

    /**
     * Test of charAtB method, of class Strings.
     */
    public void testCharAtB() {
    }

    /**
     * Test of substringB method, of class Strings.
     */
    public void testSubstringB() {
    }

    /**
     * Test of replaceHead method, of class Strings.
     */
    public void testReplaceHead() {
    }

    /**
     * Test of removeHead method, of class Strings.
     */
    public void testRemoveHead() {
    }

    /**
     * Test of removePossHead method, of class Strings.
     */
    public void testRemovePossHead() {
    }

    /**
     * Test of getTail method, of class Strings.
     */
    public void testGetTail() {
    }

    /**
     * Test of replaceTail method, of class Strings.
     */
    public void testReplaceTail() {
    }

    /**
     * Test of removeTail method, of class Strings.
     */
    public void testRemoveTail_String_int() {
    }

    /**
     * Test of removeTail method, of class Strings.
     */
    public void testRemoveTail_String_String() {
    }

    /**
     * Test of removePossTail method, of class Strings.
     */
    public void testRemovePossTail() {
    }

    /**
     * Test of insert method, of class Strings.
     */
    public void testInsert() {
    }

    /**
     * Test of insertB method, of class Strings.
     */
    public void testInsertB() {
    }

    /**
     * Test of delete method, of class Strings.
     */
    public void testDelete() {
    }

    /**
     * Test of deleteChar method, of class Strings.
     */
    public void testDeleteChar() {
    }

    /**
     * Test of deleteCharB method, of class Strings.
     */
    public void testDeleteCharB() {
    }

    /**
     * Test of in method, of class Strings.
     */
    public void testIn() {
    }

    /**
     * Test of charIn method, of class Strings.
     */
    public void testCharIn() {
    }

    /**
     * Test of charInB method, of class Strings.
     */
    public void testCharInB() {
    }

    /**
     * Test of allCharsIn method, of class Strings.
     */
    public void testAllCharsIn() {
    }

    /**
     * Test of someCharsShared method, of class Strings.
     */
    public void testSomeCharsShared() {
    }

    /**
     * Test of inString method, of class Strings.
     */
    public void testInString() {
    }

    /**
     * Test of format method, of class Strings.
     */
    public void testFormat_String_int() {
    }

    /**
     * Test of limit method, of class Strings.
     */
    public void testLimit() {
    }

    /**
     * Test of trim method, of class Strings.
     */
    public void testTrim_String() {
        assertEquals("aa", Strings.trim("aa"));
        assertEquals("aa", Strings.trim(" aa "));
        assertEquals("aa", Strings.trim("aa "));
        assertEquals("aa", Strings.trim(" aa"));
        assertEquals("", Strings.trim(""));

        assertEquals("aa", Strings.trim(" \u00A0 aa \u00A0\u00A0  "));
        assertEquals("aa", Strings.trim(" \u00A0 aa \u00A0 \u00A0 "));
        assertEquals("aa", Strings.trim("\u00A0aa\u00A0\u00A0"));

//        UtilityBase timing = new UtilityBase() {
//            String x;
//            public void goE() {
//                System.out.println("=========");
//                profileStart();
//                for (int i = 0; i<100000; i++) {
//                    String str = "a" + i + "b";
//                    x = str.trim();
//                }
//                profileEnd("Traditional %f\n");
//
//                profileStart();
//                for (int i = 0; i<100000; i++) {
//                    String str = "a" + i + "b";
//                    x = Strings.trim(str);
//                }
//                profileEnd("New %f\n");
//
//                profileStart();
//                for (int i = 0; i<100000; i++) {
//                    String str = "a" + i + "b";
//                    x = str.trim();
//                }
//                profileEnd("Traditional %f\n");
//
//                profileStart();
//                for (int i = 0; i<100000; i++) {
//                    String str = "a" + i + "b";
//                    x = Strings.trim(str);
//                }
//                profileEnd("New %f\n");
//            }
//        };
//
//        timing.go();
    }



    public void test_isWhitespace() {
        assertTrue(Strings.isWhitespace(' '));
        assertTrue(Strings.isWhitespace('\t'));
        assertTrue(Strings.isWhitespace('\n'));
        assertTrue(Strings.isWhitespace('\u00A0'));

    }

    public void test_wspattern() {
        System.out.println(Strings.wspattern("abc\\sef").toString() );
        System.out.println(Strings.wspattern("abc\\s+ef").toString() );
        System.out.println(Strings.wspattern("\\sa\\s").toString() );
        System.out.println(Strings.wspattern("abc").toString() );

        assertEquals("abc[\\s\\xA0]ef", Strings.wspattern("abc\\sef").toString() );
        assertEquals("abc[\\s\\xA0]+ef", Strings.wspattern("abc\\s+ef").toString() );
        assertEquals("[\\s\\xA0]a[\\s\\xA0]", Strings.wspattern("\\sa\\s").toString() );
        assertEquals("abc", Strings.wspattern("abc").toString() );
    }

    /**
     * Test of trim method, of class Strings.
     */
    public void testTrim_String_int() {
    }

    /**
     * Test of breakIntoLines method, of class Strings.
     */
    public void testBreakIntoLines() {
    }

    /**
     * Test of format method, of class Strings.
     */
    public void testFormat_4args() {
    }

    /**
     * Test of findUniqueId method, of class Strings.
     */
    public void testFindUniqueId() {
    }

    /**
     * Test of splitId method, of class Strings.
     */
    public void testSplitId() {
    }

    /**
     * Test of findNextId method, of class Strings.
     */
    public void testFindNextId() {
    }

}
