//package org.purl.jh.feat.diffui.diff;
//
//import cz.cuni.utkl.czesl.data.layerl.*;
//import cz.cuni.utkl.czesl.data.layerw.WDoc;
//import cz.cuni.utkl.czesl.data.layerw.WForm;
//import cz.cuni.utkl.czesl.data.layerw.WLayer;
//import cz.cuni.utkl.czesl.data.layerw.WPara;
//import cz.cuni.utkl.czesl.data.layerx.Para;
//import java.util.List;
//import java.util.Collection;
//import cz.cuni.utkl.czesl.data.layerx.Form;
//import org.purl.jh.util.col.Cols;
//import org.purl.jh.util.err.Err;
//import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
//import java.util.*;
//import org.purl.jh.feat.diffui.diff.FormEd.Op;
//
///**
// *
// * todo under development, many things are ineffective, ugly, etc.
// * @author j
// */
//public class Diff2 {
//    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Diff2.class);
//    
//    private final LLayer topLayer1;
//    private final LLayer aTopLayer2;
//    private final List<FormsLayer<?>> layers1;
//    private final List<FormsLayer<?>> layers2;
//        
//    // matching organized by 1-layers
//    final Map<FormsLayer<?>, Matching> layer2matching = new HashMap<FormsLayer<?>, Matching>();
//
//    public Diff2(LLayer aTopLayer1, LLayer aTopLayer2) {
//        this.topLayer1 = aTopLayer1;
//        this.aTopLayer2 = aTopLayer2;
//
//        layers1 = DiffUtil.collectLayers(aTopLayer1, FormsLayer.class);
//        layers2 = DiffUtil.collectLayers(aTopLayer2, FormsLayer.class);
//    }
//    
//    
//    public Map<FormsLayer<?>, Matching> calculateDiff() { 
//        calculateMatching();
//
//        calculateDifferences();
//        
//        return layer2matching;
//    }
//
//    private void calculateDifferences() {
//        for (Matching matching : layer2matching.values()) {
//            final FormsLayer<?> layerA = matching.getLayerA();
//            final FormsLayer<?> layerB = matching.getLayerB();
//            calculateFormDifferences(matching, layerA, layerB);
//            
//            if (layerA instanceof LLayer) {
//                calculateEdgeDifferences(matching, (LLayer)layerA, (LLayer)layerB);
//            }
//        }
//    }
//
//    private void calculateFormDifferences(final Matching matching, final FormsLayer<?> layerA, final FormsLayer<?> layerB) {
//        for (Form f1 : layerA.getForms()) {
//            Form f2 = matching.getForms().getBMatching(f1);
//            log.info("XX: %s - %s (%s)", f1, f2, layerA.getId());
//        }
//
//        for (Form f1 : layerA.getForms()) {
//            Form f2 = matching.getForms().getBMatching(f1);
//
//            if (f2 == null) {
//                matching.addADifference(f1);
//            }
//            else {
//                // todo there is more than just token
//                if (!com.google.common.base.Objects.equal(f1.getToken(), f2.getToken())) {
//                    matching.addADifference(f1);
//                    matching.addBDifference(f2);
//                }
//                // todo other attributes, make configurable
//            }
//        }
//
//        for (Form f2 : matching.getForms().unmatchedBs( com.google.common.collect.Sets.newHashSet(layerB.getForms()) ) ) {
//            matching.addBDifference(f2);
//        }
//    }
//
//    private void calculateEdgeDifferences(final Matching matching, final LLayer layerA, final LLayer layerB) {
////        for (Form f1 : layerA.getEdges()) {
////            Form f2 = matching.getForms().getBMatching(f1);
////            log.info("XX: %s - %s (%s)", f1, f2, layerA.getId());
////        }
//
//        for (Edge e1 : DiffUtil.edges(layerA)) {
//            Edge e2 = matching.getEdges().getBMatching(e1);
//
//            if (e2 == null) {
//                matching.addADifference(e1);
//            }
//            else {
//                if (!eq(e1, e2)) {
//                    matching.addADifference(e1);
//                    matching.addBDifference(e2);
//                }
//                // todo other attributes, make configurable
//            }
//        }
//
//        for (Form f2 : matching.getForms().unmatchedBs( com.google.common.collect.Sets.newHashSet(layerB.getForms()) ) ) {
//            matching.addBDifference(f2);
//        }
//    }
//    
//
//    
//    private void matchDiffEdges(Para loPara1, Para loPara2, LPara hiPara1, LPara hiPara2) {
//        final Matching loMatching = layer2matching.get(loPara1.getLayer());
//        final Matching hiMatching = layer2matching.get(hiPara1.getLayer());
//              
//        // match edges from hiPara1 with those hiPara2 using form matching on both hi and lo layers
//        // todo currently works only with simple edges, does minimal matching
//        for (Edge eA : hiPara1.getEdges()) {
//            
//            if (eA.getLower().isEmpty() && eA.getHigher().size() == 1) {
//                final Form hiForm = eA.getHigher().iterator().next();
//                // todo
//            }
//            else if (eA.getLower().size() == 1 && eA.getHigher().isEmpty()) {
//                final Form loForm = eA.getLower().iterator().next();
//                // todo
//            }
//            else if (eA.getLower().size() == 1 && eA.getHigher().size() == 1) {
//                final Form hiFormA = eA.getHigher().iterator().next();
//                final Form loFormA = eA.getLower() .iterator().next();
//
//                final Form loFormB = loMatching.getForms().getBMatching(loFormA);
//                final Form hiFormB = hiMatching.getForms().getBMatching(hiFormA);
//                
//                
//                final Set<Edge> edgesB = DiffUtil.edgesWith(loFormB, hiFormB);
//                if (edgesB.size() == 1) {
//                    final Edge eB = edgesB.iterator().next();
//                    if (hiMatching.getEdges().getAMatching(eB) == null) { // not matched yet
//                        hiMatching.getEdges().match(eA, eB);
//                        
//                        // todo compare
//                    }
//                }
//                // todo try to find one matching 
//            }
//            else {
//                // todo handle 
//            }
//            
//        }
//
//        // todo add all unmatched edges
//        for (Edge ea : hiMatching.getEdges().unmatchedBs( hiPara1.getEdges() ) ) {
//            hiMatching.addADifference(ea);
//        }
//        for (Edge eb : hiMatching.getEdges().unmatchedBs( hiPara2.getEdges() ) ) {
//            hiMatching.addBDifference(eb);
//        }
//    }
//    
//    
//    public void calculateMatching() {
//
//        Err.iAssert(layers1.size() == layers2.size(), "Incompatible documents, different number of LLayers");
//        
//        for (int i = 0; i < layers1.size(); i++) {
//            FormsLayer<?> layer1 = layers1.get(i);
//            FormsLayer<?> layer2 = layers2.get(i);
//            layer2matching.put(layer1, new Matching(layer1, layer2));
//        }
//        
//        WLayer wlayer1 = Cols.findElement(layers1, WLayer.class);
//        WLayer wlayer2 = Cols.findElement(layers2, WLayer.class);
//
//        matchWLayers(wlayer1, wlayer2);
//        matchParas();
//    }
//
//    
//    private void matchWLayers(WLayer wlayer1, WLayer wlayer2) {
//        final Matching wMatch = layer2matching.get(wlayer1);
//        
//        Err.uAssert(wlayer1.col().size() == wlayer2.col().size(), "Compared documents do not have compatible wlayers");
//        for (int docIdx=0; docIdx < wlayer1.col().size(); docIdx++) {
//            final WDoc wdoc1 = wlayer1.get(docIdx);
//            final WDoc wdoc2 = wlayer2.get(docIdx);
//
//            Err.uAssert(wdoc1.col().size() == wdoc2.col().size(), "Compared documents do not have compatible wlayers");
//            for (int paraIdx=0; paraIdx < wdoc1.col().size(); paraIdx++) {
//                final WPara wpara1 = wdoc1.get(paraIdx);
//                final WPara wpara2 = wdoc2.get(paraIdx);
//
//                Err.uAssert(wpara1.getForms().size() == wpara2.getForms().size(), "Compared documents do not have compatible wlayers");
//                for (int wIdx = 0; wIdx < wpara1.getForms().size(); wIdx++) {
//                    WForm wForm1 = wpara1.getForms().get(wIdx);
//                    WForm wForm2 = wpara2.getForms().get(wIdx);
//                    
//                    wMatch.getForms().match(wForm1, wForm2);
//                }
//            }
//        }
//    }
//
//    /** Go horizontally thru all paras. */
//    private void matchParas() {
//        // compare one paragraph after another
//        for (int docIdx=0; docIdx < topLayer1.col().size(); docIdx++) {
//            final LDoc ldoc1 = topLayer1.get(docIdx);
//            final LDoc ldoc2 = aTopLayer2.get(docIdx);
//
//            for (int paraIdx=0; paraIdx < ldoc1.col().size(); paraIdx++) {
//                final LPara lpara1 = ldoc1.get(paraIdx);
//                final LPara lpara2 = ldoc2.get(paraIdx);
//
//                final ParaStruct paraStruct1 = new ParaStruct(lpara1); 
//                final ParaStruct paraStruct2 = new ParaStruct(lpara2); 
//
//                matchParaStack(paraStruct1, paraStruct2);
//            }
//        }
//    }
//    
//    /** Go vertically thru all paras in the current parastack. */
//    private void matchParaStack(final ParaStruct aParaStruct1, final ParaStruct aParaStruct2) {
//        Para loPara1 = aParaStruct1.getWpara();
//        Para loPara2 = aParaStruct2.getWpara();
//        
//        for (int i = 0; i < aParaStruct1.getLparas().size(); i++) {
//            //Err.iAssert(i < 2, "too many layers");
//
//            LPara hiPara1 = aParaStruct1.getLparas().get(i);
//            LPara hiPara2 = aParaStruct2.getLparas().get(i);
//            
//            matchAdjPara(aParaStruct1, aParaStruct2, loPara1, loPara2, hiPara1, hiPara2);
//        }
//    }
//
//    private void matchAdjPara(final ParaStruct aParaStruct1, final ParaStruct aParaStruct2, final Para loPara1, final Para loPara2, final LPara hiPara1, final LPara hiPara2) {
//        matchForms(hiPara1, hiPara2);
//        matchEdges(loPara1, loPara2, hiPara1, hiPara2);
//        // todo match sentences
//    }
//
//    private void matchEdges(Para loPara1, Para loPara2, LPara hiPara1, LPara hiPara2) {
//        final Matching loMatching = layer2matching.get(loPara1.getLayer());
//        final Matching hiMatching = layer2matching.get(hiPara1.getLayer());
//              
//        // match edges from hiPara1 with those hiPara2 using form matching on both hi and lo layers
//        // todo currently works only with simple edges, does minimal matching
//        for (Edge eA : hiPara1.getEdges()) {
//            
//            if (eA.getLower().isEmpty() && eA.getHigher().size() == 1) {
//                final Form hiForm = eA.getHigher().iterator().next();
//                // todo
//                
//            }
//            else if (eA.getLower().size() == 1 && eA.getHigher().isEmpty()) {
//                final Form loForm = eA.getLower().iterator().next();
//                // todo
//                
//            }
//            else if (eA.getLower().size() == 1 && eA.getHigher().size() == 1) {
//                final Form hiFormA = eA.getHigher().iterator().next();
//                final Form loFormA = eA.getLower() .iterator().next();
//
//                final Form loFormB = loMatching.getForms().getBMatching(loFormA);
//                final Form hiFormB = hiMatching.getForms().getBMatching(hiFormA);
//                
//                
//                final Set<Edge> edgesB = edgesWith(loFormB, hiFormB);
//                if (edgesB.size() == 1) {
//                    final Edge eB = edgesB.iterator().next();
//                    if (hiMatching.getEdges().getAMatching(eB) == null) { // not matched yet
//                        hiMatching.getEdges().match(eA, eB);
//                    }
//                }
//                // todo try to find one matching 
//            }
//            else {
//                // todo handle 
//            }
//            
//        }
//        
//        // todo check unmatched edges in hiPara2
//        
//    }
//
//    
//    final FormEd.SimilarityScorer<LForm> scorer = new FormEd.SimilarityScorerAdapter<LForm>() {
//        @Override
//        public int sub(List<LForm> as, int ai, List<LForm> bs, int bi) {
//            final LForm a = as.get(ai);
//            final LForm b = bs.get(bi);
//
//            return com.google.common.base.Objects.equal(a.getToken(), b.getToken()) ? 0 : 1;
//            //boolean sameSource = null;
//
//            //return formsMatch(a.getLowerForms(), b.getLowerForms()) ? 0 : 1;
//
//            // todo match forms
//
//        }
//    };
//    final FormEd<LForm> fed = new FormEd<LForm>(scorer);
//
//    
//    private void matchForms(final LPara hiPara1, final LPara hiPara2) {
//        final List<LForm> hiForms1 = hiPara1.getForms();
//        final List<LForm> hiForms2 = hiPara2.getForms();
//        
//        final Matching matching = layer2matching.get(hiPara1.getLayer());
//
//        final FormEd.Matcher<LForm> m = fed.match(hiForms1, hiForms2);
//        m.match();
//        
//        int i1 = 0;
//        int i2 = 0;
//        for (Op op : m.getOps()) {
//            if (i1 >= hiForms1.size()) {
//                System.out.println("Error i1 out: " + i1);
//                break;
//            }
//            if (i2 >= hiForms2.size()) {
//                System.out.println("Error i2 out: " + i2);
//                break;
//            }
//            if (op == Op.sub) {
//                matching.getForms().match(hiForms1.get(i1), hiForms2.get(i2));
//                i1++; 
//                i2++;
//            }
//            else if (op == Op.ins) {
//                i2++;
//            }
//            else if (op == Op.del) {
//                i1++;
//            }
//        }
//    }
//
//    
//    private <F extends Form> boolean formsMatch(Collection<F> forms1,  Collection<F> forms2) {
//        if (forms1.isEmpty() && forms2.isEmpty()) return true;
//        
//        Matching matching = layer2matching.get(forms1.iterator().next().getLayer());
//
//        if (forms1.size() == 1 && forms2.size() == 1) {
//            F f1 = forms1.iterator().next();
//            return matching.getForms().getBMatching(f1) == forms2.iterator().next();
//        }
//        
//        for (F f : forms1) {
//            if ( !forms2.contains(matching.getForms().getBMatching(f)) ) return false;
//        }
//        for (F f : forms2) {
//            if ( !forms1.contains(matching.getForms().getAMatching(f)) ) return false;
//        }
//
//        
//        return true;
//    }
//    
//    
//    
////    private void matchForms(final List<LForm> hiForms1, final List<LForm> hiForms2) {
////        final int n1 = hiForms1.size();
////        final int n2 = hiForms2.size();
////        // quick and easy checks first before doing anything complicated
////        if (n1 == 0 || n2 == 0) {
////            return;
////        }
////        else if (n1 == n2) {
////            // todo this is simplifying, match properly
////            for (int i = 0; i < n1; i++) {
////                match.match(hiForms1.get(i), hiForms2.get(i));
////            }
////        }
////        
////        // todo use edit distance diff 
////        for (int i = 0; i < Math.min(n1,n2); i++) {
////            match.match(hiForms1.get(i), hiForms2.get(i));
////        }
////    }
//    
//    
////    private void x() {
////        for (int wIdx = 0; wIdx < aParaStruct1.getWpara().getForms().size(); wIdx++) {
////            WForm wForm1 = aParaStruct1.getWpara().getForms().get(wIdx);
////            WForm wForm2 = aParaStruct2.getWpara().getForms().get(wIdx);
////
////            for (int i = 0; i < aParaStruct1.getLparas().size(); i++) {
////                LPara hiPara1 = aParaStruct1.getLparas().get(i);
////                LPara hiPara2 = aParaStruct2.getLparas().get(i);
////            }            
////        }
////        
////            List<LForm> hiForms1 = new ArrayList<LForm>();
////            List<LForm> hiForms2 = new ArrayList<LForm>();
////            
////            for (Diff.FormForm ff : loMatchedForms) {
////                if (ff.f1 != null) hiForms1.addAll( hiPara1.getForms(ff.f1) );
////                if (ff.f2 != null) hiForms2.addAll( hiPara2.getForms(ff.f2) );
////            }
////            
////            Collection<Diff.FormForm<LForm>> hiMatchedForms = matchForms(hiForms1, hiForms2, loMatchedForms, hiPara1, hiPara2);
////
////            for (Diff.FormForm ff : hiMatchedForms) {
////                if (!ff.isIdentical()) { // todo check what the difference is
////                    if (ff.f1 != null) difference.elements1.add(ff.f1);
////                    if (ff.f2 != null) difference.elements2.add(ff.f2);
////                }
////            }
////
////            List<EdgeEdge> matchedEdges = matchEdges(loMatchedForms, hiMatchedForms);
////            
////            // compare them, taking into account matched forms
////            
////            loMatchedForms = hiMatchedForms;
////        }            
////        
////    }
////    
////    private void matchPara(final ParaStruct aParaStruct1, final ParaStruct aParaStruct2) {
////    
////    }
//
//    
//}
