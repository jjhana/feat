package org.purl.jh.util.col;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.purl.jh.util.Pair;

/**
 *
 * @author jirka
 */
public class HashMapHashMap<K,L,V> implements MapMap<K,L,V> {
    final Map<Pair<K,L>,V> fullmap = XCols.newHashMap();
    final Map<K,Map<L,V>>  mapmap  = XCols.newHashMap();

    @Override
    public Map<L, V> get(K key) {
        return mapmap.get(key);
    }

    @Override
    public V get(K aKey1, L aKey2, V aValue) {
        final Map<L,V> tmp = mapmap.get(aKey1);

        return tmp == null ? null : tmp.get(aKey2);
    }

    @Override
    public boolean containsValue(V value) {
        return fullmap.containsValue(value);
    }

    @Override
    public boolean containsKey(K key) {
        return mapmap.containsKey(key);
    }

    /** DO not modify this collection. todo */
    @Override
    public Collection<V> values() {
        return fullmap.values();
    }

    @Override
    public Set<Entry<K, Map<L, V>>> entrySet() {
        return mapmap.entrySet();
    }

    @Override
    public int size() {
        return fullmap.size();
    }

// =============================================================================
// Modifications
// =============================================================================
    @Override
    public V put(K aKey1, L aKey2, V aValue) {
        fullmap.put(new Pair<K,L>(aKey1, aKey2), aValue);

        Map<L,V> tmp = mapmap.get(aKey1);

        if (tmp == null) {
            tmp = XCols.newHashMap();
            tmp.put(aKey2, aValue);
        }

        return tmp.put(aKey2, aValue);
    }

    @Override
    public Map<L, V> remove(K key1) {
        for (Iterator<Entry<Pair<K, L>, V>> it = fullmap.entrySet().iterator(); it.hasNext();) {
            if (it.next().getKey().mFirst.equals(key1)) it.remove();
        }
        
        return mapmap.remove(key1);
    }

//    public void putAll(Map<? extends K, ? extends Map<L, V>> m) {
//        map.putAll(m);
//    }
//
//    public Map<L, V> put(K key, Map<L, V> value) {
//        return map.put(key, value);
//    }

    @Override
    public Set<K> keySet() {
        return mapmap.keySet();
    }

    @Override
    public boolean isEmpty() {
        return mapmap.isEmpty();
    }

    @Override
    public int hashCode() {
        return fullmap.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return fullmap.equals(o);
    }

    @Override
    public void clear() {
        mapmap.clear();
        fullmap.clear();
    }
}
