package org.purl.jh.util;

import org.purl.jh.util.str.Strings;


/**
 *
 * @author  Jirka Hana
 */
public final class Numbers {
    
    private Numbers() {
    }

    public static Integer parseInt(String aStr) {
        // --- a quick check ---
        if (Character.isLetter(aStr.charAt(0)) ) return null;

        try {
            return Integer.parseInt(aStr);
        }
        catch(NumberFormatException e) {
            return null;
        }
    }



    /** 
     * Converts a roman numeral to an integer.
     * 
     * @param rNum a string representing a roman numeral, must be either all
     *    upper case or all lower case.
     * @return integer corresponding to rNum or -1 if rNum is not a roman numeral (or mixes upper and lower case).
     */
    public static int roman(String rNum) {
        try {
            // --- a quick check ---
            if (! (allIn(rNum, "IVXLCDM") || allIn(rNum, "ivxlcdm")) ) return -1;

            return romanI(rNum);
        }
        catch (NumberFormatException e) {
            return -1;
        }
        
    }

   static final int  romans[]  =  {1, 5, 10, 50, 100, 500, 1000};
   static final char letters[] = {'I','V','X','L','C','D','M'};

    private static void check(boolean aTest) {
         if (!aTest) throw new NumberFormatException();
    }

    private static int pos(char aRDigit) {
        return "IVXLCDM".indexOf(aRDigit);
    }
    
    private static boolean allIn(String aValuesToCheck, String aPermitedValues) {
        int len = aValuesToCheck.length();

        for (int i=0; i < len; i++) 
            if (!Strings.charIn(aValuesToCheck, i, aPermitedValues)) return false;
        
        return true;
    }
    
    private static int romanI(String rNum) {
        int rNumLen = rNum.length();
        rNum = rNum.toUpperCase();
        int num = 0;
        int prevR = Integer.MAX_VALUE;
        int[] counter = {0,3,0,3,0,3, Integer.MIN_VALUE};
        
        for (int i=0; i < rNumLen; i++) {
            int curR = pos(rNum.charAt(i));
            int nextR    = (i + 1 < rNumLen) ? pos(rNum.charAt(i+1)) : -1;

            check(curR <= prevR);
            check(nextR    <= prevR);
            if (curR < nextR) {
                check(curR % 2 == 0);  //I-0, X-2, D-4, M-6;
                num += romans[nextR] - romans[curR];
                i++;
            }
            else {        
                check(counter[curR] < 4);      // 3 here and 2 in the counter array would be stricter (allows IIII for 4)
                num += romans[curR];
                counter[curR]++;
            }

            prevR = curR;
        }
        
        return (num == 0) ? -1 : num;
    }
}