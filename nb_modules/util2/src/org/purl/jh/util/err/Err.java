package org.purl.jh.util.err;

import java.io.*;
import org.purl.jh.util.io.LineReader;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.str.Strings;

/**
 * Errors, logging, debuging; exception utilities, a lot of legacy code.
 *
 * Requires org.purl.jh.util.io.Encoding/XFile, org.purl.jh.util.IException/HException.
 * Cooperates with org.purl.jh.util.ui.InfoBar
 *
 * @author Jiri
 * @todo create an on screen logger
 */
public class Err  {
    // to prevent object creation
    private Err() {}
    
// -----------------------------------------------------------------------------
// Errors (Usually Internal, but can be user)
// Using iErr or fErr is preffered if possible    
// -----------------------------------------------------------------------------
    
    public static XException err(String aFormat, Object ... aParams) {
        return new XException(aFormat, aParams);
    }

    public static XException err(Throwable aE)  {
        return new XException(aE);
    }

    public static XException err(Throwable aE, String aFormat, Object ... aParams)  {
        return new XException(aE, aFormat, aParams);
    }

    //@todo really throw??
//    public static XException fatalErr(String aFormat, Object ... aParams) {
//        return new XException(aFormat, aParams);
//    }
// -----------------------------------------------------------------------------
// Internal Errors 
// -----------------------------------------------------------------------------

    public static IException iErr()  {
        return iErr("Should never get here");
    }
    
    public static IException iErr(String aFormat, Object ... aParams)  {
        return new IException(aFormat, aParams);
    }

    public static IException iErr(Throwable aE)  {
        return new IException(aE);
    }

    public static IException iErr(Throwable aE, String aFormat, Object ... aParams)  {
        return new IException(aE, aFormat, aParams);
    }

    
// -----------------------------------------------------------------------------
// User Errors 
// -----------------------------------------------------------------------------

    /** Function that can be used to generate context of an erroneous input */
    public static CharSequence forwardCtx(CharSequence aStr, int aPos, int aMaxLen) {
        return aStr.subSequence(aPos, Math.min(aStr.length(), aPos+aMaxLen));
    }
    
    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(String aFormat, Object ... aParams)  {
        return new FormatError(aFormat, aParams);
    }

    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(Throwable aE) throws FormatError {
        return new FormatError(aE);
    }

    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(Throwable aE, String aFormat, Object ... aParams) throws FormatError {
        return new FormatError(aE, aFormat, aParams);
    }
    
    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(Reader aReader, String aFormat, Object ... aParams) throws FormatError {
        return new FormatError(createReaderInfo(aReader) + aFormat, aParams);
    }

    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(Reader aReader, Throwable aE) throws FormatError {
        return new FormatError(createReaderInfo(aReader));
    }

    /**
     *
     * @throws FormatError
     */
    public static FormatError fErr(Reader aReader, Throwable aE, String aFormat, Object ... aParams) throws FormatError {
        return new FormatError(aE, createReaderInfo(aReader) + aFormat, aParams);
    }

// -----------------------------------------------------------------------------    
// Asserts
// -----------------------------------------------------------------------------    

    public static void assertE(boolean aTest, String aFormat) {
        if (!aTest) err(aFormat);
    }
    
    public static void assertE(boolean aTest, String aFormat, Object ... aParams) {
        if (!aTest) err(aFormat, aParams);
    }

    
// -----------------------------------------------------------------------------    
// Internal Asserts
// -----------------------------------------------------------------------------    

    
    public static void iAssert(boolean aTest, String aFormat) {
        if (!aTest) iErr(aFormat);
    }
    
    public static void iAssert(boolean aTest, String aFormat, Object ... aParams) {
        if (!aTest) iErr(aFormat, aParams);
    }

    
    
// -----------------------------------------------------------------------------    
// Format Asserts
// -----------------------------------------------------------------------------    

    public static void fAssert(boolean aTest, String aFormat, Object ... aParams) {
        if (!aTest) fErr(aFormat, aParams);
    }

    public static void fAssert(boolean aTest, String aFormat) {
        if (!aTest) fErr(aFormat);
    }

    public static void fAssert(boolean aTest, Reader aR, String aFormat, Object ... aParams) {
        if (!aTest) fErr(aR, aFormat, aParams);
    }
    
// =============================================================================
// Implementation  <editor-fold desc="Implementation">"
// =============================================================================

    protected static String createReaderInfo(Reader aReader) {
        if (aReader instanceof LineReader) {
            LineReader r = (LineReader) aReader;
            return String.format("[%d] %s\n", r.getLineNumber(), r.getFile());
        }
        else if (aReader instanceof LineNumberReader) {
            return String.format("[%d] ", ((LineNumberReader)aReader).getLineNumber());
        }
        else
            return "";
    }

// </editor-fold>    
}
