
package org.purl.jh.util.str;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author Jirka Hana
 */
public class StrSetTest extends TestCase {
    private Set<Character> set(String aStr) {
        return new StrSet(aStr);
    }

    private Set<Character> hset(Character ... aChars) {
        return new HashSet<Character>(Arrays.asList(aChars));
    }

    public StrSetTest(String testName) {
        super(testName);
    }



    public void testConstructor() {
        assertEquals(new StrSet("").getString(), "" );
        assertEquals(new StrSet("a").getString(), "a" );
        assertEquals(new StrSet("aa").getString(), "a" );
        assertEquals(new StrSet("acbac").getString(), "abc" );

        assertEquals(new StrSet("", false).getString(), "" );
        assertEquals(new StrSet("a", false).getString(), "a" );
        assertEquals(new StrSet("aa", false).getString(), "a" );
        assertEquals(new StrSet("acbac", false).getString(), "abc" );

        assertEquals(new StrSet("", true).getString(), "" );
        assertEquals(new StrSet("a", true).getString(), "a" );
        assertEquals(new StrSet("aa", true).getString(), "aa" );
        assertEquals(new StrSet("acbac", true).getString(), "acbac" );

        assertEquals(new StrSet(Arrays.<Character>asList()).getString(), "" );
        assertEquals(new StrSet(Arrays.asList('a')).getString(), "a" );
        assertEquals(new StrSet(Arrays.asList('a', 'a')).getString(), "a" );
        assertEquals(new StrSet(Arrays.asList('a', 'c', 'b',  'a', 'c')).getString(), "abc" );
    }



    /**
     * Test of size method, of class StrSet.
     */
    public void testSize() {
        assertEquals(set("").size(),     0 );
        assertEquals(set("a").size(),    1 );
        assertEquals(set("abc").size(),  3 );
        assertEquals(set("abac").size(), 3 );
    }

    /**
     * Test of isEmpty method, of class StrSet.
     */
    public void testIsEmpty() {
        assertTrue( set("").isEmpty() );
        assertFalse(set("aa").isEmpty() );
    }

    /**
     * Test of contains method, of class StrSet.
     */
    public void testContains() {
        assertFalse( set("").contains('a') );
        assertFalse( set("bcd").contains('a') );
        assertTrue( set("a").contains('a') );
        assertTrue( set("bcda").contains('a') );
    }


    /**
     * Test of iterator method, of class StrSet.
     */
    public void testIterator() {
        List<Character> origList = Arrays.asList('a', 'b', 'c', 'd', 'e');
        List<Character> list = new ArrayList<Character>();
        for (Character c : new StrSet(origList)) {
            list.add(c);
        }
        Collections.sort(list);

        assertEquals(list, origList);
    }

    public void testEquals() {
        assertTrue(new StrSet("").equals(new StrSet("")) );
        assertTrue(new StrSet("abcac").equals(new StrSet("abc")) );
        assertTrue(new StrSet("abcac").equals(new StrSet("abcac")) );

        assertTrue(new StrSet("").equals(hset()) );
        assertTrue(new StrSet("a").equals(hset('a')) );
        assertTrue(new StrSet("aa").equals(hset('a')) );
        assertTrue(new StrSet("abcac").equals(hset('a','b','c')) );
    }

    public void testHashCode() {
        assertEquals(new StrSet("").hashCode(), new StrSet("").hashCode() );
        assertEquals(new StrSet("abcac").hashCode(), new StrSet("abc").hashCode() );
        assertEquals(new StrSet("abcac").hashCode(), new StrSet("abcac").hashCode() );
    }

    public void testInters() {
        intersTest("", "", "");
        intersTest("ab", "ab", "ab");
        intersTest("abc", "abd", "ab");
        intersTest("ab", "cd", "");
        intersTest("abcdef", "bdegh", "bde");
    }

    private void intersTest(String aStr1, String aStr2, String aResult) {
        assertEquals( new StrSet(aStr1).inters(new StrSet(aStr2)), new StrSet(aResult) );
    }

    /**
     * Test of add method, of class StrSet.
     */
    public void testAdd() {
        try {
            set("a").add('a');
            fail("RO");
        }
        catch (UnsupportedOperationException e) {
            // fine
        }
        catch (Throwable e) {
            fail("RO 2");
        }
    }


}
