package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import org.purl.jh.pml.ts.io.TagsetWriter;

/**
 * Todo move some parts into the atomic and general tagset reader.
 * @author jirka
 */
public class ErrorTagsetWriter extends TagsetWriter<ErrorTagset> {

    public ErrorTagsetWriter() {
        super("http://purl.org/jh/lea/");
    }
    
    
    @Override
    protected org.jdom.Element createJdom() {
        final org.jdom.Element root = el("tagset");

        root.addContent( createHeadE("errtagset_schema") );
        addIdEtc(root);
        root.addContent(createTagsE());

        return root;
    }

    protected org.jdom.Element createHeadE(String aSchema)  {
        final org.jdom.Element headE = el("head");

        headE.addContent( el("schema").setAttribute("href", aSchema + ".xml") );
        //addReferences(headE);

        return headE;
    }
    
    protected void addIdEtc(final org.jdom.Element aRoot) {
        addContent(aRoot, "tid",     tagset.getId());
        //addContent(aRoot, "version", tagset.getVersion());
        addContent(aRoot, "desc",    tagset.getDescr());
        // addContent(aRoot, "domain",  tagset.getDomain());
        // addContent(aRoot, "lg",      tagset.getLg());

        super.addProperites(aRoot, tagset.getProperties());
    }
    
    private org.jdom.Element createTagsE() {
        final org.jdom.Element tagsetE = el("tagset");

        for (ErrorTag tag : tagset.getTags()) {
            tagsetE.addContent( createTagE(tag) );
        }

        return tagsetE;
    }

    protected org.jdom.Element createTagE(final ErrorTag aTag) {
        final org.jdom.Element tagE = el("tag");
        addContent(tagE, "tid",    aTag.getId());
        addContent(tagE, "desc",   aTag.getDescr());
        // todo handle menu label!

        addContent(tagE, "auto",        aTag.isAuto());
        addContent(tagE, "deprecated",  aTag.isDeprecated());
        addContent(tagE, "minLLegs",    aTag.getMinLowerLegs());
        addContent(tagE, "maxLLegs",    aTag.getMaxLowerLegs());
        addContent(tagE, "minULegs",    aTag.getMinUpperLegs());
        addContent(tagE, "maxULegs",    aTag.getMaxUpperLegs());
        addContent(tagE, "minLinks",    aTag.getMinLinks());
        addContent(tagE, "maxLinks",    aTag.getMaxLinks());
        
        // todo str -> obj addProperites(tagE, aTag.getProperties());
        
        return tagE;
    }
        
}
