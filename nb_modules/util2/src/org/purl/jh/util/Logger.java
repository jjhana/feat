package org.purl.jh.util;

import java.util.logging.Level;

/**
 * java.util.logging.Logger wrapper with more 
 * comfortable logging methods (with printf formatting).
 * 
 * @author jirka
 */
public class Logger {
    private final java.util.logging.Logger lg;

    public static synchronized Logger getLogger(final Class<?> aClass) {
        return new Logger(java.util.logging.Logger.getLogger(aClass.getName()));
    }

    public static synchronized Logger getLogger(final String name) {
        return new Logger(java.util.logging.Logger.getLogger(name));
    }

    public Logger(java.util.logging.Logger aLogger) {
        lg = aLogger;
    }

    public java.util.logging.Logger getLg() {
        return lg;
    }

    public boolean isLoggable(Level level) {
        return lg.isLoggable(level);
    }

    public void fatAssert(boolean aTest, String aFormat, Object ... aParams) {
        if (aTest) return;
        severe(aFormat, aParams);
        throw new RuntimeException("Fatal Error");
    }

    public void sevAssert(boolean aTest, String aFormat, Object ... aParams) {
        if (!aTest) severe(aFormat, aParams);
    }
    
    
    
    public final boolean severe() {
        return lg.isLoggable(Level.SEVERE);
    }

    public final boolean warning() {
        return lg.isLoggable(Level.WARNING);
    }

    public final boolean info() {
        return lg.isLoggable(Level.INFO);
    }

    public final boolean config() {
        return lg.isLoggable(Level.CONFIG);
    }

    public final boolean fine() {
        return lg.isLoggable(Level.FINE);
    }

    public final boolean finer() {
        return lg.isLoggable(Level.FINER);
    }

    public final boolean finest() {
        return lg.isLoggable(Level.FINEST);
    }


    public void log(Level aLevel, Throwable aThrown, String aFormat, Object ... aParams) {
        lg.log(aLevel, String.format(aFormat, aParams), aThrown);
    }

    public void log(Level aLevel, Throwable aThrown, String aMsg) {
        lg.log(aLevel, aMsg, aThrown);
    }

    public void log(Level aLevel, String aFormat, Object ... aParams) {
        lg.log(aLevel, String.format(aFormat, aParams));
    }

    public void log(Level aLevel, String aMsg) {
        lg.log(aLevel, aMsg);
    }

    public void severe(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.SEVERE) ) lg.severe(String.format(aFormat, aParams));
    }

    public void severe(String aFormat) {
        if (lg.isLoggable(Level.SEVERE) ) lg.severe(aFormat);
    }

    public void severe(Throwable aThrown, String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.SEVERE) ) {
            if (aThrown == null) aThrown = new Throwable("Dummy, was null");   // to get stack-trace
            log(Level.SEVERE, aThrown, aFormat, aParams);
        }
    }

    public void warning(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.WARNING) ) lg.warning(String.format(aFormat, aParams));
    }

    public void warning(String aFormat) {
        if (lg.isLoggable(Level.WARNING) ) lg.warning(aFormat);
    }

    public void info(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.INFO) ) lg.info(String.format(aFormat, aParams));
    }

    public void info(String aFormat) {
        if (lg.isLoggable(Level.INFO) ) lg.info(aFormat);
    }

    public void config(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.CONFIG) ) lg.config(String.format(aFormat, aParams));
    }

    public void config(String aFormat) {
        if (lg.isLoggable(Level.CONFIG) ) lg.config(aFormat);
    }

    public void fine(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.FINE) ) lg.fine(String.format(aFormat, aParams));
    }

    public void fine(String aFormat) {
        if (lg.isLoggable(Level.FINE) ) lg.fine(aFormat);
    }

    public void finer(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.FINER) ) lg.finer(String.format(aFormat, aParams));
    }

    public void finer(String aFormat) {
        if (lg.isLoggable(Level.FINER) ) lg.finer(aFormat);
    }

    public void finest(String aFormat, Object ... aParams) {
        if (lg.isLoggable(Level.FINEST) ) lg.finest(String.format(aFormat, aParams));
    }

    public void finest(String aFormat) {
        if (lg.isLoggable(Level.FINEST) ) lg.finest(aFormat);
    }
}
