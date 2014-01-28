package cz.cuni.utkl.czesl.data.layerl;

import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import org.purl.jh.pml.Element;

/**
 * Creates a text representation of a document.
 *
 * @author Jirka Hana
 */
public class DocFormatter {
    public static String format(WDoc aDoc) {
        return new DocFormatter(aDoc).sb.toString();
    }

// =============================================================================
// Implementation
// =============================================================================    
    private final WDoc doc;
    private final StringBuilder sb;

    /** Creates a new instance of DocFormatter */
    private DocFormatter(WDoc aDoc) {
        doc = aDoc;
        sb = new StringBuilder(doc.size() * 7);
        fillDoc();
    }

    private void fillDoc() {
        for (Element element : doc.col()) {
            if (element instanceof WPara) {
                addPara((WPara)element);
            }
        }
    }   

    private void addPara(WPara aPara) {
        for (Element element : aPara.col()) {
            if (element instanceof FForm) {
                WForm f = (WForm) element;
                f.setDocOffset(sb.length());
                sb.append(f.getToken());
                if (f.hasSpaceAfter()) sb.append(' ');
            }
        }
        sb.append('\n');
    }
}
