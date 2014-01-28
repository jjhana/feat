/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.util.sgml;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.col.XCols;

/**
 *
 * @author jirka
 */
public class SgmlTags {
    public static Collection<SgmlTag> filterWithCore(Iterable<SgmlTag> aTags, String aCore) {
        return filterWithCore(aTags, Pattern.compile(aCore, Pattern.LITERAL));
    }

    public static Collection<SgmlTag> filterWithCore(Iterable<SgmlTag> aTags, Pattern aCorePattern) {
        final List<SgmlTag> filteredTags = XCols.newArrayList();
        
        for (SgmlTag tag : aTags) {
            if ( aCorePattern.matcher( tag.getCore() ).matches() ) {
                filteredTags.add(tag);
            }
        }

        return filteredTags;
    }

    public static SgmlTag oneWithCore(Iterable<SgmlTag> aTags, String aCore) {
        return oneWithCore(aTags, Pattern.compile(aCore, Pattern.LITERAL));
    }

    public static SgmlTag oneWithCore(Iterable<SgmlTag> aTags, Pattern aCorePattern) {
        for (SgmlTag tag : aTags) {
            if ( aCorePattern.matcher( tag.getCore() ).matches() ) return tag;
        }

        return null;
    }
}
