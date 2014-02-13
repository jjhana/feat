package org.purl.jh.speedsupport;

import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFile;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileContent;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileFolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openide.util.Exceptions;

/**
 * A bundle of documents. The bunch is in a folder and so is each document within it.
 * @author jirka
 */
public class SynchronizedFile implements ISynchronizedFile {
    final File folder;
    final String baseName;

    public SynchronizedFile(File folder, String baseName) {
        this.folder = folder;
        this.baseName = baseName;
    }


    @Override
    public String getFileName() {
        return baseName;
    }

    @Override
    public ISynchronizedFileContent[] getHead() {
        // return the cmd.xml file
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ISynchronizedFileFolder[] getFolders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
//    @Override
//    public String getCommandFile() {
//        return getFileString("cmd");
//    }
//
//    @Override
//    public String getHtmlFile() {
//        return getFileString("html");
//    }
//
//    @Override
//    public String getXmlWFile() {
//        return getFileString("w.xml");
//    }
//
//    @Override
//    public String getXmlAFile() {
//        return getFileString("a.xml");
//    }
//
//    @Override
//    public String getXmlBFile() {
//        return getFileString("b.xml");
//    }

    protected String getFileString(String aExt) {
        File file = new File(folder, baseName + "." + aExt);
        if (!file.exists()) return null;

        try {
            return FileUtils.readFileToString(file, "utf8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
