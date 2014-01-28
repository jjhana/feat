package org.purl.jh.pml;

import lombok.Getter;
import lombok.Setter;
import org.openide.filesystems.FileObject;
import org.purl.jh.util.err.Err;

/**
 * A layer with a single sublayer.
 * @author j
 */
public abstract class StackedLayer<L extends Layer<?>, C extends Element> extends Layer<C> {
    @Getter @Setter L lowerLayer;
    @Getter int layerIdx;
    
    public StackedLayer(FileObject aFile, String aId) {
        super(aFile, aId);
    }

    @Override
    public void addRefVar(final RefVar refVar) {
        Err.iAssert(refVar.getLayer() != null, "Referenced layer cannot be null (%s)",      refVar);
        Err.iAssert(getLowerLayer()   == null, "Cannot add more than one lower layer (%s)", refVar);

        super.addRefVar(refVar);

        lowerLayer = (L)refVar.getLayer();
        layerIdx = lowerLayer instanceof StackedLayer ? ((StackedLayer)lowerLayer).getLayerIdx() + 1 : 1;
    }
    
//    /**
//     * Returns all the lower forms layers plus this one ordered from the deepest to this one.
//     * @todo precompute/cache?
//     */
//    public List<Layer<?>> getLowerFormsLayersEq() {
//        final List<Layer<?>> layers = new ArrayList<>();
//        
//        for (Layer<?> cur = this; cur != null; cur = cur.getLowerLayer()) {
//            layers.add(0,cur);  // not very effective, but the number of layer is small
//        }
//        
//        return layers;
//    }
    
    /**
     * Gets the first layer under a chain of stacked layers.
     * @return 
     */
    public Layer<?> getBottomLayer() {
        Layer<?> layer = this;
        for (;;) {
            if (!(layer instanceof StackedLayer)) return (Layer<?>)layer;
            layer = ((StackedLayer<?,?>)layer).getLowerLayer();
        }
    }
    
    
    
}
