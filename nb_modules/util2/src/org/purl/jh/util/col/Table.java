
package org.purl.jh.util.col;

import java.util.Collection;

/**
 *
 * @author Administrator
 */
public interface Table<E> extends Iterable<E> {

    int getNoOfRows();
    int getNoOfCols();
    
    public E get(int aRow, int aCol);
    public void set(int aRow, int aCol, E aItem);

    public boolean contains(Object aItem);
    public boolean containsAll(Collection<?> aCol);    

    Table<E> subTable(int fromRow, int toRow, int fromCol, int toCol);
    
    //    E[][] toArrays();
//    <T> T[][] toArrays(T[][] a);


//    boolean add(E e);
//    boolean remove(Object o);
//    boolean containsAll(Collection<?> c);
//    boolean addAll(Collection<? extends E> c);
//    boolean addAll(int index, Collection<? extends E> c);
//    boolean removeAll(Collection<?> c);
//    boolean retainAll(Collection<?> c);

//    void clear();
//
//    E get(int index);

//    E set(int index, E element);
//    void add(int index, E element);
//    E remove(int index);


//    int indexOf(Object o);
//    int lastIndexOf(Object o);


//    ListIterator<E> listIterator();
//    ListIterator<E> listIterator(int index);

    //List<E> subList(int fromIndex, int toIndex);
    
}