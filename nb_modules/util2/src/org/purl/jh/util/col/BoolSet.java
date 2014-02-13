package org.purl.jh.util.col;


/**
 * DOES NOT WORK!!! USE BitSet or BoolTag instead!!
 *
 * This immutable class implements a vector of up to 64 bools (bits) .
 * The bits are indexed by nonnegative integers.
 * The class is immutable, operations like set, etc. produce a new vector. 
 */
public final class BoolSet implements Comparable<BoolSet> {
    protected final static long cBitsPerUnit     =  1 << 6;	// = 2^(2^6) ???
    protected final static long cAllTrueBits     = cBitsPerUnit - 1;			// 1....1 (64*)

    /**
     * The mBits in this BoolSet.  
     */
    protected final long mBits;  
    protected final int mSize;

// --------------------------------------------------------------------------------------------
    public static BoolSet allTrue(int aSize) {return new BoolSet(aSize).set(0,aSize-1); }
    public static BoolSet allFalse(int aSize) {return new BoolSet(aSize); }

    public BoolSet(int aSize)	{
            this(aSize, 0);
    }

    protected BoolSet(int aSize, long aBits)	{
		assert aSize <= 64;
		mSize = aSize;
        mBits = aBits;
    }


// --------------------------------------------------------------------------------------------
// set, get
// --------------------------------------------------------------------------------------------

    /** 
     * Returns the value of the bit of the specified slot. 
     *
	 * @param  aSlot   slot index of the bit to be returned. Must be in <0,14> (not checked)
     * @return the value of the bit with the specified index.
     */
    public boolean get(int aSlot) {
		return (mBits & bit(aSlot)) != 0 ;
	}
    

    /**
	 * Assigns the specified bit the specified value.
	 *
	 * @param  aSlot   slot index of the bit to be set. Must be in <0,14> (not checked)
     * @param aValue      value to be assigned to the specified bit
	 */
    public BoolSet set(int aSlot, boolean aValue)	{
		return (aValue) ? set(aSlot) : clear(aSlot);
	}

	/**
	 * Sets the bit specified by the index to <code>true</code>.
	 *
	 * @param  aSlot   slot index of the bit to set. Must be in <0,14> (not checked)
	 */
    public BoolSet set(int aSlot)	{
        return newOne(mBits | bit(aSlot));
    }



    /** 
     * Sets the bit specified by the index to <code>false</code>.
     *
	 * @param  aSlot   index of the bit to be cleared. Must be nonnegative (not checked, asserted).
     */
    public BoolSet clear(int aSlot) {
		return newOne(mBits & ~bit(aSlot));
	}

    /**
	 * Set all bits in the specified range to <code>true</code>.
     *
     * @param aFrom the first index to be set
     * @param aTo the last index to be set
	 */
    public BoolSet set(int aFrom, int aTo)	{
		long tmpBits = mBits;
		
		for (int i = aFrom; i <= aTo; i++)
			tmpBits = setI(tmpBits, i);
		
		return newOne(tmpBits);
	}

    /**
	 * Set all bits in the specified range to <code>false</code>.
     *
     * @param aFrom the first index to be cleared
     * @param aTo the last index to be cleared
	 */
    public BoolSet clear(int aFrom, int aTo)	{
		long tmpBits = mBits;
		
		for (int i = aFrom; i <= aTo; i++)
			tmpBits = clearI(tmpBits, i);

		return newOne(tmpBits);
	}



// --------------------------------------------------------------------------------------------
// Logical operations (not in place)
// --------------------------------------------------------------------------------------------

    /** 
 * Result = ~this
 * <p>
     * Returns a logical <b>not</b> of this BoolSet.
     * Each bit in it the resulting bit is reversed (<code>true</code> to <code>false</code> and vice versa.)
     *
     * @return ~aSet
     */
    public BoolSet not() {
            return newOne(~mBits);
    }

