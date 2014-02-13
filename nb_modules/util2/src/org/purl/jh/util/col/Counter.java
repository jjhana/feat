package org.purl.jh.util.col;

import java.util.*;

/** 
 * Map counting frequencies of its keys.
 */
public class Counter<T> {
    /**
     * Map containing the counters for each key. 
     */
    private final Map<T,Integer> mMap;

    /**
     * Sum of all the frequencies.
     */
    private long mSigma;
    
    /**
     * Sum of squares of all the frequencies.
     */
    private long mSigmaSquare;
    
    /**
     * Creates a new Counter class with a default map implementation.
     */
    public Counter() {
       mMap = new HashMap<T,Integer>();
    }

    /**
     * Creates a new Counter class using the supplied map implementation.
     * Usually the supplied map is empty.
     */
    public Counter(Map<T,Integer> aMapImpl) {
       mMap = aMapImpl;
    }
    
    
    /** 
     * Sets the counter for the specified item to the specified value.
     *
     * @param aItem item to set the counter for
     * @param aFrequency frequency to set the counter for the item to
     */
    void set(T aItem, int aFrequency) {
        mMap.put(aItem, new Integer(aFrequency));
    }

    /** 
     * Initializes a counter for the specified item.
     *
     * @param aItem item to initialize the counter for
     */
    public void init(T aItem) {
        set(aItem, 0);
    }

    /**
     * Sets frequency of all supplied objects to zero.
     * This makes the manipulation with the counter in some cases easier.
     *
     * @param aObjs objects to initiante the counter with
     **/
/*    public void init(T[] aObjs) {
        for(int i = 0; i < aObjs.length; i++) 
            set(aObjs[i],0);
    }
*/    
    /** 
     * Increment the counter for the specified item by one.
     * If the item does not have its counter, a new counter for it is created 
     * and initialized to 1.
     *
     * @param aItem item to increment the counter for
     */
    public void add(T aItem) {
        set(aItem, frequency(aItem) + 1);
    }

    /** 
     * Increment the counter for the specified item by the specified increment.
     * If the item does not have its counter, a new counter for it is created 
     * and initialized to the specified increment.
     *
     * @param aItem item to increment the counter for
     * @param aInc size of the increment (or the initial value if aItem is not in the counter)
     */
    public void add(T aItem, int aInc) {
        set(aItem, frequency(aItem) + aInc);
    }

    /** 
     * Increment the counter for all the specified items by one.
     * If any item does not have its counter, a new counter for it is created 
     * and initialized to 1.
     *
     * @param aItems collection of items to increment the counter for
     */
    public void addAll(Iterable<T> aItems) {
        for (T item : aItems) {
            add(item);
        }
    }
    
// =============================================================================   
// Map interface    
// =============================================================================   
    
    public Map<T,Integer> getMap() {
        return mMap;
    }
    
    /** 
     * Returns set of all keys (counted items).
     */
    public Set<T> keySet()                      {return mMap.keySet();}

    
    /**
     * Returns a set view of the mappings contained in this counter.  Each element
     * in the returned set is a {@link Map.Entry}.  The set is backed by the
     * counter, so changes to the counter are reflected in the set, and vice-versa.
     *
     * If the counter is modified while an iteration over the set is in progress
     * (except through the iterator's own <tt>remove</tt> operation, or through
     * the <tt>setValue</tt> operation on a entry returned by the iterator)
     * the results of the iteration are undefined.  
     * 
     * The set supports element removal, which removes the corresponding 
     * mapping from the counter, via the <tt>Iterator.remove</tt>, 
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and 
     * <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this counter.
     */
    public Set<Map.Entry<T,Integer>> entrySet() {return mMap.entrySet();}
    
    
    /** 
     * Returns frequency of the specified item.
     * If the item does not have its counter, zero is returned.
     *
     * @param aItem item to return the frequency for
     * @return frequency of the specified item
     */
    public int frequency(T aItem) {
        Integer c = mMap.get(aItem);
        return (c == null) ? 0 : c.intValue();
    }
    
    /** 
     * Returns frequency of the specified item increased by one.
     * If the item does not have its counter, one is returned.
     *
     * @param aItem item to return the increased frequency for
     * @return frequency of the specified item plus one
     * @see #frequency(T)
     */
    public int frequencyA(T aItem) {
        return frequency(aItem) + 1;
    }

// =============================================================================    
// Summary statistics    
// =============================================================================    

    /**
     * Number of entries in the counter (all entries are counted as 1, regardless of their frequencies).
     * 
     * @return number of entries
     */
    public int size() {
        return mMap.size();
    }
    
    /**
     * Updates the sumamry statistics structure.
     * This function can be called after any update of the map before the summary
     * statistics functions are called.
     */
    public void calculate() {
        mSigma = 0;
        mSigmaSquare = 0;
        
        for(Integer freq : mMap.values()) {
            int freqV = freq.intValue();
            mSigma += freqV;
            mSigmaSquare += freqV*freqV;
        }
    }
    
    /**
     * Mean of the frequencies.
     * If the the counters are updated, the {@link #calculate()} function has to be called
     * before this function is called.  
     *
     * @return the mean of the frequencies.
     */
    public double mean() {
        return ((double) mSigma) / (mMap.size());
    }
    
    /**
     * Variance of the frequencies.
     * If the the counters are updated, the {@link #calculate()} function has to be called
     * before this function is called.  
     *
     * @return Variance of the frequencies.
     */
    public double var() {
        return ( mSigmaSquare - ( ((double)mSigma * mSigma) / mMap.size()) ) / (mMap.size() - 1);
    }

