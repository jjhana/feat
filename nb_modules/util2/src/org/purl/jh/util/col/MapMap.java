package org.purl.jh.util.col;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author jirka
 */
public interface MapMap<K,L,V> { //extends Map<K,Map<L,V>>  {

//    boolean addAll(K aKey, Map<L, V> aValues);
//
//    void addAll(MapMap<K, L, V> aMap);

    Map<L, V> get(K key);

    V get(K aKey1, L aKey2, V aValue);

    boolean containsKey(K key);

    boolean containsValue(V value);

    Collection<V> values();

    Set<K> keySet();

    Set<Entry<K, Map<L, V>>> entrySet();

    boolean isEmpty();

    int size();

    V put(K aKey1, L aKey2, V aValue);

    void clear();

    Map<L, V> remove(K key1);

    boolean equals(Object o);

    int hashCode();
}
