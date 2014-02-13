package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.Para.SplitInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.Pair;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.str.Search;
import org.purl.jh.util.str.Strings;


/**
 *
 * Code attachment ( | means or, + means one or more):
 * <pre>
 * T1 | T1 C1
 * T1 = T2 W | {W+}
 * W = word | word{...}<co>
     * C1 = <in> | <tr-odsud> | <tr-odsud-#>
 * C2 = <pd> | <ni>
 * </pre>
 * - code C cannot be repeated,
 * - ... comment cannot be empty,
 *
 * @todo BitSet substring
 * @author Jirka
 */
public class ParaParser {
    private final static Logger log = Logger.getLogger(ParaParser.class.getName());
    private final CountingLogger userLogger;
    
    public static final char cSpecSpace = '\u23D8';

    // input
    /** beginning of {@link text} within the text of the whole document (text's offset)*/
    private final int offset;
    /** Text of this paragraph */
    private DelString  text;

    // --- result ---
    private final Para para;

    // Processing structures // needed to apply format on tokens???

    /** Just for easier access; copy of para.tokens */
    private final List<Token> tokens;
    /** Just for easier access; copy of para.codes*/
    private final List<CodeInfo> codes;

    private final List<Pair<Integer,Integer>> tmpSplitInfos = new ArrayList<Pair<Integer,Integer>>();
    private final List<TmpChangeInfo> tmpChangeInfos = new ArrayList<TmpChangeInfo>();

    static class TmpChangeInfo {
        String origText;
        int from;
        int to;
        boolean multiWord;
        int replaceFrom;
    }

    private boolean x = false;

    /**
     * Create a parser of a paragraph's text
     * 
     * @param aText text of this paragraph
     * @param aFrom beginning of aText within the text of the whole document (aText's offset)
     * @param aTo   end of aText within the text of the whole document
     */
    public ParaParser(Doc aDoc, String aText, int aFrom, int aTo, CountingLogger aLogger) {
        para = new Para(aDoc, aFrom, aTo);

        tokens = para.tokens;
        codes = para.codes;

        text = new DelString(aText);
        offset = aFrom;
        
        userLogger = aLogger;
    }

    /** For unit tests */
    protected DelString getText() {
        return text;
    }
    
    
    
    
    public Para parse() throws BadLocationException {
        removeQuotes();
            basicValidations(text.clean());
            simplifyingConversion();

            //userLogger.info System.out.println("ParaParser.parse()");
            log(text);
            extractCodes();
            //removeSpecialSpaces();  // not needed, handled by parser, remove spaces in things that should not be tokenized (e.g. {<space>}, text within braced)
            new Diacritics(userLogger).process(text);  // in case of errors continues, to report more errors
            extractArrows();
        
        log(text);

        extractsSplits();

        log(text);

        tokenize(); 

        processSplits();
        processArrows();

        para.applyCodes();

        return para;
    }

    
    protected void basicValidations(String clean) {
        
        // --- for now - throw away anything suspicious --- // todo make an option to consider a warning to be an error 
        //if (clean.startsWith("\\|")) suspicious("para initial |");
        check(clean, "^\\|", "para initial |");
        check(clean,"^\\s*\\{[^\\}]*\\}\\<co\\>\\s*$","comment only para");
        check(clean,"\\[^\\[\\]]{4,}\\]", "diacritic sequence too long");
        check(clean,"\\{\\}", "empty braces");
        check(clean,"\\{\\s\\}\\s", "word final optional space");
        check(clean,"\\s\\{(?![^\\}]+(\\||\\-\\>))[^\\}]+\\}\\s", "braces without use");
        check(clean,"\\}\\s+\\<(dt|pr|priv|co)\\>", " space before code "); // todo ignore
        check(clean,"\\<pd\\>", "<pd> code is not supported yet"); // todo ignore
        
        
        // todo {||a} -> ??
        check(clean,"\\{\\{\\{", "tripple braces");  // todo that should be fine
        checkW(clean,"\\{[^\\}]{0,4}\\}\\<dt\\>", "dt text too short");

        checkW(clean,"\\- \\>", "Space in change arrow?");

        // todo not handled yet
        check(clean,"\\<gr\\>", "gr code (not handled yet)");
        check(clean,"\\<st\\>", "st code (not handled yet)");
        //check(clean,"(0?[1-9]|[12][0-9]|3[01])\\.(0?[1-9]|1[012])\\.", "possible date (not handled yet)");
        check(clean,"\\{[^\\{\\}]*\\|[^\\{\\}]*\\}\\<", "code attached to variant braces (not handled yet)");
        check(clean,"\\{[^\\{\\}]*\\-\\>[^\\{\\}]*\\}\\<", "code attached to change braces (not handled yet)");
        check(clean,"\\{(a+\\sa+\\|a*)\\}".replace("a", "[^\\}]"), "variant braces with space  (not handled yet)");
        check(clean,"\\{(a*\\|a+\\sa+)\\}".replace("a", "[^\\}]"), "variant braces with space  (not handled yet)");
        
        
        checkW(clean,"^[\\*\\-]", "Looks like a bullet, but is not coded as one");
        //todo
        // {velke male}in 
        
        int idx = Util.findUnbalanced(clean, 0, '{', '}');
        if (idx != -1) suspicious(idx, "unbalanced braces:\n %s", clean);
        // todo no embeded codes, braces in comments, prv
        // todo ...
        idx = Util.findUnbalanced(clean, 0, '[', ']');
        if (idx != -1) suspicious(idx, "unbalanced brackets (should be used for diacritics only)");
    }
    
