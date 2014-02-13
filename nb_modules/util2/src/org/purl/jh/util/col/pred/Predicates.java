package org.purl.jh.util.col.pred;

/**
 *
 */
public class Predicates {
    private Predicates() {}

    /**
     * Keeps all items.
     */
    @SuppressWarnings("unchecked")
    public static final <T> Predicate<T> truePredicate() {
        return (Predicate<T>) cTrue;
    }
    

    /**
     * Keeps no items.
     */
    @SuppressWarnings("unchecked")
    public static final <T> Predicate<T> falsePredicate() {
        return (Predicate<T>) cFalse;
    }

    /**
     * Creates a negation of a predicate
     * @param <T>
     * @param aPred
     * @return
     */
    public static <T> Predicate<T> neg(final Predicate<T> aPred) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return ! aPred.isOk(aElement);}
        };
    }
    
    public static <T> Predicate<T> and(final Predicate<T> aPred1, final Predicate<T> aPred2) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {
                return aPred1.isOk(aElement) && aPred2.isOk(aElement);
            }
        };
    }

    public static <T> Predicate<T> or(final Predicate<T> aPred1, final Predicate<T> aPred2) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {
                return aPred1.isOk(aElement) || aPred2.isOk(aElement);
            }
        };
    }
    
// -----------------------------------------------------------------------------    
// Comparisons
// -----------------------------------------------------------------------------    
    
    /**
     * Keeps all items equal to aObject.
     */
    public static final <T> Predicate<T> eq(final T aObject) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return aElement.equals(aObject);}
        };
    }

    /**
     * Keeps all items smaller than aObject.
     */
    public static final <T extends Comparable<T>> Predicate<T> less(final T aObject) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) < 0;}
        };
    }

    /**
     * Keeps all items smaller or equal to aObject.
     */
    public static final <T extends Comparable<T>> Predicate<T> lessEq(final T aObject) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) <= 0;}
        };
    }
    
    /**
     * Keeps all items greater than aObject.
     */
    public static final <T extends Comparable<T>> Predicate<T> greater(final T aObject) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) > 0;}
        };
    }

    /**
     * Keeps all items greater or equal to aObject.
     */
    public static final <T extends Comparable<T>> Predicate<T> greaterEq(final T aObject) {
        return new AbstractPredicate<T>() {
            public boolean isOk(T aElement) {return aElement.compareTo(aObject) > 0;}
        };
    }
    
// =============================================================================
// Implementation
// =============================================================================
    
//    private static abstract class PredicateNL<E> implements AbstractPredicate<E> {
//        public abstract boolean isOk(E aElement);
//        public void addPredicateListener(PredicateListener l) {}
//        public void removePredicateListener(PredicateListener l) {}
//    }
    
    private final static Predicate<?> cTrue = new AbstractPredicate<Object>() {
        public boolean isOk(Object aElement) {return true;}
    };

    private final static Predicate<?> cFalse = new AbstractPredicate<Object>() {
        public boolean isOk(Object aElement) {return false;}
    };
    
}
