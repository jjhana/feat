package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.purl.jh.pml.io.JDomUtil;
import org.purl.jh.pml.io.LayerReader;

/*
 * Reads in czesl w-layer.
 * Structure: doc - para - w
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class WLayerReader extends LayerReader<WLayer> {

    public WLayerReader() {
        super("http://utkl.cuni.cz/czesl/");
    }

    @Override
    protected WLayer createLayer() {
        return new WLayer(fileObject, "W");
    }

    @Override
    protected void processJdom(org.jdom.Element aRoot) {
        processHead(aRoot);

        for (org.jdom.Element docE : getChildren(aRoot, "doc")) {
            processDoc(docE);
        }
    }

    /** Layers containing scans are optional. */
    @Override
    public boolean optionalLayer(String id, String name, String href) {
        return href.endsWith(".jpg") || href.endsWith(".jpeg");
    }

    private void processDoc(final org.jdom.Element aDocE) {
        final WDoc doc = new WDoc(data, JDomUtil.getId(aDocE));
        data.addIdedElement(doc);
        data.add(doc);
        doc.setParent(data);

        for (org.jdom.Element paraE : getChildren(aDocE, "para") ) {
            processPara(doc, paraE);
        }

        doc.setCorrections( aDocE.getChild("corrections", n));
    }

    private void processPara(final WDoc aDoc, final org.jdom.Element aParaE) {
        final WPara para = new WPara(data, JDomUtil.getId(aParaE));
        data.addIdedElement(para);
        aDoc.add(para);

        boolean li = JDomUtil.getBoolAttribute(aParaE, "li", false);
        para.setLi(li);
        
        for (org.jdom.Element e : getChildren(aParaE, "w")) {
            processForm(para, e);
        }
    }

    private void processForm(final WPara aPara, final org.jdom.Element aFormE) {
        String id    = JDomUtil.getId(aFormE);

        String token = aFormE.getChildText("token", n);
        
        final List<String> altTokens = new ArrayList<>();
        for (Element e : (List<Element>) aFormE.getChildren("alt", n)) {
            altTokens.add(e.getText());
        }

//        // backward compatibility
//        List<Element> tokens = aFormE.getChildren("token", n);
//        if (tokens.size() > 1) {
//            for (Element e :tokens.subList(1, tokens.size())) {
//                altTokens.add(e.getText());
//            }
//        }

        // todo parse old-style alternatives
        // parseAlternatives

        String oldToken = aFormE.getChildText("oldToken", n); 
        if (oldToken == null) { // compatibility
            oldToken = aFormE.getChildText("altToken", n); 
        }
        
        org.jdom.Element pos = aFormE.getChild("original_position", n);
        int from = Integer.valueOf( pos.getAttributeValue("from") );
        int len  = Integer.valueOf( pos.getAttributeValue("len") );

        String tmp = aFormE.getChildText("no_space_after", n);
        boolean no_space = tmp == null || tmp.equals("0");  // TODO create util for reading bools

        tmp = aFormE.getChildText("gr", n);        // todo: very likely to change, do not provide gui yet
        boolean foreignScript = !(tmp == null || tmp.equals("0"));

        FForm.Type type = FeatLayerIo.getTokenType(this, aFormE);
        
        final WForm form = new WForm(data, id, token, altTokens, oldToken, type, no_space, from, len);
        form.setForeignScript(foreignScript);
        readComment(aFormE, form);

        data.addIdedElement(form);
        data.addForm(form);
        aPara.add(form);
    }

    

// </editor-fold>

}
