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
public class PairTest extends TestCase {
    
    public PairTest(String testName) {
        super(testName);
    }            

    /**
     * Test of equals method, of class Pair.
     */
    public void testEquals() {
        assertTrue( new Pair<String,Integer>("a", 1)    .equals(new Pair<String,Integer>("a", 1)) );
        assertTrue( new Pair<String,Integer>(null, 2)   .equals(new Pair<String,Integer>(null, 2)) ) ;
        assertTrue( new Pair<String,Integer>(null, null).equals(new Pair<String,Integer>(null, null)) );
        assertTrue( new Pair<String,Integer>("a", null) .equals(new Pair<String,Integer>("a", null)) );

        assertFalse( new Pair<String,Integer>("a", 1)   .equals(new Pair<String,Integer>("b", 1)) );
        assertFalse( new Pair<String,Integer>("a", 1)   .equals(new Pair<String,Integer>("a", 2)) );
        assertFalse( new Pair<String,Integer>("a", 1)   .equals(new Pair<String,Integer>("b", 2)) );
        assertFalse( new Pair<String,Integer>(null, 2)  .equals(new Pair<String,Integer>(null, 3)) ) ;
        assertFalse( new Pair<String,Integer>(null, 2)  .equals(new Pair<String,Integer>("a", 2)) ) ;
        assertFalse( new Pair<String,Integer>("a", null).equals(new Pair<String,Integer>("b", null)) );
        assertFalse( new Pair<String,Integer>("a", null).equals(new Pair<String,Integer>("b", 1)) );
    }

    /**
     * Test of hashCode method, of class Pair.
     */
    public void testHashCode() {
        assertEquals(new Pair<String,Integer>("abc", 1).hashCode(), new Pair<String,Integer>("abc", 1).hashCode());
        assertEquals(new Pair<String,Integer>("abc", null).hashCode(), new Pair<String,Integer>("abc", null).hashCode());
        assertEquals(new Pair<String,Integer>(null,null).hashCode(),new Pair<String,Integer>(null,null).hashCode());
    }

    /**
     * Test of toString method, of class Pair.
     */
    public void testToString() {
        assertEquals(new Pair<String,Integer>("abc", 1).toString(), "abc:1");
        assertEquals(new Pair<String,Integer>("abc", null).toString(), "abc:null");
        assertEquals(new Pair<String,String>("", "").toString(), ":");
    }

}
