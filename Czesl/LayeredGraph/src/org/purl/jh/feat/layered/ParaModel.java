
package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerx.Para;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Read-only Stacked model behind the LayeredGraph.
 * This model serves as an adaptor to actual data objects.
 * <p>
 * In the model, for every layer there is maximally one higher layer. In general,
 * there might be multiple.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class ParaModel {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ParaModel.class);

    /** Enclosing pseudomodel (containing all paras, not jsut this one) */
    private final VModel pseudoModel;

// --- Current paragraph ---
    /**
     * Current paragraph across layers.
     * @todo 'Current' should be only in the view,
     */
    private final List<Para> paras;

    /**
     * Current paragraph at the highest layer
     * @todo 'Current' should be only in the view,
     */
    private final Para curTopPara;    

    /**
     * Forms in the current paragraph, linearly organized
     * @todo 'Current' should be only in the view,
     */
    private final List<List<? extends FForm>> layer2forms = new ArrayList<>();
        
// --- ---


    /**
     *
     * @param aLayers list of all layers ordered from the lowest to the highest
     * @param aParas list of all paragraphs at the highest layer
     * @param aTopPara the current para in the highest layer
     */
    public ParaModel(VModel aPseudoModel, final Para aTopPara) {
        pseudoModel = aPseudoModel;
        curTopPara = aTopPara;

        paras = curTopPara.getLowerEqParas();

        fillForms();
    }

    public VModel getPseudoModel() {
        return pseudoModel;
    }


    /**
     * The current paragraph as represented across layers.
     * The paragraph of the lowest layer comes first.
     */
    public List<Para> getParas() {
        return paras;
    }



    // todo this is a mess, it relies on the order, adding forms differently than the layer. Make nicer.
    // todo sublists don't like modifications of the underlying list!!!
    // todo and the underlying list cannot be modified before/after the sublist list
    protected void fillForms() {
        log.info("=== filling forms ===");
        layer2forms.clear();

        for (Para para = curTopPara; para != null;) {
            log.fine("   para: %s", para);
            layer2forms.add(0, para.getFormsList());    // todo something better
            if (para instanceof LPara) {
                para = ((LPara)para).getLowerPara();
            }
            else {
                para = null;
            }
        }
        log.fine("=== end filling forms ===");
    }

    // todo this is a mess, it relies on the order, adding forms differently than the layer. Make nicer.
    // todo sublists don't like modifications of the underlying list!!!
    // todo and the underlying list cannot be modified before/after the sublist list
//    protected void fillForms(Para aPara) {
//        log.info("=== filling para forms ===");
//        int layerIdx = pseudoModel.getLayerIdx( (FormsLayer<?>) aPara.getLayer() );
//        layer2forms.set(layerIdx, aPara.getForms());    
//        log.fine("=== end filling para forms ===");
//    }

// =============================================================================    
// Paragraph specific
// =============================================================================    
    
    /**
     * Returns the layered nodes within the current paragraph.
     *
     * @see #getLayerIdx(cz.cuni.utkl.czesl.data.layerx.FormsLayer) for the row index
     */
    public List<List<? extends FForm>> getNodes() {
        ensureCurrent(-1);
        return layer2forms;
    }

    /**
     * Returns a layer of nodes within the current paragraph.
     *
     * @see #getLayerIdx(cz.cuni.utkl.czesl.data.layerx.FormsLayer) for the row index
     */
    public <F extends FForm> List<F> getNodes(int aLayerIdx) {
        ensureCurrent(aLayerIdx);
        return (List<F>) layer2forms.get(aLayerIdx);
    }

    public <F extends FForm> List<F> getNodes(FormsLayer<?> aLayer) {
        return getNodes(pseudoModel.getLayerIdx(aLayer));
    }

    public Collection<Edge> getEdges(int aHigherLayerIdx) {
        return ((LPara)paras.get(aHigherLayerIdx)).getEdges();
    }


    private void ensureCurrent(int aLayerIdx) {
        // todo check if modified
        fillForms(); // todo temporary, horribly ineffective
        
    }
    
}
