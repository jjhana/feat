package org.purl.jh.feat.profiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.Data;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.DictionaryProvider;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.openide.util.Lookup;

/**
 * Support for spellchecking.
 */
public class Spellchecker {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Spellchecker.class);

    public final static Dictionary EMPTY_DICTIONARY = new Dictionary() {
        @Override
        public ValidityType validateWord(CharSequence word) {
            return ValidityType.VALID;
        }

        @Override
        public List<String> findValidWordsForPrefix(CharSequence word) {
            return Collections.emptyList();
        }

        @Override
        public List<String> findProposals(CharSequence word) {
            return Collections.emptyList();
        }
    };
    
    /**
     * Returns the first dictionary for the specified language.
     * @param lg required language.
     * @return a dictionary for the specified language. If none is found, an empty dictionary is returned
     */
    public static Dictionary init(String lg) {
        for (DictionaryProvider dp : Lookup.getDefault().lookupAll(DictionaryProvider.class)) {
            log.info("DictionaryProvider: %s", dp);
            if (dp != null) {
                Dictionary dict = dp.getDictionary(new Locale(lg));       
                if (dict != null) return new XDictionary(dict);
            }
        }

        return EMPTY_DICTIONARY;
    }

    @Data
    static class XDictionary implements Dictionary {
        private final Dictionary dict;

        private final static Pattern wordPattern = Pattern.compile("\\p{L}+");

        @Override
        public ValidityType validateWord(CharSequence word) {
            if (dict == null) return ValidityType.VALID;

            if (wordPattern.matcher(word).matches()) return dict.validateWord(word);

            return ValidityType.VALID;
        }

        @Override
        public List<String> findValidWordsForPrefix(CharSequence word) {
            if (dict == null) return Collections.emptyList();
            return dict.findValidWordsForPrefix(word);

        }

        @Override
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
