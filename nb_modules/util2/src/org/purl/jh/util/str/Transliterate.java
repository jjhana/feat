package org.purl.jh.util.str;

/**
 *
 * @author  Jiri
 * @TODO make general
 */
public class Transliterate {
    
    /** Creates a new instance of Transliterate */
    public Transliterate() {
    }

// -----------------------------------------------------------------------------    
// Transliteration
// -----------------------------------------------------------------------------    

    // @todo bug - if all is upper case, digraphs have the second char in lower case
    public static String transliterate(String aStr) {
        if (aStr == null) return null;
        StringBuilder buf = new StringBuilder(2*aStr.length());

        for (int i = 0; i < aStr.length(); i++) 
            buf.append( transliterate(aStr.charAt(i)) );

        return buf.toString();
        
    }

    public static String transliterate(char aC) {
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

    public static String transliterateTbl(char aC) {
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

    public static String czTranscriptionTbl(char aC) {
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
    
    
    public static String fromUppsala1(String aString) {
        int len = aString.length();
        StringBuilder buf = new StringBuilder(len);
        
        for (int i = 0; i < len; i++) {
            char c2 = (i+1 < len) ? aString.charAt(i+1) : 0;
            int translatedChar = fromUppsalaChar1(aString.charAt(i), c2);
            
            if (translatedChar < 0) {
                translatedChar = -translatedChar;
                i++;
            }
            buf.append((char)translatedChar);
        }
        return buf.toString();
    }
    
    public static int fromUppsalaChar1(char c, char c2) {
        switch (c) {
            case 'a': return '\u0430'; 
            case 'b': return '\u0431'; 
            case 'c': return (c2 == 'h') ? -'\u0447' : '\u0446'; 
            case 'd': return '\u0434'; 
            case 'e': return (c2 == 'h' ) ? -'\u044D' : '\u0435';
            case 'f': return '\u0444'; 
            case 'g': return '\u0433'; 
            case 'i': return '\u0438'; 
            case 'j': 
            	switch (c2) {
                    case 'u': return -'\u044E';
                    case 'a': return -'\u044F';
                    default:  return '\u0439'; 
                }
            case 'k': return '\u043A'; 
            case 'l': return '\u043B'; 
            case 'm': return '\u043C'; 
            case 'n': return '\u043D'; 
            case 'o': return (c2 == 'h' ) ? -'\u0451' : '\u043E'; 
            case 'p': return '\u043F'; 
            case 'q': return (c2 == 'h') ? -'\u044C' : '\u044A';
            case 'r': return '\u0440'; 
            case 's': return (c2 == 'h') ? -'\u0448' : '\u0441'; 
            case 't': return '\u0442'; 
            case 'u': return '\u0443'; 
            case 'v': return '\u0432'; 
            case 'w': return '\u0449';
            case 'x': return '\u0445'; 
            case 'y': return '\u044B'; 
            case 'z': return (c2 == 'h' ) ? -'\u0436' : '\u0437'; 
            default : return c;
        }        
    }
    
    /**
     * Converts to unicode from Uppsala trasliteration.
     * The Uppsala corpus files must be read using the 850 codepage (DOS Latin-1).
     *
     * @param @aString a string to convert
     * @return the converted string
     */
    public static String fromUppsala(String aString) {
        int len = aString.length();
        StringBuilder buf = new StringBuilder(len);
        
        for (int i = 0; i < len; i++) 
            buf.append(fromUppsala(aString.charAt(i)));
        
        return buf.toString();
    }
    
    /**
     * Converts a characters from Uppsala trasliteration to unicode.
     */
    public static char fromUppsala(char aC) {
        if (Character.isUpperCase(aC)) 
            return Character.toUpperCase( fromUppsalaCharTbl(Character.toLowerCase(aC)) );
        else
            return fromUppsalaCharTbl(aC);
    }
    
    /**
     * Converts a lower-case characters from Uppsala trasliteration to unicode.
     */
    static char fromUppsalaCharTbl(char aC) {
        switch (aC) {
            case 'a': return '\u0430'; 
            case 'b': return '\u0431'; 
            case 'c': return '\u0446'; 
            case 'd': return '\u0434'; 
            case 'e': return '\u0435';
            case 'f': return '\u0444'; 
            case 'g': return '\u0433'; 
            case 'h': return '\u0436'; // zh
            case 'i': return '\u0438'; 
            case 'j': return '\u0439';
            case 'k': return '\u043A'; 
            case 'l': return '\u043B'; 
            case 'm': return '\u043C'; 
            case 'n': return '\u043D'; 
            case 'o': return '\u043E'; 
            case 'p': return '\u043F'; 
            case 'q': return '\u0449'; //shch
            case 'r': return '\u0440'; 
            case 's': return '\u0441'; 
            case 't': return '\u0442'; 
            case 'u': return '\u0443'; 
            case 'v': return '\u0432'; 
            case 'w': return '\u0448'; // sh
            case 'x': return '\u0445'; 
            case 'y': return '\u044B'; 
            case 'z': return '\u0437'; 
            case '~':      return '\u044E'; // ju  @todo JU
            case '\'':     return '\u044C'; // soft sign @todo SOFT SIGN
            case '\u00E4': return '\u044F'; //  \"{a}->ja
            case '\u00E5': return '\u0447'; //  \r{a} ->ch
            case '\u00F6': return '\u044D'; //  \"{o}->3
            case '„': return '\u044F'; //  ja
            case '†': return '\u044D'; //  3
            default : return aC;
        }        
    }
    
}
