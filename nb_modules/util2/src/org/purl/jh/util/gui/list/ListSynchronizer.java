package org.purl.jh.util.gui.list;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Synchronizes scrolling and selection of two SingleSelectionLists.
 * To obtain the usually desired results the two lists should have the same height.
 *
 * @author Jirka
 */
public class ListSynchronizer implements AdjustmentListener, ListSelectionListener{
    private SingleSelectionList mList1 = null;
    private SingleSelectionList mList2 = null;
    private JScrollBar mSB1, mSB2;

    /** 
     * Creates a new instance of ListSynchronizer 
     * The lists are required to be in scroll panes (throws Cast exception if not)
     */
    public ListSynchronizer(SingleSelectionList aList1, SingleSelectionList aList2) {
        setList1(aList1);
        setList2(aList2);
    }    

    public void setList1(SingleSelectionList aList) {
        if (mList1 != null) {
            mList1.removeListSelectionListener(this);
            mSB1.removeAdjustmentListener(this);  
        }
    
        mList1 = aList; 
        mSB1 = ((JScrollPane) mList1.getParent().getParent()).getVerticalScrollBar();
        
        mList1.addListSelectionListener(this);
        mSB1.addAdjustmentListener(this);  
   } 

    
    public void setList2(SingleSelectionList aList) {
        if (mList2 != null) {
            mList2.removeListSelectionListener(this);
            mSB2.removeAdjustmentListener(this);  
        }
    
        mList2 = aList;
        mSB2 = ((JScrollPane) mList2.getParent().getParent()).getVerticalScrollBar();
        
        mList2.addListSelectionListener(this);
        mSB2.addAdjustmentListener(this);  
   } 
    
    /**  
    * Synchronizes vertical scrolling in the form and LT lists
    * When one vertical scroll bar moves, the other is adjusted.
    *
    * @param e the AdjustmentEvent that ocurred (meaning that one of the scroll  
    *        bars position has changed.  
    * @todo isn't it bouncing there and back?
    */  
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource() == mSB1) 
            mSB2.setValue(e.getValue());
        else if (e.getSource() == mSB2) 
            mSB1.setValue(e.getValue());
    }
    
    /**  
    * Synchronizes selection in the form and LT lists
    * When selection in one changes, the selection in the other is adjusted.
    *
    * @param e the ListSelectionEvent that ocurred 
    * @todo isn't it bouncing there and back?
    */  
    public void valueChanged(ListSelectionEvent e) { 
        if (e.getSource() == mList1) 
            mList2.setSelectedIndex(mList1.getCurIdx());
        else if (e.getSource() == mList2) 
            mList1.setSelectedIndex(mList2.getCurIdx());
    }
    
}
