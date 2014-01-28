package org.purl.jh.feat.diffui.diff;

import com.google.common.collect.ImmutableList;
import cz.cuni.utkl.czesl.data.layerl.*;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.util.*;
import org.purl.jh.pml.Layer;

/**
 *
 * @author j
 */
public class DiffUtil {
//    public static <L extends Layer<?>> List<L> collectLayers(Layer<?> aLayer, Class<L> aClass) {
//        final List<L> layers = new ArrayList<L>();
//        for (Layer<?> layer : aLayer.getReferencedLayers()) {
//            collectLayers(layer, layers, aClass);
//        }
//        if (aClass.isInstance(aLayer)) layers.add((L)aLayer);
//        return layers;
//    }
//
//    public static <L extends Layer<?>> void collectLayers(Layer<?> aLayer, List<L> layers, Class<L> aClass) {
//        for (Layer<?> layer : aLayer.getReferencedLayers()) {
//            collectLayers(layer, layers, aClass);
//        }
//        if (aClass.isInstance(aLayer)) layers.add((L)aLayer);
//    }
    
    static WLayer getWLayer(FormsLayer<?> aLayer) {
        for (;;) {
            if (aLayer == null || aLayer instanceof WLayer) return (WLayer)aLayer;
            aLayer = aLayer.getLowerLayer();
        }
    }
    

    public static <L extends Layer<?>> List<L> collectLayers(Layer<?> aLayer, Class<? super L> aClass) {
        final List<L> layers = new ArrayList<>();
        for (Layer<?> layer : aLayer.getReferencedLayers()) {
            collectLayers(layer, layers, aClass);
        }
        if (aClass.isInstance(aLayer)) layers.add((L)aLayer);
        return layers;
    }

    public static <L extends Layer<?>> void collectLayers(Layer<?> aLayer, List<L> layers, Class<? super L> aClass) {
        for (Layer<?> layer : aLayer.getReferencedLayers()) {
            collectLayers(layer, layers, aClass);
        }
        if (aClass.isInstance(aLayer)) layers.add((L)aLayer);
    }

    // todo to util
    public static List<Layer<?>> collectLayers(Layer<?> aLayer) {
        final List<Layer<?>> layers = new ArrayList<>();
        for (Layer<?> layer : aLayer.getReferencedLayers()) {
            collectLayers(layer, layers);
        }
        layers.add(aLayer);
        return layers;
    }

    public static void collectLayers(Layer<?> aLayer, List<Layer<?>> layers) {
        for (Layer<?> layer : aLayer.getReferencedLayers()) {
            collectLayers(layer, layers);
        }
        layers.add(aLayer);
    }

    /**
     * To util. 
     * @param aLoForm
     * @param aHiForm
     * @return 
     */
    public static Set<Edge> edgesWith(final FForm aLoForm, final FForm aHiForm) {
        
        return new EdgeFilter(aHiForm.getLower()).setReqLoForms(ImmutableList.<FForm>of(aLoForm)).set();
//        final Set<Edge> edges = new HashSet<Edge>(aHiForm.getLower());
//        for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
//            Edge edge = it.next();
//            if (!edge.getLower().contains(aLoForm)) it.remove();
//        }
//
//        return edges;
    }
    
    public static Iterable<Edge> edges(LLayer aLLayer) {
        final List<Edge> edges = new ArrayList<>();
        
        for (LDoc doc : aLLayer.col()) {
            for (LPara para : doc.col()) {
                edges.addAll(para.getEdges());
           }
        }
        
        return edges;
    }
    
//    private static class EdgeIterator implements Iterator<Edge> {
//        private final Iterator<LDoc>  docIt;
//        private Iterator<LPara> paraIt;
//        private Iterator<Edge>  edgeIt;
//        
//        public EdgeIterator(final LLayer aLLayer) {
//            docIt = aLLayer.iterator();
//            nextDoc();
//        }
//        
//        private void nextDoc() {
//            if (docIt.hasNext()) {
//                paraIt = docIt.next().iterator();
//                nextPara();
//            }
//        }
//
//        private void nextPara() {
//            if (paraIt.hasNext()) {
//                edgeIt = paraIt.next().getEdges().iterator();
//            }
//        }
//        
//        @Override
//        public boolean hasNext() {
//            if (edgeIt == null) {
//                if (paraIt == null) {
//                    if (docIt.hasNext()) {
//                        paraIt = docIt.next().iterator();
//                    }
//                    else {
//                        return false;
//                    }
//                }
//                
//                if (paraIt.hasNext()) {
//                    
//                }
//                
//                }
//            }
//            
//            if (edgeIt = null && edge)
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//        @Override
//        public Edge next() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//   
//    }
//    
//    
//    public static Iterable<Edge> edgesI(final LLayer aLLayer) {
//        return new Iterable<Edge>() {
//            @Override
//            public Iterator<Edge> iterator() {
//                return new EdgeIterator(aLLayer);
//            }
//        };
//    }
    
