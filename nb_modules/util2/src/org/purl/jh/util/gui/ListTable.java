package org.purl.jh.util.gui;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import static java.util.Arrays.asList;


/**
 *
 * @author jirka
 */
public class ListTable<T> extends JTable implements ItemSelectable {
    public static interface Printer<T> {
        List<String> print(T aObj);
    }

    // todo move to utils
    public static ListTable<Map.Entry<String,String>> createMapTable(final Map<String,String> aProperties, String aPropertyLabel, String aValueLable) {
        final ListTable.Printer<Map.Entry<String,String>> printer = new ListTable.Printer<Map.Entry<String,String>>() {
            @Override
            public List<String> print(final Map.Entry<String,String> aEntry) {
                return asList(aEntry.getKey(), aEntry.getValue());
            }
        };

        return new ListTable<Map.Entry<String,String>>(
                asList(aPropertyLabel, aValueLable),
                asList(false, false),
                printer, new ArrayList<Map.Entry<String,String>>(aProperties.entrySet()) );
    }






    /** todo Should be in the model */
    private final List<T> data;

    private int curIdx;

    public class  ListTableModel extends AbstractTableModel {
        final List<String> colNames;
        final List<Boolean> editable;
        final ListTable.Printer<T> printer;
        final List<T> data;

        public ListTableModel(final List<String> aColNames, final List<Boolean> aEditable, final ListTable.Printer<T> aPrinter, final List<T> aData) {
            colNames = aColNames;
            editable = aEditable;
            printer = aPrinter;
            data = aData;
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return colNames.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return printer.print(data.get(rowIndex)).get(columnIndex);      // very ineffective
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return editable.get(columnIndex);
        }
        
    }


    /** Todo create a builder */
    public ListTable(final List<String> aColNames, final List<Boolean> aEditable, final ListTable.Printer<T> aPrinter, final List<T> aData) {
        data = aData;
        curIdx = -1;

//        final Vector<Vector<Object>> vector = new Vector<Vector<Object>>();
//        for (T o : aData) {
//            Vector<Object> row = new Vector<Object>();
//            vector.add(row);
//            for (String str : aPrinter.print(o)) {
//                row.add(str);
//            }
//        }
//
//        final Vector<String> colNames = new Vector<String>(aColNames);
//
//        setModel(new javax.swing.table.DefaultTableModel(vector, colNames) {
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return aEditable.get(columnIndex);
//            }
//        });

        setModel(new ListTableModel(aColNames, aEditable, aPrinter, aData));
        getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);

        final SelectionListener listener = new SelectionListener();
        getSelectionModel().addListSelectionListener(listener);
        //tagsetsTbl.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    }

    public int getCurIdx() {
        return curIdx;
    }

    public T getCurItem() {
        return curIdx != -1 ? data.get(curIdx) : null;
    }

    public class SelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) { // If cell selection is enabled, both row and column change events are fired
            if (curIdx != -1) {
                fireItemStateChanged(new ItemEvent(ListTable.this, ItemEvent.ITEM_STATE_CHANGED, getCurItem(), ItemEvent.DESELECTED));
            }

            curIdx = getSelectionModel().getMinSelectionIndex();

            if (curIdx != -1) {
                fireItemStateChanged(new ItemEvent(ListTable.this, ItemEvent.ITEM_STATE_CHANGED, getCurItem(), ItemEvent.SELECTED));
            }
        }
    }

    @Override
    public void addItemListener(ItemListener listener) {
        listenerList.add(ItemListener.class, listener);
    }

    @Override
    public void removeItemListener(ItemListener listener) {
        listenerList.remove(ItemListener.class, listener);
    }


    private final Object[] selectedObjs = new Object[1];

    @Override
    public Object[] getSelectedObjects() {
        selectedObjs[0] = getCurItem();
        return selectedObjs;
    }

    protected void fireItemStateChanged(final ItemEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length-2; i>=0; i-=2 ) {
            if ( listeners[i]==ItemListener.class ) {
                ((ItemListener)listeners[i+1]).itemStateChanged(e);
            }
        }
    }


}
