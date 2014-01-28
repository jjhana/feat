package org.purl.jh.speedsupport.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.purl.jh.speedsupport.Util;
import org.purl.jh.util.CountingLogger;

/**
 * A folder containing bundles (in the future probably also documents directly). 
 * Realized as a folder on the disk, presented to the user as one or more BoxNodes.
 * 
 * Currently it includes speed support (synchronization + Inbox/Outbox/Backup), 
 * in the future it should be separated into 
 * a subclass (another subclass would support normal disk based bundles/documents).
 * 
 * @todo listen to file changes
 */
public class Box {
    private CountingLogger userOutput = null; // todo
            
    protected final FileObject pathFobj;
    protected final File path;
    protected final String defDisplayName;
    protected boolean readOnly;
    
    protected final List<SBundle> bundles = new ArrayList<>();
    
    /**
     * listeners for changes in hierarchy.
     */
    private transient EventListenerList listeners = new EventListenerList();

    public Box(FileObject path, final String defDisplayName, boolean aReadOnly) {
        this.pathFobj = path;
        this.path = FileUtil.toFile(path);
        this.defDisplayName = defDisplayName;
        this.readOnly = aReadOnly;
        
            // todo ? lazily create bundles?  isn't there some support for this in netbeans?
        this.pathFobj.addFileChangeListener(fileListener);
    }

    public FileObject getPathFobj() {
        return pathFobj;
    }

    public File getPath() {
        return path;
    }
    
    public String getDefDisplayName() {
        return defDisplayName;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
 
    
    /**
     * Includes corrupted bundles
     * @return 
     */
    public List<SBundle> getBundles() {
        return bundles;
    }

//    public Bundle getOneBundle() {
//        final Enumeration<? extends FileObject> en = path.getFolders(false);
//
//        while(en.hasMoreElements()) {
//            Bundle bundle = new Bundle(en.nextElement());
//            if (!bundle.isCorrupted()) return bundle;
//        }
//        return null;
//    }

    public boolean contains(String bundleName) {
        for (FileObject fobj : pathFobj.getChildren()) {
            if (fobj.getName().equals(bundleName)) {
                return true;
            }
        }
        return false;
    }
    
    public void refresh() {
        final List<SBundle> tmp = new ArrayList<>();

        // todo refresh those that exist, drop/add
        
        pathFobj.refresh(); 
        final Enumeration<? extends FileObject> en = pathFobj.getFolders(false);
        while(en.hasMoreElements()) {                           
            SBundle bundle = new SBundle(en.nextElement());
            tmp.add( bundle ); 
        }

        // check if anything changed?
        bundles.clear();
        bundles.addAll(tmp);
        
        fireBoxChanged();
    }

    public void cleanup() {
        userOutput.info("Cleaning up %s", defDisplayName);
        // todo wait with updates till this is all done
        
        refresh();
//        for (Bundle bundle : getBundles()) {            // todo cycle through this or lsit of bundles?
//            if (bundle.isCorrupted()) cleanupBundle(bundle);
//        }
        // todo what if there is only a marker (marker created but no folder created), maybe go by real folders/files?
        for (FileObject file : pathFobj.getChildren()) {            // todo cycle through this or lsit of bundles?
            if (!file.isFolder() && file.hasExt(Util.cMarkerExt)) {
                cleanupBundle(file);
            }
        }

        userOutput.info("Cleaned up");

        fireBoxChanged();
    }
    
    private void cleanupBundle(FileObject marker) {
        try {
            userOutput.info("Removing corrupted %s!", marker.getName());
            throw new UnsupportedOperationException();
        } catch (Throwable ex) {
            userOutput.severe(ex, "Error removing corrupted %s!", marker.getName());
        }
    }
    
    
    protected void fireBoxChanged() {
        final Object[] tmp = listeners.getListenerList();
        for (int i = tmp.length - 2; i >= 0; i -= 2) {
            if (tmp[i] == BoxListener.class) {
                // if (fooEvent == null) event = new BoxEvent(this);
                ((BoxListener) tmp[i + 1]).changed();
            }
        }
    }

    /** 
     * For example nodes listen to changes in the box.
     */ 
    public void addBoxListener(BoxListener l) {
        listeners.add(BoxListener.class, l);
    }

    public void removeBoxListener(BoxListener l) {
        listeners.remove(BoxListener.class, l);
    }

    public SBundle getBundle(String baseName) {
        for (SBundle bundle : bundles) {
            if (baseName.equals(bundle.getName())) return bundle;
        }
        return null;
    }

    /**
     * todo move to the factory?
     * todo finegrained events
     */
    public interface BoxListener extends EventListener {
        public void changed();
    }

    public static class BoxListenerAdapter implements BoxListener {
        public void changed() {
        }
    }
    
    private FileChangeListener fileListener = new FileChangeAdapter() {
        @Override
        public void fileFolderCreated(FileEvent fe) {
            refresh();
        }

        @Override
        public void fileDeleted(FileEvent fe) {
            refresh();
        }
        
    };
    
}
