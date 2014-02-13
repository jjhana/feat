package org.purl.jh.util.gui.list;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

/**
 * Listbox with a single selection.
 *
 * Note: Conceptually, there must always be a single selected item (except empty
 * lists), however the item may not be displayed (for example in filtered lists).
 *
 * Note: The scroll function requires the list to be inside a JScrollPane (and a JViewPort).
 *
 * Listening for item change can be done in the following way
 * <code>
 * list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
 *     @Override public void valueChanged(ListSelectionEvent e) {
 *         ItemType item = list.getCurItem();
 *         ...
 *     }
 * });
 * </code>
 * @todo add selection listener simplified to provide a single item
 *
 *
 * @author Jirka Hana
 */
public class SingleSelectionList<T> extends JList {


    
// =============================================================================    
// Constructors
// =============================================================================    
    
    public SingleSelectionList(ListModel aListModel) {
        super(aListModel);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public SingleSelectionList() {
        this(Collections.<T>emptyList());
    }
    
    public SingleSelectionList(final List<T> aList) {
        this(Lists.getModel(aList));
    }

// =============================================================================    
// 
// =============================================================================    
   
    public void setModel(final List<T> aList) {
        setModel(Lists.getModel(aList));
    }
    
    /**
     * Returns the index of the current item.
     *
     * @return the index of the current item; or -1 there is no current item (e.g. 
     *    there are no items, they were filtered out by a super class, etc.)
     * @see #getCurItem()
     */
    public int getCurIdx() {
        return getSelectedIndex();  //getAnchorSelectionIndex();
    }

    /** 
     * Returns the current item (the item currently selected).
     * 
     * @return the current item or null if there is no current item
     * @see #getCurIdx()
     */
    @SuppressWarnings("unchecked")
    public T getCurItem() {
        int idx = getCurIdx();
        if (idx == -1) return null;
        return (T) (getModel().getElementAt(idx));
    }

    
    /**
     * Returns the string representation of the current item
     */ 
    public String getCurStr() {
        T curItem = getCurItem();
        return (curItem == null) ? null : curItem.toString();
    }
    
    /**
     * Makes the specified item current.,
     *
     * @param aIdx index of the item to select
     */
    public void setCur(int aIdx) {
        ensureIndexIsVisible(aIdx);
        setSelectedIndex(aIdx);
    }
    
    
    
//    public void tryToSelect(T aItem, int aSelVisIdx) {
//        int idx = getModel().indexOf(aItem);        // @todo optimize
//    
//        if (idx == -1) {    
//            // the item is not present => cannot be selected 
//            // (the current form and visible selection index are not changed)
//            getSelectionModel().clearSelection();
//        }
//        else {
//            // --- try to scroll to the same place ---
//            if (aSelVisIdx != -1) {
//                scroll(idx, aSelVisIdx);
//            }
//            ensureIndexIsVisible(idx);
//            setSelectedIndex(idx);
//        }
//    }    

    /**
     * Tries to make the specified index the n-th visible index.
     *
     * Requires the list to be inside a JScrollPane (and a JViewPort). This
     * requirement is not checked.
     */
    public void scroll(int aIdx, int aNth) {
        int newFirstVisIdx = Math.max(0, aIdx - aNth);

        Point loc = indexToLocation(newFirstVisIdx);
        if (loc == null) 
            System.out.printf("Null loc: idx %d, nth %d, fvi: %d", aIdx, aNth, newFirstVisIdx );
        else
            ((JViewport)getParent()).setViewPosition(loc);
    }    

    /**
     * Returns the selected index relative to viewport.
     * 
     * @return the selected index relative to the viewport, 
     *     -1 if no index is selected.
     */
    public int getSelectedVisibleIndex() {
        if (getSelectedIndex() == -1)
            return -1;
        else
            return getSelectedIndex() - getFirstVisibleIndex();
    }

    /**
     * A convenience method for adding shortcuts to input map
     */
    public void addKeyBinding(int aKeyCode, int aModifiers, String aActionId) {
        getInputMap().put(KeyStroke.getKeyStroke(aKeyCode, aModifiers), aActionId);
    }

    /**
     * Shows tooltip on demand.
     */
    public void showToolTip() {
        Action action = getActionMap().get("postTip");
        if (action != null) {
            action.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "postTip")
            );
        }        
    }
    
// =============================================================================
// Implementation
// =============================================================================
    

    @Override public String getToolTipText() {
        setCurToolTipText();        // update
        return super.getToolTipText();
    }

    /**
     * Override to get tooltip for the current item
     * Use putClientProperty(TOOL_TIP_TEXT_KEY, String) to update the tooltip, not setToolTipText(String).
     */
    protected void setCurToolTipText() {
    }
    
    
}
