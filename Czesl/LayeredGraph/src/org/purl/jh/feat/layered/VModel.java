package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerx.ChangeEvent;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.awt.UndoRedo;
import org.openide.awt.UndoRedo.Manager;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.purl.jh.feat.layered.util.Util;
import org.purl.jh.pml.HtmlLayer;
import org.purl.jh.pml.ImgLayer;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * Read-only model combining connected layers into one object. All modifications
 * must be performed through the appropriate layer.
 * 
 * @author j
 */
public class VModel implements DataListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(VModel.class);
    
    /**
     * Lowest layers are first, top layer is last.
     * Assumes only one other layer is referenced in each layer.
     */
    private final List<Layer<?>> layers = new ArrayList<>();

    /**
     * W (L0) layer is first, top layer is last.
     */
    private final List<FormsLayer<?>> formLayers = new ArrayList<>();   // todo currently sorted as it goes in, maybe it is ok
    private final List<LLayer>        lLayers = new ArrayList<>();          // todo currently sorted as it goes in, maybe it is ok
    
    private final Layer<?> topLayer;
    private final WLayer wLayer;
    private final HtmlLayer textLayer;
    private final ImgLayer imgLayer;

    private final List<DataObject> dobjs = new ArrayList<>();

    private final List<? extends Para> topParas;
    private final List<WPara> wParas;
    
    // replace with soft-reference cache
    private final List<ParaModel> paraModels = new ArrayList<>();
    
    private final UndoRedo.Manager undoMngr;
    private final UndoRecorder undoRecorder;
    
    public VModel(Layer<?> topLayer, UndoRedo.Manager undoMngr) {
        this.topLayer = topLayer;    
        this.undoMngr = undoMngr;

        collectLayers(topLayer);
        collectDObjs();

        log.info("TC: Layers: %s", layers);

        textLayer = Cols.findElement(layers, HtmlLayer.class);
        imgLayer  = Cols.findElement(layers, ImgLayer.class);
        wLayer    = Cols.findElement(layers, WLayer.class);

        wParas    = (List<WPara>)this.collectParas(wLayer);
        topParas  = this.collectParas(topLayer);
        Cols.init(paraModels, topParas.size());

        for (Layer<?> layer : layers) {
            if (layer instanceof FormsLayer) {
                formLayers.add((FormsLayer<?>)layer);

                if (layer instanceof LLayer) {
                    lLayers.add((LLayer)layer);
                }
            }
        }
        
//        wLayer.setReadOnly(true);
//        if (imgLayer  != null) imgLayer.setReadOnly(true);
//        if (textLayer != null) textLayer.setReadOnly(true);

        undoRecorder = new UndoRecorder(getUndoMngr(), this);
    
    }

    /**
     * Convenience method adding an 
     * @param aView 
     */
    public void addChangeListener(DataListener aView) {
        for (Layer<?> layer : layers) {
            layer.addChangeListener(aView);
        }
    }

    /**
     * Removes a listener that's notified each time a change to the data model occurs.
     * @param l the <code>GraphDataListener</code> to be removed
     */
    public void removeChangeListener(DataListener aView) {
        for (Layer<?> layer : layers) {
            layer.removeChangeListener(aView);
        }
    }
    
    public final Manager getUndoMngr() {
        return undoMngr;
    }
