package org.purl.jh.feat.views.layertext;

import org.purl.jh.feat.layered.Css;
import org.purl.jh.feat.layered.WidgetNode;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerx.Doc;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.purl.jh.feat.util0.XComboBox;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;
import org.purl.jh.util.col.IntInt;
import org.purl.jh.util.col.Mapper;

/**
 * Top component displaying tokens of annotation layers as continuous text.
 * 
 * @todo Currently broken, because PseudoModel covering the whole layer stack was replaced 
 * by a list of layers. I can (or should) only go downwards. 
 * @todo put vmodel into lookup??
 */                            
@ConvertAsProperties(dtd = "-//org.purl.jh.feat.views.layertext//Text//EN", autostore = false)
@TopComponent.Description(
    preferredID = "TextTopComponent",
    iconBase="org/purl/jh/feat/views/layertext/text_area.png",
    persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "org.purl.jh.feat.views.layertext.TextTopComponent")
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 900 ),
    @ActionReference(path = "Toolbars/Window", position = 900 )
})
@TopComponent.OpenActionRegistration(displayName = "#CTL_TextAction", preferredID = "TextTopComponent")
public final class TextTopComponent extends TopComponent implements DataListener {
    private FormsLayer<?> layer;
    private WidgetNode node;
    private Map<FForm,IntInt> form2pos;
    

    private Lookup.Result<Node> nodeLookupResult;

    public TextTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(TextTopComponent.class, "CTL_TextTopComponent"));
        setToolTipText(NbBundle.getMessage(TextTopComponent.class, "HINT_TextTopComponent"));
    }


    void setModel(Layer<?> aTopLayer) {
//        if (model != null) {
//            model.removeDataListener(this);
//        }
//        //if (aModel == model) return;
//        //System.err.printf("Setting model %d/%d\n", (model == null ? -1 : model.hashCode()), (model == null ? -1 : aModel.hashCode()) );
//        model = aModel;
//        model.addDataListener(this);
//        
//        layer = null;
//
//        List<FormsLayer<?>> formLayers = model.getFormLayers();
//        System.err.println("formLayers " + formLayers.size());
//        getCombo().setData(formLayers);
//        System.err.println("setModel: " + getCombo().comboItemsToString(getCombo()) );
    }

    void setLayer(final FormsLayer<?> aLayer) {
        if (layer != null) {
            layer.removeChangeListener(this);
        }
        
        layer = aLayer;
        getCombo().setData(Arrays.<FormsLayer<?>>asList(layer));
        getCombo().setCurItem(layer);
        setText();
        
        layer.addChangeListener(this);
    }

    /**
     * Reaction to model change.
     * @param aChangeEvent 
     */
    @Override
    public void handleChange(DataEvent aChangeEvent) {
        setText();
    }

    // todo use AcceptingLocation
    class NodeLookupListener implements LookupListener {
        int c = 1;

        public void resultChanged(LookupEvent le){
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    Collection<? extends Node> nodes = nodeLookupResult.allInstances();
                    System.err.println("TextTopComponent.resultChanged " + nodes);

                    if (nodes.isEmpty()) {
                            layer = null;
                            setText();                                            
        //                 if (Utilities.actionsGlobalContext().lookup(NavigatorLookupHint.class) != null) {
        //                    getCombo().setData(Collections.<FormsLayer>emptyList());
        //                 }
                    }
                    else {
                        Node node = nodes.iterator().next();
                        if (!(node instanceof WidgetNode)) return;
                        WidgetNode wnode = (WidgetNode)node;

//                      // now there is no model except a layer                        
//                        System.err.printf("model %d/%d\n", hash(model), hash(wnode.getModel()));
//                        if (model != wnode.getModel()) {
//                            System.err.println("diff model");
//                            setModel( wnode.getModel());
//                        }

                        System.err.printf("Layer %d/%d\n", hash(layer), hash(wnode.getLayer()));
                        if (layer != wnode.getLayer()) {
                            //layer = (FormsLayer<?>)wnode.getLayer();
                            System.err.println("diff layer");
                            //todo does not work any more getCombo().setCurItem((FormsLayer<?>)wnode.getLayer());
                            Layer<?> tmp = wnode.getLayer();
                            if (tmp instanceof FormsLayer<?>) {
                                layer = (FormsLayer<?>)tmp;
                                //todo not used any more getPrefs().putInt("layerComboIdx", getCombo().getSelectedIndex());       
                            }
                            else {
                                layer = null;
                            }
                            setText();                                            
                        }

                        removeOldHighlight();
                        highlightInText( Arrays.asList( ((WidgetNode)node).getObject()) ); // todo
                        System.err.flush();
                    }
                }
            });
       }
    }




    private JComboBox createComboBox() {
        return new XComboBox<>(new Mapper<FormsLayer,String>() {
            @Override
            public String map(FormsLayer aOrigItem) {
                return aOrigItem.getId();
            }
        });
    }

    protected XComboBox<FormsLayer<?>> getCombo() {
        return (XComboBox<FormsLayer<?>>) layerCombo;
    }




    private void setText() {
        // todo does not work any more final FormsLayer layer = getCombo().getCurItem();
        setText(layer);
    }

    private final Pattern noSpaceBefore = Pattern.compile("[\\.,;)\\]:?“]");
    private final Pattern noSpaceAfter = Pattern.compile("[\\(\\[„]");
    // simple quotes are not handled 
    
    private void setText(FormsLayer<?> aLayer) {
        final StringBuilder sb = new StringBuilder();
        form2pos = new HashMap<>();

        for (Doc doc : docs(aLayer)) {
            for (Para para : doc.getParas()) {
                boolean start = true;
                boolean spaceAfter = true;
                
                for (FForm form : para.getForms()) {
                    final String token =  form.getToken();
                    if (start) {
                        start = false;
                    }
                    else {
                        if (spaceAfter && !noSpaceBefore.matcher(token).matches())
                            sb.append(' ');
                    }
                    

                    form2pos.put(form, new IntInt(sb.length(), token.length()));
                    sb.append(token);
                    spaceAfter = !noSpaceAfter.matcher(token).matches();
                }
                
                sb.append("\n\n");
            }
        }
        this.jTextPane1.setText(sb.toString() );

        // todo allow showing comments, corrections, etc in one place by customizable format
//        StyledDocument textDoc = jTextPane1.getStyledDocument();
//
//        Style commentStyle = jTextPane1.addStyle("Comment", null);
//        //StyleConstants.setUnderline(commentStyle, true);
//        StyleConstants.setForeground(commentStyle, Css.INSTANCE.getStyle(Css.comment).border_color); // todo use dedicated style
//
//        for (Doc<?> doc : docs(aLayer)) {
//            for (Para<?> para : doc.getParas()) {
//                for (Form<?> form : para.getForms()) {
//                    if ( ElementUtils.isCommented(form) ) {
//                        IntInt pos_len = this.form2pos.get(form);
//                        textDoc.setCharacterAttributes(pos_len.mFirst, pos_len.mSecond, jTextPane1.getStyle("Comment"), true);
//                    }
//                }
//            }
//        }


    }

    private Iterable<? extends Doc> docs(FormsLayer<?> aLayer) {
        if (aLayer instanceof LLayer) {
            return ((LLayer)aLayer).col();
        }
        else if (aLayer instanceof WLayer) {
            return ((WLayer)aLayer).col();
        }
        else {
            return Collections.<Doc>emptyList();
        }
    }

