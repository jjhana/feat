package org.purl.net.jh.nb.rtf;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.OpenSupport;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.windows.CloneableTopComponent;
import org.purl.jh.util.io.IO;
import org.purl.net.jh.nbutil.XDataObject;

/**
 * Not tested!
 * @author jirka
 */
public class RtfDataObject extends XDataObject {
    private final RTFEditorKit kit;
    private StyledDocument doc;

    static class RtfOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {
        public RtfOpenSupport(MultiDataObject.Entry entry) {
            super(entry);
        }

        @Override
        protected CloneableTopComponent createCloneableTopComponent() {
            return new org.purl.net.jh.nb.rtf.RtfTopComponent( (RtfDataObject)entry.getDataObject() );
        }
    }

    public RtfDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        kit = new RTFEditorKit();

        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) new RtfOpenSupport(getPrimaryEntry()));
    }


    public RTFEditorKit getKit() {
        return kit;
    }

    public StyledDocument getDoc() {
        synchronized (this) {
            if (doc == null) {
                load();
            }
        }
        return doc;
    }

    protected synchronized void load() {
        Reader r = null;
        try {
            r = new InputStreamReader(getPrimaryFile().getInputStream(), "utf8");    // TODO what with encoding
            doc = (StyledDocument)new RTFEditorKit().createDefaultDocument();
            kit.read(r, doc, 0);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        finally {
            IO.close(r);
        }

    }

}