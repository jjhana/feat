package org.purl.jh.feat.export2vert;

import java.util.Arrays;
import java.util.List;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.feat.filesaction.RecFolderOutputAction;
import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;

@ActionID(
        category = "Tools",
        id = "org.purl.jh.feat.export2vert.Export2VertAction"
)
@ActionRegistration(
        displayName = "#CTL_Export2VertAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 131),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 1575)
})
@Messages("CTL_Export2VertAction=Export to vertical")
public class Export2VertAction extends RecFolderOutputAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Export2VertAction.class);    
    
    //private final Counter counter = new Project2W.Counter();
    
    public Export2VertAction(List<DataObject> context) {
        super(context, "Export to Vertical", "Export to Vertical");
    }

    @Override
    protected boolean beforeProcessing() {
        return warning() && super.beforeProcessing();
    }

    @Override
    protected void afterProcessing() {
        super.afterProcessing();
    }
    
    
    
    @Override
    protected boolean isFileProcessed(FileObject aFObj) {
        return Arrays.asList("text/feat-l+xml", "text/feat-a+xml", "text/feat-b+xml").contains(aFObj.getMIMEType());
    }
    
    @Override
    public void processSingle(FileObject aFObj) {
        String fileStr = FileUtil.getFileDisplayName(aFObj);
        DataObject dobj;
        try {
            dobj = DataObject.find(aFObj);
        } catch (DataObjectNotFoundException ex) {
            userOutput.severe(ex, "The file %s cannot be found", fileStr);
            return;
        }

        //((LLayerDataObject)dobj).setUserOut(userOutput); // todo!
        try {
            ((LLayerDataObject)dobj).getData();
        } catch (Throwable ex) {
            userOutput.severe(ex, "Error loading file %s.", fileStr);
            return;
        }

        log.info("Projecting %s.", fileStr);
        new Export2Vert((LLayerDataObject)dobj).project();
    }
    
    // todo experimental/temporary
    
    
    
}
