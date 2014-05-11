package org.purl.jh.util.col;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.purl.jh.util.col.pred.Predicate;

/**
 *
 * @author jirka
 */
public interface MultiMap<K, V> extends Map<K,Set<V>> {
    /**
     *
     * @return <tt>true</tt> if the value was added to this map.
     */
    boolean add(K aKey, V aValue);

    /**
     * @todo ineffective if aKey not present and aValues is a Set (recreated and added)
     * @return <tt>true</tt> if at least one value was added to this map.
     */
    boolean addAll(K aKey, Collection<V> aValues);

    void addAll(MultiMap<K, V> aMap);

    boolean addEmpty(K aKey);

    /**
     * Copies all individual values into an collection.
     * @todo do not copy, just iterate over
     * @return
     */
    Iterable<V> allValues();

    /**
     * If the key is not yet in the map, adds to it associated with empty set.
     *
     * @return a set of values associated with the key (empty if the key was not in the map)
     */
    Set<V> getS(K aKey);

    /**
     * Returns a list of keys sorted by the number of associated values.
     */
    List<K> keysByFrequency();

    /**
     *
     */
    void merge(K aNewKey, K aOldKey);

    /**
     * Removes the specified value from a specified key.
     * Removes the key if its last value was removed.???
     */
    boolean remove(Object aKey, Object aValue);

    /**
     * Removes all values satisfying the specified predicate.
     * Emty entries are removed.
     */
    MultiMap<K, V> removeAll(Predicate<V> aPredicate);

    /**
     * Removes the specified value from all keys.
     */
    void removeValue(V aValue);

    /**
     * Keeps only the values satisfying the specified predicate.
     * Emty entries are removed.
     */
    MultiMap<K, V> retainAll(Predicate<V> aPredicate);

    int totalSize();

}
