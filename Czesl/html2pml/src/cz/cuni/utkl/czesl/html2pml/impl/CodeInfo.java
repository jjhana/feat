package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.str.Strings;

/**
 * Properties of a code extracted from the text.
 * Code is a directive written by a transcriber to encode special properties of the
 * text, such as corrections, unreadable passages, etc.
 * - see <i>Manuál pro přepis psaných textů</i>.
 *
 * See ParaParser.extractCodes
 *
 *
 * Does not handle bar/img/li as they are left in the text and treated as regular tokens.
 * Does capture tr-odsud-# codes, but note tr-sem-#, as they are handled by the move code
 * todo we should probably use SgmlTag
 * @author Jirka
 */
public class CodeInfo {
    /** does not capture <tr-sem-#>/<img ...>/dt/priv/pr */
    public final static String  cCode = "\\<((gr|ni|in|pd|co|tr\\-odsud(\\-\\d+)?)[^\\>]*)\\>"; 

    public enum Code {
        in,     // correction: insert  ... {some inserted text}<in> ? ...{..}<in>...
        pd,     // illegible due to teacher's modification
        co,     // comment by the transcriber: ...{comment ...}<co>
        trFrom, // correction: source of movement
        trTo,   // correction: target of movement

        ni, // interpretation of an unreadable text .. {abc}<ni>
        gr, // non-latin characters {XXX ...}<gr>
        st, // alternatives written by the author: rok{u|y}<st> TODO
        dt  // pre-written text
        // todo priv // coded for privacy priv/pr
                ;

        public boolean in()     {return this == in;}
        public boolean pd()     {return this == pd;}
        public boolean co()     {return this == co;}
        public boolean trFrom() {return this == trFrom;}
        public boolean trTo()   {return this == trTo;}
        public boolean ni()     {return this == ni;}
        public boolean gr()     {return this == gr;}
        public boolean st()     {return this == st;}
        public boolean dt()     {return this == dt;}


    }

    final String code;
    int from;
    int len; // -1 if from is simply a position within a word
    /** Only used for comments */
    String comment;
    /** Populated later by {@link applyCodes()}. Not used for comments */
    List<Token> affected;

    public CodeInfo(final String aCode) {
        code = aCode;
        type(); // to check validity
    }

    private boolean startsWith(String aStr) {
        return code.equals(aStr) || code.startsWith(aStr + " ");
    }


    public Code type() {
        if (startsWith("in")) {
            return Code.in;
        }
        else if (startsWith("pd")) {
            return Code.pd;
        }
        else if (startsWith("co")) {
            return Code.co;
        }
        else if (startsWith("dt")) {
            return Code.dt;
        }
        else if (code.startsWith("tr-odsud")) {  // todo exact or followed by a number
            return Code.trFrom;
        }
        else if (code.startsWith("tr-sem")) {   // todo exact or followed by a number (the number should be a flag)
            return Code.trTo;
        }
        else if (startsWith("ni")) {
            return Code.ni;
        }
        else if (startsWith("gr")) {
            return Code.gr;
        }
        else if (startsWith("st")) {
            return Code.st;
        }
        // bar, img, li, .
        throw new RuntimeException("Unknown type '" + code + "'");
    }

    public List<String> flags() {
        List<String> tokens = Strings.splitL(code);
        
        return tokens.subList(1, tokens.size());
    }
    
    
    private final Pattern movePattern = Pattern.compile("tr\\-odsud(\\-\\d+)?");

    
    
    public boolean isMove() {
        return movePattern.matcher(code).matches();
    }

    /**
     *
     * @param aFrom starting index relative to the start of the paragraph
     * @param aTo ending index (exclusive) relative to the start of the paragraph
     * @return
     */
    public boolean isWithin(int aFrom, int aTo) {
        //System.out.printf("   within (%s %d:%d) x %d:%d\n", code, from, from + len, aFrom, aTo);
        return aFrom <= from && from + len < aTo;
    }

    @Override
    public String toString() {
        final String str  = String.format("%s (%d) affected:(%s)", code, from, affected);
        if (type().co()) {
            return str + " : " + comment;
        }
        else {
            return str;
        }
    }

    /**
     *
     * @param aText
     * @return
     */
    public CharSequence toString(CharSequence aText) {
        final int tmpLen = len == -1 ? 1 : len;
        if (type().co()) {
            return "Comment: " + aText.subSequence(from, from + tmpLen)  + " : " + comment;
        } else {
            return code + ": " + aText.subSequence(from, from + tmpLen);
        }
    }
}
