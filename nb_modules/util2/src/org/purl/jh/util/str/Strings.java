package org.purl.jh.util.str;

import java.util.*;
import java.util.regex.*;
import org.purl.jh.util.Pair;
import org.purl.jh.util.PairC;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * String operations (would be added to an extension of String if that were possible)
 *
 * @todo consider the load time of constants (patterns and comparators). Move 
 *   to a separate class? Cache?
 * @author  Jirka Hana
 */
public final class Strings {
    /** Prevents creation of an instance */
    private Strings() {}

    public final static Pattern cWhitespacePattern = Pattern.compile("[\\s\\xA0]+");    // adds A0, i.e. non-breaking space (&nbsp;)
    public final static Pattern cDotPattern = Pattern.compile("\\.");
    public final static Pattern cColonPattern = Pattern.compile("\\:");
    public final static Pattern cSemicolonPattern = Pattern.compile("\\;");

    public final static String newLine = System.getProperty("line.separator", "\n");

    public final String newLine() {
        return newLine;
    }

    /**
     * Returns comparator comparing objects based on their string representation
     * (as returned by String.valueOf function)
     *
     * @param <T> type of the object to compare
     * @return comparator
     * @todo lazy initialization ???
     */
    public final  static <T> Comparator<T> comparator() {
        return new Comparator<T>() {
            public int compare(T o1, T o2) {
                return String.valueOf(o1).compareTo(String.valueOf(o2));
            }
        };
    }


    /**
     * Lexical order of reversed strings. 
     * Strings are compared by their tails and not heads.
     *
     * For example: [a, aa, b, x, ax, aax, aaaaax, bax, bx]
     */
    public final static Comparator<String> tailLexOrder = new Comparator<String>() {
        public int compare(String aStr1, String aStr2) {
            int len1 = aStr1.length();
            int len2 = aStr2.length();
            int n = Math.min(len1, len2);

            for (int i = 1; i <= n; i++) {
                char c1 = aStr1.charAt(len1 - i);
                char c2 = aStr2.charAt(len2 - i);
                if (c1 != c2) return c1 - c2;
            }
            return len1 - len2;
        }    
        
        public boolean equals(Object obj) {return this == obj;}
    };

    /**
     * Lexical order of reversed strings with reversed length.
     * A tail of a string follows that string.
     *
     * For example: [aa, a, b, aaaaax, aax, ax, bax, bx, x]
     */
    public final static Comparator<String> tailLexOrderRL = new Comparator<String>() {
        public int compare(String aStr1, String aStr2) {
            int len1 = aStr1.length();
            int len2 = aStr2.length();
            int n = Math.min(len1, len2);

            for (int i = 1; i <= n; i++) {
                char c1 = aStr1.charAt(len1 - i);
                char c2 = aStr2.charAt(len2 - i);
                if (c1 != c2) return c1 - c2;
            }
            return len2 - len1;
        }    
        
        public boolean equals(Object obj) {return this == obj;}
    };

// =============================================================================
//
// =============================================================================
    
    public static List<Character> toList(String aChars) {
        final List<Character> chars = new ArrayList<Character>(aChars.length());
        for (char c : aChars.toCharArray()) {
            chars.add(c);
        }
        return chars;
    }

    /**
     * Creates a string consisting of the specified number of a character.
     * @param aC character to repeat
     * @param aN number of of repetitios of aC (must be positive)
     */
    public static String repeatChar(char aC, int aN) {
        final StringBuilder buf = new StringBuilder(aN);
        for (int i = 0; i < aN; i++)
            buf.append(aC);
        return buf.toString();
    }

    /**
     * Creates a string consisting of the specified number of spaces.
     * @param aN number of spaces
     */
    public final static String spaces(int aN) {
        return (aN < 20) ? "                    ".substring(0, aN) : repeatChar(' ', aN);
    }
        
