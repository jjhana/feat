/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.util.gui.list;

import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * Utility class with methods for listboxes.
 * @author Jirka
 */
public class Lists {
    /**
     * Creates a lightweight model wrapped by the supplied list.
     *
     * @param <X> type of the list item
     * @param aList list to wrap into the model
     * @return model
     * @todo move elsewhere
     */
    public static <X> ListModel getModel(final List<X> aList) {
        return new AbstractListModel() {
            public int getSize() { return aList.size(); }
            public X getElementAt(int i) { return aList.get(i); }
        };
    }

}
