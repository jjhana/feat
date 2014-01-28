package org.purl.jh.util.io;


/**
 * Immutable class.
 * Serves as a registry as well.
 * @author Jiri
 * @todo move here things from morph.io related to encoding
 */
public class Encoding extends Preformat<Encoding> implements Comparable<Encoding> {
    public final static Encoding cAscii     = new Encoding("ASCII");
    public final static Encoding cUtf8      = new Encoding("UTF8");
    public final static Encoding cUtf16     = new Encoding("UTF-16");
    public final static Encoding cIsoLatin1 = new Encoding("ISO8859_1","iso latin1");
    public final static Encoding cIsoLatin2 = new Encoding("ISO8859_2","iso latin2");
    public final static Encoding cWinLatin2 = new Encoding("Cp1250", "win latin2");
    public final static Encoding cIsoCyr    = new Encoding("ISO8859_5", "iso cyrrilics");
    public final static Encoding cWinCyr    = new Encoding("Cp1251", "win Cyrillics");

    public final static Encoding[] cUsuEncodings;
    public final static String[] cUsuEncodingsStr;

    static {
        cUsuEncodings = new Encoding[] {
            cAscii, cUtf8, cUtf16,
            cIsoLatin1, cIsoLatin2, cWinLatin2, cIsoCyr, cWinCyr};

        cUsuEncodingsStr = new String[cUsuEncodings.length];
        for (int i = 0; i < cUsuEncodings.length; i++) {
            cUsuEncodingsStr[i] = cUsuEncodings[i].getEnc();
        }
    }

// -----------------------------------------------------------------------------

    /**
     * Translates character encoding nicknames to the real encoding names.
     * Case of the nickname is irrelevant. If no translation is found
     * for the specified encoding abbreviation, it is considered to be
     * the real name and returned.
     *
     * <table>
     * <tr><td>ISO8859_1 <td>latin1
     * <tr><td>ISO8859_2 <td>isoCE, latin2
     * <tr><td>Cp1250    <td>winCE
     * <tr><td>ISO8859_5 <td>isoCE, latin5
     * <tr><td>Cp1252    <td>winCyr
     * <tr><td>tr        <td>t (marking transliteration)
     * </table>
     *
     * @todo transliteration schemas (t:...)
     */
    public static Encoding fromString(String aEncName) {
        aEncName = aEncName.trim();
        String lc = aEncName.toLowerCase();
        Encoding enc = mEncodingAbbrs.get(lc);
        return (enc == null) ? new Encoding(aEncName) : enc;
    }

    public static void registerEncAbbr(String aAbbr, Encoding aEnc) {
        mEncodingAbbrs.put(aAbbr, aEnc);
    }

    private static java.util.Map<String,Encoding> mEncodingAbbrs;

    static {
        mEncodingAbbrs = new java.util.HashMap<String,Encoding>();

        mEncodingAbbrs.put("latin1", cIsoLatin1);
        mEncodingAbbrs.put("isoce",  cIsoLatin2);
        mEncodingAbbrs.put("latin2", cIsoLatin2);
        mEncodingAbbrs.put("wince",  cWinLatin2);
        mEncodingAbbrs.put("isocyr", cIsoCyr);
        mEncodingAbbrs.put("latin5", cIsoCyr);
        mEncodingAbbrs.put("wincyr", cWinCyr);
    }


// -----------------------------------------------------------------------------

    /** Creates a new instance of Encoding */
    public Encoding(String aId) {
        this(aId, aId);
    }

    public Encoding(String aId, String aDesc) {
        super(aId.toLowerCase(), aDesc);
    }


    @Deprecated
    public String getEnc() {return mId;}
}
