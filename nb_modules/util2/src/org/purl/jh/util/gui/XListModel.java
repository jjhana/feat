package org.purl.jh.util.gui;

import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * Basic JList model supporting collection's list (Swing works with Vectors).
 * @author jirka
 */
public class XListModel<I> extends AbstractListModel {
    private final List<I> items;

    public XListModel(I ... items) {
        this.items = Arrays.asList(items);
    }

    /**
     * The model is backed by the passed list.
     * @param items 
     */
    public XListModel(List<I> items) {
        this.items = items;
    }

    @Override
    public int getSize() { 
        return items.size();
    }

    @Override
    public I getElementAt(int i) {
        return items.get(i);
    }
}
