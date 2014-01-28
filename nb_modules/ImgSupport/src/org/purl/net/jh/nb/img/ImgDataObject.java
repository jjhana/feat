package org.purl.net.jh.nb.img;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
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
import org.purl.net.jh.nbutil.XDataObject;

public class ImgDataObject extends XDataObject {
    static class ImgOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {
        public ImgOpenSupport(MultiDataObject.Entry entry) {
            super(entry);
        }

        @Override
        protected CloneableTopComponent createCloneableTopComponent() {
            return new ImgTopComponent( (ImgDataObject)entry.getDataObject() );
        }
    }


    private BufferedImage img = null;

    public ImgDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) new ImgOpenSupport(getPrimaryEntry()));
    }


    public BufferedImage getImg() {
        synchronized (this) {
            if (img == null) {
                load();
            }
        }
        return img;
    }

    protected synchronized void load() {
        try {
            img = ImageIO.read(getPrimaryFile().getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("todo cannot read image");
        }
    }
}
