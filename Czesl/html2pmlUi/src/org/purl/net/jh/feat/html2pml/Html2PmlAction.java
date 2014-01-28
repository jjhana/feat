package org.purl.net.jh.feat.html2pml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.util.io.Files;
import org.purl.jh.util.io.XFile;
import org.purl.jh.feat.filesaction.RecFolderOutputAction;

@ActionID(category = "Tools",
id = "org.purl.net.jh.feat.html2pml.Any2Pml")
@ActionRegistration(displayName = "#CTL_Any2Pml", iconBase="org/purl/net/jh/feat/html2pml/html2pml.png", iconInMenu=true)
@ActionReferences({
    //@ActionReference(path = "Menu/Tools", position = 131),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/html/Actions", position = 1575)
})
@Messages("CTL_Any2Pml=Convert html file(s) to feat format")
public final class Html2PmlAction extends RecFolderOutputAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Html2PmlAction.class);    
    
    public Html2PmlAction(List<DataObject> context) {
        super(context, "Html2Pml Conversion", "Html2Pml");
    }

    @Override
    protected boolean beforeProcessing() {
        return warning() && super.beforeProcessing();
    }

    @Override
    protected boolean isFileProcessed(FileObject aFObj) {
        return "text/html".equals(aFObj.getMIMEType());
    }
    
    FileObject problemDir = null;
    
    @Override
    public void processSingle(FileObject aFObj) {
        int errCountBefore = userOutput.getErrorCount();

        File file = FileUtil.toFile(aFObj);
        convert(file);

        if (userOutput.getErrorCount() == errCountBefore) {
            if (file.getName().endsWith("htm")) file.renameTo(Files.replaceExtension(file, "htm", "html"));
            userOutput.info("Successfully converted %s", file);
        }
        else {
            moveToProblemDirectory(aFObj);
        }
    }

    
    
    private void convert(File file) {
        // todo check if files exists, warn about overwrite
        final File outPrefix = new File(file.getParent(), new XFile(file).getNameOnly());

        new cz.cuni.utkl.czesl.html2pml.Main().translate(file, outPrefix, userOutput);
    }

    private void moveToProblemDirectory(FileObject aFObj) {
        userOutput.info("Moving %s to problemDir.", aFObj);
    
        if (problemDir == null) {
            problemDir = aFObj.getParent().getFileObject("problem");    // check if it already exist

            if (problemDir == null) {
                try {
                    problemDir = aFObj.getParent().createFolder("problem");
                } catch (IOException ex) {
                    userOutput.severe(ex, "Error creating problem subdirectory. Problematic file %s left where it was.", aFObj);
                    return;
                }
            }
        }
        
        FileLock lock = null;
        try {
            lock = aFObj.lock();
            aFObj.move(lock, problemDir, aFObj.getName(), aFObj.getExt());
        } catch (IOException ex) {
            userOutput.severe(ex, "Error moving %s to the problem subdirectory.", aFObj);
        }
        finally {
            if (lock != null) lock.releaseLock();
        }
    }

    
}