    /** 
 * Result = this & aSet.
 * <p>
     * Returns a logical <b>and</b> of this BoolSet and the supplied one.
     * Each bit in it the resulting BoolSet has the value <code>true</code> iff
     * corresponding bit in both BoolSet have value <code>true</code>.
     *
     * @param  aSet  a BoolSet.
     * @return (this & aSet)
     */
    public BoolSet and(BoolSet aSet) {
            return newOne(mBits & aSet.mBits);
    }

    /** 
 * Result = this & ~aSet.
 * <p>
     * Returns a logical <b>nand</b> of this BoolSet and the supplied one.
     * Each bit in it the resulting BoolSet has the value <code>true</code> iff
     * corresponding bit in this BoolSet is <code>true</code> and in aSet is <code>false</code> .
     *
     * @param  aSet  a BoolSet.
     * @return (this & ~aSet)
     */
    public BoolSet nand(BoolSet aSet)	{
            return newOne(mBits & (~aSet.mBits));
    }

    /** 
 * Result = this or aSet.
 * <p>
     * Returns a logical <b>or</b> of this BoolSet and the supplied one.
     * Each bit in it the resulting BoolSet has the value <code>true</code> iff
     * either the corresponding bit in this BoolSet and/or in aSet is <code>true</code> .
     *
     * @param  aSet  a BoolSet. It has to be at least as long as this BoolSet
     * @return (this or aSet)
     */
    public BoolSet or(BoolSet aSet)	{
            return newOne( mBits | aSet.mBits);
    }

    /**
 * Result = this xor aSet.
 * <p>
 * Returns a logical <b>xor</b> of this BoolSet and the supplied one.
 * Each bit in it the resulting BoolSet has the value <code>true</code> iff
 * either the corresponding bit in this BoolSet or in aSet is <code>true</code>, but not both.
 *
 * @param aSet a BoolSet. It has to be at least as long as this BoolSet
 * @return (this xor aSet)
 */
    public BoolSet xor(BoolSet aSet) {
            return newOne(mBits ^ aSet.mBits);
    }

// --------------------------------------------------------------------------------------------
// Test
// --------------------------------------------------------------------------------------------

    /**
     * ## TODO: more effective
     */
    public BoolSet subMap(int aBeginIndex, int aEndIndex) {
        int size = aEndIndex - aBeginIndex;
        long tmpBits = 0;
        
        for (int i = 0; i < size; i++)
        	if (get(aBeginIndex+i))
            	tmpBits = setI(tmpBits, i);
            else
            	tmpBits = clearI(tmpBits, i);
        
        return new BoolSet(size, tmpBits);
    }
    
// --------------------------------------------------------------------------------------------
// Test
// --------------------------------------------------------------------------------------------

    /** Tests whether this BoolSet is a subset of the supplied BoolSet
 *
 * @param aSet potential superset of this set
 * @return <CODE>true</CODE> iff this BoolSet is a subset of the supplied BoolSet
 */
    public boolean isSubsetOf(BoolSet aSet)	{
    return
        ( (mBits & aSet.mBits) == mBits) &&
        ( (mBits | aSet.mBits) == aSet.mBits);
    }

    /** Tests whether all bits in this set are <code>true</code>
 * @return <code>true</code> iff all bits are <code>false</code>
 * @todo optimize
 */
    public boolean hasAllSet()	{
    for (int i = 0; i < mSize; i++)
        if (!get(i)) return false;
    return true;
    }

    /** Tests whether all bits in this set are <code>false</code>
 * @see #allCleared(int,int)
 * @return <code>true</code> iff all bits are <code>false</code>
 */
    public boolean hasAllCleared() 	{
    return (mBits == 0);
    }

    /** 
 * Tests whether some bits in this set are <code>true</code>
 * @return <code>true</code> iff all bits are not <code>false</code>
 */
    public boolean hasSomeSet() 	{
    return (mBits != 0);
    }