//
//
//    public List<DataObject> getDobjs() {
//        return dobjs;
//    }
//
    
    
    public ImgLayer getImgLayer() {
        return imgLayer;
    }

    public List<Layer<?>> getLayers() {
        return layers;
    }

    /**
     * Returns the stack of form layers.
     * W (L0) layer is first, top layer is last.
     * @return
     */
    public List<FormsLayer<?>> getFormLayers() {
        return formLayers;
    }


    /**
     * Returns the stack of L-layers.
     * A (L1) layer is first, top layer is last.
     * @return
     */
    public List<LLayer> getLLayers() {
        return lLayers;
    }
    
    
    public List<? extends Para> getParas() {
        return topParas;
    }

    public HtmlLayer getTextLayer() {
        return textLayer;
    }

    public Layer<?> getTopLayer() {
        return topLayer;
    }

    public WLayer getwLayer() {
        return wLayer;
    }

    public List<WPara> getWparas() {
        return wParas;
    }
 
    private void collectLayers(Layer<?> aLayer) {
        if (aLayer == null) return;         // optional layer
        
        for (Layer<?> layer : aLayer.getReferencedLayers()) {
            collectLayers(layer);
        }
        layers.add(aLayer);
    }

    private void collectDObjs() {
        for (Layer<?> layer : layers) {
            try {
                dobjs.add(DataObject.find(layer.getFile()));
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);     // todo
            }
        }
    }


    /**
     * Collects all paragraphs in this layer.
     *
     * TODO this looks like a job for XQUery somehow
     * TODO create flattening utility class?
     *
     * @return list of paragraphs, the list is computed at each invocation of
     *   this method, it is modifiable without any write-through.
     */
    private List<? extends Para> collectParas(final Layer<?> aLayer) {
        if (aLayer instanceof WLayer) {
            final List<WPara> tmpParas = new ArrayList<>();
            for (WDoc doc : ((WLayer) aLayer).col()) {
                tmpParas.addAll(doc.getParas());
            }
            return tmpParas;
        }
        else {
            final List<LPara> tmpParas = new ArrayList<>();
            Err.iAssert(aLayer instanceof LLayer, "Should be an LLayer, but is not: %s", aLayer);

            for (LDoc doc : ((LLayer) aLayer).col()) {
                tmpParas.addAll(doc.getParas());
            }
            return tmpParas;
        }
        
    }

    /**
     * Creates a new para model. 
     * @param aParaIdx
     * @return
     */
    public ParaModel getParaModel(int aParaIdx) {
        ParaModel paraModel = paraModels.get(aParaIdx);
        if (paraModel == null) {
            paraModel = new ParaModel(this, topParas.get(aParaIdx));
            paraModels.set(aParaIdx, paraModel);
        }
        
        return paraModel;
    }
//    
//    
//    public void save() throws IOException {
//        log.info("--- Save cookie ---");
//        for (DataObject dobj : dobjs) {
//            log.info("Checking do %s", dobj);
//            if (dobj.isModified()) {
//                SaveCookie saveCookie = dobj.getCookie(SaveCookie.class);
//                saveCookie.save();
//            }
//        }
//        log.info("---End Save cookie ---");
//    }
//
//    
//    
//    
//    
//
//// =============================================================================
//// Read only access to the model
//// =============================================================================
//
    /**
     * Returns the index of the layer within the model's stacked lists
     *
     * @throws  IllegalArgumentException if the layer is not part of the stack
     */
    public int getLayerIdx(final FormsLayer<?> aLayer) {
        final int idx = formLayers.indexOf(aLayer);

        if (idx == -1) throw new IllegalArgumentException(String.format("Model %s does not contain layer %s.", this, aLayer));

        return idx;
    }



    /**
     * Returns the layer immediately above the specified.
     *
     * @param aLayer layer to look above
     * @return layer above aLayer
     */
    public LLayer getLayerAbove(final FormsLayer<?> aLayer) {
        return (LLayer) Util.getOffset(formLayers, aLayer, 1);
    }
