package org.purl.jh.util.col;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Lightweight collection created by a mapper on a basis of an underlying list.
 * <p>
 * This list is backed by the original list, so any changes to that list are reflected
 * in this list. 
 * This list is unmodifiable, all modifications will throw {@link UnsupportedOperationException}. 
 * The only exception is {@link #remove(int)} which might be called. Use {@link #copy} 
 * to produce a modifiable copy of this list (which is no longer backed by the original list).
 * 
 * @author Jirka Hana
 */
public class MappingList<D, R> extends AbstractList<R> {
    final private List<D> origCollection;
    final private Mapper<? super D, R> mapper;

    public MappingList(List<D> aOrigCollection, Mapper<? super D, R> aMapper) {
        origCollection = aOrigCollection;
        mapper = aMapper;
    }

    @Override
    public R get(int aIdx) {  
        return mapper.map(origCollection.get(aIdx));
    }

    @Override public Iterator<R> iterator() {
        return new MappingIterator<D, R>(origCollection, mapper);
    }

    @Override
    public int size() {
        return origCollection.size();
    }
    
    @Override public R remove(int index) { 
	return mapper.map( origCollection.remove(index));
    }


    /**
     * Creates a new list containing all the mapped elements.
     * The resulting list is no longer backed by the original list, so any
     * changes to it (deletions, inserts) do not affect the original list.
     *
     *
     * @return
     */
    public List<R> copy() {
        return new ArrayList<R>(this);
    }
}

