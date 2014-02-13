//package org.purl.jh.util.gui;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.JTextComponent;
//
//
//
///**
// *
// * @author Jiri
// *
// * http://www.jroller.com/page/santhosh/20050620#file_path_autocompletion:
// * If you have noticed, it tries to mimic Run dialog of Microsoft windows. As you start typing the file path, it helps you with a popup. You can use up/down arrow to select an item in list and press enter, it gets appended to text field.
// *
// * AutoCompleter does all the dirty job of showing popup, updating popup when required,
// * listening for up/down arrow keys and enter key etc...
// *
// * FileAutoCompleter extends AutoCompleter and tells how to populate the list in
// * popup depending on the context. Having two classes, gives lot of extensibility.
// * Let us thing, in future I can extend AutoCompleter and create DBTableAutoCompleter
// * which lets you autocomplete database table name without typing its complete name.
// *
// */
//public class ListAutoCompleter extends AutoCompleter {
//    private List<String> mCompletionList;
//    private boolean mIgnoreCase = true;
//    private int mHistoryLimit = 5;
//    Options mOptions;
//    String mHistoryOptionsKey;
//    
//    public ListAutoCompleter(JTextComponent aComp){
//        super(aComp);
//        mCompletionList = new ArrayList<String>();
//    }
//
//    // @todo does it have to be List<String> not just List<T> using toString() ??
//    public ListAutoCompleter(JTextComponent aComp, boolean aIgnoreCase, int aHistoryLimit, List<String> aCompletionList){
//        super(aComp);
//        mIgnoreCase = aIgnoreCase;
//        mCompletionList = aCompletionList;
//        mHistoryLimit = aHistoryLimit;
//    }
//    
//    public ListAutoCompleter(JTextComponent aComp, boolean aIgnoreCase, int aHistoryLimit, String ... completionList){
//        this(aComp, aIgnoreCase, aHistoryLimit, new java.util.ArrayList<String>(Arrays.asList(completionList)) );
//    }
//    
//    public ListAutoCompleter(JTextComponent aComp, boolean aIgnoreCase){
//        super(aComp);
//        mIgnoreCase = aIgnoreCase;
//        
//
//    }
//
//    public void setOptions(Options aOptions) {
//        mOptions = aOptions;
//    }
//    
//    public void setOptions(Options aOptions, String aHistoryKey, String aMaxKey) {
//        mOptions = aOptions;
//        setHistory(aHistoryKey);
//        mHistoryLimit = mOptions.getInt(aMaxKey, 5);
//    }
//    
//    public void setMax(int aMax) {
//        mHistoryLimit = aMax;
//    }
//    
//    public List<String> getHistory() {
//        return mCompletionList;
//    }
//
//    public void setHistory(List<String> aHistory) {
//        mCompletionList = aHistory;
//    }
//    
//    public void setHistory(String aKey) {
//        mCompletionList = mOptions.getStrings(aKey);
//        mHistoryOptionsKey = aKey;
//    }
//    
//    
//    // update classes model depending on the data in textfield
//    protected boolean updateListData() {
//        String curStr = mTextComp.getText();
//        int curLen = curStr.length();
//        
//        List<String> possibleStrings = new ArrayList<String>();
//        
//        for (String str : mCompletionList) {
//            if ( curLen <= str.length() && curStr.regionMatches(mIgnoreCase, 0, str, 0, curLen) )
//                possibleStrings.add(str);
//        }
//        
//        list.setListData(possibleStrings.toArray());
//        return true;
//    }
//    
//    // user has selected some item in the classes. update textfield accordingly...
//    protected void acceptedListItem(String aSelected){
//        if(aSelected==null) return;
//        
//        int prefixLen = mTextComp.getDocument().getLength();
//        
//        try {
//            mTextComp.getDocument().insertString(mTextComp.getCaretPosition(), aSelected.substring(prefixLen), null);
//        } catch(BadLocationException e){
//            e.printStackTrace();
//        }
//        
//        popup.setVisible(false);
//    }
//
//    /**
//     * Add current item into the history, as the first item. If it was already present, it is just moved
//     * at the begining. If the history exceeds its size limit, the oldest item is deleted.
//     */
//    public void addCurrent() {
//        String item = mTextComp.getText();
//        if (item == null || item.equals("")) return;
//        mCompletionList.remove(item);
//        mCompletionList.add(0, item);
//        
//        if (mCompletionList.size() > mHistoryLimit) 
//            mCompletionList.remove(mHistoryLimit);  // remove last
//        
//        if (mOptions != null)
//            mOptions.updateList(mHistoryOptionsKey, item, mHistoryLimit);
//    }
//}