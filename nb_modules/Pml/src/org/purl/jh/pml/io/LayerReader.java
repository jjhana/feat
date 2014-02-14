package org.purl.jh.pml.io;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.RefVar;
import org.purl.jh.pml.ts.Tagset;
import org.purl.jh.util.col.XCols;

/**
 * Superclass of readers of various pml layers.
 *
 * Note: tagset handling is still not settled. We cannot really use references in
 * the usual way, as say positional tagset file is not listing all the tags (but
 * instead specifies the ts constraints), and an avm tagset might be even infinite.
 *
 * This is not a problem in a layer with a single tagset, because simply the
 * tagset used, is the only referenced tagset. For layers, with
 * multiple tagsets, we per-use the id: its values are fixed an denote the use of the tagset.
 *
 * @param <L> type of the handled data
 * @author Jirka
 */
public abstract class LayerReader<L extends Layer<?>> extends DataReader<L> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerReader.class);

    public LayerReader(String aNamespace) {
        this(Namespace.getNamespace(aNamespace));
    }

    public LayerReader(Namespace aNamespace) {
        super(aNamespace);
    }


// -----------------------------------------------------------------------------
// <editor-fold desc="Support Reading">
// -----------------------------------------------------------------------------

    protected void rootEtc(org.jdom.Element aRoot) {
        processHead(aRoot);
    }


    protected void processHead(org.jdom.Element aRoot)  {
        readReferences(getReqChild(aRoot, "head"));
    }

    /**
     * Reads references to other layers.
     * @param aHead
     */
    protected void readReferences(final Element aHead) {
        final Element references = aHead.getChild("references", n);
        log.info("references: %s", references);

        if (references == null) return;

        for (Element e : JDomUtil.getChildren(references)) {
            log.fine(" reference: %s", e);
            final String id   = e.getAttributeValue("id");     // id to use to refer to the resource within this layer
            final String name = e.getAttributeValue("name");
            final String href = e.getAttributeValue("href");

            if ("tagset".equals(name)) {    // experimental, ugly, redo, do we need it at all?
                // todo ignore for now
            }
//            else if ("tagset".equals(name)) {    // experimental, ugly, redo, do we need it at all?
//                // todo would be nice to have uid instead of href, possibly some desc and versions
//                // href -> (tid,version)
//                String tid = href;
//                String version = "";
//                String desc = "";
//                
//                Tagset<?> tagset = null;
//                try {
//                    tagset = layerLoader.getTagset(this, tid, version, desc);
//                }
//                catch(Throwable ex) {
//                    getErr().warning("Could not load tagset: " + ex.getMessage());       // todo this is ugly
//                }
//                
//                
//                if (tagset != null) {
//                    data.addTagset(id, tagset);
//                }
//                else {
//                    handleUnloadableTagset(tid, version, id, desc);
//                }
//            }
//			// usual non-tagset layer
            else {
                final Layer<?> referredLayer = layerLoader.getLayer(this, id, name, href);
                if (referredLayer != null) {
                    final RefVar refVar = new RefVar(id, name, href, referredLayer);
                    data.addRefVar(refVar);
                }
                else if (optionalLayer(id, name, href)) {
                    final RefVar refVar = new RefVar(id, name, href, null);
                    data.getMissingLayers().add(refVar);
                }
                else {
                    err.fatalError("Cannot load layer %s referenced from %s", href, fileObject);
                }
            }
        }
    }

    /**
     * Override to handle unloadable tagsets. By default causes fatal error.
     * @param uid
     * @param lid
     * @param desc
     */
    protected void handleUnloadableTagset(String uid, String version, String lid, String desc) {
        err.fatalError("Tagset %s is missing", uid);
    }

    /**
     * Defines whether a referenced data can be ignored when not present.
     * Queried by the {@link #layerLoader}.
     * The default implementation requires all layers to be read in.
     *
     * @return true if the data can be ignored when not present; false if it is
     * required.
     */
    public boolean optionalLayer(String id, String name, String href) {
        return false;
    }

    /**
     * May generate a default referenced data when it is missing.
     * Used by the {@link #layerLoader}.
     * The default implementation just returns null.
     *
     * @param id
     * @param name
     * @param href
     * @return a data to replace missing referenced data, or null.
     */
    public Layer<?> generateLayer(String id, String name, String href) {
        return null;
    }

