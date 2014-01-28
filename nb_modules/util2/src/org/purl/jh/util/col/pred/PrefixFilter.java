package org.purl.jh.util.col.pred;

/**
 * Filters items, e.g. in a list.
 *
 * @param <T> type of items to filter, uses their toString to convert to string to
 * check.
 * @author Jirka
 */
public class PrefixFilter<T> extends StringFilter<T> {
    //private boolean caseSensitive;


    public PrefixFilter() {
        super("");
    }

    public PrefixFilter(String aItemPrefix) throws java.util.regex.PatternSyntaxException {
        super(aItemPrefix);
        setString(aItemPrefix);      // setString in super does not call the overriden method.
    }

// =============================================================================
// Attributes
// =============================================================================


    /**
     * Notifies listeners.
     *
     * @param aItemPrefix ; null means .* (for initial settings, when layer, etc is nto yet known )
     */
    @Override
    public void setString(String aItemPrefix) {
        if (aItemPrefix == null) {
            aItemPrefix = "";
        }
        super.setString(aItemPrefix);
    }


//    public boolean getCaseSensitive() {
//        return !caseSensitive;
//    }
//
//    public void setCaseSensitive(boolean aCaseSensitive) {
//        caseSensitive = aCaseSensitive;
//        updated();
//    }

    public boolean isOk(T aItem) {
        if (aItem == null) return mString.equals("");

        return aItem.toString().startsWith(mString);
    }

    /**
     * Returns the prefix corresponding to this filter.
     */
    @Override
    public String toString() {
        return mString;
    }
}
