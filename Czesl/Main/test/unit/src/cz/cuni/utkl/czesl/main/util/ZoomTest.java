/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.utkl.czesl.main.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jirka
 */
public class ZoomTest {

    public ZoomTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testZoomFactor() {
    }

    @Test
    public void testFactor2Slider() {
        checkReversible(100, 100);
        checkReversible( 50, 100);
        checkReversible(  5, 100);
        checkReversible(  0, 100);

        checkReversible( 10, 10);
        checkReversible(  5, 10);
        checkReversible(  1, 10);
        checkReversible(  0, 10);

        checkReversible(1000, 1000);
        checkReversible( 500, 1000);
        checkReversible( 100, 1000);
        checkReversible(  50, 1000);
        checkReversible(   5, 1000);
        checkReversible(   1, 1000);
        checkReversible(   0, 1000);
    }

    private void checkReversible(int aVal, int aMax) {
// todo        assertEquals(aVal, Zoom.factor2Slider(Zoom.zoomFactor(aVal, aMax), aMax) );
    }


    @Test
    public void testScale() {
    }

    @Test
    public void testZoom() {
    }

    @Test
    public void testPagefit() {
    }

    @Test
    public void testWidthfit() {
    }

}