package org.purl.jh.util.err;

import java.io.PrintWriter;
import lombok.Getter;

/**
 * todo Experimental, very rough handling/reporting of errors in file loading, etc.
 * 
 * @author jirka
 */
@Getter
public class UserOut implements ErrorHandler {
    protected final PrintWriter w;
    protected int errorCount = 0;
    protected int warningCount = 0;
    
    public UserOut(PrintWriter w) {
        this.w = w;
    }

    public UserOut() {
        this(new PrintWriter(System.err, true));
    }

    /** Override to show the information to the user */
    public void show() {}
    
    @Override
    public void severe(String aFormat, Object... aParams) {
        fatalError(aFormat, aParams);
    }

    @Override
    public void severe(Throwable aThrown, String aFormat, Object... aParams) {
        fatalError(aThrown, aFormat, aParams);
    }

    @Override
    public void fatalError(String aFormat, Object... aParams) {
        errorCount++;
        String msg = "ERROR: " + String.format(aFormat, aParams);
        w.println(msg);
        throw new FormatError(msg);
    }

    @Override
    public void fatalError(Throwable aThrown, String aFormat, Object... aParams) {
        String msg = "ERROR: " + String.format(aFormat, aParams) + "\n   " + aThrown.getMessage();
        w.println(msg);
        throw new FormatError(msg);
    }

    @Override
    public void info(String aFormat, Object... aParams) {
        w.println("Info: " + String.format(aFormat, aParams));
    }

    @Override
    public void warning(String aFormat, Object... aParams) {
        warningCount++;
        w.println("Warning: " + String.format(aFormat, aParams));
    }

    /**
     * Sets error and warning counts to zero.
     * Call between running independent jobs. However, if possible create a new
     * CountingLogger for each job.
     */
    @Override
    public void resetCounts() {
        errorCount = 0;
        warningCount = 0;
    }

    
}
