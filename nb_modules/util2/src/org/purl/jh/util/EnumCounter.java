package org.purl.jh.util;

import java.util.*;


public class EnumCounter<K extends Enum<K>> {
    /**
     * The <tt>Class</tt> object for the enum type of all the keys of this map.
     *
     * @serial
     */
    //private final Class<K> keyType;

    /**
     * Array representation of this map.  The ith element is the value
     * to which universe[i] is currently mapped, or null if it isn't
     * mapped to anything, or NULL if it's mapped to null.
     */
    private int[] vals;
    
    private int mTotal;


    //private static Enum[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];

    /**
     * Creates an empty enum map with the specified key type.
     *
     * @param keyType the class object of the key type for this enum map
     * @throws NullPointerException if <tt>keyType</tt> is null
     */
    public EnumCounter(Class<K> aKeyType) {
        vals = new int[aKeyType.getEnumConstants().length];   // 
    }

    
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return vals.length;
    }

    public int total() { 
        return mTotal;
    }

    /**
     */
    public int frequency(Enum<K> aVal) {
        return vals[aVal.ordinal()];
    }

    /**
     */
    public int inc(Enum<K> aVal) {
        mTotal++;
        return (vals[aVal.ordinal()]++);
    }

    /**
     */
    public int inc(Enum<K> aVal, int aDelta) {
        mTotal += aDelta;
        return (vals[aVal.ordinal()] += aDelta);
    }

    public void addAll(EnumCounter<K> aCounter) {
        mTotal  += aCounter.mTotal;
        for (int i = 0; i < vals.length; i++) {
            vals[i] += aCounter.vals[i];
        }
    }
    
    
    public String toString() {
        return Arrays.toString(vals);
    }

    
    /**
     * Checks whether all points are with the specified value.
     * Equivalent to: frequency(aVal) == total()
     */
    public boolean all(Enum<K> aVal) { 
        return frequency(aVal) == mTotal;
    }

}
