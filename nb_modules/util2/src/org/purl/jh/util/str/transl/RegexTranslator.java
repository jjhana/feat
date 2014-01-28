package org.purl.jh.util.str.transl;

import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class RegexTranslator implements Translator {
    private final Pattern match;
    private final String replacement;

    public RegexTranslator(Pattern aMatch, String aReplacement) {
        match = aMatch;
        replacement = aReplacement;
    }

    public RegexTranslator(String aMatch, String aReplacement) {
        this(Pattern.compile(aMatch), aReplacement);
    }

    public String translate(String aWord) {
        return match.matcher(aWord).replaceAll(replacement);
    }
}
