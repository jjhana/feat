package org.purl.jh.util.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.regex.Pattern;
import org.purl.jh.util.err.Err;

/**
 * Line reader with support of:
 * <ul>
 * <li>line counting 
 * <li>pushing a line back
 * <li>keeping track of source
 * </ul>
 *
 * @todo stack of pushed back lines
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class LineReader extends LineNumberReader {
   
    public LineReader(XFile aFile) throws IOException {
        this( IO.openReader(aFile) );
        setFile(aFile);
    }

    /**
     * Create a new line-numbering reader, using the default input-buffer size.
     *
     * @param aIn  a Reader object to provide the underlying stream.
     */
    public LineReader(Reader aIn) {
        super(aIn);
    }

    /**
     * Create a new line-numbering reader, reading characters into a buffer of
     * the given size.
     *
     * @param aIn  a Reader object to provide the underlying stream.
     * @param aSz  an int specifying the size of the buffer.
     */
    public LineReader(Reader aIn, int aSz) {
        super(aIn, aSz);
    }

    // @todo do via another reader
    public LineReader configureSplitting(Pattern aSplittingPattern, int aParts, String aErrMessage) {
        mSplittingPattern = aSplittingPattern;
        mReqParts = aParts;
        mErrorMsg = aErrMessage;
        return this;
    }

    /**
     * Default is true.
     * @param aAllow
     * @return
     */
    public LineReader allowMoreThanReq(boolean aAllow) {
        allowMoreThanReq = aAllow;
        return this;
     }

    
// -----------------------------------------------------------------------------
// Fields
// -----------------------------------------------------------------------------
    
    public LineReader setFile(XFile aFile) {
        mFile = aFile;
        return this;
    }

    public XFile getFile() {
        return mFile;
//        if (in instanceof MultiFileInputStream) {
//            return ((MultiFileInputStream)in).getFile();
//        }
    }

    public String getCommentStr() {
        return commentStr;
    }

    public void setCommentStr(String aCommentStr) {
        commentStr = aCommentStr;
    }

    /**
     * Return the last read line.
     *
     * @return the line returned by the last call of readLine()
     */
    public String getLine() {
        return mLine;
    }

    public String getPushedBackLine() {
        return mPushedBackLine;
    }
    
// -----------------------------------------------------------------------------
// Functions
// -----------------------------------------------------------------------------
    
    /**
     * Read a line of text.  
     * A line is considered to be terminated by any of '\n', '\r' or "\r\n".
     *
     * @return     A String containing the contents of the line, not including
     *             any line-termination characters, or null if the end of the
     *             stream has been reached
     * 
     * @exception  IOException  If an I/O error occurs
     */
    @Override
    public String readLine() throws IOException {
        mLine = (mPushedBackLine == null) ? super.readLine() : mPushedBackLine;
        mPushedBackLine = null;
        return mLine;
    }    
    
    /**
     * Skips empty lines and commented lines.
     * @return first non-empty, non-commented line or null if end of the input source was reached.
     * @throws java.io.IOException
     */
    public String readNonEmptyLine() throws IOException {
        do {
            mLine = (mPushedBackLine == null) ? super.readLine() : mPushedBackLine;
            mPushedBackLine = null;
            if (mLine == null) break;
        } while (mLine.startsWith(commentStr) || mLine.trim().equals("")); // TODO eff - contains a non space
        return mLine;
    }    


    /**
     * Before using this function, configure splitting by configureSplitting. 
     * Empty lines are not skipped, they produce an error.
     */
    public String[] readSplitLine() throws IOException {
        if (readLine() == null) return null;
            
        final String[] strs = mSplittingPattern.split(mLine);
        checkSplits(strs);
        return strs;
    }

    public String[] readSplitNonEmptyLine() throws IOException {
        if (readNonEmptyLine() == null) return null;
            
        final String[] strs = mSplittingPattern.split(mLine);
        checkSplits(strs);
        return strs;
    }

    private void checkSplits(final String[] aStrs) {
        if (aStrs.length >= mReqParts && (allowMoreThanReq || aStrs.length == mReqParts)) return;
        throw Err.fErr(mErrorMsg);
    }

    
    
    /**
     * "Pushes back" the last read line, so that it is returned by the next
     * call of readLine().
     */
    public void pushBack() {
        mPushedBackLine = mLine;
    }

// -----------------------------------------------------------------------------
// Other
// -----------------------------------------------------------------------------

    /**
     * Creates a message string in the format:
     * <pre>
     * Format Error: [shortFileName]:[lineNr] - [aTitle]
     *    [fileName]
     *    [lineString]
     *    [aDetails]
     * </pre>
     */
    public String fErr(String aTitle) {
        return message("Format Error", aTitle);
    }

    /**
     * Creates a message string in the format:
     * <pre>
     * [aPrefix]: [shortFileName]:[lineNr] - [aTitle]
     *    [fileName]
     *    [lineString]
     * </pre>
     */
    public String message(String aPrefix, String aTitle) {
        return String.format("%s: %s:%d - %s\n   %s\n   %s", 
                aPrefix, getFile().file().getName(), getLineNumber(), aTitle, getFile(), mLine);
    }

    /**
     * Creates a message string in the format:
     * <pre>
     * [aPrefix]: [shortFileName]:[lineNr] - [aTitle]
     *    [fileName]
     *    [lineString]
     *    [aDetails]
     * </pre>
     */
    public String message(String aPrefix, String aTitle, String aDetails) {
        return message(aPrefix, aTitle) + '\n' + aDetails;
    }
    
    public String logString() {
        return String.format("[%d] %s", getLineNumber(), getFile());
    }

    public String logStringWL() {
        return String.format("[%d] %s\n", getLineNumber(), getFile(), mLine);
    }
    
// =============================================================================
// <editor-fold desc="Implementation" defaultstate="collapsed">
// =============================================================================
    
    protected String mLine = null;
    private String mPushedBackLine = null;

    protected XFile mFile; //@todo generalize to any source
    protected String commentStr = "//";
    // spplitting support
    int mReqParts;
    boolean allowMoreThanReq = true;
    Pattern mSplittingPattern;
    String mErrorMsg;

// </editor-fold>
}
