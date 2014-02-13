package org.purl.jh.speedsupport;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.purl.jh.speedsupport.data.SBundle;
import org.purl.jh.speedsupport.data.Document;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.IO;

/**
 *
 * @author jirka
 */
public final class Util {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Util.class);
    
    public final static String cMarkerExt = "tmp~";
    /** the associated item can be safely removed when found */
    public final static String cDelMarkerExt = "del~";
    
        
    /** 
     * Checks if marker exists, if yes an error occurred, remove everything
     * (the original is still untouched - if removed, it is removed only after the marker is deleted)
     */ 
    public static void cleanupDoc(FileObject aMarker, CountingLogger userOutput) throws IOException {
        if (!aMarker.isValid()) return;
        
        if (userOutput != null) userOutput.info("Found partial document %s, deleting.", aMarker.getName());
        
        final FileObject boxFolder = aMarker.getParent();
        
        
        final FileObject cmdFile = boxFolder.getFileObject(aMarker.getName() + ".cmd");

        if (cmdFile != null) {
            // close associated dobj
            final DataObject dobj = DataObject.find(cmdFile);
            try {
                dobj.setValid(false);
            } catch (PropertyVetoException ex) {
                throw new RuntimeException("Cmd file " + cmdFile.getName() + " cannot be closed");
            }

            cmdFile.delete();
        }
        
        FileObject docFolder = boxFolder.getFileObject(aMarker.getName());
        if (docFolder != null) docFolder.delete();
            
        

        // backward compatibility - delete particular files without a folder
        for (String tail : Arrays.asList(".html", ".w.xml", ".a.xml", ".b.xml")) {
            FileObject file = boxFolder.getFileObject(aMarker.getName() + tail);
            if (file != null) file.delete();
        }

        removeMarker(aMarker);
        
        if (userOutput != null) userOutput.info("Partial document %s deleted.", aMarker.getName());
    }

    public static void cleanupFolder(FileObject aFolder, CountingLogger userOutput) throws IOException {
        aFolder.refresh();
        for (FileObject file : aFolder.getChildren()) {
            if (!file.isFolder() && file.hasExt(cMarkerExt)) {
                cleanupDoc(file, userOutput);
            }
        }
    }

    public static FileObject addMarker(SBundle bundle, FileObject aDest) throws IOException {
        return addMarker(bundle.getName(), aDest);
    }

    public static FileObject addMarker(String bundleName, FileObject aDest) throws IOException {
        return aDest.createData(bundleName, cMarkerExt);
    }
    
    public static void removeMarker(FileObject aMarker) throws IOException  {
        aMarker.delete();
    }

    public static boolean isMarked(SBundle bundle) {
        return new File(FileUtil.toFile(bundle.getFolder()).getParentFile(), bundle.getName() + "." + cMarkerExt).exists();
    }

    /**
     * save modified documents (todo: should ask)
     * todo first save marked copy, then delete original, then remove mark
     */ 
    public static void saveBundle(SBundle bundle, boolean close) throws IOException {
        log.info("Saving bundle %s", bundle.toString());

        saveFile(bundle.getCmdFile(), close);
        
        for (Document doc : bundle.getDocuments()) {
            saveDocument(doc, close);
        }
        // todo save cmd, properties
        log.info("Saved bundle %s", bundle.toString());
    }

    public static void saveDocument(Document doc, boolean close) throws IOException {
        log.info("Saving document %s", doc.toString());
        final List<FileObject> fobjs = doc.documentFiles();

        // save modified documents (todo: should ask)
        for (FileObject fobj : fobjs) {
            saveFile(fobj, close);
        }
        log.info("Saved document %s", doc.toString());
    }
    
//    public static void moveBundle(Bundle bundle, FileObject aDest) throws IOException {
//        moveBundle(new Bundle(aCmdDObj.getPrimaryFile()), aDest);
//    }

    /**
     * move to backup instead
     */ 
    public static void deleteBundle(SBundle bundle) throws IOException {
        final FileObject folder = bundle.getFolder();
        final FileLock lock = folder.lock();
        bundle.getFolder().rename(lock, folder.getNameExt(), cDelMarkerExt);
        bundle.getFolder().delete(lock);
        lock.releaseLock();
    }


