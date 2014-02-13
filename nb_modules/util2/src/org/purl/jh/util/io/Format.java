package org.purl.jh.util.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Immutable except extension set.
 * @todo add format parameters (internal to the format)  
 * @todo remember the real extension
 * @author Jirka
 */
public class Format extends Preformat<Format> {
    
    /**
     * No or default format.
     */
    public static Format cDef;

    /**
     * No or default format.
     */
    public static Format cNone;
    
    static {
        cDef = new Format("Def", "Default format");
        cNone = cDef;
    }
    
// -----------------------------------------------------------------------------
    private Set<String> mExtensions;
    
// -----------------------------------------------------------------------------

    /**
     * Ids are case sensitive
     */
    public Format(String aId, String aDesc) {
        super(aId, aDesc);
        mExtensions = new HashSet<String>();
    }

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    

    public Set<String> getExts() {return mExtensions;}


    public void addExt(String aExt) {
        mExtensions.add(aExt);
    }

    public void addExts(String ... aExts) {
        addExts(Arrays.asList(aExts));
    }

    public void addExts(Collection<String> aExts) {
        mExtensions.addAll(aExts);
    }
    
// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    

    /**
     * An optional reader/writer or any other supporting object.
     *
     * @return null, unless overriden.
     */
    public Object getRW() {
        return null;
    };
    
    /**
     * An optional reader or any other supporting object.
     *
     * @return null, unless overriden.
     */
    public Object getReader() {
        return null;
    };

    /**
     * An optional writer or any other supporting object.
     *
     * @return null, unless overriden.
     */
    public Object getWriter() {
        return null;
    };
    
    
    
    public boolean isDef() {
        return this == cDef;
    }

//    public String fileFilterString() {
//        
//        return String.format("%s files (%s): ",mId + " (" + mDesc + ")";
//    }    
    
}
