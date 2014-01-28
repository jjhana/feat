package org.purl.jh.feat.navigator;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author j
 */
public class XTable extends JTable {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(XTable.class);
    
    /** 
     * The column specification wrapped as an event list, should be in the mdoel, but ...
     * 
     */ 
    private EventList<Column> objs;

    private JButton upButton; 
    private JButton downButton; 
    private JButton addButton; 
    private JButton delButton;
    private ActionListener a, b, c, d;
    
    public XTable() {
    }

    /**
     * Note: buttons must be attached before the model is set
     * @param data 
     */
    public void setModel(final List<Column> data) {
        log.info("setModel %d", data.size());
        if (objs != null) {
            objs.removeListEventListener(listEventListener);
        }
        
        objs = new BasicEventList<>(data);      
        super.setModel(createColumnTblModel());
        objs.addListEventListener(listEventListener);

        updateButtonState();
        log.info("setModel done");
    }
    
    public void attachButton(final JButton upButton, final JButton downButton, final JButton addButton, final JButton delButton) {
        this.upButton = upButton;
        this.downButton = downButton;
        this.addButton = addButton;
        this.delButton = delButton;
        
        upButton.addActionListener(a = new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        delButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delButtonActionPerformed(evt);
            }
        });
        
        getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                updateButtonState();
            }
        });

        // todo disable all of them
        updateButtonState();
    }
    
    public void detachButtons() {
        downButton.removeActionListener(a);
        upButton.removeActionListener(b);
        addButton.removeActionListener(c);
        delButton.removeActionListener(d);
    }
    
    
    private final ListEventListener listEventListener = new ListEventListener<Column>() {
        @Override
        public void listChanged(ListEvent<Column> listChanges) {
            updateButtonState();
        }            
    };

    
    private void updateButtonState() {
        if (objs == null) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
            addButton.setEnabled(false); 
            delButton.setEnabled(false);
        }
        else {
            final int idx = getSelectedRow();
            upButton.setEnabled(idx > 0);
            downButton.setEnabled(idx != -1 && idx + 1 < objs.size());
            addButton.setEnabled(true); 
            delButton.setEnabled(idx != -1);
        }
    }
    
    public TableModel createColumnTblModel() {
        final TableFormat<Column> tblFormat = new TableFormat<Column>() {
            @Override
            public int getColumnCount() {
                return 4;
            }

            @Override
            public String getColumnName(int colIdx) {
                switch (colIdx) {
                    case 0: return "Title";
                    case 1: return "Use in filtering";
                    case 2: return "Form printer";
                    default:return "Edge printer";
                }
            }

            @Override
            public Object getColumnValue(Column col, int colIdx) {
                switch (colIdx) {
                    case 0: return col.getTitle();
                    case 1: return col.isFiltered();
                    case 2: return col.getFormPrinter();
                    default:return col.getEdgePrinter();
                }
            }
        };
                
        return new EventTableModel<Column>(objs, tblFormat) {
            @Override
            public Class<?> getColumnClass(int colIdx) {
                switch (colIdx) {
                    case 0: return String.class;
                    case 1: return Boolean.class;
                    case 2: return ColumnPrinter.class;
                    default:return ColumnPrinter.class;
                }
            }
            
            @Override
            public Object getValueAt(int rowIdx, int colIdx) {
                final Column col = objs.get(rowIdx);
                
                switch (colIdx) {
                    case 0: return col.getTitle();
                    case 1: return col.isFiltered();
                    case 2: return col.getFormPrinter();
                    default:return col.getEdgePrinter();
                }
            }

            @Override
            public boolean isCellEditable(int rowIndex, int colIdx) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIdx, int colIdx) {
                final Column col = objs.get(rowIdx);
                
                switch (colIdx) {
                    case 0: col.setTitle( (String) aValue ); return;
                    case 1: col.setFiltered( (Boolean)aValue ); return;
                    case 2:  col.setFormPrinter( (ColumnPrinter) aValue ); return; // todod
                    default: col.setEdgePrinter( (ColumnPrinter) aValue ); return; // todod
                }
            }
        };
    }

    
    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        final int modelColumn = convertColumnIndexToModel( column );

        switch(modelColumn) {
            case 2: return formPrinterCombo;
            case 3: return edgePrinterCombo;
            default:
            return super.getCellEditor(row, column);
        }
    }
    
    private static final DefaultCellEditor formPrinterCombo = new FormPrinterEditor();
    private static final DefaultCellEditor edgePrinterCombo = new EdgePrinterEditor();
    
    /** 
     * Editor for changing column printer
     * todo this relies on toString, use some description, add tooltip
     */
    private static class FormPrinterEditor extends DefaultCellEditor {
        public FormPrinterEditor() {
            super( new JComboBox(ColumnPrinters.getDef().getPrintersNameSorted(FForm.class).toArray(new ColumnPrinter[]{})) );
        }
    }
    
    private static class EdgePrinterEditor extends DefaultCellEditor {
        
        public EdgePrinterEditor() {
            super( new JComboBox(ColumnPrinters.getDef().getPrintersNameSorted(Edge.class).toArray(new ColumnPrinter[]{})) );
        }
    }
    

    // todo use event list, and listen to changes modifying the table and button status
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        objs.add(new Column("New Column", true, "", ColumnPrinters.cForm2token, ColumnPrinters.cEdge2str ));
    }                                               

    private void delButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        final int colIdx = getSelectedRow();
        if (colIdx == -1) return;
            
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Do you really want to delete the selected navigator column?", NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) return;
        
        objs.remove(colIdx);
    }                                                  

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {                                               
        final int colIdx = getSelectedRow();
        if (!isSwappable(objs, colIdx, colIdx-1)) return;
        swapElement(objs, colIdx, colIdx-1);
        getSelectionModel().setSelectionInterval(colIdx-1, colIdx-1);
    }                                              

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        final int colIdx = getSelectedRow();
        if (!isSwappable(objs, colIdx, colIdx+1)) return;
        swapElement(objs, colIdx, colIdx+1);
        getSelectionModel().setSelectionInterval(colIdx+1, colIdx+1);
    }                                                
    

    
    private static <T> boolean isSwappable(final List<T> aList, final int aIdx1, final int aIdx2) {
        return 
                aIdx1 != -1 &&
                aIdx2 != -1 &&
                aIdx1 != aIdx2 &&
                aIdx1 < aList.size() &&
                aIdx2 < aList.size();
    }
    
    private static <T> void swapElement(List<T> aList, int aIdx1, int aIdx2) {
        T a = aList.get(aIdx1);
        aList.set(aIdx1, aList.get(aIdx2));
        aList.set(aIdx2, a);
    }
    
}
