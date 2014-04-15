package org.purl.jh.pml.io;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.Data;

public interface XWriter<L extends Data<?>> {
    void save(L aData, FileObject aFile) throws IOException;
}
