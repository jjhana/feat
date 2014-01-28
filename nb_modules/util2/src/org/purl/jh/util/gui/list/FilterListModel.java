package org.purl.jh.util.gui.list;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.pred.Filter;
import org.purl.jh.util.col.pred.Filters;

/**
 *
 * @todo encapsulate nonfiltered model
 * @author Jirka
 */
public class FilterListModel<T, F extends Filter<T>> extends AbstractListModel {
    protected final List<T> mAllItems;

    /** Items from mAllItems satisfying the filter <i>and</i> not removed by 
     * {@link #remove(java.lang.Object)} or {@link #remove(int)}
     */
    protected List<T> mFilteredItems;


    protected F mFilter = null;

    
    /** Creates a new instance of FilterListModel */
    public FilterListModel(F aFilter) {
        this(new ArrayList<T>(), aFilter);
    }

    /**
     * Use only if F is Filter<T>!!
     * @param aList the full list of items. It is used directly.
     */
    @SuppressWarnings("unchecked")
    public FilterListModel(List<T> aList) {
        this(aList, (F) Filters.<T>trueFilter());
    }
    
    /**
     * @param aList the full list of items. It is used directly.
     * @param aFilter filter used
     */
    public FilterListModel(List<T> aList, F aFilter) {
        mAllItems = aList;
        mFilteredItems = mAllItems;
        setFilter(aFilter);
    }
    
// -----------------------------------------------------------------------------
// Filtering
// -----------------------------------------------------------------------------    
    
    public F getFilter() {
        return mFilter;
    }

    public void setFilter(F aFilter) {
        mFilter = aFilter;
    }

    /**
     */
    public void refresh() {
        //System.out.println("FLModel: Filtering " + mFilter);
        if (mFilter == null) 
            mFilteredItems = mAllItems; 
        else {
            // HO: filter(mAllItems, \i . mFilter.formOk(i));
            mFilteredItems = new ArrayList<T>(mAllItems.size());
            for(T i : mAllItems) {
                if  ( mFilter.isOk(i) ) {
                    mFilteredItems.add(i);
                }
            }
        }

        fireContentsChanged(this, 0, getSize());
    }

    /**
     * Hides the specified item. Nothing happens if the item is not shown (or 
     * is not in the list at all).
     * Effect of this function is lost after calling refresh.
     * 
     * @param aItem item to remove. 
     */
    public void remove(T aItem) {
        int idx = mFilteredItems.indexOf(aItem);
        if (idx != -1) remove(idx);
    }
    
    /**
     * Hides the item with the specified index.
     * Effect of this function is lost after calling refresh.
     *
     * @param aIdx index of the item to remove (within filtered items)
     */
    public void remove(int aIdx) {
        mFilteredItems.remove(aIdx);
        fireIntervalRemoved(this, aIdx, aIdx);
    }

    public List<T> getFilteredItems() {
        return mFilteredItems;
    }
    
// -----------------------------------------------------------------------------
// Abstract Model implementation
// -----------------------------------------------------------------------------    

    /**
     * Size of the filtered list
     */
    public int getSize() {
        return mFilteredItems.size();
    }

    /**
     * @param index of the desired element (in the filtered list)
     */
    public T getElementAt(int aIdx) {
        return mFilteredItems.get(aIdx);
    }
    
// -----------------------------------------------------------------------------
// Other 
// -----------------------------------------------------------------------------    

    /**
     * Updates the rendering of the specified item (if shown).
     */
    public void updateItem(T aItem) {
        int idx = mFilteredItems.indexOf(aItem);
        if (idx == -1) return;
        fireContentsChanged(this, idx, idx);
    }
    
    public int indexOf(T aItem) {
        return mFilteredItems.indexOf(aItem);
    }

    public int biasedIndexOf(T aItem, int aBias) {
        return Cols.biasedIndexOf(mFilteredItems, aItem, aBias);
    }

// -----------------------------------------------------------------------------
// Access to all items
// -----------------------------------------------------------------------------    

    public int getAllSize() {
        return mAllItems.size();
    }

    public List<T> getAllItems() {
        return mAllItems;
    }

    public int indexOfInAll(T aItem) {
        return mAllItems.indexOf(aItem);
    }
}
