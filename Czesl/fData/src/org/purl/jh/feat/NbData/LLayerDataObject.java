package org.purl.jh.feat.NbData;

import cz.cuni.utkl.czesl.data.io.LLayerReader;
import cz.cuni.utkl.czesl.data.io.LLayerWriter;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.purl.jh.pml.io.LayerReader;
import org.purl.jh.pml.io.LayerWriter;

/**
 * Superclass of layers above w-layer.
 * @author jirka
 */
public class LLayerDataObject extends FeatLayerDataObject<LLayer> {

    public LLayerDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    protected LayerReader<LLayer> getReader() {
        return new LLayerReader();
    }

    @Override
    protected LayerWriter<LLayer> getWriter() {
        return new LLayerWriter();
    }


}
