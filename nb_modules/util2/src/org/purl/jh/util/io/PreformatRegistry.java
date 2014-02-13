/*
 * PreformatRegistry.java
 *
 * Created on February 4, 2006, 11:37 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.purl.jh.util.io;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Used for format, encoding and other registries.
 *
 * @author Jirka
 */
class PreformatRegistry<T extends Preformat> {
    protected Map<String,T> mId2Format = new HashMap<String,T>();
    
// -----------------------------------------------------------------------------
// Create
// -----------------------------------------------------------------------------    

    public PreformatRegistry() {
    }

    /**
     * Id Case is not relevant.
     */
    public void register(T aFormat) {
        mId2Format.put(aFormat.getId().toLowerCase(), aFormat);
    }

// -----------------------------------------------------------------------------
// Use
// -----------------------------------------------------------------------------    
    
    public Collection<T> getFormats() {
        return mId2Format.values();
    }

    public Collection<T> getFormatsExcept(T ... aMinus) {
        Collection<T> formats = getFormats();
        formats.removeAll(Arrays.asList(aMinus)); // keep only non-native formats
        return formats;
    }
    
    public Set<Map.Entry<String,T>> getIdFormats() {
        return mId2Format.entrySet();
    }
    
    /**
     * @param aId the unique id of the desired format; case is not relevant.
     */
    public T getById(String aId) {
        return mId2Format.get(aId);
    }

    /**
     * @param aId the unique id of the desired format; case is not relevant.
     * @param aDef the default format to return, if format with aId id is not found.
     */
    public T getById(String aId, T aDef) {
        T format = mId2Format.get(aId);
        return format == null ? aDef : format;
    }

    /**
     * @param aId the unique format id to look for; case is not relevant.
     */
    public boolean isRegisteredId(String aId) {
        return mId2Format.containsKey(aId);
    }
    
}
