package org.purl.jh.util;

import java.util.Collection;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author Administrator
 */
public class PairsTest  {
    
    public PairsTest(String testName) {
        super(testName);
    }            


    /**
     * Test of firsts method, of class Pairs.
     */
    public <X,Y> void testFirsts() {
        System.out.println("firsts");
        Collection<? extends Pair<X, Y>> aPairs = null;
        Collection<X> expResult = null;
        Collection<X> result = Pairs.firsts(aPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of seconds method, of class Pairs.
     */
    public <X,Y> void testSeconds() {
        System.out.println("seconds");
        Collection<? extends Pair<X, Y>> aPairs = null;
        Collection<Y> expResult = null;
        Collection<Y> result = Pairs.seconds(aPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of firstSet method, of class Pairs.
     */
    public <X,Y> void testFirstSet() {
        System.out.println("firstSet");
        Set<? extends Pair<X, Y>> aPairs = null;
        Set<X> expResult = null;
        Set<X> result = Pairs.firstSet(aPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of secondSet method, of class Pairs.
     */
    public <X,Y> void testSecondSet() {
        System.out.println("secondSet");
        Collection<Pair<X, Y>> aPairs = null;
        Set<Y> expResult = null;
        Set<Y> result = Pairs.secondSet(aPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of firstIt method, of class Pairs.
     */
    public <X,Y> void testFirstIt() {
        System.out.println("firstIt");
        Iterable<Pair<X, Y>> aColOfPairs = null;
        Iterable<X> expResult = null;
        Iterable<X> result = Pairs.firstIt(aColOfPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of secondIt method, of class Pairs.
     */
    public <X,Y> void testSecondIt() {
        System.out.println("secondIt");
        Iterable<Pair<X, Y>> aColOfPairs = null;
        Iterable<Y> expResult = null;
        Iterable<Y> result = Pairs.secondIt(aColOfPairs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
