package org.purl.jh.util;


/**
 * Triple of objects.
 * Can contain nulls.
 */
public class Triple<A,B,C> {
    public A mFirst;
    public B mSecond;
    public C mThird;

    // private to prevent using the default constructor
    private Triple() {}

    public Triple(A aFirst, B aSecond, C aThird) {
        mFirst = aFirst;
        mSecond = aSecond;
        mThird = aThird;
    }

    @Override
    public boolean equals(final Object aX) {
        if (aX == this) return true;

        if (!(aX instanceof Triple)) return false;

        Triple x = (Triple)aX;
        return
            ( mFirst  == x.mFirst  || (mFirst  != null && mFirst.equals(x.mFirst)) ) &&
            ( mSecond == x.mSecond || (mSecond != null && mSecond.equals(x.mSecond)) ) &&
            ( mThird  == x.mThird  || (mThird  != null && mThird.equals(x.mThird)) );
    }

    @Override
    public int hashCode() {
        return
            (mFirst == null  ? 0 : mFirst.hashCode()) ^
            (mSecond == null ? 0 : mSecond.hashCode()) ^
            (mThird == null  ? 0 : mThird.hashCode());      // based on com.sun.corba.se.spi.orb.StringPair
    }

    @Override
    public String toString() {
        return String.valueOf(mFirst) + ':' + String.valueOf(mSecond) + ':' + String.valueOf(mThird);
    }


}
