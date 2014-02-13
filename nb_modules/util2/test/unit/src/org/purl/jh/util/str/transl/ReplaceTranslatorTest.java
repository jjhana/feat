package org.purl.jh.util.str.transl;

import junit.framework.TestCase;

/**
 *
 * @author Administrator
 */
public class ReplaceTranslatorTest extends TestCase {
    
    public void testCharForChar() {
        Translator t = new ReplaceTranslator("a", "b");
        
        assertEquals(t.translate("cde"), "cde");
        assertEquals(t.translate(""), "");
        
        assertEquals(t.translate("aaaa"), "bbbb");
        assertEquals(t.translate("abca"), "bbcb");

        assertEquals(t.translate("AaAa"), "AbAb");

    }

    public void testStrForStr() {
        Translator t = new ReplaceTranslator("ch", "x");

        assertEquals(t.translate("cde"), "cde");
        assertEquals(t.translate(""), "");

        assertEquals(t.translate("chcha"), "xxa");
        assertEquals(t.translate("ch"), "x");
    }

    public void testStrForStr2() {
        Translator t = new ReplaceTranslator("hh", "x");

        assertEquals(t.translate("cde"), "cde");
        assertEquals(t.translate(""), "");

        assertEquals(t.translate("hha"), "xa");
        assertEquals(t.translate("hhh"), "xh");
    }

}
