package org.purl.jh.util.str.transl;

import junit.framework.TestCase;

/**
 *
 * @author Administrator
 */
public class TranslatorsTest extends TestCase {
    
    /**
     * Test of translate method, of class Translators.
     */
    public void testTranslate() {
        Translators t = new Translators();
        t.add(new ReplaceTranslator("ch", "C"));
        t.add(new ReplaceTranslator("sh", "S"));
        t.add(new ReplaceTranslator("h", "g"));

        assertEquals(t.translate("cde"), "cde");
        assertEquals(t.translate(""), "");

        assertEquals(t.translate("chcha"), "CCa");
        assertEquals(t.translate("chsha"), "CSa");

        // order is important ch is gone when replacing h->g
        assertEquals(t.translate("hchha"), "gCga");
    }

}
