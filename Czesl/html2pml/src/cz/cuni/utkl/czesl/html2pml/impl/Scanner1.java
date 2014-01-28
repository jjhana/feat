package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.sgml.Sgml;
import org.purl.jh.util.sgml.SgmlTag;
import org.purl.jh.util.str.Search;
import org.purl.jh.util.str.Strings;

/**
 * Segments text into tokens, taking into account braced tokens, codes, etc.
 * Tokens:
 * <ul>
 * <li>individual words (can contain: optional spaces, variant strings, parentheses, word-internal codes, word internal punctuation)
 * <li>spaces
 * <li>codes (codes marking words are separate tokens, e.g. XXX<gr> are two tokens),
 *     includes inserted period (<.>)
 * <li>braced text (is kept together as a single token; token excludes braces)
 * </ul>
 * 
 * Currently, intra-word codes are not supported, abc{XXX}<gr>def is scanned as (abc{XXX}, <gr>, def).
 * 
 * Notes:
 * <ul>
 * <li>aaa(a) gives aaa(a), but (aaa) gives ( aaa )
 * <li>aaa<bar>aaa gives aaa<bar>aaa
 * </ul>
 * 
 * See unit tests.
 * @todo 'a' vs don't
 * @todo {telo cvicnu|telocvicnu}
 * @todo dates take precedence over decimal points 12.5. is tokenized as 12/./5/. not as 12.5/.
 * @todo check if there are any unknown tags (should catch }space<dt> 
 * @todo make more effective (reuse matchers, etc)
 * @todo 'a' vs don't
 * @todo {telo cvicnu|telocvicnu}
//        "(0?[1-9]|[12][0-9]|3[01])\\.(0?[1-9]|1[012])",            
 * 
 * @author Jirka
 */
public class Scanner1 {
// =============================================================================
// Patterns determining individual tokens
// =============================================================================

    
    // puctuation which *can* be part of a word 
    private static final String cWordPunct = "-‐‑‒–—―";

    private static final String cNrsTail = "(?=(\\s|\\p{Punct}|$))";

    // date patterns - catch them as individual items so that they are not processed by number pattersn. 
    private static final List<String> cDates = Arrays.asList(
        "(0?[1-9]|[12][0-9]|3[01])(?=\\.(0?[1-9]|1[012])\\.)",   // day in a date (followed by a dot + month)
        "(0?[1-9]|1[012])(?=\\.(19\\d\\d|2\\d\\d\\d|[01]\\d)[\\s\\{Punct}])",   // month in a date (followed by a year and then some separator). Month without a date is parsed as Nr + punct
        "(0?[1-9]|1[012])(?=\\.(19\\d\\d|2\\d\\d\\d|[01]\\d)\\Z)"               // dtto but the year is at the end of input
        //Note: look behind (to check if the month is preceded by a day) cannot be used as the already scanned text is not visible. 
    );
    
    // numbers
    private static final List<String> cNrs = Arrays.asList(
        "[+-]?\\d{1,3}(,\\d{3})*(\\.\\d+)?" + cNrsTail, 
        "[+-]?\\d{1,3}(\\.\\d{3})*(\\,\\d+)?" + cNrsTail, 
        "[+-]?\\d{1,3}([\\s]\\d{3})*(\\.\\d+)?" + cNrsTail, 
        "[+-]?\\d+([,.]\\d+)?" + cNrsTail,
        "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" + cNrsTail,  // ip address, just for fun
        "n([\\Q+-*/:=\\E]n)+".replace("n", "(([+-]?\\d+([a-z]|[,.]\\d+)?)|[a-z])")    
    );
    
    // puctuation which is separated from a word (without |, (), {}, <>) (does not include alternatives support)
    private static final String cPunct0 = "(" +
        "[?!]+|" +
        "\\.+|\\*+|\\++|\\-+|" +
        "\\Q<bar>\\E|" +
        "\\Q<.>\\E|" +
        "[\\Q!\"#$%&*+,-./:;=?@[]\\^_`~…“”„‟" + cWordPunct + "\\E]|" +
        "\u9128\u9128|\u23AC\u23AC|\u2308\u2308|\u2309\u2309|\u2329\u2329|\u232A\u232A)";

