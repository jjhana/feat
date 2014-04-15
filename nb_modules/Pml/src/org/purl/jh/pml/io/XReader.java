package org.purl.jh.pml.io;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.Data;
import org.purl.jh.util.err.ErrorHandler;

public interface XReader<L extends Data<?>> {
    L read(FileObject aFile, NbLoader aLayerLoader, ErrorHandler aErr) throws IOException;
}
