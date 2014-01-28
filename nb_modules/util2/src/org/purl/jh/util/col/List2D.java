package org.purl.jh.util.col;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.purl.jh.util.Pair;

/**
 * Interface + impl.
 * @author Jirka
 */
public class List2D<E> { //implements List<List<E>> {
    private final List<List<E>> lists = new ArrayList<List<E>>();

    /**
     * Creates an initializes a new list2d. No elements are added.
     *
     * @param aRows number of rows in the created list2d
     */
    public List2D(final int aRows) {
        for (int i = 0; i < aRows; i++) {
            lists.add(new ArrayList<E>());
        }
    }

    /**
     * Creates a new list2d by copies elements from an existing one.
     * @param aList2D
     */
    public List2D(final List2D<? extends E> aList2D) {
        for (List<? extends E> list : aList2D.getLists()) {
            lists.add(new ArrayList<E>(list));
        }
    }

    /**
     * Creates a new list2d by copies elements from a collection of collections.
     * @param aCols
     */
    public List2D(final Iterable<? extends Collection<? extends E>> aCols) {
        for (Collection<? extends E> col : aCols) {
            lists.add(new ArrayList<E>(col));
        }
    }

    public List<List<E>> getLists() {
        return lists;
    }

    public List<E> getRow(int aRow) {
        return lists.get(aRow);
    }

    /**
     * Would not be in the API if list contained one parameter sublist function.
     * @param aRow
     * @param aStartCol
     * @return
     */
    public List<E> getSubRow(int aRow, int aStartCol) {
        final List<E> row = lists.get(aRow);
        return row.subList(aStartCol, row.size());
    }


    public E get(int aRow, int aCol) {
        return getRow(aRow).get(aCol);
    }

    public E set(int aRow, int aCol, E element) {
        return getRow(aRow).set(aCol, element);
    }

    public void addEmptyColumn(int idx) {
        for (int r = 0; r < lists.size(); r++) {
            getRow(r).add(idx, null);
        }
    }
    
    public IntInt indexOf(Object o) {
        for (int i = 0; i < lists.size(); i++) {
            final int idx = lists.get(i).indexOf(o);
            if (idx != -1) return new IntInt(i,idx);
        }
        return null;
    }

    public boolean contains(Object o) {
        return indexOf(o) != null;
    }

    /** Nr of rows */
    public int size() {
        return lists.size();
    }

    public boolean isEmpty() {
        return lists.isEmpty();
    }

    public int getLongestRowSize() {
        int longestRowLength = 0;

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).size() > longestRowLength) {
                longestRowLength = lists.get(i).size();
            }
        }

        return longestRowLength;
    }

    public int getLongestRowIdx() {
        int longestRowIdx = -1;
        int longestRowLength = 0;

        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).size() > longestRowLength) {
                longestRowLength = lists.get(i).size();
                longestRowIdx = i;
            }
        }

        return longestRowIdx;
    }


    public void clear() {
        for (int i = 0; i < lists.size(); i++) {
            lists.get(i).clear();
        }
    }

    public Collection<Pair<E,IntInt>> itemsWithPos() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public Collection<Pair<E,Integer>> itemsWithRows() {
        final List<Pair<E,Integer>> itemsWLayer = new ArrayList<Pair<E, Integer>>();

        for (int i = 0; i < lists.size(); i++) {
            final Integer layerIdx = new Integer(i);
            for (int j = 0; j < lists.get(i).size(); j++) {     // todo mapping and addAll?
                itemsWLayer.add(new Pair<E,Integer>(get(i, j), layerIdx));
            }
        }

        return itemsWLayer;
    }

    public void padWith(final E aPaddingObj) {
        final int maxLen = getLongestRowIdx();

        for (int i = 0; i < lists.size(); i++) {
            for (int j = lists.get(i).size(); j<maxLen; j++) {     
                lists.get(i).add(aPaddingObj);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(List<E> list : lists) {
            sb.append(Cols.toStringNl(list));
            sb.append("---");
        }
        return sb.toString();
    }




//    public boolean add(Object e) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void add(int index, Object element) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean addAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean addAll(int index, Collection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean containsAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public int indexOf(Object o) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Iterator iterator() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public int lastIndexOf(Object o) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ListIterator listIterator() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public ListIterator listIterator(int index) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean remove(Object o) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Object remove(int index) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean removeAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public boolean retainAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Object set(int index, Object element) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public List subList(int fromIndex, int toIndex) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Object[] toArray() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Object[] toArray(Object[] a) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

}
