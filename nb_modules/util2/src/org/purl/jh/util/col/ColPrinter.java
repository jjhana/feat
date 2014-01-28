package org.purl.jh.util.col;

import java.util.Arrays;
import java.util.Map;
import org.purl.jh.util.str.pp.Printer;


/**
 * Object used by toString(Printer, Collection) to print collection items.
 * This class is supposed to be subclassed to get the required behavior.
 * The subclasses usually override the method toString(Object).
 */
public class ColPrinter<T> {
    final String mL;
    final String mR;
    final String mSep;
    final String mEmpty;
    Printer<? super T> mItemPrinter;
    final boolean mRecursive;

// =============================================================================
// Basic Item Printers
// =============================================================================

    public static Printer directItemPrinter() {
        if (cDirectItemPrinter == null)
            cDirectItemPrinter = new DirectItemPrinter();
        
        return cDirectItemPrinter;
    }
    
    static DirectItemPrinter cDirectItemPrinter;
    
    public static class DirectItemPrinter<T> implements Printer<T> {
        public String toString(T aItem)	{
            return String.valueOf(aItem);
        }
    }

    public static class DecoratorItemPrinter<T> implements Printer<T> {
        String mBeforeItem;
        String mAfterItem;
                
        public DecoratorItemPrinter(String aBeforeItem, String aAfterItem) {
            mBeforeItem = aBeforeItem;
            mAfterItem = aAfterItem;
        }
        
        public String toString(T aItem)	{
            return mBeforeItem + String.valueOf(aItem) + mAfterItem;
        }
    }

    
// =============================================================================
// Factories
// =============================================================================
    
    public static <X> ColPrinter<X> spaceSeparated(Printer<X> aItemPrinter) {
        return new ColPrinter<X>("[", "]", " ", "", true).setItemPrinter(aItemPrinter);
    }

    public static <X> ColPrinter<X> spaceSeparated() {
        return new ColPrinter<X>("[", "]", " ", "", true).setItemPrinter(directItemPrinter());
    }
    
    

    public static <K,V> ColPrinter<Map.Entry<K,V>> map() {
        return new ColPrinter<Map.Entry<K,V>>() {
            public String toString(Map.Entry<K,V> aE) 
                {return aE.getKey().toString() + ':' + aE.getValue().toString();} 
        };
    }
        
// =============================================================================
// 
// =============================================================================
    
    public ColPrinter<T> setItemPrinter(Printer<? super T> aItemPrinter) {
        mItemPrinter = aItemPrinter;
        return this;
    }
    
    /**
     * Creates a standard printer - uses '[' and ']' as parens, ", " as an separator,
     * and "[]" as an empty collection.
     * That means a collection of number 1,2,3 is printed as [1,2,3]
     * 
     */
    public ColPrinter()	{
        this("[", "]", ", ", "[]", true);
    }

    public ColPrinter(String aL, String aR, String aSep, String aEmpty)	{
        this(aL, aR, aSep, aEmpty, true);
    }    
    /**
     * Creates a customized printer object.
     * The constructor setups strings used for printing various parts of the 
     * collection.
     *
     * @param aL    left paren
     * @param aR    right paren 
     * @param aSep  separator between items
     * @param aEmpty string used for an empty collection 
     */
    public ColPrinter(String aL, String aR, String aSep, String aEmpty, boolean aReccursive)	{
        mL = aL;
        mR = aR;
        mSep = aSep;
        mEmpty = aEmpty;
        mRecursive = aReccursive;
    }


    /**
     * Returns a string representation of the specified item.
     * This implementation of the method calls item printer's toString method.
     * This method is the most likely method to be overriden in custom Printers.
     *
     * @param aItem item be converted to a string. Should not be a collection or array. 
     * @return a string representation of the specified item
     */
    @SuppressWarnings("unchecked")
    public String toString(T aItem)	{
        if (mRecursive) {
            if (aItem instanceof Iterable)
                return Cols.toString((Iterable)aItem, this);
            else if (aItem instanceof Object[])
                return Cols.toString( Arrays.asList(aItem), this);
        }
        
        return mItemPrinter.toString(aItem);
    }
}
