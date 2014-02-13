package org.purl.jh.util.str.transl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class Translators implements Translator {
    final List<Translator> translators = new ArrayList<Translator>();;

    public void add(Translator aTranslator) {
        translators.add(aTranslator);
    }

    public String translate(String aWord) {
        for (Translator t : translators) {
            aWord = t.translate(aWord);
        }
        return aWord;
    }
}

