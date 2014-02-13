package org.purl.jh.util.err;

import org.purl.jh.util.io.XFile;

/**
 * User Format exception
 * @todo add src (file, etc.) as a field (File/String)
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class FormatError extends XException {
    /**
     * @todo
     * Format error with information about a file it occurred in.
     *
     * @param aFile file where the error occurred 
     * @param aLine line where the error occurred; set to -1 if unknown
     * @param aCol  column where the error occurred; set to -1 if unknown
     */
    public FormatError(XFile aFile, int aLine, int aCol, String aFormat, Object ... aParams) {
        super(aFormat, aParams);
    }
    
    public FormatError(String aFormat, Object ... aParams) {
        super(aFormat, aParams);
    }
    
    public FormatError(Throwable aCause) {
        super(aCause);
    }

    public FormatError(Throwable aCause, String aFormat, Object ... aParams) {
        super(aCause, aFormat, aParams);
    }
}
