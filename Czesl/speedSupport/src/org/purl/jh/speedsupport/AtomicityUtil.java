package org.purl.jh.speedsupport;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.purl.jh.util.str.Strings;

/**
 * Class supporting atomic upload/download of files belonging to the same document.
 *
 * @author j
 */
public class AtomicityUtil {

    public static final String cFlagTail = ".tmp~";


    public static void addUnfinishedDocFlag(String aDocBase) throws IOException {
        FileUtils.touch(flag(aDocBase));
    }

    public static void delUnfinishedDocFlag(String aDocBase) {
        FileUtils.deleteQuietly(flag(aDocBase));
    }

    public static boolean containsUnfinishedDocs(File aDir) {
        return aDir.list(cFlagFilter).length > 0;
    }

    /**
     * Deletes all unfinished documents from
     * @param aDir
     */
    public static void cleanup(File aDir) {
        final String[] flags = aDir.list(cFlagFilter);

        for (String flag : flags) {
            final String docBaseDot = docBase(flag) + ".";

            final String[] docFiles = aDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(docBaseDot) && !isFlagFile(name);
                }
            });

            if (!deleteAll(docFiles)) throw new RuntimeException("Error perfoming cleanup on " + docBaseDot + "*. Clean the directory manually.");
            new File(flag).delete();
        }
    }

    private static final FilenameFilter cFlagFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return isFlagFile(name);
        }
    };

    private static boolean isFlagFile(String aFile) {
        return aFile.endsWith(cFlagTail);
    }

    private static File flag(String aDocBase) {
        return new File(aDocBase + cFlagTail);
    }

    private static String docBase(String aFlagFile) {
        if ( !isFlagFile(aFlagFile) ) throw new IllegalArgumentException(aFlagFile + " is not a marker file");
        return Strings.removeTail(aFlagFile, cFlagTail);
    }

    /**
     * Deletes all files in a list.
     * @param aFiles
     * @return true if all files were deleted, false otherwise
     */
    private static boolean deleteAll(String[] aFiles) {
        for (String docFile : aFiles) {
            if (!new File(docFile).delete()) return false;
        }
        return true;
    }

}
