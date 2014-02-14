package org.purl.jh.feat.NbData;

import cz.cuni.utkl.czesl.data.io.WLayerReader;
import cz.cuni.utkl.czesl.data.io.WLayerWriter;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.purl.jh.pml.io.LayerReader;
import org.purl.jh.pml.io.LayerWriter;

public class WLayerDataObject extends FeatLayerDataObject<WLayer> {

    public WLayerDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        MIMEResolver a;
    }

    @Override
    protected LayerReader<WLayer> getReader() {
        return new WLayerReader();
    }

    @Override
    protected LayerWriter<WLayer> getWriter() {
        return new WLayerWriter();
    }



}
