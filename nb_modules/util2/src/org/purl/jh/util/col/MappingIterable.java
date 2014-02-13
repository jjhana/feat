package org.purl.jh.util.col;

import java.util.Iterator;

/**
 * Iterable collection mapping items of some other iterable.
 *
 * @param <D> type of the items in the original iterable
 * @param <R> type of the items in the mapped iterable
 */
public class MappingIterable<D,R> implements Iterable<R> {
    final private Iterable<D> origCollection;
    final private Mapper<? super D,R> mapper;

    public MappingIterable(Iterable<D> aOrigCollection, Mapper<? super D,R> aMapper) {
        origCollection = aOrigCollection;
        mapper = aMapper;
    }

    @Override
    public Iterator<R> iterator() {
        return new MappingIterator<D,R>(origCollection, mapper);
    }
}
