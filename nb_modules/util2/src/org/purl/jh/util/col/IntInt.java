
package org.purl.jh.util.col;

import org.purl.jh.util.PairC;


/**
 *
 * @author Administrator
 */
public class IntInt extends PairC<Integer,Integer> {
    public IntInt(int aFirst, int aSecond) {
        super(aFirst, aSecond);
    }

    /**
     * Absolute distance between the two numbers.
     * @return
     */
    public int distance() {
        return Math.abs(mSecond - mFirst);
    }

}
