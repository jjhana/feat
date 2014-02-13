package org.purl.jh.util.io;

import java.util.Map;

/**
 * Class containing the common things to various File's properties (Encoding, 
 * Format, Transliteration, etc)
 *
 * @author Jirka
 */
abstract class Preformat<T extends Preformat> implements Comparable<T>{
    protected String mId;
    protected String mDesc;
    
    protected Map<String,String> mParams;
    
    /** Creates a new instance of Preformat */
    public Preformat(String aId, String aDesc) {
        mId = aId;
        mDesc = aDesc;
    }
    
    public String getId() {return mId;}


    /**
     * Compares only ids.
     */
    public boolean equals(Object obj) {
        if ( !(obj instanceof Preformat) ) return false;        // @todo!! instance of T
        return mId.equals( ((Preformat)obj).mId);
    }

    
    /**
     * Compares only ids.
     */
    public int compareTo(T aObj) {
        return mId.compareTo(aObj.mId);
    }
    
    /**
     * Considers only id.
     */
    public int hashCode() {
        return mId.hashCode();
    }

    /**
     * Encoding name, possibly a comment (if different from the id).
     */
    public String toString() {
        if (mId.equals(mDesc))
            return mId;
        else
            return mId + " (" + mDesc + ")";
    }    
    
}
