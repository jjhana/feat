package org.purl.jh.feat.navigator;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.EventComboBoxModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.purl.jh.feat.layered.WidgetNode;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.location.AcceptingLocation;
import org.purl.jh.pml.location.Location;
import org.purl.net.jh.nbutil.XDataObject;
import org.purl.jh.feat.layered.VModel;
import org.purl.jh.nbpml.OpenAtCookie;


/**
 * todo messy in development
 * todo make empty once another or no TC is in focus
 * todo support diff - currently there is one VModel
 * todo make a node public, so that properties can be displayed
 * @author jirka
 */
public final class NavigatorTopComponent extends JPanel implements LookupListener, NavigatorPanel, ExplorerManager.Provider  {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(NavigatorTopComponent.class);
    
    private final Lookup lookup;
    private InstanceContent lookupContent = new InstanceContent();
    private Lookup.Result<VModel> result = null; // todo
    // todo generalize
    private VModel pseudoModel;

    private EventList<FilterView> filters;
    private FilterView curFilter;
    private TextComponentMatcherEditor<Element> matcherEditor;
    
    private FilterList<Element> filteredItems;
        
    /** 
     * A node standing for any selectable widget in the graph. 
     * It is in the lookup so that other components can access/observe the 
     * current object.
     */
    private final WidgetNode widgetNode = new WidgetNode();
    /** Manager exposing the currently selected object (as the widgetNode) */
    private final ExplorerManager mngr = new ExplorerManager();
    
