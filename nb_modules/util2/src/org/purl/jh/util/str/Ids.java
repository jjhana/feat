
package org.purl.jh.util.str;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.Pair;
import org.purl.jh.util.PairC;

/**
 * Support for unique id strings.
 * 
 * @author Jirka dot Hana at gmail dot com
 */
public class Ids {
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
    public static Pair<String,Integer> splitId(String aCurId) {
        Matcher matcher = cStrPlusDigits.matcher(aCurId);
        matcher.matches();
        String core = matcher.group(1);
        String finalDigitsStr = matcher.group(2);
        Integer finalDigits = (finalDigitsStr.length() > 0) ? Integer.parseInt(finalDigitsStr) : null;

        return new PairC<String,Integer>(core, finalDigits);
    }

    public static int getFinalCounter(final String aId) {
        final Matcher matcher = cStrPlusDigits.matcher(aId);
        matcher.matches(); // always matches
        final String finalDigitsStr = matcher.group(2);
        return (finalDigitsStr.length() > 0) ? Integer.parseInt(finalDigitsStr) : -1;
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
