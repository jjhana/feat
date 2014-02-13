package org.purl.jh.feat.layered;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.openide.util.Lookup;

/**
 * Support for spellchecking.
 * This is very experimental.
 * @author j
 */
public class Spellchecker {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Spellchecker.class);
    /**
     *
     * @param lg required language.
     * @return
     */
    public static Dictionary init(String lg) {
        DictionaryProvider dictionaryProvider = null;
        for (DictionaryProvider dp : Lookup.getDefault().lookupAll(DictionaryProvider.class)) {
            log.info("DictionaryProvider: %s", dp);
            dictionaryProvider = dp;
        }
        if (dictionaryProvider != null) {
            Dictionary dict = dictionaryProvider.getDictionary(new Locale("cs"));       // todo make configurable
            return new XDictionary(dict);
        }
        else {
            return new NoDictionary();
        }
    }

    static class NoDictionary implements Dictionary {

        public ValidityType validateWord(CharSequence word) {
            return ValidityType.VALID;
        }

        public List<String> findValidWordsForPrefix(CharSequence word) {
            return Collections.emptyList();
        }

        public List<String> findProposals(CharSequence word) {
            return Collections.emptyList();
        }

    }


    static class XDictionary implements Dictionary {
        private Dictionary dict;

        private XDictionary(Dictionary dict) {
            this.dict = dict;
        }

        private static Pattern wordPattern = Pattern.compile("\\p{L}+");

        public ValidityType validateWord(CharSequence word) {
            if (dict == null) return ValidityType.VALID;

            if (wordPattern.matcher(word).matches()) return dict.validateWord(word);

            return ValidityType.VALID;
        }

        public List<String> findValidWordsForPrefix(CharSequence word) {
            if (dict == null) return Collections.emptyList();
            return dict.findValidWordsForPrefix(word);

        }

        public List<String> findProposals(CharSequence word) {
            if (dict == null) return Collections.emptyList();
            return filter(word, dict.findProposals(word));

        }

        private List<String> filter(CharSequence word, List<String> aList) {
            if (word.length() == 0) return aList;

            final List<String> list = new ArrayList<>();

            // todo if match any w/o diacritics, suggest only that
            // todo

            boolean isLowerCase = Character.isLowerCase(word.charAt(0));
            for (String str : aList) {
                if (Character.isLowerCase(str.charAt(0)) == isLowerCase) list.add(str);
            }

            Collections.sort(list);
            return list;
        }

    }

}
