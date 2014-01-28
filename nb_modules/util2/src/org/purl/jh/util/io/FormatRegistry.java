package org.purl.jh.util.io;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @todo compression 
 * @author Jiri
 */
public class FormatRegistry extends PreformatRegistry<Format> {
    private final Map<String,Format> mExt2Format = new HashMap<String,Format>();

// -----------------------------------------------------------------------------
// Create 
// -----------------------------------------------------------------------------    

    public void register(Format aFormat, Set<String> aExts) {
        aFormat.addExts(aExts);
        register(aFormat);
        for (String ext : aFormat.getExts())
            mExt2Format.put(ext, aFormat);
    }

    public void register(Format aFormat, String ... aExts) {
        register( aFormat, new HashSet<String>(Arrays.asList(aExts)) );
    }

    public void register(String aId, String aDesc, Set<String> aExts) {
        register(new Format(aId, aDesc), aExts);
    }

// -----------------------------------------------------------------------------
// Use
// -----------------------------------------------------------------------------    
    
    public Format getByExt(String aExt) {
        return mExt2Format.get(aExt);
    }

    public Format getByExt(String aFileName, Format aFormatDef) {
        Format format = mExt2Format.get(aFileName);
        return format == null ? aFormatDef : format;
    }
    
    public boolean isRegistredExt(String aExt) {
        return mExt2Format.containsKey(aExt);
    }
}
