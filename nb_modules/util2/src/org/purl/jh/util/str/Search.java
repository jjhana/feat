package org.purl.jh.util.str;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Search {
    // @todo to Strings, with test
    public static int findMatchingOpeningBrace(final CharSequence aText, final int start) {
        return findMatchingOpening(aText, start, '{', '}');
    }

    /**
     * Strings
     * @param aText
     * @param start
     * @return
     * @todo to Strings, with test
     */
    public static int findMatchingOpening(final CharSequence aText, final int start, final char aOpen, final char aClose) {
        int counter = 1;

        for (int i=start-1; i>=0; i--) {
            char c = aText.charAt(i);
            if (c == aClose) {
                counter++;
            }
            else if (c == aOpen) {
                counter--;
            }
            if (counter == 0) return i;
        }

        return -1;
    }

    // @todo to Strings, with test
    public static int findMatchingClosingBrace(final CharSequence aText, final int start) {
        return findMatchingClosing(aText, start, '{', '}');
    }

    /**
     * Strings
     * @param aText
     * @param start
     * @return
     */
    public static int findMatchingClosing(final CharSequence aText, final int start, final char aOpen, final char aClose) {
        int counter = 1;

        for (int i=start+1; i < aText.length(); i++) {
            char c = aText.charAt(i);
            if (c == aOpen) {
                counter++;
            }
            else if (c == aClose) {
                counter--;
            }
            if (counter == 0) return i;
        }

        return -1;
    }
}
