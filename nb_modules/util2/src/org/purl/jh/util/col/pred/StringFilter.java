package org.purl.jh.util.col.pred;

/**
 *
 * @author Jirka
 */
public abstract class StringFilter<T> extends AbstractFilter<T>  implements Filter<T> {
    protected String mString;
    
    /** 
     * Creates a new instance of StringFilter 
     * @param aString null should mean always true
     */
    public StringFilter(String aString) {
        setString(aString);
    }

    /**
     *
     * Notifies listeners of the change
     * @param aString null should mean always true
     */
    public void setString(String aString) {
        mString = aString;
        updated();
    }
}
