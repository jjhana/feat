package cz.cuni.utkl.czesl.main.tmp;

import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import javax.swing.table.AbstractTableModel;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.CloneableTopComponent;
import org.purl.jh.feat.NbData.view.ErrorTagsetView;
import org.purl.jh.nbpml.TagsetProvider;

/**
 * todo handle changes
 * todo undo
 * Todo remove the singleton code
 * todo handle tag properties
 * todo send changes to documents using this tagset (or maybe just require reload)
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//cz.cuni.utkl.czesl.main.tmp//ErrorTagsetView//EN",autostore = false)
@TopComponent.Description(preferredID = "ErrorTagsetViewTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "cz.cuni.utkl.czesl.main.tmp.ErrorTagsetViewTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ErrorTagsetViewAction",preferredID = "ErrorTagsetViewTopComponent")
public final class ErrorTagsetViewTopComponent extends CloneableTopComponent {

    @ServiceProvider(service=ErrorTagsetView.class)
    public static class ErrorTagsetViewSupport implements ErrorTagsetView {
        public CloneableTopComponent getTopComponent(DataObject aDObj) {
            return (aDObj == null) ? null : new ErrorTagsetViewTopComponent(aDObj);
        }
    }
    
    
    public ErrorTagsetViewTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "CTL_ErrorTagsetViewTopComponent"));
        setToolTipText(NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "HINT_ErrorTagsetViewTopComponent"));

    }

    public ErrorTagsetViewTopComponent(DataObject aDobj) {
        this();

        final ErrorTagset tagset = (ErrorTagset) aDobj.getNodeDelegate().getLookup().lookup(TagsetProvider.class).getTagset();
        
        uidText.setText(tagset.getId());
        descText.setText(tagset.getDescr());
        
        jTable1.setModel(new AbstractTableModel() {

            public int getRowCount() {
                return tagset.getTags().size();
            }

            public int getColumnCount() {
                return 8;
            }

            @Override
            public String getColumnName(int columnIdx) {
                switch (columnIdx) {
                    case 0: return "Tag";           // todo load string 
                    case 1: return "Description";
                    case 2: return "Min Lower Legs";
                    case 3: return "Max Lower Legs";
                    case 4: return "Min Upper Legs";
                    case 5: return "Max Upper Legs";
                    case 6: return "Min Links";
                    case 7: return "Max Links";
                    default: throw new IllegalArgumentException("Column="+columnIdx);
                }
            }
            
            public Object getValueAt(int rowIdx, int columnIdx) {
                final ErrorTag tag = tagset.getTags().get(rowIdx);
                
                switch (columnIdx) {
                    case 0: return tag.getId();
                    case 1: return tag.getDescr();
                    case 2: return tag.getMinLowerLegs();
                    case 3: return tag.getMaxLowerLegs();
                    case 4: return tag.getMinUpperLegs();
                    case 5: return tag.getMaxUpperLegs();
                    case 6: return tag.getMinLinks();
                    case 7: return tag.getMaxLinks();
                    default: throw new IllegalArgumentException("Column="+columnIdx);
                }
            }

            @Override
            public void setValueAt(Object aVal, int rowIdx, int colIdx) {
                final ErrorTag tag = tagset.getTags().get(rowIdx);

                switch (colIdx) {
                    case 0: tag.setId((String) aVal); break;
                    case 1: tag.setDescr((String) aVal); break;
                    case 2: tag.setMinLowerLegs((Integer) aVal); break;
                    case 3: tag.setMaxLowerLegs((Integer) aVal); break;
                    case 4: tag.setMinUpperLegs((Integer) aVal); break;
                    case 5: tag.setMaxUpperLegs((Integer) aVal); break;
                    case 6: tag.setMinLinks((Integer) aVal); break;
                    case 7: tag.setMaxLinks((Integer) aVal); break;
                    default: throw new IllegalArgumentException("Column="+colIdx);
                }
            }
        });
    };
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        uidLabel = new javax.swing.JLabel();
        uidText = new javax.swing.JTextField();
        descLabel = new javax.swing.JLabel();
        descText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        org.openide.awt.Mnemonics.setLocalizedText(uidLabel, org.openide.util.NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "ErrorTagsetViewTopComponent.uidLabel.text")); // NOI18N

        uidText.setText(org.openide.util.NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "ErrorTagsetViewTopComponent.uidText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(descLabel, org.openide.util.NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "ErrorTagsetViewTopComponent.descLabel.text")); // NOI18N

        descText.setText(org.openide.util.NbBundle.getMessage(ErrorTagsetViewTopComponent.class, "ErrorTagsetViewTopComponent.descText.text")); // NOI18N

        jTable1.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(descLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(uidLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 58, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descText, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                            .addComponent(uidText, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(uidLabel)
                    .addComponent(uidText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descLabel)
                    .addComponent(descText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descLabel;
    private javax.swing.JTextField descText;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel uidLabel;
    private javax.swing.JTextField uidText;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
