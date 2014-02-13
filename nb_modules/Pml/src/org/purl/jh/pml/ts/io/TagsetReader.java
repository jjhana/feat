package org.purl.jh.pml.ts.io;

import org.purl.jh.pml.ts.Tagset;
import org.purl.jh.pml.ts.Tag;
import org.purl.jh.pml.ts.PositionalTagset;
import org.purl.jh.pml.ts.SimpleAtomicTagset;
import java.io.IOException;
import java.util.Map;
import org.jdom.Element;
import org.jdom.Namespace;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.io.DataReader;

/**
 * A general super class for reading tagset specifications. Subclass to handle particular tagsests.
 * 
 * todo move out of PML into a tagset module?
 * 
 * 
 * @author jirka
 */
public abstract class TagsetReader<T extends Tag<Ts>, Ts extends Tagset<T>> extends DataReader<Ts> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(TagsetReader.class);    

    protected Ts tagset;
    
    public TagsetReader(String aNamespace) {
        super(Namespace.getNamespace(aNamespace));
    }

    /**
     * Loads the layer in the specified file, loading referenced layers as well (if not already loaded)
     *
     * @param aFile file to read the layer from
     * @param aLayerLoader object retrieving referenced layers
     * @return
     * @throws IOException
     * @todo support a general source, not just a file (maybe it is already general??)
     */
    public Ts readTagset(FileObject aFile) throws IOException {
        log.info("readTagset %s", aFile);
        fileObject   = aFile;

        jdom = readXml(aFile);

        processJdom(jdom.getRootElement());

        // todo ! if (isImporting()) layer.setModified(true);
        return tagset;
    }

    /**
     * Creates an empty layer to be filled by this reader.
     * @return
     */
    @Override
    protected abstract Ts createLayer(); 
    
    @Override
    protected abstract void processJdom(Element aRootElement);
    
    
    
// -----------------------------------------------------------------------------
// <editor-fold desc="Tagset Support">
// -----------------------------------------------------------------------------

    protected Tagset<?> getUserSpecifiedTagset(Element aTagsetElement) {
        if (true) return new PositionalTagset(null, null, null, null);
        String domain = "???"; // todo
        return new SimpleAtomicTagset("", "User Defined", null, domain);
    }

    protected void addTag(Element tagE) {
        tagset.add( this.readTag(tagE) );
    }

    /**
     * Override to handle non-generic tags
     * @param tagE
     * @return
     */
    protected T readTag(Element tagE) {
        final String tid   = getText(tagE, "tid");      // tag id
        final String descr = getText(tagE, "descr");    // description
        final Map<String,String> map = readProperites(getElement(tagE, "properties"));
        
        final T tag = tagset.createTag(tid, descr);
        tag.getProperties().putAll(map);

        return tag;
    }



    /**
     * Loads or retrieves tagsets.
     * Each tagset is either embedded or is referred to by a unique id.
     * Under construction.
     *
     * @param <T>
     * @param aRoot
     */
//    protected <T extends Tag> void processTagsets(org.jdom.Element aRoot)  {
//        final org.jdom.Element tagsetsE = aRoot.getChild("tagsets", n);
//        log.info("tagsets: %s", tagsetsE);
//
//        if (tagsetsE != null) {
//            for (org.jdom.Element e : getChildren(tagsetsE, "tagset")) {
//
//                Tagset<?> embedTs = null;
//                Tagset<?> refTs = null;
//                String use = "??"; // todo this layer
//
//                // embedded tagset - deprecated
//                if (!e.getChildren().isEmpty()) {
//                    embedTs = readEmbeddedTagset(e);
//                }
//
//                final String tid     = e.getAttributeValue("tid");    // unique id of the tagset
//
//                // referrenced tagset
//                if (tid != null) {
//                    String version = e.getAttributeValue("version"); // version of the tagset
//                    use     = e.getAttributeValue("use"); // version of the tagset (unlike domain, this must be unique)
//                    Err.fAssert(use != null, "Tagset element requires the use attribute.");
//                    // todo min / max version
//        //                String lg      = e.getAttributeValue("lg");
//        //                String domain  = e.getAttributeValue("domain");
//        //                String type    = e.getAttributeValue("type");
//
//                    refTs = TagsetRegistry.getDefault().getTagset(tid);
//                    if (refTs == null) log.severe("Unknown tagset %s (%s)", tid, e);    // if not embedded, caught by if-else below
//                }
//
//
//                final Tagset ts;
//                if (refTs != null) {
//                    // todo handle case when the tagset is both referrenced and embedded but is different
//                    ts = refTs;
//                }
//                else if (embedTs != null) {
//                    ts = embedTs;
//                }
//                else {
//                    Err.fErr("Unknown tagset.");
//                    ts = null;
//                }
//            }
//        }
//    }
//
//    private <T extends Tag> Tagset<?> readEmbeddedTagset(final Element e) {
//        final Tagset<?> embedTs = getUserSpecifiedTagset(e);
//        for (Element tagE : getChildren(e)) {
//            addTag(embedTs, tagE);
//        }
//
//        return embedTs;
//    }
    
}
