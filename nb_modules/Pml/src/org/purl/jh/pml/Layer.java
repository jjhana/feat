package org.purl.jh.pml;

import java.util.*;
import org.purl.jh.util.str.Strings;
import org.purl.jh.util.err.Err;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.ts.Tagset;


/**
 * Layer of annotation.
 *
 * @param <C> type of child elements
 */
public abstract class Layer<C extends Element> extends Data<C> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Layer.class);

//    public static char getLayerTypeId() {return 0;}


    /** All registered ided elements in this layer. */
    protected final transient Map<String,IdedElement> id2element = new TreeMap<>();   // not saved
    
    /** variables used to reference other layers; var -> <var,name,href> */
    protected final Map<String,RefVar> refVars = new HashMap<>();

    protected final List<RefVar> missingLayers = new ArrayList<>();
    
    
    /**
     * Maps layer references to layers.
     */
    private final static org.purl.jh.util.col.Mapper<RefVar,Layer<?>> refVar2Layer = new org.purl.jh.util.col.Mapper<RefVar,Layer<?>>() {
        @Override public Layer<?> map(RefVar aOrigItem) {
            return aOrigItem.getLayer();
        }
    };

    /** View of reference variables ({@link #refVars}) as a simple collection of referenced layers */
    protected final Collection<Layer<?>> refLayers = new org.purl.jh.util.col.MappingCollection<>(refVars.values(), refVar2Layer);

    protected final Map<String,Tagset<?>> use2Tagset = new HashMap<>();


// -----------------------------------------------------------------------------
//    
// -----------------------------------------------------------------------------
    
    /**
     * Creates a new Layer.
     *
     * @param aFile
     * @param aId a unique (??) identifier of the layer
     * @todo unique among layers or generally
     */
    public Layer(FileObject aFile, String aId) {
        file = aFile;
        id = aId;
    }


// -----------------------------------------------------------------------------
// References
// -----------------------------------------------------------------------------

    public void addRefVar(final RefVar aRefVar) {
        refVars.put( aRefVar.getId(), aRefVar );
    }

//    /**
//     * Use the layer's id as the id of the reference.
//     */
//    public void addRefVar(Layer aLayer, String aInternalId, String aName) {
//        RefVar ref = refVars.get(aInternalId);
//        Err.iAssert(ref == null || ref.getName().equals(aName), "Replacing reference with a different layer type");
//
//        refVars.put( aInternalId, new RefVar(aInternalId, aName, aLayer) );
//    }

    /**
     * Returns a map of referenced layers.
     * @return
     */
    public Map<String,RefVar> getReferences() {
        return refVars;
    }

    /**
     * Can return null if a layer is optional (todo filter?)
     * @return 
     */
    public Collection<Layer<?>> getReferencedLayers() {
        return refLayers;
    }

    public Collection<Layer<?>> getTransitivelyReferencedLayers() {
        final List<Layer<?>> layers = new ArrayList<>();
        getTransitivelyReferencedLayers(this, layers);
        return layers;
    }

    private void getTransitivelyReferencedLayers(Layer<?> aLayer, List<Layer<?>> aAccumulator) {
        for (Layer<?> l : aLayer.getReferencedLayers()) {
            aAccumulator.add(l);
            getTransitivelyReferencedLayers(l,aAccumulator);
        }
     }


    /**
     * Returns a list of referenced layers with a particular name.
     * 
     * @param aName layer name. Note that it is not layer id (see {@link RefVar} for more info).
     * @return list of refvar objects linking to the layers with the specified name.
     */
    public List<RefVar> getReferences(String aName) {
        final List<RefVar> result = new ArrayList<>();
        
        for (RefVar ref : getReferences().values()) {
            if (ref.getName().equals(aName)) result.add(ref);
        }

        return result;
    }

    public RefVar getRefVar(Layer<?> aLayer) {
        for (RefVar ref : getReferences().values()) {
            if (ref.getLayer() == aLayer) return ref;
        }
        return null;
    }

    /**
     * Creates a refering id, possibly outside of this layer.
     *
     * @param aElement element to refer to (can be in this layer or a referenced layer)
     * @return
     */
    public String getRef(IdedElement aElement) {
        if (aElement.getId().getLayer() != this) {
            RefVar refVar = getRefVar(aElement.getId().getLayer());

            return refVar.getId() + "#" + aElement.getId().getLocalId();

        }
        else {
            return aElement.getId().getLocalId();
        }
    }

    /**
     * Returns the layer immediately above the specified. 
     *
     * @param aLayer layer to look above
     * @return layer above aLayer, null if there is not such a layer
     */
    public Layer<?> getLayerAbove(final Layer<?> aLayer) {
        for (RefVar ref : getReferences().values()) {
            if (ref.getLayer() == aLayer) return this;

            final Layer<?> tmp = ref.getLayer().getLayerAbove(aLayer);
            if (tmp != null) return tmp;
        }
        
        return null;
    }

    public List<RefVar> getMissingLayers() {
        return missingLayers;
    }

    