    /**
     * Standard deviation of the frequencies.
     * If the the counters are updated, the {@link #calculate()} function has to be called
     * before this function is called.  
     *
     * @return Standard deviation of the frequencies..
     */
    public double stdDev() {
        return Math.sqrt(var());
    }

    
    // H(x) = - Sum_{v in values} ( P(v)logP(#v) )
    //        P(v) = #v / (Sum_{v in values} #v )

    public double entropy() {
        double entropy = 0.0;
        for(Integer freq : mMap.values()) {
            double frac = freq.doubleValue() / ((double)mSigma);
            entropy += frac * org.purl.jh.util.Util.log2(frac);  
//            System.out.println("Sigma: " + mSigma);
//            System.out.println("freq: " + freqV);
//            System.out.println("freq/sigma: " + freqV/((double)mSigma));
//            System.out.println("log " + Math.log(freqV));
//            System.out.println("x " + freqV / ((double)mSigma) * Math.lo(freqV));
        }
        return -entropy; 
    }
    
    /**
     * Sum of frequencies.
     * If the the counters are updated, the {@link #calculate()} function has to be called
     * before this function is called.
     *
     * @return sum of frequencies.
     */
    public long sigma() {return mSigma;}

    /**
     * Sum of squared frequencies.
     * If the the counters are updated, the {@link #calculate()} function has to be called
     * before this function is called.
     *
     * @return sum of squared frequencies.
     */
    public long sigmaSquare() {return mSigmaSquare;}
    
    
// =============================================================================    
//     
// =============================================================================    
    
    public T min(Set<T> aCandidates) {
        assert (aCandidates.size() > 0);
        T min = null;
        int minV = Integer.MAX_VALUE;
        
        for(T candidate : aCandidates) {
            if (frequency(candidate) < minV) {
                minV = frequency(candidate);
                min = candidate;
            }
        }
        
        return min;
    }

    public T max(Set<T> aCandidates) {
        assert (aCandidates.size() > 0);
        T max = null;
        int maxV = Integer.MIN_VALUE;
        
        for(T candidate : aCandidates) {
            if (frequency(candidate) > maxV) {
                maxV = frequency(candidate);
                max = candidate;
            }
        }
        
        return max;
    }

    /** 
     * Returns the string representation of this class.
     * Lists all the keys with its frequencies.
     * 
     * @return string representation of this class 
     */
/*    public String toString(){
        String tmp = "";
        
        for(Iterator<Map.Entry<T,Integer>> i = mMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry<T,Integer> e = i.next();
            tmp += e.getKey() + "=" + e.getValue() + "\n";
        }
        return tmp;
    }
*/
    /** 
     * Returns the string representation of this class with sorted keys.
     * Lists all the keys with its frequencies; keys are sorted by thier default comparator.
     * The type <tt>T</tt> has to implement <tt>Comparable</tt> interface.
     *
     * @return string representation of this class with sorted keys
     * @see  Comparable
     */
    public String toSortedString(){
        StringBuilder tmp = new StringBuilder(mMap.size()*4);
        SortedSet<T> keys = new TreeSet<T>(mMap.keySet());
        
        // format: key = freq                
        for(T key : keys) {
            tmp.append(key).append('=').append(mMap.get(key)).append('\n');
        }
        return tmp.toString();
    }

    /**
     * Returns the string representation of this class with sorted keys.
     * Lists all the keys with its frequencies; keys are sorted by thier default comparator.
     * The type <tt>T</tt> has to implement <tt>Comparable</tt> interface.
     *
     * @return string representation of this class with sorted keys
     * @see  Comparable
     */
    public String toSortedString(String aKVSep, String aEntrySep){
        StringBuilder tmp = new StringBuilder(mMap.size()*4);
        SortedSet<T> keys = new TreeSet<T>(mMap.keySet());

        // format: key = freq
        for(T key : keys) {
            tmp.append(key).append(aKVSep).append(mMap.get(key)).append(aEntrySep);
        }
        return tmp.toString();
    }


   
    /**
     * Set of entries sorted by the frequency and then by natural ordering of keys.
     * Works only for counters with comparable keys.
     */
    @SuppressWarnings("unchecked")
    public SortedSet<Map.Entry<T,Integer>> setSortedByFreq() {
        SortedSet<Map.Entry<T,Integer>> freqSortedSet = new TreeSet<Map.Entry<T,Integer>>(new FreqComparator() );
        freqSortedSet.addAll(mMap.entrySet());
        return freqSortedSet;
    }

    /**
     * List of entries sorted by the frequency and then by natural ordering of keys.
     * Works only for counters with comparable keys.
     */
    public List<Map.Entry<T,Integer>> sortedByFreq() {
        List<Map.Entry<T,Integer>> freqSorted = new ArrayList<Map.Entry<T,Integer>>(mMap.entrySet());
        Collections.sort(freqSorted, new FreqComparator() );
        return freqSorted;
    }


    
    /**
     * Set of entries sorted by the frequency and then by natural ordering of keys.
     * @todo use Cols.sort....
     */
    public static class FreqComparator<X extends Comparable<X>> implements Comparator<Map.Entry<X,Integer>> {
        public int compare(Map.Entry<X,Integer> o1, Map.Entry<X,Integer> o2) {
            int freqDist = o1.getValue() - o2.getValue();
            return - ( (freqDist == 0) ? o1.getKey().compareTo(o2.getKey()) : freqDist);
        }
        public boolean equals(Object obj) {
            return obj == this;
        }
    };

    
}
