package org.purl.jh.util.col;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static java.util.Arrays.asList;

/**
 *
 * @author jirka
 */
public class ListsTest {
    @Test
    public void testList() {
        assertEquals(asList(1,2,3), Lists.list(asList(1,2,3)));

        // todo test on sets, etc
    }

    @Test
    public void testOf() {
        assertEquals(asList(1), Lists.of(1));
        assertEquals(asList(1,2), Lists.of(1,2));
        assertEquals(asList(1,1), Lists.of(1,1));
    }

    @Test
    public void testAddAfter() {
        testAddAfter(asList(1,99,2,3,4,4,4,5,6,6), 1);
        testAddAfter(asList(1,2,99,3,4,4,4,5,6,6), 2);
        testAddAfter(asList(1,2,3,4,99,4,4,5,6,6), 4);
        testAddAfter(asList(1,2,3,4,4,4,5,6,99,6), 6);
        testAddAfter(asList(99,1,2,3,4,4,4,5,6,6), null);

        try {
           testAddAfter(asList(99,1,2,3,4,4,4,5,6,6), 9);
           fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            
        }
        catch (Throwable e) {
            fail("Should throw IllegalArgumentException");
        }
        
    }

    public void testAddAfter(List<Integer> aExpected, Integer aAnchor) {
        testAddAfter(aExpected, list(), aAnchor);
    }

    public void testAddAfter(List<Integer> aExpected, List<Integer> aIn, Integer aAnchor) {
        int x = 99;
        List<Integer> list = new ArrayList<Integer>(aIn);
        Lists.addAfter(list, aAnchor, x);
        assertEquals(aExpected, list);
    }
    
    private List<Integer> list() {
        return asList(1,2,3,4,4,4,5,6,6);
    }
    
    
}
