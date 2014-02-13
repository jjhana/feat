
package org.purl.jh.util.str;

import java.text.ParseException;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Administrator
 */
public final class Caps {
    
    private Caps() {}

    
// -----------------------------------------------------------------------------    
// Tests
// -----------------------------------------------------------------------------    

    /**
     *
     * If several interpretations are possible, allLower wins over firstCap, which
     * wins over allCaps which wins over mixed.
     *
     * @todo optimize - can be probably done in one pass thru the string
     */
    public static Cap capitalization(String aString) {
        if (isAllLowerCase(aString))
            return Cap.lower;
        else if (isFirstUpperCase(aString))
            return Cap.firstCap;
        else if (isAllUpperCase(aString))
            return Cap.caps;

        try {
            cNumberFormat.parse(aString);
            return Cap.number;
        }
        catch(ParseException e) {};

        return Cap.mixed;
    }

    /**
     *
     * For empty string, returns true (similarly as do isAllUpperCase and isAllLowerCase)
     */ 
    public static boolean isFirstUpperCase(String aString) {
        if (aString.length() == 0) return false;
        return Character.isUpperCase( aString.charAt(0) ) && isAllLowerCase(aString.substring(1));
    }

    /**
     *
     * For empty string, returns true (similarly as do isFirstUpperCase and isAllUpperCase)
     */ 
    public static boolean isAllLowerCase(String aString) {
        int len = aString.length();
        for (int i = 0; i < len; i++) {
            if (! Character.isLowerCase( (int) aString.charAt(i) ) ) return false;
        }
        return true;
    }

    /**
     *
     * For empty string, returns true (similarly as do isFirstUpperCase and isAllLowerCase)
     */ 
    public static boolean isAllUpperCase(String aString) {
        int len = aString.length();
        for (int i = 0; i < len; i++) {
            if (! Character.isUpperCase( aString.charAt(i) ) ) return false;
        }
        return true;
    }
    
// -----------------------------------------------------------------------------    
// Modification
// -----------------------------------------------------------------------------    


    // todo should be
    final static java.text.NumberFormat cNumberFormat = java.text.NumberFormat.getInstance();

    /**
     * Converts a string to a string with the first charater in upper case, and 
     * all the rest in lowercase.
     */
    public static String toFirstCapital(String aString) {
        if (aString.length() == 0) return  aString;
        
        return Character.toUpperCase(aString.charAt(0)) + aString.substring(1).toLowerCase();
    }
    
    public static String capitalizeBy(String aStrToCapitalize, String aModelStr) {
        if (aStrToCapitalize.equals(aModelStr)) return aStrToCapitalize;

        // put form into lemmas capitalization
        Cap formCap = capitalization(aStrToCapitalize);
        Cap modelCap = capitalization(aModelStr);

        if (formCap == modelCap) return aStrToCapitalize;
        
        switch (modelCap) {
            case lower:
                return aStrToCapitalize.toLowerCase();
            case firstCap:
                return toFirstCapital(aStrToCapitalize);
            case caps:
                return aStrToCapitalize.toUpperCase();
            default:   // mixed/number
                return aStrToCapitalize; // just give up, hopefully there will not be many
        }
    }

}
