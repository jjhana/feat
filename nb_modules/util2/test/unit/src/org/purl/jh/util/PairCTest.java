/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util;

import junit.framework.TestCase;

/**
 *
 * @author Administrator
 */
public class PairCTest extends TestCase {
    
    public PairCTest(String testName) {
        super(testName);
    }            

    private int c(String aStr1, Integer aInt1, String aStr2, Integer aInt2) {
        PairC<String,Integer> p1 = new PairC<String,Integer>(aStr1,aInt1);
        PairC<String,Integer> p2 = new PairC<String,Integer>(aStr2,aInt2);
        return p1.compareTo(p2);
    }
    
    
    /**
     * Test of compareTo method, of class PairCC.
     */
    public void testCompareTo() {
        assertTrue( c("a", 1, "a", 1) == 0);
        
        assertTrue( c("a", 1, "a", 2) < 0);
        assertTrue( c("a", 1, "b", 1) < 0);
        assertTrue( c("a", 1, "b", 0) < 0);

        assertTrue( c("a", 2, "a", 1) > 0);
        assertTrue( c("b", 1, "a", 1) > 0);
        assertTrue( c("b", 0, "a", 1) > 0);

        
    }

}
