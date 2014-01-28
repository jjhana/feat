//package org.purl.jh.util.gui;
//
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import javax.swing.Action;
//import javax.swing.JTextField;
//import javax.swing.KeyStroke;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import org.purl.jh.util.col.pred.StringFilter;
//
//
///**
// *
// * @author Jirka
// */
//public class FilterTextField extends JTextField implements DocumentListener {
//    private final ListAutoCompleter mItemHistory;  
//    private boolean mFilterCont = false;    // change filter each time the text changes, or only after (Ctrl)Enter?
//    
//    private StringFilter<?> mFilter;
//    
//    /** Creates a new instance of FilterTextField */
//    public FilterTextField() {
//        getDocument().addDocumentListener(this);    // listen to changes
//
//        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "mItemComboEnter");
//        getActionMap().put("mItemComboEnter", cFinishedAct);
//
//        mItemHistory = new ListAutoCompleter(this);
//    }
//
//    public void setOptions(Options aOptions, String aFilterContKey, String aMaxHistoryKey) {
//        mFilterCont = aOptions.getBool(aFilterContKey);
//        mItemHistory.setOptions(aOptions);
//        mItemHistory.setMax(aOptions.getInt(aMaxHistoryKey,5));
//    }
//    
//// -----------------------------------------------------------------------------
//// Attributes
//// -----------------------------------------------------------------------------    
//
//    public void setFilter(StringFilter<?> aFilter) {
//        mFilter = aFilter;
//    }
//
//    public void setFilterCont(boolean aFilterCont) {
//        mFilterCont = aFilterCont;
//    }
//    
//    public ListAutoCompleter getItemHistory() {
//        return mItemHistory;
//    }
//    
//
//// -----------------------------------------------------------------------------
//// DocumentListener
//// -----------------------------------------------------------------------------    
//
//    public void insertUpdate(DocumentEvent e) {updated();}
//    public void removeUpdate(DocumentEvent e) {updated();}
//    public void changedUpdate(DocumentEvent e){}
//
//// -----------------------------------------------------------------------------
//// 
//// -----------------------------------------------------------------------------    
//
//    protected void updated() {
//        //System.out.printf("FilterTextField.update (mFilterCont=%s)\n", mFilterCont);
//        if (mFilterCont) fireFilterUpdated();
//    }
//
//    protected void finished() {
//        mItemHistory.addCurrent();
//        mItemHistory.hidePopup();
//        fireFilterUpdated();
//    }
//    
//    Action cFinishedAct = new SAction() {
//        public void a() {finished();}
//    };
//    
//// -----------------------------------------------------------------------------
//// 
//// -----------------------------------------------------------------------------    
//
//    protected void fireFilterUpdated() {
//        System.out.println("FTField.fireFilterUpdated");
//        mFilter.setString(getText());       // notifies listeners @todo notify listeners that there was an error?
//    }
//    
//}
