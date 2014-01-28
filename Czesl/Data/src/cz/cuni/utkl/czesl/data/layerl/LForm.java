package cz.cuni.utkl.czesl.data.layerl;

import cz.cuni.utkl.czesl.data.layerx.*;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Word at any l-layer.
 */
public class LForm extends FForm  {

    public LForm(@NonNull LLayer aLayer, @NonNull String aLocId, @NonNull Type type, String aToken) {
        super(aLayer, aLocId, type, aToken);
    }

    @Override
    public Sentence getParent() {
        return (Sentence) super.getParent(); //
    }
    
    @Override
    public LLayer getLayer() {
        return (LLayer)super.getLayer();
    }


}

