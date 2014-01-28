package org.purl.jh.util.io.translit;

/**
 * @todo synonyms (but currently can be registered n-times under diff. names)
 * @todo list of usual transliterations?
 * @author Jiri
 */
public class TranslitRegister {
    public final static Translit cOurRus     = new OurRus();
        
    /**
     * @param aName name of the transliteration (case insensitive)
     */
    public static void registerTranslit(String aName, Translit aTransliterator) {
        aTransliterator.setName(aName);
        mRegister.put(aName.toLowerCase(), aTransliterator);
    }

    
    /**
     * @param aName name of the transliteration (case insensitive)
     */
    public static Translit getTranslit(String aName) {
        aName = aName.trim();
        String lc = aName.toLowerCase();
        return mRegister.get(lc);
    }
    
// -----------------------------------------------------------------------------
// Implementation
// -----------------------------------------------------------------------------
    private static java.util.Map<String,Translit> mRegister;

    static {
//        cUsuTranslits = new Translit[] {cOurRus};
//
//        cUsuTranslitStrs = new String[cUsuTranslits.length];
//        for (int i = 0; i < cUsuTranslits.length; i++) {
//            cUsuTranslitStrs[i] = cUsuTranslits[i].getName();
//        }
        mRegister = new java.util.HashMap<String,Translit>();
        registerTranslit("tRus", cOurRus);
    }
}
