package org.purl.jh.util.col;

import java.util.Collection;
import org.purl.jh.util.fncs.Eq;

/**
 * Higher order functions over collections.
 * @author jirka
 */
public class ColsFnc {

    /**
     * Does a collection contain an element when using a specific equalizer.
     *
     * @param <T>
     * @param aCol
     * @param aElement
     * @param aEqualizer
     * @return
     */
    public static <T> boolean contains(final Collection<T> aCol, final T aElement, final Eq<T> aEqualizer) {
        for (T x : aCol) {
            if (aEqualizer.equals(x, aElement)) return true;
        }

        return false;
    }


}