//    public static void copyDocument(Document aDoc, FileObject aDest) throws IOException {
//        copyDocument(aDoc, aDest, false);
//    }

    /**
     * Note the dataobjects of the document are not saved before copying.
     * @param aDoc
     * @param dest
     * @param overwrite
     * @throws IOException
     */
    public static void copyBundle(SBundle bundle, FileObject dest, boolean overwrite) throws IOException {
        saveBundle(bundle, true); // todo move out

        final FileObject folder = bundle.getFolder();
        
        final FileObject potentialExisting = dest.getFileObject( folder.getNameExt() );
        
        if (potentialExisting != null) {
            if (overwrite) {
                potentialExisting.delete();
            }
            else {
                throw new IOException(String.format("A bundle %s already exists", FileUtil.toFile(potentialExisting)));
            }
        }

        // todo copy to a tmp dir then move
        final FileObject marker = addMarker(bundle, dest);
        try {
            FileLock lock = bundle.getFolder().lock();
            bundle.getFolder().copy(dest, folder.getName(), folder.getExt());
            lock.releaseLock();

            removeMarker(marker);
        }
        finally {
            cleanupDoc(marker, null);
        }
    }

    
    public static void moveBundle(SBundle bundle, FileObject aDest) throws IOException {
        log.info("Moving bundle %s", bundle.toString());
        saveBundle(bundle, true); // todo move out
        
        FileObject folder = bundle.getFolder();
        FileLock lock = bundle.getFolder().lock();
  
//        if (true) return;
        bundle.getFolder().move(lock, aDest, folder.getName(), folder.getExt());
        lock.releaseLock();
        log.info("Moved bundle %s", bundle.toString());
    }

    public static boolean containsParentFile(Set<String> aAll, String aName, String aCurEnding, String aParentEnding) {
        if (!aName.endsWith(aCurEnding)) return false;
        return aName.endsWith(aCurEnding) && aAll.contains(replaceTail(aName, aCurEnding, aParentEnding));
    }

    public static String replaceTail(String aName, String aCurEnding, String aNewEnding) {
        return aName.substring(0, aName.length()-aCurEnding.length()) + aNewEnding;
    }

    public static Set<String> condense(Set<String> aFullNames, List<String> aEndings) {
        final Set<String> condensed = new HashSet<String>();
        for (String fullName : aFullNames) {
            condensed.add(condense(fullName, aEndings));
        }
        return condensed;
    }

    public static String condense(String aFullName, List<String> aEndings) {
        for (String ending : aEndings) {
            if (aFullName.endsWith(ending)) return replaceTail(aFullName, ending, "");
        }
        return aFullName;

    }

    static FileObject createFolder(FileObject aFolder, String prefix) throws IOException {
        final File folder = FileUtil.toFile(aFolder);

        for (int i = 0;;i++) {
            String name = prefix + ( i == 0 ? "" : "_" + i);
            File file = new File(folder, name);
            if (!file.exists()) {
                file.mkdirs();
                return FileUtil.toFileObject(file);
            }
        }
    }
    
    public static Comparator<FileObject> cFileObjNameComparator = new Comparator<FileObject>() {
        @Override
        public int compare(FileObject o1, FileObject o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public static void store(Properties properties, FileObject propFObj) throws FileAlreadyLockedException, IOException {
        OutputStream out = null; 
        try { 
            out = propFObj.getOutputStream();
            properties.storeToXML(out, null);
        }
        finally {
            IO.close(out);
        }
    }

    public static Properties loadProperties(FileObject propFObj) throws FileAlreadyLockedException, IOException {
        Properties properties = new Properties();

        InputStream in = null; 
        try { 
            in = propFObj.getInputStream();
            properties.loadFromXML(in);
        }
        finally {
            IO.close(in);
        }

        return properties;
    }

    public static FileObject createTmpDir(FileObject parent, String prefix) throws IOException {
       Path parentp = FileUtil.toFile(parent).toPath();
       return FileUtil.toFileObject(Files.createTempDirectory(parentp, prefix).toFile());
    }

    private static void saveFile(FileObject fobj, boolean close) throws RuntimeException, IOException, DataObjectNotFoundException {
        DataObject dobj = DataObject.find(fobj);   
        Err.fAssert(dobj != null, "File %s has an unknown structure, not saved.", FileUtil.toFile(fobj));       // todo warning
        
        SaveCookie saveCookie = dobj.getLookup().lookup(SaveCookie.class);
        if (saveCookie != null) saveCookie.save();

        if (close) {
            try {
                dobj.setValid(false);
            } catch (PropertyVetoException ex) {
                throw new RuntimeException("File " + fobj.getName() + " cannot be closed");
            }
        }
    }
    
    
}
