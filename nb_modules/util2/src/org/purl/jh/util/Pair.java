package org.purl.jh.util;


/**
 * Pair of objects.
 * Can contain nulls.
 */
public class Pair<T,U> {
    public T mFirst;
    public U mSecond;

    //public static *Self* pair(T aFirst, U aSecond) ? how to construct the actual (subclassed) class

    // private to prevent using the default constructor
    private Pair() {}

    public Pair(T aFirst, U aSecond) {
        mFirst = aFirst;
        mSecond = aSecond;
    }
    
    @Override
    public boolean equals(final Object aX) {
        if (aX == this) return true;
        
        if (!(aX instanceof Pair)) return false;

        final Pair x = (Pair)aX;
        return 
            ( mFirst  == x.mFirst  || (mFirst  != null && mFirst.equals(x.mFirst)) ) &&
            ( mSecond == x.mSecond || (mSecond != null && mSecond.equals(x.mSecond)) );
    }
    
    @Override
    public int hashCode() {
        return 
            (mFirst == null  ? 0 : mFirst.hashCode()) ^ 
            (mSecond == null ? 0 : mSecond.hashCode());      // based on com.sun.corba.se.spi.orb.StringPair
    }

    @Override
    public String toString() {
        return String.valueOf(mFirst) + ':' + String.valueOf(mSecond);
    }

    
}
