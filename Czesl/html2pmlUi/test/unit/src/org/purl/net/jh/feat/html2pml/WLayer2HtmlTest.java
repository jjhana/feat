/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.net.jh.feat.html2pml;

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
public class WLayer2HtmlTest {
    
    public WLayer2HtmlTest() {
    }
    

    @Test
    public void testGo() {
    }

    @Test
    public void testClean() {
        checkClean("abc", "abc");
        checkClean("abc|def", "abc");
        checkClean("a{bc}d", "abcd");
        checkClean("{bc}", "bc");
        checkClean("a{bc}d{ef}g", "abcdefg");
        checkClean("a{bc|de}f", "abcf");
        checkClean("ab{ }cd", "abcd");
        checkClean("{bc", "bc");
        checkClean("abc<.>", "abc");
        checkClean("abc<°>", "abc");
        checkClean("{abc}<pr>", "abc");
        checkClean("abc}<pr>", "abc");
        checkClean("abc<img>", "abc");
        checkClean("abc&lt;img&gt;", "abc");
    
    }
    
    private void checkClean(String aIn, String aExpected) {
        assertEquals(aExpected, WLayer2Html.clean(aIn));
    }
    
}
