package org.purl.jh.util.io;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.purl.jh.util.err.Err;


/**
 * TODO Use as a data source (multiple files, url, ..)
 */
public class XFile implements Cloneable,  Comparable<XFile> {
    private static XFiler mFiler = new XFiler();
    
    private File mFile;
    private Properties properties;

    // todo make immutable???
    // todo make extensible, with predefined properties, hashtable???
    public static class Properties implements Cloneable, Comparable<Properties> {
        private Compression compression;
        private Encoding enc;    
        private Transliteration translit;
        private Format format;

        public Properties(Properties aProperties) {
            compression = aProperties.compression;
            enc = aProperties.enc;
            translit = aProperties.translit;
            format = aProperties.format;
        }

        public Properties(Compression aCompression, Encoding aEnc, Transliteration aTranslit, Format aFormat) {
            compression = aCompression;
            enc = aEnc;
            translit = aTranslit;
            format = aFormat;
        }

        public Compression getCompression() {
            return compression;
        }

        public Properties setCompression(Compression compression) {
            this.compression = compression;
            return this;
        }

        public Encoding getEnc() {
            return enc;
        }

        public Properties setEnc(Encoding enc) {
            this.enc = enc;
            return this;
        }

        public Format getFormat() {
            return format;
        }

        public Properties setFormat(Format format) {
            this.format = format;
            return this;
        }

        public Transliteration getTranslit() {
            return translit;
        }

        public Properties setTranslit(Transliteration translit) {
            this.translit = translit;
            return this;
        }

        @Override
        public String toString() {
            return String.format("c: %s, e: %s, t: %s. f: %s", compression, enc, translit, format);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + (this.compression != null ? this.compression.hashCode() : 0);
            hash = 17 * hash + (this.enc != null ? this.enc.hashCode() : 0);
            hash = 17 * hash + (this.translit != null ? this.translit.hashCode() : 0);
            hash = 17 * hash + (this.format != null ? this.format.hashCode() : 0);
            return hash;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return (obj != null) && (obj instanceof XFile.Properties) && compareTo((Properties)obj) == 0;
         }

        public int compareTo(XFile.Properties aProps) {
            int tmp = compareObjs(compression, aProps.compression);
            if (tmp != 0) return tmp;

            tmp = compareObjs(enc,aProps.enc);
            if (tmp != 0) return tmp;

            tmp = compareObjs(translit, aProps.translit);
            if (tmp != 0) return tmp;

            return compareObjs(format,aProps.format);

        }
    }

    /**
     * Sets the object responsible for converting strings, etc to XFile objects.
     */
    public static void setFiler(XFiler aFiler) {
        mFiler = aFiler;
    }
    
    /**
     * Creates a new xfile, that has all the properties of the supplied xfile parameter.
     * @param aXFile file to copy properties from (can be a directory)
     */
    public static XFile create(File aFile, XFile aXFile) {
        return new XFile(aFile, aXFile.getProperties());
    }

// -----------------------------------------------------------------------------    

    public XFile(File aFile, Properties aProperties) {
        mFile = aFile;
        properties = aProperties;
    }

    /**
     * 
     * None of the parameters can be null.
     *
     * @param aFile raw file or directory
     * @param aCompression compression; use Compression.none for no copression
     * @param aEnc character encoding; use ???? @todo for default encoding
     * @param aTranslit transliteration, use Transliteration.cNone for no transliteration
     * @param aFormat format of the file; use Format.cDef for default/unspecified format
     */
    public XFile(File aFile, Compression aCompression, Encoding aEnc, Transliteration aTranslit, Format aFormat) {
        Err.iAssert(aFile != null, "aFile cannot be null");
        Err.iAssert(aCompression != null, "aCompression cannot be null");
        Err.iAssert(aEnc != null, "aEnc cannot be null");
        Err.iAssert(aTranslit != null, "aTranslit cannot be null");
        Err.iAssert(aFormat != null, "aFormat cannot be null");

        mFile = aFile;
        properties = new XFile.Properties(aCompression, aEnc, aTranslit, aFormat);
    }




    /**
     * A file in the UTF-8 encoding, default format.
     *
     * @param aFile
     */
    public XFile(String aFile) {
        this(new File(aFile));
    }

    /**
     * A file in the UTF-8 encoding, default format.
     *
     * @param aFile file
     */
    public XFile(File aFile) {
        this(aFile, Encoding.cUtf8);
    }

    /**
     *
     * The file is assumed to be in a default format.
     * 
     * @param aFile file
     * @param aEnc encoding
     */
    public XFile(File aFile, Encoding aEnc) {
        this(aFile, aEnc, Format.cDef);
    }

    /**
     * 
     * @param aFile 
     * @param aEnc 
     */
    public XFile(File aFile, String aEnc) {
        this(aFile, Encoding.fromString(aEnc));
    }

    /**
     *
     * @param aFile
     * @param aEnc
     * @param aFormat
     */
    public XFile(File aFile, Encoding aEnc, Format aFormat) {
        this(aFile, Compression.none, aEnc, Transliteration.cNone, aFormat);
    }

    public XFile(XFile aDir, String aName) {
        this( new File(aDir.file(), aName), aDir.getProperties() );
    }


// -----------------------------------------------------------------------------
// Attributes
// -----------------------------------------------------------------------------
    

