package org.purl.jh.feat.importx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.util.Exceptions;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.io.Files;

/**
 *
 * @author jirka
 */
public class ImportPanelData {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ImportPanelData.class);
    
    private File inFile = new File("");
    private File userOutPrefix = new File("");

    /** Computed from inFile and optional userOutPrefix */
    private File outPrefix;

    /** Is the input file correctly specified  and usable? */
    private boolean inFileOk = false;

    /** Is the prefix of output files correctly specified and usable? */
    private boolean outPrefixOk = false;

    private boolean copyInFile = false;
    private final List<File> outFiles = new ArrayList<File>();

    public void setInFile(String aInFile) {
        try {
            inFile   = new File(aInFile).getCanonicalFile();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        inFileOk = inFile.isFile();
        updateInput();
    }

    public void setUserOutPrefix(String aUserOutPrefix) {
        // todo maybe thow an exception when spaces are in wrong palaces
        //        try {
            userOutPrefix = new File(removeEdgeSpaces(aUserOutPrefix));
//        } catch (IOException ex) {
//            //Exceptions.printStackTrace(ex);
//        }
        log.info("setUserOutPrefix: %s -> %s", aUserOutPrefix, userOutPrefix);

        updateInput();
    }
    
    
    /**
     * Removes leading/trailing spaces from files/direcotries.
     * @param aPath
     * @return 
     */
    public static String removeEdgeSpaces(String aPath) {
        return Pattern.compile("\\s*[\\/\\\\]\\s*").matcher(aPath).replaceAll(Matcher.quoteReplacement(File.separator));
    }


// -----------------------------------------------------------------------------
// Getters
// -----------------------------------------------------------------------------

    public File getInFile() {
        return inFile;
    }
    public File getInPrefix() {
        return new File(getInDir(), getInNameOnly());
    }

    public File getInDir() {
        return inFile.getParentFile();
    }

    /** 
     * Returns the input file's name without the directory and extension.
     * Override if the input file can contain a complex extension (e.g. "a.xml") 
     */
    public String getInNameOnly() {
        return Files.removeExtension(inFile.getName());
    }

    public boolean isInFileOk() {
        return inFileOk;
    }

    public boolean getCopyInFile() {
        return copyInFile;
    }

    public File getOutPrefix() {
        return outPrefix;
    }
    
    
    
// -----------------------------------------------------------------------------
// Calculations
// -----------------------------------------------------------------------------
    
    
    /** Too many specific things */
    private void updateInput() {
        outPrefix = calculateOutPrefix();

        copyInFile = false;
        //outPrefixOk = true; // be optimistic 

        final List<String> fileNameTails = XCols.newArrayList(".w.xml", ".a.xml", ".b.xml"); 
        // copy input file(s) if the output prefix is different
        if ( !outPrefix.equals(getInPrefix())) {
            log.info("Diff: %s != %s", outPrefix, getInPrefix() );
            copyInFile = true;
            fileNameTails.add(".html");     // todo make this generic 

            File jpgFile = new File(getInPrefix().getPath() + ".jpg");
            if (jpgFile.isFile()) fileNameTails.add(".jpg");
        }
        else if (inFile.getPath().endsWith(".htm")) {
            // will need to copy htm to html
            fileNameTails.add(".html");
        }
        // todo copy jpeg to jpg

        outFiles.clear();
        if (inFileOk) {
            log.info("zzz:" + outPrefix);
            for (String tail : fileNameTails) {
                File file = new File(outPrefix+tail);
                outFiles.add(file);
                log.info("outFile:" + file);
            }
        }
    }
    
    /** Combines inFile and userOutPrefix to calculate outPrefix */
    private File calculateOutPrefix() {
        log.info("calculateOutPrefix: userOutPrefix=%s", userOutPrefix);
        log.info("calculateOutPrefix: userOutPrefix.getPath()=%s", userOutPrefix.getPath());

//        if (userOutPrefix.getPath().equals("") ) {
//            return inFileOk ? new File( getInDir(), getInNameOnly()) : new File("");
//        }

        // todo dir/c + a     -> dir/ac
        // todo dir/c + a/b/  -> dir/a/b/c
        // todo dir/c + a/b   -> dir/a/bc
        File userPrefixDir;
        String namePrefix;
        
        // does not work as the slashes are dropped
        if (userOutPrefix.getPath().endsWith("\\") || userOutPrefix.getPath().endsWith("/")) {
            userPrefixDir = userOutPrefix;
            namePrefix = "";
        }
        else {
            userPrefixDir = userOutPrefix.getParentFile();
            namePrefix = userOutPrefix.getName();
        }

        String outName = ( namePrefix.isEmpty() ? getInNameOnly() : namePrefix ).trim();

        log.info("getInDir()=" + getInDir());
        log.info("userPrefixDir=" + userPrefixDir);
        log.info("namePrefix=" + namePrefix);
        log.info("outName=" + outName);

        return Files.combine(getInDir(), new File(userPrefixDir, outName));
    }

    public List<File> getOutFiles() {
        return outFiles;
    }

    

    
    
    

    @Override
    public String toString() {
        return "ImportData{" + "inFile=" + inFile + ", userOutPrefix=" + userOutPrefix + ", outPrefix=" + outPrefix + ", inFileOk=" + inFileOk + ", outPrefixOk=" + outPrefixOk + ", copyInFile=" + copyInFile + ", outFiles=" + outFiles + '}';
    }


}
