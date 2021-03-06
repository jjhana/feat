package org.purl.jh.feat.importx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.openide.filesystems.FileChooserBuilder;
import org.purl.jh.util.gui.SimpleDocListener;
import org.purl.jh.util.io.FileFilters;
import org.purl.jh.util.gui.XComboBoxModel;
import org.purl.jh.util.gui.XListModel;

/**
 * @todo 
 * @author jirka
 */
public class ImportPanel extends javax.swing.JPanel {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ImportPanel.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(ImportPanel.class);

    /** 
     * The ok button of the enclosing dialog (to be enabled/disabled based on 
     * the correctness of user's input 
     */
    private final JButton okButton;

    private ImportPanelData data = new ImportPanelData();
    
    /** Creates new form ImportPanel 
     * 
     * @param aOkButton ok button so that it can be enabled/disabled
     * 
     * @todo pass list of formats, list of encodings, list of tagsets
     * @todo pass things that determines the auto encoding/format
     * @todo pass default tagset
     * @todo allwo specifying how to remove extension(s) from the in name
     */
    public ImportPanel(JButton aOkButton) {
        okButton = aOkButton;

        initComponents();   // nb basic initialization cocde

        
        initControls();     // my initialization cocde
    }

    public ImportPanelData getData() {
        return data;
    }
    
    
    private void inFileTextUpdated() {
        data.setInFile(inFileText.getText());
        
        if (data.isInFileOk()) {
            inFileGuessedFormat.setText("html");
            inFileGuessedEncoding.setText("UTF-8");
            //outPrefixEdit.setText(util.io.Files.removeExtension(inFile.getPath()));
        }
        else {
            inFileGuessedFormat.setText(" ");
            inFileGuessedEncoding.setText(" ");
        }

        handleMessages();
    }

    private void outPrefixUpdated() {
        data.setUserOutPrefix(outPrefixEdit.getText());

        handleMessages();
    }
    
//                String msg = fileMsg(file);
//                outFileMsgs.add(file + " " + msg);
//    
//        private void x() {
//        }
//

    boolean outPrefixOk = true;
    
    private void handleMessages() {
        outPrefixOk = true;
                
        // outFilesList
        final List<String> outFileMsgs = new ArrayList<String>();
        for (File file : data.getOutFiles()) {
            String msg = fileMsg(file);
            outFileMsgs.add(file + " " + msg);
        }
        outFilesList.setModel( new XListModel<String>(outFileMsgs) );

        // infotext
        String info = "";
        if (! data.isInFileOk()) info += "Specify an existing file to import.\n";
        //todo if (!data.outFileOk) info += "Specify a usable prefix for the output files.";
        
        this.infoText.setText(info);
     
        okButton.setEnabled(data.isInFileOk() && outPrefixOk);
        //okButton.setEnabled(inFileOk && outPrefixOk);
    }

    
    private String fileMsg(final File aFile) {
        if (aFile.isDirectory()) {
            outPrefixOk = false;
            return "- Cannot create, directory with that name exists";
        }
        else if (aFile.exists()) {
            return "- Will overwrite an existing file.";
        }
        else  {
            return "";
        }
    }

    
    
    
    
