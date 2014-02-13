package org.purl.jh.util.sgml;

import java.io.IOException;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.LineReader;

/**
 * Sgml utilities.
 *
 * @author Jirka Hana
 */
public final class Sgml {

    
    public static String readStringBetween(LineReader aR, String aStartCore, String aEndCore) throws IOException {
        String lineStr = aR.readLine();
        if (lineStr == null) return null;
        
        if ( startsWithTag( lineStr, aStartCore) )  {
            StringBuilder sb = new StringBuilder();
            for (;;) {
                lineStr = aR.readLine();
                Err.fAssert(lineStr != null, "No <%s> found for <%s> in %s", aR.getFile(), aEndCore, aStartCore);
                if (Sgml.startsWithTag(lineStr,aEndCore)) break;

                sb.append(lineStr).append('\n');
            }

            return sb.toString();
        }
        else {
            aR.pushBack(); 
            return null;
        }
    }
    
// -----------------------------------------------------------------------------
// Search
// -----------------------------------------------------------------------------

    /** 
     * Tests whether the strings starts with a tag with the specified core.
     *
     * Note: does not check whether the first character is '<'.
     */
    public static boolean startsWithTag(String aStr, String aTagCore) {
        int tagLen = aTagCore.length() + 1;                //  '<' + aTag
        if (aStr.length() <= tagLen) return false;     // too short (at least < aTag >
        if (!aStr.startsWith(aTagCore, 1)) return false;   // tag core is wrong
        char nextChar = aStr.charAt(tagLen);           // should be '>' / ' '
        return nextChar == '>' || nextChar == ' ';
    }

    /** 
     * Tests whether the strings starts with a tag with the specified core.
     *
     * Faster version of startsWithTag for single character cores.
     * Note: does not check whether the first character is '<'.
     */
    public static boolean startsWithTag(String aStr, char aTag) {
        if (aStr.length() <= 2) return false;      // too short
        if (aStr.charAt(1) != aTag) return false;  // tag core is wrong
        char nextChar = aStr.charAt(2);            // should be '>' / ' '   
        return (nextChar == '>' || nextChar == ' ');
    }

    /** 
     * A convenience method; tests whether the strings starts with a tag with 
     * either of the two cores.
     *
     * Faster than calling statsWithTag twice.
     * Note: does not check whether the first character is '<'.
     */
    public static boolean startsWithTag(String aStr, char aTagCore1, char aTagCore2) {
        if (aStr.length() <= 2) return false;      // too short
        if (aStr.charAt(1) != aTagCore1 && aStr.charAt(1) != aTagCore2) return false;  // tag core is wrong
        char nextChar = aStr.charAt(2);            // should be '>' / ' '   
        return (nextChar == '>' || nextChar == ' ');
    }
    
	/**
	 * Search a string for a sgml tag with the specified core.
	 *
	 * @param   aStr    the string to search thru
     * @param   aTagCore the tag core to to search for (e.g. l for <l das='+'>)
	 * @return  The index of the first tag, if it was found; -1 otherwise.
     *    
	 */
    public static int indexOfTagCore(String aStr, String aTagCore) {
        return indexOfTagCore(aStr, aTagCore, 0);
    }

	/**
	 * Search a string for a sgml tag with the specified core starting at the 
     * specified location.
	 *
	 * @param   aStr      the string to search thru
     * @param   aTagCore the tag core to to search for (e.g. l for <l das='+'>)
	 * @param   aFromIdx  the index to start the search from
	 * @return  The index of the first tag following the aFrom index, if it was found; -1 otherwise.
	 */
    public static int indexOfTagCore(String aStr, String aTagCore, int aFromIdx) {
        return org.purl.jh.util.str.Strings.indexOfPlus2(aStr, '<' + aTagCore, '>', ' ');
    }
    
    /**
     * Returns the first found tag (excl. brackets).
     */
    public static String getFirstTag(String aString, int aSearchFrom) {
        int tagStartIdx = aString.indexOf('<', aSearchFrom);
        if (tagStartIdx == -1) return null;
        
        tagStartIdx++;
        
        int tagEndIdx = aString.indexOf('>', tagStartIdx);
        if (tagEndIdx == -1) return null;       // incorrect sgml
        
        return aString.substring(tagStartIdx,tagEndIdx);
    }
        
    /** 
     * Returns an sgml tag at a particular position
     * 
     * @throws FormatError when the aPos doe not contain &lt; or the closing &gt; cannot be found.
     * In the future possibly 
     */
    public static SgmlTag getTagAt(CharSequence aStr, int aPos) {
        if (aStr.charAt(aPos) != '<') throw Err.fErr("Expecting an sgml tag, found %s...", Err.forwardCtx(aStr, aPos, 10)); 
        
        int closingBraceIdx = aStr.toString().indexOf('>', aPos);
        
        if (closingBraceIdx == -1) throw Err.fErr("Expecting an sgml tag, found %s...", Err.forwardCtx(aStr, aPos, 10)); 
        
        return new SgmlTag(aStr.subSequence(aPos, closingBraceIdx+1).toString());
    }
}
