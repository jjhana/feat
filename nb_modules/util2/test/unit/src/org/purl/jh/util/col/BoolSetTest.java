package org.purl.jh.util.col;

import junit.framework.*;
import java.io.*;
import org.purl.jh.util.col.BoolSet;

/**
 *
 * @author Jiri
 */
public class BoolSetTest extends TestCase {
    
    public BoolSetTest(String testName) {
        super(testName);
    }

    protected void setUp() throws java.lang.Exception {
    }

    protected void tearDown() throws java.lang.Exception {
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(BoolSetTest.class);
        
        return suite;
    }

    public void testAllTrue() {
    }

    public void testAllFalse() {
    }

    public void testGet() {
    }

    public void testSet() {
        assertEquals(new BoolSet(4).toString(), "----");
        assertEquals(new BoolSet(4).set(1).toString(), "-+--");
    }

    public void testClear() {
    }

    public void testNot() {
    }

    public void testAnd() {
    }

    public void testNand() {
    }

    public void testOr() {
    }

    public void testXor() {
    }

    public void testSubMap() {
    }

    public void testIsSubsetOf() {
    }

    public void testHasAllSet() {
    }

    public void testHasAllCleared() {
    }

    public void testHasSomeSet() {
    }

    public void testNrOfSet() {
    }

    public void testHashCode() {
    }

    public void testEquals() {
    }

    public void testToString() {
    }
    
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
    
}
