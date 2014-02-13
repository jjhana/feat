package org.purl.jh.util;

import java.util.logging.Level;
import org.purl.jh.util.err.ErrorHandler;

/**
 *
 * @author jirka
 */
public class CountingLogger extends Logger implements ErrorHandler {
    private int errorCount = 0;
    private int warningCount = 0;

    /** Default logger for easy use in testing code */
    public static synchronized CountingLogger getLogger() {
        return new CountingLogger(java.util.logging.Logger.getLogger("generic"));
    }

    public static synchronized CountingLogger getLogger(final Class<?> aClass) {
        return new CountingLogger(java.util.logging.Logger.getLogger(aClass.getName()));
    }

    public static synchronized CountingLogger getLogger(final String name) {
        return new CountingLogger(java.util.logging.Logger.getLogger(name));
    }

    public CountingLogger(java.util.logging.Logger aLogger) {
        super(aLogger);
    }

    /**
     * Sets error and warning counts to zero.
     * Call between running independent jobs. However, if possible create a new
     * CountingLogger for each job.
     */
    public void resetCounts() {
        errorCount = 0;
        warningCount = 0;
    }

    @Override
    public void log(Level aLevel, Throwable aThrown, String aFormat, Object ... aParams) {
        record(aLevel);
        super.log(aLevel, aThrown, aFormat, aParams);
    }

    @Override
    public void log(Level aLevel, Throwable aThrown, String aMsg) {
        record(aLevel);
        super.log(aLevel, aThrown, aMsg);
    }

    @Override
    public void log(Level aLevel, String aFormat, Object ... aParams) {
        record(aLevel);
        super.log(aLevel, aFormat, aParams);
    }

    @Override
    public void log(Level aLevel, String aMsg) {
        record(aLevel);
        super.log(aLevel, aMsg);
    }

    @Override
    public void severe(String aFormat, Object ... aParams) {
        errorCount++;
        super.severe(aFormat, aParams);
    }

    @Override
    public void severe(String aFormat) {
        errorCount++;
        super.severe(aFormat);
    }

    @Override
    public void severe(Throwable aThrown, String aFormat, Object ... aParams) {
        errorCount++;
        super.severe(aThrown, aFormat, aParams);
    }

    @Override
    public void warning(String aFormat, Object ... aParams) {
        warningCount++;
        super.warning(aFormat, aParams);
    }

    @Override
    public void warning(String aFormat) {
        warningCount++;
        super.warning(aFormat);
    }


    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    protected void record(final Level aLevel) {
        if (aLevel.intValue() >= Level.SEVERE.intValue()) {
            errorCount++;
        }
        else if (aLevel.intValue() == Level.WARNING.intValue()) {
            warningCount++;
        }
    }

    /**
     * Does the same thing as severe.
     * 
     * @param aFormat
     * @param aParams 
     */
    @Override
    public void fatalError(String aFormat, Object... aParams) {
        severe(aFormat, aParams);
    }

    /**
     * Does the same thing as severe.
     * @param aThrown
     * @param aFormat
     * @param aParams 
     */
    @Override
    public void fatalError(Throwable aThrown, String aFormat, Object... aParams) {
        severe(aThrown, aFormat, aParams);
    }

}
        