    public static class EdgeFilter {
        private final Iterable<Edge> edges;
                
        private int hiMinArity = -1;
        private int loMinArity = -1;
        private int hiMaxArity = -1;
        private int loMaxArity = -1;
        private List<FForm> reqLoForms = null;
        private List<FForm> reqHiForms = null;
        private List<String> reqErrorTags = null;
        // todo predicate

        public EdgeFilter(Iterable<Edge> edges) {
            this.edges = edges;
        }

        public EdgeFilter setHiMaxArity(int hiMaxArity) {
            this.hiMaxArity = hiMaxArity;
            return this;
        }

        public EdgeFilter setHiMinArity(int hiMinArity) {
            this.hiMinArity = hiMinArity;
            return this;
        }

        public EdgeFilter setLoMaxArity(int loMaxArity) {
            this.loMaxArity = loMaxArity;
            return this;
        }

        public EdgeFilter setLoMinArity(int loMinArity) {
            this.loMinArity = loMinArity;
            return this;
        }

        public EdgeFilter setReqErrorTags(List<String> reqErrorTags) {
            this.reqErrorTags = reqErrorTags;
            return this;
        }

        public EdgeFilter setReqHiForms(List<FForm> reqHiForms) {
            this.reqHiForms = reqHiForms;
            return this;
        }

        public EdgeFilter setReqLoForms(List<FForm> reqLoForms) {
            this.reqLoForms = reqLoForms;
            return this;
        }
        
        public EdgeFilter insert() {
            loMaxArity = 0;
            hiMinArity = 1;
            hiMaxArity = 1;
            return this;
        }
        
        public EdgeFilter delete() {
            loMinArity = 1;
            loMaxArity = 1;
            hiMaxArity = 0;
            return this;
        }
        
        public EdgeFilter simple() {
            loMinArity = 1;
            loMaxArity = 1;
            hiMinArity = 1;
            hiMaxArity = 1;
            return this;
        }
        
        public Set<Edge> set() {
            final Set<Edge> out = new HashSet<>();
            
            for (Edge e : edges) {
                if (check(e)) out.add(e); 
            }
            
            
            return out;
        }
        
        private boolean check(Edge aEdge) {
            return (
                (hiMinArity == -1 || hiMinArity <= aEdge.getHigher().size()) &&
                (loMinArity == -1 || loMinArity <= aEdge.getLower().size()) &&
                (hiMaxArity == -1 || aEdge.getHigher().size() <= hiMaxArity) &&
                (loMaxArity == -1 || aEdge.getLower().size()  <= loMaxArity) &&
                (reqLoForms == null || aEdge.getLower().containsAll(reqLoForms)) &&
                (reqHiForms == null || aEdge.getHigher().containsAll(reqHiForms)) &&
                (reqErrorTags    == null || isSubset(reqErrorTags, aEdge.getErrors())) 
                // todo predicate
                );
        }
        
        //private boolean 
        
//        List<Form> reqLoForms = null;
//        List<Form> reqHiForms = null;
//        List<String> reqErrorTags = null;
        
        
        public Iterable<Edge> iterable() {
            throw new UnsupportedOperationException();
        }

    }
    
    public static boolean isSubset(List<String> aSubset, Set<Errorr> aSet) {
        // theoretically ineffective, but aSet is usually very small (0 or 1, rarely 2 or 3)
        for (String tag : aSubset) {
            if (!contains(aSet, tag)) return false;
        }
        return true;
    }

    public static boolean contains(Set<Errorr> aSet, String aTag) {
        for (Errorr e : aSet) {
            if (e.getTag().equals(aTag)) return true;
        }
        return false;
    }
    
    
}
