package org.purl.net.jh.nb.rtf;

import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.ImageUtilities;
import org.openide.windows.CloneableTopComponent;

/**
 * TODO currently singleton
 *
 * Top component which displays something.
 */
//@ConvertAsProperties(dtd = "-//cz.cuni.utkl.czesl.views.rtf//Rtf//EN",
//autostore = false)
public final class RtfTopComponent extends CloneableTopComponent {

    /** path to the icon used by the component and its open action */
    static final String ICON_PATH = "cz/cuni/utkl/czesl/views/rtf/rtf.16.gif";
    private static final String PREFERRED_ID = "RtfTopComponent";

    public RtfTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(RtfTopComponent.class, "CTL_RtfTopComponent"));
        setToolTipText(NbBundle.getMessage(RtfTopComponent.class, "HINT_RtfTopComponent"));
        setIcon(ImageUtilities.loadImage(ICON_PATH, true));

    }

   public RtfTopComponent(RtfDataObject aData) {
        this();

        this.jEditorPane1.setEditorKit(aData.getKit());
        this.jEditorPane1.setDocument(aData.getDoc());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();

        jScrollPane1.setViewportView(jEditorPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }
}
