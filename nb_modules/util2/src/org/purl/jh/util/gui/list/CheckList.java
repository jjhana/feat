package org.purl.jh.util.gui.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import org.purl.jh.util.col.pred.Filter;



/**
 * How to use:
 * <ol>
 * <li> Subclass it
 * <li> Override isSelected/setSelected.
 * <li> Call addChecks() (e.g. in the constructor). if you use your own your own renderer
 *    call addChecks() after you set it.
 * </ol>
 *
 * @author Jirka Hana
 */
public abstract class CheckList<T,F extends Filter<T>> extends FilterList<T,F> {
    private final int cHotspot = new JCheckBox().getPreferredSize().width; 
    private final Font mDeselectedFont = new Font(getFont().getName(), getFont().ITALIC, getFont().getSize());
    
    public CheckList() {
    }
    
    public CheckList(FilterListModel<T,F> aDataModel) {
        super(aDataModel);
        addMouseListener(new MouseButtonListener()); 
        registerKeyboardAction(new SpaceList(), KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), JComponent.WHEN_FOCUSED); 
    }

    /**
     * Call this after you set your renderers (if any)
     */
    public void addChecks() {
        setCellRenderer(new CheckListCellRenderer(getCellRenderer())); 
    }

// -----------------------------------------------------------------------------
// Testing/changing selection
// -----------------------------------------------------------------------------
    /**
     * Get selection of the current item.
     * Override to get selection of an item.  
     * @todo move to the model?
     */
    public abstract boolean isSelected(int aIdx);
    
    /**
     * Override to set selection of an item.  
     * @todo move to the model?
     */
    public abstract void setSelection(int aIdx, boolean aSelection);


    /**
     * A convenience method for toggling the selection of an item.
     */
    public void toggleSelection(int aIdx) {
        repaint(getCellBounds(aIdx, aIdx));
        setSelection(aIdx, !isSelected(aIdx));
    }

    /**
     * A convenience method for setting selection of the current item.
     * Does nothing if there is no current item.
     */
    public void setCurSelection(boolean aSelection) {
        int idx = getCurIdx();
        if (idx != -1) setSelection(idx, aSelection);
    }

    /**
     * A convenience method for toggling the selection of the current item.
     * Does nothing if there is no current item.
     */
    public void toggleCurSelection() {
        int idx = getCurIdx();
        if (idx != -1) toggleSelection(idx);
    }
    
// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------
    
    public Font deselectedFont() {
        return mDeselectedFont;
    }

    /**
     * Moves the cursor to the first selected item. 
     * If no item is selected, the cursor is not moved. If there are no items, 
     * nothing happens.
     */
    public void curToFirstSelected() {
        for (int i = 0; i < getModel().getSize(); i++) {
            if (isSelected(i)) {
                setSelectedIndex(i);
                ensureIndexIsVisible(i);
                return;
            }
	}   
    }
    
    
// -----------------------------------------------------------------------------
// Listeners
// -----------------------------------------------------------------------------
    
    /**
     * Toggles item's selection as a response to clicking its checkbox
     */
    class MouseButtonListener extends MouseAdapter {
                
        @Override public void mouseClicked(MouseEvent me){
            int index = locationToIndex(me.getPoint()); 
            
            // --- get out if no item or a non-checkbox part of an item was clicked ---
            if (index < 0) return;  // empty model
            Rectangle cellBounds = getCellBounds(index,index);
            if (!cellBounds.contains(me.getPoint()) || me.getX() > getCellBounds(index, index).x + cHotspot) return; 

            toggleSelection(index); 
        } 
    }
    
    /**
     * Toggles item's selection as a response to pressing space
     */
    class SpaceList implements ActionListener {
        public void actionPerformed(ActionEvent e){ 
            toggleSelection(getCurIdx()); 
        } 
    }

// -----------------------------------------------------------------------------
// Renderer
// -----------------------------------------------------------------------------
    
    // based on http://www.jroller.com/page/santhosh/20050611#jlist_with_checkboxes
    public class CheckListCellRenderer extends JPanel implements ListCellRenderer{ 
        private ListCellRenderer mDelegate; 
        private JCheckBox mCheckBox = new JCheckBox(); 

        public CheckListCellRenderer(ListCellRenderer aRenderer) { 
            mDelegate = aRenderer; 
            setLayout(new BorderLayout()); 
            setOpaque(false); 
            mCheckBox.setOpaque(false); 
        } 

        public Component getListCellRendererComponent(JList aList, Object value, int aIdx, boolean aIsSelected, boolean aCellHasFocus) { 
            Component renderer = mDelegate.getListCellRendererComponent(aList, value, aIdx, aIsSelected, aCellHasFocus); 
            boolean selected = isSelected(aIdx);
            mCheckBox.setSelected(selected);
            renderer.setFont( selected ? aList.getFont() : ((CheckList) aList).mDeselectedFont);
                    
            
            removeAll(); 
            add(mCheckBox, BorderLayout.WEST); 
            add(renderer, BorderLayout.CENTER); 
            return this; 
        } 
    }    
}
