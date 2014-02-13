
package org.purl.jh.util.str;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class SearchTest {

    public SearchTest() {
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
    public void testFindMatchingOpeningBrace() {
        testO(4,33);

        testO(7,9);
        testO(7,8);

        testO(4,10);
        testO(4,33);

        testO(-1,34);
    }

    @Test
    public void testFindMatchingClosingBrace() {
        testC(33,4);
        testC(33,5);

        testC(9,7);
        testC(9,8);

        testC(13,12);

        testC(17,14);

        testC(-1,1);
    }


    String text = "0123{56{8}01{}{{}}89{1{3{{67}9}}}}4";

    private void testO(int aExp, int aStart) {
        assertEquals(aExp, Search.findMatchingOpeningBrace(text, aStart));
    }

    private void testC(int aExp, int aStart) {
        assertEquals(aExp, Search.findMatchingClosingBrace(text, aStart));
    }

    @Test
    public void testFindMatchingOpening() {
    }


    @Test
    public void testFindMatchingClosing() {
    }

}