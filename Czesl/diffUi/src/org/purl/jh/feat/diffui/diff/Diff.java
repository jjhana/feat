package org.purl.jh.feat.diffui.diff;

import cz.cuni.utkl.czesl.data.layerl.*;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.purl.jh.feat.util0.ByListSort;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.MultiHashHashMap;
import org.purl.jh.util.col.MultiMap;

/**
 * The two annotations (should) have an identical wlayer.
 * Note: that the layers are connected from up down.  An edge on a higher layer
 * links to tokens on a lower layer, but the lower layer does not have such links
 * going up. The reason is that several higher layers might link to a single
 * lower layer.
 *
 * todo under development
 * @author j
 */
public class Diff {


    // pointer to an object or its part
    public static class Marker<E extends Element> {
        E element;
        public E getElement() {
            return element;
        }
    }
    
    public static class LFormMarker extends Marker<LForm> {
        
    }
    
    public static class EdgeMarker extends Marker<Edge> {
        
    }
        
    public static class SentenceMarker extends Marker<Sentence> {
        
    }
    
    
    public static class Difference {
        List<WForm> wforms;
        // todo add something to be able to refer to parts of elements 
        // characters for words, leg/tag for edge, comment, etc.
        final List<Element> elements1 = new ArrayList<>();
        final List<Element> elements2 = new ArrayList<>();

        public Difference(WForm wform) {
            wforms = Arrays.asList(wform);
        }
        
        public List<Element> getElements1() {
            return elements1;
        }

        public List<Element> getElements2() {
            return elements2;
        }

        public List<WForm> getWforms() {
            return wforms;
        }

        @Override
        public String toString() {
            return "Difference{" + "wforms=" + wforms + ", elements1=" + elements1 + ", elements2=" + elements2 + '}';
        }
        
        public boolean isEmpty() {
            return elements1.isEmpty() && elements2.isEmpty();
        }
        
    }


    /**
     * Note: assumes that LDoc/LPara structure is fixed
     * 
     * @param aLayer1
     * @param aLayer2
     * @return 
     */
    public List<Difference> calculateDiff(LLayer aLayer1, LLayer aLayer2) {
        final List<Difference> diffs = new ArrayList<>();

        final List<Layer<?>> layers1 = DiffUtil.collectLayers(aLayer1);
        final List<Layer<?>> layers2 = DiffUtil.collectLayers(aLayer2);

        WLayer wlayer1 = Cols.findElement(layers1, WLayer.class);
        WLayer wlayer2 = Cols.findElement(layers2, WLayer.class);

        // todo check if compatible, or maybe even identical.
        for (int docIdx=0; docIdx < aLayer1.col().size(); docIdx++) {
            final LDoc ldoc1 = aLayer1.get(docIdx);
            final LDoc ldoc2 = aLayer2.get(docIdx);

            for (int paraIdx=0; paraIdx < ldoc1.col().size(); paraIdx++) {
                final LPara lpara1 = ldoc1.get(paraIdx);
                final LPara lpara2 = ldoc2.get(paraIdx);

                ParaDiffStruct paraStruct1 = new ParaDiffStruct(lpara1); 
                ParaDiffStruct paraStruct2 = new ParaDiffStruct(lpara2); 

                diffs.addAll( comparePara(paraStruct1, paraStruct2) );

            }
        }

        return diffs;
    }



    private Collection<Difference> comparePara(ParaDiffStruct paraStruct1, ParaDiffStruct paraStruct2) {
        final List<Difference> diffs = new ArrayList<>();

        for (int formIdx = 0; formIdx < paraStruct1.wpara.size(); formIdx++) {
            WForm wform1 = paraStruct1.wpara.get(formIdx);
            WForm wform2 = paraStruct2.wpara.get(formIdx);

            // todo currently considers each wform separatly, should considere spans
            Difference diff = compareWForms(wform1, wform2, paraStruct1, paraStruct2);
            if (diff != null) diffs.add(diff);
        }

        return diffs;
    }

