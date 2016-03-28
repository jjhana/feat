package cz.cuni.utkl.czesl.data.layerl;

import com.google.common.collect.ImmutableList;
import cz.cuni.utkl.czesl.data.layerx.*;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Word at any l-layer.
 */
public class LForm extends FForm  {

    public LForm(@NonNull LLayer aLayer, @NonNull String aLocId, @NonNull Type type, String aToken) {
        this(aLayer, aLocId, type, aToken, ImmutableList.<org.jdom.Element>of());
    }
    
    public LForm(@NonNull LLayer aLayer, @NonNull String aLocId, @NonNull Type type, String aToken, ImmutableList<org.jdom.Element> other) {
        super(aLayer, aLocId, type, aToken, other);
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

