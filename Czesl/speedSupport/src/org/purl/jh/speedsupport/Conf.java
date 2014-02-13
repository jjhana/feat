package org.purl.jh.speedsupport;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;

/**
 * Holds and loads (and in the future saves) any configuration data.
 *
 * @author jirka
 */
public class Conf {
    private File speedRoot;

    private FileObject speedInboxFObj;
    private FileObject speedOutboxFObj;
    private FileObject speedBakFObj;
    private FileObject speedTmpFObj;
    
    public Conf() {
        String speedRootStr = getPrefs().get("speedRoot", null);
        if (speedRootStr == null) {
            String userDir = System.getProperty("netbeans.user");
            setSpeedRoot(new File(userDir, "speed"));
        }
        else {
            setSpeedRoot(new File(speedRootStr));
        }
    }

    public void setSpeedRoot(File aSpeedRoot) {
        try {
            speedRoot = aSpeedRoot;
            FileObject speedRootFObj = FileUtil.createFolder(speedRoot);
            speedInboxFObj  = FileUtil.createFolder(speedRootFObj, "inbox");
            speedOutboxFObj = FileUtil.createFolder(speedRootFObj, "outbox");
            speedBakFObj    = FileUtil.createFolder(speedRootFObj, "bak");
            speedTmpFObj    = FileUtil.createFolder(speedRootFObj, "tmp");

        } catch (IOException ex) {
            throw new RuntimeException("Cannot initialize speed folder in " + speedRoot, ex);
        }

    }


    public File getSpeedInbox() {
        return FileUtil.toFile(speedInboxFObj);
    }

    public FileObject getSpeedInboxFObj() {
        return speedInboxFObj;
    }

    public File getSpeedOutboxFile() {
        return FileUtil.toFile(speedOutboxFObj);
    }

    public FileObject getSpeedOutboxFObj() {
        return speedOutboxFObj;
    }

    public FileObject getBakFolderFObj() {
        return speedBakFObj;
    }

    public File getBakFolderFile() {
        return FileUtil.toFile(speedBakFObj);
    }

    public FileObject getSpeedTmpFObj() {
        return speedTmpFObj;
    }
    
    public File getSpeedRootFile() {
        return speedRoot;
    }

    
    
    // property names
    public static final String cSpeedUrl = "speedUrl";
    public static final String cUserName = "user";
    public static final String cUserPassword  = "pswd";
    public static final String cInboxCheckEnabled = "inboxCheckEnabled";
    public static final String cInboxSyncEnabled = "inboxSyncEnabled";
    public static final String cOutboxSyncEnabled = "outboxSyncEnabled";
    public static final String cReadAnySpeedFileEnabled = "readAnySpeedFileEnabled";

    public String getSpeedUrl() {
        return getPrefs().get(cSpeedUrl, " http://speed.aspone.cz/Feat2Speed.asmx");
    }

    public String getUserName() {
        return getPrefs().get(cUserName, "anonymous");
    }

    public String getUserPassword() {
        return getPrefs().get(cUserPassword, "");
    }

    public boolean isInboxCheckEnabled() {
        return getPrefs().getBoolean(cInboxCheckEnabled, true);
    }

    public int readMaxInboxFileCount() {
        return isInboxSyncEnabled() ? Integer.MAX_VALUE : 0;
    }

    public boolean isInboxSyncEnabled() {
        return getPrefs().getBoolean(cInboxSyncEnabled, true);
    }

    /**
     * Should outbox be uploaded?
     *
     * Called just before upload, use for initializing progress dialog as well.
     * @return
     */
    public boolean isOutboxSyncEnabled() {
        return getPrefs().getBoolean(cOutboxSyncEnabled, true);
    }

    public boolean isReadAnySpeedFileEnabled() {
        return getPrefs().getBoolean(cReadAnySpeedFileEnabled, false);
    }

    private Preferences getPrefs() {
        return NbPreferences.forModule(getClass());
    }



}
