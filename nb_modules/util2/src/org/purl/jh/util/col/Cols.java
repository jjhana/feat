package org.purl.jh.util.col;

import java.util.*;
import java.util.Map.Entry;
import org.purl.jh.util.col.pred.Filters;
import org.purl.jh.util.col.pred.Predicate;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.str.pp.Printer;

/**
 *
 * @author  Jiri
 */
public final class Cols {
    
    private Cols() {
    }


    /**
     * Like Arrays.asList but allows null argument.
     * <p>
     * Returns a fixed-size list backed by the specified array.  (Changes to
     * the returned list "write through" to the array.)  This method acts
     * as bridge between array-based and collection-based APIs, in
     * combination with {@link Collection#toArray}.  The returned list is
     * serializable and implements {@link RandomAccess}.
     *
     * <p>This method also provides a convenient way to create a fixed-size
     * list initialized to contain several elements:
     * <pre>
     *     List&lt;String&gt; stooges = Arrays.asList("Larry", "Moe", "Curly");
     * </pre>
     *
     * @param aArray the array by which the list will be backed; can be null
     * @return a list view of the specified array; or null if <code>aArray</code> is null.
     */
    public static <T> List<T> asList(T ... aArray) {
        return (aArray == null) ? null : Arrays.asList(aArray);
    }

     public static <K,V> MultiMap<V,K> transpose(MultiMap<K,V> aMultiMap) {
         final MultiMap<V,K> tmap =  XCols.newMultiHashHashMap();

         for (Entry<K, Set<V>> e : aMultiMap.entrySet()) {
             for (V v : e.getValue()) {
                tmap.add(v, e.getKey());
             }
         }

         return tmap;
     }

    /**
     * Filters a list by a bitset.
     * @param <T>
     * @param aList
     * @param aBitSet
     * @return forall i in indexes(aList) . aBitset[i] iff (aList[i] in result)
     */
    public static <T> List<T> filter(final List<T> aList, final BitSet aBitSet) {
        final List<T> list = new ArrayList<T>();

        for (int i = 0; i < aList.size(); i++) {
            if (aBitSet.get(i)) list.add( aList.get(i) );
        }

        return list;
    }


    /**
     * Returns the single item in an iterable.
     * @param <T> type of the element
     * @param aCol iterable to unwrap the element from
     * @return the single item in the iterable
     * @throws MyException if there is more or less than one element in the iterable.
     */
    public static <T> T one(final Iterable<T> aCol) {
        final Iterator<T> it = aCol.iterator();
        Err.iAssert(it.hasNext(), "Supposedly singleton collection (%s) is empty", aCol);
        final T item = it.next();
        Err.iAssert(!it.hasNext(), "Supposedly singleton collection (%s) has multiple items", aCol);
        return item;
    }



// =============================================================================
// Various lists
// =============================================================================

    public static int[] range(int start, int end, int step) {
        final int[] list = new int[(end-start)/step];

        for(int i=start; i<end; i+=step) {
            list[i-start] = i;
        }

        return list;
    }

    /**
     * Creates a sorted list of all integers in a range.throws
     */
    public static int[] range(int start, int end) {
        final int[] list = new int[end-start];

        for(int i=start; i<end; i++) {
            list[i-start] = i;
        }

        return list;
    }

// =============================================================================
//
// =============================================================================

    /** Adds n nulls to the list */
    public static void init(final List<?> aList, final int n) {
        for(int i = 0; i<n;i++) {
            aList.add(null);
        }
    }


// =============================================================================
//
// =============================================================================

    /**
     * Sorts map entries.
     * @param <K>
     * @param <V>
     * @param aMapEntries
     * @param aComp
     * @return 
     */
    public static <K,V> List<Map.Entry<K,V>> sort(Collection<Map.Entry<K,V>> aMapEntries, Comparator<Map.Entry<K,V>> aComp) {
        List<Map.Entry<K,V>> list = new ArrayList<Map.Entry<K,V>>(aMapEntries);
        Collections.sort(list,aComp);
        return list;
    }

