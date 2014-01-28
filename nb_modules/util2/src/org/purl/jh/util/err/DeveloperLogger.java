package org.purl.jh.util.err;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.MissingResourceException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import org.purl.jh.util.io.Encoding;

/**
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class DeveloperLogger extends Logger {
    
    /**
     * Find or create a logger for a named subsystem.  
     * If a logger has already been created with the given name it is returned.  
     * Otherwise a new logger is created. If the logger exists it must be 
     * a DeveloperLogger.
     * <p>
     * If a new logger is created its log level will be configured
     * based on the LogManager configuration and it will configured
     * to also send logging output to its parent's handlers.  It will
     * be registered in the LogManager global namespace.
     * 
     * @param	name	A name for the logger.  This should
     *			be a dot-separated name and should normally
     *			be based on the package name or class name
     *			of the subsystem, such as java.net or javax.swing
     * @return a suitable Logger
     * @throws NullPointerException if the name is null.
     */
    public static synchronized DeveloperLogger getLogger(String name) {
	LogManager manager = LogManager.getLogManager();
	Logger result = manager.getLogger(name);
	if (result == null) {
	    result = new DeveloperLogger(name, null);
	    manager.addLogger(result);
	    result = manager.getLogger(name);
	}
	return (DeveloperLogger) result;
    }
    
// =============================================================================
// 
// =============================================================================
    
    public static boolean mScreenLog;   // send internal errors to the screen? 


    /**
     * The second step of initialization.
     * @param aLogFile  name of the file to log to; can be null (no file logging is done)
     * @param aLevel    minimal logging level (all events at this and higher level are logged)
     * @param aScreenLog should a simple console logging be done? 
     */
    public void configLogger(File aLogFile, Level aLevel, boolean aScreenLog) {
        setLevel(aLevel);
        mScreenLog = aScreenLog;
        mLogFile = aLogFile;
        openLoggers();
    }

    public void openLoggers() {
        openScreenLogger();
        if (mLogFile != null) openFileLogger();
    }
    
    public void openScreenLogger() {
        mCh = new ConsoleHandler();
        mCh.setFormatter(new ConsoleFormatter());
        addHandler(mCh);
    }
    
    public void openFileLogger() {
        setUseParentHandlers(false);

        try {
            OutputStream os = new FileOutputStream(mLogFile, true);
            mFh = new StreamHandler(os, new Formatter());
            mFh.setEncoding(Encoding.cUtf8.getId());
            mFh.setLevel(getLevel());
        }
        catch (Throwable e) {   // catch all other errors
            if (!mLoggerErr) {
                severe(String.format("Cannot (re)initializing a logger, errors wont be logged.\n %s", e));
                mLoggerErr = true;
            }
            if (!mScreenLog) setUseParentHandlers(true);
            return;
        } 

        if (mFh != null) {
            addHandler(mFh);
        }
    }
    
    public void closeLogger() {
        if (mFh != null) mFh.close();
    }

    public void flushLogger() {
        if (mFh != null) {
            removeHandler(mFh);
            mFh.close();
            openFileLogger();
        }
    }        
    
    
// =============================================================================
// <editor-fold desc="Implementation" defaultstate="collapsed">
// =============================================================================

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers true.
     *
     * @param	aName	A name for the logger.  This should
     *				be a dot-separated name and should normally
     *				be based on the package name or class name
     *				of the subsystem, such as java.net
     *				or javax.swing.  It may be null for anonymous Loggers.
     * @param 	aResourceBundleName  name of ResourceBundle to be used for localizing
     *				messages for this logger.  May be null if none
     *				of the messages require localization.
     * @throws MissingResourceException if the ResourceBundleName is non-null and
     *		   no corresponding resource can be found.
     */
    protected DeveloperLogger(String aName, String aResourceBundleName) {
        super(aName, aResourceBundleName);
    }
    
    protected File mLogFile;
    protected boolean mLoggerErr;
    protected StreamHandler mFh;
    protected ConsoleHandler mCh;
    
    
// </editor-fold>
}
