package org.purl.jh.feat.filesaction;

import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.purl.jh.util.CountingLogger;

/**
 *
 * @author jirka
 */
public abstract class RecFolderOutputAction extends RecFolderAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(RecFolderOutputAction.class);    
    
    protected CountingLogger userOutput;
    protected NbOutLogger errLogger;

    protected final InputOutput io;
    
    public RecFolderOutputAction(List<DataObject> context, String aProcessName, String aWindowTitle) {
        super(context, aProcessName);
        io = IOProvider.getDefault().getIO(aWindowTitle, true); // reuse if previous process finished (but we do not want two processes writing to the same io at the same time)
    }

    @Override
    protected InputOutput getIo() {
        return io;
    }

    /**
     * @return true if the process can be started, false if not.
     * 
     * todo reuse io only if 
     */
    @Override
    protected boolean beforeProcessing() {
        errLogger = new NbOutLogger(io, name + " Inner Logger", null); 
        userOutput = new CountingLogger(errLogger);  // todo this should be done via handler

        io.select();

        IoUtils.println(io, "Running " + name + " ...", NbOutLogger.infoColor); 
        
        return true;
    }

    @Override
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
    
    
    @Override
    public abstract void processSingle(FileObject aFObj);
    
    

    
}
