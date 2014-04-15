package org.purl.jh.feat.layered;

import com.google.common.base.Preconditions;
import java.io.IOException;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.purl.jh.pml.Data;
import org.purl.jh.pml.Layer;

/**
 * todo: merge with layer
 * @author j
 */
public abstract class LayerModelSupport {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerModelSupport.class);

    
    public static DataObject getDObj(Data aData) {
        try {
            return DataObject.find(aData.getFile());
        } catch (DataObjectNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Recursively saves data. 
     * If aData is a layer object, referenced layers are saved by depth-first traversal.
     * 
     * @param aData
     * @throws IOException 
     */
    public static void save(final Data aData) throws IOException {
        log.info("--- Save cookie ---");
        final DataObject dobj = getDObj(aData);
        if (dobj.isModified()) {
            Preconditions.checkNotNull(dobj.getCookie(SaveCookie.class)).save();
        }
        
        // todo shouldn we use their dobj's to save them
        if (aData instanceof Layer) {
            log.info("--- Saving referenced layers ---");
            for (Layer<?> l : ((Layer<?>)aData).getReferencedLayers()) {
                save(l);
            }
        }
        
        log.info("--- Saved cookie ---");
    }
    

    
    
}
