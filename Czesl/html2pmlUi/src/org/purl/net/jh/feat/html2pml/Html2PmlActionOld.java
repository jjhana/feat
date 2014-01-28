package org.purl.net.jh.feat.html2pml;

import org.purl.net.jh.nbutil.io.NbOutLogger;
import org.purl.net.jh.nbutil.io.IoUtils;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.io.XFile;

/**
 * Action performing html to pml conversion for selected files.
 * 
 * @todo add support for links (see http://blogs.oracle.com/geertjan/entry/identifying_the_png_files_in)
 * @author jirka
 */
public final class Html2PmlActionOld implements ActionListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Html2PmlActionOld.class);    
    
    private final List<DataObject> context;
    /** Logger for user directed output (connected with output tab via {@link #io}. */

    private CountingLogger errLogger;
    private NbOutLogger nbOut;

    private static final InputOutput io;
    
    static {
        io = IOProvider.getDefault().getIO("Html2Pml Output", false);
    }

    public Html2PmlActionOld(List<DataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        process();
    }
    
    public void process() {
        nbOut = new NbOutLogger(io, "Html2Pml Inner Logger", null);
        errLogger = new CountingLogger(nbOut);  // todo this should be done via handler

        io.select();

        for (DataObject dataObject : context) {
            process(dataObject.getPrimaryFile());
            try {
                dataObject.setValid(false);
            } catch (PropertyVetoException ex) {
                log.info("Cannot invalidate dobj %s", dataObject);
            }
        }
        
        if (errLogger.getErrorCount() > 0) {
            IoUtils.println(io, "THERE WERE ERRORS! See the log above.", NbOutLogger.finalErrColor);
            IoUtils.println(io, "Number of errors = " + errLogger.getErrorCount(), NbOutLogger.errColor);
        }
        else {
            IoUtils.println(io, "Conversion was successfull", NbOutLogger.finalSuccesColor);
        }

        if (errLogger.getWarningCount() > 0) {
            IoUtils.println(io, "Number of warnings = " + errLogger.getWarningCount(), NbOutLogger.warningColor);
        }
    }
    
    // todo push into superclass
    public void process(FileObject aFObj) {
        log.info("process: %s, folder: %s; mime: %s\n", aFObj, aFObj.isFolder(),  aFObj.getMIMEType());
        if (aFObj.isFolder()) {
            for (FileObject fo : aFObj.getChildren()) {
                process(fo);
            }
        }
        else {
            if (aFObj.getMIMEType().equals("text/html")) {
                processSingle(aFObj);
            }
        }
    }

    public void processSingle(FileObject aFObj) {
        int errCountBefore = errLogger.getErrorCount();
        //System.out.println("Before:" + errCountBefore);

        File file = FileUtil.toFile(aFObj);
        convert(file);

        if (errLogger.getErrorCount() == errCountBefore) {
            errLogger.info("Successfully converted %s", file);
        }
    }
    
    private void convert(File file) {
        // todo check if files exists, warn about overwrite
        final File outPrefix = new File(file.getParent(), new XFile(file).getNameOnly());

        new cz.cuni.utkl.czesl.html2pml.Main().translate(file, outPrefix, errLogger);
        FileUtil.refreshFor(file.getParentFile());
    }

}
