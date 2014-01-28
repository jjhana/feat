package org.purl.jh.feat.layered.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo move to Cols.
 * @author jirka
 */
public class Util {

    /**
     * Return an element at a specified distance from an element
     * @param <T>
     * @param aList list to search in
     * @param aStart element to count offset from, if there are multiple such
     * elements in the list, the first one is used
     * @param aOffset distance of the desired element from aStart; may be negative
     * @return the element at a give offset from aStart; null if the resulting position is out of range.
     * @throws IllegalArgumentException if aStart is not present in the list
     */
    public static <T> T getOffset(List<T> aList, T aStart, int aOffset) {
        final int idx = aList.indexOf(aStart);
        if (idx == -1) throw new IllegalArgumentException(String.format("The start element %s is not part of the list.", aStart));

        final int newIdx = idx + aOffset;

        if (newIdx < 0 || aList.size() <= newIdx) return null;

        return aList.get(newIdx);
    }

    /**
     * Converts a list to an obj2idx  map.
     * 
     * getObj2IdxMap(list).get(obj) == i iff list.get(i) == obj
     * 
     * @param <T>
     * @param aObjs list of objects, no duplicates allowed (not checked)
     * @return 
     */
    public static <T> Map<T,Integer> getObj2IdxMap(final List<? extends T> aObjs) {
        final Map<T,Integer> obj2idx = new HashMap<>();
                
        for (int i = 0; i < aObjs.size(); i++) {
            obj2idx.put(aObjs.get(i), i);
        }

        return obj2idx;
    }

}
