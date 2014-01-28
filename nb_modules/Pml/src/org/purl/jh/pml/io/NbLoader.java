package org.purl.jh.pml.io;

import org.purl.jh.pml.Layer;
import org.purl.jh.util.err.ErrorHandler;
import org.purl.jh.pml.ts.Tagset;

/**
 * Retrieves the layer in a specified file, loading it if not loaded yet.
 *
 * Note: It is called NbLoader for historical reasons, it would be better to call it Ref(erence)Loader.
 * 
 * @author Jirka
 */
public interface NbLoader {
    public Tagset   getTagset(LayerReader<? extends Layer<?>> curLayer, String uid, String version, String desc);
    public Layer<?> getLayer (LayerReader<? extends Layer<?>> curLayer, String id, String name, String href);
    /** todo experimental - reacts to error/warnings when processing the document */

    public ErrorHandler getErr();
    public void setErr(ErrorHandler err);
}