    private void check(String clean, String aPattern, String aMsg, Object ... aParams) {
        Matcher m = Pattern.compile(aPattern).matcher(clean);
        if (m.find()) {
            suspicious(m.start(), String.format(aMsg, aParams));
        }
    }
    
    private void checkW(String clean, String aPattern, String aMsg, Object ... aParams) {
        Matcher m = Pattern.compile(aPattern).matcher(clean);
        if (m.find()) {
            assertW(false, m.start(), String.format(aMsg, aParams));
        }
    }
    
    
    /** 
     * Replacing strings must have the same length
     * 
     * @param aStr
     * @return 
     */
    private final static Pattern coAsPrivPatternStart = Pattern.compile("(^\\{[^\\{\\}]*?\\})<co>");
    private final static Pattern coAsPrivPattern      = Pattern.compile("(\\s\\{[^\\{\\}]*?\\})<co>");
    private final static Pattern ringAsDotPattern     = Pattern.compile("<°>");
    
    protected void simplifyingConversion() {
        // legacy co code used as newer priv code, replace with <pr> for easier processing
        // used as convert space{..}<co> to space{..}<pr> 
        replace(coAsPrivPatternStart, "$1<pr>", "Space separated comment considered to be a <priv> code. Please check.");
        replace(coAsPrivPattern, "$1<pr>",  "Space separated comment considered to be a <priv> code. Please check.");      
        replace(ringAsDotPattern, "<.>", "<°> corrected to <.>, please do not use.");         // some transcribers use <°> instead of the correct <.>
        // todo ignore spaces before codes
    }
    
    protected boolean replace(final Pattern aPattern, final String aReplacement, final String aWarning) {
        if (aPattern.matcher(text).find()) {
            final int origLen = text.length();
            text.replaceAll(aPattern, aReplacement);
            
            if (aWarning != null) userLogger.warning(aWarning);
            assertErr(origLen == text.length(), "Internal error! Please report. The pattern %s is length changing.", aPattern);
            return true;
        }
        return false;
    }
    
    
    // characters used to temporary replace quoted characters, these are extremely unlikely to be used in the text, esp. in pairs
    private final String cXOpenBrace    = "\u9128\u9128";
    private final String cXCloseBrace   = "\u23AC\u23AC";
    private final String cXOpenBracket  = "\u2308\u2308";
    private final String cXCloseBracket = "\u2309\u2309";
    private final String cXOpenAngle    = "\u2329\u2329";
    private final String cXCloseAngle   = "\u232A\u232A";

    /**
     * Remove (some) quoted characters, so that they do not show up when I look say
     * for closing brace. They will be returned back later by {@link #returnQuotes(String)}.
     * The double-bar stays as it is marks split corrections.
     */
    private void removeQuotes() {
        replace("{{", cXOpenBrace);
        replace("}}", cXCloseBrace);
        replace("[[", cXOpenBracket);
        replace("]]", cXCloseBracket);
        //replace("||", cXBar);
        replace("<<", cXOpenAngle);
        replace(">>", cXCloseAngle);
    }

    /**
     * Return quoted characters back to their original form.
     */
    private String returnQuotes(String aStr) {
        return aStr
            .replaceAll("\\Q" + cXOpenBrace    + "\\E", "{{")
            .replaceAll("\\Q" + cXOpenBrace    + "\\E", "{{")
            .replaceAll("\\Q" + cXCloseBrace   + "\\E", "}}")
            .replaceAll("\\Q" + cXOpenBracket  + "\\E", "[[")
            .replaceAll("\\Q" + cXCloseBracket + "\\E", "]]")
            //.replaceAll("\\Q" + cXBar          + "\\E", "||")
            .replaceAll("\\Q" + cXOpenAngle    + "\\E", "<<")
            .replaceAll("\\Q" + cXCloseAngle   + "\\E", ">>");
    }

