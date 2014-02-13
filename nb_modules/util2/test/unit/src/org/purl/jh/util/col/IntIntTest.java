/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util.col;

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
public class IntIntTest {

    public IntIntTest() {
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
    public void testDistance() {
        assertEquals(5, new IntInt(0,5).distance());
        assertEquals(5, new IntInt(5,0).distance());
        assertEquals(5, new IntInt(0,-5).distance());
        assertEquals(5, new IntInt(-5,0).distance());

        assertEquals(0, new IntInt(5,5).distance());

        assertEquals(10, new IntInt(-5,5).distance());
        assertEquals(10, new IntInt(5,-5).distance());
    }

}