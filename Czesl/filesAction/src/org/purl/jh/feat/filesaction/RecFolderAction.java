package org.purl.jh.feat.filesaction;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Cancellable;
import org.openide.windows.InputOutput;


/**
 * @author jirka
 */
public abstract class RecFolderAction implements ActionListener, Runnable, Cancellable {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(RecFolderAction.class);

    private final List<DataObject> context;

    protected final String name;
    protected ExecutorTask task = null;
    protected ProgressHandle handle = null;

    public RecFolderAction(DataObject context, String aName) {
        this(Collections.singletonList(context), aName);
    }

    public RecFolderAction(List<DataObject> context, String name) {
        this.context = context;
        this.name = name;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        ExecutionEngine engine = ExecutionEngine.getDefault();
        task = engine.execute(ev.getActionCommand(), this, getIo());
    }

    protected InputOutput getIo() {
        return InputOutput.NULL;
    }


    @Override
    public void run() {
        handle = ProgressHandleFactory.createHandle(name, this);
        handle.start();
        process();
        handle.finish();
    }

    @Override
    public boolean cancel() {
        if (task != null) {
            task.stop();
            task.getInputOutput().getOut().println("Canceled.");
            if (handle != null) handle.finish();
        }
        return false;
    }


    public void process() {
        if (!beforeProcessing()) return;

        for (DataObject dataObject : context) {
            dataObject.getPrimaryFile().refresh();      // to make sure we operate on the latest directory tree
            process(dataObject.getPrimaryFile());
        }
        afterProcessing();
    }

    /**
     * Warning that can be used in an overriden version of {@link #beforeProcessing()}.
     * @return true if the action should proceed.
     */
    protected boolean warning() {
        return DialogDisplayer.getDefault().notify(
            new NotifyDescriptor.Confirmation(
                "Existing files will be overwritten. Proceed?",
                "Warning",
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.WARNING_MESSAGE))
            == NotifyDescriptor.YES_OPTION;
    }

    /**
     * Override to perform pre -process actions (such as initializing user loggers, etc)
     *
     * @return false if the action should be aborted; true otherwise
     */
    protected boolean beforeProcessing() {return true;}

    /** Override to perform post-process actions (such printing results, etc) */
    protected void afterProcessing() {}

    protected void process(FileObject aFObj) {
        if (aFObj.isFolder()) {
            inFolder(aFObj);
            for (FileObject fo : aFObj.getChildren()) {
                process(fo);
            }
        }
        else {
            if (isFileProcessed(aFObj)) {
                processSingle(aFObj);
                aFObj.refresh(true);
            }
        }
    }

    /** Override to respond to entering a folder. Default implementation does nothing */
    protected void inFolder(FileObject aFObj) {}

    /**
     * Override to specify if a particular file should be passed to {@link #processSingle(org.openide.filesystems.FileObject)}.
     * @param aFObj
     * @return true if the file should be processed
     */
    protected boolean isFileProcessed(FileObject aFObj) {
        return true;
    }

    /** Implement to process each individual file. */
    public abstract void processSingle(FileObject aFObj);

}
