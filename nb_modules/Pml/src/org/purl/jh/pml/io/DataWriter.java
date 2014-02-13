package org.purl.jh.pml.io;

import java.io.IOException;
import java.io.OutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.Data;
import org.purl.jh.util.io.IO;

/**
 *
 * @author jirka
 */
public abstract class DataWriter<L extends Data<?>> extends JDomWriter {
    /**
     * The layer being saved in.
     */
    protected L data;

    protected DataWriter(String aNamespace) {
        this(Namespace.getNamespace(aNamespace));
    }

    public DataWriter(Namespace n) {
        super(n);
    }

    // -----------------------------------------------------------------------------
    // <editor-fold desc="Support">
    // -----------------------------------------------------------------------------

    protected Element createHeadE(String aSchema) {
        final Element headE = el("head");
        headE.addContent(el("schema").setAttribute("href", aSchema + ".xml"));
        addReferencesE(headE);
        return headE;
    }

    /**
     * Saves references to other data.
     * Overriden for layers, tagsets currently do not link other tagsets, other \
     * data are currently not supported.
     */
    protected void addReferencesE(org.jdom.Element aHead) {}

    /**
     * Typically the overriden function first calls {@link #rootEtc(schema, schema)}
     * and then the main body.
     *
     * @return
     */
    @Override
    protected abstract Element createJdom();

    /**
     * Note: does not set modified flag to false, as this can be used as Save To, etc.
     *
     * @param aData
     * @param aFile
     * @throws IOException
     * @todo move this (without the data parameter to JDomWriter
     */
    public void save(L aData, FileObject aFile) throws IOException {
        data = aData;
        OutputStream o = null;
        try {
            o = aFile.getOutputStream();
            final Element root = createJdom();
            final Document doc = new Document(root);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setOmitDeclaration(false).setLineSeparator("\n"));
            //log.fine("Omit decl: %s", outputter.getFormat().getOmitDeclaration());
            outputter.output(doc, o);
        } finally {
            IO.close(o);
        }
    }

}
