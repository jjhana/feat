package org.purl.jh.util.col.pred;

/**
 *
 * @author Jirka
 */
public class FilterUpdateEvent<T> extends java.util.EventObject {
    Filter<T> mFilter;

    public FilterUpdateEvent(Object aSrc, Filter<T> aFilter) {
        super(aSrc);
        mFilter = aFilter;
    }

    public Filter<T> getFilter() {
        return mFilter;
    }
}
