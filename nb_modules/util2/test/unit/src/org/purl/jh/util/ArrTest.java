/*
 * ArrTest.java
 * JUnit based test
 *
 * Created on July 29, 2005, 4:27 PM
 */

package org.purl.jh.util;

import java.util.Arrays;
import junit.framework.*;

/**
 *
 * @author Jiri
 */
public class ArrTest extends TestCase {
    
    public ArrTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ArrTest.class);
        
        return suite;
    }

    public void testAsArray() {
        Arrays.equals( Arr.asArray(1,2,3,4), new Integer[] {1,2,3,4} );
        Arrays.equals( Arr.asArray(), new Integer[] {} );
        Arrays.equals( Arr.asArray("ABC", 1, null), new Object[] {"ABC", 1, null} );
    }
    
    public void testAddItem() {
        Arrays.equals( Arr.addItem(Arr.asArray(1,2,3,4), 5), Arr.asArray(1,2,3,4,5) );
        Arrays.equals( Arr.addItem(Arr.asArray(), 5), Arr.asArray(5) );
    }

    public void testAddItems() {
        Arrays.equals( Arr.addItems(Arr.asArray(1,2,3,4), 5, 6), Arr.asArray(1,2,3,4,5,6) );
        Arrays.equals( Arr.addItems(Arr.asArray(), 5,6), Arr.asArray(5,6) );
    }

    public void testGrow() {
    }
    
}
