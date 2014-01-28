package org.purl.jh.nbpml;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.purl.jh.pml.Layer;
import org.purl.net.jh.nbutil.XDataObject;
import org.purl.jh.util.col.Cols;

/**
 * Used for injecting layer provider into dataobjects.
 * 
 * @author jirka
 */
public abstract class Mime2Layer {
    public final List<String> mimeTypes;

    public Mime2Layer(String ... aMimeTypes) {
        this.mimeTypes = Arrays.asList(aMimeTypes);
    }

    public  Collection<String> getMimeTypes() {
        return mimeTypes;
    }

    public abstract Layer<?> getLayer(XDataObject aDObj);

    @Override
    public String toString() {
        return "Mime2Layer{ mimeTypes=" + Cols.toString(mimeTypes) + '}';
    }

}
