/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util.col;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jirka
 */
public class NullSkippingIteratorTest {

    public NullSkippingIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private static <T> NullSkippingIterator<T> it(T ... aObjs) {
        return new NullSkippingIterator<T>( Arrays.asList(aObjs) );
    }

    private static void abc(String ... aABCs) {
        abc0(aABCs);
        abc1(aABCs);
        abc2(aABCs);
    }
    
    private static void abc0(String ... aABCs) {
        Iterator<String> it = it(aABCs);
        assertEquals("A", it.next());
        assertEquals("B", it.next());
        assertEquals("C", it.next());
        assertFalse( it.hasNext() );
    }

    private static void abc1(String ... aABCs) {
        Iterator<String> it = it(aABCs);
        assertTrue( it.hasNext() );
        assertEquals("A", it.next());
        assertTrue( it.hasNext() );
        assertEquals("B", it.next());
        assertTrue( it.hasNext() );
        assertEquals("C", it.next());
        assertFalse( it.hasNext() );
    }

    private static void abc2(String ... aABCs) {
        Iterator<String> it = it(aABCs);
        assertTrue( it.hasNext() );
        assertTrue( it.hasNext() );
        assertEquals("A", it.next());
        assertTrue( it.hasNext() );
        assertTrue( it.hasNext() );
        assertEquals("B", it.next());
        assertTrue( it.hasNext() );
        assertTrue( it.hasNext() );
        assertEquals("C", it.next());
        assertFalse( it.hasNext() );
        assertFalse( it.hasNext() );
    }

    @Test
    public void testX() {
        assertFalse( it(null, null, null).hasNext() );

        Iterator it = it(null, null, "A");
        assertTrue( it.hasNext() );
        assertEquals("A", it.next());
        assertFalse( it.hasNext() );

        abc("A", "B", "C");
        abc("A", "B", "C", null);
        abc(null, null, null, "A", "B", "C");

        abc(null, "A", null, "B", null, "C");
        abc(null, "A", null, "B", null, "C", null);
        abc(null, "A", null, "B", null, "C", null, null);

        abc("A", null, "B", null, "C");
        abc("A", null, "B", null, "C", null);
        abc("A", null, "B", null, "C", null, null);

        abc(null, null, "A", null, null, "B", null, null, "C");
        abc(null, null, "A", null, null, "B", null, null, "C", null);
        abc(null, null, "A", null, null, "B", null, null, "C", null, null);

        abc("A", null, null, "B", null, null, "C");
        abc("A", null, null, "B", null, null, "C", null);
        abc("A", null, null, "B", null, null, "C", null, null);
    }



}