package org.purl.jh.util.col.pred;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.purl.jh.util.str.Strings;

/**
 * Filters items, e.g. in a list.
 *
 * @param <T> type of items to filter, uses their toString to conver to string to
 * pass to the regex.
 * @author Jirka
 */
public class RegexFilter<T> extends StringFilter<T> {
    private Pattern pattern;
    private boolean caseSensitive;


    public RegexFilter() {
        super(null);
    }

    /** Creates a new instance of QFilter */
    public RegexFilter(String aItemRegex) throws java.util.regex.PatternSyntaxException {
        super(aItemRegex);
        setString(aItemRegex);      // setString in super does not call the overriden method.
    }

// =============================================================================
// Attributes
// =============================================================================


    /**
     * Notifies listeners.
     *
     * @param aItemPrefix ;null means .* (for initial settings, when layer, etc is nto yet known )
     */
    @Override
    public void setString(String aItemRegex) {
        if (aItemRegex == null) {
            aItemRegex = ".*";
        }
        else if (aItemRegex.length() > 0 && Strings.lastChar(aItemRegex) == '$') {
            aItemRegex = Strings.removeTail(aItemRegex, 1);   // the whole item
        }
        else {
            aItemRegex += ".*";  // prefix
        }
        super.setString(aItemRegex);
    }


    public boolean getCaseSensitive() {
        return !caseSensitive;
    }

    public void setCaseSensitive(boolean aCaseSensitive) {
        caseSensitive = aCaseSensitive;
        updated();
    }

// =============================================================================
//
// =============================================================================

    @Override
    public void updated() {
        int flags = 0;
        if (!caseSensitive) {
            flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        }

        Pattern tmpPattern;
        try {
            tmpPattern = Pattern.compile(mString, flags);
        }
        catch(PatternSyntaxException e) {
            mString = pattern.toString();  // restore the string to be valid
            return; // keep the old filter (this one is probably not yet finished)
        }

        // --- the new filter-string is okay, update the filter ---
        pattern = tmpPattern;

        super.updated();
    }

    public boolean isOk(T aItem) {
        if (mString.equals(".*")) return true;

        return aItem != null && pattern.matcher( aItem.toString() ).matches();
    }

    /**
     * Returns the regex corresponding to this filter.
     */
    @Override
    public String toString() {
        return pattern.toString();
    }
}
