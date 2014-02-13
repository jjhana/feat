package org.purl.jh.nbpml;

import org.purl.jh.pml.Layer;
import org.purl.jh.util.err.ErrorHandler;


/**
 *
 * @author Jirka
 */
public interface LayerProvider  {
    /**
     * 
     * @param aErr if null a default user output is provided automatically
     * @return 
     */
    Layer<?> getLayer(ErrorHandler aErr);
}