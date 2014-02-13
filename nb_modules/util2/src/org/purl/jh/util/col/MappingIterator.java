package org.purl.jh.util.col;

import java.util.Iterator;

/**
 * Light-weight mapping iterator.
 * 
 * @author Jirka Hana
 */
public class MappingIterator<D,R> implements Iterator<R> {
    final private Iterator<D> origIterator;
    final private Mapper<? super D,R> mapper;

    public MappingIterator(Iterator<D> aOrigIt, Mapper<? super D,R> aMapper) {
        mapper = aMapper;
        origIterator = aOrigIt;
    }

    public MappingIterator(Iterable<D> aOrigCollection, Mapper<? super D,R> aMapper) {
        this(aOrigCollection.iterator(), aMapper);
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {return origIterator.hasNext();}

    /**
     * Returns the next element in the iteration.
     * The elemeent is an element from the original collection mapped by the mapper.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public R next() {return mapper.map( origIterator.next() );}

   /**
     * Removes from the underlying collection/iterable the last element returned by the
     * iterator.  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * Note that the item is removed both from the mapped collection and the original
     * collection.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by the backing Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
     public void remove() {origIterator.remove();}
}