    /**
     * Removes tail of a word. If the tail is not present the word is marked.
     *
     * @todo Too specific, move somewhere else
     */
    public static String removeEnding(String aWord, String aEnding) {
        if (! aWord.endsWith(aEnding))
            return aWord + "!!+" + aEnding;
        
        return removeTail(aWord, aEnding.length()); 
    }

    

 
 
    /**
     * Use sparingly.
     * @param aStr1
     * @param aStr2
     * @param aStr3
     * @return
     */
    public static String concat(String aStr1, String aStr2, String aStr3) {
        int len1 = aStr1.length();
        int len2 = aStr2.length();
        int len3 = aStr3.length();
        int len = len1 + len2 + len3;

        char buf[] = new char[len];
        aStr1.getChars(0, len1, buf, 0);
        aStr2.getChars(0, len2, buf, len1);
        aStr3.getChars(0, len3, buf, len1 + len2);
        return new String(buf, 0, len);
    }
    
    /**
     * Use sparingly.
     */
    public static String concat(String aStr1, char aChar2, String aStr3) {
        int len1 = aStr1.length();
        int len3 = aStr3.length();
        int len = len1 + 1 + len3;

        char buf[] = new char[len];
        aStr1.getChars(0, len1, buf, 0);
        buf[len1] = aChar2;
        aStr3.getChars(0, len3, buf, len1 + 1);
        return new String(buf, 0, len);
    }

    /**
     * Use sparingly.
     */
    public static String concat(String ... aStrs) {
        // --- Calculate total length ---  HO: sum(aStrs, \x . length), ie. fold(aStrs, \x . length, +, 0)
        int len = 0;
        for(String str : aStrs)
            len += str.length();

        char buf[] = new char[len];
        
        int pos = 0;
        for(String str : aStrs) {
            str.getChars(0, str.length(), buf, pos);
            pos += str.length();
        }
        return new String(buf, 0, len);
    }

    /**
     * The character at the specified index is set to <code>ch</code>. 
     * A new string is created that is identical to <code>aStr</acode>, 
     * except that it contains the character <code>aChar</code> at position <code>aIdx</code>. 
     *
     * @param      aIdx   the index of the character to modify.
     *                <code>0 <= aIdx aStr.length()</code>
     * @param      aChar      the new character.
     * @throws     IndexOutOfBoundsException  if not aIdx is illegal
     */
    public static String setCharAt(String aStr, int aIdx, char aChar) {
        return aStr.substring(0,aIdx) + aChar + aStr.substring(aIdx+1);
    }
    
    
// -----------------------------------------------------------------------------
// Spliting
// -----------------------------------------------------------------------------
    
    /**
     * Splits the string by whitespace (using a precompiled pattern).
     * Equivalent (but faster) to aString.split("\\s+)
     */
    public static String[] split(String aString) {
        return cWhitespacePattern.split(aString);
    }

    /**
     * Splits the string by whitespace (using a precompiled pattern).
     * @return immutable list of strings
     */
    public static List<String> splitL(String aString) {
        return Arrays.asList(cWhitespacePattern.split(aString));
    }

    /**
     * Future faster version of String.split()
     */
    public static String[] split(String aString, char aChar) {
        return aString.split("" + aChar);
    }

    /**
     * Slow!!
     * Future faster version of String.split()
     */
    public static List<String> splitL(String aString, char aChar) {
        return Arrays.asList(aString.split("" + aChar));
    }
    

    public static List<String> splitL(String aString, Pattern aPattern) {
        return Arrays.asList( aPattern.split(aString) );
    }

    public static List<String> splitLL(String aString, Pattern aPattern) {
        return new ArrayList<String>( splitL(aString, aPattern) );
    }

    
    
