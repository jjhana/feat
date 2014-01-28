package org.purl.jh.pml;

//import org.netbeans.api.annotations.common.NonNull;

/**
 * Variable referring to another layer.
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class RefVar {
    /**
     * Id under which the external layer is references in this layer.
     */
    private final /*@NonNull*/ String id;
    private final String name;
    private final String href;

    /**
     * Instantiation of the href (layer has precedence over href)
     */
    private Layer<?> layer;

    /**
     * @param aId variable name under which the external layer is referenced
     * @param aName type of the external layer, e.g. wdata, mdata, etc.
     * @param aHref url of the external layer file
     */
    public RefVar(/*@NonNull*/ String aId, String aName, String aHref) {
        id = aId;
        name = aName;
        href = aHref;
    }

    /**
     * @param aId id under which the external layer is references in this layer
     * @param aName (type) of the external layer, e.g. wdata, mdata, etc.
     * @param aHref url of the external layer file
     * @param aLayer layer object this reference will points to.
     */
    public RefVar(/*@NonNull*/ String aId, String aName, String aHref, Layer<?> aLayer) {
        this(aId, aName, aHref);
        layer = aLayer;
    }

    /**
     * Id under which the external layer is references in this layer.
     */
    public String getId() {
        return id;
    }

    /**
     * Name (type) of the external layer, e.g. wdata, mdata, etc.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Name of the external layer file
     */
    public String getHRef() {
        return (layer == null) ? href : layer.getFile().getNameExt();
    }
    
    /**
     * Layer object this reference points to.
     */
    public void setLayer(Layer<?> aLayer) {
        layer = aLayer;
    }

    public Layer<?> getLayer() {
        return layer;
    }

    @Override
    public String toString() {
        return String.format("id=%s, name=%s, href=%s, layer=%s", id, name, href, layer);
    }

        
        
}
