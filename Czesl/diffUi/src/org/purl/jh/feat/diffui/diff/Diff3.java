package org.purl.jh.feat.diffui.diff;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import java.util.List;
import java.util.Collection;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.util.*;
import org.purl.jh.feat.diffui.diff.DiffUtil.EdgeFilter;
import org.purl.jh.feat.diffui.diff.FormEd.Op;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.location.Location;

/**
 *
 * todo under development, many things are ineffective, ugly, etc.
 * @author j
 */
public class Diff3 {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Diff3.class);
    
    private boolean optCompareComments = false;
    
    private final int doc1;
    private final int doc2;
    
    private final LLayer topLayerA;
    private final LLayer topLayerB;
    private final List<FormsLayer<?>> layers1;
    private final List<FormsLayer<?>> layers2;
        
    
    // matching organized by 1-layers
    final Map<FormsLayer<?>, Matching> layer2matching = new HashMap<>();

    public Diff3(LLayer aTopLayerA, LLayer aTopLayerB, int docA, int docB, boolean optCompareComments) {
        this.optCompareComments = optCompareComments;
        
        this.doc1 = docA;
        this.doc2 = docB;

        this.topLayerA = aTopLayerA;
        this.topLayerB = aTopLayerB;

        layers1 = DiffUtil.collectLayers(aTopLayerA, FormsLayer.class);
        layers2 = DiffUtil.collectLayers(aTopLayerB, FormsLayer.class);
    }
    
    /**
     * 
     * @return  matching organized by l-layers from the first document
     */
    public CombinedMatching calculateDiff() { 
        Err.iAssert(layers1.size() == layers2.size(), "Incompatible documents, different number of LLayers");
        
        for (int i = 0; i < layers1.size(); i++) {
            final FormsLayer<?> layer1 = layers1.get(i);
            final FormsLayer<?> layer2 = layers2.get(i);
            
            final Matching m = new Matching(layer1, layer2);
            layer2matching.put(layer1, m);
            layer2matching.put(layer2, m);
        }
        
        matchWLayers();
        
        for (int i = 1; i < layers1.size(); i++) {
            matchAndDiffLayer(layer2matching.get(layers1.get(i-1)), layer2matching.get(layers1.get(i)));
        }

        return new CombinedMatching(layer2matching, doc1, doc2);
    }
    
    /**
     * Todo under development
     * Refreshes diff for a single object (matching of objects is unaffected).
     * @param aObj
     * @param loMatching
     * @param aMatching 
     */
    public static void refreshDiff(CombinedMatching layer2matching, Element aObj) {
        
        final Matching matching = layer2matching.getLayerMatching(aObj);
        final boolean doc1 = matching.isInDoc1(aObj);
        final LayerEq eq = matching.getEq();
        
        if (aObj instanceof FForm) {
            FForm f1, f2;
            if (doc1) {
                f1 = (FForm)aObj;
                f2 = matching.getForms().getMatching2(f1);
            }
            else {
                f2 = (FForm)aObj;
                f1 = matching.getForms().getMatching1(f2);
            }

            // remove old differences (if any)
            if (f1 != null) {
                eq.hiMatching.getDifferences1().remove(Location.of(f1));
            }
            if (f2 != null) {
                eq.hiMatching.getDifferences2().remove(Location.of(f2));
            }

            // re-compare
            eq.eq(f1, f2);
        }
        else if (aObj instanceof Edge) {
            Edge e1, e2;
            if (doc1) {
                e1 = (Edge)aObj;
                e2 = matching.getEdges().getMatching2(e1);
            }
            else {
                e2 = (Edge)aObj;
                e1 = matching.getEdges().getMatching1(e2);
            }

            // remove old differences (if any)
            if (e1 != null) {
                eq.hiMatching.getDifferences1().remove(Location.of(e1));
            }
            if (e2 != null) {
                eq.hiMatching.getDifferences2().remove(Location.of(e2));
            }

            // re-compare
            eq.eq(e1, e2);
        }
    }

    /**
     * Creates matching and diff at the layer pair of hiMatching. 
     * At this time all lower layers have been matched (and diffed).
     * 
     * @param loMatching matching on the lower layer; already computed
     * @param hiMatching matching on this layer; to be filled
     */
    private void matchAndDiffLayer(final Matching loMatching, final Matching hiMatching) {
        final LLayer layer1 = (LLayer) hiMatching.getLayer1();
        final LLayer layer2 = (LLayer) hiMatching.getLayer2();

        final LayerEq eq = new LayerEq(loMatching, hiMatching, optCompareComments);
        hiMatching.setEq(eq);
        
        // compare one paragraph after another
        for (int docIdx=0; docIdx < layer1.col().size(); docIdx++) {
            final LDoc ldocA = layer1.get(docIdx);
            final LDoc ldocB = layer2.get(docIdx);

            for (int paraIdx=0; paraIdx < ldocA.col().size(); paraIdx++) {
                final LPara lparaA = ldocA.get(paraIdx);
                final LPara lparaB = ldocB.get(paraIdx);

                matchAndDiffPara(eq, lparaA, lparaB);
            }
        }
    }
    
    /**
     * Creates matching and diff for a single pair of paragraphs at a layer.. 
     * 
     * @param eq object managing comparisons and matchings for this layer
     * @param aParaA
     * @param aParaB 
     */
    private void matchAndDiffPara(final LayerEq eq, final LPara aParaA, final LPara aParaB) {
        matchForms(eq, aParaA, aParaB);
        diffForms( eq, aParaA, aParaB);
        matchAndDiffEdges(eq, aParaA, aParaB);
    }

    private void matchForms(final LayerEq eq, final LPara hiPara1, final LPara hiPara2) {
        final List<LForm> hiForms1 = hiPara1.getFormsList();
        final List<LForm> hiForms2 = hiPara2.getFormsList();
        
        final Matching matching = layer2matching.get(hiPara1.getLayer());

        final FormEd.Matcher<LForm> m = eq.fed.match(hiForms1, hiForms2);
        m.match();
        
        int i1 = 0;
        int i2 = 0;
        for (Op op : m.getOps()) {
            if (i1 >= hiForms1.size()) {
                System.out.println("Error i1 out: " + i1);
                break;
            }
            if (i2 >= hiForms2.size()) {
                System.out.println("Error i2 out: " + i2);
                break;
            }
            if (op == Op.sub) {
                matching.getForms().match(hiForms1.get(i1), hiForms2.get(i2));
                i1++; 
                i2++;
            }
            else if (op == Op.ins) {
                i2++;
            }
            else if (op == Op.del) {
                i1++;
            }
        }
    }

    private void diffForms(final LayerEq eq, final LPara aParaA, final LPara aParaB) {
        // todo remove after done
        for (FForm f1 : aParaA.getForms()) {
            final FForm f2 = eq.hiMatching.getForms().getMatching2(f1);
            log.info("XX: %s - %s (%s)", f1, f2, aParaA.getLayer().getId());
        }

        for (FForm f1 : aParaA.getForms()) {
            final FForm f2 = eq.hiMatching.getForms().getMatching2(f1);

            if (f2 == null) {
                eq.hiMatching.addDifference1(f1);
            }
            else {
                eq.eq(f1, f2);
            }
        }

        for (FForm f2 : eq.hiMatching.getForms().unmatched2s( com.google.common.collect.Sets.newHashSet(aParaB.getForms()) ) ) {
            eq.hiMatching.addDifference2(f2);
        }
    }
    
    /**
     * Matches and diffs layers in a single pair of paragraphs. 
     * The forms (in both this and the lower paragraph) have been already matched.
     * 
     * @param eq object managing comparisons and matchings for this layer
     * @param para1
     * @param para2 
     */
    private void matchAndDiffEdges(final LayerEq eq, final LPara para1, final LPara para2) {
        final Matching loMatching = eq.loMatching;
        final Matching hiMatching = eq.hiMatching;
              
        // match edges from hiPara1 with those hiPara2 using form matching on both hi and lo layers
        // todo currently works only with simple edges, does minimal matching
        // remove edges as they are matched 
        for (Edge e1 : para1.getEdges()) {

            // insert edge (there should be just one)
            if (e1.isInsert()) {
                final FForm hiForm1 = e1.getOneHigher();
                final FForm hiForm2 = hiMatching.getForms().getMatching2(hiForm1);
                
                if (hiForm2 != null) {
                    final Set<Edge> edges2 = new EdgeFilter(hiForm2.getLower()).insert().set();
                    
                    if (edges2.size() == 1) {
                        final Edge e2 = edges2.iterator().next();
                        hiMatching.getEdges().match(e1, e2);
                    }
                }
            }
            // delete edge
            else if (e1.isDelete()) {
                final FForm loForm1 = e1.getOneLower();
                final FForm loForm2 = hiMatching.getForms().getMatching2(loForm1);
                
                if (loForm2 != null) {
                    final Set<Edge> edges2 = new EdgeFilter(loForm2.getHigher()).delete().set();
                    
                    if (edges2.size() == 1) {
                        final Edge e2 = edges2.iterator().next();
                        hiMatching.getEdges().match(e1, e2);
                    }
                }
            }
            // simple 1:1 edge
            else if (e1.isSimple()) {
                final FForm hiForm1 = e1.getOneHigher();
                final FForm loForm1 = e1.getOneLower();

                final FForm loForm2 = loMatching.getForms().getMatching2(loForm1);
                final FForm hiForm2 = hiMatching.getForms().getMatching2(hiForm1);

                if (loForm2 != null && hiForm2 != null) {

                    final Set<Edge> edges2 = DiffUtil.edgesWith(loForm2, hiForm2);
                    if (edges2.size() == 1) {
                        final Edge e2 = edges2.iterator().next();
                        if (hiMatching.getEdges().getMatching1(e2) == null) { // not matched yet
                            hiMatching.getEdges().match(e1, e2);
                        }
                    }
                    // todo try to find one matching 
                }
            }
            // spider edge
            else {
                // todo handle 
            }
            
        }

        for (Map.Entry<Edge,Edge> ee : hiMatching.getEdges().getMatching() ) {
            eq.eq(ee.getKey(), ee.getValue());
        }
        
        // todo check unmatched edges in hiPara2
        // todo add all unmatched edges
        for (Edge ea : hiMatching.getEdges().unmatched1s( para1.getEdges() ) ) {
            hiMatching.addDifference1(ea);
        }
        for (Edge eb : hiMatching.getEdges().unmatched2s( para2.getEdges() ) ) {
            hiMatching.addDifference2(eb);
        }
    }



    /**
     * Performs the obvious matching between two identical copies of the same wlayer.
     * Performs basic checks the layers are really the same.
     */
    private void matchWLayers() {
        final WLayer wlayer1 = Cols.findElement(layers1, WLayer.class);
        final WLayer wlayer2 = Cols.findElement(layers2, WLayer.class);

        final Matching wMatch = layer2matching.get(wlayer1);
        
        Err.fAssert(wlayer1.col().size() == wlayer2.col().size(), "Compared documents do not have compatible wlayers");
        for (int docIdx=0; docIdx < wlayer1.col().size(); docIdx++) {
            final WDoc wdoc1 = wlayer1.get(docIdx);
            final WDoc wdoc2 = wlayer2.get(docIdx);

            Err.fAssert(wdoc1.col().size() == wdoc2.col().size(), "Compared documents do not have compatible wlayers");
            for (int paraIdx=0; paraIdx < wdoc1.col().size(); paraIdx++) {
                final WPara wpara1 = wdoc1.get(paraIdx);
                final WPara wpara2 = wdoc2.get(paraIdx);

                Err.fAssert(wpara1.getForms().size() == wpara2.getForms().size(), "Compared documents do not have compatible wlayers");
                for (int wIdx = 0; wIdx < wpara1.getForms().size(); wIdx++) {
                    WForm wForm1 = wpara1.getForms().get(wIdx);
                    WForm wForm2 = wpara2.getForms().get(wIdx);
                    
                    wMatch.getForms().match(wForm1, wForm2);
                }
            }
        }
    }

    

    

    
    
    
}
