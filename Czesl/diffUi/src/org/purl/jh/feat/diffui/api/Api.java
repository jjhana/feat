package org.purl.jh.feat.diffui.api;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.windows.TopComponent;
import org.purl.jh.feat.diffui.DiffPanel;
import org.purl.jh.feat.diffui.DiffTopComponent;
import org.purl.jh.nbpml.LayerDataObject;
import org.purl.net.jh.nbutil.Notify;

/**
 * todo decouple!!
 * @author j
 */
public class Api {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Api.class);
    
    public static void openDiff(FileObject file1, FileObject file2) {
        final DataObject dobj1 = getDataObject(file1);
        final DataObject dobj2 = getDataObject(file2);
        openDiff((LayerDataObject<?>)dobj1, (LayerDataObject<?>)dobj2);
    } 
    
    public static void openDiff(LayerDataObject<?> dobj1, LayerDataObject<?> dobj2) {
        // todo check compatibility? 
        String name = dobj1.getName() + " / " + dobj2.getName();
        DiffPanel panel = new DiffPanel(dobj1,dobj2); // todo use lookup instead of casting
        TopComponent tc = new DiffTopComponent(name,panel);
        tc.open();
        tc.requestFocus();
    }
    
    /** To util */
    private static DataObject getDataObject(FileObject aFile) {
        try {
            return DataObject.find(aFile);
        } catch (DataObjectNotFoundException ex) {
            String msg = String.format("Cannot open %s",  aFile);
            Notify.error(ex, msg);
            log.severe(ex, msg);
            return null; // todo
        }
    }
    
}
