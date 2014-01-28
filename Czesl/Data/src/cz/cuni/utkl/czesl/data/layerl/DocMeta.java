package cz.cuni.utkl.czesl.data.layerl;

import org.purl.jh.pml.AbstractElement;


/**
 * Currently works for both - doc meta and otherdoc meta
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class DocMeta extends AbstractElement {
    
    /** Creates a new instance of DocMeta */
    public DocMeta(String aOtherMetaOrig, String aOtherMeta) {
        mOtherMetaOrig = aOtherMetaOrig;
        mOtherMeta = aOtherMeta;
    }
    
    
    public String getOtherMetaOrig() {
        return mOtherMetaOrig;
    }

    public String getOtherMeta() {
        return mOtherMeta;
    }
    
// =============================================================================
// <editor-fold desc="Implementation" defaultstate="collapsed">
// =============================================================================
    private String mOtherMetaOrig;
    private String mOtherMeta;
    
    
// </editor-fold>

}