    /**
     *
     * Assuming '^' is the separator:
     * <table>
     * <tr><td>  "aaa^bbb"     <td>-&gt; ("aaa", "bbb")
     * <tr><td>  "aaa^bbb^ccc" <td>-&gt; ("aaa", "bbb^ccc")
     * <tr><td>  "aaa^"        <td>-&gt; ("aaa", "")
     * <tr><td>  "aaa"         <td>-&gt; ("aaa", "")
     * </table>
     */
    public static StringPair splitIntoTwo(String aString, char aSeparator) {
        int sepIdx = aString.indexOf(aSeparator);
        if (sepIdx == -1) 
            return new StringPair(aString, "");
        else
            return new StringPair(aString.substring(0,sepIdx), aString.substring(sepIdx+1));
    }
    
    /**
     *
     * Assuming '^' is the separator:
     * <table>
     * <tr><td>  "aaa^bbb"     <td>-&gt; ("aaa", "bbb")
     * <tr><td>  "aaa^bbb^ccc" <td>-&gt; ("aaa^bbb", "ccc")
     * <tr><td>  "aaa^"        <td>-&gt; ("aaa", "")
     * <tr><td>  "aaa"         <td>-&gt; ("aaa", "")
     * </table>
     */
    public static StringPair splitIntoTwoB(String aString, char aSeparator) {
        int sepIdx = aString.lastIndexOf(aSeparator);
        if (sepIdx == -1) 
            return new StringPair(aString, "");
        else
            return new StringPair(aString.substring(0,sepIdx), aString.substring(sepIdx+1));
    }

    /** 
     * Two level split.
     *
     * E.g (aSep1 = ':', aSep2 = '/', the outter [] in a result mean List)
     *  "a/b/c:d/e/f" -> [[a, b, c], [d, e, f]]
     *  "a/b/c:d/e"   -> [[a, b, c], [d, e]]
     *  "a/b/c"       -> [[a, b, c]]
     *  "a:d"         -> [[a], [d]]
     *  "a"           -> [[a]]
     */
    public static List<List<String>> doubleSplit(String aString, char aSep1, char aSep2)  {
        String[] corrs = split(aString,aSep1);
        List<List<String>> tuples = new ArrayList<List<String>>(corrs.length);
        
        for (String corr : corrs ) {
            tuples.add(splitL(corr, aSep2));
        }
        return tuples;
    }
    
    /**
     * @todo generalize
     * Translates a level string to SimpleLTTags
     * Separators:
     * '|' separates alternatives within level and 
     * ':' separates tag and src (empty src means any src)
     */
    public static List<StringPair> splitIntoPairs(String aStr, String aSep1, char aSep2) {
        // --- get tag:src set for this level ---
        List<StringPair> tss = new ArrayList<StringPair>();
        for ( String tsStr  : aStr.split(aSep1) ) {
            tss.add( Strings.splitIntoTwo(tsStr, aSep2) );
        }

        return tss;
    }
    
    
// -----------------------------------------------------------------------------
// Search
// -----------------------------------------------------------------------------
    
	/**
	 * Search a string for a substring followed by one of two characters.
	 *
	 * @param   aStr    the string to search thru
     * @param   aSubStr the substring to search for
	 * @param   aCh1/2  one of these chars should followed aStr
	 * @return  If the substring was found in the string followed
	 *          by either of the characters, the index of the first character
	 *          of the first such substring is returned. Otherwise -1 is returned.
	 */
    public static int indexOfPlus2(String aStr, String aSubStr, char aCh1, char aCh2) {
        return indexOfPlus2(aStr, aSubStr, aCh1, aCh2, 0);
    }

