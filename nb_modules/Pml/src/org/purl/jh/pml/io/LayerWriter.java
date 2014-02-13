package org.purl.jh.pml.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jdom.Namespace;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.RefVar;
import org.purl.jh.pml.ts.Tagset;

/**
 * @param <L> layer written by this writer
 *
 * Convention: use
 * <ul>
 * <li> add<i>ElementName</i>(Element aParent) for methods conditionally adding elements to the aParent
 * <li> create<i>ElementName</i>() for methods creating an element (un-conditionally), the caller adds it
 * to the appropriate parent.
 * </ul>
 * Both types methods should return the created element (the former returns null if nothing was not created).
 * Note that escaping is automatically handled by the jdom's outputer.
 *
 * @todo Move jdom/xml things into a subclass (this must support also non-xml formats)
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public abstract class LayerWriter<L extends Layer<?>> extends DataWriter<L> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerWriter.class);

    protected LayerWriter(String aNamespace) {
        super(aNamespace);
    }

    protected LayerWriter(Namespace aNamespace) {
        super(aNamespace);
    }
    
    protected org.jdom.Element rootEtc(String aRootElement, String aSchema) {
        final org.jdom.Element root = el(aRootElement);

        root.addContent( createHeadE(aSchema) );
        
        return root;
    }

    /**
     * Saves references to linked data (other layers and/or tagsets).
     * 
     * @param aHead 
     */
    @Override
    protected void addReferencesE(org.jdom.Element aHead) {
        final Collection<RefVar> references = new ArrayList<>(data.getReferences().values());
        references.addAll(data.getMissingLayers());

        if (references.isEmpty() && data.getTagsets().isEmpty()) return;

        org.jdom.Element referencesEl = el("references");
        aHead.addContent(referencesEl);

        // references to layers
        for (RefVar ref : references) {
            org.jdom.Element referenceEl = el("reffile");
            referenceEl.setAttribute("id", ref.getId());
            if (ref.getName() != null) {
                referenceEl.setAttribute("name", ref.getName());
            }
            referenceEl.setAttribute("href", ref.getHRef());

            referencesEl.addContent(referenceEl);
        }
        
        addTagsets(referencesEl);
    }

    /**
     * Add tagsets references to the references element.
     *
     * @param aParent references element to add tagsets too.
     */
    protected void addTagsets(org.jdom.Element aReferencesRoot)  {
        final Map<String,Tagset<?>> id2tagset = data.getTagsets();

        for (Map.Entry<String,Tagset<?>> ut : id2tagset.entrySet()) {
            org.jdom.Element referenceEl = el("reffile");
            referenceEl.setAttribute("id", ut.getKey());        // local id of the tagset within this layer
            referenceEl.setAttribute("name", "tagset");
            referenceEl.setAttribute("href", ut.getValue().getId());  // unique id of the tagset (incorporates id+version)

            aReferencesRoot.addContent(referenceEl);
        }
    }

    /**
     * Create a reference to an ided element, e.g. <pre><w.rf>w#w-doc1p1s2w3</w.rf></pre>.
     *
     * If the ided element is in another layer, the layers id is automatically added to the reference.
     *
     * @param aElementName name of this reference. The ".rf" suffix is not added.
     * @param aEl element to refer to, can be in another layer.
     * @return reference element
     */
    protected org.jdom.Element rfEx(String aElementName, IdedElement aEl) {
        return el(aElementName).addContent(data.getRef(aEl));
    }

    /**
     * Create a reference to an ided element, e.g. <pre><w.rf>w#w-doc1p1s2w3</w.rf></pre>.
     *
     * If the ided element is in another layer, the layers id is automatically added to the reference.
     *
     * @param aElementName name of this reference. Automatically suffixed with ".rf" (e.g. w.rf).
     * @param aEl element to refer to, can be in another layer.
     * @return reference element
     */
    protected org.jdom.Element rfE(String aElementName, IdedElement aEl) {
        return rfEx(aElementName + ".rf", aEl);
    }

    /**
     * Create a references to ided elements, e.g.
     * <pre>
     *    <w.rf>w#w-doc1p1s2w3</w.rf>
     *    <w.rf>w#w-doc1p1s2w4</w.rf>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>.
     * The elements are added in the same order as provided by the iterable.
     * 
     * @param aElementName name of this reference. The ".rf" suffix is not added.
     * @param aEls elements to refer to, can be in another layer.
     * @return
     */
    protected List<org.jdom.Element> rfEsx(String aElementName, Iterable<? extends IdedElement> aEls) {
        final List<org.jdom.Element> els = new ArrayList<>();

        for(IdedElement e : aEls) {
            els.add(rfEx(aElementName, e));
        }

        return els;
    }

    /**
     * Create references to ided elements, e.g.
     * <pre>
     *    <w.rf>w#w-doc1p1s2w3</w.rf>
     *    <w.rf>w#w-doc1p1s2w4</w.rf>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>.
     *
     * @param aElementName name of this reference. Automatically suffixed with ".rf" (e.g. w.rf).
     * @param aEls elements to refer to, can be in another layer.
     * @return
     */
    protected List<org.jdom.Element> rfEs(String aElementName, Iterable<? extends IdedElement> aEls) {
        return rfEsx(aElementName + ".rf", aEls);
    }

    /**
     * Create an element encoding one or more eferences to ided elements using the LM list encoding..
     *
     * One reference:
     * <pre>
     *    <w.rf>w#w-doc1p1s2w3</w.rf>
     * </pre>.

     * Multiple references:
     * <pre>
     *    <w.rf>
     *      <LM>w#w-doc1p1s2w3</LM>
     *      <LM>w#w-doc1p1s2w4</LM>
     *      <LM>w#w-doc1p1s2w5</LM>
     *    </w.rf>
     * </pre>
     *
     * @param aElementName
     * @param aEls
     * @return null when aEls is empty
     */
    protected org.jdom.Element rfELM(String aElementName, List<? extends IdedElement> aEls) {
        return rfELMx(aElementName + ".rf", aEls);
    }

    /**
     * Create an element encoding one or more eferences to ided elements using the LM list encoding..
     *
     * As {@link #rfELM(java.lang.String, java.util.List)} but does not add .rf automatically to the
     * element name.
     *
     * @param aElementName
     * @param aEls
     * @return null when aEls is empty
     */
    protected org.jdom.Element rfELMx(String aElementName, List<? extends IdedElement> aEls) {
        if (aEls.size() == 1) {
            return rfEx(aElementName, aEls.get(0));
        }
        else if (aEls.size() > 1) {
            final org.jdom.Element result = el(aElementName);
            for(IdedElement e : aEls) {
                addContent(result, "LM", data.getRef(e));
            }
            return result;
        }
        else {
            return null;
        }
    }

}
