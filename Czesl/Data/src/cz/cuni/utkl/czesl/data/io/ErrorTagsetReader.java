package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import java.util.Map;
import org.jdom.Element;
import org.purl.jh.pml.ts.io.TagsetReader;
import org.purl.jh.util.err.Err;
import org.purl.jh.pml.ts.Tag;
import org.purl.jh.pml.ts.Tagset;
import org.purl.jh.pml.ts.TagsetRegistry;

/**
 *
 * @author jirka
 */
public class ErrorTagsetReader extends TagsetReader<ErrorTag,ErrorTagset> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ErrorTagsetReader.class);    
    
    public ErrorTagsetReader() {
        super("http://purl.org/jh/lea/");
    }
    
    @Override
    protected ErrorTagset createLayer() {
        return new ErrorTagset();
    }

    @Override
    protected void processJdom(Element aRootElement) {
        processHead(aRootElement);

        processIdEtc(aRootElement);
        
        processTags(aRootElement.getChild("tags", n));
    }

    protected void processHead(org.jdom.Element aHeadE)  {
    }
    
    protected void processIdEtc(final org.jdom.Element aRoot) {
        tagset = createLayer();

        tagset.setId(getText(aRoot, "tid"));
        tagset.setDescription(getText(aRoot, "descr"));
        

        for (Map.Entry<String,String> e : readProperites(aRoot).entrySet() ) {
            tagset.setProperty(e.getKey(), e.getValue());
        }
    }

    protected void processTags(final org.jdom.Element aTagsetE) {
        for (org.jdom.Element tagE : getChildren(aTagsetE, "tag")) {
            tagset.add(processTag(tagE));
        }
    }
    
    protected ErrorTag processTag(final org.jdom.Element aTagE) {
        String id    = getText(aTagE, "tid");
        String descr = getText(aTagE, "descr");
        // todo handle menu label!

        boolean auto        = getBoolean(aTagE, "auto");
        boolean deprecated  = getBoolean(aTagE, "deprecated");
        int minLLegs        = getInteger(aTagE, "minLLegs");
        int maxLLegs        = getInteger(aTagE, "maxLLegs");
        int minULegs        = getInteger(aTagE, "minULegs");
        int maxULegs        = getInteger(aTagE, "maxULegs");
        int minLinks        = getInteger(aTagE, "minLinks");
        int maxLinks        = getInteger(aTagE, "maxLinks");
        
        ErrorTag tag = new ErrorTag(tagset, id, descr, auto, deprecated, id, minLLegs, maxLLegs, minULegs, maxULegs, minLinks, maxLinks); 

        final Map<String,String> map = readProperites(getElement(aTagE, "properties"));
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
    protected <T extends Tag<?>> void processTagsets(org.jdom.Element aRoot)  {
        final org.jdom.Element tagsetsE = aRoot.getChild("tagsets", n);
        log.info("tagsets: %s", tagsetsE);

        if (tagsetsE != null) {
            for (org.jdom.Element e : getChildren(tagsetsE, "tagset")) {

                Tagset<?> embedTs = null;
                Tagset<?> refTs = null;
                String use = "??"; // todo this layer

//                // embedded tagset - deprecated
//                if (!e.getChildren().isEmpty()) {
//                    embedTs = readEmbeddedTagset(e);
//                }

                final String tid     = e.getAttributeValue("tid");    // unique id of the tagset

                // referrenced tagset
                if (tid != null) {
                    String version = e.getAttributeValue("version"); // version of the tagset
                    use     = e.getAttributeValue("use"); // version of the tagset (unlike domain, this must be unique)
                    Err.fAssert(use != null, "Tagset element requires the use attribute.");
                    // todo min / max version
        //                String lg      = e.getAttributeValue("lg");
        //                String domain  = e.getAttributeValue("domain");
        //                String type    = e.getAttributeValue("type");

                    refTs = TagsetRegistry.getDefault().getTagset(tid, version);
                    if (refTs == null) log.severe("Unknown tagset %s (%s)", tid, e);    // if not embedded, caught by if-else below
                }


                final Tagset<?> ts;
                if (refTs != null) {
                    // todo handle case when the tagset is both referrenced and embedded but is different
                    ts = refTs;
                }
                else if (embedTs != null) {
                    ts = embedTs;
                }
                else {
                    throw Err.fErr("Unknown tagset.");
                }
            }
        }
    }

//    private ErrorTagset readEmbeddedTagset(final Element e) {
//        final Tagset<?> embedTs = getUserSpecifiedTagset(e);
//        for (Element tagE : getChildren(e)) {
//            addTag(embedTs, tagE);
//        }
//
//        return embedTs;
//    }
    
}
