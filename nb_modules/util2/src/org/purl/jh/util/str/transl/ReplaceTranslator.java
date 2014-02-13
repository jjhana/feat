package org.purl.jh.util.str.transl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class ReplaceTranslator extends RegexTranslator {
    public ReplaceTranslator(String aOrig, String aReplacement) {
        super( Pattern.compile(aOrig, Pattern.LITERAL), Matcher.quoteReplacement(aReplacement.toString()) );
    }
}
