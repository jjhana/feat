
package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.BitSet;
import java.util.regex.Pattern;
import org.purl.jh.util.str.Strings;

/**
 * One day this will be string with ignorable/deletable characters.
 * Used to remove certain things from a string without shifting character's
 * indexes.
 * 
 * Note: All ignored characters are guaranteed to be 0 (ascii). Todo would space be better???
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class DelString implements Comparable<DelString>, CharSequence {

    /**
     * Characters in text corresponding to set bits were deleted.
     * Meaningful spaces are never deleted (meaningless spaces are spaces
     * incorrectly inserted before codes).
     * The characters are marked for deletion instead of deleted so that indexes
     * into the text remain valid.
     */
    private final BitSet ignored;
    private final StringBuilder chars;

    public DelString(final String s) {
        this( s, new BitSet(s.length()) );
    }

    public DelString(final CharSequence s, final BitSet bitset) {
        this.ignored = bitset;
       this.chars = new StringBuilder(s);
    }

    public CharSequence getChars() {
        return chars;
    }

    public BitSet getIgnored() {
        return ignored;
    }

    public boolean isIgnored(int aCharIdx) {
        return ignored.get(aCharIdx);
    }

    /** 
     * Returns the first non-ignored index on or after the specified index 
     */
    public int skipIgnored(int aIdx) {
        while (aIdx < chars.length() && isIgnored(aIdx)) {
            aIdx++;
        }
        return aIdx;
    }

    public String clean() {
        final StringBuilder sb = new StringBuilder(chars.length());

        for (int i=0; i < chars.length(); i++) {
            if (!ignored.get(i)) sb.append( chars.charAt(i) );
        }

        return sb.toString();
    }

    public String clean(int aFrom, int aToExcl) {
        final StringBuilder sb = new StringBuilder(aToExcl-aFrom);

        for (int i=aFrom; i < aToExcl; i++) {
            if (!ignored.get(i)) sb.append( chars.charAt(i) );
        }

        return sb.toString();
    }

    public String cleanDebug() {
        final StringBuilder sb = new StringBuilder(chars.length());

        for (int i=0; i < chars.length(); i++) {
            if (!ignored.get(i)) {
                sb.append( chars.charAt(i) );
            }
            else {
                sb.append( '_' );
            }
        }

        return sb.toString();
    }




    /**
     * Returns clean substring.
     * @param aFrom
     * @param aToExcl
     * @return
     */
    public String substring(int aFrom, int aToExcl) {
        CharSequence subseq = chars.subSequence(aFrom, aToExcl);
        return clean(subseq, ignored, aFrom);
    }




    public void setCharAt(int aIdx, char aChar) {
        chars.setCharAt(aIdx, aChar);
        ignored.clear(aIdx);
    }


    /**
     * Note that the replaced and replacing strings must have the same length.
     * @param aRegex
     * @param aNewStr 
     */
    public void replaceAll(String aRegex, String aNewStr) {
        replaceAll(Pattern.compile(aRegex), aNewStr);
    }

    /**
     * Note that the replaced and replacing strings must have the same length.
     * @param aRegex
     * @param aNewStr 
     */
    public void replaceAll(Pattern aRegex, String aNewStr) {
        String newText = aRegex.matcher(chars).replaceAll(aNewStr);

        if (newText.length() != chars.length()) {
            System.out.println("old: " + chars);
            System.out.println("new: " + newText);
            throw new RuntimeException("Replace cannot change length of the string" );
        }

        //chars = new StringBuilder(aNewText);
        chars.setLength(0);
        chars.append(newText);
    }
    
    /**
     * Replaces a substring by another. The replacing string cannot be longer than 
     * the replaced one.
     * 
     * @param aFrom   initial index of the replaced string
     * @param aToExcl the first index after the replaced string
     * @param aNewStr the replacing string; cannot be longer than the replaced one.
     */
    public void replace(int aFrom, int aToExcl, String aNewStr) {
        //System.out.println("Replacing: " + chars.substring(aFrom, aToExcl) + " - " + aNewStr);
        final int oldLen = aToExcl - aFrom;
       if (aNewStr.length() > oldLen) throw new RuntimeException("Replacing string cannot be longer than the replaced one");
       
        chars.replace(aFrom, aToExcl, aNewStr + Strings.spaces(oldLen - aNewStr.length()));

       if (aNewStr.length() < oldLen) {
           ignore(aFrom + aNewStr.length(), aToExcl);
       }
    }

    /** Index of the preceding character (skipping ignored slots) */
    public int prevCharIdx(int aIdx) {
        return prevClearBit(aIdx);
    }

    /**
     * @param aIdx
     * @return
     */
    public int nextCharIdx(int aIdx) {
        return nextClearBit(aIdx);
    }




    public void ignore(final int aCharIdx) {
        chars.setCharAt(aCharIdx, (char)0);
        ignored.set(aCharIdx);
    }

    public void ignore(final int aFrom, final int aToExclusive) {
        for (int i=aFrom; i<aToExclusive; i++) {
            chars.setCharAt(i, (char)0);
        }
        ignored.set(aFrom, aToExclusive);
    }


    /**
     * TODO make faster by going by words
     * @param aIdx
     * @return
     */
    private int nextClearBit(final int fromIndex) {
        // Todo not sure what ignored.nextClearBit(aIdx) excatly does (e.g. what it returns if there is no next clear bit)
        for (int i = fromIndex + 1; i < chars.length(); i++) {
            if (!ignored.get(i)) return i;
        }

        return -1;
    }

    /**
     * Previous non-ignored index.
     * 
     * TODO make faster by going by words
     * @param aIdx
     * @return
     */
    private int prevClearBit(final int fromIndex) {
        for (int i = fromIndex - 1; i > 0; i--) {
            if (!ignored.get(i)) return i;
        }

        return -1;
    }




    // @todo to Strings, with test
    public static String clean(CharSequence aOrig, BitSet aMap, int aOffset) {
        final StringBuilder sb = new StringBuilder(aOrig.length());

        for (int i=0; i < aOrig.length(); i++) {
            if (!aMap.get(i+aOffset)) sb.append( aOrig.charAt(i) );
        }

        return sb.toString();
    }

    public static String cleanDebug(CharSequence aOrig, BitSet aMap, int aOffset) {
        StringBuilder sb = new StringBuilder(aOrig.length());

        for (int i=0; i < aOrig.length(); i++) {
            if (!aMap.get(i+aOffset)) {
                sb.append( aOrig.charAt(i) );
            }
            else {
                sb.append( '_' );
            }
        }

        return sb.toString();
    }





    public char charAt(int index) {
        return chars.charAt(index);
    }

    public int length() {
        return chars.length();
    }

    public CharSequence subSequence(int start, int end) {
        return chars.subSequence(start, end);
    }

    public void replaceAll(final int aFrom, final int aTo, final Pattern aRegex, final String aReplacement) {
        String oldStr = chars.substring(aFrom, aTo);
        String newStr = aRegex.matcher(oldStr).replaceAll(aReplacement);

        if (newStr.length() != oldStr.length()) throw new UnsupportedOperationException("Only same length replace supported");

        chars.replace(aFrom, aTo, newStr);
    }


    public int compareTo(DelString o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toString() {
        return cleanDebug(chars, ignored, 0);
    }

}
