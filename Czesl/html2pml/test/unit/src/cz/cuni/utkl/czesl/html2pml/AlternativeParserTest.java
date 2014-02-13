package cz.cuni.utkl.czesl.html2pml;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author j
 */
public class AlternativeParserTest {
    private final AlternativeParser altParser = new AlternativeParser();
    
    public AlternativeParserTest() {
    }

    @Test
    public void testParseAlternatives() {
        test("abc", "abc");

        test("abc|def", "abc", "def");
        test("abc|de|f", "abc", "de", "f");
        
        test("a{b|bb}c",  "abc", "abbc");
        test("a{b|bb|bbb}c", "abc", "abbc", "abbbc");
        test("a{b|}c",  "abc", "ac");
        test("a{|b}c",  "ac", "abc");
        test("a{b}c",   "ac", "abc");
        test("a{ }c",   "ac", "a c");

        test("a{b}c{d}",   "ac", "abc", "acd", "abcd");
        test("a{b|B}c{d}",   "abc", "aBc", "abcd", "aBcd");
        test("a{b|B}c{d|D}",   "abcd", "aBcd", "abcD", "aBcD");
    }
    
    private void test(String aAltString, String ... aExpResult) {
        List<String> results = new ArrayList<>( altParser.parseAlternatives(aAltString) );
        Collections.sort(results);
        List<String> expected = new ArrayList<>( asList(aExpResult) );
        Collections.sort(expected);

        assertEquals(expected, results);
    }
    
    @Test
    public void testParseOpts() {
        testOpt(" ",  "", " ");
        testOpt("a",  "", "a");
        testOpt("aa",  "", "aa");
        testOpt("a|b",  "a", "b");
        testOpt("a|bcd",  "a", "bcd");
        testOpt("a|b|c",   "a", "b", "c");
        testOpt("a|b|c|d",   "a", "b", "c", "d");
    }

    private void testOpt(String optString, String ... aExpResult) {
        List<String> results = altParser.parseOpts(optString);
        assertEquals(asList(aExpResult), results);
    }
    
    @Test
    public void testIsBalanced() {
        assertTrue(AlternativeParser.isBalanced("abc"));
        assertTrue(AlternativeParser.isBalanced("a{b}c"));
        assertTrue(AlternativeParser.isBalanced("a{{b}}c"));
        assertTrue(AlternativeParser.isBalanced("a{}c"));
        assertTrue(AlternativeParser.isBalanced("a{{}}c"));
        assertTrue(AlternativeParser.isBalanced("a{ }c"));
        assertTrue(AlternativeParser.isBalanced("a{b}c{d}e"));
        assertTrue(AlternativeParser.isBalanced("a{b}c{d}"));
        assertTrue(AlternativeParser.isBalanced("a{b{o}o}c{d}"));

        assertTrue(AlternativeParser.isBalanced("{K|(}"));
        
        assertFalse(AlternativeParser.isBalanced("{"));
        assertFalse(AlternativeParser.isBalanced("}"));
        assertFalse(AlternativeParser.isBalanced("a{b}}c"));
        assertFalse(AlternativeParser.isBalanced("a{bc"));
        assertFalse(AlternativeParser.isBalanced("a{{b}c"));
        assertFalse(AlternativeParser.isBalanced("ab}c"));
        assertFalse(AlternativeParser.isBalanced("a{b}c{de"));
        assertFalse(AlternativeParser.isBalanced("a{b}cd}"));
        assertFalse(AlternativeParser.isBalanced("a{b{o}o}c{d"));
        assertFalse(AlternativeParser.isBalanced("a{b{o}o}c{d}{"));
    } 
}