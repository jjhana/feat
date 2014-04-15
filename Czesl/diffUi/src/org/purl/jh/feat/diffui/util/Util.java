package org.purl.jh.feat.diffui.util;

import com.google.common.base.Predicate;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.feat.diffui.OpenPanel;
import org.purl.jh.feat.layered.ParaModel;
import org.purl.jh.util.Pair;
import org.purl.jh.util.err.XException;

/**
 *
 * @author j
 */
public class Util {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Util.class);
    
    public static LLayerDataObject dobj(File aFile) {
        return dobj(FileUtil.toFileObject(aFile));
    }
    
    public static LLayerDataObject dobj(FileObject aFObj) {
        final String fileStr = FileUtil.getFileDisplayName(aFObj);

        LLayerDataObject dobj;
        try {
            dobj = (LLayerDataObject)DataObject.find(aFObj);
        }
        catch (DataObjectNotFoundException ex) {
            throw new XException(ex, "The file %s cannot be found", fileStr);       // todo use user ex
        }
        catch (ClassCastException ex) {
            throw new XException(ex, "The file %s does not contain a feat a/b layer", fileStr);
        }
        

        try {
            dobj.getData();
        } catch (Throwable ex) {
            throw new XException(ex, "Error loading file %s.", fileStr);
        }
        
        return dobj;
    }

//    public Pair<DataObject,DataObject> getTwoDObjs(List<DataObject> context) {
//        if (context.size() != 2) {
//            for (;;) {
//                final JButton ok = new JButton(bundle.getString("OpenDiffAction.OK"));
//                final JButton cancel = new JButton(bundle.getString("OpenDiffAction.Cancel"));
//                final OpenPanel p = new OpenPanel(ok);
//
//                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(p, bundle.getString("OpenDiffAction.title"));
//                nd.setOptions(new Object[]{ok, cancel});
//                Object o = DialogDisplayer.getDefault().notify(nd);
//
//                if (o != ok) return null;
//                try {
//                    DataObject dobj1 = Util.dobj(p.getFile1());
//                    DataObject dobj2 = Util.dobj(p.getFile2());
//                    return new Pair<>(dobj1, dobj2);
//                }
//                catch(XException ex) {
//                    Util.message(ex, "Cannot open the documents (%s)", ex.getMessage());
//                }
//            }
//        }
//        else {
//            return new Pair<>(context.get(0), context.get(1));
//        }
//    }
    
    
    public static void message(XException ex, String aTemplate, Object ... aParams) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(String.format(aTemplate, aParams));
        DialogDisplayer.getDefault().notify(nd);
    }
    
    /**
     * Returns the form which is closest to the anchor and satisfies a predicate
     * 
     * @todo rename, nothing is moved
     * @param <F>
     * @param paramodel
     * @param anchor
     * @param isFormOk
     * @param forward
     * @return 
     */
    public static <F extends FForm> F move(ParaModel paramodel, F anchor, Predicate<F> isFormOk, boolean forward) {
        final List<F> forms = paramodel.getNodes((FormsLayer<?>)anchor.getLayer());
        final int idx = move(forms, anchor, isFormOk, forward);
        return idx == -1 ? null : forms.get(idx);
    }

    /**
     * Returns the form which is closest to the anchor and satisfies a predicate
     * 
     * @todo rename, nothing is moved
     * 
     * @param <F>
     * @param items
     * @param anchor
     * @param predicate
     * @param forward
     * @return 
     */
    public static <F extends FForm> int move(List<F> items, F anchor, Predicate<F> predicate, boolean forward) {
        final int anchorIdx = items.indexOf(anchor);
        
        if (forward) {
            for (int i = anchorIdx+1; i < items.size(); i++) {
                F form = items.get(i);
                if (predicate.apply(form)) return i;
            }
            return -1;
        }
        else {
            for (int i = anchorIdx-1; i >= 0; i--) {
                F form = items.get(i);
                if (predicate.apply(form)) return i;
            }
            return -1;
        }
    }
    
}
