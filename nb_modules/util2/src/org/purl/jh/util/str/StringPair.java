package org.purl.jh.util.str;

import org.purl.jh.util.PairC;

/**
 * Pair of two strings. The string pairs are comparable (lexical ordering).
 *
 * @author  Jirka Hana
 */
public final class StringPair extends PairC<String,String> {
    
    /** Creates a new instance of StringPair */
    public StringPair(String aFirst, String aSecond) {
        super(aFirst, aSecond);
    }
    
}
