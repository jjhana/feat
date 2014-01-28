package org.purl.net.jh.nb.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.OpenSupport;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.text.DataEditorSupport;
import org.openide.windows.CloneableTopComponent;
import org.purl.net.jh.nbutil.XDataObject;

public class HtmlDataObject extends XDataObject {

    static class HtmlOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {
        public HtmlOpenSupport(MultiDataObject.Entry entry) {
            super(entry);
        }

        @Override
        protected CloneableTopComponent createCloneableTopComponent() {
            return new HtmlTopComponent( (HtmlDataObject)entry.getDataObject() );
        }
    }


    private final HTMLEditorKit kit = new HTMLEditorKit();
    private HTMLDocument doc;

    public HtmlDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        final CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add((Node.Cookie) new HtmlOpenSupport(getPrimaryEntry()));
    }



    public HTMLEditorKit getKit() {
        return kit;
    }

    public HTMLDocument getDoc() {
        synchronized (this) {
            if (doc == null) {
                load();
            }
        }
        return doc;
    }

    protected synchronized void load() {
        Reader r = null;
        InputStream s1 = null;
        InputStream s2 = null;
        try {
            s1 = getPrimaryFile().getInputStream();
            final String enc = org.purl.jh.util.io.Html.getEncoding(s1);

            s2 = getPrimaryFile().getInputStream();
            r = new InputStreamReader(s2, enc);
            doc = org.purl.jh.util.io.Html.load(r);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        finally {
            org.purl.jh.util.io.IO.close(s1,r,s2);
        }
    }
}

