package org.purl.jh.util.io.translit;

/**
 *
 * @author Jiri
 */
public abstract class AbstractTranslit extends Translit {
    
    public String transliterate(String aStr) {
        if (aStr == null) return null;
        int len = aStr.length();
        StringBuilder buf = new StringBuilder(2*len);

        for (int i = 0; i < len; i++) 
            buf.append( transliterate(aStr.charAt(i)) );

        return buf.toString();
    }

    public String transliterate(int aC) {
        if (Character.isUpperCase(aC)) {
            String tmp = transliterateTbl(Character.toLowerCase(aC));
            if (tmp.length() > 1)
                return Character.toUpperCase(tmp.charAt(0)) + tmp.substring(1);
            else 
                return Character.toUpperCase(tmp.charAt(0)) + "";
        }
        else
            return transliterateTbl(aC);
    }

    public abstract String transliterateTbl(int aC);
    
}
