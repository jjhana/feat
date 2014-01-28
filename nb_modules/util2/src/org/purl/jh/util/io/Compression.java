package org.purl.jh.util.io;

/**
 *
 * @author Jirka
 */
public enum Compression {
    none, gz, zip;
    public boolean none() {return this == none;}
    public boolean gz()   {return this == gz;}
    public boolean zip()  {return this == zip;}
    
    public static boolean isRegisteredId(String aId) {
        return aId.equals("gzip") || aId.equals("gz") || aId.equals("zip");
    }

    /**
     * @return compression if the id is registered, or null othewise
     */
    public static Compression getById(String aId) {
        return getById(aId, null);
    }

    /**
     * @return compression if the id is registered, or aCompDef othewise
     */
    public static Compression getById(String aId, Compression aCompDef) {
        if (aId.equals("gz") || aId.equals("gzip"))
            return gz;
        else if (aId.equals("zip")) 
            return zip;
        else
            return aCompDef;
    }

    /**
     * Currently equivalent to getById.
     * 
     * @return compression if the extension is registered, or aCompDef othewise
     */
    public static Compression getByExt(String aExt, Compression aCompDef) {
        return getById(aExt, aCompDef);
    }
    
    /**
     * Currently equivalent to getById.
     * 
     * @return compression if the extension is registered, or null othewise
     */
    public static Compression getByExt(String aExt) {
        return getById(aExt);
    }

}
