package org.purl.jh.feat.iaa;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.execution.ExecutionEngine;
import org.openide.execution.ExecutorTask;
import org.openide.loaders.DataObject;
import org.openide.util.Cancellable;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.purl.jh.util.CountingLogger;
import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;


/**
 * @author jirka
 */
public abstract class ConsoleAction implements ActionListener, Runnable, Cancellable {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(org.purl.jh.feat.filesaction.RecFolderAction.class);

    protected final List<DataObject> context;
    protected final String name;

    protected CountingLogger userOutput;
    protected NbOutLogger errLogger;

    protected final InputOutput io;

    protected ExecutorTask task = null;
    protected ProgressHandle handle = null;

    public ConsoleAction(DataObject context, String aProcessName, String aWindowTitle) {
        this(Collections.singletonList(context), aProcessName, aWindowTitle);
    }

    public ConsoleAction(List<DataObject> context, String aProcessName, String aWindowTitle) {
        this.context = context;
        this.name = aProcessName;
        io = IOProvider.getDefault().getIO(aWindowTitle, true); // reuse if previous process finished (but we do not want two processes writing to the same io at the same time)
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        ExecutionEngine engine = ExecutionEngine.getDefault();
        task = engine.execute(ev.getActionCommand(), this, getIo());
    }

    protected InputOutput getIo() {
        return io;
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
        initConsole();
        if (!beforeProcessing()) return;
        processCore();
        afterProcessing();
    }

    /**
     * Initializes console loggers, etc.
     * @return 
     */
    protected boolean initConsole() {
        errLogger = new NbOutLogger(io, name + " Inner Logger", null); 
        userOutput = new CountingLogger(errLogger);  // todo this should be done via handler

        io.select();

        IoUtils.println(io, "Running " + name + " ...", NbOutLogger.infoColor); 
        
        return true;
    }

    /**
     * @return true if the process can be started, false if not.
     * 
     * todo reuse io only if 
     */
    protected boolean beforeProcessing() {
        return true;
    }

    public abstract void processCore();
    
    protected void afterProcessing() {
        if (userOutput.getErrorCount() > 0) {
            IoUtils.println(io, "THERE WERE ERRORS! See the log above.", NbOutLogger.finalErrColor);
            IoUtils.println(io, "Number of errors = " + userOutput.getErrorCount(), NbOutLogger.errColor);
        }
        else {
            IoUtils.println(io, name + " was successfull", NbOutLogger.finalSuccesColor); // todo
        }

        if (userOutput.getWarningCount() > 0) {
            IoUtils.println(io, "Number of warnings = " + userOutput.getWarningCount(), NbOutLogger.warningColor);
        }
    }
    
    
    
}
