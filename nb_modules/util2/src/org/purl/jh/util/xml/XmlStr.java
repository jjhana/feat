package org.purl.jh.util.xml;

import org.purl.jh.util.str.Strings;

/**
 * Low level xml utilities.
 *
 * @author jirka
 */
public class XmlStr {

    /**
     * Checks if the given string is an xml tag.
     * 
     * Currently only the opening and closing parens are checked. More to come.
     * 
     * @param aStr string to check
     * @return true if the para can be an xml string.
     */
    public static boolean isTag(final String aStr) {
        if (aStr.charAt(0) != '<' || Strings.lastChar(aStr) != '>') return false;

        // todo more checks
        return true;
    }
}
