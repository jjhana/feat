/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.util.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 *
 * @author j
 */
public final class Throwables {

    private Throwables() {
    }
    
    /**
     * If the cause chain contains an exception of the specified type, it is returned.
     * @param aThrowable
     * @param aEmbeddedClass
     * @return exception of the aEmbeddedClass type, or null if it is not in the cause chain.
     */
    public static Throwable getCause(final Throwable aThrowable, final Class<? extends Throwable> aEmbeddedClass) {
        Throwable cur = aThrowable;
        while (cur != null) {
            if (aEmbeddedClass.isInstance(cur)) return cur;
            cur = cur.getCause();
        }
        
        return null;
    }
    
    /**
     * Returns a throwable's stack trace as a string.
     * @param aThrowable
     * @return 
     * Todo Move to utils
     */
    public static String stackTrace2String(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

//    /**
//    * Defines a custom format for the stack trace as String.
//      * From http://www.javapractices.com/topic/TopicAction.do?Id=78
//    */
//    public static String getCustomStackTrace(Throwable aThrowable) {
//        //add the class name and any message passed to constructor
//        final StringBuilder result = new StringBuilder( "BOO-BOO: " );
//        result.append(aThrowable.toString());
//        final String NEW_LINE = System.getProperty("line.separator");
//        result.append(NEW_LINE);
//
//        //add each element of the stack trace
//        for (StackTraceElement element : aThrowable.getStackTrace() ){
//        result.append( element );
//        result.append( NEW_LINE );
//        }
//        return result.toString();
//    }
    
}
