package org.purl.jh.pml.ts;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.col.MappingList;

/**
 * A tagset with a flat list of T objects.
 *
 * @author jirka
 */
public abstract class AtomicTagset<T extends Tag<?>> extends Tagset<T> {
    private final List<T> tags;
    private final List<T> tagsConst;
    private final Map<String,T> id2tags = XCols.newHashMap();

    public AtomicTagset() {
        tags      = XCols.newArrayList();
        tagsConst = Collections.unmodifiableList(tags);
    }
    
    
    public AtomicTagset(String id, String aDescr, String aDomain, String lg) {
        super(id, aDescr, aDomain,cAtomic, lg);

        tags      = XCols.newArrayList();
        tagsConst = Collections.unmodifiableList(tags);
    }

    @Override
    public abstract T createTag(String id, String descr, Object ... aParam);

    /**
     * Immutable list of all tags of the tagset.
     * @return
     */
    @Override
    public List<T> getTags() {
        return tagsConst;
    }

    @Override
    public List<String> getStrTags() {
        return new MappingList<>(tagsConst, T.<T>tag2id());
    }

    @Override
    public synchronized void add(T aTag) {
        tags.add(aTag);
        id2tags.put(aTag.getId(), aTag);
        //return this;
    }

    @Override
    public synchronized AtomicTagset<T> addAll(List<? extends T> aTags) {
        for (T tag : aTags) {
            tags.addAll(tags);
            id2tags.put(tag.getId(), tag);
        }

        return this;
    }

    @Override
    public synchronized AtomicTagset<T> remove(T aTag) {
        tags.remove(aTag);
        id2tags.remove(aTag.getId());

        return this;
    }


    @Override
    public T getUnknownTag() {
        return tags.get(0);
    }

    @Override
    public T getTag(String aId) {
        return id2tags.get(aId);
    }
}
