package org.purl.jh.util;

/**
 * Array support (the same kind as java.util.Arrays)
 * @todo move to *.util.col
 *
 * @author Jiri
 */
public final class Arr {
    private Arr() {}
   
    public static <T> T[] asArray(T ... aItems) {
        return aItems;
    }

    /**
     * Returns a new longer array having a new item at the end
     * @param <T> type of the items in the arrays and of the item to be added
     * @param aArray array to add to
     * @param aNewItem item to add
     * @return new array, containing the items of aArray followed by aNewItem
     */
    public static <T> T[] addItem(T[] aArray, T aNewItem) {
    	T[] newArray = grow(aArray, 1);
        newArray[aArray.length] = aNewItem;
        return newArray;
    }

    /**
     * Returns a new longer array having new items at the end
     * @param <T> type of the items in the arrays and of the items to be added
     * @param aArray array to add to
     * @param aNewItems items to add
     * @return new array, containing the items of aArray followed by aNewItems
     */
    public static <T> T[] addItems(T[] aArray, T ... aNewItems) {
    	T[] newArray = grow(aArray, aNewItems.length);
        System.arraycopy(newArray, aArray.length, newArray, 0, aNewItems.length);
        return newArray;
    }
    
    /**
     * Returns a new longer array having all the items of the original array followed by nulls.
     * 
     * @param <T> type of the items in the arrays and of the items to be added
     * @param aArray array to grow
     * @param aDelta number of additional slots to add
     * @return new array containing all the items of aArray followed by aDelta nulls
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] grow(T[] aArray, int aDelta) {
        int oldLen = aArray.length;
        T[] newArray = (T[])java.lang.reflect.Array.newInstance(aArray.getClass().getComponentType(), oldLen+aDelta);
        System.arraycopy(aArray, 0, newArray, 0, oldLen);
        return newArray;
    }
 
    
}
