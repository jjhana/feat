package cz.cuni.utkl.czesl.data.layerl;

import cz.cuni.utkl.czesl.data.layerx.Doc;
import java.util.List;
import org.netbeans.api.annotations.common.NonNull;
import org.purl.jh.pml.Id;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.AbstractListElement;

/**
 *
 * @author Jirka Hana
 */
public class LDoc extends AbstractListElement<LPara> implements Doc {
    private final Id id;
    private final Doc lowerDoc;

    public LDoc(@NonNull Layer<?> aLayer, @NonNull String aLocId, Doc aLowerDoc) {
        id = new Id(aLayer, aLocId);
        lowerDoc = aLowerDoc;
    }

    @Override
    public Id getId() {
        return id;
    }

    public Doc getLowerDoc() {
        return lowerDoc;
    }

    @Override
    public List<LPara> getParas() {
        return col();
    }



}
