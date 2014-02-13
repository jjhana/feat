package org.purl.jh.util.col.pred;

import java.util.Collection;
import java.util.Iterator;

/**
 * Filtering iterator.
 */
public final class FilteringIt<E> implements Iterator<E> {

    public static <X> Iterable<X> col(final Collection<X> aCol, final Predicate<? super X> aFilter) {
        return new Iterable<X>() {
            @Override
            public Iterator<X> iterator() {return new FilteringIt<>(aCol, aFilter); }
        };
    }
    
// -----------------------------------------------------------------------------
// Constructors
// -----------------------------------------------------------------------------
    
    public FilteringIt(Iterator<E> aIterator, Predicate<? super E> aFilter) {
        mIt = aIterator;
        mFilter = aFilter;
        findNext();
    }

    public FilteringIt(Collection<E> aCol, Predicate<? super E> aFilter) {
        mIt = aCol.iterator();
        mFilter = aFilter;
        findNext();
    }

// -----------------------------------------------------------------------------
// Iterator 
// -----------------------------------------------------------------------------
    
    @Override
    public boolean hasNext() {
        return mNextItem != null;
    }

    @Override
    public E next() {
        E item = mNextItem;
        findNext();
        return item;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

// =============================================================================
// Implementation
// =============================================================================
    
    final private Iterator<E> mIt;
    final private Predicate<? super E> mFilter;
    private E mNextItem;
    
    private void findNext() {
        for (; mIt.hasNext(); ) {
            mNextItem = mIt.next();
            if (mFilter.isOk(mNextItem)) return;
        }
        mNextItem = null;
    }
    
}
