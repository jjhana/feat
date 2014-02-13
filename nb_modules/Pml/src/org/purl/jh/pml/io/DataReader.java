package org.purl.jh.pml.io;

import java.io.IOException;
import org.jdom.Namespace;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.Data;
import org.purl.jh.util.err.ErrorHandler;

/**
 * Super-class of regular and tagset layers.
 
 * @author jirka
 * @param <L>
 */
public abstract class DataReader<L extends Data<?>> extends JDomReader {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(DataReader.class);

    /**
     * The data being read in.
     */
    protected L data;
    /**
     * Object providing referenced layers (loading them if not loaded yet)
     */
    protected NbLoader layerLoader;

    public DataReader(String aNamespace) {
        this(Namespace.getNamespace(aNamespace));
    }

    public DataReader(Namespace aNamespace) {
        super(aNamespace);
    }

    public L getData() {
        return data;
    }

    /**
     * Loads the data in the specified file, loading referenced layers as well (if not already loaded)
     *
     * @param aFile file to read the data from
     * @param aLayerLoader object retrieving referenced layers
     * @param aErr
     * @return
     * @throws IOException
     * @todo support a general source, not just a file (maybe it is already general??)
     */
    public L read(FileObject aFile, NbLoader aLayerLoader, ErrorHandler aErr) throws IOException {
        log.info("readLayer %s", aFile);
        err = aErr;
        layerLoader = aLayerLoader;
        layerLoader.setErr(err);
        fileObject = aFile;
        data = createLayer();
        jdom = readXml(aFile);
        processJdom(jdom.getRootElement());
        // todo ! if (isImporting()) data.setModified(true);
        return data;
    }

    /**
     * Creates an empty data to be filled by this reader.
     * @return
     */
    protected abstract L createLayer();
    
}
