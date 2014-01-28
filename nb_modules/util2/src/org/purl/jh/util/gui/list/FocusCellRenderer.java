package org.purl.jh.util.gui.list;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Clearly shows which list has a focus - the current item (i.e., the selected item) 
 * is in yellow selection, otherwise the selection is blue as usual.
 *
 * Subclasses can easily override formatting of the text.
 */
public class FocusCellRenderer extends JLabel implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object aValue, int aCellIdx, boolean isSelected, boolean cellHasFocus) {
        // current/other item
        if (isSelected) {
            if (list.hasFocus()) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(Color.GRAY.brighter());
                setForeground(list.getForeground());
            }

        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setEnabled(list.isEnabled());
        setOpaque(true);

        setTextEtc(list, aValue);
        return this;
    }

    /** 
     * Often overriden in subclasses.
     */
    protected void setTextEtc(JList aList, Object aValue) {
        setText(aValue.toString());
    }
}
