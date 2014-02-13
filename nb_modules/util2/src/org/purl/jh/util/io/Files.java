package org.purl.jh.util.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.str.Strings;

/**
 * Things that would be part of java.io.File if it were possible to add them.
 *
 * @author Jirka
 */
public class Files {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Files.class);

    /**
     * @param aDir
     * @param aChild
     * @return
     * @todo test
     */
    public static File combine(File aDir, File aChild) {
        log.fine("combine: %s;  %s;  abs=%s",aDir, aChild, aChild.isAbsolute());
        if (aChild.isAbsolute()) return aChild;

        return new File(aDir, aChild.getPath());
    }
    
// -----------------------------------------------------------------------------    
// FileName operations   
// -----------------------------------------------------------------------------    
    public final static Pattern cDotSplitter = Pattern.compile("\\.");
    
    /**
     * Removes an extension (if present) from a file name.
     * @param aFileName file to remove an extension from
     * @return file name without an extension "file.x.txt" -> "file.x", "file" -> "file".
     */
    public static String removeExtension(String aFileName) {
        int lastDotIdx = aFileName.lastIndexOf('.');
        
        if (lastDotIdx != -1) 
            return aFileName.substring(0,lastDotIdx);
        else
            return aFileName;
    }

    /**
     * Removes the specified extension (if present) from a file name.
     * @param aFileName file to remove an extension from
     * @param aExtension extension to remove
     * @return file name without the specified extension 
     *    (file.x.txt, txt) -> file.x, 
     *    (file.x.txt, x) -> file.x.txt, 
     *    (file.x.txt, x.txt) -> file, 
     */
    public static File removePossExtension(File aFileName, String aExtension) {
        String newFileNameStr = Strings.removePossTail(aFileName.getPath(), '.' + aExtension);
        return (aFileName.getPath() == newFileNameStr) ? aFileName : new File(newFileNameStr);
    }
 
    /**
     * Returns the list of extensions for the specified file. 
     * <table>
     * <tr><td>text.doc <td> [doc]
     * <tr><td>text.doc.gz <td> [gz,doc]
     * <tr><td>text <td> []
     * <tr><td>text..doc <td> [doc,]
     * </table>
     *
     * @param aFileName filename to analyze
     * @return list of extension (the list is reversed - the last extension is the first element of the list.
     *   If there are no extensions, an empty list is returned.
     *
     * If the file has no extensions, an empty list is returned.
     */
    public static List<String> getExtensions(File aFileName) {
        String[] tokens = cDotSplitter.split(aFileName.getName(), -1);
        if (tokens.length > 1) {
            List<String> extensions = Arrays.asList(tokens).subList(1, tokens.length);
            Collections.reverse(extensions);
            return extensions;
        }
        else
            return Collections.<String>emptyList();
    }
    
    /**
     * @return (the last) extension; or an empty string if there is none extension
     */
    public static String getExtension(String aFileName) {
        int lastDotIdx = aFileName.lastIndexOf('.');
        
        return (lastDotIdx == -1) ? "" : aFileName.substring(lastDotIdx+1 );
    }

    public static String addBeforeExtension(String aFile, String aInfix) {
        return removeExtension(aFile) + '.' + aInfix + '.' + getExtension(aFile);
    }

    public static File addExtension(File aFile, String aExtension) {
        return new File(aFile.getPath() + '.' + aExtension); 
    }

    public static File replaceExtension(File aFile, String aPossOrigExt, String aExtension) {
        return new File(Strings.removePossTail(aFile.getPath(), '.' + aPossOrigExt) + '.' + aExtension);
    }
    
    
    public static File replaceDir(File aFileNames, File aDir){
        return new File(aDir,  aFileNames.getName());
    }

// -----------------------------------------------------------------------------    
// FileNames (battery) operations   
// -----------------------------------------------------------------------------    

    public static XFile[] toArray(Collection<XFile> aFiles) {
        return aFiles.toArray(new XFile[aFiles.size()]);
    }

    public static List<XFile> toXFiles(Iterable<File> aFiles, XFile.Properties aProperties) {
        ArrayList<XFile> files = new ArrayList<XFile>();

        for (File file : aFiles) {
            files.add(new XFile(file, aProperties));
        }

        return files;
    }

    public static List<XFile> toXFiles(Iterable<File> aFiles, Compression aCompression, Encoding aEnc, Transliteration aTranslit, Format aFormat) {
        return toXFiles(aFiles, new XFile.Properties(aCompression, aEnc, aTranslit, aFormat) );
    }

    public static List<XFile> toXFiles(Collection<File> aFiles, Encoding aEnc, Format aFormat) {
        return toXFiles(aFiles, Compression.none, aEnc, Transliteration.cNone, aFormat);
    }

    public static List<XFile> toXFiles(Collection<File> aFiles, Encoding aEnc) throws IOException {
        return toXFiles(aFiles, Compression.none, aEnc, Transliteration.cNone, Format.cDef);
    }
    
    /**
     *
     * @param aFile xfile to get the non-file properties from (encoding, format, compression)
     */
    public static XFile[] filesToXFiles(File[] aFiles, XFile aFile){
        return xFiles(aFiles, aFile);
    }

    public static XFile[] xFiles(File[] aFiles, XFile aFile){
        XFile[] result = new XFile[aFiles.length];
        for (int i = 0; i < aFiles.length; i++)
            result[i] = XFile.create(aFiles[i], aFile);
        return result;
    }    
    /**
     * Changes the directories of a battery of files.
     * 
     * @param aFiles battery of files (they do not need to be in the same directory)
     * @param aDir the directory the resulting file names will have
     * @return battery of files in the specified directory
     * @todo move to corpus battery
     */
    public static XFile[] replaceDir(XFile[] aFiles, File aDir){
        XFile[] result = new XFile[aFiles.length];
        for (int i = 0; i < aFiles.length; i++)
            result[i] = aFiles[i].replaceDir(aDir);
        return result;
    }
   
    /**
     * Adds an extension to a battery of files.
     *
     * @param aFileNames battery of files (they do not need to be in the same directory)
     * @param aExtension the extension to be added
     * @return battery of files with the added extension
     * @todo move to (Corpus)Battery
     */
    public static XFile[] addExtension(XFile[] aFiles, String aExtension){
        XFile[] result = new XFile[aFiles.length];
        for (int i = 0; i < aFiles.length; i++)
            result[i] = aFiles[i].addExtension(aExtension);
            
        return result;
    }
}
