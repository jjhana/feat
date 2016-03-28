package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import org.purl.jh.pml.io.JDomUtil;
import org.purl.jh.pml.io.LayerWriter;

/**
 * Translates an LLayer into the corresponding jdom structure.
 *
 * TODO parametrize adata/adata_schema
 * TODO under development - changing to the new format which include edges into
 * the first form they refer to.
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class WLayerWriter extends LayerWriter<WLayer> {

    public WLayerWriter() {
        super("http://utkl.cuni.cz/czesl/");
    }



    @Override
    protected org.jdom.Element createJdom() {
        final org.jdom.Element root = rootEtc("wdata", "wdata_schema");

        for (WDoc doc : data.col()) {
            root.addContent(createDocE(doc));
        }
        
        return root;
    }

    private org.jdom.Element createDocE(final WDoc aDoc) {
        final org.jdom.Element docE = el(Xml.DOC, aDoc);

        for (WPara para : aDoc.col()) {
            docE.addContent( createParaE(para) );
        }
        if (aDoc.getCorrections() != null) {
            docE.addContent(aDoc.getCorrections().detach());
        }

        return docE;
    }

    private org.jdom.Element createParaE(final WPara aPara) {
        final org.jdom.Element paraE = el(Xml.PARA, aPara);
        JDomUtil.setAttribute(paraE, "li", aPara.isLi(), false);

        for (WForm w : aPara.getForms()) {
            paraE.addContent(createFormE(w));
        }

        // todo save corrections

        return paraE;
    }

    private org.jdom.Element createFormE(final WForm aForm) {
        final org.jdom.Element wE = el(Xml.W, aForm);

        if (!aForm.getType().normal()) {
            addContent(wE, Xml.TYPE, aForm.getType().name());
        }

        addContent(wE, Xml.TOKEN, aForm.getToken());
        
        for (String altToken : aForm.getAltTokens()) {
            addContent(wE, "alt", altToken);
        }
        
        if ( aForm.getOldToken() != null && !aForm.getToken().equals(aForm.getOldToken()) ) {
            addContent(wE, "oldToken", aForm.getOldToken());
        }

        wE.addContent(
            el("original_position")
                .setAttribute("from", String.valueOf(aForm.getDocOffset()))     
                .setAttribute("len",  String.valueOf(aForm.getLen()))
        );
        
        addComment(wE, aForm);
        
        //todo printFormats(aToken);
        
        writeFlag(wE, "gr",   aForm.isForeignScript());
        //todo writeFlag(wE, "st", aForm.isSt());
        writeFlag(wE, "no_space_after", !aForm.hasSpaceAfter());

        return wE;
    }

    /** Todo move lower */
    private void writeFlag(org.jdom.Element aParent, String aElementName, boolean aFlag) {
        if (aFlag) {
            addContent(aParent, aElementName, "1");
        }
    }
    
    //</editor-fold>
}
