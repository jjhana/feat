package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.Scanner1;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.col.Cols;
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
public class Scanner1Test {
    
    public Scanner1Test() {
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

    //@Test
    public void testWordTokenPattern() {
        Matcher m = Pattern.compile("\\d\\d|\\d+").matcher("12345a");
        //m.find();
        m.matches();
        System.out.println("M: " + m.group());
       
    }

    public static void xmain(String[] args) {
        Pattern dayP = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])(?=\\.(0?[1-9]|1[012])\\.)");   // day in a date (followed by a dot + month)
        Pattern moP = Pattern.compile("(?<=0?[1-9]|[12][0-9]|3[01]\\.)(0[1-9]|1[012])(?=\\.)");    // month in a date (preceded by a day + dot)
        
        Matcher m = dayP.matcher("13.5.");
        System.out.println(m.find());
        System.out.println(m.group());
//        
//
//        for (String pstr : Arrays.asList("a+|a+b", "a+b|a+", "a+bc?|a+")) {
//            Pattern p = Pattern.compile("^(" + pstr + ")");
//        
//            Matcher m = p.matcher("aaabc");
//            
//            if (m.lookingAt()) {
//                System.out.printf("find: %s : %s\n", p.toString(),  m.group());
//                
//            }
//            else {
//                System.out.println("no match: " + p.toString());
//            }
//        }
    }
    
    
    @Test
    public void testNext() {
        // '/' mark token boundaries (used in test only, removed before tokenization)
        test("aa/ /bb");

        test("aa/ /<img>/ /bb");
        //?? test("aa/ /a<img>/ /bb");
        test("aa/ /<img :-)>/ /bb");
        test("aa/ /<.>/ /bb");
        test("aa/ /a/<.>/ /bb");

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

        test("aa/ /(a)bc/ /bb");
        test("aa/ /a(b)c/ /bb");
        test("aa/ /ab(c)/ /bb");
        test("aa/ /(/ab/)/ /bb");
        test("aa/ /(/a/ /a/)/bc/ /bb");
        test("aa/ /a/(/b/ /b/)/c/ /bb");
        test("aa/ /ab/(/c/ /c/)/ /bb");
        test("aa/ /(/ab/ /cd/)/ /bb");
        test("aa/ /ab/ /cd/)/ /bb");
        test("aa/ /(/a/ /bb");
        test("aa/ /a/)/ /bb");
        test("aa/ /a/)/b/ /bb");

        test("aa/ /a'b/ /bb");
        test("aa/ /a'b-c/ /bb");
        test("aa/ /'ab'/ /bb"); // todo '/ab/'

        // dash
        test("aa/ /a-b/ /bb");
        test("aa/ /a—b/ /bb");
        test("aa/ /aa-bc/ /bb");
        test("aa/ /aa—bc/ /bb");
        test("aa/ /2-3/ /bb");      // see more lower
        test("aa/ /1980-89/ /bb");
        test("aa/ /AK40-b/ /bb");

        // punctuation
        test("aa/ /a/./ /bb");
        test("aa/ /a/,/ /bb");
        test("aa/ /a/!/ /bb");
        test("aa/ /a/?/ /bb");
        test("aa/ /a/;/ /bb");
        test("aa/ /\"/a/\"/ /bb");
        //todo test("aa/ /'/a/'/ /bb");
        test("aa/ /a/../ /bb");
        test("aa/ /a/.../ /bb");
        test("aa/ /a/..../ /bb");
        test("aa/ /a/!!/ /bb");
        test("aa/ /a/??/ /bb");
        test("aa/ /a/??!!/ /bb");
        test("aa/ /\"/aa/\"/./ /bb");
        test("aa/ /\"/aa/\"/?/ /bb");
        test("aa/ /\"/aa/\"/??/ /bb");
        test("aa/ /(/a/)/,/ /bb");
        test("aa/ /(/a/)/./ /bb");
        test("aa/ /(/a/)/?/ /bb");
        test("aa/ /(/aa/ /bb/)/,/ /bb");
        test("aa/ /(/aa/ /bb/)/./ /bb");
        test("aa/ /(/aa/ /bb/)/?/ /bb");
       
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
        test("aa/ /11.000,3/ /bb");
        test("aa/ /+11.000,3/ /bb");
        test("aa/ /-11.000,3/ /bb");

        // dates
        test("aa/ /1/./2/./ /bb");
        test("aa/ /01/./2/./ /bb");
        test("aa/ /11/./2/./ /bb");
        test("aa/ /20/./2/./ /bb");
        test("aa/ /30/./2/./ /bb");
        test("aa/ /1/./12/./ /bb");
        test("aa/ /01/./12/./ /bb");
        test("aa/ /11/./12/./ /bb");
        test("aa/ /20/./12/./ /bb");
        test("aa/ /30/./12/./ /bb");

//        test("aa/ /1/./2/./1999/ /bb");
        test("aa/ /01/./2/./1999/ /bb");
        test("aa/ /11/./2/./1999/ /bb");
        test("aa/ /20/./2/./1999/ /bb");
        test("aa/ /30/./2/./1999/ /bb");
        test("aa/ /1/./12/./1999/ /bb");
        test("aa/ /01/./12/./1999/ /bb");
        test("aa/ /11/./12/./1999/ /bb");
        test("aa/ /20/./12/./1999/ /bb");
        test("aa/ /30/./12/./1999/ /bb");
        
        
        test("aa/ /1.32/./ /bb");
        test("aa/ /1.40/./ /bb");
        test("aa/ /12.40/./ /bb");
        test("aa/ /33.11/./ /bb");
        
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
        test("aa| |1-4/5+3=1| |bb", "|");
        test("aa/ /1x+2y=3/ /bb");

        
        test("aa {bb}<dt> cc",                "aa/ /bb:<dt>/ /cc", "/");
        test("{bba}<dt> cc",                   "bba:<dt>/ /cc", "/");
        test("aa {bb bb bb}<dt> cc",          "aa/ /bb bb bb:<dt>/ /cc", "/");
        test("aa {bb b{x|y}b bb}<dt> cc",     "aa/ /bb b{x|y}b bb:<dt>/ /cc", "/");
        test("aa {bb}<dt bel cz> cc",         "aa/ /bb:<dt bel cz>/ /cc", "/");
        test("aa {bb bb bb}<dt bel frg> cc",  "aa/ /bb bb bb:<dt bel frg>/ /cc", "/");
        test("aa {bb b{x|y}b bb}<dt> cc",     "aa/ /bb b{x|y}b bb:<dt>/ /cc", "/");

        test("aa {bb b{x|y}b bb}<priv> cc",   "aa/ /bb b{x|y}b bb:<priv>/ /cc", "/");
        test("aa {bb b{x|y}b bb}<pr> cc",     "aa/ /bb b{x|y}b bb:<pr>/ /cc", "/");

       
        test("aa/ /<a b c>/ /bb");
        test("aa/,/ /<a b c>/ /bb/!/ /cc");
        test("aa/,/ /<img :-)>/ /bb/!/ /cc");
        test("aa/,/ /dd{ }ee/ /<a b c>/ /bb/!/ /cc");
        test("aa/,/ /dd{ }ee{ }z/ /<a b c>/ /bb/!/ /cc");
        test("aa/,/ /{dd ee|ddee}/ /<a b c>/ /bb/!/ /cc");

        
        //todo: report an error (impossible word-final opt space): test("dd{ }ee{ } ab");
        //todo: no way to handle higher but should not be on input: test("dd{ }ee{ }. ab");
        test("aa/,/ /bb/<.>/ /cc/ /dd/ /<.>/ /ee{ }ff/ /<.>");
        test("aa/,/ /a{bb|cc}a/ /dd/ /<.>/ /ee{ }ff/ /<.>");

        test("aa/,/ /bb(a)");
        test("aa/,/ /(/aa/)/ /bb(a)");
        test("aa/<bar>/aa/ /bb(a)");

        // codes: test checks tokenization and code annotation; (abc:<dt> - abc token with <dt> code)

//todo        test("aa/ /XXX/<gr>/ /aa/ /XXX/<gr>/ /bb");
        //todo test("aa/ /pro{c}<gr>házela/ /bb");

       test("a{a}a/ /{a}aa/ /aa{a}");
        test("a{a|b}a/ /{a|b}aa/ /aa{a|b}");
        
        
        // numbers
        test("aa/ /11/ /AK40/ /11a");
        //test("11/ /3.14/ /1,000/ /1,000,000"); unfortunately 3 is treated as month, and 14 as year, look behind would fix this
        test("11/ /3/./14/ /1,000/ /1,000,000");        
        test("3.14/./ /1,000/,/ /1,000,000/!"); 
        //todo? test("1 000/,/ /1 000 000/!");

        test("3/./prosince"); 
        test("3/./ /12/./ /3/./12/./ /13.14");  // not great, but what else?
       
        test("mam|/|nemam| |rad", "|");
//        test("12/13/2009", "|");     // not great, but what else?
        

        // parens
        test("cc/ /(/cc/)/ /c(c)/ /(c)c/ /c(c)c");
        test("cc/ /(/cc/)/,/ /c(c)/,/ /(c)c/,/ /c(c)c/,/ /cc");

        test("cc/ /(/c{cc|dd}c/)/ /c({cc|dd})/ /({cc|dd})c/ /c({cc|dd})c");
        test("cc/ /(/c{cc|dd}c/)/ /c(e{cc|dd})/ /(e{cc|dd})c/ /c(e{cc|dd})c");
        test("cc/ /(/cc/)/,/ /c(c)/,/ /(c)c/,/ /c(c)c/,/ /cc");
    }
    
    
    private void run(String aStr) {
        final Scanner1 scanner = new Scanner1(aStr);
        //System.out.println("Tokenize: \n" + text.cleanDebug());

        for (;;) {
            Scanner1.Token token = scanner.next();
            if (token == null) break;
            
            System.out.printf("'%s' - %s\n", token.text, token.type);
         }
    }
    
    
    /** Check that a string is segmented into a specified list of tokens */
    private void test(String aStr) {
        test(aStr, "/");
    }
    
    private void test(String aStr, String aSep) {
        test(aStr.replace(aSep, ""), aStr, aSep);
    }
    
    /** Check that a string is segmented into a specified list of expected tokens */
    private void test(String aStr, String aExpected, String aSep) {
        final Scanner1 scanner = new Scanner1(aStr);
        System.out.println("Exp tokens:" + Arrays.toString(aExpected.split("\\Q"+aSep+"\\E")));

        for (String str : aExpected.split("\\Q"+aSep+"\\E")) {
            Scanner1.Token token = scanner.next();
            
            String tokenStr = null;
            if (token.text != null) {
                tokenStr = token.text;
            }
            System.out.printf("token: %s '%s'\n", token.type, tokenStr);
            
            if (token.code != null) {
                tokenStr = (tokenStr != null) ? (tokenStr + ":") : "";
                tokenStr += token.code.toString();
            }
            if (token.codes != null) {
                tokenStr = (tokenStr != null) ? (tokenStr + ":") : "";
                tokenStr += Cols.toString(token.codes, "", "", "", "");
            }

            assertEquals(str, tokenStr);
        }
        System.out.println("=============");    
    }

    @Test
    public void testNextI() {
    }

    @Test
    public void testFindWordSplit() {
    }

    @Test
    public void testSpaceSplit() {
    }

    @Test
    public void testPunctSplit() {
    }

    @Test
    public void testParenSplit() {
    }

    @Test
    public void testMatches() {
        assertTrue( new Scanner1("abc{ }<dt>").matches(0, "abc") );
        assertTrue( new Scanner1("abc{ }<dt>").matches(1, "bc") );
        assertTrue( new Scanner1("abc{ }<dt>").matches(1, "b") );
        assertTrue( new Scanner1("abc{ }<dt>").matches(3, "\\{") );
        assertTrue( new Scanner1("abc{ }<dt>").matches(6, "\\<") );
        assertTrue( new Scanner1("abc{ }<dt>").matches(6, "\\<dt") );

        assertFalse( new Scanner1("abc{ }<dt>").matches(0, "bc") );
        assertFalse( new Scanner1("abc{ }<dt>").matches(1, "bcc") );
        assertFalse( new Scanner1("abc{ }<dt>").matches(3, " \\{") );
        assertFalse( new Scanner1("abc{ }<dt>").matches(6, "\\>") );
        assertFalse( new Scanner1("abc{ }<dt>").matches(6, "\\<dt ") );
    }

    
    
}
