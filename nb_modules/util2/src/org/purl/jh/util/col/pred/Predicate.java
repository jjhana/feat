package org.purl.jh.util.col.pred;

import java.util.Collection;
import java.util.Iterator;


/**
 * An object used to select items from a collection.
 */
public interface Predicate<E> {
    boolean isOk(E aVal);

    <X extends E> Iterable<X> col(final Collection<X> aCol);
    <X extends E> Iterator<X> iterator(final Collection<X> aCol);
}
