package org.purl.jh.util.col;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set mapped from another set of objects. For example, a structured object on its item.
 * <p>
 * This set is backed by the original set, so any changes to that set are reflected
 * in this set. This set is unmodifiable, use {@link #copy} to produce a modifiable
 * copy of it (which is no longer backed by the original list).
 */
public class MappingSet<D,R> extends AbstractSet<R> {
    final private Set<D> origCollection;
    final private Mapper<? super D, R> mapper;

    public MappingSet(Set<D> aOrigCollection, Mapper<? super D, R> aMapper) {
        origCollection = aOrigCollection;
        mapper = aMapper;
    }

    @Override 
    public Iterator<R> iterator() {
        return new MappingIterator<D, R>(origCollection, mapper);
    }

    @Override
    public int size() {
        return origCollection.size();
    }

    /**
     * Creates a new set containing all the mapped elements.
     * The resulting set is no longer backed by the original set, so any
     * changes to it (deletions, inserts) do not affect the original set, nor 
     * the other way round.
     */
    public Set<R> copy() {
        return new HashSet<R>(this);
    }
}

