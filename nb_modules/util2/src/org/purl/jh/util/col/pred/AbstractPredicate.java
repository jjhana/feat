package org.purl.jh.util.col.pred;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.event.EventListenerList;

/**
 * This filter is intended for quickly instantiated <i>constant</i> filters. 
 * Thus it has not listeners support.
 *
 * @author Jirka
 */
public abstract class AbstractPredicate<T> implements Predicate<T> {
    public <X extends T> Iterable<X> col(final Collection<X> aCol) {
        return new Iterable<X>() {
            public Iterator<X> iterator() {return new FilteringIt<X>(aCol, AbstractPredicate.this); }
        };
    }
    
    public <X extends T> Iterator<X> iterator(final Collection<X> aCol) {
        return new FilteringIt<X>(aCol, AbstractPredicate.this);
    }
}

        