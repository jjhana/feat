package org.purl.jh.util.col;

import java.util.Collection;
import org.purl.jh.util.err.XException;

/**
 * Thrown from functions that are called under the assumption that some collection
 * contains exactly one item.
 * 
 * @author jirka
 */
public class NotSingleton extends XException {

    /**
     * Checks if a collection is a singleton. 
     * 
     * @param aCol collection to check
     * @throws NotSingleton if the collection is not a singleton
     */
    public static void check(final Collection<?> aCol) {
        if (aCol.size() != 1) throw new NotSingleton(aCol);
    }

    public NotSingleton() {
    }

    public NotSingleton(final Collection<?> aCol) {
        super("The collection should have exactly one item, but it has %d items", aCol.size());
    }


    public NotSingleton(Throwable aCause) {
        super(aCause);
    }

    public NotSingleton(Throwable aCause, String aFormat, Object ... aParams) {
        super(aCause, aFormat, aParams);
    }

    public NotSingleton(String aFormat, Object ...  aParams) {
        super(aFormat, aParams);
    }
}
