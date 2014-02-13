package org.purl.jh.util.err;

/**
 * Unexpected Internal Error.
 *
 * @author jirka
 */
public class IException extends XException {
    public IException() {
    }

    public IException(Throwable cause) {
        super(cause);
    }

    public IException(Throwable cause, String message, Object ... aObjects) {
        super(cause, message, aObjects);
    }

    public IException(String message, Object ... aObjects) {
        super(message, aObjects);
    }
    
}
