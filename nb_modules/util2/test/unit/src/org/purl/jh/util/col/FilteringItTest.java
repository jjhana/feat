/*
 * FilteringItTest.java
 * JUnit based test
 *
 * Created on February 21, 2006, 5:20 PM
 */

package org.purl.jh.util.col;

import org.purl.jh.util.col.pred.FilteringIt;
import org.purl.jh.util.col.pred.Predicate;
import org.purl.jh.util.col.pred.AbstractPredicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.*;
import java.util.List;

/**
 *
 * @author Jirka
 */
public class FilteringItTest extends TestCase {
    List<String> mStrCol, mStrColA, mStrColB;
    List<Integer> mIntCol, mIntColA, mIntColB;
    Predicate<String> cStrFilterA, cStrFilterB;
    Predicate<Integer> cIntFilterA, cIntFilterB;
    
    
    public FilteringItTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        mStrCol = Arrays.asList("a1", "b1", "a2", "b2", "b3", "a3");
        mIntCol = Arrays.asList(11,-11,12,-12,-13,13);
        mStrColA = Arrays.asList("a1", "a2", "a3");
        mStrColB = Arrays.asList("b1", "b2", "b3");
        mIntColA = Arrays.asList(11,12,13);
        mIntColB = Arrays.asList(-11,-12,-13);
        
        cStrFilterA = new AbstractPredicate<String>() {public boolean isOk(String a) {return a.startsWith("a");}};
        cStrFilterB = new AbstractPredicate<String>() {public boolean isOk(String a) {return a.startsWith("b");}};
        cIntFilterA = new AbstractPredicate<Integer>() {public boolean isOk(Integer a) {return a > 0;}};
        cIntFilterB = new AbstractPredicate<Integer>() {public boolean isOk(Integer a) {return a < 0;}};
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FilteringItTest.class);
        
        return suite;
    }

    public void testCol() {
        System.out.println("col");

        testFiltering(mStrCol,  cStrFilterA, mStrColA);
        testFiltering(mStrCol,  cStrFilterB, mStrColB);
        testFiltering(mStrColA, cStrFilterA, mStrColA);
        testFiltering(mIntCol,  cIntFilterA, mIntColA);
        testFiltering(mIntCol,  cIntFilterB, mIntColB);
        testFiltering(mIntColA,  cIntFilterA, mIntColA);

        testFiltering(Collections.<String>emptyList(), cStrFilterA, Collections.<String>emptyList());
    
    }

    private <T> void testFiltering(List<T> aOrigList, Predicate<T> aPredicate, List<T> aResult) {
        List<T> result = new ArrayList<T>();
        for (T o : FilteringIt.col(aOrigList, aPredicate) ) {
            result.add(o);
        }
        assertTrue(result.equals(aResult));
    }
    
    public void testHasNext() {
    }

    public void testNext() {
    }

}
