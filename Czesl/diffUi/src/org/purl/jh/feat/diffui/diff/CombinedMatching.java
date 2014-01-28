package org.purl.jh.feat.diffui.diff;

import com.google.common.base.Preconditions;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.location.Location;
import org.purl.jh.util.err.Err;

/**
 * Matching across several layers (intended for the whole document).
 * 
 * @author j
 */
public class CombinedMatching implements IMatching {
    private final int docA;
    private final int docB;

    /**
     * Matching split by layers, organized by both annotator's layers.
     */
    private final Map<FormsLayer<?>, Matching> layer2matching;

    private final Set<Location> differencesA = new HashSet<>();
    private final Set<Location> differencesB = new HashSet<>();
    
    public CombinedMatching(Map<FormsLayer<?>, Matching> layer2matching, int docA, int docB) {
        this.layer2matching = layer2matching;
        this.docA = docA;
        this.docB = docB;
        
        for (Matching m : layer2matching.values()) {
            //forms.matchAll(m.getForms());
            //edges.matchAll(m.getEdges());
            differencesA.addAll(m.getDifferences1());   // todo drop, calculate on the fly, otherwise not updated 
            differencesB.addAll(m.getDifferences2());
        }
    }

    public Matching getLayerMatching(Element el) {
        final Layer<?> layer = el.getLayer();
        Err.iAssert(layer instanceof FormsLayer, "Matching exists only for FormsLayers (obj=%s, layer=%s)", el, layer);

        return  layer2matching.get(layer);
        
    }
    
    @Override
    public Set<Location> getDifferences1() {
        return differencesA;
    }

    @Override
    public Set<Location> getDifferences2() {
        return differencesB;
    }

    private void checkDocIdx(int docIdx) {
        Preconditions.checkArgument(docIdx == docA || docIdx == docB);
    }

    private void checkElement(Element el) {
        Preconditions.checkArgument(el instanceof FForm || el instanceof Edge);
    }
    
    public <T extends Element> T getMatching(final T el, int srcDoc) {
        checkElement(el);
        checkDocIdx(srcDoc);
        if (el instanceof FForm) {
            return (T)this.getMatching((FForm)el, srcDoc);
        }
        else {
            return (T)this.getMatching((Edge)el, srcDoc);
        }
    }
    
    public FForm getMatching(final FForm form, int srcDoc) {
        checkDocIdx(srcDoc);
        if (srcDoc == docA) {
            return getBMatching(form);
        }
        else {
            return getAMatching(form);
        }
    }

    public Edge getMatching(final Edge edge, int srcDoc) {
        checkDocIdx(srcDoc);
        if (srcDoc == docA) {
            return getBMatching(edge);
        }
        else {
            return getAMatching(edge);
        }
    }
    
    public FForm getBMatching(final FForm form1) {
        final FormsLayer<?> layer = form1.getLayer();
        return layer2matching.get(layer).getForms().getMatching2(form1);
    }

    public Edge getBMatching(final Edge edge1) {
        final FormsLayer<?> layer = edge1.getLayer();
        return layer2matching.get(layer).getEdges().getMatching2(edge1);
    }

    
    public FForm getAMatching(final FForm form1) {
        final FormsLayer<?> layer = form1.getLayer();
        return layer2matching.get(layer).getForms().getMatching1(form1);
    }

    public Edge getAMatching(final Edge edge1) {
        final FormsLayer<?> layer = edge1.getLayer();
        return layer2matching.get(layer).getEdges().getMatching1(edge1);
    }
    
    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param aAItems
     * @return 
     */
    public Set<FForm> getBFormMatching(final Iterable<? extends FForm> aAItems) {
        Set<FForm> bItems = new HashSet<>();
        for (FForm a : aAItems) {
            FForm b = getBMatching(a);
            if (b != null) bItems.add(b);
        }
        return bItems;
    }

    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param aBItems
     * @return 
     */
    public Set<FForm> getAFormMatching(final Iterable<? extends FForm> aBItems) {
        Set<FForm> aitems = new HashSet<>();
        for (FForm b : aBItems) {
            FForm a = getAMatching(b);
            if (a != null) aitems.add(a);
        }
        return aitems;
    }
    
    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param aAItems
     * @return 
     */
    public Set<Edge> getBEdgeMatching(final Iterable<? extends Edge> aAItems) {
        Set<Edge> bItems = new HashSet<>();
        for (Edge a : aAItems) {
            Edge b = getBMatching(a);
            if (b != null) bItems.add(b);
        }
        return bItems;
    }

    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param aBItems
     * @return 
     */
    public Set<Edge> getAEdgeMatching(final Iterable<? extends Edge> aBItems) {
        Set<Edge> aitems = new HashSet<>();
        for (Edge b : aBItems) {
            Edge a = getAMatching(b);
            if (a != null) aitems.add(a);
        }
        return aitems;
    }

    @Override
    public boolean isInDoc1(Element el) {
        return layer2matching.containsKey(el.getLayer());
    }
    
}
