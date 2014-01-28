package org.purl.jh.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * utility class with static methods for mapping and filtering collections of pairs
 *   (all firsts, all seconds, all seconds with a particular first)
 *
 *
 * @author Jiri
 */
public class Pairs {
    
    /** Creates a new instance of Pairs */
    private Pairs() {}
    /**
     * All first items.
     */
    public  static <X,Y> Collection<X> firsts(final Collection<? extends Pair<X,Y>> aPairs) {
        return new AbstractCollection<X>() {
            public int size() {return aPairs.size();}
            public Iterator<X> iterator() {return new FirstIt<X,Y>(aPairs);}
        };
    }

    public  static <X,Y> List<X> firsts(final List<? extends Pair<X,Y>> aPairs) {
        return new AbstractList<X>() {
            public X get(int aIdx) {return aPairs.get(aIdx).mFirst;}
            public int size() {return aPairs.size();}
            public Iterator<X> iterator() {return new FirstIt<X,Y>(aPairs);}
        };
    }
    
    /**
     * All seconds items.
     */
    public  static <X,Y> Collection<Y> seconds(final Collection<? extends Pair<X,Y>> aPairs) {
        return new AbstractCollection<Y>() {
            public int size() {return aPairs.size();}
            public Iterator<Y> iterator() {return new SecondIt<X,Y>(aPairs);}
        };
    }

    public  static <X,Y> List<Y> seconds(final List<? extends Pair<X,Y>> aPairs) {
        return new AbstractList<Y>() {
            public Y get(int aIdx) {return aPairs.get(aIdx).mSecond;}
            public int size() {return aPairs.size();}
            public Iterator<Y> iterator() {return new SecondIt<X,Y>(aPairs);}
        };
    }
    
    
    public  static <X,Y> Set<X> firstSet(final Collection<? extends Pair<X,Y>> aPairs) {
        return new HashSet<X>(firsts(aPairs));
    }

    /**
     * Set of all first items.
     */
    public  static <T,U> Set<U> secondSet(final Collection<Pair<T,U>> aPairs) {
        return new HashSet<U>(seconds(aPairs));
    }
    

    /**
     * All second items with a particular first item
     */
    public  static <T,U> Set<U> seconds(final Collection<? extends Pair<T,U>> aPairs, T aFirst) {
        Set<U> seconds = new HashSet<U>();
        
        for (Pair<T,U> p : aPairs) {
            if (p.mFirst.equals(aFirst))
                seconds.add(p.mSecond);
        }
        
        return seconds;
    }

    /**
     * Iterable view of the first items
     */
    public  static <T,U> Iterable<T> firstIt(final Iterable<Pair<T,U>> aColOfPairs) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new FirstIt<T,U>(aColOfPairs);
            }
        };
    }

    /**
     * Iterable view of the second items
     */
    public  static <T,U> Iterable<U> secondIt(final Iterable<Pair<T,U>> aColOfPairs) {
        return new Iterable<U>() {
            public Iterator<U> iterator() {
                return new SecondIt<T,U>(aColOfPairs);
            }
        };
    }
    
    protected static abstract class It<X,T,U> implements Iterator<X> {
        Iterator<? extends Pair<T,U>> mIterator;

        It(final Iterable<? extends Pair<T,U>> aColOfPairs) {
            mIterator = aColOfPairs.iterator();
        }
        
        public boolean hasNext() {return mIterator.hasNext();}
        public void remove()     {throw new UnsupportedOperationException();}
    }

    protected static class FirstIt<T,U> extends It<T,T,U> {
        FirstIt(final Iterable<? extends Pair<T,U>> aColOfPairs) {
            super(aColOfPairs);
        }
        
        public T next()          {return mIterator.next().mFirst;}  
    }

    protected static class SecondIt<T,U> extends It<U,T,U> {
        SecondIt(final Iterable<? extends Pair<T,U>> aColOfPairs) {
            super(aColOfPairs);
        }
        
        public U next()          {return mIterator.next().mSecond;}  
    }
}
