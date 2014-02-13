package org.purl.jh.util.gui.list;

import java.util.Collections;
import javax.swing.ListModel;
import org.purl.jh.util.col.pred.Filter;
import org.purl.jh.util.col.pred.FilterListener;

/**
 * A single selection list whose items can be filtered.
 * 
 * @todo why single selection??
 *
 * @param <T> type of items
 * @param <F> type of the filter
 * @author Jirka
 */
public class FilterList<T, F extends Filter<T>> extends SingleSelectionList<T> implements FilterListener {
    protected T mCurItem;

    public FilterList() {
        super(new FilterListModel<T,F>(Collections.<T>emptyList()));
    }
    
    /** 
     * By default, notification is turned on.
     *
     * @param aModel 
     */
    public FilterList(FilterListModel<T,F> aModel) {
        super(aModel);
        notifyMe(true);
    }
    
// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
   
    @SuppressWarnings("unchecked")
    @Override
    public FilterListModel<T,F> getModel() {
        return (FilterListModel<T,F>) super.getModel();
    } 

    /**
     *
     * Handles notifications.
     *
     * @see #setModel(ListModel,boolean)
     */
    @Override
    public void setModel(ListModel aModel) {
        setModel(aModel, true);
    }    
    /**
     *
     * @param aHandleNotification  if true, removes this list from the listeners of
     * the old model's filter and adds it to the listeners to the new model's filter.
     * If false does not remove or add this list to the listeners of any filter
     */
    public void setModel(ListModel aModel, boolean aHandleNotification) {
        if (!(aModel instanceof FilterListModel) ) throw new IllegalArgumentException();
        
        if (aHandleNotification && getModel() != null) notifyMe(false);
        
        super.setModel(aModel);
        if (aHandleNotification) notifyMe(true);
    } 
    
// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------    
    
    /**
     * Adds this list as a listener to its own filter. 
     * If the filter supports notifications, this list will be notified 
     * when the filter changes.
     *
     * @param aNotify notification is turned on when <code>true</code>, and off when <code>false</code>.
     * @see AbstractFilter#updated()
     */ 
    public void notifyMe(boolean aNotify) {
        //System.out.println("notifyMe " + aNotify + " "+ this.getClass().getName());
        if (aNotify) {
            getModel().getFilter().addFilterListener(this);
        }
        else {
            getModel().getFilter().removeFilterListener(this);
        }
    }
    
    @Override
    public void filterUpdated() {
        refresh();
    }
    
    /**
     * Refreshes the model, trying to keep selection.
     * 
     * @todo does not work if there are multiple lists with the same model
     */
    public void refresh()                   {
        int curVisIdx = getSelectedVisibleIndex();
        mCurItem =  getCurItem();   // todo this is weird, either keep constantly uptodate or drop
        //System.out.println("refresh " + curVisIdx + " " + mCurItem);
        
        getModel().refresh();
        //System.out.println("refresh2 " + mCurItem);

        tryToSetCur(mCurItem, curVisIdx);
    }

    
    /**
     * Attempts to make the specified item current.
     */
    public void tryToSetCur(T aItem) {
        tryToSetCur(aItem, -1);
    }

    /**
     * Attempts to make the specified item current and scroll it to the desired
     * location.
     *
     * @param aSelVisIdx the visible index the current item should be scrolled to. 
     *    If -1, no scrolling is performed
     */
    public void tryToSetCur(T aItem, int aSelVisIdx) {
//        System.out.println("tryToSetCur1: " + aSelVisIdx + " " + mCurItem);

        int idx = getModel().indexOf(aItem);        // @todo optimize
//        System.out.println("tryToSetCur2: " + idx + " " + mCurItem);
    
        if (idx == -1) {    
            // the item is not present => cannot be selected 
            // (the current form and visible selection index are not changed)
            getSelectionModel().clearSelection();
        }
        else {
            // --- try to scroll to the same place ---
            if (aSelVisIdx != -1) {
                scroll(idx, aSelVisIdx);
            }
            setCur(idx);
        }
    }    

    
}
