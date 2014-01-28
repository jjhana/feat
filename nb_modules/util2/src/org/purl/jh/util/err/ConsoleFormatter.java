package org.purl.jh.util.err;

import java.io.*;
import java.text.*;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Print a very brief summary of the LogRecord in a human readable
 * format.  The summary will typically be 1 or 2 lines. No time is printed.
 *
 */
public class ConsoleFormatter extends Formatter {
    
    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();

//        if (record.getSourceClassName() != null) {
//            sb.append(record.getSourceClassName());
//        } 
//        else {
//            sb.append(record.getLoggerName());
//        }
//        if (record.getSourceMethodName() != null) {
//            sb.append(" ");
//            sb.append(record.getSourceMethodName());
//        }
//        sb.append(lineSeparator);

        //sb.append(record.getLevel().getLocalizedName()).sb.append(": ");
        sb.append(formatMessage(record)).append('\n');

        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } 
            catch (Exception ex) {}
        }
        return sb.toString();
    }

    private MessageFormat formatter;
    
}