    private void initControls() {
        // --- temporary defaults (todo) ---
        inFileEncodingCombo.setEnabled(false);
        inFileFormatCombo.setEnabled(false);
        //outPrefixEdit.setEnabled(false);
        outPrefixBrowse.setEnabled(false);
        outTagsetCombo.setEnabled(false);
        
        inFileEncodingCombo.setModel(new XComboBoxModel("Auto"));
        inFileFormatCombo  .setModel(new XComboBoxModel("Auto"));
        outTagsetCombo.setModel(new XComboBoxModel("Default"));
        
        inFileEncodingCombo.setSelectedIndex(0);
        inFileFormatCombo  .setSelectedIndex(0);
        outTagsetCombo     .setSelectedIndex(0);
        
        inFileText.getDocument().addDocumentListener(new SimpleDocListener() {
            @Override
            public void textUpdated() {
                inFileTextUpdated();
            }
        });
        
        outPrefixEdit.getDocument().addDocumentListener(new SimpleDocListener() {
            @Override
            public void textUpdated() {
                outPrefixUpdated();
            }
        });
    
        handleMessages();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        inFileLabel = new javax.swing.JLabel();
        inFileText = new javax.swing.JTextField();
        inFileBrowseButton = new javax.swing.JButton();
        inFileFormatLabel = new javax.swing.JLabel();
        inFileFormatCombo = new javax.swing.JComboBox();
        inFileEncodingLabel = new javax.swing.JLabel();
        inFileEncodingCombo = new javax.swing.JComboBox();
        inFileGuessedFormat = new javax.swing.JLabel();
        inFileGuessedEncoding = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        outPrefixLabel = new javax.swing.JLabel();
        outPrefixEdit = new javax.swing.JTextField();
        outPrefixBrowse = new javax.swing.JButton();
        outFilesLabel = new javax.swing.JLabel();
        outListScrollPane = new javax.swing.JScrollPane();
        outFilesList = new javax.swing.JList();
        outTagsetLabel = new javax.swing.JLabel();
        outTagsetCombo = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoText = new javax.swing.JTextArea();
        infoIcon = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.jLabel1.text")); // NOI18N

        inFileLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileLabel.text")); // NOI18N

        inFileText.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileText.text")); // NOI18N

        inFileBrowseButton.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileBrowseButton.text")); // NOI18N
        inFileBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inFileBrowseButtonActionPerformed(evt);
            }
        });

        inFileFormatLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileFormatLabel.text")); // NOI18N

        inFileFormatCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        inFileEncodingLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileEncodingLabel.text")); // NOI18N

        inFileEncodingCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        inFileGuessedFormat.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileGuessedFormat.text")); // NOI18N

        inFileGuessedEncoding.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.inFileGuessedEncoding.text")); // NOI18N

        outPrefixLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.outPrefixLabel.text")); // NOI18N
        outPrefixLabel.setMaximumSize(new java.awt.Dimension(46, 16));
        outPrefixLabel.setMinimumSize(new java.awt.Dimension(46, 16));
        outPrefixLabel.setPreferredSize(new java.awt.Dimension(46, 16));

        outPrefixEdit.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.outPrefixEdit.text")); // NOI18N

        outPrefixBrowse.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.outPrefixBrowse.text")); // NOI18N

        outFilesLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.outFilesLabel.text")); // NOI18N

        outFilesList.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        outListScrollPane.setViewportView(outFilesList);

        outTagsetLabel.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.outTagsetLabel.text")); // NOI18N

        outTagsetCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jScrollPane1.setBorder(null);

        infoText.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        infoText.setColumns(20);
        infoText.setEditable(false);
        infoText.setRows(5);
        infoText.setBorder(null);
        jScrollPane1.setViewportView(infoText);

        infoIcon.setText(org.openide.util.NbBundle.getMessage(ImportPanel.class, "ImportPanel.infoIcon.text")); // NOI18N
        infoIcon.setBorder(null);
        infoIcon.setContentAreaFilled(false);
        infoIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoIconActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 808, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(outTagsetLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inFileFormatLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(inFileLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(outPrefixLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(inFileText, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                                .addGap(9, 9, 9)
                                .addComponent(inFileBrowseButton))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(inFileGuessedFormat, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                                    .addComponent(inFileFormatCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 268, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inFileEncodingLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(inFileGuessedEncoding, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                                    .addComponent(inFileEncodingCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 269, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(outPrefixEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(outPrefixBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(outTagsetCombo, 0, 670, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(infoIcon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(outFilesLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(outListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {inFileBrowseButton, outPrefixBrowse});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inFileLabel)
                    .addComponent(inFileText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inFileBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inFileFormatLabel)
                    .addComponent(inFileFormatCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inFileEncodingLabel)
                    .addComponent(inFileEncodingCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inFileGuessedFormat)
                    .addComponent(inFileGuessedEncoding))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(outPrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outPrefixEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(outPrefixBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(outTagsetLabel)
                    .addComponent(outTagsetCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(outFilesLabel)
                    .addComponent(outListScrollPane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void inFileBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inFileBrowseButtonActionPerformed
        File inFile = new FileChooserBuilder("import.open.dir")
              .setTitle(bundle.getString("import.open.title"))
              .setFilesOnly(true)   
                //.setDefaultWorkingDirectory(defFile)
              .setApproveText(bundle.getString("import.open.button"))
              .addFileFilter(FileFilters.endingFilter("html?", bundle.getString("import.open.filter.html")))
              .showOpenDialog();

        if (inFile != null) {
            inFileText.setText(inFile.getPath());
        }
    }//GEN-LAST:event_inFileBrowseButtonActionPerformed

    private void infoIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoIconActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_infoIconActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton inFileBrowseButton;
    private javax.swing.JComboBox inFileEncodingCombo;
    private javax.swing.JLabel inFileEncodingLabel;
    private javax.swing.JComboBox inFileFormatCombo;
    private javax.swing.JLabel inFileFormatLabel;
    private javax.swing.JLabel inFileGuessedEncoding;
    private javax.swing.JLabel inFileGuessedFormat;
    private javax.swing.JLabel inFileLabel;
    private javax.swing.JTextField inFileText;
    private javax.swing.JButton infoIcon;
    private javax.swing.JTextArea infoText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel outFilesLabel;
    private javax.swing.JList outFilesList;
    private javax.swing.JScrollPane outListScrollPane;
    private javax.swing.JButton outPrefixBrowse;
    private javax.swing.JTextField outPrefixEdit;
    private javax.swing.JLabel outPrefixLabel;
    private javax.swing.JComboBox outTagsetCombo;
    private javax.swing.JLabel outTagsetLabel;
    // End of variables declaration//GEN-END:variables

}
