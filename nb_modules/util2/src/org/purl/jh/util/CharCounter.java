package org.purl.jh.util;

import org.purl.jh.util.col.Counter;

/** 
 * Boxed version of Counter for char.
 * This class can be droped once Java supports automatic boxing.
 *
 * @see Counter
 * @deprecated Use Counter<Character>
 */
@Deprecated
public class CharCounter extends Counter<Character> {

    /**
     * Creates a counter and sets frequency of all supplied characters to zero.
     * This makes the manipulation with the counter in some cases easier.
     *
     * @param aChars characters to initiante the counter with
     * @return the initialized counter
     **/
    public static CharCounter initCounter(char[] aChars) {
        CharCounter counter = new CharCounter();
        counter.init(aChars);
        return counter;
    }

    /** 
     * Sets the counter for the specified item to the specified value.
     *
     * @param aItem item to set the counter for
     * @param aFrequency frequency to set the counter for the item to
     */
    public void set(char aItem, int aFrequency) {
        set(new Character(aItem), aFrequency);
    }

    /** 
     * Initializes a counter for the specified item.
     *
     * @param aItem item to initialize the counter for
     */
    public void init(char aItem) {
        init(new Character(aItem));
    }

    /**
     * Sets frequency of all supplied characters to zero.
     * This makes the manipulation with the counter in some cases easier.
     *
     * @param aChars characters to initiante the counter with
     **/
    public void init(char[] aChars) {
        for(int i = 0; i < aChars.length; i++) {
            char c = (char)aChars[i];
            set(c,0);
        }
    }

    /** 
     * Increment the counter for the specified item by one.
     * If the item does not have its counter, a new counter for it is created 
     * and initialized to 1.
     *
     * @param aItem item to increment the counter for
     */
    void add(char aItem) {
        add(new Character(aItem));
    }

    /** 
     * Returns frequency of the specified item.
     * If the item does not have its counter, zero is returned.
     *
     * @param aItem item to return the frequency for
     * @return frequency of the specified item
     */
    public int frequency(char aItem) {
        return frequency(new Character(aItem));
    }
  
}
