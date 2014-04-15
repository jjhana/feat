package org.purl.jh.util.err;

/**
 * TODO experimental
 *
 * @author jirka
 */
public interface ErrorHandler {
    void fatalError(String aFormat, Object ... aParams);
    void fatalError(Throwable aThrown, String aFormat, Object ... aParams);
    void severe(String aFormat, Object ... aParams);
    void severe(Throwable aThrown, String aFormat, Object ... aParams);
    void warning(String aFormat, Object ... aParams);
    void info(String aFormat, Object ... aParams);

    /**
     * Sets error and warning counts to zero.
     * Call between running independent jobs. However, if possible create a new ErrorHandler for each job.
     */
    void resetCounts();

    int getErrorCount();
    int getWarningCount();
}
