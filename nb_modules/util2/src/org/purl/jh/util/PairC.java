package org.purl.jh.util;

/**
 * Comparable Pair of comparable items.
 * Cannot contain nulls.
 *
 * @author Jirka
 */
public class PairC<T extends Comparable<T>, U extends Comparable<U>> 
        extends Pair<T,U> implements Comparable<PairC<T,U>> {
    
    public PairC(T aFirst, U aSecond) {
        super(aFirst, aSecond);
    }

    /**
     * Does not support pairs storing nulls.
     */
    public int compareTo(final PairC<T,U> aPair) {
        final int tmp = mFirst.compareTo(aPair.mFirst);
        return (tmp == 0) ? mSecond.compareTo(aPair.mSecond) : tmp;
    }
        
}