// -----------------------------------------------------------------------------
// <editor-fold desc="support for references">
// -----------------------------------------------------------------------------

    /**
     *
     * @param aRoot
     * @param aElementName
     * @return list of reference ids
     */
    protected String getRf(Element aRoot, String aElementName) {
        return getRfx(aRoot, aElementName + ".rf");
    }

    /**
     * Returns a list (LM) of rf elements. Supports abbreviated notation.
     *
     * <pre>
     *    <w.rf>
     *      <LM>w#w-doc1p1s2w5</LM>
     *      <LM>w#w-doc1p1s2w6</LM>
     *    </w.rf>
     * </pre>
     *
     * Singleton list can be abbreviated as:
     * <pre>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>
     *
     * @param aRoot
     * @param aElementName
     * @return list of reference ids
     */
    protected List<String> getRfLM(Element aRoot, String aName) {
        final List<String> rfs = new ArrayList<>();
        for (Element lmE : getLM(aRoot, aName + ".rf")) {
            rfs.add(lmE.getTextNormalize());
        }
        return rfs;
    }

    /**
     *
     * @param aRoot
     * @param aElementName
     * @return list of reference ids
     */
    protected List<String> getRfs(Element aRoot, String aElementName) {
        return getRfsx(aRoot, aElementName + ".rf");
    }

    /**
     *
     * @param aRoot
     * @param aName
     * @return list of reference ids.  Does not add ".rf" suffix.
     */
    protected List<String> getRfsx(Element aRoot, String aName) {
        final List<String> rfs = XCols.newArrayList();
        for (Element rfE : getChildren(aRoot, aName)) {
            rfs.add(rfE.getTextNormalize());
        }
        return rfs;
    }

    /**
     * @param aRoot
     * @param aName
     * @return list of reference ids.  Does not add ".rf" suffix.
     */
    protected String getRfx(Element aRoot, String aName) {
        final Element e = aRoot.getChild(aName, n);
        return (e == null) ? null : e.getTextNormalize();
    }


    /**
     * Resolve a references, e.g.
     * <pre>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>.
     *
     * @param <E>
     * @param aRoot
     * @param aElementName
     * @return
     */
    protected <E extends IdedElement> E resolveRfE(org.jdom.Element aRoot, String aElementName) {
        return this.<E>resolveRfEx(aRoot, aElementName + ".rf");
    }

    /**
     * Resolve a sequence of references, e.g.
     * <pre>
     *    <w.rf>w#w-doc1p1s2w3</w.rf>
     *    <w.rf>w#w-doc1p1s2w4</w.rf>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>.
     *
     * @param <E>
     * @param aElementName name of this reference. Automatically suffixed with ".rf" (e.g. w.rf).
     * @return
     */
    protected <E extends IdedElement> List<E> resolveRfEs(org.jdom.Element aRoot, String aElementName) {
        return this.<E>resolveRfEsx(aRoot, aElementName + ".rf");
    }

    /**
     * Resolve a references, e.g.
     * <pre>
     *    <w>w#w-doc1p1s2w5</w>
     * </pre>.
     *
     * @param <E>
     * @param aRoot
     * @param aElementName. Does not add ".rf" suffix.
     * @return
     */
    protected <E extends IdedElement> E resolveRfEx(org.jdom.Element aRoot, String aElementName) {
        final String rf = getRfx(aRoot, aElementName);
        return rf == null ? null : data.<E>getElement(rf);
    }

    /**
     * Resolve a sequence of references, e.g.
     * <pre>
     *    <w>w#w-doc1p1s2w3</w>
     *    <w>w#w-doc1p1s2w4</w>
     *    <w>w#w-doc1p1s2w5</w>
     * </pre>.
     *
     * @param <E>
     * @param aElementName name of this reference. Does not add ".rf" suffix.
     * @return
     */
    protected <E extends IdedElement> List<E> resolveRfEsx(org.jdom.Element aRoot, String aElementName) {
        final List<E> objs = new ArrayList<>();

        for(String rf : getRfsx(aRoot, aElementName)) {
            objs.add( data.<E>getElement(rf) );
        }

        return objs;
    }

    /**
     * Reads comment for an potentially commented element. Empty comments are 
     * read as no comments.
     * @param aElement jdom element to read comment from
     * @param aCommented element to potentially add comment to
     */
    protected void readComment(org.jdom.Element aElement, Commented aCommented) {
        String comment = getText(aElement, "comment");
        if (comment != null) {
            comment = comment.trim();
            if (!"".equals(comment)) aCommented.setComment( getText(aElement, "comment") );
        }
    }

// </editor-fold>
}
