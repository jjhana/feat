package org.purl.jh.util.io.translit;

/**
 *
 * @author Jiri
 */
public class OurRus extends AbstractTranslit {
    
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

    public String transliterateTbl(int aC) {
        switch (aC) {
            case '\u0430': return "a";
            case '\u0431': return "b";
            case '\u0432': return "v";
            case '\u0433': return "g";
            case '\u0434': return "d";
            case '\u0435': return "e";
            case '\u0436': return "zh";
            case '\u0437': return "z";
            case '\u0438': return "i";
            case '\u0439': return "j";
            case '\u043A': return "k";
            case '\u043B': return "l";
            case '\u043C': return "m";
            case '\u043D': return "n";
            case '\u043E': return "o";
            case '\u043F': return "p";
            case '\u0440': return "r";
            case '\u0441': return "s";
            case '\u0442': return "t";
            case '\u0443': return "u";
            case '\u0444': return "f";
            case '\u0445': return "x";
            case '\u0446': return "c";
            case '\u0447': return "ch";
            case '\u0448': return "sh";
            case '\u0449': return "shch";
            case '\u044A': return "\"";
            case '\u044B': return "y";
            case '\u044C': return "'";
            case '\u044D': return "3";
            case '\u044E': return "ju";
            case '\u044F': return "ja";
            case '\u0451': return "jo";

            default: return String.valueOf(aC);
        }
    }
    
}