    /** Tests whether all bits in the specified range are <code>false</code>.
     * Slow in comparison with allCleared() - allCleared() goes by longs, this goes by bits.
     *
     * @param aStart  start of the range (inclusive)
     * @param aEnd end of the range (exclusive)
     * @see #allCleared()
     * @return <code>true</code> if all bits in the specified range are <code>false</code>
     */
    public boolean hasAllCleared(int aStart, int aEnd)	{
            for (int i=aStart; i < aEnd; i++)
                    if (get(i)) return false;

            return true;
    }

    /**
     * Returns number of <code>true</code> bits.
     *
     * @return number of <code>true</code> bits.
 * ## TODO: more effective
 * @ho
     */
    public int nrOfSet() {
            int sum = 0;
            for (int i = 0; i < mSize; i++)
                    if (get(i)) sum++;

            return sum;
    }
    
// --------------------------------------------------------------------------------------------
// Implementation
// --------------------------------------------------------------------------------------------

    public int compareTo(BoolSet aOther) {
	return mBits < aOther.mBits ? -1 : (mBits==aOther.mBits ? 0 : 1); // cannot be just subtracted because of loss of precission
    };
    
    /**
     * Returns a hash code value for this BoolSet. The hash code
     * is dependent on the internal BoolSet.
     * <p>Over  rides the <code>hashCode</code> method of <code>Object</code>.
     *
     * @return  a hash code value for this BoolSet.
     */
    public int hashCode() {
            return (int)mBits;
    }

    /** 
     * Compares this object against the specified object.
     * The result is <code>true</code> iff the argument 
     * <ul>
     *     <li>is not <code>null</code></li>
     *     <li>has exactly the same bits set to <code>true</code> as this bimap.
     *         That is, \forall i \in &lt; 0, cTagLen &gt; : 
     *              ((Boo       lSet)aObj).get(i) == this.get(i)</li>
     * </ul>
     * <p>Overrides the <code>equals</code> method of <code>Object</code>.
     *
     * @param  aObj the object to compare with.
     * @return <code>true</code> if the objects are the same;
     *          <code>false</code> otherwise.
     * @see java.util.BoolSet#equals(Object,int,int)
     */
    public boolean equals(Object aObj) {
            if (aObj == null || !(aObj instanceof BoolSet))
                    return false;
    return (mBits == ((BoolSet) aObj).mBits);
    }

    /**
     * Returns a string representation of this BoolSet.
     * Set is represented as set a sequence of '+' (for set slots) and '-'.
     *
     * @return  a string representation of this BoolSet.
     */
    public String toString() {
            StringBuilder buf = new StringBuilder(mSize);

            for (int i = 0 ; i < mSize; i++) {
                    buf.append( get(i) ? '+' : '-' );
            }

            return buf.toString();
    }

// --------------------------------------------------------------------------------------------
// Implementation
// --------------------------------------------------------------------------------------------

    protected BoolSet newOne(long aBits) {
            return new BoolSet(mSize, aBits);
    }

    /**
     * In place, sets the bit specified by the index to <code>true</code>.
     */
    protected static long setI(long aBits, int aSlot)	{
            return aBits | bit(aSlot);
    }

    /**
     * In place, sets the bit specified by the index to <code>false</code>.
     */
    protected static long clearI(long aBits, int aSlot)	{
            return aBits & (~bit(aSlot));
    }

    /**
     * Given a bit index, return a long that has exactly that bit set.
     * 
 * @param aSlot bit that should be set to <code>true</code>.
 * @return unit with all zeros except the bit at aSlot (?? bitIndex mod ADDRESS_BITS_PER_UNIT)
     *    3 -> 00..01000, 0 -> 00..00001
     */
    protected static int bit(int aSlot) {
            return 1 << (aSlot & cAllTrueBits);	// = ? bitIndex % 32
    }
}