	/**
	 * Search a string for a substring followed by one of two characters,
     * starting at the specified index.
	 *
	 * @param   aStr      the string to search thru
     * @param   aSubStr   the substring to search for
	 * @param   aCh1/2    one of these chars should followed aStr
	 * @param   aFromIdx  the index to start the search from
	 * @return  If the substring was found in the string after the specified index 
     *          followed by either of the characters, the index of the first character
	 *          of the first such substring is returned. Otherwise -1 is returned.
	 */
    public static int indexOfPlus2(String aStr, String aSubstr, char aCh1, char aCh2, int aFromIdx) {
        int substrLen = aSubstr.length();
        int len = aStr.length();
        
        for (;;) {
            if (aFromIdx + substrLen + 1 >= len) return -1;     // + 1 for aCh1/2

            int idx = aStr.indexOf(aSubstr, aFromIdx);
            if (idx == -1) return -1;

            if (aStr.charAt(idx + substrLen) == aCh1 || aStr.charAt(idx + substrLen) == aCh2) 
                return idx;
        
            aFromIdx = idx + 1; // + 1 for aCh1/2
        }
    }
    
// -----------------------------------------------------------------------------
// Accessing from the back
// -----------------------------------------------------------------------------
    
    /** 
     * Returns the last char of a string.
     * Equivalent to lastCharAt(aString, 0)
     * Does not perform any checks.
     */
    public static char lastChar(String aString) {
        return aString.charAt(aString.length() - 1);
    }

    /** 
     * Returns the char at the specified index, counting from the end of the string.
     * Does not perform any checks.
     */
    public static char charAtB(String aString, int aIdx) {
        return aString.charAt(aString.length() - 1 - aIdx);
    }

    /**
     * Returns a new string that is a substring of this string, removing the 
     * specified number of characters from each side.
     * <p>
     * Examples:
     * <blockquote><pre>
     * Strings.substringB("hamburger",0, 0) returns "hamburger"
     * Strings.substringB("hamburger",0, 1) returns "hamburge"
     * Strings.substringB("hamburger",1, 2) returns "amburg"
     * </pre></blockquote>
     *
     * @param      aLTrim   nr of chars to trim from left, 
     *     i.e. the beginning index (inclusive).
     * @param      aRTrim   nr of chars to trim from right, 
     *     i.e. the end index when counting from the back (inclusive)
     * @return     the specified substring.
     * @exception  IndexOutOfBoundsException  if <code>aLTrim</code> or 
     * <code>aRTrim</code> are inappropriate
     */
    public static String substringB(String aString, int aLTrim, int aRTrim) {
        return aString.substring(aLTrim, aString.length() - aRTrim);
    }


// -----------------------------------------------------------------------------
// Head, Tail
// -----------------------------------------------------------------------------

    /**
     *
     * Does not actually look whether aString starts with aOldHead, it simply removes 
     * a head from aString of the length of aOldHead, and replaces it with aNewHead .
     * E.g. abcde,ab,xyz -> xyzcde
     *
     * Does not check anything (whether aString actually ends with aOldHead, or whether it
     * is at least as long).
     */
    public static String replaceHead(String aString, String aOldHead, String aNewHead) {
        return aNewHead + aString.substring(aOldHead.length()); 
    }

    /**
     * Does not actually look whether aString starts with aHead, it simply removes 
     * a head from aString of the length of aOldHead.
     * E.g. abcde,ab -> cde
     *
     * Does not check anything (whether aString actually ends with aHead, or whether it
     * is at least as long).
     */
    public static String removeHead(String aString, String aHead) {
        return aString.substring(aHead.length()); 
    }

    /** 
     * Removes the specified tail if present.
     */
    public static String removePossHead(String aString, String aPossibleHead) {
        if ( aString.startsWith(aPossibleHead) ) 
            return aString.substring(aPossibleHead.length());
        else
            return aString;
    }
    

    /**
     * Gets the tail of the specified length.
     * Does not actually look whether aString ends with aOldTail, it simply removes 
     * an tail from aString of the length of aOldTail, then adds aNewTail.
     * E.g. abcde,3 -> cde
     *
     * @return tail of the specified length
     *
     * Does not check anything (whether aString has at least aLen characters)
     */
    public static String getTail(String aString, int aLen) {
        return aString.substring(aString.length()-aLen); 
    }
    
