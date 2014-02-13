package org.purl.jh.feat.util0;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 *
 * @author j
 */
public class ByListSortTest {
    
    public ByListSortTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @org.junit.Test
    public void testSomeMethod() {
        ByListSort a;
        ByListSort<Integer> sort = new ByListSort<Integer>(asList(9,8,7,6,5,4,3,2,1));

        testSort(sort, asList(1,2,3,4,5), asList(5,4,3,2,1));
        testSort(sort, asList(1,3,5), asList(5,3,1));
        testSort(sort, asList(3), asList(3));


    }

    private <T> void testSort(ByListSort<T> sort, List<T> aToSort, List<T> aSorted) {
        List<T> mutable = new ArrayList<T>(aToSort);
        sort.sort(mutable);
        assertEquals(aSorted, mutable);
    }
    
    /**
     * Test of getComparator method, of class ByListSort.
     */
    @Test
    public void testGetComparator() {
        System.out.println("getComparator");
        ByListSort instance = null;
        Comparator expResult = null;
        Comparator result = instance.getComparator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of min method, of class ByListSort.
     */
    @Test
    public void testMin_GenericType_GenericType() {
        System.out.println("min");
        Object a = null;
        Object b = null;
        ByListSort instance = null;
        Object expResult = null;
        Object result = instance.min(a, b);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of min method, of class ByListSort.
     */
    @Test
    public void testMin_Iterable() {
        System.out.println("min");
        ByListSort instance = null;
        Object expResult = null;
        Object result = instance.min(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sort method, of class ByListSort.
     */
    @Test
    public void testSort() {
        System.out.println("sort");
        ByListSort instance = null;
        List expResult = null;
        List result = instance.sort(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
