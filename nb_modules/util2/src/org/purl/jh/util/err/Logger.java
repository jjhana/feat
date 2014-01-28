package org.purl.jh.util.err;

import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class extending the standard java.util's logger with varargs.
 *
 * TODO use {} as with std logger instead of String.format
 *
 * Levels:
* <ul>
 * <li>SEVERE (highest value)
 * <li>WARNING
 * <li>INFO
 * <li>CONFIG
 * <li>FINE
 * <li>FINER
 * <li>FINEST  (lowest value)
 * </ul>

 * @author Jirka
 */
public final class Logger {
    private final java.util.logging.Logger delegate;

    public static synchronized Logger getLogger(String name, String resourceBundleName) {
        return new Logger( java.util.logging.Logger.getLogger(name, resourceBundleName) );
    }

    public static synchronized Logger getLogger(Class<?> aClass) {
        return new Logger( java.util.logging.Logger.getLogger(aClass.getName()) );
    }

    public static synchronized Logger getLogger(String name) {
        return new Logger( java.util.logging.Logger.getLogger(name) );
    }

    public static synchronized Logger getAnonymousLogger(String resourceBundleName) {
        return new Logger( java.util.logging.Logger.getAnonymousLogger(resourceBundleName) );
    }

    public static synchronized Logger getAnonymousLogger() {
        return new Logger( java.util.logging.Logger.getAnonymousLogger() );
    }

    private Logger(java.util.logging.Logger aJuLogger) {
        delegate = aJuLogger;
    }

    /**
     * Checks if the level is loggable. 
     *
     * Use only when the preparation of parameters is expensive (say reporting
     * a collection.
     *
     * if (log.severe()) log.severe("abc %s", Cols.toString(list));
     */
    public final boolean severe() {
        return delegate.isLoggable(Level.SEVERE);
    }

    public final boolean warning() {
        return delegate.isLoggable(Level.WARNING);
    }

    public final boolean info() {
        return delegate.isLoggable(Level.INFO);
    }

    public final boolean config() {
        return delegate.isLoggable(Level.CONFIG);
    }

    public final boolean fine() {
        return delegate.isLoggable(Level.FINE);
    }

    public final boolean finer() {
        return delegate.isLoggable(Level.FINER);
    }

    public final boolean finest() {
        return delegate.isLoggable(Level.FINEST);
    }



    public void severe(String msg) {
        delegate.severe(msg);
    }

    public void severe(String msg, Object ... args) {
        if (delegate.isLoggable(Level.SEVERE)) {
            delegate.severe(String.format(msg, args));
        }
    }

    public void warning(String msg) {
        delegate.warning(msg);
    }

    public void warning(String msg, Object ... args) {
        if (delegate.isLoggable(Level.WARNING)) {
            delegate.warning(String.format(msg, args));
        }
    }

    public void info(String msg) {
        delegate.info(msg);
    }

    public void info(String msg, Object ... args) {
        if (delegate.isLoggable(Level.INFO)) {
            delegate.info(String.format(msg, args));
        }
    }

    public void config(String msg) {
        delegate.config(msg);
    }

    public void config(String msg, Object ... args) {
        if (delegate.isLoggable(Level.CONFIG)) {
            delegate.severe(String.format(msg, args));
        }
    }

    public void fine(String msg) {
        delegate.fine(msg);
    }

    public void fine(String msg, Object ... args) {
        if (delegate.isLoggable(Level.FINE)) {
            delegate.fine(String.format(msg, args));
        }
    }

    public void finer(String msg) {
        delegate.finer(msg);
    }

    public void finer(String msg, Object ... args) {
        if (delegate.isLoggable(Level.FINER)) {
            delegate.finer(String.format(msg, args));
        }
        delegate.finer(msg);
    }

    public void finest(String msg) {
        delegate.finest(msg);
    }

    public void finest(String msg, Object ... args) {
        if (delegate.isLoggable(Level.FINEST)) {
            delegate.finest(String.format(msg, args));
        }
    }




//    public void setParent(Logger parent) {
//        delegate.setParent(parent);
//    }

// delegate below



    public void throwing(String sourceClass, String sourceMethod, Throwable thrown) {
        delegate.throwing(sourceClass, sourceMethod, thrown);
    }


    public synchronized void setUseParentHandlers(boolean useParentHandlers) {
        delegate.setUseParentHandlers(useParentHandlers);
    }


    public void setLevel(Level newLevel) throws SecurityException {
        delegate.setLevel(newLevel);
    }

    public void setFilter(Filter newFilter) throws SecurityException {
        delegate.setFilter(newFilter);
    }

    public synchronized void removeHandler(Handler handler) throws SecurityException {
        delegate.removeHandler(handler);
    }

    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Throwable thrown) {
        delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }

    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object[] params) {
        delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }

    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg, Object param1) {
        delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }

    public void logrb(Level level, String sourceClass, String sourceMethod, String bundleName, String msg) {
        delegate.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        delegate.logp(level, sourceClass, sourceMethod, msg, thrown);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        delegate.logp(level, sourceClass, sourceMethod, msg, params);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        delegate.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    public void logp(Level level, String sourceClass, String sourceMethod, String msg) {
        delegate.logp(level, sourceClass, sourceMethod, msg);
    }

    public void log(Level level, String msg, Throwable thrown) {
        delegate.log(level, msg, thrown);
    }

    public void log(Level level, String msg, Object[] params) {
        delegate.log(level, msg, params);
    }

    public void log(Level level, String msg, Object param1) {
        delegate.log(level, msg, param1);
    }

    public void log(Level level, String msg) {
        delegate.log(level, msg);
    }

    public void log(LogRecord record) {
        delegate.log(record);
    }

    public boolean isLoggable(Level level) {
        return delegate.isLoggable(level);
    }

    public synchronized boolean getUseParentHandlers() {
        return delegate.getUseParentHandlers();
    }

    public String getResourceBundleName() {
        return delegate.getResourceBundleName();
    }

    public ResourceBundle getResourceBundle() {
        return delegate.getResourceBundle();
    }

    public java.util.logging.Logger getParent() {
        return delegate.getParent();
    }

    public String getName() {
        return delegate.getName();
    }


    public Level getLevel() {
        return delegate.getLevel();
    }

    public synchronized Handler[] getHandlers() {
        return delegate.getHandlers();
    }

    public Filter getFilter() {
        return delegate.getFilter();
    }


    public void exiting(String sourceClass, String sourceMethod, Object result) {
        delegate.exiting(sourceClass, sourceMethod, result);
    }

    public void exiting(String sourceClass, String sourceMethod) {
        delegate.exiting(sourceClass, sourceMethod);
    }

    public void entering(String sourceClass, String sourceMethod, Object[] params) {
        delegate.entering(sourceClass, sourceMethod, params);
    }

    public void entering(String sourceClass, String sourceMethod, Object param1) {
        delegate.entering(sourceClass, sourceMethod, param1);
    }

    public void entering(String sourceClass, String sourceMethod) {
        delegate.entering(sourceClass, sourceMethod);
    }

    public synchronized void addHandler(Handler handler) throws SecurityException {
        delegate.addHandler(handler);
    }

}