    /**
     *
     * Does not actually look whether aString ends with aOldTail, it simply removes 
     * an tail from aString of the length of aOldTail, then adds aNewTail.
     * E.g. abcde,de,xyz -> abcxyz
     *
     * Does not check anything (whether aString actually ends with aOldTail, or whether it
     * is at least as long).
     */
    public static String replaceTail(String aString, String aOldTail, String aNewTail) {
        return removeTail(aString, aOldTail.length()) + aNewTail; 
    }

    /** 
     * Removes tail of the specified length.
     */
    public static String removeTail(String aString, int aTailLen) {
        return aString.substring(0,aString.length()-aTailLen); 
    }
    
    /** 
     * Removes the specified tail.
     * Note: There are no checks performed, simply the aTail's number of characters
     * are removed from aString.
     */
    public static String removeTail(String aString, String aTail) {
        return Strings.removeTail(aString, aTail.length());
    }

    /** 
     * Removes the specified tail if present.
     */
    public static String removePossTail(String aString, String aPossibleTail) {
        if ( aString.endsWith(aPossibleTail) ) 
            return Strings.removeTail(aString, aPossibleTail.length());
        else
            return aString;
    }

    
// -----------------------------------------------------------------------------
// Inserting, deleting
// -----------------------------------------------------------------------------

    public static String insert(String aString, int aCharIdx, char aChar) {
        return aString.substring(0, aCharIdx) + aChar + aString.substring(aCharIdx); 
    }
    
    public static String insertB(String aString, int aCharLastIdx, char aChar) {
        int charIdx = aString.length() - aCharLastIdx;
        return insert(aString, charIdx, aChar);
    }

    /**
     * Deletes the characters between the specified indexes (inclusive).
     * Chars on neither of the indexes will occur in the resulting string.
     * @todo make the sencond index exclusive.
     */
    public static String delete(String aString, int aFrom, int aTo) {
        return aString.substring(0, aFrom) + aString.substring(aTo+1); 
    }

    /**
     * Deletes the character with the specified index.
     */ 
    public static String deleteChar(String aString, int aIdx) {
        return aString.substring(0, aIdx) + aString.substring(aIdx+1); 
    }
    
    /**
     * Deletes the character with the specified index, counting from the end.
     * The last character has index 0.
     */ 
    public static String deleteCharB(String aString, int aLIdx) {
        return deleteChar(aString, aString.length() - aLIdx - 1);
    }

    
// -----------------------------------------------------------------------------    
// Testing inclusion
// -----------------------------------------------------------------------------    
    
    /**
     * Tests whether a string is in a list of strings.
     */
    public static boolean in(String aItem, String ... aSet) {
        for (String e : aSet)
            if (aItem.equals(e)) return true;
        
        return false;
    }


    /**
     * Checks if a specified character in a string is contained in another string.
     * 
     * @param aString string containing the character to check
     * @param aCharIdx index of the tested character in aString
     * @param aSet set of characters to check. Can be empty.
     */ 
    public static boolean charIn(String aString, int aCharIdx, String aSet) {
        return inString(aString.charAt(aCharIdx), aSet);
    }
    
    
    /**
     * Checks if a specified character in a string is contained in another string.
     * 
     * @param aString string containing the character to check
     * @param aCharIdx reverse index of the tested character in aString
     * @param aSet set of characters to check. Can be empty.
     */ 
    public static boolean charInB(String aString, int aCharIdx, String aSet) {
        return inString(charAtB(aString, aCharIdx), aSet);
    }

    /**
     * Checks if a string contains only allowed characters.
     * In other words - checks if charaters of one string form a subset of the 
     * other string.
     *
     * @param aString string to check characters in
     * @param aSet allowed characters
     * HO: aString.forAll(\\x.aSet.indexOf(x) != -1)
     */ 
    public static boolean allCharsIn(String aString, String aSet) {
        for (int i = 0; i<aString.length();i++)
            if (aSet.indexOf(aString.charAt(i)) == -1) return false;
        return true;
    }
    
