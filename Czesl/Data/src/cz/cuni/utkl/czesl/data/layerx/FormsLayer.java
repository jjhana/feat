package cz.cuni.utkl.czesl.data.layerx;

import cz.cuni.utkl.czesl.data.layerw.WLayer;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;

/**
 * Superclass of layers used in Czel. Layers are organized into a chain.
 * @todo make FormsLayer<C extends Doc>
 * @author Jirka
 */
public abstract class FormsLayer<C extends Element> extends Layer<C> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(FormsLayer.class);
    
    // todo use aggreated light-weight list instead !!!
    //private final List<FForm> mForms = new ArrayList<FForm>();;  // computed from Doc -> Para -> FForm
//    private Map<String,FForm> mId2Forms = new HashMap<String,FForm>();  // id -> WForm

    /**
     * Prefix used in creating new ids.
     * Based on file name.
     * TODO: does this change when the file name changes?
     * TODO: do existing ids change when the file name changes? Optionally?
     */
    protected String idPrefix;
    
    /**
     * Index of the layer: 
     * <ul>
     * <li>Layer W - WLayer - Tier 0 - layerIdx = 0
     * <li>Layer A - LLayer - Tier 1 - layerIdx = 1
     * <li>Layer B - LLayer - Tier 2 - layerIdx = 2
     * </ul>
     */
    protected int layerIdx;
    
    /** 
     * Link to the lower FormsLayer if there is any. 
     * Null for the lowest FormsLayer, i.e. wlayer 
     */
    protected FormsLayer<?> lowerLayer;



    /**
     * Creates a new Layer.
     *
     * @param aId a unique identifier of the layer TODO not sure what the purpose and properties of this id are
     * @todo unique among layers or generally
     */
    public FormsLayer(FileObject aFile, String aId) {
        super(aFile, aId);
        idPrefix = aFile.getName() + "-";
    }

    protected void setLowerLayer(FormsLayer<?> aLowerLayer) {
        lowerLayer = aLowerLayer;
        layerIdx = lowerLayer.layerIdx + 1;
    }

// -----------------------------------------------------------------------------

    @Override
    public String getId() {
        return "" + layerIdx;
    }


    /**
     * Returns the layer closer to W-layer (plain-text layer).
     */
    public FormsLayer<?> getLowerLayer() {
        return lowerLayer;
    }

    /**
     * Returns all the lower forms layers plus this one ordered from the deepest to this one.
     * @todo precompute/cache?
     */
    public List<FormsLayer<?>> getLowerFormsLayersEq() {
        final List<FormsLayer<?>> layers = new ArrayList<>();
        
        for (FormsLayer<?> cur = this; cur != null; cur = cur.lowerLayer) {
            layers.add(0,cur);  // not very effective, but the number of layer is small
        }
        
        return layers;
    }
    
    public WLayer getWLayer() {
        FormsLayer<?> layer = this;
        for (;;) {
            if (layer instanceof WLayer) return (WLayer)layer;
            layer = layer.getLowerLayer();
        }
    }
    
    /**
     * Level of the layer. The lowest layer has index zero.
     * @return
     */
    public int getLayerIdx() {
        return layerIdx;
    }


// -----------------------------------------------------------------------------
// Forms
// -----------------------------------------------------------------------------

    public abstract <T extends FForm> Iterable<T> getForms();

    public abstract FForm getFirstForm();
    
    public abstract FForm getLastForm();

    //public abstract <T extends FForm> T getForm(T aForm, int aOffset);
    
    
//    public FForm getForm(String aId) {
//        return mId2Forms.get(aId);
//    }

    /**
     * Called by paragraph when a form is added to it.
     */
    public void addForm(FForm aForm) {
        //mForms.add(aForm);
//        mId2Forms.put(aForm.getId().getIdStr(), aForm);
    }




    /**
     * Optimize storage of information for memory and speed of access.
     * Use once it can be anticipated no inserts or deletions of children and
     * related computed structures (e.g. after reading the element from a file).
     * Override this function to optimize structures in a subclass (e.g.
     * rehashing has tables, triming growable arrays, etc.)
     */
    @Override
    public void optimizeForAccess() {
        super.optimizeForAccess();
        //((ArrayList)mForms).trimToSize();
//        mId2Forms = new HashMap<String,FForm>(mId2Forms);
    }

    // --- support for modifications -------------------------------------------

    public FForm addForm() {
        return null;
    }

// =============================================================================
// MVC modifications
// =============================================================================
        
    public boolean formEdit(final FForm aForm,  final String aText, final DataListener aSrcView, final Object aSrcInfo) {
        if (readOnly) return false;

        final String oldToken = aForm.getToken();
        aForm.setToken(aText);

        // fire updates
        final ChangeEvent<FormsLayer<C>> event = new ChangeEvent<>(this, ChangeEvent.cFormEdit, aSrcView, aSrcInfo);
        event.form = aForm;
        event.old = oldToken;

        fireEvents(event);
        return true;
    }
    
    /**
     * Any change to the form's properties except the actual form's text (currently only comment).
     *
     * TODO Currently requires write thru.
     * TODO use location
     * TODO allow adding comments??
     */
    public boolean formChange(final FForm aForm,  final DataListener aSrcView, final Object aSrcInfo) {
        if (readOnly) return false;

        // fire updates
        final ChangeEvent<FormsLayer<C>> event = new ChangeEvent<>(this, ChangeEvent.cFormChange, aSrcView, aSrcInfo);
        event.form = aForm;

        fireEvents(event);
        return true;
    }
    
    
    public abstract boolean formAdd(final String aStr, final Position aAnchor, final DataListener aSrcView, final Object aSrcInfo);

    public abstract boolean formDel(final FForm aForm, final DataListener aSrcView, final Object aSrcInfo);

    public abstract boolean formMove(final FForm aMovedForm, final Position aAnchor, final DataListener aSrcView, final Object aSrcInfo);
    
    /**
     * FForm must stay within the same paragraph.
     * Note: the moved form is always moved into the sentence of the anchor
     * @param aMovedForm
     * @param aAnchor target anchor. The aMovedForm is moved into the anchor's form's sentence.
     * @param aApproxPos
     * @param aSrcView
     * @param aSrcInfo
     */
    public abstract boolean formMove(final List<? extends FForm> aMovedForms, final Position aAnchor, final DataListener aSrcView, final Object aSrcInfo);

    /**
     * Checks if all the elements are at this layer.
     * @param aEls
     * @return 
     */
    protected boolean allHere(Iterable<? extends Element> aEls) {
        for (Element el : aEls) {
            if (this != el.getLayer()) return false;
        }
        return true;
    }
    
    
    // only to produce report, remove once bugs found
    @Override
    public void fireEvents(final DataEvent<?> aDataEvent) {
        log.info("Firing %s", aDataEvent);
        log.info("" + getForms());
        
        setModified();
        final Object[] list = listeners.getListenerList();
        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = list.length - 2; i >= 0; i -= 2) {
            if (list[i] == DataListener.class) {
                ((DataListener) list[i + 1]).handleChange(aDataEvent);
            }
        }

        log.fine("Done Firing %s", aDataEvent);
    }    
    
}

