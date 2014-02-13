package org.purl.jh.util.col;


import java.util.*;
import org.purl.jh.util.col.pred.Predicate;

/**
 * Map containing sets as values
 * @todo provide an efficient immutable/slowly mutable MultiMap using exact arrays
 * @todo iterator over all values
 *
 * @author jirka
 */
public class MultiHashHashMap<K,V> extends HashMap<K,Set<V>> implements MultiMap<K, V> {
    private static final MultiMap cEmpty = new MultiHashHashMap();

    /**
     * Do not modify this map!
     * @param <KK>
     * @param <VV>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <KK,VV> MultiHashHashMap<KK,VV> cEmpty() {
	return (MultiHashHashMap<KK,VV>) cEmpty;
    }

    public static <KK,VV> MultiHashHashMap<KK,VV> neww() {
        return new MultiHashHashMap<KK,VV>();
    }

    public static <KK,VV> MultiHashHashMap<KK,VV> neww(MultiMap<KK,VV> aMap) {
        return new MultiHashHashMap<KK,VV>(aMap);
    }

// =============================================================================
//
// =============================================================================

    public MultiHashHashMap() {}

    public MultiHashHashMap(MultiMap<K,V> aMap) {
        super(aMap);
    }

// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

    @Override
    public int totalSize() {
        int size = 0;
        for(Set<V> entry : values()) {    //HO: ? values().fold(0, \\x:int,e:V . x + e.size() )
            size += entry.size();
        }
        return size;
    }

    /**
     * Copies all individual values into an collection.
     * @todo do not copy, just iterate over
     * @return
     */
    @Override
    public Iterable<V> allValues() {
        final List<V> allValues = new ArrayList<V>(totalSize());
        for (Set<V> oneKeyValues : values()) {
            allValues.addAll(oneKeyValues);
        }
        return allValues;
    }

    @Override
    public boolean addEmpty(K aKey) {
        if (get(aKey) != null) return false;
        put(aKey, new HashSet<V>());
        return true;
    }

    /**
     *
     * @return <tt>true</tt> if the value was added to this map.
     */
    @Override
    public boolean add(K aKey, V aValue) {
        return getS(aKey).add(aValue);
    }

    /**
     * @todo ineffective if aKey not present and aValues is a Set (recreated and added)
     * @return <tt>true</tt> if at least one value was added to this map.
     */
    @Override
    public boolean addAll(K aKey, Collection<V> aValues) {
        return getS(aKey).addAll(aValues);
    }

    @Override
    public void addAll(MultiMap<K,V> aMap) {
        for (Map.Entry<K,Set<V>> e : aMap.entrySet()) {
            addAll(e.getKey(), e.getValue());
        }
    }



    /**
     * If the key is not yet in the map, adds to it associated with empty set.
     *
     * @return a set of values associated with the key (empty if the key was not in the map)
     */
    @Override
    public Set<V> getS(K aKey) {
        Set<V> set = get(aKey);

        if (set == null) {
            set = new HashSet<V>();
            put(aKey, set);
        }
        return set;
    }

    /**
     * Removes the specified value from a specified key.
     * Removes the key if its last value was removed.???
     */
    @Override
    public boolean remove(K aKey, V aValue) {
        Set<V> set = get(aKey);
        return (set == null) ? false : set.remove(aValue);
    }

    /**
     * Removes the specified value from all keys.
     */
    @Override
    public void removeValue(V aValue) {
        for (Map.Entry<K,Set<V>> e : entrySet()) {
            e.getValue().remove(aValue);
        }
    }

    /**
     * Removes all values satisfying the specified predicate.
     * Emty entries are removed.
     */
    @Override
    public MultiHashHashMap<K,V> removeAll(Predicate<V> aPredicate) {
        for (Iterator<Map.Entry<K,Set<V>>> ei = entrySet().iterator(); ei.hasNext(); ) {
            Set<V> set = ei.next().getValue();
            if ( Cols.removeAll(set, aPredicate).isEmpty() )
                ei.remove();
        }
        return this;
    }

   /**
     * Keeps only the values satisfying the specified predicate.
     * Emty entries are removed.
     */
    @Override
    public MultiHashHashMap<K,V> retainAll(Predicate<V> aPredicate) {
        for (Iterator<Map.Entry<K,Set<V>>> ei = entrySet().iterator(); ei.hasNext(); ) {
            Set<V> set = ei.next().getValue();
            if ( Cols.retainAll(set, aPredicate).isEmpty() )
                ei.remove();
        }
        return this;
    }

    /**
     * Merges all values of two keys, assigns them to the new key and drops the old key.
     * Neither key is required to exist.
     */
    @Override
    public void merge(K aNewKey, K aOldKey) {
        final Set<V> newValues = get(aNewKey);
        final Set<V> oldValues = remove(aOldKey);

        if (oldValues != null) {
            if (newValues != null) {
                newValues.addAll(oldValues);
            }
            else {
                put(aNewKey, oldValues);
            }
        }
    }

    /**
     * Performs and action on all items of the set with the specified key.
     *
     * @param aKey key of the desired set
     * @param aAction action to be performed on the set with the key aKey
     */
/*    public void actionOn(K aKey, Action aAction)      // @ZTODO Generic action how to doit
    {
        Set<V> s = get(aKey);

        if (s == null) return;

        for (Iterator i = s.iterator(); i.hasNext();)
                aAction.action(i.next());
    }*/

    /**
     * Returns a list of keys sorted by the number of associated values.
     */
    @Override
    public List<K> keysByFrequency() {
        final Comparator<K> comparator = new Comparator<K>() {
            public int compare(K o1, K o2) {
                return get(o1).size() - get(o2).size();
            }
        };
        final List<K> sortedKeys = new ArrayList<K>(keySet());
        Collections.sort(sortedKeys, comparator);
        return  sortedKeys;
    }

}