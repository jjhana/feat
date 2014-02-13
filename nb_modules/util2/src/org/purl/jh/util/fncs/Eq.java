package org.purl.jh.util.fncs;

/**
 * 
 * @todo?? remove eq semantics? Call BinPredicate? Rel? But both args are of the same type
 * @author jirka
 */
public interface Eq<T> { // todo implemnts BinPredicate<T,T>
    boolean equals(T a, T b);
}
