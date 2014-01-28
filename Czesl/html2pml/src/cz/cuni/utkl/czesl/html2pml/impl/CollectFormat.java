package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;


/**
 * Collects format from the html element tree.
 * See {@link #go()}.
 */
public class CollectFormat {

    /** Processed document (could be R/O)*/
    private final StyledDocument doc;

    /**
     * Resulting format bitmaps. Each type-formatting is associated with a bitmap
     * indicating which characters have it and which not.
     */
    private final Map<Format.Type, BitSet> formatMap = new EnumMap<Format.Type, BitSet>(Format.Type.class);


    public CollectFormat(StyledDocument aDoc) {
        doc = aDoc;
        for (Format.Type format : Format.Type.values()) {
            formatMap.put(format, new BitSet());
        }
    }

    /**
     * Collects format from the html element tree.
     *
     * @return map from format-types to bitmaps indicating where the format is
     * used.
     * Each type-formatting is associated with a bitmap indicating which
     * characters have it and which not.
     */
    public Map<Format.Type, BitSet> go() {
        collectFormat(doc.getRootElements()[0]);

//        System.out.println("Collected format map");
//        System.out.println(formatMap);

        return formatMap;
    }

    int level = 0;

    private void collectFormat(final Element aElement) {
        level++;
        //System.out.println("" + i + " e=" + aElement);
        if (!aElement.isLeaf()) {
            for (int i = 0; i < aElement.getElementCount(); i++) {
                collectFormat( aElement.getElement(i) );
            }
        }
        else if (aElement.getName().equals("content")) {
            final int from = aElement.getStartOffset();
            final int to = aElement.getEndOffset();
            final AttributeSet attrs = aElement.getAttributes();
            final List keys = Collections.list(attrs.getAttributeNames());

            for (Object keyo : keys) {
                String key = keyo.toString().toLowerCase();
//                System.out.printf("format: '%s', from=%d, to=%d, attrs=%s\n", key, from, to, attrs.getAttribute(keyo));
                if (!Format.recognized().contains(key)) {
                    continue;
                }
                Format.Type type = Format.Type.fromTag(key);
//                System.out.println("   type: " + type);

                boolean set = true;
                if (key.equals("foreground")) {
                    set = attrs.getAttribute(keyo).equals( java.awt.Color.RED);
                }

//                System.out.println("   type: " + type + "  " + set);
                formatMap.get(type).set(from, to, set);
            }
        }
        level--;
    }
}
