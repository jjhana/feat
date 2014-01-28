package org.purl.jh.util.err;

/**
 * TODO experimental
 *
 * @author jirka
 */
public interface ErrorHandler {
    public void fatalError(String aFormat, Object ... aParams);
    public void fatalError(Throwable aThrown, String aFormat, Object ... aParams);
    public void severe(String aFormat, Object ... aParams);
    public void severe(Throwable aThrown, String aFormat, Object ... aParams);
    public void warning(String aFormat, Object ... aParams);
    public void info(String aFormat, Object ... aParams);
}
