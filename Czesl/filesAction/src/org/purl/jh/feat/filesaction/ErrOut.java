package org.purl.jh.feat.filesaction;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Logger for keeping track of errors/warnings, apart from reporting them.
 * 
 * @author jirka
 */
public class ErrOut extends java.util.logging.Logger {

    public ErrOut(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }
    
    private int errorCount = 0;
    private int warningCount = 0;

    public int getErrorCount() {
        return errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }
    
    protected void record(LogRecord record) {
        if (record.getLevel().intValue() >= Level.SEVERE.intValue()) {
            errorCount++;
        }
        else if (record.getLevel().intValue() == Level.WARNING.intValue()) {
            warningCount++;
        }
    }
    
    @Override
    public void log(LogRecord record) {
        super.log(record);
        record(record);
    }
}
