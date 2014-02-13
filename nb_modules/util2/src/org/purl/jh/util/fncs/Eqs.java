package org.purl.jh.util.fncs;

import java.util.Comparator;

/**
 * Predefined and aggregate equalizers.
 * 
 * @author jirka
 * @todo to util
 * @todo tests
 */
public final class Eqs {
    private Eqs() {}

    /**
     * Equalizer based on a comparator.
     * The returned equalizer considers two objects equal iff the comparator does.
     * 
     * @param <T> type of compared objects
     * @param aComparator
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static <T> Eq<T> compEq(final Comparator<T> aComparator) {
        return new Eq<T>() {
            @Override public boolean equals(T a, T b) {
                return aComparator.compare(a, b) == 0;
            }
        };
    }


    private final static Eq<?> cTrue = new Eq<Object>() {
        @Override public boolean equals(Object aElement1, Object aElement2) {return true;}
    };

    /**
     * An equalizer considering everything equal.
     */
    @SuppressWarnings("unchecked")
    public static <T> Eq<T> trueEq() {
        return (Eq<T>) cTrue;
    }
    

    private final static Eq<?> cFalse = new Eq<Object>() {
        @Override public boolean equals(Object aElement1, Object aElement2) {return true;}
    };

    /**
     * An equalizer considering nothing equal.
     */
    @SuppressWarnings("unchecked")
    public static <T> Eq<T> falseEq() {
        return (Eq<T>) cFalse;
    }

    /**
     * Creates a negation of an equalizer
     * @param <T>
     * @param aEq
     * @return
     */
    public static <T> Eq<T> neg(final Eq<T> aEq) {
        return new Eq<T>() {
            public boolean equals(T aElement1, T aElement2) {return !aEq.equals(aElement1,aElement2);}
        };
    }
    
    
    /**
     * Combines two equalizers by and.
     * @param <T>
     * @param aEq1
     * @param aEq2
     * @return
     */
    public static <T> Eq<T> and(final Eq<T> aEq1, final Eq<T> aEq2) {
        return new Eq<T>() {
            @Override public boolean equals(T aElement1, T aElement2) {
                return aEq1.equals(aElement1, aElement2) && aEq2.equals(aElement1, aElement2);
            }
        };
    }

    /**
     * Combines two equalizers by or.
     * @param <T>
     * @param aEq1
     * @param aEq2
     * @return
     */
    public static <T> Eq<T> or(final Eq<T> aEq1, final Eq<T> aEq2) {
        return new Eq<T>() {
            @Override public boolean equals(T aElement1, T aElement2) {
                return aEq1.equals(aElement1, aElement2) || aEq2.equals(aElement1, aElement2);
            }
        };
    }
    

    
}
