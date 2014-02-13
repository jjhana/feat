package org.purl.net.jh.feat.html2pml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.feat.NbData.WLayerDataObject;
import org.purl.jh.util.io.Encoding;
import org.purl.jh.util.io.Files;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;
import org.purl.jh.feat.filesaction.RecFolderOutputAction;

@ActionID(category = "Tools",
id = "org.purl.net.jh.feat.html2pml.Pml2Html")
@ActionRegistration(displayName = "#CTL_Pml2Html", iconBase="org/purl/net/jh/feat/html2pml/html2pml.png", iconInMenu=true)      /// todo change icon
@ActionReferences({
    //@ActionReference(path = "Menu/Tools", position = 131),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-w+xml/Actions", position = 1575)
//    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 1575),
//    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 1575),
//    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 1575)
})
@Messages("CTL_Pml2Html=Convert w-layer file(s) to plain html")
public final class Pml2HtmlAction extends RecFolderOutputAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Pml2HtmlAction.class);    
    
    public Pml2HtmlAction(List<DataObject> context) {
        super(context, "w2html Conversion", "w2html");
    }

    @Override
    protected boolean beforeProcessing() {
        return warning() && super.beforeProcessing();
    }

    @Override
    protected boolean isFileProcessed(FileObject aFObj) {
        //return Arrays.asList("text/feat-w+xml", "text/feat-l+xml", "text/feat-a+xml", "text/feat-b+xml").contains(aFObj.getMIMEType());
        return Arrays.asList("text/feat-w+xml").contains(aFObj.getMIMEType());
    }
    
    FileObject problemDir = null;
    
    @Override
    public void processSingle(FileObject aFObj) {
        int errCountBefore = userOutput.getErrorCount();

        try {
            convert(aFObj);
        } catch (IOException ex) {
            userOutput.severe(ex, "Error processing %s", aFObj);
        }

        if (userOutput.getErrorCount() == errCountBefore) {
            userOutput.info("Successfully converted %s", aFObj);
        }
    }

    
    
    private void convert(FileObject aFObj) throws IOException {
        WLayerDataObject dobj = null;
        try {
            dobj = (WLayerDataObject) DataObject.find(aFObj);
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        // todo check if files exists, warn about overwrite
        File outFile = Files.replaceExtension(FileUtil.toFile(aFObj), "xml", "html");
        PrintWriter w = IO.openPrintWriter(new XFile(outFile, Encoding.cUtf8));
        
        new WLayer2Html(dobj, w).go();
        IO.close(w);
    }
}
