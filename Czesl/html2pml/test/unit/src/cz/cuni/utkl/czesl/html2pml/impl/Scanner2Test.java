package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.DelString;
import cz.cuni.utkl.czesl.html2pml.impl.Scanner;
import org.purl.jh.util.col.Cols;
import java.util.BitSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.jh.util.str.Strings;
import static org.junit.Assert.*;

/**
 *
 * @author jirka
 */
public class Scanner2Test {

    public Scanner2Test() {
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

    /**
     * Test of next method, of class Scanner.
     */
    @Test
    public void testNext() {
        // '/' mark token boundaries (used in test only, removed before tokenization)
        test("aa/ /bb");

        test("aa/ /<img>/ /bb");
        //?? test("aa/ /a<img>/ /bb");
        test("aa/ /<img :-)>/ /bb");
        test("aa/ /<.>/ /bb");
        test("aa/ /a/<.>/ /bb");

        test("aa/ /a/./ /bb");
        test("aa/ /a/,/ /bb");
        test("aa/ /a/{.|,}/ /bb");
        //test("aa/ /a/{.}/ /bb"); fails - we have no way to encode it anyway
        test("aa/ /a/{.|!|,}/ /bb");
        test("aa/ /a|b/ /bb");
        test("aa/ /{a|b}/ /bb");
        test("aa/ /2|2/ /bb");
        test("aa/ /{2|2}/ /bb");

// does not work        
//        test("aa/ /{K|(}/ /bb"); 
//        test("aa/ /a/{.|a}/ /bb"); 
//        test("aa/ /a{a|.}/ /bb"); 
//        test("aa/ /a/{.|)}/ /bb"); 
//        test("aa/ /a{(|.}/ /bb"); 

        test("aa/ /a{ }b/ /bb");
        test("aa/ /a{ }b{ }c/ /bb");
        test("aa/ /a{b}c/ /bb");
        test("aa/ /{a}bc/ /bb");
        test("aa/ /ab{c}/ /bb");
        test("aa/ /a{b|}c/ /bb");
        test("aa/ /a{b|b|}c/ /bb");

        test("aa/ /<bar>/bc/ /bb");
        test("aa/ /a/<bar>/c/ /bb");
        test("aa/ /ab/<bar>/ /bb");

       test("Mamka/ /a||Eva/ /vaří");
        test("Mamka/ /{a||Eva|a||eva}/ /vaří");
        test("Mamka/ /a||{E|eva}/ /vaří");
        
        
        test("aa/ /(a)bc/ /bb");
        test("aa/ /a(b)c/ /bb");
        test("aa/ /ab(c)/ /bb");
        test("aa/ /(/ab/)/ /bb");
        test("aa/ /a/)/ /bb");
        test("aa/ /a/)/b/ /bb");

        // dash
        test("aa/ /a-b/ /bb");
        test("aa/ /a—b/ /bb");
        test("aa/ /aa-bc/ /bb");
        test("aa/ /aa—bc/ /bb");
        test("aa/ /2-3/ /bb");      // see more lower
        test("aa/ /1980-89/ /bb");
        test("aa/ /AK40-b/ /bb");

        test("aa/ /xy/?/ /bb");
        test("aa/ /xy/??/ /bb");
        test("aa/ /xy/?!?/ /bb");
        test("aa/ /xy/../??/ /bb");
        test("aa/ /**/xy/**/??/ /bb");
        test("aa/ /++/xy/++/??/ /bb");
        test("aa/ /--/xy/++/??/ /bb");
        test("aa/ /--/*/xy/+++/??/ /bb");
        
        
        //numbers
        test("aa/ /11/ /bb");
        test("aa/ /+11/ /bb");
        test("aa/ /-11/ /bb");
        test("aa/ /11.3/ /bb");
        test("aa/ /+11.3/ /bb");
        test("aa/ /-11.3/ /bb");
        test("aa/ /11,3/ /bb");
        test("aa/ /+11,3/ /bb");
        test("aa/ /-11,3/ /bb");
        test("aa/ /11,000.3/ /bb");
        test("aa/ /+11,000.3/ /bb");
        test("aa/ /-11,000.3/ /bb");
//x        test("aa/ /11.000,3/ /bb"); fails
        test("aa/ /+11.000,3/ /bb");
        test("aa/ /-11.000,3/ /bb");

        test("aa/ /AK40/ /bb");
        test("aa/ /AK40a/ /bb");
        test("aa/ /11a/ /bb");
        test("aa/ /+/11a/ /bb");
        test("aa/ /-/11a/ /bb");
        test("aa/ /11/./3a/ /bb");
        test("aa/ /+11/./3a/ /bb");
        test("aa/ /-11/./3a/ /bb");
        test("aa/ /11/,/3a/ /bb");
        test("aa/ /+11/,/3a/ /bb");
        test("aa/ /-11/,/3a/ /bb");
        test("aa/ /11,000/./3a/ /bb");
        test("aa/ /+11,000/./3a/ /bb");
        test("aa/ /-11,000/./3a/ /bb");
        test("aa/ /11.000/,/3a/ /bb"); 
        test("aa/ /+11.000/,/3a/ /bb");
        test("aa/ /-11.000/,/3a/ /bb");
        test("aa/ /11 000/./3a/ /bb");
        test("aa/ /+11 000/./3a/ /bb");
        test("aa/ /-11 000/./3a/ /bb");
        test("aa/ /11 000 127.2/./3a/ /bb");

        // dates
        test("aa/ /1/./6/./2005/ /bb");
        test("aa/ /19/./6/./2005/ /bb");
        test("aa/ /1/./10/./2005/ /bb");
        test("aa/ /19/./10/./2005/ /bb");
        test("aa/ /1/./6/./05/ /bb");
        test("aa/ /19/./6/./05/ /bb");
        test("aa/ /1/./6/./11/ /bb");
        test("aa/ /19/./6/./11/ /bb");
        
        test("aa/ /1/./6/./2005");
        test("aa/ /19/./6/./2005");
        test("aa/ /1/./10/./2005");
        test("aa/ /19/./10/./2005");
        test("aa/ /1/./6/./05");
        test("aa/ /19/./6/./05");
        test("aa/ /1/./6/./11");
        test("aa/ /19/./6/./11");

        // not dates
        test("aa/ /41.6/ /bb");
        test("aa/ /1.36/ /bb");
//x        test("aa/ /1.111/ /bb");  fails
        test("aa/ /59.6/./11/ /bb");    //  ??? this is the answer, hard to say what it should be
        
        // ip addresses (why not :)
        test("aa/ /128.12.136.254/ /bb");      
        test("aa/ /128.12/./136/./254a/ /bb");

        // expressions
        test("aa/ /1-2/ /bb");
        test("aa/ /1-20/ /bb");
        test("aa/ /1+2/ /bb");
        test("aa/ /1+20/ /bb");
        test("aa/ /1.5+2/ /bb");
        test("aa/ /1+2=3/ /bb");
        test("aa/ /1-4*5+3=x/ /bb");
//        test("aa| |1-4/5+3=1| |bb", "|");
        test("aa/ /1x+2y=3/ /bb");
        
//        test("aa {bb}<dt> cc",                "aa/ /bb:<dt>/ /cc", "/");
//        test("{bba}<dt> cc",                   "bba:<dt>/ /cc", "/");
//        test("aa {bb bb bb}<dt> cc",          "aa/ /bb bb bb:<dt>/ /cc", "/");
//        test("aa {bb b{x|y}b bb}<dt> cc",     "aa/ /bb b{x|y}b bb:<dt>/ /cc", "/");
//        test("aa {bb}<dt bel cz> cc",         "aa/ /bb:<dt bel cz>/ /cc", "/");
//        test("aa {bb bb bb}<dt bel frg> cc",  "aa/ /bb bb bb:<dt bel frg>/ /cc", "/");
//        test("aa {bb b{x|y}b bb}<dt> cc",     "aa/ /bb b{x|y}b bb:<dt>/ /cc", "/");
//
//        test("aa {bb b{x|y}b bb}<priv> cc",   "aa/ /bb b{x|y}b bb:<priv>/ /cc", "/");
//        test("aa {bb b{x|y}b bb}<pr> cc",     "aa/ /bb b{x|y}b bb:<pr>/ /cc", "/");
       
        test("aa/ /<a b c>/ /bb");
        test("aa/,/ /<a b c>/ /bb/!/ /cc");
        test("aa/,/ /<img :-)>/ /bb/!/ /cc");
        test("aa/,/ /dd{ }ee/ /<a b c>/ /bb/!/ /cc");
        test("aa/,/ /dd{ }ee{ }z/ /<a b c>/ /bb/!/ /cc");
        //todo: report an error (impossible word-final opt space): test("dd{ }ee{ } ab");
        //todo: no way to handle higher but should not be on input: test("dd{ }ee{ }. ab");
        test("aa/,/ /bb/<.>/ /cc/ /dd/ /<.>/ /ee{ }ff/ /<.>");
        test("aa/,/ /a{bb|cc}a/ /dd/ /<.>/ /ee{ }ff/ /<.>");

        test("aa/,/ /bb(a)");
        test("aa/,/ /(/aa/)/ /bb(a)");
        test("aa/<bar>/aa/ /bb(a)");
    }

    private void test(String aStr) {
        test(aStr, aStr.split("/"));
    }
    
    private void test(String aStr, String ... aExpected) {
        final StringBuilder sb = new StringBuilder(aStr.replace("/", ""));

        // randomly insert stretches of ignored chars (here marked as 0)
        for (int i = 0; i < 3; i++) {
            int from = rand(sb.length());
            int len  = rand(10);
            sb.insert(from, Strings.repeatChar('\u0000', len));
        }
        
        // create the corresponding map
        final BitSet bitSet = new BitSet(sb.length());
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\u0000') bitSet.set(i);
        }
        
        DelString dirty = new DelString(sb.toString(), bitSet);
        
        test(dirty, aExpected);
    }
    
    public static int rand(int aMax) {
        return (int) Math.round( Math.random() * aMax );        
    }
    
    
    /** Check that a string is segmented into a specified list of expected strings */
    private void test(DelString aStr, String ... aExpected) {
        final Scanner scanner = new Scanner(aStr);
//        System.out.println("Tokenize: \n" + aStr.cleanDebug());
//        System.out.println(aStr.clean());

        for (String str : aExpected) {
            Scanner.Token token = scanner.next();
            
            String tokenStr = null;
            if (token.text != null) {
                tokenStr = token.text;
            }
            else if (token.code != null) {
                tokenStr = token.code.toString();
            }
            else if (token.codes != null) {
                tokenStr = Cols.toString(token.codes, "", "", "", "");
            }
            
//            System.out.printf("tokenStr0: '%s'\n", tokenStr);

            assertEquals(str, tokenStr);
        }
    }


}