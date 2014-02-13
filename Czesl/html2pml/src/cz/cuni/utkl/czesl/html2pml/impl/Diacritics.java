
package cz.cuni.utkl.czesl.html2pml.impl;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.Logger;

/**
 * Converts text containing diacritics encodings into text containing their 
 * (normalized) unicode representation (possibly using combining characters).
 * 
 * All codes have the form of [ + letter + sequence of diacritic encodings + ],
 * e.g. [L/] encodes Ł, [ao] encodes å, etc.
 * 
 * Typical use:
 * <pre>
 *    new Diacritics(errLog).process(text);
 * </pre>
 * 
 * See 
 * {@link http://www.unicode.org/charts/PDF/U0300.pdf} and
 * {@link http://en.wikipedia.org/wiki/Combining_character}.
 *
 * TODO make configurable, add latex codes
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Diacritics {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Diacritics.class);
    private final org.purl.jh.util.Logger userLogger;
    
    
    /** Direct string to string dictionary */
    private static final Map<String,String> direct = new HashMap<String,String>();
    /** Diacritic shortcut to unicode composing diacritic accent */
    private static final Map<Character,Character> d2d = new HashMap<Character,Character>();

    private static final Pattern diacriticPattern = Pattern.compile("\\[(..+?)\\]");
    
    static {
        direct.put("L/", "Ł");
        direct.put("l/", "ł");
        
        d2d.put('`', '\u0300'); // grave accent
        d2d.put('´', '\u0301'); // acute accent
        d2d.put('\'', '\u0301'); // acute accent
        d2d.put('^', '\u0302'); // circumflex
        d2d.put('~', '\u0303'); // tilde
        d2d.put('-', '\u0304'); // macron, short dash over  ā
        d2d.put('.', '\u0307'); // dot over
        d2d.put('\u02D9', '\u0307'); // dot over
        d2d.put('¨', '\u0308'); // umlaut
        d2d.put('"', '\u0308'); // umlaut
        d2d.put('“', '\u0308'); // umlaut
        d2d.put('°', '\u030A'); // ring above; å
        d2d.put('\u02DA', '\u030A'); // ring above; å
        d2d.put('\u00B0', '\u030A'); // degree sign
        d2d.put('o', '\u030A'); // ring above; å
        d2d.put('ˇ', '\u030C'); // hacek
        d2d.put('ˇ', '\u030C'); // hacek
        d2d.put(':', '\u030B'); // xxx long umlaut

        d2d.put(',', '\u0327'); // cedilla
        d2d.put(';', '\u0328'); // ogonek
        // todo for some reason does not work, handled by direct map
        d2d.put('/', '\u0338'); // SHORT SOLIDUS OVERLAY, Ł  

    }

    public Diacritics() {
        userLogger = log;
    }

    /**
     * Creates the conversion object.
     * 
     * @param aUserLogger logger reporting and/or keeping track of errors and warnings
     */
    public Diacritics(Logger aUserLogger) {
        userLogger = aUserLogger;
    }
    
    public void process(final DelString text) {
        final Matcher m = diacriticPattern.matcher(text);

        while (m.find()) {
            String seq = m.group(1); // char + diacritics
            if (seq.length() < 2) {userLogger.severe("Diacritic sequence %s is too short. Skipping.", seq); continue;}
            String normalized = normalize(seq);

            if (normalized.length() > seq.length()+2) {
                userLogger.warning("Normalized diacritic sequence (%s) is too long, keeping the original (%s)!", normalized, seq);
            }
            else {
                text.replace(m.start(), m.end(), normalized);
                if (normalized.length() > 1) userLogger.warning("Diacritic sequence %s cannot be normalized to a single character (result: %s)!", seq, normalized);
            }
        }
    }

    public String normalize(final String aStr) {
        return Normalizer.normalize(standardizeDiacritics(aStr), Normalizer.Form.NFC);
    }

    /**
     * Converts a single diacritics encoding into its (normalized) unicode representation.
     * See {@link #d2d}.
     *
     * @param aStr sequence to standardize (letter + sequence of diacritics)
     * @return standardized sequence (usually a single character)
     */
    public CharSequence standardizeDiacritics(final String aStr) {
        String dir = direct.get(aStr);
        if (dir != null) return dir;
        
        final StringBuilder sb = new StringBuilder();
        sb.append(aStr.charAt(0));

        for (char c : aStr.substring(1).toCharArray()) {
            Character newDia = d2d.get(c);
            if (newDia == null) {
                userLogger.severe("Unknown diacritic sequence %s (c=%d)", aStr, (int)c);
                return aStr;
            }
            sb.append(newDia);
        }
        
        return sb;
    }
}