    private void replace(String aOrig, String aNew) {
        text.replaceAll("\\Q" + aOrig + "\\E", aNew);
    }


    private final static Pattern codePattern = Pattern.compile(CodeInfo.cCode);
    private final static Pattern codeTailPattern = Pattern.compile(".*" + CodeInfo.cCode); // todo: possibly add meaningless spaces

    /**
     * Extracts information contained in codes into CodeInfo objects and
     * removes the code from the text.
     */
    private void extractCodes() {
        //System.out.println("--- Extracting codes ---");
        // find, interpret, remove in/pd/ni/dt/.. (can be chained)
        // 1. word{comment}<co>?
        // 2. ...<code><code>  (code preceded by any of 1/3/4)      
        // 3. {...}<code>
        // 4. word<code>

        final Matcher m = codePattern.matcher(text);

        while (m.find()) {
            userLogger.fine("Code: %s : %s", m.group(), m.group(1));

            final CodeInfo codeInfo = new CodeInfo(m.group(1));
            codes.add(codeInfo);

            final int start = m.start();  // index of <
            final int end = m.end();      // index of >
            text.ignore(start, end);    // ignore <..>

            final int prevIdx = start-1;
            final char prevChar = (prevIdx >= 0) ? text.charAt(prevIdx) : 0;
            //String prevText = aText.substring(0, prevIdx);

            if (codeInfo.type().co()) {
                assertErr(prevChar == '}', start, "Comment format: word{..}<co> or {..}<co para/doc>");         // todo validate

                final int openningBraceIdx = Search.findMatchingOpeningBrace(text,prevIdx);
                assertErr(openningBraceIdx != -1, start, "Unballanced braces.");

                // extract and remove the actual comment, remember the position of the commented word
                codeInfo.comment = text.substring(openningBraceIdx+1, prevIdx);
                codeInfo.from = openningBraceIdx-1;   // just some char in the commented word, no need to do the tokenization (ignored later if para/doc comment)
                codeInfo.len = -1;                   //
                text.ignore(openningBraceIdx, start);
            }
            // preceded by another code matching codePattern
            else if ( codeTailPattern.matcher(text.substring(0,prevIdx+1)).matches() ) {
                // use the already extracted info from the preceding code
                final CodeInfo prevCodeInfo = codes.get( codes.size()-2 );
                //System.out.println("Two codes");
                codeInfo.from = prevCodeInfo.from;
                codeInfo.len  = prevCodeInfo.len;
            }
            // braced expression (e.g. {...}<in>
            else if (prevChar == '}') {       
                int openningBraceIdx = Search.findMatchingOpeningBrace(text,prevIdx);
                assertErr(openningBraceIdx != -1, start, "Unballanced braces.");

                codeInfo.from = openningBraceIdx+1;
                codeInfo.len  = prevIdx - openningBraceIdx - 1;
                text.ignore(openningBraceIdx);   // {
                text.ignore(openningBraceIdx + codeInfo.len + 1);
            }
            // single word
            else {
                codeInfo.from = prevIdx;   // just some char in the word, no need to do the tokenization
                codeInfo.len = -1;                   // 
            }
        }
    }

    private void removeSpecialSpaces() {
        //System.out.println("removeSpecialSpaces");
        for (CodeInfo code : codes) {
            if (code.type().dt()) {
                for (int i = code.from; i < code.from+code.len; i++) {
                    if (Strings.isWhitespace(text.charAt(i))) {
                        text.setCharAt(i, '_');     // todo replace with a unique character
                    }
                }
            }
        }
    }


    /**
     * Splits words marked for splitting (a||b) and remembers it.
     */
    private final static Pattern cSplitPattern = Pattern.compile("[^\\s\\xA0](\\|\\|)[^\\s\\xA0]");

    /**
     * First pass of split changes processing.
     *
     * This function extracts information about the change and leaves only the
     * corrected text, replacing the split symbol (||) by spaces.
     * The resulting {@link #tmpSplitInfos} are translated by {@link #processSplits()}
     * into {@link Para#splitInfos} once token objects exit.
     *
     * @todo warning if || surrounded by whitespace
     */
    private void extractsSplits() {
        final Matcher m = cSplitPattern.matcher(text);

        while (m.find()) {
            text.replace(m.start(1), m.start(1)+2, " ");

            int prevChar = text.prevCharIdx(m.start(1));
            int nextChar = text.nextCharIdx(m.end(1)-1);
            //System.out.printf("Split %d:%d %c:%c: \n", prevChar, nextChar, sb.charAt(prevChar-offset), sb.charAt(nextChar-offset));

            tmpSplitInfos.add(new Pair(prevChar, nextChar));
        }
    }