    /**
     * Checks if the intersection of the characters in the two strings is non-empty.
     * In other words - if the two strings are not disjunct.
     *
     * @param aString the string to test
     * @param aSet    the set of characters to look for.
     * @returns true if aString contains some chars from aSet, false otherwise.
     *     <br>True:  (abcd, axy), (a, axy),
     *     <br>False: (abcd, xyz), ("", axy),  (abcd, "")
     * @todo originally called someCharsIn
     */ 
    public static boolean someCharsShared(String aString, String aSet) {
        for (int i = 0; i<aString.length();i++)
            if (aSet.indexOf(aString.charAt(i)) != -1) return true;
        return false;
    }

    
    /**
     * Tests if a character is contained in a string.
     * 
     * @param aItem character to look for
     * @param aSet string to search through
     * @return true if aItem is in aSet, false otherwise
     */
    public static boolean inString(char aItem, String aSet) {
        return aSet.indexOf(aItem) != -1;
    }


// -----------------------------------------------------------------------------    
// Format
// -----------------------------------------------------------------------------    
    /**
     * Ensures that a string is not longer and not shorter than specified.
     * Longer strings are trimmed, shorter strings are right padded.
     * 
     * @param aStr
     * @param aWidth
     * @return
     */
    public static String format(String aStr, int aWidth) {
        int len = aStr.length();
        if (len == aWidth) {
            return aStr;
        }
        else if (len > aWidth) {
            return aStr.substring(0, aWidth);
        } 
        else { 
            return aStr + spaces(aWidth-len);
        }
    }


    public static String limit(String aString, int aLimit) {
        return (aString == null || aString.length() <= aLimit) ? aString : aString.substring(0, aLimit);
    }

    /**
     *
     * @param aString
     * @param aLimit
     * @return
     */
    public static String dotsLimit(String aString, int aLimit) {
        Err.iAssert(aLimit >=3, "Limit is %d; it must be at least 3", aLimit);
        return (aString == null || aString.length() <= aLimit) ? aString : aString.substring(0, aLimit-3) + "...";
    }



    private static final Pattern cTrimPattern = Pattern.compile("[\\s\\xA0]*(.*?)[\\s\\xA0]*");

    /**
     * Removes leading and trailing whitespace, including non-breaking space.
     * For strings not containing non-breaking spaces (ascii 160, hex A0), the
     * result is the same as when {@link java.lang.String#trim()} is used.
     *
     *
     * @param aStr string to trim
     * @return trimmed string
     */
    public static String trim(final String aStr) {
        final Matcher m = cTrimPattern.matcher(aStr);
        m.matches();
        //System.out.println("|" + aStr + "| -> |" + m.group(1) + "|");
        return m.group(1);
    }

    public static boolean isWhitespace(char aChar) {
        return Character.isWhitespace(aChar) || aChar == 0xA0;
    }

    /**
     * Creates a pattern with a more general white-space (includes not-breaking space).
     * Does not handle whitespace as a part of a set.
     *
     * @param aRegex
     * @return
     */
    public static Pattern wspattern(String aRegex) {
        String wsRegex = aRegex.replaceAll("\\Q\\s\\E", "[\\\\s\\\\xA0]");
        return Pattern.compile(wsRegex);
    }


    
    /**
     * Truncates a string if necessary.
     * 
     * @param aString string to truncate
     * @param aMaxLength maximum length of the returned string
     * @return aString if its length is smaller than aLimit, otherwise returns
     *   prefix of aString of the length aLimit. Null string is returned as null.
     */
    public static String trim(String aString, int aMaxLength) {
        return (aString == null || aString.length() <= aMaxLength) ? aString : aString.substring(0, aMaxLength);
    }
    
