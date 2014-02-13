package org.purl.jh.nbpml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.err.ErrorHandler;
/**
 * Superclass of all layer data-objects.
 * Not ready for multithreading.
 *
 * Note: listening/detecting layer modification is messy, buggy
 * @author Jirka Hana
 */
public abstract class LayerDataObject<L extends Layer<?>> extends DataDataObject<L> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerDataObject.class);

    private transient final List<LayerDataObject<?>> linkedDObjs = new ArrayList<>();

    public LayerDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.assign(LayerProvider.class, new LayerProvider() {
            @Override public L getLayer(ErrorHandler aErr) {
                return LayerDataObject.this.getData(aErr);
            }
        });

    }

    /** This probably does not work at all */
    public List<LayerDataObject<?>> getLinkedDObjs(ErrorHandler aErr) {
        getData(aErr);
        return linkedDObjs;
    }


}