    // puctuation, possibly alternatives of punctuation
    //private static final String cPunct = String.format("(%s|\\{%s(\\|%s)+\\})", cPunct0, cPunct0, cPunct0);
    private static final String cPunct = String.format("(%s|\\{%s(\\|%s)+\\})", cPunct0, cPunct0, cPunct0);
    
    
    // when unknown token is encountered declare it a single token
    private static final String unknown = "."; 

// =============================================================================


    
    public enum TokenType {
        /** Word character (incl. some punctuation, e.g. a dash) */
        word, 
        /** Non word-punctuation (e.g. comma, period, parenthesis) */
        punct,
        /** White-space (incl. non-breaking spaces) */
        space,
        /** &lt;, i.e. opening paren of a code */
        code,
        /** braced text marked by a code (dt,priv)*/
        braced;
    }

    public static class Token {
        public boolean isWord()   {return type == TokenType.word;}
        public boolean isPunct()  {return type == TokenType.punct;}
        public boolean isSpace()  {return type == TokenType.space;}
        public boolean isCode()   {return type == TokenType.code;}
        public boolean isBraced() {return type == TokenType.braced;}
        
        
        /** Clean text of the token. */
        String text;

        /** Starting position in the scanned text */
        int from;

        /** Length of the token.  */
        int len;

        /** Type of the token. */
        TokenType type;
        
        /** Codes valid only if {@link #type} is {@link TokenType#code} */
        SgmlTag code;

        /** Token-Codes assigned to the token, valid only if {@link #type} is {@link TokenType#braced} */
        List<SgmlTag> codes;

        int totLen;
        
        private void setText(String aStr, int aFrom, int aToExcl) {
            text      = aStr.substring(aFrom, aToExcl);
            from      = aFrom;
            len       = aToExcl - aFrom;
        }

        @Override
        public String toString() {
            return "Token{" + "text=" + text + ", from=" + from + ", len=" + len + ", totlen=" + totLen + ", type=" + type + ", code=" + code + ", codes=" + codes + '}';
        }
        
    }

    
    
    
    

    /** Deletable String to scan */
    private final String text;

    /** Length of the scanned text, for efficiency */
    private final int n;

    /** Current position of the scanner */
    int pos;

    public Scanner1(String aText) {
        //System.out.println("Skanning " + aText.toString());
        text = aText;
        n = aText.length();
        pos = 0;
    }

    public Token next() {
        Token tok = nextI();
        //System.out.println("token1: " + tok);
        return tok;
    }

    public Token nextI() {
        if (pos >= n) {
            return null;
        }

        int start = pos;

        // todo {a|b}cde or {a}bc should be a word
        // braced token together with marking code(s) {...}<code>
        if (curChar() == '{')  { // braced: dt/pr/priv   // todo what about {...|...}
            final int closingBrace = Search.findMatchingClosingBrace(text, pos);
            //System.out.printf("Match { pos=%d, closing=%d; '%s'\n", pos, closingBrace, Strings.trim(text.substring(closingBrace),3));
            if (closingBrace == -1) throw Err.fErr("No matching brace found: pos=%d - %s", pos, Strings.trim(text.substring(pos),10));
            
            // must be followed by a code TODO why???
            //final int barIdx = text.substring(pos,closingBrace).indexOf('|');  // todo must be mine, not embeded's {a{b|c}d}<dt> is okay
            if (matches(closingBrace+1, "\\<")) { // todo sgml codes, todo check for errors (should not contain non-embeded |)
                //System.out.println("Match {..}<");
                pos = closingBrace+1;

                final Token token = new Token();
                token.type = TokenType.braced;
                token.setText(text, start+1, closingBrace);
                token.codes = readCodes();
                token.totLen = pos-start;
                return token;
            }
            else {
                //System.out.println("Nomatch < :" + text.substring(closingBrace+1, closingBrace+3) + Strings.trim(text.substring(closingBrace+1),10));  
            }
        }
        // handle codes as a single token, e.g. <img :)>  (there are no <<,>> because they are still quoted)
        if (curChar() == '<') {
            int end = text.indexOf('>', pos);
            if (end == -1) throw Err.fErr("No matching '>' found: pos=%d - %s", pos, Strings.trim(text.substring(pos),10));
            pos = end+1;

            final Token token = new Token();
            token.type      = TokenType.code;
            token.from = start;
            token.len  = end+2-start;
            token.code = new SgmlTag(text.substring(start, end+1));
            token.totLen = pos-start;

            return token;
        }
        else { // word/punct/space
            // return the first token caught
            Token tok = null;
            tok = readToken(TokenType.word, cDates);
            if (tok != null) return tok;

            tok = readToken(TokenType.word, cNrs);
            if (tok != null) return tok;
           
            tok = readWordToken();
            if (tok != null) return tok;

            tok = readToken(TokenType.punct, cPunct);
            if (tok != null) return tok;

            tok = readToken(TokenType.space, Strings.cWhitespacePattern.toString()); 
            if (tok != null) return tok;


            tok = readToken(TokenType.word, unknown); 
            
            return tok;
        }
    }
    


