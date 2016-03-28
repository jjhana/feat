package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.jdom.Attribute;
import org.purl.jh.feat.util0.ByListSort;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.io.JDomUtil;
import org.purl.jh.pml.io.LayerWriter;
import org.purl.jh.util.col.Cols;

import org.purl.jh.util.err.Err;
import org.purl.jh.util.col.MultiHashHashMap;
import org.purl.jh.util.col.MultiMap;

/**
 * Translates an LLayer into the corresponding jdom structure.
 *
 * TODO parametrize adata/adata_schema
 * TODO under development - changing to the new format which include edges into
 * the first form they refer to.
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class LLayerWriter extends LayerWriter<LLayer> {

    public LLayerWriter() {
        super("http://utkl.cuni.cz/czesl/");
    }



    @Override
    protected org.jdom.Element createJdom() {
        final org.jdom.Element root = rootEtc("ldata", "ldata_schema");

        for (LDoc doc : data.col()) {
            root.addContent(createDocE(doc));
        }
        return root;
    }

    private org.jdom.Element createDocE(final LDoc aDoc) {
        final org.jdom.Element docE = el(Xml.DOC, aDoc);
        docE.getAttributes().add( new Attribute(Xml.LOWERDOC_RF, data.getRef(aDoc.getLowerDoc())) );

        for (LPara para : aDoc.col()) {
            docE.addContent( createParaE(para) );
        }

        return docE;
    }

    private org.jdom.Element createParaE(final LPara aPara) {
        final org.jdom.Element paraE = el(Xml.PARA, aPara);
        paraE.getAttributes().add( new Attribute(Xml.LOWERPARA_RF, data.getRef(aPara.getLowerPara())) );

        JDomUtil.setAttribute(paraE, Xml.LI, aPara.isLi(), false);
        
        final EdgesInfo edgesInfo = prepareEdges(aPara);

        for (Sentence s : aPara.getSentences()) {
            paraE.addContent(createSentenceE(s,edgesInfo));
        }

        // save remaining (non-trivial) edges
        for (Edge edge : edgesInfo.getDeleteEdges()){
            paraE.addContent(createEdgeE(edge, null, edgesInfo));
        }

        return paraE;
    }


    private org.jdom.Element createSentenceE(final Sentence aS, final EdgesInfo aEdgesInfo) {
        final org.jdom.Element sE = el(Xml.S, aS);

        for (LForm form : aS.col()) {
            sE.addContent( createFormE(form, aEdgesInfo) );
        }

        return sE;
    }

    private org.jdom.Element createFormE(final LForm aForm, final EdgesInfo aEdgesInfo) {
        final org.jdom.Element wE = el(Xml.W, aForm);
        if (!aForm.getType().normal()) {
            addContent(wE, Xml.TYPE, aForm.getType().name());
        }

        addContent(wE, Xml.TOKEN, aForm.getToken());
        addComment(wE, aForm);

        if (!aForm.getOther().isEmpty()) {
            for (org.jdom.Element element : aForm.getOther()) {
                wE.addContent(element);
            }
        }
        
        final Set<Edge> edges = aEdgesInfo.getEdges(aForm);  //
        for (Edge e : edges) {
            wE.addContent(createEdgeE(e, aForm, aEdgesInfo));
        }

        return wE;
    }

    /** Single edge with one higher form (higher:lower - 1:n)*/
    private Edge simpleEdge(FForm aForm) {
        if (aForm.getLower().size() != 1) return null;

        Edge e = Cols.one(aForm.getLower());
        if (e.getHigher().size() != 1) return null;

        return e;
    }

    /**
     * Organizes edges of a paragraph
     */
    private class EdgesInfo {
        private final MultiMap<LForm,Edge> form2edges = new MultiHashHashMap<>();
        private final List<Edge> deleteEdges = new ArrayList<>();
        private final ByListSort<LForm> hiWoSorter;
        private final ByListSort loWoSorter;            

        private EdgesInfo(final LPara aPara) {
            // initializes structures used in wo-sorting, consider adding this to the actual data structure, used in other places too
            hiWoSorter = new ByListSort<>(aPara.getFormsList());
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

        public <F extends FForm> List<F> sortLoByWo(Collection<F> aLoForms) {
            final List<F> forms = new ArrayList<>(aLoForms);
            loWoSorter.sort(forms);
            return forms;    
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


    private org.jdom.Element unsupportedElement(Element aE) {
        String parentClass = (aE.getParent() != null) ? aE.getParent().getClass().toString() : "(No parent)";
        Err.err("Unsupported element %s under %s:\n", aE.getClass(), parentClass, aE);
        return null;
    }


    // todo sort forms by word-order
    private org.jdom.Element createEdgeE(Edge aEdge, LForm aParentForm, EdgesInfo aEdgesInfo) {
        final org.jdom.Element edgeE = el(Xml.EDGE, aEdge);

        final List<LForm> higherForms = aEdgesInfo.sortHiByWo(aEdge.getHigher());
        higherForms.remove(aParentForm);

        final List<FForm> lowerForms = aEdgesInfo.sortLoByWo(aEdge.getLower());

        edgeE.addContent(rfEsx(Xml.FROM, lowerForms));
        edgeE.addContent(rfEsx(Xml.TO, higherForms));
        addComment(edgeE, aEdge);
        edgeE.addContent(createErrorEs(aEdge));

        return edgeE;
    }

    private List<org.jdom.Element> createErrorEs(Edge aEdge) {
        final List<org.jdom.Element> errorEs = new ArrayList<>();

        if (!aEdge.getErrors().isEmpty()) {
            for (Errorr error : new TreeSet<>(aEdge.getErrors())) {     // sort by tagId
                errorEs.add(createErrorE(error));
            }
        }
                
        
        return errorEs;
    }

    private org.jdom.Element createErrorE(Errorr aError) {
        final org.jdom.Element errorE = el(Xml.ERROR);

        addContent(errorE, Xml.TAG, aError.getTag());
        addComment(errorE, aError);
        errorE.addContent(rfEsx(Xml.LINK, aError.getLinks()));

        return errorE;
    }


//</editor-fold>
}
