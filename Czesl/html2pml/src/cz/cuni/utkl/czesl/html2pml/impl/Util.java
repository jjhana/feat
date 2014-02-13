/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author jirka
 */
public class Util {
    // todo util
    public static int min(int ... aNrs) {
        int min = Integer.MAX_VALUE;
        
        for (int i = 0; i < aNrs.length; i++) {
            if (aNrs[i] < min) min = aNrs[i];
        }
                
        return min;        
    }

    /** Returns the position of the first unbalanced paren or -1 */
    public static int findUnbalanced(final CharSequence aText, final int start, final char aOpen, final char aClose) {
        final Stack<Integer> openinPoss = new Stack<Integer>();

        for (int i=start; i < aText.length(); i++) {
            char c = aText.charAt(i);
            if (c == aOpen) {
                openinPoss.push(i);
            }
            else if (c == aClose) {
                if (openinPoss.isEmpty()) return i;
                openinPoss.pop();
            }
        }
        return  (openinPoss.isEmpty()) ? -1 : openinPoss.elementAt(0);
    }

    /**
     * Sorts tokens by their position in the document.
     * 
     * @param aList list of tokens to sort (in place)
     * @return sorted list (identical to aList
     */
    public static List<Token> sortByPos(List<Token> aList) {
        Collections.sort(aList, new Comparator<Token>() {
            @Override
            public int compare(Token o1, Token o2) {
                return o1.from - o2.from;
            }

            
        });
        return aList;
    }
}
