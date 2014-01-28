package org.purl.jh.util.err;

/**
 * The preferred exception in our systems (at least from now on).
 * It is unchecked and supports formating.
 *
 * Note: Do not use checked exceptions unless they are really needed. Document
 * all exceptions instead.
 *
 * @author jirka
 */
public class XException extends RuntimeException {
    public XException() {
	super();
    }

    public XException(final String aFormat, final Object ... aParams) {
	super(String.format(aFormat, aParams));
    }

    public XException(final Throwable aCause, final String aFormat, final Object ... aParams) {
	super(String.format(aFormat, aParams), aCause);
    }

    public XException(final Throwable aCause) {
        super(aCause);
    }
}
