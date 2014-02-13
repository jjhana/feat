package org.purl.jh.util.col.pred;


/**
 * An object used to select items from a collection.
 * This is a predicate extended with listener support.
 *
 * @author Jirka
 */
public interface Filter<E> extends Predicate<E> {
    void addFilterListener(FilterListener aListener);
    void removeFilterListener(FilterListener aListener);
}


