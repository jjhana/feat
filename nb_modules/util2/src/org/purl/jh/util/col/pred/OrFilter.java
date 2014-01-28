package org.purl.jh.util.col.pred;

/**
 *
 * @author Jirka
 */
public class OrFilter<T> extends CompositeFilter<T> {
    public OrFilter(Filter<T> ... aFilters) {
        super(aFilters);
    }

    /**
     * Note: the supplied list is used directly, so any changes to it affect the filter
     */
    public OrFilter(java.util.List<Filter<T>> aFilters) {
        super(aFilters);
    }


// -----------------------------------------------------------------------------
// Filter
// -----------------------------------------------------------------------------    
    
    public boolean isOk(T aElement) {
        for (Filter<T> f : mFilters) {
            if (f.isOk(aElement)) return true;
        }
        
        return false;
    }
}
    
