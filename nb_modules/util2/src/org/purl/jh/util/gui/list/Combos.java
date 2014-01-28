package org.purl.jh.util.gui.list;

import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * Utility class with methods for listboxes.
 * @author Jirka
 */
public class Combos {

    public static abstract class AbstractComboBoxModel<X> extends AbstractListModel implements ComboBoxModel  {
        X selectedObject;
        
        public void setSelectedItem(Object anObject) {
            if ((selectedObject != null && !selectedObject.equals( anObject )) ||
                selectedObject == null && anObject != null) {
                selectedObject = (X) anObject;
                fireContentsChanged(this, -1, -1);
            }
        }

        // implements javax.swing.ComboBoxModel
        public X getSelectedItem() {
            return selectedObject;
        }
    }
    
    /**
     * Creates a lightweight model wrapped by the supplied list.
     *
     * @param <X> type of the list item
     * @param aList list to wrap into the model
     * @return model
     * @todo move elsewhere
     */
    public static <X> ComboBoxModel getModel(final List<X> aList) {
        return new AbstractComboBoxModel<X>() {
            public int getSize() { return aList.size(); }
            public X getElementAt(int i) { return aList.get(i); }
        };
    }

}