// =============================================================================
// Highlighting
// todo coppied fromm LayeredViewTopComponent, reuse
// =============================================================================
    private final Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Css.selectHighlight);

    static class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

//    private void highlightInText(final Iterable<Object> aObjs) {
//        for (Object obj : aObjs) {
//            if (obj instanceof Form<?>) highlightInText((Form<?>) obj);
//        }
//    }
//
//    private void highlightInText(final Form<?> aForm) {
//        highlightInText(FeatDataUtil.getWForms(aForm));
//    }

    private void highlightInText(final Collection<?> aObjs) {
        final Highlighter hiliter = jTextPane1.getHighlighter();

        for (Object obj : aObjs) {
            if (! (obj instanceof FForm) ) continue;

            IntInt pos_len = this.form2pos.get((FForm)obj);

            if (pos_len == null) continue;

            // --- add extra visibility for short words ---
//            if (pos_len.mSecond < 3) {  // todo precalculate?
//                if (pos_len.mFirst > 0) start--;
//                if (end < jTextPane1.getDocument().getLength() - 1) end++;
//            }
            int end = pos_len.mFirst + pos_len.mSecond;

            try {
                hiliter.addHighlight(pos_len.mFirst, end, myHighlightPainter);
                System.err.println("Scrolling to " + jTextPane1.modelToView(end));
                Rectangle rect = jTextPane1.modelToView(end);
                if (rect != null) jTextPane1.scrollRectToVisible(rect);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }


    // Removes only our private highlights
    public void removeOldHighlight() {
        Highlighter hiliter = jTextPane1.getHighlighter();

        for (Highlighter.Highlight hilite : hiliter.getHighlights()) {
            if (hilite.getPainter() instanceof MyHighlightPainter) {
                hiliter.removeHighlight(hilite);
            }
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        layerCombo = createComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TextTopComponent.class, "TextTopComponent.jLabel1.text")); // NOI18N
        jLabel1.setEnabled(false);
        jToolBar1.add(jLabel1);

        layerCombo.setEnabled(false);
        layerCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerComboActionPerformed(evt);
            }
        });
        jToolBar1.add(layerCombo);

        jTextPane1.setEditable(false);
        jTextPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextPane1.setAutoscrolls(false);
        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void layerComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerComboActionPerformed
        layer = getCombo().getCurItem();
        System.err.println("layerComboActionPerformed " + hash(layer));
        getPrefs().putInt("layerComboIdx", getCombo().getSelectedIndex());          setText();     }//GEN-LAST:event_layerComboActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox layerCombo;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
      nodeLookupResult = Utilities.actionsGlobalContext().lookupResult(Node.class);
      nodeLookupResult.addLookupListener(new NodeLookupListener());

    }

    @Override
    public void componentClosed() {
//        modelLookupResult.removeLookupListener(this);
//        nodeLookupResult.removeLookupListener(this);
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

    private Preferences getPrefs() {
        return NbPreferences.forModule(getClass());
    }

    private int hash(Object a) {
        return a == null ? 0 : a.hashCode();
    }
}
