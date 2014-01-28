package cz.cuni.utkl.czesl.data.layerw;

import cz.cuni.utkl.czesl.data.layerl.DocFormatter;
import cz.cuni.utkl.czesl.data.layerl.DocMeta;
import cz.cuni.utkl.czesl.data.layerx.Doc;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.List;
import org.jdom.Element;
import org.netbeans.api.annotations.common.NonNull;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.IdedListElement;
import org.purl.jh.pml.Layer;

/**
 * Element representing a document. Its children are mainly paragraphs but
 * possibly also other elements.
 *
 * Currently ignoring corrections.
 *
 * @author Jirka Hana
 */
public class WDoc extends IdedListElement<WPara> implements Doc, Commented {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(WDoc.class);
    private DocMeta mDocMeta;


    /**
     * Text representation of the document
     * Cached ?
     */
        private String mDocStr;

    /*
     * Any comment an annotator may assign to this paragraph.
     */
    protected String comment;

    protected org.jdom.Element corrections;
    
// =============================================================================

    
    /** 
     * Creates a new instance of Document. 
     */
    public WDoc(@NonNull WLayer aLayer, @NonNull String aLocId) {
        super(aLayer, aLocId);
    }
    
// =============================================================================
// Fields
// =============================================================================
    
    public DocMeta getDocMeta() {
        return mDocMeta;
    }

    public void setDocMeta(DocMeta aDocMeta) {
        mDocMeta = aDocMeta;
    }


    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public WDoc setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Element getCorrections() {
        return corrections;
    }

    public void setCorrections(Element corrections) {
        this.corrections = corrections;
    }


    
    
// ===============================================================================    
//
// ===============================================================================    

    @Override
    public List<WPara> getParas() {
        return col();
    }


    /**
     * Returns the text representation of this document.
     * Implementation: The representation is created only when needed.
     *
     * @return the text representation of this document.
     * @see DocFormatter
     * @see Form#getDocOffset()
     */
    public String getDocStr() {
        if (mDocStr == null) {
            mDocStr = DocFormatter.format(this);
        }
        log.fine("Doc.getDocStr - length %d", mDocStr.length() );
        return mDocStr;
    }

}
