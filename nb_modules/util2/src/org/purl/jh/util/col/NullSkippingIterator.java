package org.purl.jh.util.col;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Jirka
 */
public class NullSkippingIterator<T> implements Iterator<T> {

    private final Iterator<T> it;
    private T prepared;

    public NullSkippingIterator(final Iterator<T> aIt) {
        it = aIt;
        fetchNext();
    }

    public NullSkippingIterator(final Iterable<T> aCol) {
        this(aCol.iterator());
    }


    public boolean hasNext() {
        return prepared != null;
    }

    public T next() {
        final T tmp = prepared;
        if (hasNext()) {
            fetchNext();
            return tmp;
        }

        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void fetchNext() {
        prepared = null;
        for (; prepared == null && it.hasNext();) {
            prepared = it.next();
        }
    }
}
