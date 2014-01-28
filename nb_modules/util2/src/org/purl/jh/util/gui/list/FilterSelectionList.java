package org.purl.jh.util.gui.list;

import org.purl.jh.util.col.pred.Filter;
import org.purl.jh.util.col.pred.FilterListener;

/**
 * Filtered list with a single, always visible, selection. If the
 * filter hides the current selection, the first non-filtered item is selected instead.
 *
 * @author Jirka
 */
public class FilterSelectionList<T, F extends Filter<T>> extends FilterList<T,F> implements FilterListener {

    public FilterSelectionList() {
        super();
    }

    /**
     * By default, notification is turned on.
     *
     * @param aModel
     */
    public FilterSelectionList(FilterListModel<T,F> aModel) {
        super(aModel);
        notifyMe(true);
    }


    /**
     * Refreshes the model, trying to keep selection, if the currently
     * selected item gets filtered-out the first item is selected insted.
     */
    @Override
    public void refresh()                   {
        super.refresh();

        int idx = getCurIdx();
        if (idx == -1) setCur(0);
    }
}