    // todo match forms, match edges, compare. Then recursively for the higher layer.
    private Difference compareWForms(WForm wform1, WForm wform2, ParaDiffStruct paraStruct1, ParaDiffStruct paraStruct2) {
        final Difference difference = new Difference(wform1);

        Collection<? extends FormForm> loMatchedForms = Arrays.asList(new FormForm<>(wform1, wform2));
       
//        Para loPara1 = paraStruct1.wpara;
//        Para loPara2 = paraStruct2.wpara;
        for (int i = 1; i < paraStruct1.paras.size(); i++) {
            LPara hiPara1 = (LPara)paraStruct1.paras.get(i);
            LPara hiPara2 = (LPara)paraStruct2.paras.get(i);

            List<LForm> hiForms1 = new ArrayList<>();
            List<LForm> hiForms2 = new ArrayList<>();
            
            for (FormForm ff : loMatchedForms) {
                if (ff.f1 != null) hiForms1.addAll( hiPara1.getForms(ff.f1) );
                if (ff.f2 != null) hiForms2.addAll( hiPara2.getForms(ff.f2) );
            }
            
            Collection<FormForm<LForm>> hiMatchedForms = matchForms(hiForms1, hiForms2, loMatchedForms, hiPara1, hiPara2);

            for (FormForm ff : hiMatchedForms) {
                if (!ff.isIdentical()) { // todo check what the difference is
                    if (ff.f1 != null) difference.elements1.add(ff.f1);
                    if (ff.f2 != null) difference.elements2.add(ff.f2);
                }
            }

            List<EdgeEdge> matchedEdges = matchEdges(loMatchedForms, hiMatchedForms);
            
            // compare them, taking into account matched forms
            
            loMatchedForms = hiMatchedForms;
        }            

        
        
        return difference.isEmpty() ? null : difference;
    }

    /**
     * Returns an alignment of forms.
     * 
     * @param hiForms1
     * @param hiForms2
     * @param loMatchedForms
     * @param hiPara1
     * @param hiPara2
     * @return 
     */
    private Collection<FormForm<LForm>> matchForms(List<LForm> hiForms1, List<LForm> hiForms2, Collection<? extends FormForm> loMatchedForms, LPara hiPara1, LPara hiPara2) {
        // todo improve
        final List<FormForm<LForm>> formMatching = new ArrayList<>();
        
        final int n = Math.min(hiForms1.size(),hiForms2.size());
        for (int i = 0; i < n; i++) {
            LForm form1 = hiForms1.get(i);
            LForm form2 = hiForms2.get(i);
            formMatching.add(new FormForm(form1,form2));
        }
        for(int i = n; i < hiForms1.size(); i++) {
            formMatching.add(new FormForm(hiForms1.get(i),null));
        }
        for(int i = n; i < hiForms2.size(); i++) {
            formMatching.add(new FormForm(null, hiForms2.get(i)));
        }
        return formMatching;
    }


