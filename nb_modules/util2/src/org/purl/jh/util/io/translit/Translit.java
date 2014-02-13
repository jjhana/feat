package org.purl.jh.util.io.translit;

/**
 *
 * @author Jiri
 */
public abstract class Translit {
    public String getName()    {return mName;}
    public String getComment() {return mComment;}
    
    public abstract String transliterate(String aC);

    public abstract String transliterate(int aC);
    
    /**
     * Ignores comment
     */
    public int hashCode() {
        return mName.hashCode();
    }

    /**
     * Name, and comment.
     */
    public String toString() {
        return String.format("%s (%s)", mName, mComment);
    }

// =============================================================================    
// Implementation
// =============================================================================    
    
    private String mName;
    private String mComment;
    
    /**
     * Used by register only (constructors can be all default)
     */
    void setName(String aName) {
        mName = aName;
    }
    
}