    private final static Pattern cArrowPattern = Strings.wspattern("\\s+\\-\\>\\s+");

    /**
     * First pass of local changes processing.
     * <p>
     * Local changes have the form {... -> ...} (with one or more spaces on each 
     * side of the arrow) and can occur:
     * <ul>
     *   <li>within a word, e.g. w{ro -> or}d or {ow -> wo}rd
     *   <li>over one or more word(s), e.g. {wrod -> word} {word word2 -> word2 word} or {word word2 -> word word2 word3}
     * </ul>
     *
     * This function extracts information about the change and leaves only the
     * resulting text, removing the original text and control sequences.
     * The resulting {@link #tmpChangeInfos} are translated by {@link #processArrows()}
     * into {@link Para#changeInfos} once token objects exit.
     *
     */
    private void extractArrows() {
        final Matcher m = cArrowPattern.matcher(text);

        while (m.find()) {
            int arrowIdx = m.start();
            int openBraceIdx  = Search.findMatchingOpeningBrace(text, arrowIdx);
            int closeBraceIdx = Search.findMatchingClosingBrace(text, arrowIdx);
            assertErr(openBraceIdx  != -1, arrowIdx, "Cannot find the opening brace for ->");
            assertErr(closeBraceIdx != -1, arrowIdx, "Cannot find the closing brace for ->");

            int prevCharIdx = text.prevCharIdx(openBraceIdx);
            int nextCharIdx = text.nextCharIdx(closeBraceIdx);
            boolean insideBef   = (prevCharIdx != -1) && Strings.isWhitespace(text.charAt(prevCharIdx));
            boolean insideAfter = (nextCharIdx != -1) && Strings.isWhitespace(text.charAt(nextCharIdx));


            final TmpChangeInfo info = new TmpChangeInfo();
            info.multiWord = insideBef || insideAfter;
            info.origText = text.substring(openBraceIdx+1, arrowIdx);

//            System.out.println("Arrow " + info.origText);
//            System.out.printf("   %d:%d %c:%c %s:%s\n", prevCharIdx, nextCharIdx, text.charAt(prevCharIdx), text.charAt(nextCharIdx), insideBef, insideAfter);

            info.replaceFrom = openBraceIdx+1;
            info.from = m.end();
            info.to = closeBraceIdx;
            tmpChangeInfos.add(info);

            text.ignore(openBraceIdx, m.end());
            text.ignore(closeBraceIdx);
        }
    }

    // todo maybe we should merge the code in this function and of Scanner
    private void tokenize() { //DelString text, int offset) {
        //final List<Token> tokens = new ArrayList<Token>();
        final Scanner scanner = new Scanner(text);
        //System.out.println("Tokenize: \n" + text.cleanDebug());

        Token prev = null; // to set information about the following spaces
        for (;;) {
            final Scanner.Token scToken = scanner.next();
            if (scToken == null) break;

            if (scToken.isSpace()) {
                if (prev != null) {
                    String spaceStr = scToken.text;
                    prev.spaceAfter = (spaceStr.length() == 1) ? 1 : 2;
                    //boolean endOfSentence = prev.spaceAfter == 2 || ( Arrays.asList(".", "!", "?").contains(prev.finalToken) ;
                    prev = null;
                }
            }
            else {
                if (prev != null) {
                    prev.spaceAfter = 0;  // todo !codes do not mark this
                }
               
                if (scToken.isCode()) {
                    if ("<.>".equals(scToken.code.toString())) {
                        prev = null; // <.> is inserted by the transcriber, so it is not part of w layer; feat should look it up in html when adding b layer
                    }
                    // marking codes (<dt>,<priv>/<pr>; ? <gr>
                    else if (scToken.code.getCore().matches("dt|priv|pr|st")) {
                        assertErr(prev != null, scanner.pos, "The code %s must be attached to a token", scToken.code);
                        prev.len += scToken.len;
                        prev.flags.add(scToken.code);
                    }
                    // token codes <tr-sem-#>, <img>, <bar> (space/punctuation separated)
                    else {
                        final Token token = new Token();

                        token.from      = scToken.from + offset;
                        token.token     = returnQuotes( scToken.code.toString() );
                        token.len       = token.token.length();
                        token.origToken = token.token;  // do we need it?

                        tokens.add(token);
                        prev = token;
                    }
                }
                else {
                    final Token token = new Token();

                    token.from      = scToken.from + offset;
                    token.len       = scToken.len;
                    token.token     = returnQuotes( scToken.text );
                    token.origToken = returnQuotes( scToken.dirtyText );
                    if (scToken.isBraced()) {
                        token.flags.addAll(scToken.codes);
                     }

                    tokens.add(token);

                    prev = token;
                }
            }
        }

    }


