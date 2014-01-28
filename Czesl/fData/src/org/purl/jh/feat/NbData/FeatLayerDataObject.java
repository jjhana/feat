package org.purl.jh.feat.NbData;

import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.purl.jh.nbpml.LayerDataObject;
import org.purl.jh.pml.io.LayerReader;
import org.purl.jh.pml.io.LayerWriter;
import org.purl.jh.util.err.ErrorHandler;

/**
 * Superclass of all Czesl dobjs layers, i.e. of W/A/B/C-layers.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public abstract class FeatLayerDataObject<L extends FormsLayer<?>>  extends LayerDataObject<L> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(FeatLayerDataObject.class);

    public FeatLayerDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);

        log.fine("Creating do: %s", this);
        CookieSet cookies = getCookieSet();
        cookies.add((Node.Cookie) new FeatLayerOpenSupport(getPrimaryEntry()));
    }

    @Override
    protected L loadData(ErrorHandler aErr) throws IOException {
        final L layer = super.loadData(aErr);
        return layer;
    }



    @Override
    protected abstract LayerReader<L> getReader();

    @Override
    protected abstract LayerWriter<L> getWriter();

    @Override
    public String toString() {
        return "FeatLayerDataObject{" + super.toString() +
                ", modified: %s" + isModified() +
                ", layer modified: %s" + getData().isModified() + '}';
    }
}
