package org.purl.jh.feat.navigator;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.swing.EventListModel;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringWriter;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbPreferences;
import org.purl.jh.util.gui.SimpleDocListener;

/**
 *
 * @author j
 */
public class ConfPanel extends javax.swing.JPanel {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ConfPanel.class);
    
    private EventList<FilterView> filters;
    private FilterView curFilter;
    
    
    private boolean guiReacts = true;
    
    private JButton okButton;

//    public List<Column> getColumns() {
//        return curFilter.getColumns();
//    }
    
    
    /**
     * Creates new form NavigatorConf
     */
    public ConfPanel(JButton okButton) {
        initComponents();

        this.okButton = okButton;
        okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) {
                //validate?
                loadSave();
            }
        });
        
        // --- select appropriate checkboxes when patter field is modified ---
        errorPatternFld.getDocument().addDocumentListener(new SimpleDocListener() {
            @Override public void textUpdated() {
                autoSet(edgeWithErrorCheck, true);
                validateInput();
            }
        });

        commentPatternFld.getDocument().addDocumentListener(new SimpleDocListener() {
            @Override public void textUpdated() {
                autoSet(propCommentsCheck, true);
                validateInput();
            }
        });

        formPatternFld.getDocument().addDocumentListener(new SimpleDocListener() {
            @Override public void textUpdated() {
                validateInput();
            }
        });

        ((XTable)columnsTable).attachButton(columnUpButton, columnDownButton, columnAddButton, columnRemoveButton);
    }

    
    private ListModel getFiltersListModel() {
        final DefaultListModel filters = new DefaultListModel();
        filters.addElement("Default");
        return filters;
    }
    
    
    public void setData(final EventList<FilterView> filters, final FilterView aCurFilter) {
        this.filters = filters;
        
        filtersList.setModel(new EventListModel(filters));      // relies on toString, todo improve
        filtersList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // allow changing filter only if the current filter is validated and thus can be saved
                if (curFilter != null && !validateInput()) {
                    filtersList.setSelectedValue(curFilter, true);
                    return;
                }
                
                loadSave();
            }
        });
        
        filtersList.setSelectedValue(aCurFilter, true);
        
        //curFilter = new ConfBean(aCurFilter);
    }

    
    public FilterView getData() {
        gui2filter();
        return curFilter;
    }
    