    private String replace(String aText, String aOrig, String aNew) {
        return aText.replaceAll("\\Q" + aOrig + "\\E", aNew);
    }



//    protected void assertFatal(boolean aTest, String aFormat, Object ... aParams) {
//        if (aTest) return;
//
//        userLogger.severe("Fatal error: " + aFormat+'\n', aParams);
//        System.exit(-1);
//    }

    
    /**
     * Second pass of split changes processing; creates {@link Para#splitInfos}.
     *
     * Translate positions in {@link #tmpSplitInfos} into tokens and connects 
     * chains of splits (a||b||c is remembered as a||b and b||c; probably not
     * very common, but still possible)
     */
    private void processSplits() {
        final Equalizer<Token> eq = new Equalizer<Token>();

        for (Pair<Integer,Integer> tt : tmpSplitInfos) {
            Token first  = para.findToken(tt.mFirst);
            Token second = para.findToken(tt.mSecond);
//            System.out.println("split: " + tt.mFirst + "  "  + tt.mSecond);
//            System.out.println("split: " + first + "  "  + second);
            eq.add(first, second);
        }

        for (Set<Token> split : eq.getClasses()) {
            para.addSplitInfo( new SplitInfo(Util.sortByPos(new ArrayList<Token>(split))) ); 
        }
    }
    
    

    /**
     * Second pass of local changes processing; creates {@link Para#changeInfos}.
     *
     * Translate positions in {@link #tmpChangeInfos} into tokens.
     */
    private void processArrows() {
        for (TmpChangeInfo info : tmpChangeInfos) {
            List<Token> affected = para.findTokens(info.from, info.to-info.from);
            userLogger.fine("Looking for tokens %d-%d : %s\n   %s\n", info.from, info.to, info.origText, Cols.toString(affected));

            if (!info.multiWord) {
                // we need to remember the whole original word, not just the replacing part
                final Token token = affected.get(0);
                int from = info.replaceFrom - 1 - token.from + para.from;   // opening brace
                int to   = info.to          + 1 - token.from + para.from;   // closing brace

                String oldText = new StringBuilder(affected.get(0).origToken)
                    .replace(from, to, info.origText)
                    .toString();

                para.addChangeInfo(affected, oldText);
            }
            else {
                para.addChangeInfo(affected, info.origText);
            }

        }
    }




    protected boolean assertW(boolean aTest, String aFormat, Object ... aParams) {
        if (aTest) return false;
        warning(aFormat, aParams);
        return true;
    }

    protected boolean assertW(boolean aTest, int aPos, String aFormat, Object ... aParams) {
        if (aTest) return false;

        String msg = String.format(aFormat, aParams) + "\nSomewhere around: \n" + errCtx(aPos);
        warning(msg);
        return true;
    }

    private void suspicious(int aPos, String aFormat, Object ... aParams) {
        assertErr(false, aPos, "Suspicious: " + aFormat, aParams);
    }
    
    protected void warning(String aFormat, Object ... aParams) {
        userLogger.warning(aFormat, aParams);
    }


    protected void assertErr(boolean aTest, String aFormat, Object ... aParams) {
        if (aTest) return;

        throw new FormatError(aFormat, aParams);
    }

    protected void assertErr(boolean aTest, int aPos, String aFormat, Object ... aParams) {
        if (aTest) return;

        String msg = String.format(aFormat, aParams) + "\nError somewhere around: \n" + errCtx(aPos);
        
        throw new FormatError(msg);
    }

    protected String errCtx(int aPos) {
        if (0 <= aPos && aPos < text.length()) {
            return text.substring(Math.max(0,aPos-40),Math.min(aPos+40,text.length()) ) + "\n" 
                    + "." + Strings.spaces(Math.min(aPos,40)) + "^";
        }
        else {
            new RuntimeException().printStackTrace();
            return String.format("!! Internal error - reported context is out of range. pos=%d, len=%d", aPos, text.length());
        }
    }
    
    private void log(DelString aFormat, Object ... aParam) {
        if (x) System.out.printf(aFormat + "\n", aParam);
    }

}

