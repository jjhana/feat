package org.purl.jh.util.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @todo support embedded directories (via flat function)
 * @author Jirka
 */
class MultiFileInputStream extends InputStream {
    protected Iterable<XFile> mFiles;
    protected XFile mCurFile;
    protected Iterator<XFile> mFileIt;
    protected InputStream mCurIs;
    protected int mCurIsIdx;   // 1 based

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
            
    public MultiFileInputStream(Iterable<XFile> aFiles) throws IOException {
        init(aFiles);
    }

    public MultiFileInputStream(XFile ... aFiles) throws IOException {
        this(Arrays.asList(aFiles));
    }

    /**
     * A convenience constructor.
     */
    public MultiFileInputStream(XFile aDirectory) throws IOException {
        this(  aDirectory.listFiles() );
    }
    
//    /**
//     * @todo this should be part of XFiles and then called as MultiFileInputStream(XFiles.files(...));
//     * @para aFiles 
//     * @deprecated 
//     */
//    public MultiFileInputStream(Collection<File> aFiles, Compression aCompression, Encoding aEnc, Transliteration aTranslit, Format aFormat) throws IOException {
//        ArrayList<XFile> files = new ArrayList<XFile>();
//        
//        for (File file : aFiles) {
//            files.add(new XFile(file, aCompression, aEnc, aTranslit, aFormat));
//        }
//
//        init(files);
//    }
//
//    /**
//     * @deprecated 
//     */
//    public MultiFileInputStream(Collection<File> aFiles, Encoding aEnc, Format aFormat) throws IOException {
//        this(aFiles, Compression.none, aEnc, Transliteration.cNone, aFormat);
//    }
//
//    /**
//     * @deprecated 
//     */
//    public MultiFileInputStream(Collection<File> aFiles, Encoding aEnc) throws IOException {
//        this(aFiles, Compression.none, aEnc, Transliteration.cNone, Format.cDef);
//    }

    
// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
    public XFile getCurFile() {
        if (mCurIs instanceof MultiFileInputStream) {
            return ((MultiFileInputStream)mCurIs).getCurFile();
        }
        else {
            return mCurFile;
        }
    }
    
    public int read() throws IOException {
        if (mCurIs == null) return -1;
        int tmp;
        try {
            tmp = mCurIs.read();
        }
        catch(IOException e) {
            System.out.println(e);
            System.out.println("mFiles " + mFiles);
            System.out.println("mCurFile " + mCurFile);
            System.out.println("mCurIsIdx " + mCurIsIdx);
            throw e;
        }
        if (tmp != -1) return tmp;

        do {
            mCurIs.close();
            mCurIs = null;
            if (!openNext()) break;

            tmp = mCurIs.read();
        } while (tmp == -1);

        return tmp;
    }

    public void close() throws IOException {
        if (mCurIs != null) mCurIs.close();
    }
// =============================================================================
//
// =============================================================================    

    protected void init(Iterable<XFile> aFiles) throws IOException {
        mFiles = aFiles;
        mFileIt = mFiles.iterator();
        mCurIsIdx = 0;
        openNext();
    }

    //@todo use some chanel ?Err
    protected boolean openNext() throws IOException {
        if (!mFileIt.hasNext()) return false;

        mCurFile = mFileIt.next();
        mCurIs = IO.openInputStream(mCurFile);
        mCurIsIdx++;

        // todo some logging channel
        String size = (mFiles instanceof Collection) ? (""+((Collection)mFiles).size()) : "?";
        System.out.printf("File [%d/%s]: %s\n", mCurIsIdx, size, mCurFile);
        
        return true;
    }
}

