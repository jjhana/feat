package org.purl.jh.feat.iaa;

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
import org.purl.jh.feat.iaa.Project2W.Counter;
import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;

/**
 *
 * @author jirka
 */
@ActionID(category = "Tools",
id = "org.purl.jh.feat.iaa.FolderProject2WAction")
@ActionRegistration(displayName = "#CTL_FolderProject2WAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 131),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 1575)
})
@Messages("CTL_FolderProject2WAction=Project tags and emendations to W-layer")
public class Project2WAction extends RecFolderOutputAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Project2WAction.class);    
    
    private final Counter counter = new Project2W.Counter();
    
    public Project2WAction(List<DataObject> context) {
        super(context, "Tag/Emendation projection", "te2w");
    }

    @Override
    protected boolean beforeProcessing() {
        return warning() && super.beforeProcessing();
    }

    @Override
    protected void afterProcessing() {
        super.afterProcessing();
        // todo format
        IoUtils.println(io, "" + counter.totalWForms + "- number of all w-forms", NbOutLogger.infoColor);
        IoUtils.println(io, "" + counter.totalEForms + "- number of all emendating forms", NbOutLogger.infoColor);
        IoUtils.println(io, "" + counter.inserted    + "- number of inserted tokens (no w-layer counterpart)", NbOutLogger.infoColor);
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
        new Project2W((LLayerDataObject)dobj, counter).project();
    }
    
    // todo experimental/temporary
    
    
    
}
