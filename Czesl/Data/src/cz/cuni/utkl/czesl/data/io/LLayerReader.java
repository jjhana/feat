
package cz.cuni.utkl.czesl.data.io;

import com.google.common.collect.ImmutableList;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.ErrorSpecs;
import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import cz.cuni.utkl.czesl.data.layerx.Doc;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.AbstractListElement;
import org.purl.jh.pml.io.JDomUtil;
import org.purl.jh.pml.io.LayerReader;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.col.XCols;

/*
 * Reads in Czesl A/B (R1, R2) layers.
 * <ul>
 * <li>Each document and paragraph links to the corresponding doc/para in the lower layer.
 * <li>Forms are connected by n:m hyper-edges to the forms on the lower layer. Most edges are 1:1.
 * <li>Edges can contain one or more error annotations.
 * <li>Forms are within sentences. Sentences on other layers do not have to match (there are no sentences on the w-layer)
 * </ul>
 * <p>
 * todo: to improve readibility of the xml file (and their diffs), all edges (except del edges) will
 * be encoded as subelements of their first form. Currently only simple (1:1) edges are read that way.
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class LLayerReader extends LayerReader<LLayer> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LLayerReader.class);

    
    
    
    public LLayerReader() {
        super("http://utkl.cuni.cz/czesl/");
    }


    @Override
    protected LLayer createLayer() {
        return new LLayer(fileObject, "L");
    }

    @Override
    protected void processJdom(org.jdom.Element aRoot) {
        rootEtc(aRoot);

        // todo: tagset support is unfinished
        // set default tagset, todo make configurable
        if (data.getTagsets().isEmpty()) {
            data.addTagset("t", ErrorSpecs.INSTANCE.getErrorSpecs(data));
        }

        for (org.jdom.Element docE : getChildren(aRoot, Xml.DOC)) {
            processDoc(docE);
        }
    }

    private void processDoc(final org.jdom.Element aDocE) {
        final String lowerDocId = aDocE.getAttributeValue(Xml.LOWERDOC_RF);
        final Doc lowerDoc = (Doc) data.getElement(lowerDocId);
        final LDoc doc = new LDoc(data, JDomUtil.getId(aDocE), lowerDoc);
        data.addIdedElement(doc);
        data.add(doc);

        doc.setParent(data);

        for (org.jdom.Element paraE : getChildren(aDocE, Xml.PARA) ) {
            processPara(doc, paraE);
        }
    }

    private void processPara(final LDoc aDoc, final org.jdom.Element aParaE) {
        final String lowerParaId = aParaE.getAttributeValue(Xml.LOWERPARA_RF);
        final Para lowerPara = (Para) data.getElement(lowerParaId);
        if (lowerPara == null) {
            err.severe("Cannot find paragraph in the lower level id=%s linked from paragraph %s", lowerParaId, JDomUtil.getId(aParaE));
            return;
        }
        final LPara para = new LPara(data, JDomUtil.getId(aParaE), lowerPara);
        data.addIdedElement(para);
//        System.out.println("processPara: Paras: " + para + " -> " + lowerPara);

        aDoc.add(para);
        para.setParent(aDoc);

        boolean li = JDomUtil.getBoolAttribute(aParaE, Xml.LI, false);
        para.setLi(li);
        
        for (org.jdom.Element sE : getChildren(aParaE, Xml.S)) {
            processSentence(para, sE);
        }

        // --- read all edges (all form-ids are resolvable) --- todo use xpath
        for (org.jdom.Element sE : getChildren(aParaE, Xml.S)) {
            for (org.jdom.Element formE : getChildren(sE, Xml.W) ) {
                final String formId = JDomUtil.getId(formE);
                final LForm form = data.<LForm>getElement(formId);
                for (org.jdom.Element simpleEdge : getChildren(formE, Xml.EDGE)) {
                    processEdge(form, simpleEdge);
                }
            }
        }

        // deletion edges, and edges in the old format
        for (org.jdom.Element edgeE : getChildren(aParaE, Xml.EDGE)) {
            processSeparateEdge(para, edgeE);
        }

        // resolve error links (now when all edges are loaded)
        for (Edge edge : para.getEdges()) {
            for (Errorr error : edge.getErrors()) {
                for (String edgeId : error2edgeIds.getS(error)) {
                    Edge linkedEdge = data.<Edge>getElement(edgeId); // todo casting error should be handled
                    if (linkedEdge == null) {
                        err.warning("Edge %s linking to unknown edge %s. Removing link.", edge.getId().getIdStr(), edgeId);
                    }
                    else {
                        error.addLink(linkedEdge);
                    }
                }
            }
        }
    }


    private void processSentence(final LPara aPara, final org.jdom.Element aSentenceE) {
        final Sentence sentence = new Sentence(data, JDomUtil.getId(aSentenceE));
        data.addIdedElement(sentence);
        aPara.add(sentence);
        sentence.setParent(aPara);

        for (org.jdom.Element formE : getChildren(aSentenceE, Xml.W) ) {
            processForm(sentence, formE);
        }
    }

    private void processForm(final Sentence aSentence, final org.jdom.Element aFormE) {
        final String token = getText(aFormE, Xml.TOKEN);
        final String id = JDomUtil.getId(aFormE);
        final FForm.Type type = FeatLayerIo.getTokenType(this, aFormE);
        final ImmutableList<org.jdom.Element> other = otherChildren(aFormE, 
                ImmutableList.of(Xml.TOKEN, Xml.TYPE, Xml.COMMENT, Xml.EDGE));
        
        LForm form = new LForm(data, id, type, token, other);

        data.addIdedElement(form);
        form.setParent(aSentence);
        aSentence.add(form);

        data.addForm(form);

        readComment(aFormE, form);

        // edges are read-in later (not all form-ids are resolved now)
    }

    /** todo For some weir reason does not work, once it does, push to LayerReader */
    private <P extends AbstractListElement<C>, C extends IdedElement> void addIdedElement(P aParent, C aIdedElement) {
        data.addIdedElement(aIdedElement);
        aIdedElement.setParent(aParent);
        aParent.add(aIdedElement);
    }


    /**
     * Read an edge specified as a sub-element of a form.
     *
     * @param aParentForm the parent form of this edge (one of the higher forms)
     * @param aEdgeE edge element containing this
     */
    private void processEdge(final LForm aParentForm, org.jdom.Element aEdgeE) {
        final LPara para = aParentForm.getParent().getParent();

        final List<LForm> higherForms = new ArrayList<>();
        higherForms.add(aParentForm);
        higherForms.addAll( this.<LForm>resolveRfEsx(aEdgeE, "to") );

        final List<FForm> lowerForms = this.<FForm>resolveRfEsx(aEdgeE, "from");

        processEdge(para, aEdgeE, lowerForms, higherForms);
    }

    /**
     * Read a regular edge specified as a sub-element of a paragraph.
     *
     * @param aPara
     * @param aEdgeE edge element containing this
     */
    private void processSeparateEdge(final LPara aPara, org.jdom.Element aEdgeE) {
        List<FForm> lowerForms = this.<FForm>resolveRfEsx(aEdgeE, "from");
        List<LForm> higherForms  = this.<LForm>resolveRfEsx(aEdgeE, "to");

        processEdge(aPara, aEdgeE, lowerForms, higherForms);
   }

    private void processEdge(final LPara aPara, org.jdom.Element aEdgeE, List<FForm> aLowerForms, List<LForm> aHigherForms) {
        final Edge edge = new Edge(data, JDomUtil.getId(aEdgeE));
        data.addIdedElement(edge);
        edge.setParent(aPara);
        aPara.add(edge);

        edge.getLower() .addAll( aLowerForms );
        edge.getHigher().addAll( aHigherForms );
        readComment(aEdgeE, edge);

        try {
            for (FForm form : edge.getLower()) {
                form.getHigher().add(edge);
            }
            for (FForm form : edge.getHigher()) {
                form.getLower().add(edge);
            }
        }
        catch(Exception e) {
            fatalError(e, "Error reading edge %s", edge.getId().getIdStr());
        }

        for (org.jdom.Element errorE : getChildren(aEdgeE, Xml.ERROR)) {
            processError(edge, errorE);
        }
    }
    
    private void processError(final Edge edge, org.jdom.Element errorE) {
        String id = JDomUtil.getId(errorE);
        // for backward compatibility
        if (id == null) {
            id = data.getUniqueId(edge, "r", edge.getErrors().size());
        }

        String tagId = errorE.getChildText("tag", n);
        String newTagId = data.getTagset().update(tagId);
        if (newTagId != null) tagId = newTagId;

        ErrorTag errorTag = data.getTagset().getTag(tagId);
        if (errorTag == null) {
            warning("The %s tag of the edge %s is not known, replacing with the 'unknown tag'", tagId, edge.getId().getIdStr());
            errorTag = data.getTagset().getUnknownTag();
        }

        final Errorr error = new Errorr(data, id, tagId);
        edge.getErrors().add(error);
        error.setParent(edge);

        readComment(errorE, error);

        error2edgeIds.addAll(error, getRfs (errorE, Xml.LINK));   // compatibility (some are saved with rf some without)
        error2edgeIds.addAll(error, getRfsx(errorE, Xml.LINK));
   }


   /**
    * Map holding references to edges. It is resolved after all edges are read in
    * because errors can have forward links.
    */
   private final MultiMap<Errorr,String> error2edgeIds = XCols.newMultiHashHashMap();

// </editor-fold>

    /** Namespaces are ignored. todo Move to utils*/
    private static ImmutableList<org.jdom.Element> otherChildren(final org.jdom.Element element, Collection<String> exclude) {
        ImmutableList.Builder<org.jdom.Element> builder = ImmutableList.builder();
        
        for (org.jdom.Element child : JDomUtil.getChildren(element)) {
            if (!exclude.contains(child.getName())) {
                builder.add( (org.jdom.Element) child.clone());
            }
        }
        
        return builder.build();
    }
     
}
