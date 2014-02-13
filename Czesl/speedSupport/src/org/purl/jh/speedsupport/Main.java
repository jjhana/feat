package org.purl.jh.speedsupport;

import cz.jager.uk.mff.ufal.Feat2Speedver2.IFeatInterface;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFile;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileContent;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileFolder;
import cz.jager.uk.mff.ufal.Feat2Speedver2.SynchronizeFactory;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.purl.jh.speedsupport.data.Box;
import org.purl.jh.speedsupport.data.SBundle;
import org.purl.jh.speedsupport.speedPanel.SpeedPanel;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.col.Cols;
import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;

/**
 * The document consists of multiple files. 
 * 
 * All moves, uploads, downloads work on the level of files. 
 * They should be written/deleted/moved
 * atomically. So each operation is marked by a marker file, which is deleted
 * if the operation succeeds. If a marker file is found, its document's
 * should be deleted. Move is done as copy all files followed by delete all files.
 *
 * 
 * @todo assumes that the gui does not allow concurrent modification, improve
 * todo reasonably report and react to errors - into an user output (as with batch operations)
 * todo the whole module needs refactoring, split gui, data, logic etc.
 * todo logging/error handling under development, errors are reported to the user logger but not to the main app logger
 * @author jirka
 */
public class Main {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Main.class);

    private final IFeatInterface featInterface;
    private final SpeedPanel gui; // currently used to access progress bar access only
    private final Conf conf = new Conf();

    private final Box inbox;
    private final Box outbox;
    private final Box backup;

    /**
     * Current progress (how many files were processed).
     *
     * Note: there is no parallelism, so we can do have this field.
     */
    private int progress;

    /**
     * Backup directory of this sync operation. The same file might be downloaded/uploaded
     * several times. So we would get duplicates. Instead, we save each sync batch
     * into a separate directory named by the current date and time.
     *
     * Note: there is no parallelism, so we can do have this field.
     */
    private FileObject datedBakFolder;

    private String bakDirPrefix;

    // todo merge io and userOutput
    protected CountingLogger userOutput;
    protected InputOutput io;

    public Main(SpeedPanel aGui) {
        io = IOProvider.getDefault().getIO("Speed server support", false);
        NbOutLogger errLogger = new NbOutLogger(io, "Speed server support", null);
        userOutput = new CountingLogger(errLogger);  // todo this should be done via handler
        // todo log to dev logger all user messages

        gui = aGui;

        featInterface = new FeatInterface(conf, this);

        inbox  = new Box(conf.getSpeedInboxFObj(), "Inbox", false);
        outbox = new Box(conf.getSpeedOutboxFObj(), "Outbox", true);
        backup = new Box(conf.getBakFolderFObj(), "Backup", true);
    }

    
    public IFeatInterface getFeatInterface() {
        return featInterface;
    }

    public Conf getConf() {
        return conf;
    }

    public Box getInbox() {
        return inbox;
    }

    public Box getOutbox() {
        return outbox;
    }

    public Box getBackup() {
        return backup;
    }

   /**
     * Should outbox be uploaded? + initialize upload.
     *
     * Called just before upload, use for initializing progress dialog as well.
     *
     * @todo move out the initialization part
     * @return
     */
    public synchronized boolean isOutboxSyncEnabled() {
        if (!conf.isOutboxSyncEnabled()) return false;

        final int bundlesToUpload = outbox.getBundles().size();

        userOutput.info("Upload bundles in outbox to the speed server; outbox size=%d", bundlesToUpload);

        gui.getProgressHandle().setDisplayName("Uploading bundles in outbox");
        gui.getProgressHandle().switchToDeterminate(bundlesToUpload);
        return true;
    }

    public synchronized void moveToInbox(SBundle bundle) {
        io.select();
        userOutput.info("Moving %s to inbox", bundle.getName());
        try {
            Util.moveBundle(bundle, inbox.getPathFobj());
            bundle.setReadOnly(false);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public synchronized void moveToOutbox(SBundle bundle) {
        io.select();
        userOutput.info("Moving %s to outbox", bundle.getName());
        try {
            Util.moveBundle(bundle, outbox.getPathFobj());     // todo read-only has to be passed to cmd
            //bundle.setReadOnly(true);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public synchronized void cleanup() {
        io.select();
        userOutput.info("Cleaning up");
        try {
            Util.cleanupFolder(inbox.getPathFobj(),  userOutput);
            Util.cleanupFolder(outbox.getPathFobj(), userOutput);
            // todo backup folder - recursively?, but not when called from sync()
            userOutput.info("Cleaned up");
        } catch (Throwable ex) {
            userOutput.severe(ex, "Error cleaning up!");
            //throw new RuntimeException(ex);
        }
    }


    public synchronized void sync() {
        userOutput.resetCounts();
        io.select();

        //todo switch off refresh?, then turn it on and refresh
        
        cleanup();
        
        syncImpl();

        if (userOutput.getErrorCount() > 0) {
            IoUtils.println(io, "THERE WERE ERRORS! See the log above.", NbOutLogger.finalErrColor);
            IoUtils.println(io, "Number of errors = " + userOutput.getErrorCount(), NbOutLogger.errColor);
        }
        else {
            IoUtils.println(io, "Sync was successfull", NbOutLogger.finalSuccesColor); // todo
        }

        if (userOutput.getWarningCount() > 0) {
            IoUtils.println(io, "Number of warnings = " + userOutput.getWarningCount(), NbOutLogger.warningColor);
        }
    }

    private void syncImpl() {
        progress = 0;

        final Date now = new Date();

        userOutput.info("%s - synchronizing with the speed server", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now) );  // todo url

        bakDirPrefix = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(now);
        datedBakFolder = null; // create only if needed

        try {
            SynchronizeFactory.getFactory().synchronize().synchronize(featInterface);
        }
        catch(Throwable ex) {
            userOutput.severe(ex, "Synchronization error");
        }
    }

    /**
     * Remove specified bundles from Inbox.
     * @param bundleNames 
     */
    public synchronized void cleanInbox(List<String> bundleNames) {
        userOutput.info("Removing submitted documents from inbox");
        final List<SBundle> toDelete = new ArrayList<SBundle>();

        for (SBundle bundle : inbox.getBundles()) {
            if (!bundleNames.contains(bundle.getName() )) toDelete.add(bundle);
        }

        if (!toDelete.isEmpty()) {
            final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                    "The following documents should not be in inbox (anymore). Delete?" + Cols.toStringNl(toDelete),
                    "Delete documents from inbox?",
                    NotifyDescriptor.YES_NO_OPTION);
            if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {;
                try {
                    for (SBundle bundle : toDelete) {
                        userOutput.info("Cleaning inbox: Removing %s from inbox", bundle);
                        Util.deleteBundle(bundle);
                    }
                }
                catch (IOException ex) {
                    throw new RuntimeException("Error during cleaning inbox", ex);
                }
            }
            else {
                userOutput.warning("Found files that should not be in inbox %s. User declined deletion.", toDelete);
            }
        }
    }


    public synchronized void setInboxSize(int aFilesToDownload) {
        userOutput.info("Downloading inbox. %d documents to download", aFilesToDownload);
        gui.getProgressHandle().setDisplayName("Downloading new files");
        gui.getProgressHandle().switchToDeterminate(aFilesToDownload);
    }

    public synchronized boolean isFileInInbox(String docName) {
        return inbox.contains(docName);
    }

    /**
     * Writes a bundle to inbox.
     * Atomicity: Everything written to a tmp directory, then moved. If anything,
     * fails, no cleanup necessary (assuming java removes tmp files).
     * 
     * @param bundle
     * @throws IOException
     */
    public synchronized void write2Inbox(ISynchronizedFile speedBundle) throws IOException {
        final String name = speedBundle.getFileName();
        userOutput.info("Downloaded %s bundle, writing to inbox", name );

        if (!ensureNameAvailable(name)) return;

        // tmp directory
        final FileObject tmpBundleFolder = Util.createTmpDir(conf.getSpeedTmpFObj(), name);
        
        try {
            // todo bundle/document metadata. Very temporary solution
            Properties properties = new Properties();

            // write cmd file
            for ( ISynchronizedFileContent cmd : speedBundle.getHead() ) {
                writeFileContent(cmd, tmpBundleFolder, name );
            }
            
            for (int i = 0; i < speedBundle.getFolders().length; i++) {
                ISynchronizedFileFolder speedDoc = speedBundle.getFolders()[i];
                String docName = name + "." + i;
                FileObject docFolder = tmpBundleFolder.createFolder(docName);    
            
                properties.put(docName + ".author",   speedDoc.getAuthor());
                properties.put(docName + ".workOn",   String.valueOf(speedDoc.isWorkOnFile()));
                properties.put(docName + ".readonly", String.valueOf(speedDoc.isReadOnly()));
                
                for ( ISynchronizedFileContent file : speedDoc.getContent() ) {
                    writeFileContent(file, docFolder, name);
                }
            }

            // save properties - todo better
            final FileObject propFObj = tmpBundleFolder.createData(name + ".meta.xml");
            Util.store(properties, propFObj);

            // move to inbox
            FileLock lock = tmpBundleFolder.lock();
            tmpBundleFolder.move(lock, inbox.getPathFobj(), name, null);
            lock.releaseLock();
        }
        catch(Throwable ex) {  
            userOutput.severe(ex, "Document was not downloaded correctly.");
            throw ex;
        }

        gui.getProgressHandle().progress(++progress);
    }

    private boolean ensureNameAvailable(String name) {
        final FileObject existingFObj = inbox.getPathFobj().getFileObject(name);
        
        if (existingFObj != null) {
            SBundle existingBundle = new SBundle(existingFObj);
            
            final NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                    "Inbox already contains a bundle with name " + name + ". Replace with a bundle from the server? " +
                    "(Old bundle will be backed-up.) Choosing no will not prevent a download in the future.)",
                    NotifyDescriptor.YES_NO_OPTION);
          
            if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.YES_OPTION) {
                userOutput.info("Existing bundle with the same name found, user rejected overwrite." );
                gui.getProgressHandle().progress(++progress);
                return false;
            }

            try {
                userOutput.info("Backing up existing bundle with the same name" );
                Util.saveBundle(existingBundle, true);  // saves if necessary (ask?)
                backup(existingBundle);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return true;
    }
    
    public synchronized ISynchronizedFile getNextOutboxFile() {
        if (outbox.getBundles().isEmpty()) return null;

        SBundle bundle = outbox.getBundles().iterator().next();

        userOutput.info("Uploading %s", bundle.getName() );

        return bundle.getSynchronizedFile();
    }

    /**
     * @param baseName
     */
    public synchronized void outboxFileProcessed(String baseName) {
        userOutput.info("%s uploaded, backing up", baseName );

        
        SBundle bundle = outbox.getBundle(baseName);

        try {
            backup(bundle);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        gui.getProgressHandle().progress(++progress);
    }

    /**
     * Note: If modified, the document must be saved before backing up.
     * @param bundle
     * @throws IOException
     */
    private void backup(SBundle bundle) throws IOException {
        if (datedBakFolder == null) {
            try {
                datedBakFolder = Util.createFolder(getConf().getBakFolderFObj(), bakDirPrefix);   // todo just prepare, might not send anything there
            }
            catch(IOException ex) {
                userOutput.severe(ex, "Error creating the backup directory");
                return;
            }
        }

        // copy todo to to the most-recent folder, overriding older files, then to the dated folder; 
        // todo cleanup the most recent directory
        Util.copyBundle(bundle, getConf().getBakFolderFObj(), true);
        Util.moveBundle(bundle, datedBakFolder);
    }


    private void writeFileContent(ISynchronizedFileContent fileContent, FileObject docFolder, String fileBase) {
        final File file = new File(FileUtil.toFile(docFolder), fileBase + "." + fileContent.getFileExtension());
        
        try {
            if (fileContent.getFileType() == ISynchronizedFileContent.txtFileType) {
                FileUtils.writeStringToFile(file, fileContent.getTxtBasedFileContent(), "utf8");
            }
            else {
                FileUtils.writeByteArrayToFile(file, fileContent.getBinFileContent());

            }
        } catch (IOException ex) {
            throw new RuntimeException(ex); // todo
        }
    }

}
