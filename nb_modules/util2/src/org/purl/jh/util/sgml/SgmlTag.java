package org.purl.jh.util.sgml;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.purl.jh.util.str.Strings;


/**
 * Note: the object is immutable (is this good??)
 * @todo optimize
 * @author Jiri
 */
public class SgmlTag {
    final protected String mToken;
    
    protected String mCore;

    /**
     * Note: The format is not checked. E.g. it is assumed that the first character
     * is &lt; and the last one is &gt; plus the standard assumption about format 
     * of attributes, etc. If this is not true, the behavior of this class is 
     * unpredictable.
     */
    public SgmlTag(String aToken) {
        mToken = aToken;
    }


// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------

    /** Returns the whole tag as passed to the oonstructor */
    public String getmToken() {
        return mToken;
    }

    
    /**
     * Returns the getCore of the tag token.
     * E.g. if the token is <i>&lt;abcd xyz='1'&gt;</i>, <i>abcd</i> is returned.
     */
    public String getCore()      {
        if (mCore == null) extractCore();
        return mCore;
    }

    /**
     * A conveniece method for testing the core.
     */
    public boolean cEq(String aCore) {
        return getCore().equals(aCore);
    }
    
    public String getAttributesStr() {
        int spaceIdx = mToken.indexOf(' ');
        
        return (spaceIdx == -1) ? "" : Strings.substringB(mToken, spaceIdx+1, 1);
    }
    
    
    /**
     * Returns the specified getAttribute of the current tag token.
     * The token type is asserted to be a tag.
     * @param aName name of the getAttribute
     * @param aDef value to return if the getAttribute is not present
     * @return the value of the specified getAttribute; or <code>aDef</code> 
     * if the attribute is not present.
     *
     * E.g. <ul>
     * <li>current token: <i>&lt;abcd xyz='1'&gt;</i>; getAttribute("xyz", "0") returns "1".
     * <li>current token: <i>&lt;abcd&gt;</i>; getAttribute("xyz", "0") returns "0".
     * </ul>
     */
    public String getAttribute(String aName, String aDef)      {   // add method passing " attr="
        int start = mToken.indexOf(' ' + aName + '=');
        if (start == -1) return aDef;
        
        start = mToken.indexOf('\"', start)+1;
        int end = mToken.indexOf('\"', start);
        if (start != 0 && end != -1) return StringEscapeUtils.unescapeHtml4(mToken.substring(start,end));
    
        Pattern p = attrPattern(aName);
        return getAttribute(p, aDef);
    }

    public String getAttribute(Pattern aPattern, String aDef)      {   
        final Matcher m = aPattern.matcher(mToken);

        if (!m.matches()) return aDef;
        for (int i=1; i< m.groupCount() + 1; i++) {
            String s = m.group(i);
            if ( s!=null ) return StringEscapeUtils.unescapeHtml4(s);
        }

        return null;
    }

    public static Pattern attrPattern(String aName) {
        return Pattern.compile(".*\\s+" + aName + "\\s*=\\s*(?:\"(.*?)\"|'(.*?)'|(?:(.*?)[\\s>])).*");
    } 
    
    /**
     * Returns the specified getAttribute of the current tag token.
     * The token type is asserted to be a tag.
     * @param aName name of the getAttribute
     * @return the value of the specified getAttribute (or <code>null</code>)
     * E.g. <ul>
     * <li>current token: <i>&lt;abcd xyz='1'&gt;</i>; getAttribute("xyz") returns "1".
     * <li>current token: <i>&lt;abcd&gt;</i>; getAttribute("xyz") returns <code>null</code>.
     * </ul>
     */
    public String getAttribute(String aName)      {
        return getAttribute(aName, null);
    }
    
// @todo maybe replace for specific attr/values by regex functions, maybe even getting values? 
// needs measuring    
     //static Pattern selectedPtr = Pattern.compile("das=\"+\"", Pattern.LITERAL);
     //static Pattern notSelectedPtr = Pattern.compile("das=\"-\"", Pattern.LITERAL);

    
    /**
     * 
     * @param aName
     * @param aValue value of the attribute; automatically escaped
     * @return 
     */
    public SgmlTag setAttribute(String aName, String aValue) {
        StringBuilder newToken = new StringBuilder(2*mToken.length());
        int start = mToken.indexOf(' ' + aName + '=');
        
        aValue = StringEscapeUtils.escapeHtml4(aValue);
        
        if (start == -1) {
            newToken.append(Strings.substringB(mToken,0, 1)).append(' ');
            newToken.append(aName).append("=\"").append(aValue).append("\">");
        }
        else {
            int startQuote = mToken.indexOf('\"', start);
            int endQuote   = mToken.indexOf('\"', startQuote+1);
            newToken.append(mToken.substring(0,startQuote+1));
            newToken.append(aValue);
            newToken.append(mToken.substring(endQuote));
        }
        
        return constructorx( newToken.toString(), mCore );
    }

//    public String setAttribute(String aName, boolean aValue) {
//        return setAttribute(aName, aValue ? "+" : "-");
//    }
    
    /**
     * Does nothing if the attribute is not present. 
     * @todo using regular expr.?
     */
    public SgmlTag removeAttribute(String aName) {
        int start = mToken.indexOf(' ' + aName + '=');      // optimize indexOf(char, String, char)
        if (start == -1) return this;
        
        int startQuote = mToken.indexOf('\"', start);
        int endQuote   = mToken.indexOf('\"', startQuote+1);
        String newToken = mToken.substring(0,start) + mToken.substring(endQuote+1);
        return constructorx( newToken, mCore );
    }
    

// -----------------------------------------------------------------------------
// Edits ??immutable
// -----------------------------------------------------------------------------
    /**
     * Changes the core of the tag.
     * @mutable
     */
    public SgmlTag setCore(String aNewCore) {
        int spaceIdx = mToken.indexOf(' ');
        
        if (spaceIdx == -1)
            return constructorx( '<' + aNewCore + '>', aNewCore );
        else
            return constructorx( '<' + aNewCore + mToken.substring(spaceIdx), aNewCore );
    }

// -----------------------------------------------------------------------------
// Other functions
// -----------------------------------------------------------------------------

    public String toString() {
        return mToken;
    }
// -----------------------------------------------------------------------------    
// Implementation
// -----------------------------------------------------------------------------    
    
    private void extractCore() {
        int spaceIdx = mToken.indexOf(' ');
        mCore = (spaceIdx == -1) ? Strings.substringB(mToken, 1, 1) : mToken.substring(1, spaceIdx);
    }

    protected SgmlTag constructor(String aToken) {
        return new SgmlTag(aToken);
    }
    
    public SgmlTag constructorx(String aToken, String aCore) {
        SgmlTag t = constructor(aToken);
        t.mCore = aCore;
        return t;
    }
}