    /**
     * Sorts map entries by values.
     * @param <K>
     * @param <V>
     * @param aMapEntries
     * @param aComp
     * @return
     */
    public static <K,V> List<Map.Entry<K,V>> sortByVals(Collection<Map.Entry<K,V>> aMapEntries, final Comparator<V> aComp) {
        List<Map.Entry<K,V>> list = new ArrayList<Map.Entry<K,V>>(aMapEntries);

        Comparator<Map.Entry<K,V>> entryComp = new Comparator<Map.Entry<K,V>>() {
            public int compare(Map.Entry<K,V> o1, Map.Entry<K,V> o2) {
                return aComp.compare(o1.getValue(), o2.getValue());
            }
        };

        Collections.sort(list,entryComp);
        return list;
    }

//    public static <K,V> List<Map.Entry<K,V>> sortByVals(Collection<Map.Entry<K,V>> aMapEntries) {
//        List<Map.Entry<K,V>> list = new ArrayList<Map.Entry<K,V>>(aMapEntries);
//
//        Comparator<Map.Entry<K,V>> entryComp = new Comparator<Map.Entry<K,V>>() {
//            public int compare(Map.Entry<K,V> o1, Map.Entry<K,V> o2) {
//                return aComp.compare(o1.getValue(), o2.getValue());
//            }
//        };
//
//        Collections.sort(list,entryComp);
//        return list;
//    }
    
    public static <T> List<T> shiftUp(List<T> aList) {
        final int n=aList.size();
        if (n > 0) {
            // ? use array copy?
            for (int i=n-1; i>0;i--) {
                aList.set(i, aList.get(i-1));
            }
            aList.set(0,null);
        }
        return aList;
    }
    
    public static <T> List<T> shiftDown(List<T> aList) {
        final int n=aList.size();
        if (n > 0) {
            for (int i=1; i<n;i++) {
                aList.set(i-1, aList.get(i));
            }
            aList.set(n-1,null);
        }
        return aList;
    }
//    }
    
    /**
     * Tests whether an object is in a list.
     */
    public static <E> boolean in(E aEl, final E ... aList) {
        for (E e : aList)
            if (aEl.equals(e)) return true;
        
        return false;
    }

    /**
     *
     */
    public static <E> List<E> optimizeForReadOnly(List<E> aCol) {
        if (aCol instanceof ArrayList) {
            ((ArrayList)aCol).trimToSize();
        }
        return aCol;
    }

    public static <E> List<E> repeat(E aItem, int n) {
        List<E> list = new ArrayList<E>(n);
        for (int i = 1; i < n; i++) {
            list.add(aItem);
        }
        return list;
    }
    
// =============================================================================
// Searching <editor-fold desc="Searching">
// =============================================================================


    
    /**
     * Searches the whole lists but first starts at bias forward and then searches before it.
     */
    public static <E> int biasedIndexOf(List<E> aList, E aElement, int aBias) {
        int i = aList.subList(aBias, aList.size()).indexOf(aElement);
        if (i != -1) return aBias + i;
        
        return aList.subList(0, aBias).indexOf(aElement);
    }
    
    /**
     * Finds index of an element of a particular type.
     *
     * @param aList list to search
     * @param aClass type of the element to search for
     * @return returns the index of te first element of the desired type or
     *    -1 if no such an element exits.
     */
    public static <E> int indexOf(List<E> aList, Class<? extends E> aClass) {
        for (int i = 0; i < aList.size(); i++)
            if ( aClass.isInstance(aList.get(i)) ) return i;
        
        return -1;
    }

