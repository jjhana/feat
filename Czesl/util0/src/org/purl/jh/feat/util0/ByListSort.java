package org.purl.jh.feat.util0;

import java.util.*;

/**
 *
 * @author j
 */
//  /**
public class ByListSort<T> {
    private final Map<T, Integer> obj2idx;
    private final List<T> idx2obj;
    private final Comparator<T> comparator = new Comparator<T>() {

        @Override
        public int compare(T o1, T o2) {
            try {
                int i1 = obj2idx.get(o1);
                int i2 = obj2idx.get(o2);
                return i1 - i2;
            } catch (NullPointerException e) {
                throw new IllegalArgumentException(String.format("The order is not defined on %s or %s.\n%s", o1, o2, idx2obj));
            }
        }
    };

    public ByListSort(final List<T> aOrder) {
        obj2idx = new HashMap<>();
        idx2obj = new ArrayList<>(aOrder.size());
        for (int i = 0; i < aOrder.size(); i++) {
            obj2idx.put(aOrder.get(i), i);
            idx2obj.add(aOrder.get(i));
        }
    }

    public Comparator<T> getComparator() {
        return comparator;
    }

    public T min(T a, T b) {
        return comparator.compare(a, b) < 0 ? a : b;
    }

    /**
     * Finds a minimum in a collection.\
     *
     * @param aCol
     * @return minimum in aCol; null if the collection is empty
     */
    public T min(Iterable<T> aCol) {
        T tmp = null;
        for (T obj : aCol) {
            if (tmp == null) {
                tmp = obj;
            } else {
                tmp = min(tmp, obj);
            }
        }
        return tmp;
    }

    public List<T> sort(List<T> aList) {
        if (aList.size() < 5) {
            Collections.sort(aList, comparator);
        } else {
            // todo translate first, sort, translate back
            Collections.sort(aList, comparator);
        }
        return aList;
    }
    
}
