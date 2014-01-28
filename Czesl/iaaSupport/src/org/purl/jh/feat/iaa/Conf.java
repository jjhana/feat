/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.iaa;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    
/**
 *
 * @author j
 */
public class Conf {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(IaaConfPanelData.class);
    
    private File set1 = new File("");
    private File set2 = new File("");
    private Pattern filter = null;
    /** Backward compatibility with the Lodz paper - tags are projected on all wforms. Now we project on the first one only. */
    public boolean lodzCompatibility = false;

    /** Order in which the tags will be reported, null means default */
    private List<String> out_tagOrder = null;
    
    private File out_exampleFile = new File("C:\\examples.txt");        // todo hack, provide gui
    
    public Conf() {
    }
    
    public Conf(Conf aConf) {
        this.set1 = aConf.set1;
        this.set2 = aConf.set2;
        this.filter = aConf.filter;
        this.lodzCompatibility = aConf.lodzCompatibility;
        this.out_tagOrder = aConf.out_tagOrder;
        this.out_exampleFile = aConf.out_exampleFile;
    }
    
    public void setSet1(File set1) {
        this.set1 = set1;
    }

    public void setSet2(File set2) {
        this.set2 = set2;
    }

    public void setFilter(Pattern filter) {
        this.filter = filter;
    }

    
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

    public void setOut_TagOrder(List<String> out_tagOrder) {
        this.out_tagOrder = out_tagOrder;
    }

    public void setOut_exampleFile(File out_exampleFile) {
        this.out_exampleFile = out_exampleFile;
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

    public List<String> getOut_tagOrder() {
        return out_tagOrder;
    }

    public File getOut_exampleFile() {
        return out_exampleFile;
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
