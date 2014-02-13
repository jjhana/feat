/*
 * Filters.java
 *
 * Created on January 2, 2006, 7:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.purl.jh.util.col.pred;

import java.util.Collections;

/**
 * A collection of usual filters.
 *
 * @author Jirka
 */
public class Filters {
        
    private Filters() {}

    /**
     * Keeps all items.
     */
    @SuppressWarnings("unchecked")
    public static final <T> Filter<T> trueFilter() {
        return (Filter<T>) cTrue;
    }
    

    /**
     * Keeps no items.
     */
    @SuppressWarnings("unchecked")
    public static final <T> Filter<T> falseFilter() {
        return (Filter<T>) cFalse;
    }

    /**
     * Creates a negation of a predicate
     * @param <T>
     * @param aPred
     * @return
     * TODO remove (IT is in Predicates) ? Redo to work on Filters?
     */
    public static <T> Predicate<T> neg(final Predicate<T> aPred) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return ! aPred.isOk(aElement);}
        };
    }
    
    
    /**
     * Keeps all items equal to aObject.
     */
    public static final <T> Filter<T> eq(final T aObject) {
        return new AbstractFilter<T>() {
            public boolean isOk(T aElement) {return aElement.equals(aObject);}
        };
    }

    /**
     * Keeps all items smaller than aObject.
     */
    public static final <T extends Comparable<T>> Filter<T> less(final T aObject) {
        return new AbstractFilter<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) < 0;}
        };
    }

    /**
     * Keeps all items smaller or equal to aObject.
     */
    public static final <T extends Comparable<T>> Filter<T> lessEq(final T aObject) {
        return new AbstractFilter<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) <= 0;}
        };
    }
    
    /**
     * Keeps all items greater than aObject.
     */
    public static final <T extends Comparable<T>> Filter<T> greater(final T aObject) {
        return new AbstractFilter<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) > 0;}
        };
    }

    /**
     * Keeps all items greater or equal to aObject.
     */
    public static final <T extends Comparable<T>> Filter<T> greaterEq(final T aObject) {
        return new AbstractFilter<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) > 0;}
        };
    }
    
// =============================================================================
// Implementation
// =============================================================================
    
//    private static abstract class FilterNL<E> implements AbstractFilter<E> {
//        public abstract boolean isOk(E aElement);
//        public void addFilterListener(FilterListener l) {}
//        public void removeFilterListener(FilterListener l) {}
//    }
    
    private final static Filter<?> cTrue = new AbstractFilter<Object>() {
        public boolean isOk(Object aElement) {return true;}
    };

    private final static Filter<?> cFalse = new AbstractFilter<Object>() {
        public boolean isOk(Object aElement) {return false;}
    };
    
}
