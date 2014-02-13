package org.purl.jh.util;

import java.util.*;

/** 
 * Map remembering weights for its keys.
 * Like Counter but uses max (\x,y.max(x,y)) to fold the old and new value instead of + (\x,y,x + y) 
 */
public class MaxCounter<T> {
    /**
     * Map containing the counters for each key. 
     */
    private final Map<T,Double> mMap;

    /**
     * Creates a new Counter class.
     */
    public MaxCounter() {
       mMap = new HashMap<T,Double>();
    }
    
    
    /** 
     * Sets the counter for the specified item to the specified value.
     *
     * @param aItem item to set the counter for
     * @param aFrequency frequency to set the counter for the item to
     */
    void set(T aItem, double aFrequency) {
        mMap.put(aItem, aFrequency);
    }


    /** 
     * Remember the maximal weight for the specified item.
     * If the item is not yet in the counter, the specified weight is remembered.
     *
     * @param aItem item to remember the max weight increment the counter for
     */
    public void add(T aItem, double aWeight) {
        Double c = mMap.get(aItem);
        if ( c == null || (c.doubleValue() <  aWeight) )
            mMap.put(aItem, aWeight);
    }

    /** 
     * Returns set of all keys (counted items).
     */
    public Set<T> getKeySet() {return mMap.keySet();}

    /** 
     * Returns weight for the specified item.
     * If the item in this counter, zero is returned.
     *
     * @param aItem item to return the weight for
     * @return weight of the specified item
     */
    public double weight(T aItem) {
        Double c = mMap.get(aItem);
        return (c == null) ? 0.0 : c.doubleValue();
    }
    
    /**
     * Returns sum of weights for items in this counter.
     */
    public double sum() {
        double sum = 0.0;
        for(Double weight : mMap.values()) 
            sum += weight.doubleValue();

        return sum;
    }
    
    /** 
     * Returns the string representation of this counter sorted by the items.
     * Lists all the items with their weights; items are sorted by their default comparator.
     * The type <tt>T</tt> has to implement <tt>Comparable</tt> interface.
     *
     * @return string representation of this class with sorted keys
     * @see  Comparable
     * @todo to Utils for general map?
     */
    public String toSortedString(){
        String tmp = "";    // @todo StringBuffer(keys.size() * .. )
        SortedSet<T> keys = new TreeSet<T>(mMap.keySet());
        
        // format: key = freq                
        for(T key : keys) {
            tmp += key + "=" + mMap.get(key) + "\n";
        }
        return tmp;
    }

}
