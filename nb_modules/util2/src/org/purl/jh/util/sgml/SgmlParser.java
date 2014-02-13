package org.purl.jh.util.sgml;

import org.purl.jh.util.*;
import java.util.*;
import org.purl.jh.util.err.Err;

/**
 * @todo? two classes (+ a common superclass) one for disambiguated one for normal (share interface with TNT)
 * @todo? inits 
 * @todo make consistent with SgmlTokenizer, etc.
 * @deprecated !!! 
 * 
 */
public final class SgmlParser {
	String mString;
    int mCur;
    String mTagStart;
    int mTagStartLen;
    
// -----------------------------------------------------------------------------    
// Configuration
// -----------------------------------------------------------------------------    

    /**
     * Defaults:
     * <ul>
     * <li>Reading only FDLines (see setOnlyFDLines(boolean))
     * <li>filling lemma and tag (see ..)
     * <li>Using sgml tags: <l> for lemma, <t> for tag, <MMt> for ambiguous tags (see ...)
     * </ul>
     */
    public SgmlParser(String aString) throws java.io.IOException {
    	mString = aString;
    }

    
   
// -----------------------------------------------------------------------------    
// 
// -----------------------------------------------------------------------------    
    
    public String extractByCore(String aTag) {
        int tagStartIndex = mString.indexOf('<' + aTag);
        if (tagStartIndex == -1) return null;

        int tagEndIndex = mString.indexOf('>', tagStartIndex + 1); 
        assert tagEndIndex != -1 : aTag + " : " + mString;
        
        int endIndex = mString.indexOf('<', tagEndIndex + 1);
        
        return (endIndex != -1) ? mString.substring(tagEndIndex + 1, endIndex) : mString.substring(tagEndIndex + 1).trim();
    }

    public String extract(String aTag) {
        int startIndex = mString.indexOf(aTag);
        if (startIndex == -1) return null;
        startIndex += aTag.length();
        
        int endIndex = mString.indexOf("<", startIndex);
        
        return (endIndex != -1) ? mString.substring(startIndex, endIndex) : mString.substring(startIndex).trim();
    }
    
    public String extract(String aTag, int aStartIdx) {
        int startIndex = mString.indexOf(aTag, aStartIdx) + aTag.length();
        assert (startIndex != -1 + aTag.length()) : aTag + " : " + aStartIdx + " " + mString;
        
        int endIndex = mString.indexOf("<", startIndex);
        
        return (endIndex != -1) ? mString.substring(startIndex, endIndex) : mString.substring(startIndex).trim();
    }
    
//    public List<Tag> extractTags(String aSgmlTag)  throws MException {
//       return Tags.fromStrings(extractValues(aSgmlTag));
//    }   

    public List<String> extractValues(String aTagCore)  {
        List<String> values = new ArrayList<String>();
        mTagStart = '<' + aTagCore;
        mTagStartLen = mTagStart.length();
        
        for(mCur = 0; mCur != -1;) {
            String value = nextVal();
            if (value == null) break;
            values.add(value);
        }
        return values;
    }

    protected String nextVal()  {
        // --- find the next tag ---
        for(;;) {
            mCur = mString.indexOf(mTagStart, mCur);
            if (mCur == -1) return null;
            //System.out.println("A:" + mString.substring(mCur));
            char nextChar = mString.charAt(mCur+mTagStartLen);
            if (nextChar == '>' || nextChar == ' ' ) break;
            mCur++;
        }
        //System.out.println("B:" + mString.substring(mCur));

        mCur = mString.indexOf('>', mCur+mTagStartLen)+1;
        Err.assertE(mCur != 0, "@todo");
        //System.out.println("C:" + mString.substring(mCur));

        // --- get the value following the tag ---
        int endIndex = mString.indexOf('<', mCur);
        String tmp = (endIndex != -1) ? mString.substring(mCur, endIndex) : mString.substring(mCur).trim();

        mCur = endIndex;

        return tmp;
    }
   
}
    
