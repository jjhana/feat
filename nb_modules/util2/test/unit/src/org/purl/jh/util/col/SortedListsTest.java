/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util.col;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jhana
 */
public class SortedListsTest {

    public SortedListsTest() {
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

    private List<Integer> list(Integer ... aList) {
        return Arrays.asList(aList);
    }

    private static class NatComparator<T extends Comparable<T>> implements Comparator<T> {
        public int compare(T o1, T o2) {
            return o1.compareTo(o2);
        }
    }

    private final NatComparator<Integer> c = new NatComparator<Integer>();

    /**
     * Test of isGrowing method, of class SortedLists.
     */
    @Test
    public void testIsGrowing() {
        assertTrue(SortedLists.isGrowing(list(1,2,3,4,5), c));
        assertTrue(SortedLists.isGrowing(list(1), c));
        assertTrue(SortedLists.isGrowing(list(), c));

        assertTrue(SortedLists.isGrowing(list(1,1,3,4,4), c));
        assertTrue(SortedLists.isGrowing(list(1,1,1,1,1), c));

        assertTrue(SortedLists.isGrowing(list(-10,-5,0), c));

        assertFalse(SortedLists.isGrowing(list(1,2,3,2,3), c));
        assertFalse(SortedLists.isGrowing(list(10,1), c));          // only two elements
        assertFalse(SortedLists.isGrowing(list(10,10,10,1), c));
        assertFalse(SortedLists.isGrowing(list(1,2,3,4,3), c));     // last wrong
        assertFalse(SortedLists.isGrowing(list(2,1,2,3,4), c));     // first wrong
    }

    /**
     * Test of delete method, of class SortedLists.
     */
    @Test
    public void testDelete() {
        testDelete(list(1,2,3,4,5,6,7), list(2,3,4,5), list(1,6,7));
        testDelete(list(1,2,3,4,5,6,7), list(2,5,7), list(1,3,4,6));
        testDelete(list(1,2,3,4,5,6,7), list(2,3,4,5,9), list(1,6,7));
        testDelete(list(1,2,3,4,5,6,7), list(0,3,4,5,9), list(1,2,6,7));
        testDelete(list(1,2,3,5,7), list(0,3,4,5,9), list(1,2,7));

        testDelete(list(1,2,3), list(1,2,3), list());

        testDelete(list(1,2,3,4,5,6,7), list(1,2,3,4,5,6,7), list());
        testDelete(list(1,2,3,4,5,6,7), list(0,1,2,3,4,5,6,7,9), list());

        testDelete(list(1,2,3,4,5,6,7), list(0,9,10), list(1,2,3,4,5,6,7));

        testDelete(list(1,1,2,2,3,4,4,6,7), list(2,3,4,5), list(1,1,6,7));
    }


    private void testDelete(List<Integer> aOrig, List<Integer> aVals, List<Integer> aResult) {

        List<Integer> list = new ArrayList<Integer>(aOrig);

        try {
            SortedLists.delete(list, aVals, c);
        }
        catch(Exception e) {
            System.out.println("list " + list);
        }

        assertEquals(aResult, list);
    }

}