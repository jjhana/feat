package org.purl.jh.feat.profiles;

import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.util.List;
import lombok.Data;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.purl.jh.pml.Layer;

@Data
public class Profile {
    private final String id;
    private final String desc;
    private final Dictionary spellchecker;
    // todo number of layers
    // layer properties - tags, read only, sentence boundaries?

    private final List<LayerInfo> layerInfos;
    
    /** 
     * Returns layer info for a given layer.
     * 
     * @param layer layer to return info for
     * @return layer info if available (only FormsLayers are supported) or null if not.
     */
    public LayerInfo getLayerInfo(Layer layer) {
        if (layer instanceof FormsLayer) {
            int idx = ((FormsLayer)layer).getLayerIdx();
            if (idx <= idx && idx < layerInfos.size()) return layerInfos.get(idx);
        }
        
        return null;
    }

    /** 
     * Returns the error tagset for a given layer.
     * A convenience method.
     * 
     * @param layer layer to return info for
     * @return tagset if available (i.e. the info about that layer is available, and it contains info about the tagset) or null if not.
     */
    public ErrorTagset getTagset(Layer layer) {
        LayerInfo layerInfo = getLayerInfo(layer);
        return layerInfo == null ? null : layerInfo.getTagset();
    }

}