    /**
     * Finds element of a particular type.
     *
     * @param aItems items to search
     * @param aClass type of the element to search for
     * @return returns the first element of the desired type or null if no
     *    such an element exits.
     */
    public static <T, E extends T> E findElement(Iterable<? extends T> aItems, Class<E> aClass) {
        for (T e : aItems) {
            if (aClass.isInstance(e)) return aClass.cast(e);
        }
        return null;
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T, E extends T> E findClass(Collection<? extends T> aList, Class<E> aClass) {
        for (T element : aList)
            if (aClass.isInstance(element)) return (E) element;
        
        return null;
    }
    
// </editor-fold>

// =============================================================================
    
    /**
     *
     * Efficiency: aForms1 should be bigger than aForms2
     */
    public static boolean notDisjunctive(Collection<?> aForms1, Collection<?> aForms2) {
        for (Object form : aForms2) {
            if (aForms1.contains(form)) return true;
        }
        
        return false;
    }
    
    /**
     * Takes one element out of a collection.
     *
     * Note: the element taken out is the one returned by the first use of next
     * on an iterator. The collection must contain at least one item.
     */
    public static <T> T getFirstOut(Iterable<T> aCol) {
        Iterator<T> it = aCol.iterator();
        T el = it.next();
        it.remove();
        return el;
    }
    
    /**
     * Returns one element from a collection.
     *
     * @param aCol the collection to get the element from (the collection is not modified)
     * @return null if the collection is empty
     */
    public static <T> T getFirstElement(Iterable<T> aCol) {
        Iterator<T> it = aCol.iterator();
        return (it.hasNext()) ? it.next() : null;
    }

    /**
     * Returns the first element from a collection.
     *
     * @param aCol the collection to get the element from (the collection is not modified)
     * @return null if the collection is empty
     */
    public static <T> T first(Iterable<T> aCol) {
        Iterator<T> it = aCol.iterator();
        return (it.hasNext()) ? it.next() : null;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list.
     *
     * @throws IndexOutOfBoundsException if the list is empty.
     */
    public static <T> T last(List<T> aList) {
        return aList.get(aList.size() - 1);
    }

    public static <T> T prev(final List<T> aList, final T aEl) {
        final int idx = aList.indexOf(aEl);
        return (idx > 0) ? aList.get(idx-1) : null;
    }


    /**
     * Returns the element at the specified position in this list counting from 
     * the end.
     *
     * @param aIndex index of element to return counting from the end.
     * @return the element at the specified position in this list.
     * 
     * @throws IndexOutOfBoundsException if the index is out of range (index
     * 		  &lt; 0 || index &gt;= size()).
     */
    public static <T> T getB(List<T> aList, int aIndex) {
        return aList.get(aList.size() - aIndex - 1);
    }
    
    
    /**
     * Creates a copy of an array with extra empty items at the end.
     * @param aArray          the array to create copy of
     * @param aNrOfExtraItems number of extra items to add in addition to those copied.
     * @return an array X where:
     *    X.length = aArray.length + aNrOfExtraItems
     *    forall i in 0..aArray.length-1 . X[i] = aArray[i]
     *    forall i in 0..aNrOfExtraItems . X[aArray.length+i] = null
     */
    public static Object[] addExtraItems(Object[] aArray, int aNrOfExtraItems) {
        Object[] tmp = new Object[aArray.length+aNrOfExtraItems];       // automatically initialized to null??
        System.arraycopy(aArray,0,tmp,0,aArray.length);
        return tmp;
    }
    
    /**
     * Creates a copy of an array with extra empty items at the end.
     * @param aArray          the array to create copy of
     * @param aNrOfExtraItems number of extra items to add in addition to those copied.
     * @return an array X where:
     *    X.length = aArray.length + aNrOfExtraItems
     *    forall i in 0..aArray.length-1 . X[i] = aArray[i]
     *    forall i in 0..aNrOfExtraItems . X[aArray.length+i] = 0
     */
    public static int[] addExtraItems(int[] aArray, int aNrOfExtraItems) {
        int[] tmp = new int[aArray.length+aNrOfExtraItems];       // automatically initialized to 0's
        System.arraycopy(aArray,0,tmp,0,aArray.length);
        return tmp;
    }
    
    /**
     * Like List.add(int, T), but allows adding items beyond the current size
     * adding necessary number of null items.
     */
    public static <T> void listAdd(List<T> aList, int aPos, T aItem) {
        if (aPos >= aList.size()) {
            for (int i = aList.size(); i < aPos; i++)
                aList.add(null);
        }
        aList.add(aPos, aItem);
    }
    
    
//    public static  <T> int indexOf(ArrayList<T> aList, Comparator<T> aComp, int aStartIdx) {
//        int size = aList.size();
//		for (int i = aStartIdx; i < size; i++) {
//            if (aComp.compare(o1, o2) == 0) return i;
//        }
//        return -1;
//    }
    
// =============================================================================
// Sublists <editor-fold desc="Sublists">
// ==============================================================================
    
    
    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, exclusive.  (If
     * <tt>fromIndex</tt> and <tt>toIndex</tt> are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     *
     * @see #List.subList(int,int)
     */
    public static <T> List<T> subList(T[] aArray, int aFrom, int aTo) {
        return Arrays.asList(aArray).subList(aFrom,aTo);
    }
    
    /**
     * Returns a view of the portion of this list between the specified
     * <tt>fromIndex</tt>, inclusive.
     */
    public static <T> List<T> subList(T[] aArray, int aFrom) {
        return subList(aArray, aFrom,aArray.length);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] subArray(T[] aArray, int aFrom, int aTo) {
        T[] a = (T[])java.lang.reflect.Array.newInstance(aArray.getClass().getComponentType(), aTo-aFrom);
        return subList(aArray, aFrom, aTo).toArray(a);
    }
    
    
    /**
     * Returns a view of the portion of this list between the specified bounds,
     * removing the specified number of elements from each side.
     *
     * @param      aLTrim   nr of elements to remove from left,
     *     i.e. the beginning index (inclusive).
     * @param      aRTrim   nr of elements to remove from right,
     *     i.e. the end index when counting from the back (inclusive)
     * @return     the specified sublist.
     * @exception  IndexOutOfBoundsException  if <code>aLTrim</code> or
     * <code>aRTrim</code> are inappropriate
     */
    public static <T> List<T> subListB(List<T> aList, int aLTrim, int aRTrim) {
        return aList.subList(aLTrim, aList.size() - aRTrim);
    }
    
    
    /**
     * Returns a view of the portion of this list between the specified bounds,
     * removing the specified number of elements from each side.
     *
     * @param      aLTrim   nr of elements to remove from left,
     *     i.e. the beginning index (inclusive).
     * @param      aRTrim   nr of elements to remove from right,
     *     i.e. the end index when counting from the back (inclusive)
     * @return     the specified sublist.
     * @exception  IndexOutOfBoundsException  if <code>aLTrim</code> or
     * <code>aRTrim</code> are inappropriate
     */
    public static <T> List<T> subListB(T[] aArray, int aLTrim, int aRTrim) {
        return subListB(Arrays.asList(aArray), aLTrim, aRTrim);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] subArrayB(T[] aArray, int aLTrim, int aRTrim) {
        return subArray(aArray, aLTrim, aArray.length - aRTrim);
    }

// </editor-fold>

// =============================================================================
// Filtering <editor-fold desc="Filtering">
// ==============================================================================

    /**
     * Retains only items satifying a predicate.
     * 
     * @param <V>
     * @param <X>
     * @param aCol
     * @param aPredicate
     * @return
     */
    public static <V, X extends Iterable<V>> X retainAll(X aCol, Predicate<V> aPredicate) {
        return removeAll(aCol, Filters.neg(aPredicate));
    }

    /**
     * Retains only items satisfying a predicate.
     * 
     * @param <K>
     * @param <V>
     * @param aMap
     * @param aPredicate
     * @return
     */
    public static <K,V> Map<K,V> retainAll(Map<K,V> aMap, Predicate<V> aPredicate) {
        return removeAll(aMap, Filters.neg(aPredicate));
    }

    /**
     * Removes all items satifying a predicate.
     * 
     * @param <V>
     * @param <X> 
     * @param aCol collection (iterable object) of elements of type V
     * @param aPredicate predicate over object of type V
     * @return
     */
    public static <V, X extends Iterable<V>> X removeAll(X aCol, Predicate<V> aPredicate) {
        for (Iterator<V> i = aCol.iterator(); i.hasNext();) {
            V e = i.next();
            if (aPredicate.isOk(e))  {
                i.remove();
            }
        }
        return aCol;
    }
    
    /**
     * Removes all items satifying a predicate.
     * 
     * @param <K>
     * @param <V>
     * @param <X>
     * @param aMap
     * @param aPredicate
     * @return
     */
    public static <K,V> Map<K,V> removeAll(Map<K,V> aMap, Predicate<V> aPredicate) {
        for (Iterator<Map.Entry<K,V>> i = aMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry<K,V> e = i.next();
            if (aPredicate.isOk(e.getValue()))  {
                i.remove();
            }
        }
        return aMap;
    }
    
    
// =============================================================================
// Item collections <editor-fold desc="Item collections">
// ==============================================================================
    /** Use {@link Mapper} */
    @Deprecated
    public interface Itemizer<T,Q> {
        public T item(Q aBigItem);
    }

    /** Use {@link MappingIterator}. */
    @Deprecated
    private static class ItemIterator<T,Q> implements Iterator<T> {
        final private Itemizer<T,Q> mItemizer;
        final private Iterator<Q> mBigIterator;

        public ItemIterator(Itemizer<T,Q> aItemizer, Iterable<Q> aBigCollection) {
            mItemizer = aItemizer;
            mBigIterator = aBigCollection.iterator();
        }

        public boolean hasNext() {return mBigIterator.hasNext();}
        public T next() {return mItemizer.item(mBigIterator.next() );}
        public void remove() {mBigIterator.remove();}
    }

    /** Use {@link MappingIterable}. */
    @Deprecated
    public abstract static class ItemIterable<T,Q> implements Itemizer<T,Q> {
        final private Iterable<Q> mBigCollection;

        public abstract T item(Q aBigItem);

        public ItemIterable(Iterable<Q> aBigCollection) {
            mBigCollection = aBigCollection;
        }

        public Iterator<T> iterator() {
            return new ItemIterator<T,Q>(this, mBigCollection);
        }
    }


    /** Use {@link MappingCollection}. */
    @Deprecated
    public abstract static class ItemCollection<T,Q> extends AbstractCollection<T> implements Itemizer<T,Q> {
        final private Collection<Q> mBigCollection;

        public abstract T item(Q aBigItem);

        public ItemCollection(Collection<Q> aBigCollection) {
            mBigCollection = aBigCollection;
        }

        public Iterator<T> iterator() {
            return new ItemIterator<T,Q>(this, mBigCollection);
        }

        public int size() {
            return mBigCollection.size();
        }
    }

    /** Use {@link MappingList}. */
    @Deprecated
    public abstract static class ItemList<T,Q> extends AbstractList<T> implements Itemizer<T,Q> {
        final private List<Q> mBigCollection;

        public abstract T item(Q aBigItem);

        public ItemList(List<Q> aBigCollection) {
            mBigCollection = aBigCollection;
        }

        public T get(int aIdx) {
            return item( mBigCollection.get(aIdx) );
        }

        public Iterator<T> iterator() {
            return new ItemIterator<T,Q>(this, mBigCollection);
        }

        public int size() {
            return mBigCollection.size();
        }
    }    
// </editor-fold>

    
// =============================================================================
// Folding <editor-fold desc="Folding">
// ==============================================================================

    
    /**
     * Calculates the summ of all the elements of the specified array.
     * HO: aArray.fold(0,+);
     */
    public static int sum(int[] aArray) {
        int sum = 0;
        for(int e : aArray)
            sum += e;
        return sum;
    }

    /**
     * Calculates the summ of all the elements of the specified iterable.
     * HO: aArray.fold(0,+);
     */
    public static int sum(Iterable<Integer> aArray) {
        int sum = 0;
        for(Integer e : aArray)
            sum += e;
        return sum;
    }
    
    
// -----------------------------------------------------------------------------
// SB @ytodo appendable?
    
    /**
     * Fills the collection to a string builder. If an item is also collection,
     * it is printed using the same formatting, arrays are printed using Arrays.toString(..).
     *
     * @param aBuffer string builder to use
     * @param aCol collection to be printed
     * @param aL left bracket (e.g. "[")
     * @param aR right bracket (e.g. "]")
     * @param aSep separator of items (e.g. ", " or "\n" )
     * @param aEmpty string returned when the collection is empty (e.g. "[]" or "null")
     *
     * @see #toString(Collection, String, String, String, String)
     */
    public static StringBuilder fold(StringBuilder aBuffer, Iterable<?> aCol, String aL, String aR, String aSep, String aEmpty)	{
        Iterator i = aCol.iterator();
        if (! i.hasNext() ) { return aBuffer.append(aEmpty);}
        
        aBuffer.append(aL);
        itemToBuffer(aBuffer, i.next(), aL, aR, aSep, aEmpty);
        
        while( i.hasNext() ) {
            aBuffer.append(aSep);
            itemToBuffer(aBuffer, i.next(), aL, aR, aSep, aEmpty);
        }
        aBuffer.append(aR);
        
        return aBuffer;
    }
    
    private final static void itemToBuffer(StringBuilder aBuffer, Object aItem, String aL, String aR, String aSep, String aEmpty)	{
        if (aItem instanceof Iterable)
            fold(aBuffer, (Iterable)aItem, aL, aR, aSep, aEmpty);
        else if (aItem instanceof Object[])
            aBuffer.append( Arrays.toString((Object[])aItem) );
        else 
            aBuffer.append(String.valueOf(aItem));
    }
    
    
    /**
     * Prints (converts) a collection to a oneline string.
     * The collection is enclosed by brackets and items are separated by ', '.
     *
     * @param aCol collection to be printed
     * @param aAprxLength approximate length of the resulting string
     *      (it is better to overestimate, then underestimate)
     *
     * @see #toStringNl(Collection)
     * @see #toStringNl(Collection, String)
     * @see #toString(Collection, String, String, String, String)
     */
    public static String toString(Collection<?> aCol, int aAprxLength) {
        return toString(aCol, aAprxLength, "[", "]", ", ", "[]");
    }
    
    /**
     * Prints (converts) a collection to a oneline string.
     * The collection is enclosed by brackets and items are separated by ', '.
     *
     * @param aCol collection to be printed
     *
     * @see #toStringNl(Collection)
     * @see #toStringNl(Collection, String)
     * @see #toString(Collection, String, String, String, String)
     */
    public static String toString(Collection<?> aCol) {
        return toString(aCol, "[", "]", ", ", "[]");
    }
    
    /**
     * Prints (converts) the collection to a multiline string.
     * Each item is on a separate line.
     *
     * @param aCol collection to be printed
     *
     * @see #toString(Collection)
     * @see #toStringNl(Collection, String)
     * @see #toString(Collection, String, String, String, String)
     */
    public static String toStringNl(Collection aCol)	{
        return toStringNl(aCol, "");
    }
    
    /**
     * Prints (converts) the collection to a multiline string with indentation.
     * Each item is on a separate line indented by the specified string.
     *
     * @param aCol collection to be printed
     * @param aTab indenteation string
     *
     * @see #toString(Collection)
     * @see #toStringNl(Collection)
     * @see #toString(Collection, String, String, String, String)
     */
    public static String toStringNl(Collection aCol, String aTab)	{
        return toString(aCol, aTab, "", "\n" + aTab, "[]");
    }
    
    /**
     * Prints (converts) the collection to a string.
     *
     * @param aCol collection to be printed
     * @param aL left bracket (e.g. "[")
     * @param aR right bracket (e.g. "]")
     * @param aSep separator of items (e.g. ", " or "\n" )
     * @param aEmpty string returned when the collection is empty (e.g. "[]" or "null")
     *
     * @see #toString(Collection)
     * @see #toStringNl(Collection)
     * @see #toString(Collection, String, String, String, String)
     * @see #toStringNl(Collection, String)
     */
    public static String toString(Collection aCol, String aL, String aR, String aSep, String aEmpty)	{
        return toString(aCol, aCol.size()*4, aL, aR, aSep, aEmpty);
    }
    
    /**
     * Prints (converts) the collection to a string. If an item is also collection,
     * it is printed using the same formatting, arrays are printed using Arrays.toString(..).
     *
     * @param aCol collection to be printed
     * @param aAprxLength approximate length of the resulting string
     *      (it is better to overestimate, then underestimate)
     * @param aL left bracket (e.g. "[")
     * @param aR right bracket (e.g. "]")
     * @param aSep separator of items (e.g. ", " or "\n" )
     * @param aEmpty string returned when the collection is empty (e.g. "[]" or "null")
     *
     * @see #toString(Collection)
     * @see #toStringNl(Collection)
     * @see #toString(Collection, String, String, String, String)
     * @see #toStringNl(Collection, String)
     */
    public static String toString(Iterable<?> aCol, int aAprxLength, String aL, String aR, String aSep, String aEmpty)	{
        StringBuilder buf = new StringBuilder(aAprxLength);      
        return fold(buf, aCol, aL, aR, aSep, aEmpty).toString();
    }

    
    
//    private final static String itemToString(Object aItem, String aL, String aR, String aSep, String aEmpty)	{
//        if (aItem instanceof Collection)
//            return toString((Collection)aItem, aL, aR, aSep, aEmpty);
//        else if (aItem instanceof Object[])
//            return Arrays.toString((Object[])aItem);
//        else
//            return aItem.toString();
//    }
    
//
    // Extend to appendable
    
// =============================================================================
// Printer folding
// =============================================================================
    
     
    private static Printer cEmptyNull = new Printer() {
        public String toString(Object aItem) {
            return (aItem == null) ? "" : aItem.toString();
        }
    };
    
    @SuppressWarnings("unchecked")
    public static <T> Printer<T> emptyNullPrinter() {
        return (Printer<T>) cEmptyNull;
    }

    
    /**
     * Prints the collection to a string using the specified printer.
     *
     * @param aCol collection to print
     * @param aPrinter printer object used to print items, and specify separators, etc.
     * @todo eff modify as above
     */
    public  static <T> String toString(final Iterable<T> aCol, final ColPrinter<? super T> aPrinter)	{
        final Iterator<T> i = aCol.iterator();
        if (! i.hasNext() ) return aPrinter.mEmpty;
        
        final StringBuilder tmp = new StringBuilder();      // @todo initialize with some size??? Pass it as an argument??
        tmp.append(aPrinter.mL).append(aPrinter.toString(i.next()));
        
        while( i.hasNext() ) {
            tmp.append(aPrinter.mSep).append(aPrinter.toString(i.next()));
        }
        tmp.append(aPrinter.mR);
        
        return tmp.toString();
    }
    
    /*
     *
     * Uses empty strings for brackets and empty collection.
     * @deprecated
     */
    public  static <T> String toString(final Iterable<T> aCol, final Printer<? super T> aItemPrinter, final char aSep)	{
        final Iterator<T> i = aCol.iterator();
        if (! i.hasNext() ) return "";
        
        final StringBuilder tmp = new StringBuilder();      // @todo initialize with some size??? Pass it as an argument??
        tmp.append(aItemPrinter.toString(i.next()));
        
        while( i.hasNext() ) {
            tmp.append(aSep).append(aItemPrinter.toString(i.next()));
        }
        
        return tmp.toString();
    }
    
    /**
     * A convenience method printing the collection to a string using the specified item printer, separator, etc.
     *
     * @param aItemPrinter printer object used to print items
     * @param aCol collection to print
     * @todo eff modify as above
     */
    public  static <T> String toString(Iterable<T> aCol, Printer<? super T> aItemPrinter, String aL, String aR, String aSep, String aEmpty)	{
        return toString(aCol, new ColPrinter<T>(aL,aR,aSep,aEmpty,true).setItemPrinter(aItemPrinter));
    }
    
    
    /**
     * Prints (converts) the collection to a string using the specified printer
     * for items and separating items by spaces.
     * This is a convenience method for using ColPrinter.spaceSeparated(aItemPrinter) printer.
     *
     * @param aItemPrinter printer object used to print items
     * @param aCol collection to print
     * @todo eff modify as above
     */
    public  static <T> String toStringSpaceSep(Iterable<T> aCol, Printer<T> aItemPrinter)	{
        return toString(aCol, aItemPrinter, ' ');
    }

// </editor-fold>
    
}
