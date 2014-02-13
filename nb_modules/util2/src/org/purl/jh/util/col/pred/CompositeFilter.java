package org.purl.jh.util.col.pred;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A filter based on a list of filters, combining them by some operation. 
 * Subclasses (can) implement and, or, xor, etc.
 * 
 * @author Jirka
 */
public abstract class CompositeFilter<T> extends AbstractFilter<T> implements FilterListener {  // List<Filter<T>>
    protected List<Filter<T>> mFilters;
    
    public CompositeFilter(Filter<T> ... aFilters) {
        mFilters = new ArrayList<Filter<T>>(Arrays.asList(aFilters));
    }

    /**
     * Note: the supplied list is used directly, so any changes to it affect the filter
     */
    public CompositeFilter(List<Filter<T>> aFilters) {
        mFilters = aFilters;
        for (Filter f : mFilters) 
            f.addFilterListener(this);
    }


// -----------------------------------------------------------------------------
// Filter
// -----------------------------------------------------------------------------    
    
    public abstract boolean isOk(T aElement);

    
// -----------------------------------------------------------------------------
// FilterListener
// -----------------------------------------------------------------------------    

    public void filterUpdated() {
        updated();
    }
    
// -----------------------------------------------------------------------------
// List
// -----------------------------------------------------------------------------    

    public List<Filter<T>> getFilters() {
        return mFilters;
    }
    
    public Iterator<Filter<T>> iterator() {
        return mFilters.iterator();
    }
    
    public int size() {
        return mFilters.size();
    }
    
    public boolean add(Filter<T> aFilter) {
        return mFilters.add(aFilter);
    }

    public Filter<T> set(int aIdx, Filter<T> aElement) {
	return mFilters.set(aIdx, aElement);
    }
    
    public Filter<T> get(int aIdx) {
	return mFilters.get(aIdx);
    }
    
    public void remove(Filter<T> aFilter) {
        mFilters.remove(aFilter);
    }
}
    