    /**
     * Breaks long string into a several lines with specified width.
     * 
     * @param aString string to format
     * @param aWidth max length of each line (must be >0)
     * @return returns list of lines string
     * @todo
     */
    public static List<String> breakIntoLines(String aString, int aWidth) {
        int len = aString.length();
        List<String> lines = new ArrayList<String>(len/aWidth+1);
        
        // --- break ---
        for (int i=0;;) {
            // --- the rest fits the line ---
            if (len <= i+aWidth) {
                lines.add( aString.substring(i) );
                break;
            }

            // --- find place to break the text ---
            int afterLastSpace = findBreak(aString, i, i+aWidth);
            lines.add( aString.substring(i, afterLastSpace) );

            i = afterLastSpace;
        }

        return lines;
    }

    public static String format(String aString, int aFirstInd, int aInd, int aWidth) {
        List<String> lines = breakIntoLines(aString, aWidth);
        return Cols.toString(lines, 2 * aString.length(), spaces(aFirstInd), "", '\n' + spaces(aInd), "");
    }
    
    /**
     * Finds space between aMin (excl) and aMax (incl), as closest to aMax as possible.
     * If there is none, returns aMax
     * @return index imediatelly after the space
     */
    private static int findBreak(String aString, int aMin, int aMax) {
        int lastSpace = aString.lastIndexOf(' ', aMax);
        return (lastSpace > aMin) ? lastSpace+1 : aMax;
    }

// =============================================================================
// Special  <editor-fold desc="Special">"
// =============================================================================

    /**
     * Finds a unique id on the basis of a suggestion.
     * If the suggested id is not taken, it is returned. Otherwise it is appended
     * with a positive number making it unique (the number is as small as possible)
     * 
     * @param aSuggestedId the suggested id
     * @param aExistingIds set of existing ids (the new id is <b>not</b> added to the set)
     * @param aSeparator string separating the suggested id and the distinguishing number
     * @hasTest
     */
    @Deprecated
    public static String findUniqueId(String aSuggestedId, Collection<String> aExistingIds, String aSeparator) {
        if (!aExistingIds.contains(aSuggestedId)) return aSuggestedId;
        
        for(int counter = 1;;counter++) {
            String newId = aSuggestedId + aSeparator + counter;
            if (!aExistingIds.contains(newId)) return newId;
        }
    }
        
    
    private static Pattern cStrPlusDigits = Pattern.compile("(.*?)([0-9]*)");
        
    /**
     * Splits an id into the core part followed by a counter.
     * <ul>
     * <li>splitId("abc12") = ("abc",12)
     * <li>splitId("abc") = ("abc",null)
     * <li>splitId("12") = ("",12)
     * <li>splitId("") = ("",null)
     * </ul>
     */
    @Deprecated
    public static Pair<String,Integer> splitId(String aCurId) {
        Matcher matcher = cStrPlusDigits.matcher(aCurId);
        matcher.matches();
        String core = matcher.group(1);
        String finalDigitsStr = matcher.group(2);
        Integer finalDigits = (finalDigitsStr.length() > 0) ? Integer.parseInt(finalDigitsStr) : null;
        
        return new PairC<String,Integer>(core, finalDigits);
    }

    /**
     * Finds the next available id on the basis of an existing id.
     * It assumes ids have two parts a core and a counter
     *
     * <ul>
     * <li>findNextId("abc12", ... ) = "abc13"
     * <li>findNextId("abc", ... )   = "abc1"
     * <li>findNextId("12", ... )    = "13"
     * </ul>
     */
    @Deprecated
    public static String findNextId(String aCurId, Collection<String> aExistingIds) {
        Pair<String,Integer> strPlusDigits = splitId(aCurId);
        
        int counter = (strPlusDigits.mSecond == null) ? 1 : strPlusDigits.mSecond + 1;
        
        for(;;counter++) {
            String newId = strPlusDigits.mFirst + counter;
            if (!aExistingIds.contains(newId)) return newId;
        }
    }
    
// </editor-fold>    
    

}