    /**
     * 
     * @return 
     */
    public File        file()          {return mFile;}

    public File        getFile()        {return mFile;}


    public XFile.Properties getProperties() {
        return properties;
    }


    /**
     * Encoding; cannnot be null
     * @return 
     */
    public Encoding    enc()           {return properties.enc;}

    /**
     * 
     * @return 
     */
    public Transliteration translit()      {return properties.translit;}

    /**
     * Format; cannot be null (use Format.cDef instead)
     * @return 
     */
    public Format      format()        {return properties.format;}
    /**
     * 
     * @return 
     */
    public Compression compression()   {return properties.compression;}


// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------

    public XFile setProperties(Properties aProperties) {
        return new XFile(mFile, aProperties);
    }
    
    /**
     * Returns a new object that is like this except its file name.
     * (i.e. encoding, format and compression are the same)
     * 
     * 
     * @see #setFile(File,String)
     * @see #setShortFile(String)
     * @param aFile the new file name
     * @return 
     */
    public XFile setFile(File aFile) {
        return new XFile(aFile, new Properties(properties));
    }


    /**
     * Returns a new object that is like this except its file name.
     * (i.e. encoding, format and compression are the same)
     * 
     * 
     * @see #setFile(File)
     * @param aPath path of the new file name
     * @param aShortName last portion of the new file name
     * @return 
     */
    public XFile setFile(File aPath, String aShortName) {
        return setFile(new File(aPath, aShortName));
    }

    /**
     * Returns a new object that is like this except the last portion of its name.
     * (i.e. path, encoding, format and compression are the same)
     * 
     * @see #setFile(File)
     * @param aFile 
     * @return 
     */
    public XFile setShortFile(String aFile) {
        return setFile(new File(mFile.getParent(), aFile));
    }

    /**
     * Returns the file name without the path and extension.
     * Extension is the part following the last dot, if any.
     */
    public String getNameOnly() {
        String name = mFile.getName();
        int lastDotIdx = name.lastIndexOf('.');
        
        return (lastDotIdx == -1) ? name : name.substring(0, lastDotIdx);
        
    }

    /**
     * Returns extension, i.e the part following the last dot of the short-name.
     * If there is no extension, an empty string is returned.
     */
    public String getExtension() {
        String str = mFile.getName();
        int lastDotIdx = str.lastIndexOf('.');
        
        return (lastDotIdx == -1) ? "" : str.substring(lastDotIdx+1 );
    }

    /**
     * 
     * @param aExtension 
     * @return 
     */
    public XFile addExtension(String aExtension) {
        return setFile( new File(mFile.getPath() + '.' + aExtension) );
    }
    
    /**
     * If this file is absolute, nothing happens, otherwise it is put into the 
     * specified directory.
     * @param aDir 
     * @return 
     */
    public XFile putIntoADir(File aDir) {
        if (mFile.isAbsolute())
            return this;
        else
            return setFile( new File(aDir, mFile.getPath()) ); 
    }

    /**
     * 
     * @param aDir 
     * @return 
     */
    public XFile replaceDir(File aDir){
        return setFile( new File(aDir, mFile.getName()) );
    }

    public XFile setEncoding(Encoding aEnc) {
        return new XFile(mFile, new Properties(properties).setEnc(aEnc));
    }

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

    /**
     * Returns a list of xfile objects denoting the files in this
     * directory. All X-properties (compression, encoding, ...) are inherited.
     * @return
     */
    public List<XFile> listFiles() {
        File[] files = mFile.listFiles();
        if (files == null) return null;
        
        return Files.toXFiles(Arrays.asList(files), properties);
    }
    
    /**
     * 
     * @return 
     */
    @Override
    protected XFile clone() {
        try {
            XFile file = (XFile) super.clone();
            file.properties = new Properties(properties);
            return file;
        }
        catch(Exception e) {}
        return null;
    }

    
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return mFile + " (" + properties + ")";
    }

    /**
     * Check if the specified file is equal to this file.
     *
     * @param obj  the other file (can be null)
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
    	return (obj != null) && (obj instanceof XFile) && compareTo((XFile)obj) == 0;
    }

    /**
     * 
     * @param aFile 
     * @return 
     */
    @Override
    public int compareTo(XFile aFile) {
        int tmp = mFile.compareTo(aFile.mFile);
        if (tmp != 0) return tmp;
        
        return properties.compareTo(aFile.properties);
    }
    
    /*
     * @todo A general routine?
     * Null is smaller than anything else (two nulls are equal)
     * @param aObj1
     * @param aObj2
     * @return 0 if aObj1 equals aObj2;
     *         a negative value if aObj1 is smaller than aObj2;
     *         a positive value if aObj1 is larger than aObj2.
     */
    public static <T extends Comparable<T>> int compareObjs(T aObj1, T aObj2) {
        if (aObj1 == aObj2) return 0;       // a quick check
        
        if (aObj1 == null) {
            return -1;
        }
        else if (aObj2 == null) {
            return 1;
        }
        else {
            return aObj1.compareTo(aObj2);
        }
    }

    /**
     * The hashcode relies sollely on the file object.
     * @return 
     */ 
    @Override
    public int hashCode() {
        return mFile.hashCode();
    }
}
