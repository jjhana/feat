package org.purl.jh.pml;

import org.purl.jh.pml.NonpmlLayer;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.filesystems.FileObject;


/**
 * HtmlLayer to be injected into a HtmlDataObject
 * @author Jirka
 */
public class HtmlLayer extends NonpmlLayer {
    private final HTMLDocument doc;
    private final HTMLEditorKit kit;

    public HtmlLayer(FileObject aFile, String aId, HTMLDocument aDoc, HTMLEditorKit aKit) {
        super(aFile, aId);

        doc = aDoc;
        kit = aKit;
    }

    public HTMLDocument getDoc() {
        return doc;
    }

    public HTMLEditorKit getKit() {
        return kit;
    }

}
