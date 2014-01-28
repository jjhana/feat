package org.purl.jh.speedsupport;

import cz.jager.uk.mff.ufal.Feat2Speedver2.IFeatInterface;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import org.openide.awt.StatusDisplayer;
import org.purl.jh.util.err.Throwables;

/**
 * @todo do not put everything in main?
 * @author jirka
 */
public class FeatInterface implements IFeatInterface {
    private final Main main;
    private final Conf conf;

    public FeatInterface(Conf aConf, Main aMain) {
        this.main = aMain;
        this.conf = aConf;
    }

    @Override
    public String getSpeedUrl() {
        return conf.getSpeedUrl();
    }

    @Override
    public String getUserName() {
        return conf.getUserName();
    }

    @Override
    public String getUserPassword() {
        return conf.getUserPassword();
    }

    @Override
    public boolean isInboxCheckEnabled() {
        return conf.isInboxCheckEnabled();
    }


    /**
     * If true synchronize will re-download files that have been downloaded before
     * (possibly to a different computer). Default is false.
     */
    @Override
    public boolean isReadAnySpeedFileEnabled() {
        return conf.isReadAnySpeedFileEnabled();
    }

    @Override
    public boolean isOutboxSyncEnabled() {
        return main.isOutboxSyncEnabled();
    }

    //@Override
    public void showErrorMessage(Throwable aEx) {
        if (Throwables.getCause(aEx, java.net.UnknownHostException.class) != null) {
            main.userOutput.severe("Specified speed server is not known. Check the url and your internet connection.");  // todo report url
        }
        else if (Throwables.getCause(aEx, MalformedURLException.class) != null) {
            main.userOutput.severe("The speed url todo has a wrong format.");
        }
        else {
            // todo hack, but how else to distinguish 'expected' user errrors from the rest, when the SynchronizeFactory etc does not provide any support for it?
            String msg = aEx.getMessage();
            if (msg != null) {
                if (msg.contains("Unknow user or invalid password!") || msg.contains("Unknown user or invalid password!")) {
                    main.userOutput.severe("Unknown user or invalid password!");
                }
                else {
                    main.userOutput.severe(aEx, "Other Error");
                }
            }
            else {
                main.userOutput.severe(aEx, "Other Error");
            }
        }
        StatusDisplayer.getDefault().setStatusText("Speed Sync Error: " + aEx);
    }
    
    @Override
    public void showErrorMessage(String aMsg) {
        main.userOutput.severe("Speed Sync Error: " + aMsg);
        if (aMsg == null) {
            main.userOutput.severe(new Throwable("Dummy Throwable"), "Null Speed sync error message!");
        }
        StatusDisplayer.getDefault().setStatusText("Speed Sync Error: " + aMsg);
    }

    @Override
    public void addWarning(String aMsg) {
        main.userOutput.warning(aMsg);
        StatusDisplayer.getDefault().setStatusText("Speed Sync Warning: " + aMsg);
    }


    /**
     * Only files in this list are allowed to be in the inbox.
     * Typically, if a file is in the inbox but is not in this list, it was uploaded
     * from another copy of the inbox on a different computer.
     *
     * Note that not all files in this list must be in the inbox, some are new and
     * will be downloaded during this synchronization and some are already in the
     * inbox (they will be uploaded during this synchronization, but marked as
     * 'duplicates')
     */
    @Override
    public void checkInbox(String docNames[]) {
        main.cleanInbox(Arrays.asList(docNames));
    }

    /**
     * Called to allow realistinc progress dialog.
     * @param speadInboxSize
     */
    @Override
    public void setInboxSize(int speadInboxSize) {
        main.setInboxSize(speadInboxSize);
    }

    // todo check
    @Override
    public boolean isFileInInbox(String docName) {
        return main.isFileInInbox(docName);
    }

    @Override
    public void write2Inbox(ISynchronizedFile bundle) throws IOException {
        main.write2Inbox(bundle);
    }

    @Override
    public ISynchronizedFile getNextOutboxFile() {
        return main.getNextOutboxFile();
    }

    @Override
    public void outboxFileProcessed(String fileName) {
        main.outboxFileProcessed(fileName);
    }

    @Override
    public int readMaxInboxFileCount() {
        return conf.readMaxInboxFileCount();
    }

}