    // todo find the longest match
    private Token readToken(TokenType aType, String ... aPattern) {
        return readToken(aType, Arrays.asList(aPattern));
    }
    
    /** Returns the longest token matching any of the specified patterns */
    private Token readToken(TokenType aType, List<String> aPatterns) {
        final int splitIdx = findTokenEnd(aPatterns);
        if (splitIdx == -1) return null;
        
        Token token = new Token();
        token.type = aType;
        token.setText(text, pos, splitIdx);
        token.totLen = splitIdx-pos;

        pos = splitIdx;;
        return token;
    }

    /** Finds the end of the longest token matching any of the specified patterns */
    private int findTokenEnd(List<String> aPattern) {
        int max = -1;
        
        for (String patStr : aPattern) {
            final Pattern pat = Pattern.compile(patStr);
            Matcher m = pat.matcher(text.substring(pos));
            if (m.lookingAt() && m.end() > max) max = m.end();
        }

        return max == -1 ? -1 : pos + max;
    }
    
//    private String readStr(String aPattern) {
//        final Pattern pat = Pattern.compile("^" + aPattern);
//        Matcher m = pat.matcher(text.substring(pos));
//        return (m.find()) ? m.group() : "";
//    }
    
    
    private SgmlTag readCode() {
        SgmlTag tag = Sgml.getTagAt(text, pos); 
        pos+= tag.toString().length();
        return tag;
    }
    
    private List<SgmlTag> readCodes() {
        final List<SgmlTag> tags = XCols.newArrayList();
        
        for(;;) {
            if (pos >= text.length() || text.charAt(pos) != '<') return tags;
            SgmlTag tag = readCode();
            //System.out.printf("pos = %d, tag = %s\n", pos, tag.toString());
            tags.add( tag );
        }
    }
   
        
    
    private char curChar() {
        return text.charAt(pos);
    }

    private Token readWordToken() {
        int splitIdx = findWordSplit();
        if (splitIdx == pos) return null;
        
        Token token = new Token();
        token.type = TokenType.word;
        token.setText(text, pos, splitIdx);
        token.totLen = splitIdx-pos;
    
        pos = splitIdx;
        return token;
    }

    
    // 
    int findWordSplit() {
        int space = spaceSplit();
        int pct = punctSplit();
        int splitIdx = Util.min(space, pct,  n);
        //System.out.printf("space = %d, pct=%d, splitIdx = %d\n", space, pct, splitIdx);
        int paren = parenSplit(splitIdx);
        splitIdx = Util.min(splitIdx,  paren);
        //System.out.printf("paren = %d, splitIdx = %d\n", paren, splitIdx);
    
        if (splitIdx > pos && text.charAt(pos) == '(' && text.charAt(splitIdx-1) == ')') return pos; 
        
        return splitIdx;

    }
    
    // todo reuse patterns
    int spaceSplit() {
        Matcher m = Strings.cWhitespacePattern.matcher(text);
        int next = pos;
        for (;;) {
            //System.out.printf("space search: next=%d, pref=%s\n", next, text.substring(next));
            if (!m.find(next)) return Integer.MAX_VALUE;
            next = m.start();
            if (! matches(next-1, "\\{\\s\\}")) return next;
            next++;
        }
    }

    int punctSplit() {
        Matcher m = Pattern.compile(cPunct).matcher(text);
        int next = pos;
        for (;;) {
            if (!m.find(next)) return Integer.MAX_VALUE;
            next = m.start();

            if (!matches(next, "[\\Q" + cWordPunct + "\\E]")) return next;
            if (! matches(next-1, "\\p{L}|..\\p{L}")) return next;                    // a- or -a
            
            next++;
        }
    }

    // paren works like a regular punctuation, except (a)aa, or a(a), or aa(a); but not (aa)
    int parenSplit(int aMaxTokenEnd) {
        int unbalanced = Util.findUnbalanced(text.substring(0,aMaxTokenEnd), pos, '(', ')');   // but the balancing must be within this token
        //System.out.printf("unbalanced ub=%d pos=%d %s\n", unbalanced, pos, text.substring(0,aMaxTokenEnd));
        return (unbalanced == -1) ? Integer.MAX_VALUE : unbalanced;
    }
    
    boolean matches(int aPos, String aPattern) {
        return 0 <= aPos && aPos < n && Pattern.compile(aPattern).matcher(text).region(aPos, n).lookingAt();
    }
    
}