//
////    /**
////     * Returns the layered nodes within the current paragraph.
////     *
////     * @see #getLayerIdx(cz.cuni.utkl.czesl.data.layerx.FormsLayer) for the row index
////     */
////    public List<List<? extends Form>> getNodes() {
////        return layer2forms;
////    }
////
////    /**
////     * Returns a layer of nodes within the current paragraph.
////     *
////     * @see #getLayerIdx(cz.cuni.utkl.czesl.data.layerx.FormsLayer) for the row index
////     */
////    public <F extends Form> List<F> getNodes(int aLayerIdx) {
////        return (List<F>) layer2forms.get(aLayerIdx);
////    }
////
////    public <F extends Form> List<F> getNodes(FormsLayer<?> aLayer) {
////        return (List<F>) layer2forms.get(getLayerIdx(aLayer));
////    }
////
////    public Collection<Edge> getEdges(int aHigherLayerIdx) {
////        return ((LPara)paras.get(aHigherLayerIdx)).getEdges();
////    }
//
//    
//    
//
//    /**
//     * Finds the place on the higher layer for a new sentence corresponding to a
//     * sentence on the lower layer.
//     *
//     * @param aL1S lower-layer sentence
//     * @param aL2Ss list of higher-layer sentences
//     * @return
//     */
//    private int findPlace(final Sentence aL1S, final List<Sentence> aL2Ss) {
//        if (aL2Ss.isEmpty()) return 0;
//
//        final LForm form = Cols.last(aL1S.getChildren());
//        final LPara para = aL1S.getParent();
//
//        for (int i= 0; i < aL2Ss.size(); i++) {  // todo go from size to 0??
//            // finds some lower form
//            final Form l1form = lowerForm(aL2Ss.get(i).getChildren());
//
//            // skips unconnected sentences;
//            if (l1form == null) continue;
//
//            // relative position of form and lowerForm
//            if (para.before(form, l1form) ) return i;
//        }
//
//        return aL2Ss.size();
//    }
//
//    /**
//     * Finds some form on lower level corresponding to a list of forms
//     *  considers only one form of the sentence (usually the last one).
//     *  We could consider all or majority, but that would be too costly and in most actual cases would give the same result
//     *
//     * @return null if the list of forms is not connected to the lower layer.
//     */
//    private Form lowerForm(final List<LForm> aForms) {
//        for (int i = aForms.size()-1; i >= 0; i--) {
//            final LForm l2form = aForms.get(i);
//            for (Form l1form : l2form.getLowerForms()) {
//                if (l1form != null) return l1form;
//            }
//        }
//        return null;
//    }
//
//    public boolean sentenceCopyHigherPossible(final Sentence aS1) {
//        final LPara p      = aS1.getParent();
//        final LPara p2     = getHigherPara(p);
//        if (p2 == null) return false;       // no higher layer/para
//
//        final LLayer layer2 = (LLayer)p2.getLayer();
//        if (layer2.isReadOnly() || aS1.isEmpty() || !noEdgeHigher(aS1) ) return false;       // a new sentence is added only if S1 is not linking to anything on the higher layer yet.
//
//        return true;
//    }
//
//
//
//
//    /**
//     * Note: The lower layer must contain sentence, i.e. it must be LLayer.
//     * @param aS2
//     * @return
//     */
//    private Set<Sentence> getLowerSentences(final Sentence aS2) {
//        Err.iAssert(aS2.getLayer().getLowerLayer() instanceof LLayer, "No sentences to look for on lower layer (%s)", aS2);
//        Set<Sentence> s1s = new SmallSet<Sentence>();
//        for (LForm f2 : aS2.getChildren()) {
//            for (Edge e : f2.getLower()) {
//                for (Form f1 : e.getLower()) {
//                    s1s.add((Sentence)f1.getParent());
//                }
//            }
//
//        }
//
//        return s1s;
//    }
//
//    // todo optimize
//    public int findPlaceForS2(final Sentence aS1, final LPara aP2) {
//        // todo check the most probable position first
//        final int anchor1idx = indexOf(aS1);
//
//        final List<Sentence> s1s = aS1.getParent().getSentences();
//        final List<Sentence> s2s = aP2.getSentences();
//
//        final int size1 = s1s.size();
//
//        int start=0;
////        for (int i = 0; i < s2s.size(); i++) {
////            Sentence s2 = s2s.get(i);
////            Set<Sentence> s1 = getLowerSentences(s2);
////
////            for (.,/?..
////                    .
////                    ??.aP2l;.aP2;l.
////                    Sentence s : s1) {
////                .
////            }
////
////        }
////            todo
////
////        int s1idx =
//
//
//        return -1;
//    }
//
//    /**
//     * Returns the index of a sentence within its paragraph.
//     * @param aS
//     * @return
//     */
//    public int indexOf(final Sentence aS) {
//        return aS.getParent().getSentences().indexOf(aS);
//    }
//
//    public <T> int indexOf(final List<T>  aList, final int aSize, final T aElement, final int aStart) {
//        return aList.subList(aStart, aSize).indexOf(aElement);
//    }
//// =============================================================================
//// Support for views
//// =============================================================================
//
//
//
//
//
//
////        undoManager.addEdit(new AbstractUndoableEdit() {
////            public void undo() throws CannotUndoException {
////                super.undo();
////                formDel(newForm, null, null);
////            }
////            public void redo() throws CannotUndoException {
////                super.redo();
////                formAdd(aStr, aAnchor, aSrcView, aSrcInfo);
////            }
////        });
//
//
////    }
//
//
//    public static boolean isDtForm(final Form aForm) {
//        return (aForm instanceof WForm && !((WForm)aForm).isNormal());
//    }
//
//    public static boolean noEdgeHigher(final Sentence aS) {
//        for (LForm f : aS.getChildren()) {
//            if (!f.getHigher().isEmpty()) return false;
//        }
//
//        return true;
//    }
//
//
//
//     protected static void addToList(final List<LForm> aList, final Position aPosition, final LForm aEl) {
//        int idx = aList.indexOf(aPosition.getForm());
//        Err.iAssert(idx != -1, "Cannot find position %s\n%s", aPosition, aList);
//
//        if (aPosition.isAfterForm()) {
//            idx++;
//        }
//
//        if (idx < aList.size()) {
//            //log.info("adding form to " + idx);
//            aList.add(idx, aEl);
//        } else {
//            //log.info("adding form to the end of sentence" + idx);
//            aList.add(aEl);
//        }
//        //log.info("  added=" + Cols.toString(newSentence.getChildren()));
//    }
//
//     protected static void addToList(final List<LForm> aList, final LForm aMarker, final List<LForm> aEls) {
//        final int idx = aList.indexOf(aMarker);
//        Err.iAssert(idx != -1, "Cannot find marker %s\n%s", aMarker, aList);
//        aList.remove(idx);
//
//        if (idx < aList.size() - 1) {
//            //log.info("adding form to " + idx);
//            aList.addAll(idx, aEls);
//        } else {
//            //log.info("adding form to the end of sentence" + idx);
//            aList.addAll(aEls);
//        }
//        //log.info("  added=" + Cols.toString(newSentence.getChildren()));
//    }
//
//     protected static void addToList(final List<LForm> aList, final Position aPosition, final List<LForm> aEls) {
//        int idx = aList.indexOf(aPosition.getForm());
//        Err.iAssert(idx != -1, "Cannot find position %s\n%s", aPosition, aList);
//
//        if (aPosition.isAfterForm()) {
//            idx++;
//        }
//
//        if (idx < aList.size()) {
//            //log.info("adding form to " + idx);
//            aList.addAll(idx, aEls);
//        } else {
//            //log.info("adding form to the end of sentence" + idx);
//            aList.addAll(aEls);
//        }
//        //log.info("  added=" + Cols.toString(newSentence.getChildren()));
//    }
//
//    private boolean assertW(boolean aTest, String aFormat, Object ... aParams) {
//        if ( aTest ) return false;
//        log.info("Warning: [Model.%s] " + aFormat, aParams);
//        return true;
//   }
//
//    private boolean assertE(boolean aTest, String aFormat, Object ... aParams) {
//        if ( aTest ) return false;
//
//        log.info("Error: [Model.%s] " + aFormat, aParams);
//        return true;
//   }
//
//// =============================================================================    
//// Querries about links etc.
//// =============================================================================    
//    
//    
//    public Para getPara(Element aElement) {
//        return aElement.getAncestor(Para.class);
//    }
//
//    /**
//     * Find the corresponding paragraph at a higher level.
//     *
//     * This means, finds a paragraph at the higher layer with a link to the
//     * specified paragraph. In theory, there might be multiple higher layers,
//     * each having a paragraph linking to the specified one, so the search
//     * is done within lLayers only.    *
//     *
//     * @param aPara paragraph to find the higher counterpart for.
//     * @return the corresponding higher paragraph, or null if there is none.
//     */
//    private LPara getHigherPara(final Para aPara) {
//        final int idx = lLayers.indexOf(aPara.getLayer());
//        if (idx + 1 == lLayers.size()) return null;      // todo throw exception??
//        final Doc lowerDoc = (Doc)aPara.getParent();
//        final LLayer higherLayer = lLayers.get(idx+1);
//
//        for(LDoc doc : higherLayer.getChildren() ) {
//            if (doc.getLowerDoc() == lowerDoc) {
//                for(LPara para : doc.getParas() ) {
//                    if (para.getLowerPara() == aPara) return para;
//                }
//            }
//        }
//
//        return null;
//
//    }
//    
//    /**
//     * Not very effective. Calculate the up-pointing links for all elements (possibly lazily).
//     * @param aPara
//     * @return 
//     */
//    public Para getTopPara(final Para aPara) {
//        Para cur = aPara;
//        while (true) {
//            LPara next = getHigherPara(cur);
//            if (next == null) return cur;
//            cur = next;
//        }
//    }
//    
//    /**
//     * @param aPara 
//     * @todo not very effective, redesign completely.
//     * Todo 
//     */
//    private void fillForms(Para aPara) {
//        final Para topPara = getTopPara(aPara);
//        final ParaModel paraModel = getParaModel(topParas.indexOf(topPara));
//        paraModel.fillForms(aPara);
//    }
//    
//    

    private final static List<String> cDisruptiveEvents = 
        Arrays.asList(
//            ChangeEvent.cFormEdit   
            ChangeEvent.cFormAdd,    
            ChangeEvent.cFormDel,    
            ChangeEvent.cFormMove,   
            ChangeEvent.cFormsMove,  
//            ChangeEvent.cFormChange 
//            ChangeEvent.cEdgeAdd    
//            ChangeEvent.cEdgeDel    
//            ChangeEvent.cEdgeChange 
//            ChangeEvent.cLegAdd     
//            ChangeEvent.cLegDel     
//            ChangeEvent.cErrorAttrChange
//            ChangeEvent.cErrorAdd   
//            ChangeEvent.cErrorDel   
//            ChangeEvent.cErrorLinkAdd 
            //else if (aE.getId().equals(C
//            ChangeEvent.cSentenceMerge
//            ChangeEvent.cSentenceSplit
            ChangeEvent.cSentenceDel,
            ChangeEvent.cSentenceAdd,
            ChangeEvent.cSentenceCopy
    );

    
    @Override
    public void handleChange(DataEvent aE) {
        if (cDisruptiveEvents.contains(aE.getId())) {
            // get para, invalidate form structures in that para
            final Para para = getPara((ChangeEvent) aE);
            
            // todo invalidate the structure containing this paragraph
            // invalidate the whole structure, todo invalidate just a layer or some subpart, might even modify it
        }
        
    }
    
    public Para getPara(ChangeEvent aE) {
        if (aE.form != null) return aE.form.getAncestor(Para.class);
        if (aE.sentence2 != null) return aE.sentence2.getAncestor(Para.class);

        throw new NullPointerException();
    }
    

}
