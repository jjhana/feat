//package org.purl.jh.util.err;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.purl.jh.util.col.Cols;
//
///**
// * @todo flushing
// *
// * Levels:
// * <ol>
// * <li>Fatal Error (internal/user)
// * <li>Error (internal/user)
// * <li>Warning  (internal/user)
// * <li>User Error 
// * <li>User Warning  
// * <li>Info - 
// * <li>Config - 
// * <li>Fine - for debugging
// * </ol>
// *
// * Usually the user output ends at Info level, sometimes at Config level.
// *
// * Some functions use two versions - with a single string parameter and 
// * with a format and arguments parameters. The former is provided for efficiency 
// * reasons. Logging where the speed is not crucial (errors & warnings) have only
// * the format+arguments versions. 
// *
// * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
// */
//public class Log {
//    /**
//     * A fatal error. The default handler should shut down the system.
//     */
//    public final static Level cDirect  = new XLevel("", 999);
//    
//    public final static Level cFatalErr  = new XLevel("Fatal Error", 950);
//    public final static Level cErr   = new XLevel("IError", Level.SEVERE.intValue());;
//    public final static Level cW     = new XLevel("IWarning", Level.WARNING.intValue());
//    public final static Level cUErr  = new XLevel("Error", 850);
//    public final static Level cUW    = new XLevel("Warning", 840);;
//    public final static Level cInfo  = Level.INFO;
//    public final static Level cConf  = Level.CONFIG;
//    public final static Level cFine  = Level.FINE;
//    
//// =============================================================================
//// 
//// =============================================================================
//    
//    
//    /** Creates a new instance of AbstractLogger */
//    public Log() {
//    }
//
//    
//    public static void initLoggers(String aName)  {
//        mILogger = DeveloperLogger.getLogger(aName + "Developer");
//        mULogger = Logger.getLogger(aName + "User");
//        mULogger.setUseParentHandlers(false);
//        setLevel(cUW);     
//    }
//    
//    /**
//     * Configure developper logger.
//     * @param aLogFile  name of the file to log to; can be null (no file logging is done)
//     * @param aLevel    minimal logging level (all events at this and higher level are logged)
//     * @param aScreenLog should a simple console logging be done? 
//     */
//    public static void configDeveloperLogger(File aLogFile, int aLevel, boolean aScreenLog) {
//        configDeveloperLogger(aLogFile, new XLevel("", aLevel), aScreenLog);
//    }
//
//    /**
//     * Configure developper logger.
//     * @param aLogFile  name of the file to log to; can be null (no file logging is done)
//     * @param aLevel    minimal logging level (all events at this and higher level are logged)
//     * @param aScreenLog should a simple console logging be done? 
//     */
//    public static void configDeveloperLogger(File aLogFile, Level aLevel, boolean aScreenLog) {
//        mILogger.configLogger(aLogFile, aLevel, aScreenLog);
//    }
//    
//    public static void setLevel(int aLevel) {
//        setLevel(new XLevel("", aLevel));
//    }
//    public static void setLevel(Level aLevel) {
//        setULevel(aLevel);
//        setILevel(aLevel);
//    }
//
//    public static void setULevel(Level aLevel) {
//        mULogger.setLevel(aLevel);     
//        levelsChanged();
//    }
//
//    public static void setILevel(Level aLevel) {
//        mILogger.setLevel(aLevel);     
//        levelsChanged();
//    }
//
//    public static java.util.logging.Logger getUserLogger() {
//        return mULogger;
//    }
//
//    public static DeveloperLogger getDeveloperLogger() {
//        return mILogger;
//    }
//
//    public static void closeLoggers() {
//        mILogger.closeLogger();
//    }
//
//// =============================================================================
//// Output
//// =============================================================================
//    /**
//     * Direct logging
//     */
//    public static void log(String aFormat, Object ... aParams) {
//        log(cDirect, aFormat, aParams);    
//    }
//    
//    /**
//     * Direct logging
//     */
//    public static void log(String aMsg) {
//        log(cDirect, aMsg);
//    }
//
//    /**
//     * Logging to both user and developer directed loggers.
//     *
//     * Direct logging
//     */
//    public static void log(Level aLevel, String aFormat, Object ... aParams) {
//        log(aLevel, String.format(aFormat, aParams));    
//    }
//    
//    /**
//     * Logging to both user and developer directed loggers.
//     * 
//     * All other logging methods call this method.
//     */
//    public static void log(Level aLevel, String aMsg) {
//        mILogger.log(aLevel, aMsg);    
//        mULogger.log(aLevel, aMsg);    
//    }
//    
//    /**
//     * Logging an exception
//     */
//    public static void log(MyException aException, String aFormat, Object ... aParams) {
//        String msg = String.format(aFormat, aParams);
//        mILogger.log(aException.getLevel(), msg, aException);
//        mULogger.log(aException.getLevel(), msg, aException);
//    }
//
//    public static void log(Throwable aException, String aFormat, Object ... aParams) {
////        String msg = String.format(aFormat, aParams) + '\n' + aException.getMessage();
//        String msg = String.format(aFormat, aParams);
////        String stackTrace = getStackTrace(aException);
////        if (aException.getCause() != null) {
////            msg += "\nCause: " + aException.getCause().getMessage();
////            stackTrace += "\nCause:\n " + getStackTrace(aException.getCause()); 
////        }
//        
//        mILogger.log(cErr, msg, aException);
//        mULogger.log(cErr, msg, aException);
//    }
//    
//// -----------------------------------------------------------------------------    
//// Errors
//// -----------------------------------------------------------------------------
//
//    public static void err(String aFormat, Object ... aParams) {
//        log(cErr, aFormat + '\n', aParams);
//    }
//
//    
//    /**
//     * User directed errors.
//     */
//    public static void uErr(String aFormat, Object ... aParams) {
//        log(cUErr, aFormat + '\n', aParams);
//    }
//
//    /**
//     * Logging an exception
//     */
//    public static void uErr(MyException aException, String aFormat, Object ... aParams) {
//        log(aException, aFormat + '\n', aParams);      // @error it should be user
//    }
//
//    public static void uErr(Throwable aException, String aFormat, Object ... aParams) {
//        log(aException, aFormat + '\n', aParams);
//    }
//    
//// -----------------------------------------------------------------------------    
//// Warning
//// -----------------------------------------------------------------------------
//    
//    /**
//     * Reports a warning if a test is fails.
//     */
//    public static void assertW(boolean aTest, String aFormat, Object ... aParams) {
//        if (!aTest) warning(aFormat, aParams);
//    }
//    
//    public static void warning(String aFormat, Object ... aParams) {
//        log(cW, aFormat, aParams);
//    }
//
//    /**
//     * Reports a warning if a test is true.
//     *
//     */
//    public static void warning(boolean aTest, String aFormat, Object ... aParams) {
//        if (aTest) warning(aFormat, aParams);
//    }
//    
//    
//    /**
//     * Reports a user directed warning if a test is true.
//     */
//    public static void uWarning(String aFormat, Object ... aParams) {
//        log(cUW, aFormat, aParams);
//    }
//
//    /**
//     * A user directed warning.
//     */
//    public static void uWarning(boolean aTest, String aFormat, Object ... aParams) {
//        if (aTest) uWarning(aFormat, aParams);
//    }
//    
//// -----------------------------------------------------------------------------    
//// Info
//// -----------------------------------------------------------------------------
//
//    public static void info(String aMsg) {
//        if (mInfo) log(cInfo, aMsg);
//    }
//
//    /**
//     * User information. Usually enabled. 
//     * For example progress messages (...., Processing File ..., etc)
//     * Must be understandable to the user.
//     */
//    public static void info(String aFormat, Object ... aArgs) {
//        if (mInfo) log(cInfo, aFormat, aArgs);
//    }
//
//    public static void dot(int aN) {
//        // // 16383 = 2^26-1
//        if ( mInfo && ((aN & 16383) == 0) ) log(cInfo, ".");
//    }
//    
//// -----------------------------------------------------------------------------    
//// Config
//// -----------------------------------------------------------------------------
//    
//    public static void config(String aMsg) {
//        if (mConfig) log(cConf, aMsg);
//    }
//
//    /**
//     * A detailed user information. Usually not enabled. 
//     * For example names of loaded files, basic configuration, etc.
//     * Must be understandable to the user.
//     */
//    public static void config(String aFormat, Object ... aArgs) {
//        if (mConfig) log(cConf, aFormat, aArgs);
//    }
//    
//
//// -----------------------------------------------------------------------------    
//// Fine
//// -----------------------------------------------------------------------------
//    
//    /**
//     * Debuging messages.
//     * Does not have to be understandable to the user.
//     */
//    public static void fine(String aMsg) {
//        if (mFine) log(cFine, aMsg);
//    }
//
//    public static void fine(String aFormat, Object ... aArgs) {
//        if (mFine) log(cFine, aFormat, aArgs);
//    }
//
//    
//    
//// =============================================================================
//// Implementation  <editor-fold desc="Implementation">"
//// =============================================================================
//    public static java.util.logging.Logger mULogger;
//    public static DeveloperLogger mILogger;
//
//    /** For effectivity, reflects current level of detailed */
//    protected static boolean mInfo;
//    protected static boolean mConfig;
//    protected static boolean mFine;
//
//    protected static String getStackTrace(Throwable aException) {
//        return "\n" + Cols.toStringNl(Arrays.asList(aException.getStackTrace()), "  ");
//    }
//    
//    protected static void levelsChanged() {
//        int level = Math.min(mULogger.getLevel().intValue(), mULogger.getLevel().intValue());
//
//        mInfo   = (level <= Level.INFO.intValue());
//        mConfig = (level <= Level.CONFIG.intValue());
//        mFine   = (level <= Level.FINE.intValue());
//    }
//
//    protected static class XLevel extends Level {
//        XLevel(String aName, int aValue) {
//            super(aName, aValue);
//        }
//    }
//
//// </editor-fold>    
//}
