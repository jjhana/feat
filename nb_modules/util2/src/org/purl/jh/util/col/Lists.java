package org.purl.jh.util.col;

import java.util.Arrays;
import java.util.List;

/**
 * Operations on lists.
 * 
 * @author jirka
 */
public class Lists {
    
    /**
     * Creates a new list containing the elements of the iterable.
     * @deprecated Use guava's Lists.newArrayList instead
     */
    @Deprecated
    public static <T> List<T> list(Iterable<T> iterable) {
        return com.google.common.collect.Lists.newArrayList(iterable);
    }
    
    /**
     * @deprecated use guavas's ImmutableList.of instead
     */
    @Deprecated
    public static <T> List<T> of(T aElement) {
        return (aElement == null) ? null : Arrays.asList(aElement);
    }

    /**
     * @deprecated use guavas's ImmutableList.of instead
     */
    @Deprecated
    public static <T> List<T> of(T aElement, T aElement2) {
        return Arrays.asList(aElement, aElement2);
    }

    /**
     * Inserts a new element into a list after another element.
     *
     * @param <T>
     * @param aList list to insert into
     * @param aAnchor object to insert the new element after; if the list contains 
     * multiple instances of the anchor, the first one is used; null anchor means 
     * insertion at the beginning of the list
     * @param aNewElement element to insert
     * @throws IllegalArgumentException if the anchor is not present in the list
     */
    public static <T> void addAfter(List<T> aList, T aAnchor, T aNewElement) {
        int idx = 0;
        if (aAnchor != null) {
            idx = aList.indexOf(aAnchor);
            if (idx == -1) throw new IllegalArgumentException("Anchor " + aAnchor + " is not in the list.");
            idx++;
        }

        if (idx < aList.size()) {
            aList.add(idx, aNewElement);
        }
        else {
            aList.add(aNewElement);
        }
    }

}
