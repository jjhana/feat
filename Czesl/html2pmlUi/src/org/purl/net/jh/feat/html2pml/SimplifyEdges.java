package org.purl.net.jh.feat.html2pml;

import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.nbpml.LayerProvider;
import org.purl.jh.pml.Layer;
import org.purl.jh.feat.filesaction.RecFolderOutputAction;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.XException;

/**
 * Converts to the new format (edges inside of forms, sorted legs)
 * Todo: split dt
 *
 * todo: This should not be here at all. It should have its own module.
 *
 * @author j
 */
@ActionID(category = "Tools",
id = "org.purl.jh.feat.diffui.SimplifyEdges")
@ActionRegistration(displayName = "#CTL_SimplifyEdges")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 128),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1580)
})
@Messages("CTL_SimplifyEdges=Simplify Edges")
public final class SimplifyEdges extends RecFolderOutputAction{

    public SimplifyEdges(List<DataObject> context) {
        super(context, "Simplifying Edges", "Simplifying Edges");
    }


    @Override
    public void processSingle(FileObject aFObj) {
        int errCountBefore = userOutput.getErrorCount();

        // load the top file only
        if (!aFObj.getNameExt().endsWith(".b.xml")) return;
        
        try {
            processSingleE(aFObj);
        }
        catch (Throwable a) {
            userOutput.severe(a, "Cannot simplify DObj %s (%s)", aFObj, a.getMessage());
        }

        if (userOutput.getErrorCount() == errCountBefore) {
            userOutput.info("Successfully fixed %s", FileUtil.toFile(aFObj));
        }
    }
    
    
    private void processSingleE(FileObject aFObj) {
        DataObject dobj = loadDobj(aFObj);
        LLayer layer = getTopLayer(dobj);
        
        saveAll(layer.getLowerFormsLayersEq());
        
        try {
            dobj.setValid(false);
        } catch (PropertyVetoException ex) {
            userOutput.warning("Cannot invalidate DObj %s (%s)", layer.getFile(), ex.getMessage());
        }
        
    }
    
    private DataObject loadDobj(FileObject aFObj) {
        try {
            return DataObject.find(aFObj);   
        } catch (DataObjectNotFoundException ex) {
            throw Err.fErr(ex, "Cannot open DObj %s", aFObj);
        }
    }
    
    private LLayer getTopLayer(DataObject dobj) {
        final LayerProvider layerProvider = dobj.getLookup().lookup(LayerProvider.class);

        Err.fAssert(layerProvider != null, "Dataobject %s does contain any layer", dobj);
        Layer<?> layer = layerProvider.getLayer(userOutput);  
        Err.fAssert(layer instanceof LLayer, "Layer in %s is not an LLayer", dobj);

        return ((LLayer)layer);
    }

    private void saveAll(List<FormsLayer<?>> layers) {
        for (FormsLayer<?> layer : layers) {
            DataObject dobj = null;
            try {
                dobj = DataObject.find(layer.getFile());
            } catch (DataObjectNotFoundException ex) {
                throw new XException(ex);
            }
            
            dobj.setModified(true);
            SaveCookie save = dobj.getLookup().lookup(SaveCookie.class);
            Err.fAssert(save != null, "DObj %s does not have save cookie", dobj);

            try {
                save.save();
            } catch (IOException ex) {
                String reason = layer.getFile().canWrite() ? "" : "- The file is read only"; 
                throw Err.fErr(ex, "Cannot save %s %s", layer.getFile().toString(), reason);
            }

            // we do not need it anymore, remove it from memory
            try {
                dobj.setValid(false);
            } catch (PropertyVetoException ex) {
                userOutput.warning("Cannot invalidate DObj %s (%s)", layer.getFile(), ex.getMessage());
            }
        }
    }
}
