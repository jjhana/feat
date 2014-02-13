  package org.purl.jh.pml.ts.io;

import org.purl.jh.pml.ts.Tagset;
import java.io.IOException;
import java.io.OutputStream;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.io.DataWriter;
import org.purl.jh.util.io.IO;

/**
 * Uses pseudo pml format.
   * 
 * @author jirka
 */
public abstract class TagsetWriter<T extends Tagset<?>> extends DataWriter<T> {
    
    protected T tagset;
    
    protected TagsetWriter(String aTagset) {
        super(Namespace.getNamespace(aTagset));
    }
    
    /**
     *
     * @param aTagset
     * @param aFile
     * @throws IOException
     */
    public void saveTagset(T aTagset, FileObject aFile) throws IOException {
        tagset = aTagset;

        OutputStream o = null;
        try {
            o = aFile.getOutputStream();

            final org.jdom.Element root = createJdom();
            final org.jdom.Document doc = new org.jdom.Document(root);

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(false).setLineSeparator("\n"));
            //log.fine("Omit decl: %s", outputter.getFormat().getOmitDeclaration());
            outputter.output(doc, o);
        }
        finally {
            IO.close(o);
        }
    }

    @Override
    protected abstract Element createJdom();
    
}
