package org.purl.jh.util.col;

import java.util.Arrays;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sevans
 */
public class FixedArrayListTest {

    private final FixedArrayList emptyList = new FixedArrayList(0);
    private final FixedArrayList<String> baseList = new FixedArrayList<String>( 10 );

    public FixedArrayListTest() {
        baseList.set( 1, "String" );
        baseList.set( 4, "Other" );
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of size method, of class FixedArrayList.
     */
    @Test
    public void testSize() {
        assertEquals( baseList.size(), 10 );
    }

    /**
     * Test of isEmpty method, of class FixedArrayList.
     */
    @Test
    public void testIsEmpty() {
        assertFalse( baseList.isEmpty() );
        assertTrue( emptyList.isEmpty() );
    }

    /**
     * Test of contains method, of class FixedArrayList.
     */
    @Test
    public void testContains() {
        assertTrue( baseList.contains( "String" ) );
        assertFalse( baseList.contains( "string" ) );
        
        assertFalse( emptyList.contains( "some value" ) );
        
        assertTrue( baseList.contains( null ) );
        assertFalse( emptyList.contains( null ) );
    }

    /**
     * Test of indexOf method, of class FixedArrayList.
     */
    @Test
    public void testIndexOf() {
        assertTrue( baseList.indexOf( "String" ) == 1 );
        assertTrue( baseList.indexOf( "Other" ) == 4 );
        assertTrue( baseList.indexOf( "non-value" ) == -1 );

        assertTrue( baseList.indexOf( null ) > -1 );
        assertFalse( emptyList.indexOf( null ) > -1 );
    }

    /**
     * Test of lastIndexOf method, of class FixedArrayList.
     */
    @Test
    public void testLastIndexOf() {
        assertTrue( baseList.lastIndexOf( null ) == 9 );
        assertTrue( emptyList.lastIndexOf( null ) == -1 );
    }

    /**
     * Test of clone method, of class FixedArrayList.
     */
    @Test
    public void testClone() {
        assertEquals( baseList, baseList.clone() );
    }

    /**
     * Test of toArray method, of class FixedArrayList.
     */
    @Test
    public void testToArray_0args() {
        Object[] objects = baseList.toArray();
        assertEquals( baseList, new FixedArrayList<Object>( Arrays.asList( objects ) ) );
    }

    /**
     * Test of toArray method, of class FixedArrayList.
     */
    @Test
    public void testToArray_GenericType() {
        String[] strings = baseList.toArray( new String[ baseList.size()] );
        assertEquals( baseList, new FixedArrayList<String>( Arrays.asList( strings ) ) );
    }

    /**
     * Test of get method, of class FixedArrayList.
     */
    @Test
    public void testGet() {
        assertEquals( baseList.get( 1 ), "String" );
    }

    /**
     * Test of set method, of class FixedArrayList.
     */
    @Test
    public void testSet() {
        FixedArrayList<Integer> list = new FixedArrayList<Integer>(3);
        list.set( 0, 1 );
        list.set( 1, 2 );
        list.set( 2, 3 );
        assertEquals( list, new FixedArrayList<Integer>( Arrays.<Integer>asList( 1, 2, 3 ) ) );
    }

    /**
     * Test of add method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testAdd_GenericType() {
        baseList.add( "some value" );
    }

    /**
     * Test of add method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testAdd_int_GenericType() {
        baseList.add( 1, "some value" );
    }

    /**
     * Test of remove method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testRemove_int() {
        Object result = baseList.remove( 1 );
    }

    /**
     * Test of remove method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testRemove_Object() {
        boolean result = baseList.remove( "String" );
    }

    /**
     * Test of clear method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testClear() {
        baseList.clear();
    }

    /**
     * Test of addAll method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testAddAll_Collection() {
        boolean result = baseList.addAll( null );
    }

    /**
     * Test of addAll method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testAddAll_int_Collection() {
        boolean result = baseList.addAll( 0, null );
    }

    /**
     * Test of removeAll method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testRemoveAll() {
        boolean result = baseList.removeAll( null );
    }

    /**
     * Test of retainAll method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testRetainAll() {
        boolean result = baseList.retainAll( null );
    }

    /**
     * Test of removeRange method, of class FixedArrayList.
     */
    @Test ( expected = UnsupportedOperationException.class )
    public void testRemoveRange() {
        baseList.removeRange( 0, 10 );
    }

}