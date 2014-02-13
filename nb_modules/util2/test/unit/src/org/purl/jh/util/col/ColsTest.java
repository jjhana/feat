/*
 * ColsTest.java
 * JUnit based test
 *
 * Created on September 22, 2004, 9:12 PM
 */

package org.purl.jh.util.col;

import junit.framework.*;
import java.util.*;
import org.purl.jh.util.col.Cols;

/**
 *
 * @author Jiri
 */
public class ColsTest extends TestCase {
    
    public ColsTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {

        junit.framework.TestSuite suite = new junit.framework.TestSuite(ColsTest.class);
        
        return suite;
    }

    protected static <T> List<T> list(T ... aItems) {
        return new ArrayList<T>(Arrays.asList(aItems));
    }

    protected static <T> List<T> nlist() {
        List<T> l = new ArrayList<T>();
        l.add(null);
        return l;
    }

    protected static <T> List<T> elist() {
        return new ArrayList<T>(Arrays.<T>asList());
    }
    
    public void testShiftUp() {
        assertEquals(Cols.shiftUp(list("a", "b", "c")), list(null, "a", "b" )  );
        assertEquals(Cols.shiftUp(list("a")), nlist()  );
        assertEquals(Cols.shiftUp(elist()), elist()  );
    }

    public void testShiftDown() {
        assertEquals(Cols.shiftDown(list("a","b","c")), list("b","c",null)  );
        assertEquals(Cols.shiftDown(list("a")), nlist()  );
        assertEquals(Cols.shiftDown(elist()), elist()  );
    }

    /**
     * Test of notDisjunctive method, of class morph.util.Cols.
     */
    public void testNotDisjunctive() {
        Set<String> abc = new HashSet<String>(Arrays.asList("a", "b", "c"));
        Set<String> bcd = new HashSet<String>(Arrays.asList("b", "c", "d"));
        Set<String> def = new HashSet<String>(Arrays.asList("d", "e", "f"));
        
        assertTrue(Cols.notDisjunctive(abc,bcd));
        assertTrue(Cols.notDisjunctive(bcd,def));
        assertFalse(Cols.notDisjunctive(abc,def));
        
        // TODO add your test code below by replacing the default call to fail.
    }

    public void testListAdd() {
        List<String> list = new ArrayList(Arrays.asList("0", "1", "2"));
        Cols.listAdd(list, 4, "5");
        assertEquals(list.toString(), "[0, 1, 2, null, 5]");

        list = new ArrayList(Arrays.asList("0", "1", "2"));
        Cols.listAdd(list, 6, "5");
        assertEquals(list.toString(), "[0, 1, 2, null, null, null, 5]");
        
        
        list = new ArrayList(Arrays.asList("0", "1", "2"));
        Cols.listAdd(list, 3, "5");
        assertEquals(list.toString(), "[0, 1, 2, 5]");

        list = new ArrayList(Arrays.asList("0", "1", "2"));
        Cols.listAdd(list, 1, "5");
        assertEquals(list.toString(), "[0, 5, 1, 2]");
    }    
    
    /**
     * Test of toString method, of class morph.util.Cols.
     */
    public void testToString() {

        
        // TODO add your test code below by replacing the default call to fail.
    }

    /**
     * Test of toStringNl method, of class morph.util.Cols.
     */
    public void testToStringNl() {

        
        // TODO add your test code below by replacing the default call to fail.
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