    public NavigatorTopComponent() {
        initComponents();
        
        lookup = new AbstractLookup(lookupContent);
        setName(NbBundle.getMessage(NavigatorTopComponent.class, "CTL_NavigatorTopComponent"));
        setToolTipText(NbBundle.getMessage(NavigatorTopComponent.class, "HINT_NavigatorTopComponent"));

        loadFilters();
        
        filtersList.setModel(new EventComboBoxModel(filters));      // relies on to string, todo improve
        filtersList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                curFilter = (FilterView) filtersList.getSelectedItem();  
                updateNavigatorModel();
            }
        });
        // todo add reaction
        
        itemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                selected(false);
            }
        });
        
        itemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected(e.getClickCount() == 2);
            }
            
        });

        readMatcherGui();
        filtersList.setSelectedItem(curFilter);
    }
    
    
    @Override
    public ExplorerManager getExplorerManager() {
        return mngr;
    }
    
    
    /**
     * An item in the navigator was selected, show it in a TC.
     * 
     * @param aFocus should the item in TC be focused or just selected
     */
    private void selected(boolean aFocus) {
        // todo handle selection properly
        if (itemList.getSelectedRow() == -1 || (itemList.getRowCount() <= itemList.getSelectedRow()) ) return;
        
        final Element element = filteredItems.get(itemList.getSelectedRow()); // ((Element)itemList.getSelectedValue()).location();
        final Location loc = element.location(); // ((Element)itemList.getSelectedValue()).location();

        
        log.info("displayProperties %s, element");
        widgetNode.setProp(element); //, pseudoModel);
        mngr.setRootContext(widgetNode);
        try {
            mngr.setSelectedNodes(new Node[]{widgetNode});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
        lookupContent.remove(widgetNode);
        lookupContent.add(widgetNode);  /// todo temporary

        openAndFocusElement(loc, aFocus); 
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        modeGroup = new javax.swing.ButtonGroup();
        scrollpane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        filtersList = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        confButton = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        filterBox = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        exactModeButton = new javax.swing.JToggleButton();
        prefixModeButton = new javax.swing.JToggleButton();
        containsModeButton = new javax.swing.JToggleButton();
        regexModeButton = new javax.swing.JToggleButton();
        ignorediaButton = new javax.swing.JToggleButton();
        refreshButton = new javax.swing.JButton();

        itemList.setModel(createTableMode());
        scrollpane.setViewportView(itemList);

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        filtersList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jToolBar1.add(filtersList);

        jSeparator2.setForeground(new java.awt.Color(240, 240, 240));
        jSeparator2.setSeparatorSize(new java.awt.Dimension(5, 0));
        jToolBar1.add(jSeparator2);

        org.openide.awt.Mnemonics.setLocalizedText(confButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.confButton.text")); // NOI18N
        confButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.confButton.toolTipText")); // NOI18N
        confButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(confButton);

        jToolBar2.setBorder(null);
        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        filterBox.setText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.filterBox.text_1")); // NOI18N
        filterBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBoxActionPerformed(evt);
            }
        });
        jToolBar2.add(filterBox);

        jSeparator1.setForeground(new java.awt.Color(240, 240, 240));
        jSeparator1.setSeparatorSize(new java.awt.Dimension(5, 0));
        jToolBar2.add(jSeparator1);

        modeGroup.add(exactModeButton);
        org.openide.awt.Mnemonics.setLocalizedText(exactModeButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.exactModeButton.text")); // NOI18N
        exactModeButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.exactModeButton.toolTipText")); // NOI18N
        exactModeButton.setFocusable(false);
        exactModeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exactModeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exactModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exactModeButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(exactModeButton);

        modeGroup.add(prefixModeButton);
        org.openide.awt.Mnemonics.setLocalizedText(prefixModeButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.prefixModeButton.text")); // NOI18N
        prefixModeButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.prefixModeButton.toolTipText")); // NOI18N
        prefixModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefixModeButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(prefixModeButton);

        modeGroup.add(containsModeButton);
        org.openide.awt.Mnemonics.setLocalizedText(containsModeButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.containsModeButton.text")); // NOI18N
        containsModeButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.containsModeButton.toolTipText")); // NOI18N
        containsModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                containsModeButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(containsModeButton);

        org.openide.awt.Mnemonics.setLocalizedText(regexModeButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.regexModeButton.text")); // NOI18N
        regexModeButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.regexModeButton.toolTipText")); // NOI18N
        regexModeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regexModeButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(regexModeButton);

        org.openide.awt.Mnemonics.setLocalizedText(ignorediaButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.ignorediaButton.text")); // NOI18N
        ignorediaButton.setToolTipText(org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.ignorediaButton.toolTipText")); // NOI18N
        ignorediaButton.setFocusable(false);
        ignorediaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ignorediaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ignorediaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignorediaButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(ignorediaButton);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/purl/jh/feat/navigator/icons/arrow_refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(refreshButton, org.openide.util.NbBundle.getMessage(NavigatorTopComponent.class, "NavigatorTopComponent.refreshButton.text")); // NOI18N
        refreshButton.setFocusable(false);
        refreshButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        refreshButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(refreshButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void confButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confButtonActionPerformed
        final JButton ok = new JButton("OK");
        final JButton cancel = new JButton("Cancel");

        final ConfPanel p = new ConfPanel(ok);
        p.setData(filters, curFilter);
        
        DialogDescriptor dd = new DialogDescriptor(p, "Navigator Filters", true, null);
        dd.setOptions(new Object[]{ok, cancel});
        Object o = DialogDisplayer.getDefault().notify(dd);

        if (o == ok) {
            curFilter = p.getData();  // todo select
            filtersList.setSelectedItem(curFilter);
            updateNavigatorModel();
            saveFilters();
        }

    }//GEN-LAST:event_confButtonActionPerformed

    private void prefixModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefixModeButtonActionPerformed
        setOptMatcherMode(TextMatcherEditor.STARTS_WITH);
    }//GEN-LAST:event_prefixModeButtonActionPerformed

    private void containsModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_containsModeButtonActionPerformed
        setOptMatcherMode(TextMatcherEditor.CONTAINS);
    }//GEN-LAST:event_containsModeButtonActionPerformed

    private void regexModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regexModeButtonActionPerformed
        setOptMatcherRegex(regexModeButton.isSelected());
    }//GEN-LAST:event_regexModeButtonActionPerformed

    private void ignorediaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignorediaButtonActionPerformed
        setOptMatcherIgnoreDia(ignorediaButton.isSelected());
    }//GEN-LAST:event_ignorediaButtonActionPerformed

    private void exactModeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exactModeButtonActionPerformed
        setOptMatcherMode(TextMatcherEditor.EXACT);
    }//GEN-LAST:event_exactModeButtonActionPerformed

    private void filterBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBoxActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        updateNavigatorModel(); 
    }//GEN-LAST:event_refreshButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton confButton;
    private javax.swing.JToggleButton containsModeButton;
    private javax.swing.JToggleButton exactModeButton;
    private javax.swing.JTextField filterBox;
    private javax.swing.JComboBox filtersList;
    private javax.swing.JToggleButton ignorediaButton;
    private javax.swing.JTable itemList;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.ButtonGroup modeGroup;
    private javax.swing.JToggleButton prefixModeButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JToggleButton regexModeButton;
    private javax.swing.JScrollPane scrollpane;
    // End of variables declaration//GEN-END:variables

    

     NavigatorLookupHint hint = new NavigatorLookupHint() {
        public String getContentType() {
            return "text/feat-model";
        }
    };
    

    @Override
    public String getDisplayName() {
        return "todo";
    }
   
    @Override
    public Lookup getLookup() {
        return lookup;
    }
   
    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public String getDisplayHint() {
        return "Navigator";
    }
   
    @Override
    public void panelActivated(Lookup context) {
        lookupContent.add(hint);    // work around: see http://markmail.org/thread/mo7xb34hoztqbmdj
        result = Utilities.actionsGlobalContext().lookupResult(VModel.class);
       
        result.addLookupListener(this);
        resultChanged(null); 
    }

    @Override
    public void panelDeactivated() {
        saveFilters();
        lookupContent.remove(hint);
        result.removeLookupListener(this);
        result = null;
    }
    
    @Override
    public void resultChanged(LookupEvent arg0) {  // todo separate thread
        final Collection<? extends VModel> pseudomodels = result.allInstances();

        if (pseudomodels.size() > 0) { 
            pseudoModel = pseudomodels.iterator().next();
            updateNavigatorModel();
        }
    }
    
    private void updateNavigatorModel() {
        SwingUtilities.invokeLater(new Runnable() { // todo cancel if resultChanged again (ineffective + racing condition)
            public void run() {
                setNavigatorModel();
            }
        });
    }    

    private TableModel createTableMode() {
        return new DefaultTableModel();
    }
    
    
    // todo cleanup
    private void setNavigatorModel() {
        if (pseudoModel == null) return;
        
        final EventList<Element> items = new BasicEventList<>();
        
        System.out.println("curFilter.getFilter()" + curFilter.getFilter());
        System.out.println("pseudoModel.getTopLayer()" + pseudoModel.getTopLayer());
        
        curFilter.getFilter().filter(pseudoModel.getTopLayer(), items);

        final TextFilterator<Element> filterator = new TextFilterator<Element>() {
            @Override
            public void getFilterStrings(final List<String> baseList, final Element element) {
                for (Column col : curFilter.getColumns()) {
                    if (col.isFiltered()) baseList.add( col.map(element) );
                }
            }
        };

        // override TextComponentMatcherEditor to handle errors in regex
        final Color normalFilterBoxColor = filterBox.getForeground();
        matcherEditor = new TextComponentMatcherEditor<Element>(filterBox, filterator) {
            @Override public void setFilterText(String[] newFilters) {            
                try {
                    super.setFilterText(newFilters);
                    filterBox.setForeground(normalFilterBoxColor);           
                }
                catch (java.util.regex.PatternSyntaxException e) {
                    StatusDisplayer.getDefault().setStatusText(e.getMessage());
                    filterBox.setForeground(Color.red);             // todo get from config
                }
            }
        };
        
        updateMatcher();
        
        filteredItems = new FilterList<>(items, matcherEditor); 

        EventTableModel<Element> model = new EventTableModel<>(filteredItems, curFilter.getTableFormat());
        itemList.setModel(model);

        //TableComparatorChooser.<Element>install(itemList, filteredItems, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
    }



    private void openAndFocusElement(final Location selected, final boolean aFocus) {
        final Layer<?> layer = selected.getElement().getLayer();

        // check open TCs first, probably one of them is a provider of the layer
        for (final TopComponent tca : WindowManager.getDefault().getRegistry().getOpened()) {
            if (tca instanceof AcceptingLocation) {
                final VModel tcWlayer = tca.getLookup().lookup(VModel.class);
                if (tcWlayer == pseudoModel) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (aFocus) {
                                tca.requestActive();
                            }
                            else {
                                tca.open();
                            }
                            ((AcceptingLocation)tca).goToLoc(selected);  
                        }
                    });
                    
                    return;
                }
            }
        }
        
        // open a view of the layer (used if the navigator is is not for a TC but for a file node)
        DataObject dobj = XDataObject.find(layer.getFile());
        if (dobj == null) return;
        
        final OpenAtCookie ec = (OpenAtCookie) dobj.getCookie(OpenAtCookie.class);
        if (ec == null) return;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ec.openAt(selected);
                //todo if (aFocus)  todo focus
            }
        });
    }
    
    
    private void saveFilters() {
        if (curFilter != null) getPrefs().put("curFilter", curFilter.getName());

        final XStream xstream = new XStream( new StaxDriver());
        xstream.autodetectAnnotations(true);
        xstream.processAnnotations(FilterView.class);
        xstream.aliasType("column", ColumnPrinter.class);
        String filtersStr = xstream.toXML(new ArrayList<>(filters));

//        System.out.println(org.purl.jh.feat.navigator.Util.prettyFormat(xstreama.toXML(curFilter)));
//        System.out.println("========");

        getPrefs().put("filters", filtersStr);
    }
    
    private void loadFilters() {
        filters = new BasicEventList<>();    // todo load from settings, create default set if empty 

        final String filtersStr = getPrefs().get("filters", null);
        
        if (filtersStr != null)  {
            System.out.println(org.purl.jh.feat.navigator.Util.prettyFormat(filtersStr));

            final XStream xstream = new XStream( new StaxDriver());
            xstream.autodetectAnnotations(true);
            xstream.processAnnotations(FilterView.class);

            try {
                Object obj = xstream.fromXML(filtersStr);

                for (FilterView fv : (List<FilterView>)obj) {
                    fv.setFilter(new Filter(fv.getFilterSpec()));
                    filters.add(fv);
                }
            }
            catch(Throwable e) {
                log.severe("Error processing filter specification\n" + org.purl.jh.feat.navigator.Util.prettyFormat(filtersStr));
            }
        }

        if (filters.isEmpty()) {
            createDefaultFilters();
        }
        
        final String curFilterId = getPrefs().get("curFilter", null);
        curFilter = filterById(curFilterId);
        if (curFilter == null) curFilter = filters.get(0);
     }

    private FilterView filterById(String aId) {
        for (FilterView fv : filters) {
            if (fv.getName().equals(aId)) return fv;
        }
        return null;
    }
    
    
    private void createDefaultFilters() {
        final Column titleCol = new Column("Token/Id", true, "t:",
                    ColumnPrinters.cForm2token, 
                    ColumnPrinters.cEdge2str);

        final Column commCol = new Column("Comment", true,  "c:",
                    ColumnPrinters.cElement2comment, 
                    ColumnPrinters.cElement2comment);

        final Column errCol = new Column("Errors", true,  "e:",
                    ColumnPrinters.cEmpty, 
                    ColumnPrinters.cEdge2errs);


        final List<Column> formCols     = Arrays.asList(titleCol, commCol);
        final List<Column> formEdgeCols = Arrays.asList(titleCol, errCol, commCol);

        FilterSpec filter;
        FilterView fv;

        filter = new FilterSpec();
        filter.layer0 = true;
        filter.forms = true;
        fv = new FilterView("L0 Forms", filter, Column.copy(formCols));
        filters.add(fv);

        filter = new FilterSpec();
        filter.layer1 = true;
        filter.forms = true;
        fv = new FilterView("L1 Forms", filter, Column.copy(formCols));
        filters.add(fv);

        filter = new FilterSpec();
        filter.layer2 = true;
        filter.forms = true;
        fv = new FilterView("L2 Forms", filter, Column.copy(formCols));
        filters.add(fv);

        filter = new FilterSpec();
        filter.layer0 = true;
        filter.layer1 = true;
        filter.layer2 = true;
        filter.forms = true;
        fv = new FilterView("All Forms", filter, Column.copy(formCols));
        filters.add(fv);

        filter = new FilterSpec();
        filter.layer1 = true;
        filter.layer2 = true;
        filter.edges = true;
        filter.withError = true;
        fv = new FilterView("Edges with errors", filter, Column.copy(formEdgeCols));
        filters.add(fv);

        filter = new FilterSpec();
        filter.layer0 = true;
        filter.layer1 = true;
        filter.layer2 = true;
        filter.forms = true;
        filter.edges = true;
        filter.withComments = true;
        fv = new FilterView("Forms/edges with comments", filter, new ArrayList<>(formEdgeCols));
        filters.add(fv);
    }

    
    
    private void readMatcherGui() {
        final int mode = getOptMatcherMode();
        
        regexModeButton.setSelected(getOptMatcherRegex());
        switch (mode) {
            case TextMatcherEditor.EXACT:       exactModeButton.setSelected(true); break;
            case TextMatcherEditor.CONTAINS:    containsModeButton.setSelected(true); break;
            case TextMatcherEditor.STARTS_WITH: prefixModeButton.setSelected(true); break;
            default: 
        }
        
        ignorediaButton.setSelected(getOptMatcherIgnoreDia());
    }
    
    private void updateMatcher() {
        if (pseudoModel == null) return;

        boolean ignoreDia = getOptMatcherIgnoreDia();
        if (!ignoreDia) matcherEditor.setStrategy(TextMatcherEditor.IDENTICAL_STRATEGY);
        
        if (getOptMatcherRegex()) {
            matcherEditor.setMode(TextMatcherEditor.REGULAR_EXPRESSION);
//            matcherEditor.setLive(false);
        }
        else {
            matcherEditor.setMode(getOptMatcherMode());
//            matcherEditor.setLive(true);
        }

        if (ignoreDia) matcherEditor.setStrategy(TextMatcherEditor.NORMALIZED_STRATEGY);
    }

