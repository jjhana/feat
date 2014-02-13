package org.purl.jh.feat.iaa;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author j
 */
public class IaaConfPanelData {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(IaaConfPanelData.class);
    
    private File set1 = new File("");
    private File set2 = new File("");
    private Pattern filter = null;
    private boolean lodzCompatibility = false;

    
    public boolean setSet1(String aInFile) {
        try {
            set1   = new File(aInFile).getCanonicalFile();
        } catch (IOException ex) {
            return false;
        }
        return updateInput();
    }

    public boolean setSet2(String aInFile) {
        try {
            set2   = new File(aInFile).getCanonicalFile();
        } catch (IOException ex) {
            return false;
        }
        return updateInput();
    }
    
    public boolean setFilter(String aFilter) {
        try {
            filter   = Pattern.compile(aFilter);
        } catch (Throwable ex) {
            return false;
        }
        return updateInput();
    }

    public void setLodzCompatibility(boolean lodzCompatibility) {
        this.lodzCompatibility = lodzCompatibility;
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

    public File getSet1() {
        return set1;
    }

    public File getSet2() {
        return set2;
    }

    public Pattern getFilter() {
        return filter;
    }

    public boolean isLodzCompatibility() {
        return lodzCompatibility;
    }

    
    
    
// -----------------------------------------------------------------------------
// Calculations
// -----------------------------------------------------------------------------
    
    
    /** Too many specific things */
    private boolean updateInput() {
//        outFile = calculateOutPrefix();
//
//        if (inFileOk) {
//            log.info("zzz:" + outFile);
//            File file = new File(outFile+".capek");
//            outFile = file;
//            log.info("outFile:" + file);
//        }
        return true;
    }
    


}
