package cz.cuni.utkl.czesl.html2pml;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.err.FormatError;

/**
 *
 * @author j
 * 
 * todo parse into a graph than do an exhaustive walk-through
 * 
 * @todo under development
 */
public class AlternativeParser {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(AlternativeParser.class);
    
    private final static Pattern quickAlternativeCheck = Pattern.compile(".*\\{.+\\}.*");

    private final static String c = "[^\\{\\}]";
    
    private final static Pattern simple0 = Pattern.compile(String.format("(%s+)", c));
    private final static Pattern simple1 = Pattern.compile(String.format("(%s*)\\{(%s+)\\}(%s*)", c, c, c));
    private final static Pattern simple2 = Pattern.compile(String.format("(%s*)\\{(%s+)\\}(%s*)\\{(%s+)\\}(%s*)", c, c, c, c, c));
    
    
    public List<String> parseAlternatives(String token) {
        if (!isBalanced(token)) throw new FormatError("Unbalanced braces %s", token);
        
        if (!token.contains("{") && !token.contains("|")) return Arrays.asList(token);

        // check if braces are ballanced 
        Matcher m = simple0.matcher(token);
        if (m.matches()) {
                return parseOpts(token);
        }
        
        m = simple1.matcher(token);
        if (m.matches()) {
            return parseSimple1(token, m);
        }
        
        m = simple2.matcher(token);
        if (m.matches()) {
            return parseSimple2(token, m);
        }

        throw new UnsupportedOperationException("Cannot parse alternatives in '" + token + "'");
    }

//    private List<String> parse(String str) {
//        if (!isBalanced(str, 0)) throw new FormatError("Unbalanced string " + str);
//        // check if balanced braces
//        
//        int openBrace = str.indexOf("{");
//        int bar  = str.indexOf("|");
//
//        if (openBrace == -1 && bar == -1) return ImmutableList.of(str);
//        
//        if 
//        
//        String head = str.substring(openBrace, openBrace);
//        
//        int matchingClosing = Search.findMatchingClosingBrace(str, openBrace);
//        // if not 
//        
//        parse
//        
//        
//        
//    }
    
    
    protected List<String> parseSimple1(String token, Matcher m) {
        m.matches();
        
        String head = m.group(1);
        String opts  = m.group(2);
        String tail = m.group(3);
        log.info("Parsing alternatives %s > %s+%s+%s\n", token, head, opts, tail);

        final List<String> result = new ArrayList<>();
        for (String opt : parseOpts(opts)) {
            result.add(head + opt + tail);
        }
        return result;
    }

    protected List<String> parseSimple2(String token, Matcher m) {
        String head   = m.group(1);
        String opts1  = m.group(2);
        String middle = m.group(3);
        String opts2  = m.group(4);
        String tail   = m.group(5);
        log.info("Parsing alternatives %s > %s+%s+%s+%s+%s\n", token, head, opts1, middle, opts2, tail);
        
        List<String> result = new ArrayList<>();
        result.add("");

        result = combine(result, ImmutableList.of(head));
        result = combine(result, parseOpts(opts1));
        result = combine(result, ImmutableList.of(middle));
        result = combine(result, parseOpts(opts2));
        result = combine(result, ImmutableList.of(tail));
        
        return result;
    }    
    
    
    protected List<String> parseOpts(String optStr) {
        if (optStr.contains("|")) {
            return Arrays.asList(optStr.split("\\|",-1));
        }
        else {
            return Arrays.asList("", optStr);
        }
    }

    public static boolean isBalanced(final CharSequence aText) {
        return isBalanced(aText, 0);
    }

    public static boolean isBalanced(final CharSequence aText, final int start) {
        return isBalanced(aText, start, '{', '}');
    }
    
    /** Returns the position of the first unbalanced paren or -1 */
    public static boolean isBalanced(final CharSequence aText, final int start, final char aOpen, final char aClose) {
        final Stack<Integer> openinPoss = new Stack<>();

        for (int i=start; i < aText.length(); i++) {
            char c = aText.charAt(i);
            if (c == aOpen) {
                openinPoss.push(i);
            }
            else if (c == aClose) {
                if (openinPoss.isEmpty()) return false;
                openinPoss.pop();
            }
        }
        return  openinPoss.isEmpty();
    }    

    private List<String> combine(Iterable<String> befores, Iterable<String> afters) {
        List<String> result = new ArrayList<>();
        
        for (String before : befores) {
            for (String after : afters) {
                result.add(before + after);
            }
        }
        
        return result;
    }
}
