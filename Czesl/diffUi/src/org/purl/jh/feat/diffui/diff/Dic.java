package org.purl.jh.feat.diffui.diff;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.*;

/**
 * Symmetric 1:1 map used to keep track of matched objects.
 * @author j
 */
public class Dic<X> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Dic.class);
    
    private final BiMap<X,X> a2bx;

    
    public Dic() {
        a2bx = HashBiMap.create();
    }

    public Set<Map.Entry<X, X>> getMatching() {
        return a2bx.entrySet();
    }

    /**
     * Returns matching object in the 2nd document.
     * @param item1 item in the 1st document 
     * @return 
     */
    public X getMatching2(final X item1) {
        return a2bx.get(item1);
    }

    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param items1
     * @return 
     */
    public Set<X> getMatching2(final Iterable<? extends X> items1) {
        final Set<X> bItems = new HashSet<>();
        for (X a : items1) {
            X b = getMatching2(a);
            if (b != null) bItems.add(b);
        }
        return bItems;
    }
    
    /**
     * Returns matching object in the 1st document.
     * @param item2 item in the 2nd document 
     * @return 
     */
    public X getMatching1(final X item2) {
        return a2bx.inverse().get(item2);
    }

    /**
     * Convenience function, matches a collection of object.
     * All objects without a counterpart are simply skipped.
     * 
     * @param items2
     * @return 
     */
    public Set<X> getMatching1(final Iterable<? extends X> items2) {
        final Set<X> aItems = new HashSet<>();
        for (X b : items2) {
            X a = getMatching1(b);
            if (a != null) aItems.add(a);
            log.info("getAMatching: added %s <-> %s (%s)" , b, a, aItems);
        }
            log.info("getAMatching: result: %s", aItems);
        return aItems;
    }
    
    /**
     * Convenience function testing if two items are matched.
     * @param item1
     * @param item2
     * @return 
     */
    public boolean areMatching(final X item1, final X item2) {
        X item1match = getMatching2(item1);
        return item1match == item2;
    }
    
    /**
     * Records matching between two items
     * 
     * @param item1 item in the 1st document
     * @param item2 item in the 2nd document
     */
    public void match(final X item1, final X item2) {
        a2bx.put(item1, item2);
    }

    public void matchAll(final Dic<X> dic) {
        a2bx.putAll(dic.a2bx);
    }
    
    
    public <Y> Collection<Y> unmatched1s(Iterable<Y> aItems) {
        return unmatched(aItems, a2bx);
    }

    public <Y> Collection<Y> unmatched2s(Iterable<Y> aItems) {
        return unmatched(aItems, a2bx.inverse());
    }

    private static <G, E> Collection<E> unmatched(Iterable<E> aObjs, Map<G, G> aDict) {
        final List<E> unmatched = new ArrayList<>();
        for (E o : aObjs) {
            if (!aDict.containsKey(o)) {
                unmatched.add(o);
            }
        }
        return unmatched;
    }
    
}
