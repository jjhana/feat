package org.purl.jh.pml.ts;

import java.util.List;
import java.util.Map;
import org.purl.jh.util.col.XCols;

/**
 *
 * @todo consider separating API and implementation
 * @author jirka
 */
public class TagsetRegistry {
    /** default instance (created by the class loader, so no synchronization needed) */
    private static TagsetRegistry instance = new TagsetRegistry();

    public static TagsetRegistry getDefault() {
        return instance;
    }

    private final Map<String,Tagset<?>> tagsets = XCols.newHashMap();

    public Map<String, Tagset<?>> getTagsets() {
        return tagsets;
    }

    public Tagset<?> getTagset(String aId, String version) {
        return tagsets.get(aId);    // todo take version into account
    }

    public void addTagset(Tagset<?> aTagset) {
        tagsets.put(aTagset.getId(), aTagset);

        // todo keep maps of common properties (morph/synt)
    }

    public List<Tagset<?>> getTagsets(String aProperty, String aValue) {
        final List<Tagset<?>> tss = XCols.newArrayList();

        for (Tagset<?> ts : tagsets.values()) {
            if ( aValue.equals( ts.getProperties().get(aProperty) )) {
                tss.add(ts);
            }
        }

        return tss;
    }

}
