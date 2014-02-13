//package org.purl.jh.feat.iaa;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.Arrays;
//import java.util.List;
//import org.openide.awt.ActionReference;
//import org.openide.awt.ActionReferences;
//import org.openide.awt.ActionRegistration;
//import org.openide.awt.ActionID;
//import org.openide.util.NbBundle.Messages;
//import org.openide.filesystems.FileObject;
//import org.openide.filesystems.FileUtil;
//import org.openide.loaders.DataObject;
//import org.openide.loaders.DataObjectNotFoundException;
//import org.openide.util.Cancellable;
//import org.openide.windows.InputOutput;
//import org.purl.jh.feat.NbData.LLayerDataObject;
//import org.purl.jh.feat.iaa.Project2W.Counter;
//import org.purl.jh.util.err.Err;
//import org.purl.net.jh.nbutil.io.IoUtils;
//import org.purl.net.jh.nbutil.io.NbOutLogger;
//
///**
// *
// * @author jirka
// */
//@ActionID(category = "Tools",
//id = "org.purl.jh.feat.iaa.FolderProject2WActionX")
//@ActionRegistration(displayName = "#CTL_FolderProject2WActionX")
//@ActionReferences({
//    @ActionReference(path = "Menu/Tools", position = 131),
//    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
//    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 1575),
//    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 1575),
//    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 1575)
//})
//@Messages("CTL_FolderProject2WActionX=Calculate IAA")
//public class Project2WActionX implements ActionListener, Runnable, Cancellable { {
//    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Project2WActionX.class);    
//    private final List<DataObject> context;
//
//    protected final String name;
//    protected ExecutorTask task = null;
//    protected ProgressHandle handle = null;
//    
//    private final Counter counter = new Project2W.Counter();
//    
//    public Project2WActionX(List<DataObject> context) {
//        this.context = context;
//        name = "Calculating IAA";
//        
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent ev) {
//        ExecutionEngine engine = ExecutionEngine.getDefault();
//        task = engine.execute(ev.getActionCommand(), this, getIo());
//    }
//
//    protected InputOutput getIo() {
//        return InputOutput.NULL;
//    }
//
//
//    @Override
//    public void run() {
//        handle = ProgressHandleFactory.createHandle(name, this);
//        handle.start();
//        process();
//        handle.finish();
//    }
//
//    @Override
//    public boolean cancel() {
//        if (task != null) {
//            task.stop();
//            task.getInputOutput().getOut().println("Canceled.");
//            if (handle != null) handle.finish();
//        }
//        return false;
//    }
//
//
//    public void process() {
//        if (!beforeProcessing()) return;
//
//        //must contain two subfolders
//        Err.fAssert(context.size() == 2, "Need two folders to compare");         // todo two subfolders?
//        x
//        
//        for (DataObject dataObject : context) {
//            dataObject.getPrimaryFile().refresh();      // to make sure we operate on the latest directory tree
//            process(dataObject.getPrimaryFile());
//        }
//        afterProcessing();
//    }
//
//    /**
//     * Warning that can be used in an overriden version of {@link #beforeProcessing()}.
//     * @return true if the action should proceed.
//     */
//    protected boolean warning() {
//        return DialogDisplayer.getDefault().notify(
//            new NotifyDescriptor.Confirmation(
//                "Existing files will be overwritten. Proceed?",
//                "Warning",
//                NotifyDescriptor.YES_NO_OPTION,
//                NotifyDescriptor.WARNING_MESSAGE))
//            == NotifyDescriptor.YES_OPTION;
//    }
//
//    /**
//     * Override to perform pre -process actions (such as initializing user loggers, etc)
//     *
//     * @return false if the action should be aborted; true otherwise
//     */
//    protected boolean beforeProcessing() {return true;}
//
//    /** Override to perform post-process actions (such printing results, etc) */
//    protected void afterProcessing() {}
//
//    protected void process(FileObject aFObj) {
//        if (aFObj.isFolder()) {
//            inFolder(aFObj);
//            for (FileObject fo : aFObj.getChildren()) {
//                process(fo);
//            }
//        }
//        else {
//            if (isFileProcessed(aFObj)) {
//                processSingle(aFObj);
//                aFObj.refresh(true);
//            }
//        }
//    }
//
//    /** Override to respond to entering a folder. Default implementation does nothing */
//    protected void inFolder(FileObject aFObj) {}
//
//    /**
//     * Override to specify if a particular file should be passed to {@link #processSingle(org.openide.filesystems.FileObject)}.
//     * @param aFObj
//     * @return true if the file should be processed
//     */
//    protected boolean isFileProcessed(FileObject aFObj) {
//        return true;
//    }
//
//    /** Implement to process each individual file. */
//    public abstract void processSingle(FileObject aFObj);
//
//    
//
//    @Override
//    protected boolean beforeProcessing() {
//        return warning() && super.beforeProcessing();
//    }
//
//    @Override
//    protected void afterProcessing() {
//        super.afterProcessing();
//        // todo format
//        IoUtils.println(io, "" + counter.totalWForms + "- number of all w-forms", NbOutLogger.infoColor);
//        IoUtils.println(io, "" + counter.totalEForms + "- number of all emendating forms", NbOutLogger.infoColor);
//        IoUtils.println(io, "" + counter.inserted    + "- number of inserted tokens (no w-layer counterpart)", NbOutLogger.infoColor);
//    }
//    
//    
//    
//    @Override
//    protected boolean isFileProcessed(FileObject aFObj) {
//        return Arrays.asList("text/feat-l+xml", "text/feat-a+xml", "text/feat-b+xml").contains(aFObj.getMIMEType());
//    }
//    
//    @Override
//    public void processSingle(FileObject aFObj) {
//        String fileStr = FileUtil.getFileDisplayName(aFObj);
//        DataObject dobj;
//        try {
//            dobj = DataObject.find(aFObj);
//        } catch (DataObjectNotFoundException ex) {
//            userOutput.severe(ex, "The file %s cannot be found", fileStr);
//            return;
//        }
//
//        //((LLayerDataObject)dobj).setUserOut(userOutput); // todo!
//        try {
//            ((LLayerDataObject)dobj).getData();
//        } catch (Throwable ex) {
//            userOutput.severe(ex, "Error loading file %s.", fileStr);
//            return;
//        }
//
//        log.info("Projecting %s.", fileStr);
//        new Project2W((LLayerDataObject)dobj, counter).project();
//    }
//    
//    // todo experimental/temporary
//    
//    
//    
//}
