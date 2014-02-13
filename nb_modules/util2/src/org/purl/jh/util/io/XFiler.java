package org.purl.jh.util.io;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.err.Err;

/**
 * Support for creating an XFile object - format, encoding, etc. registries.
 * This is plugged as an static instance into XFile.
 *
 * @todo make interface and really pluggable
 * @author Jirka
 */
public class XFiler {
    private final static Pattern cAtSplitter = Pattern.compile("@");

    private Encoding mDefEnc;
    private Format mDefFormat;

    private boolean mFilesByExt;
    
    private FormatRegistry   mFormats = new FormatRegistry();
    private TranslitRegistry mTranslits = new TranslitRegistry();

// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------
    
    /** Creates a new instance of XFiler */
    public XFiler() {
    }

    
    public void setFilesByExt(boolean aFilesByExt) {
        mFilesByExt = aFilesByExt;
    }
    
// -----------------------------------------------------------------------------
// Registries
// -----------------------------------------------------------------------------    
    
    public FormatRegistry formats() {
        return mFormats;
    }

    public void setFormats(FormatRegistry aFormats) {
        mFormats = aFormats;
    }

    public TranslitRegistry translits() {
        return mTranslits;
    }

    public void setTranslit(TranslitRegistry aTranslits) {
        mTranslits = aTranslits;
    }
    
// -----------------------------------------------------------------------------    
    
    public Encoding defEnc() {
        return mDefEnc;
    }

    public void setDefEnc(Encoding aEnc) {
        mDefEnc = aEnc;
    }

    public Format defFormat() {
        return mDefFormat;
    }

    public void defFormat(Format aFormat) {
        mDefFormat = aFormat;
    }
    
// -----------------------------------------------------------------------------    
// String -> XFile
// -----------------------------------------------------------------------------    

    
    public File plainFileFromString(String aString, File aPath) {
        File file = new File(aString);
        if (!file.isAbsolute()) {
            file = new File(aPath, aString);
        }

        try {
            return file.getCanonicalFile();
        }
        catch(IOException e) {
            throw new RuntimeException("Cannot get the canonical version of " + file.toString());
        }
    }

    /**
     * Default format is Format.def.
     */
    public XFile fileFromString(String aString, File aPath)  {
        return fileFromString(aString, aPath, Compression.none, mDefEnc, Transliteration.cNone, Format.cDef);
    }
    
    /**
     * Default format is Format.def.
     */
    public XFile fileFromString(String aString, File aPath, Encoding aEncDef)  {
        return fileFromString(aString, aPath, Compression.none, aEncDef, Transliteration.cNone, Format.cDef);
    }

    /**
     */
    public XFile fileFromString(String aString, File aPath, Encoding aEncDef, Format aFormatDef)  {
        return fileFromString(aString, aPath, Compression.none, aEncDef, Transliteration.cNone, aFormatDef);
    }

    /**
     * Compression, encoding, transliteration and format is set in the following way (in the following order of precendence):
     * <ul>
     * <li>to id, if the file name is followed @id 
     *    (if the same id is registered several times, then compression has precedence over transliteration, which wins over format)
     * <li>for compression: if mFilesByExt is true and if file's last  extension is registered as compression extension
     * <li>for format: if mFilesByExt is true and if file's last extension is registered as format extension (or the one but last when the last is registered as compression extension)
     * <li>to defaults specified by the parameters of this method (aCompDef, aEncDef, aTranslitDef, aFormatDef)
     * </ul>
     *
     * @param aPath null means no path
     */
    public XFile fileFromString(String aString, File aPath, Compression aCompDef, Encoding aEncDef, Transliteration aTranslitDef, Format aFormatDef)  {
        String[] tokens = cAtSplitter.split(aString);
        Err.assertE(tokens.length <= 5, "File spec can contain maximally 4 !/@'s (%s)", aString);
        
        File file = plainFileFromString(tokens[0], aPath);

        // --- try to get compression and format from extensions ---
        if (mFilesByExt) {
            List<String> extensions = Files.getExtensions(file);
//            System.out.println("Extension:" + extensions);
            //Collections.reverse(extensions);
            
            if (extensions.size() > 0) {
                Compression comp = Compression.getByExt(extensions.get(0));
                if (comp != null) {
                    aCompDef = comp;
                    if (extensions.size() > 1) 
                        aFormatDef = mFormats.getByExt(extensions.get(1), aFormatDef);
                }
                else {
                    aFormatDef = mFormats.getByExt(extensions.get(0), aFormatDef);
                }
            }
        }
        
//        System.out.println("aFormatDef:" + aFormatDef);
//        System.out.println("Tokens:" + tokens);
        // --- get format from tokens ---
        for (int i = 1; i < tokens.length; i++) { 
            String token = tokens[i].toLowerCase();
            if (Compression.isRegisteredId(token)) 
                aCompDef = Compression.getById(token, aCompDef);
            else if (mTranslits.isRegisteredId(token)) 
                aTranslitDef = mTranslits.getById(token, aTranslitDef);
            else if (mFormats.isRegisteredId(token)) {
                aFormatDef = mFormats.getById(token, aFormatDef);
            }
            else
                aEncDef = Encoding.fromString(tokens[i]);
        }
        //System.out.println("aFormatDef:" + aFormatDef);
        
        return new XFile(file, aCompDef, aEncDef, aTranslitDef, aFormatDef);
    }
    
}
