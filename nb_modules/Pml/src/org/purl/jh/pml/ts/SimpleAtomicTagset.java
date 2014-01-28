package org.purl.jh.pml.ts;

/**
 * A tagset with a flat list of Tag objects.
 *
 * @author jirka
 */
public class SimpleAtomicTagset extends AtomicTagset<Tag<?>> {
    public SimpleAtomicTagset(String id, String aDescr, String aDomain, String lg) {
        super(id, aDescr, aDomain, lg);
    }

    @Override
    public Tag<?> createTag(String id, String descr, Object ... aParam) {
        return new Tag<>(this, id, descr);
    }

}
