package org.purl.jh.util.str.pp;

/**
 * Converts an object from an to string.
 * 
 * Overriding classes should respect the contract that 
 * forall X . fromString(toString(X)) equals X (assuming equals is implemented "reasonably")
 * 
 * TODO does not work for null
 * 
 * @author Jirka
 */
public abstract class Pp<T> implements Printer<T>, Parser<T> {
    /**
     * @param aString a trimmed string to convert
     */
    public abstract T fromString(String aString);

    public T fromString(String aString, Object aAdditionalInfo) {
        return fromString(aString);
    }
    
    public String toString(T aObject)    {return aObject.toString();}
}
