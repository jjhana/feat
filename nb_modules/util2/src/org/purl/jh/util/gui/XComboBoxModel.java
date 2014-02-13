package org.purl.jh.util.gui;

import java.util.List;
import javax.swing.ComboBoxModel;

/**
 * Basic Combobox model supporting collection's list (Swing works with Vectors).
 * 
 * The 
 * @author jirka
 */
public class XComboBoxModel<I> extends XListModel<I> implements ComboBoxModel {
    private I selectedItem;
    
    public XComboBoxModel(I ... items) {
        super(items);
    }

    /**
     * The model is backed by the passed list.
     * @param items 
     */
    public XComboBoxModel(List<I> items) {
        super(items);
    }

    
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    @Override
    public void setSelectedItem(Object anObject) {
        if ((selectedItem != null && !selectedItem.equals( anObject )) ||
	    selectedItem == null && anObject != null) {
	    selectedItem = (I)anObject;
	    fireContentsChanged(this, -1, -1);
        }
    }

    @Override
    public I getSelectedItem() {
        return selectedItem;
    }
}
