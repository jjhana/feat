package org.purl.jh.util.gui.list;

import java.util.List;
import org.purl.jh.util.col.pred.Filter;
import org.purl.jh.util.col.pred.Filters;

/**
 *
 * @author Jirka
 */
public class TrueFilterListModel<T> extends FilterListModel<T,Filter<T>> {
    
    public TrueFilterListModel() {
        super(Filters.<T>trueFilter());
    }
    
    public TrueFilterListModel(List<T> aList) {
        super(aList, Filters.<T>trueFilter());
    }
    
}