// =============================================================================    
// Gui <-> bean
// =============================================================================    
    
    private void loadSave() {
        if (curFilter != null) {
            gui2filter();
        }
        
        curFilter = (FilterView) filtersList.getSelectedValue();
        if (curFilter == null) {
            filtersList.setSelectedIndex(0);
        }
        else {
            filter2gui();
        }
    }

    
    private boolean validateInput()  {
        // todo the msg does not fit the space, make the relevant conntrol red
        String errMsg = null;
        
        String id = nameFld.getText();
        errMsg = validate(!"".equals(id), errMsg, "Filter name cannot be empty");
        errMsg = validate(isUnique(id, curFilter), errMsg, "Filter must have a unique name");

        errMsg = validate(
            validatePattern(commentPatternFld) && validatePattern(formPatternFld) && validatePattern(errorPatternFld),
            errMsg,
            "Pattern error");
        
        
        if (errMsg == null) {
            okButton.setEnabled(true);
            msgLabel.setText("");
            return true;
        }
        else {
            okButton.setEnabled(false);
            msgLabel.setText("Incorrect Input: " + errMsg);
            return false;
        }
    }
    
    private boolean isUnique(String id, FilterView fv) {
        for (FilterView a : filters) {
            if ( id.equals(a.getName()) && a != fv ) return false;
        }
        return true;
    }
    
    
    private String validate(boolean aTest, String aInMsg, String aErrMsg) {
        if (aTest) return aInMsg;
        
        return aInMsg == null ? aErrMsg : aInMsg + "; " + aErrMsg; 
    }
    
    
    private void gui2filter() {
        curFilter.setName(nameFld.getText());
        
        curFilter.getFilterSpec().setLayer0( layerL0Check.isSelected() );
        curFilter.getFilterSpec().setLayer1( layerL1Check.isSelected() );
        curFilter.getFilterSpec().setLayer2( layerL2Check.isSelected() );
        
        curFilter.getFilterSpec().setForms( typeFormsCheck.isSelected() );
        curFilter.getFilterSpec().setEdges( typeEdgesCheck.isSelected() );

        curFilter.getFilterSpec().setWithComments( propCommentsCheck.isSelected() );
        curFilter.getFilterSpec().setCommentPattern ( getPattern(commentPatternFld) );

        curFilter.getFilterSpec().setIncorrect( formIncorrectCheck.isSelected() );
        
        curFilter.getFilterSpec().setFormPattern( getPattern(formPatternFld) );
        curFilter.getFilterSpec().setChanging(            formChangingCheck.isSelected() );
        curFilter.getFilterSpec().setChangingImmediately( formChangingImmediateCheck.isSelected() );
        curFilter.getFilterSpec().setChanged(             formChangedCheck.isSelected() );
        curFilter.getFilterSpec().setChangedImmediately(  formChangedImmediateCheck.isSelected() );

        curFilter.getFilterSpec().setWithError( edgeWithErrorCheck.isSelected() );
        curFilter.getFilterSpec().setErrorPattern( getPattern(errorPatternFld) );
        
        curFilter.getFilterSpec().setEdgeLMin( (Integer) edgeLMinFld.getValue() );
        curFilter.getFilterSpec().setEdgeLMax( (Integer) edgeLMaxFld.getValue() );
        curFilter.getFilterSpec().setEdgeHMin( (Integer) edgeHMinFld.getValue() );
        curFilter.getFilterSpec().setEdgeHMax( (Integer) edgeHMaxFld.getValue() );
        
        curFilter.getFilterSpec().setWithLinks( edgeWithLinkCheck.isSelected() );
    }

    private void filter2gui() {
        guiReacts = false; 
        nameFld.setText(curFilter.getName());

        layerL0Check.setSelected(curFilter.getFilterSpec().isLayer0());
        layerL1Check.setSelected(curFilter.getFilterSpec().isLayer1());
        layerL2Check.setSelected(curFilter.getFilterSpec().isLayer2());

        typeFormsCheck.setSelected(curFilter.getFilterSpec().isForms());
        typeEdgesCheck.setSelected(curFilter.getFilterSpec().isEdges());

        propCommentsCheck.setSelected(curFilter.getFilterSpec().isWithComments());
        setPattern(commentPatternFld, curFilter.getFilterSpec().getCommentPattern());

        formIncorrectCheck.setSelected(curFilter.getFilterSpec().isIncorrect());

        setPattern(formPatternFld, curFilter.getFilterSpec().getFormPattern());
        formChangingCheck.setSelected(curFilter.getFilterSpec().isChanging());
        formChangingImmediateCheck.setSelected(curFilter.getFilterSpec().isChangingImmediately());
        formChangedCheck.setSelected(curFilter.getFilterSpec().isChanged());
        formChangedImmediateCheck.setSelected(curFilter.getFilterSpec().isChangedImmediately());

        edgeWithErrorCheck.setSelected(curFilter.getFilterSpec().isWithError());
        setPattern(errorPatternFld, curFilter.getFilterSpec().getErrorPattern());

        edgeLMinFld.setValue(curFilter.getFilterSpec().getEdgeLMin());
        edgeLMaxFld.setValue(curFilter.getFilterSpec().getEdgeLMax());
        edgeHMinFld.setValue(curFilter.getFilterSpec().getEdgeHMin());
        edgeHMaxFld.setValue(curFilter.getFilterSpec().getEdgeHMax());

        edgeWithLinkCheck.setSelected(curFilter.getFilterSpec().isWithLinks());
    
        ((XTable)columnsTable).setModel(curFilter.getColumns());
        jScrollPane3.setViewportView(columnsTable);
        
//        // save into settings
//        XStream xstream = new XStream( new StaxDriver());
//        xstream.autodetectAnnotations(true);
//        xstream.aliasType("printer", ColumnPrinter.class);
//        xstream.processAnnotations(FilterView.class);
//        System.out.println(org.purl.jh.feat.navigator.Util.prettyFormat(xstream.toXML(curFilter)));        
//        getPrefs().put(curFilter.getName(), xstream.toXML(curFilter));
        
        guiReacts = true; 
    }


    
    private boolean validatePattern(final JTextField aFld) {
        final String str = aFld.getText();
        
        try {
            Pattern.compile(str);
            aFld.setForeground(Color.black);        // todo default color
            return true;
        }
        catch(PatternSyntaxException e) {
            aFld.setForeground(Color.red);
            return false;
        }
    }

    private Pattern getPattern(final JTextField aFld) {
        final String str = aFld.getText();
        return str == null || ".*".equals(str) ? null : Pattern.compile(str);
    }
    
    private void setPattern(JTextField aFld, Pattern aPattern) {
        if (aPattern == null) {
            aFld.setText(".*");
        }
        else {
            aFld.setText(aPattern.pattern());
        }
    }
    
    
    private JTable createColumnsTable() {
        return new XTable();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelProfile = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        labelType = new javax.swing.JLabel();
        typeFormsCheck = new javax.swing.JCheckBox();
        typeEdgesCheck = new javax.swing.JCheckBox();
        labelLayers = new javax.swing.JLabel();
        layerL0Check = new javax.swing.JCheckBox();
        layerL1Check = new javax.swing.JCheckBox();
        layerL2Check = new javax.swing.JCheckBox();
        labelProps = new javax.swing.JLabel();
        propCommentsCheck = new javax.swing.JCheckBox();
        commentPatternFld = new javax.swing.JTextField();
        formIncorrectCheck = new javax.swing.JCheckBox();
        labelPropFormShape = new javax.swing.JLabel();
        formPatternFld = new javax.swing.JTextField();
        edgeWithErrorCheck = new javax.swing.JCheckBox();
        errorPatternFld = new javax.swing.JTextField();
        labelEdgeLMin = new javax.swing.JLabel();
        edgeLMinFld = new javax.swing.JSpinner();
        labelEdgeLMax = new javax.swing.JLabel();
        edgeLMaxFld = new javax.swing.JSpinner();
        labelEdgeHMin = new javax.swing.JLabel();
        edgeHMinFld = new javax.swing.JSpinner();
        labelEdgeHMax = new javax.swing.JLabel();
        edgeHMaxFld = new javax.swing.JSpinner();
        edgeWithLinkCheck = new javax.swing.JCheckBox();
        nameFld = new javax.swing.JTextField();
        newFilterButton = new javax.swing.JToggleButton();
        deleteFilterButton = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        filtersList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        columnsTable = createColumnsTable();
        columnUpButton = new javax.swing.JButton();
        columnDownButton = new javax.swing.JButton();
        columnAddButton = new javax.swing.JButton();
        columnRemoveButton = new javax.swing.JButton();
        msgLabel = new javax.swing.JLabel();
        formChangingCheck = new javax.swing.JCheckBox();
        formChangingImmediateCheck = new javax.swing.JCheckBox();
        formChangedCheck = new javax.swing.JCheckBox();
        formChangedImmediateCheck = new javax.swing.JCheckBox();

        labelProfile.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelProfile.text")); // NOI18N

        labelType.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelType.text")); // NOI18N

        typeFormsCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.typeFormsCheck.text")); // NOI18N

        typeEdgesCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.typeEdgesCheck.text")); // NOI18N
        typeEdgesCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeEdgesCheckActionPerformed(evt);
            }
        });

        labelLayers.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelLayers.text")); // NOI18N

        layerL0Check.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.layerL0Check.text")); // NOI18N

        layerL1Check.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.layerL1Check.text")); // NOI18N

        layerL2Check.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.layerL2Check.text")); // NOI18N

        labelProps.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelProps.text")); // NOI18N

        propCommentsCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.propCommentsCheck.text")); // NOI18N
        propCommentsCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propCommentsCheckActionPerformed(evt);
            }
        });

        commentPatternFld.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.commentPatternFld.text")); // NOI18N
        commentPatternFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentPatternFldActionPerformed(evt);
            }
        });

        formIncorrectCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formIncorrectCheck.text")); // NOI18N
        formIncorrectCheck.setEnabled(false);
        formIncorrectCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formIncorrectCheckActionPerformed(evt);
            }
        });

        labelPropFormShape.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelPropFormShape.text")); // NOI18N

        formPatternFld.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formPatternFld.text")); // NOI18N
        formPatternFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formPatternFldActionPerformed(evt);
            }
        });

        edgeWithErrorCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.edgeWithErrorCheck.text")); // NOI18N
        edgeWithErrorCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeWithErrorCheckActionPerformed(evt);
            }
        });

        errorPatternFld.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.errorPatternFld.text")); // NOI18N
        errorPatternFld.setEnabled(false);
        errorPatternFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorPatternFldActionPerformed(evt);
            }
        });

        labelEdgeLMin.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelEdgeLMin.text")); // NOI18N

        labelEdgeLMax.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelEdgeLMax.text")); // NOI18N

        labelEdgeHMin.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelEdgeHMin.text")); // NOI18N

        labelEdgeHMax.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.labelEdgeHMax.text")); // NOI18N

        edgeWithLinkCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.edgeWithLinkCheck.text")); // NOI18N
        edgeWithLinkCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeWithLinkCheckActionPerformed(evt);
            }
        });

        nameFld.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.nameFld.text")); // NOI18N
        nameFld.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameFldActionPerformed(evt);
            }
        });

        newFilterButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.newFilterButton.text")); // NOI18N
        newFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFilterButtonActionPerformed(evt);
            }
        });

        deleteFilterButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.deleteFilterButton.text")); // NOI18N
        deleteFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFilterButtonActionPerformed(evt);
            }
        });

        filtersList.setModel(getFiltersListModel());
        jScrollPane1.setViewportView(filtersList);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.jLabel1.text")); // NOI18N

        jLabel2.setForeground(javax.swing.UIManager.getDefaults().getColor("ComboBox.buttonDarkShadow"));
        jLabel2.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.jLabel2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.jLabel3.text")); // NOI18N

        jScrollPane3.setViewportView(columnsTable);

        columnUpButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.columnUpButton.text")); // NOI18N

        columnDownButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.columnDownButton.text")); // NOI18N

        columnAddButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.columnAddButton.text")); // NOI18N

        columnRemoveButton.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.columnRemoveButton.text")); // NOI18N

        msgLabel.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.msgLabel.text")); // NOI18N

        formChangingCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formChangingCheck.text")); // NOI18N

        formChangingImmediateCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formChangingImmediateCheck.text")); // NOI18N

        formChangedCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formChangedCheck.text")); // NOI18N
        formChangedCheck.setEnabled(false);

        formChangedImmediateCheck.setText(org.openide.util.NbBundle.getMessage(ConfPanel.class, "ConfPanel.formChangedImmediateCheck.text")); // NOI18N
        formChangedImmediateCheck.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(282, 282, 282)
                        .addComponent(labelProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nameFld, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addGap(4, 4, 4))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newFilterButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteFilterButton)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(columnUpButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(columnDownButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(columnAddButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(columnRemoveButton))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(layerL1Check, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(layerL0Check, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(labelLayers, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(typeFormsCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(typeEdgesCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(layerL2Check, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelProps, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(propCommentsCheck)
                                            .addComponent(edgeWithErrorCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(labelPropFormShape, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(40, 40, 40)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(formPatternFld)
                                            .addComponent(errorPatternFld)
                                            .addComponent(commentPatternFld)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(formIncorrectCheck)
                                            .addComponent(edgeWithLinkCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(labelEdgeLMin, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(edgeLMinFld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(labelEdgeLMax, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(formChangingCheck))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(formChangingImmediateCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(42, 42, 42)
                                                        .addComponent(formChangedCheck)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(formChangedImmediateCheck))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(edgeLMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(labelEdgeHMin, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(edgeHMinFld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(labelEdgeHMax, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(edgeHMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(msgLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelLayers, labelType, layerL0Check, layerL1Check, layerL2Check, typeEdgesCheck, typeFormsCheck});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {formIncorrectCheck, propCommentsCheck});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deleteFilterButton, newFilterButton});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {columnAddButton, columnDownButton, columnRemoveButton, columnUpButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelProfile)
                    .addComponent(nameFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(labelType)
                                    .addComponent(labelProps))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(typeFormsCheck)
                                    .addComponent(propCommentsCheck)
                                    .addComponent(commentPatternFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(typeEdgesCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLayers)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(layerL0Check)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(layerL1Check)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(layerL2Check))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(formIncorrectCheck)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(formPatternFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labelPropFormShape))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(formChangingCheck)
                                        .addComponent(formChangingImmediateCheck)
                                        .addComponent(formChangedCheck)
                                        .addComponent(formChangedImmediateCheck))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(edgeWithErrorCheck)
                                            .addComponent(errorPatternFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(edgeLMinFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelEdgeLMax)
                                            .addComponent(edgeLMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelEdgeHMin)
                                            .addComponent(edgeHMinFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelEdgeHMax)
                                            .addComponent(edgeHMaxFld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(labelEdgeLMin))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addComponent(edgeWithLinkCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(msgLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addGap(1, 1, 1)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(columnUpButton)
                            .addComponent(columnDownButton)
                            .addComponent(columnAddButton)
                            .addComponent(columnRemoveButton))
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newFilterButton)
                            .addComponent(deleteFilterButton))
                        .addGap(6, 6, 6))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void autoSet(final JCheckBox aCheck, boolean aVal) {
        if (guiReacts) aCheck.setSelected(aVal);
    }
    
    private void propCommentsCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propCommentsCheckActionPerformed
    }//GEN-LAST:event_propCommentsCheckActionPerformed

    private void typeEdgesCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeEdgesCheckActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_typeEdgesCheckActionPerformed

    private void edgeWithLinkCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeWithLinkCheckActionPerformed
    }//GEN-LAST:event_edgeWithLinkCheckActionPerformed

    private void errorPatternFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorPatternFldActionPerformed
        autoSet(edgeWithErrorCheck, true);
    }//GEN-LAST:event_errorPatternFldActionPerformed

    private void formIncorrectCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formIncorrectCheckActionPerformed
    }//GEN-LAST:event_formIncorrectCheckActionPerformed

    private void formPatternFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formPatternFldActionPerformed
    }//GEN-LAST:event_formPatternFldActionPerformed

    private void edgeWithErrorCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeWithErrorCheckActionPerformed
    }//GEN-LAST:event_edgeWithErrorCheckActionPerformed

    private void commentPatternFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commentPatternFldActionPerformed
        autoSet(propCommentsCheck, true);
    }//GEN-LAST:event_commentPatternFldActionPerformed

    private void newFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFilterButtonActionPerformed
        FilterView filter = curFilter.copy("New Filter");
        filters.add(filter);
        filtersList.setSelectedValue(filter, true);
    }//GEN-LAST:event_newFilterButtonActionPerformed

    private void deleteFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFilterButtonActionPerformed
        if (filters.size() <= 1) return; // should not happen
        final int idx = filtersList.getSelectedIndex();

        if (idx == -1) return; 
            
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation("Do you really want to delete the filter?", NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) return;

        filtersList.setSelectedIndex(idx == 0 ? 1 : idx - 1);  // select a new filter
        
        filters.remove(idx);    // todo notify list?
    }//GEN-LAST:event_deleteFilterButtonActionPerformed

    private void nameFldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameFldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nameFldActionPerformed
    

    

    private Preferences getPrefs() {
        return NbPreferences.forModule(getClass());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton columnAddButton;
    private javax.swing.JButton columnDownButton;
    private javax.swing.JButton columnRemoveButton;
    private javax.swing.JButton columnUpButton;
    private javax.swing.JTable columnsTable;
    private javax.swing.JTextField commentPatternFld;
    private javax.swing.JToggleButton deleteFilterButton;
    private javax.swing.JSpinner edgeHMaxFld;
    private javax.swing.JSpinner edgeHMinFld;
    private javax.swing.JSpinner edgeLMaxFld;
    private javax.swing.JSpinner edgeLMinFld;
    private javax.swing.JCheckBox edgeWithErrorCheck;
    private javax.swing.JCheckBox edgeWithLinkCheck;
    private javax.swing.JTextField errorPatternFld;
    private javax.swing.JList filtersList;
    private javax.swing.JCheckBox formChangedCheck;
    private javax.swing.JCheckBox formChangedImmediateCheck;
    private javax.swing.JCheckBox formChangingCheck;
    private javax.swing.JCheckBox formChangingImmediateCheck;
    private javax.swing.JCheckBox formIncorrectCheck;
    private javax.swing.JTextField formPatternFld;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel labelEdgeHMax;
    private javax.swing.JLabel labelEdgeHMin;
    private javax.swing.JLabel labelEdgeLMax;
    private javax.swing.JLabel labelEdgeLMin;
    private javax.swing.JLabel labelLayers;
    private javax.swing.JLabel labelProfile;
    private javax.swing.JLabel labelPropFormShape;
    private javax.swing.JLabel labelProps;
    private javax.swing.JLabel labelType;
    private javax.swing.JCheckBox layerL0Check;
    private javax.swing.JCheckBox layerL1Check;
    private javax.swing.JCheckBox layerL2Check;
    private javax.swing.JLabel msgLabel;
    private javax.swing.JTextField nameFld;
    private javax.swing.JToggleButton newFilterButton;
    private javax.swing.JCheckBox propCommentsCheck;
    private javax.swing.JCheckBox typeEdgesCheck;
    private javax.swing.JCheckBox typeFormsCheck;
    // End of variables declaration//GEN-END:variables

    
}