    /**
     * 
     * @param loMatchedForms
     * @param hiMatchedForms
     * @return 
     */
    private List<EdgeEdge> matchEdges(Collection<? extends FormForm> loMatchedForms, Collection<FormForm<LForm>> hiMatchedForms) {
        // todo improve
        final List<EdgeEdge> edgeMatching = new ArrayList<>();

        for (FormForm<LForm> ff : hiMatchedForms) {
            if (ff.f1 != null && ff.f2 != null) {
                final List<Edge> es1 = new ArrayList<>(ff.f1.getLower());
                final List<Edge> es2 = new ArrayList<>(ff.f1.getLower());
                
                final int n = Math.min(es1.size(),es2.size());
                for (int i = 0; i < n; i++) {
                    Edge e1 = es1.get(i);
                    Edge e2 = es2.get(i);
                    edgeMatching.add(new EdgeEdge(e1,e2));
                }
                for(int i = n; i < es1.size(); i++) {
                    edgeMatching.add(new EdgeEdge(es1.get(i),null));
                }
                for(int i = n; i < es2.size(); i++) {
                    edgeMatching.add(new EdgeEdge(null, es2.get(i)));
                }
                
            }
            else if (ff.f1 != null) {
                for (Edge e : ff.f1.getLower()) {
                    edgeMatching.add(new EdgeEdge(e, null));
                }
            }
            else {
                for (Edge e : ff.f2.getLower()) {
                    edgeMatching.add(new EdgeEdge(null, e));
                }
            }
        }
        
        // todo half edges
        
        return edgeMatching;
    }
    
    
    class ParaDiffStruct {
        List<Para> paras;
        Para topPara;
        WPara wpara;
//        LPara apara;
//        LPara bpara;
        //List<LPara> lparas; todo

        public ParaDiffStruct(LPara aTopPara) {
            topPara = aTopPara;
            paras = topPara.getLowerEqParas();
            wpara = (WPara)paras.get(0);
            
        }

    }


    class FormForm<F extends FForm> {
        F f1;
        F f2;

        public FormForm(F f1, F f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        
        boolean isIdentical() {
            return com.google.common.base.Objects.equal(f1, f2);
        }
    }

    /**
     * Organizes edges of a paragraph
     * Based on LLayerWriter.EdgesInfo, consider to share add some of it to the actuall data struct
     */
    private class EdgesInfo {
        private final MultiMap<LForm,Edge> form2edges = new MultiHashHashMap<>();
        private final List<Edge> deleteEdges = new ArrayList<>();
        private final ByListSort<LForm> hiWoSorter;
        private final ByListSort<FForm> loWoSorter;

        private EdgesInfo(final LPara aPara) {
            // initializes structures used in wo-sorting, consider adding this to the actual data structure, used in other places too
            hiWoSorter = new ByListSort(aPara.getFormsList());
            loWoSorter = new ByListSort(aPara.getLowerPara().getFormsList());

            // for each edge find the first (by wo) hi-form it connects, or add it to del edges
            for (Edge edge : aPara.getEdges()) {
                if (!edge.getHigher().isEmpty()) {
                    form2edges.add(hiWoSorter.min(edge.getHigher()), edge);
                }
                else {
                    deleteEdges.add(edge);
                }
            }

            sortDelEdges(deleteEdges);
        }

        public List<LForm> sortHiByWo(Collection<LForm> aHiForms) {
            final List<LForm> forms = new ArrayList<>(aHiForms);
            hiWoSorter.sort(forms);
            return forms;    // todo
        }

        public List<FForm> sortLoByWo(Collection<FForm> aLoForms) {
            final List<FForm> forms = new ArrayList<>(aLoForms);
            loWoSorter.sort(forms);
            return forms;    // todo
        }

        public Set<Edge> getEdges(LForm aForm) {
            Set<Edge> edges = form2edges.get(aForm);

            return edges == null ? Collections.<Edge>emptySet() : edges;
        }

        public List<Edge> getDeleteEdges() {
            return deleteEdges;
        }

        /**
         * Sorts unary deletion edges by word-order.
         * @param deleteEdges
         */
        private void sortDelEdges(List<Edge> deleteEdges) {
            final Comparator<FForm> formComp = loWoSorter.getComparator();
            final Comparator<Edge> comp = new Comparator<Edge>() {
                @Override
                public int compare(Edge o1, Edge o2) {
                    FForm form1 = o1.getLower().iterator().next();
                    FForm form2 = o2.getLower().iterator().next();

                    return formComp.compare(form1, form2);
                }
            };
            Collections.sort(deleteEdges, comp);
        }
    }

    private EdgesInfo prepareEdges(final LPara aPara) {
        return new EdgesInfo(aPara);
    }





}