// =============================================================================    
// Reading/writing to preferences
// =============================================================================    
    // restrictions regex => exact
    // ignoreDia =/=> regex
    
    private static final String cMatcher_mode  = "matcher_mode";
    private static final String cMatcher_regex = "matcher_regex";
    private static final String cMatcher_ignoreDia = "matcher_ignoreDia";
    
    
    /**
     * All illegal values are silently interpreted as TextMatcherEditor.CONTAINS.
     * @return 
     */
    private int getOptMatcherMode() {
        final int mode = getPrefs().getInt(cMatcher_mode, 0);
    
        switch (mode) {
            case TextMatcherEditor.EXACT: 
            case TextMatcherEditor.CONTAINS: 
            case TextMatcherEditor.STARTS_WITH: 
                return mode;
            default:
                return TextMatcherEditor.CONTAINS;
        }
    }
    
    
    private boolean getOptMatcherRegex() {
        return getPrefs().getBoolean(cMatcher_regex, false);        // && mode = exact
    }
    
    private boolean getOptMatcherIgnoreDia() {
        return getPrefs().getBoolean(cMatcher_ignoreDia, false);    // && regex = false
    }
    

    private void setOptMatcherMode(int aMode) {
        if (aMode != TextMatcherEditor.EXACT) {
            getPrefs().putBoolean(cMatcher_regex, false);
        }

        getPrefs().putInt(cMatcher_mode, aMode);
    
        readMatcherGui();
        updateMatcher();
    }

    private void setOptMatcherRegex(boolean aRegex) {
        if (aRegex) {
            getPrefs().putBoolean(cMatcher_ignoreDia, false);
            getPrefs().putInt(cMatcher_mode, TextMatcherEditor.EXACT);
        }

        getPrefs().putBoolean(cMatcher_regex, aRegex);

        readMatcherGui();
        updateMatcher();
    }
    
    private void setOptMatcherIgnoreDia(boolean aIgnore) {
        if (aIgnore && getOptMatcherRegex()) {
            getPrefs().putBoolean(cMatcher_regex, false);
        } 

        getPrefs().putBoolean(cMatcher_ignoreDia, aIgnore);

        readMatcherGui();
        updateMatcher();
    }

    private Preferences getPrefs() {
        return NbPreferences.forModule(getClass());
    }

}
