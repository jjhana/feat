package org.purl.jh.util.col;

import java.util.Comparator;
import java.util.List;

/**
 * TODO CHECK IF IT WORKS
 *
 * Utility class with methods operating on sorted (growing) lists.
 * For efficiency reasons, none of the method checks whether the lists are really
 * sorted. However, {@link #isGrowing(java.util.List, java.util.Comparator)}
 * method can be used to check this.
 *
 * @author jhana
 */
public final class SortedLists {

    /**
     * Checks if a list is sorted according to a comparator
     * @param <R>
     * @param aList list to check
     * @param aComp comparator to check against
     * @return true if the list is monotonously growing (not necessarily strictly,
     * i.e. adjacent elements can be equal), false otherwise
     */
    public static <R> boolean isGrowing(final List<R> aList, final Comparator<R> aComp) {
        final int n = aList.size() -1;
        for (int i=0; i < n; i++) {
            if ( aComp.compare(aList.get(i), aList.get(i+1)) > 0 ) return false;
        }

        return true;
    }

    /**
     * Delete all the values in aVals from aList.
     *
     * @param <R> type of the items in both lists
     * @param aList list to delete from
     * @param aVals list with values to be deleted
     * @param aComp comparator according to which both lists a sorted
     */
    public static <R> List<R> delete(final List<R> aList, final List<R> aVals, final Comparator<R> aComp) {
        int n = aList.size();
        final int m = aVals.size();

        if (m==0) return aList;

        int i = 0;  // index in aList
        int j = 0;  // index in aVals

        for (; i < n && j < m; ) {
            R val = aVals.get(j);  // saves repeated get calls; usually, j changes much less frequently then i

            // skip elements in either list that do not have a match in the other
            for (;;) {
                int comp = aComp.compare(aList.get(i), val);
                if (comp < 0) {
                    i++;
                    if (i >= n) return aList;
                } else if (comp > 0) {
                    j++;
                    if (j >= m) return aList;
                    val = aVals.get(j);
                } else {
                    break;
                }
            }

            // delete all instances of val in aList
            int start = i;
            for (; i < n && aComp.compare(aList.get(i), val) == 0; i++) {
            }
            aList.subList(start,i).clear();

            n = n - (i - start); // the list got shorter
            i = start;
            j++; // move to the next value (the current one was completely cleaned)
        }
        return aList;

    }

    private SortedLists() {
    }
}
