package org.purl.jh.util.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Print a brief summary of the LogRecord in a human readable format.  
 * The summary will typically be 1 or 2 lines.
 */
public class Formatter extends java.util.logging.Formatter {
    Date mDate = new Date();
    SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-DD HH:mm:ss");

    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator = java.security.AccessController.doPrivileged(
               (PrivilegedAction<String>) new sun.security.action.GetPropertyAction("line.separator"));

    /**
     * Format the given LogRecord.
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
	StringBuffer sb = new StringBuffer();
	
        // --- create date-time string ---
	mDate.setTime(record.getMillis());
	sb.append(mDateFormat.format(mDate));
	sb.append(' ');

        // 
        String message = formatMessage(record);
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
        }
	sb.append(message);
	sb.append(lineSeparator);
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
}
