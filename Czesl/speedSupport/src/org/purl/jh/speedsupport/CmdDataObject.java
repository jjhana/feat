package org.purl.jh.speedsupport;

import org.purl.jh.speedsupport.data.CmdLayer;
import java.io.IOException;
import java.io.Writer;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.purl.jh.pml.io.JDomReader;


/**
 * DataObject for Speed's Cmd file
 *
 * todo create a general jdom DataObject
 * todo Consider moving some functions from here and from DataDataObject to XDataObject
 * @author jirka
 */
public class CmdDataObject extends XxDataObject<CmdDataObject.Data> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(CmdDataObject.class);
    
    public static class Data  {
        final protected org.jdom.Document jdom;

        public Data(org.jdom.Document jdom) {
            this.jdom = jdom;
        }

        public org.jdom.Document getJdom() {
            return jdom;
        }
    }



    public CmdDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        log.info("CmdDataObject %s", FileUtil.getFileDisplayName(getPrimaryFile()));

        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add((Node.Cookie) new CmdLayer(this));      // as a commandprovider
    }



    class Reader extends JDomReader {
        public Reader() {
            super(org.jdom.Namespace.NO_NAMESPACE);
        }

        @Override
        protected void processJdom(org.jdom.Element aRootElement) {
            // not needed in this context
        }

        public void read() throws IOException {
            err = getUserOut();
            fileObject = getPrimaryFile();
            jdom = readXml(fileObject);
        }
    };

    @Override
    protected Data read() {
        Reader reader = new Reader();
        try {
            reader.read();
        } catch (IOException ex) {
            getUserOut().severe(ex, "I/O error while reading command file %s", getPrimaryFile());
            return null;
        }

        return new Data(reader.getJdom());
    }


    @Override
    public void write(Writer w) throws IOException {
        final Document doc = getData().getJdom();
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(false).setLineSeparator("\n"));
        outputter.output(doc, w);
    }
}
