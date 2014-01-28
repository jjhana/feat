
package org.purl.jh.util.col;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Note: Table does not grow.
 * @author Administrator
 */
public class ConstTable<E> implements Table<E> {
    final int noOfRows;
    final int noOfCols;
    final List<E> mData;

    public ConstTable(int aRows, int aCols) {
        noOfRows = aRows;
        noOfCols = aCols;
        final int size = aRows*aCols;
        mData = new ArrayList<E>(size);

        // this is a stupid way of doing ArrayList.size = size (ArrayList.size is private)
        for (int i = 0; i < size; i++) {
            mData.add(null);
        }
    }

    public ConstTable(int aCols, Collection<E> aData) {
        final int size = aData.size();

        noOfRows = size / aCols;
        noOfCols = aCols;
        if (noOfCols * noOfRows != size) {
            throw new IllegalArgumentException("Data's size (" + size + " not divisible by # of cols (" + aCols + ")");
        }
        
        mData = new ArrayList<E>(aData);
    }    
    
    public int getNoOfRows() {
        return noOfRows;
    }
    
    public int getNoOfCols() {
        return noOfCols;
    }
    
    public E get(int aRow, int aCol) {
        return mData.get(getIdx(aRow,aCol));
    }

    public void set(int aRow, int aCol, E aItem) {
        mData.set(getIdx(aRow,aCol), aItem);
    }


    public boolean contains(Object aItem) {
        return mData.contains(aItem);
    }

    public Iterator<E> iterator() {
        return mData.iterator();
    }
    
    public boolean containsAll(Collection<?> aCol) {
        return mData.containsAll(aCol);
    }

    public Table<E> subTable(int fromRow, int toRow, int fromCol, int toCol) {
        throw new UnsupportedOperationException();
    }
    
//    int indexOf(Object o);
//    int lastIndexOf(Object o);
    

    private int getIdx(int aRow, int aCol) {
        if (aRow >= noOfRows) {
            throw new IndexOutOfBoundsException("Row index: " + aRow + ", # of rows: "+ noOfRows);
        }
        if (aCol >= noOfCols) {
            throw new IndexOutOfBoundsException("Col index: " + aCol + ", # of cols: "+ noOfCols);
        }
        return aRow*noOfCols+aCol;
    }

    
    
}
