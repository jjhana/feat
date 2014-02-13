package org.purl.jh.util.col;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Set intended for sets with few members.
 * Currently represents them as a simple array list.
 * @author jirka
 * todo start with a singleton, use a dispatching factory?? (but should be modifiable)
 */
public class SmallSet<T> extends AbstractSet<T> {

    final List<T> vals;

    public static <E> SmallSet<E> newx() {
        return new SmallSet<E>();
    }

    public static <E> SmallSet<E> newx(Collection<E> aCol) {
        return new SmallSet<E>(aCol);
    }

    public SmallSet() {
        vals  = new ArrayList<T>();
    }

    public SmallSet(Collection<? extends T> aCol) {
        vals  = new ArrayList<T>(aCol);
    }


    @Override
    public Iterator<T> iterator() {
        return vals.iterator();
    }

    @Override
    public int size() {
        return vals.size();
    }

    @Override
    public boolean add(T e) {
        if (vals.contains(e)) {
            return false;
        }
        return vals.add(e);
    }
}
