package org.purl.jh.util.col;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Lightweight collection created by a mapper on a basis of an underlying collection.
 * 
 * This collection is backed by the original collection, so any changes to that 
 * collection are reflected in this collection. This collection is unmodifiable, 
 * all modifications will throw {@link UnsupportedOperationException}. 
 * 
 * @author Jirka Hana
 */
public class MappingCollection<D,R> extends AbstractCollection<R> {
    final private Collection<D> origCollection;
    final private Mapper<? super D,R> mapper;

    public MappingCollection(Collection<D> aOrigCollection, Mapper<? super D,R> aMapper) {
        origCollection = aOrigCollection;
        mapper = aMapper;
    }

    @Override
    public Iterator<R> iterator() {
        return new MappingIterator<D,R>(origCollection, mapper);
    }

    @Override
    public int size() {
        return origCollection.size();
    }
}