// -----------------------------------------------------------------------------
// Tagsets
// -----------------------------------------------------------------------------

    public void addTagset(String aUse, Tagset<?> aTagset) {
        use2Tagset.put(aUse, aTagset);
    }

    /**
     * Do not use to modify the set of tagsets;
     * todo return unmofiable collection
     */
    public Map<String,Tagset<?>> getTagsets() {
        return use2Tagset;
    }

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
    
    /**
     * Registers an ided element this layer contains (directly or indirectly). 
     * Call before adding any such element.
     * @param aElement element to register
     */
    public void addIdedElement(IdedElement aElement) {
        id2element.put(aElement.getId().getIdStr(), aElement);
    }

    /**
     * Deregisters an ided element this layer contained (directly or indirectly). 
     * Call after removing any such element.
     * @param aElement element to remove from registry 
     */
    public IdedElement removeIdedElement(IdedElement aElement) {
        return id2element.remove(aElement.getId().getIdStr());
    }


    /**
     * Returns the element corresponding to the id, either on this or an external
     * referenced layer.
     *
     * @param <E> type of the element
     * @param aId full or local id
     * @return element corresponding to the id
     */
    public <E extends IdedElement> E getElement(String aId) {
        final int poundIdx = aId.indexOf('#');
        if (poundIdx != -1) {
            String layerVar = aId.substring(0, poundIdx);
            RefVar ref = refVars.get(layerVar);
            // todo assert
            Layer<?> layer = ref.getLayer();
            // todo assert
            return layer.<E>getElement(aId.substring(poundIdx+1)); // todo why not layer.<E>getElement ???
        }
        else {
            return (E) id2element.get(aId);
        }
    }

    /**
     * Returns TODO with the element corresponding to the id, either on this or an external
     * referenced layer.
     *
     * @param <XE> type of the element
     * @param <XP> type of the parent of the reference
     * @param aId full or local id
     * @return 
     */
    public <XE extends IdedElement, XP extends Element> Ref<XE,XP> getRef(String aId) {
        final int poundIdx = aId.indexOf('#');
        if (poundIdx != -1) {
            String layerVar = aId.substring(0, poundIdx);
            RefVar ref = refVars.get(layerVar);
            // todo assert
            Layer<?> layer = ref.getLayer();
            // todo assert
            return (Ref<XE,XP>) layer.getRef(aId.substring(poundIdx+1)); // todo why cannot the types be is the cast needed
        }
        else {
            XE el = (XE) id2element.get(aId);
            return new Ref<>(this, el);
        }
    }

    
    /**
     * Creates a unique object id.
     * The uniqueness is ensured only within this layer.
     * If the id is used, it must be registered.
     *
     * @param aSuggestedId id   NOT TRUE: without the layer prefix (the prefix is added automatically)
     */
    public String getUniqueId(String aSuggestedId) {
        return getUniqueIdE(aSuggestedId);
    }

    /**
     * Creates a unique object id.
     * If the id is used, it must be registered.
     */
    public String getUniqueId(final IdedElement aIdParent, final String aSuffix, final int aCounterStart) {
        return getUniqueId(aIdParent.getId().getIdStr() + aSuffix, aCounterStart);
    }

    /**
     * Creates a unique object id.
     * If the returned id is used, it must be registered via {@link #addIdedElement(org.purl.jh.pml.IdedElement)}.
     */
    public String getUniqueId(final String aPrefix, final int aCounterStart) {
        for(int counter = aCounterStart;;counter++) {
            String newId = aPrefix + counter;
            if (!id2element.keySet().contains(newId)) return newId;
        }
    }



    /**
     * Creates a unique object id.
     * If the id is used, it must be registered.
     */
    public String getUniqueIdE(String aSuggestedId) {
        return Strings.findUniqueId(aSuggestedId, id2element.keySet(), "-");
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        String str = "Layer " + id ;
        if (file != null) str += " (" + file.toString() + ")";
        return str;
    }
    
}
