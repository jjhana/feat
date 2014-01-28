/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.util0;

import java.util.*;

/**
 *
 * @author j
 */
public class ColsX {
    /**
     * Finds elements of a particular type.
     *
     * @param aItems items to search
     * @param aClass type of the elements to search for
     * @return returns a list of all the elements of the desired type; if there are no
     *    such elements, the returned list is empty.
     * @todo to util
     */
    public static <T, E extends T> List<E> filter(Iterable<? extends T> aItems, Class<E> aClass) {
        final List<E> result = new ArrayList<>();
        for (T e : aItems) {
            if (aClass.isInstance(e)) result.add( aClass.cast(e) );
        }
        return result;
    }
}
    
