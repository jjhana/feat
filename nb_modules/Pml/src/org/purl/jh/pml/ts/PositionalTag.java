package org.purl.jh.pml.ts;

import org.purl.jh.util.str.Strings;

/**
 *
 * @author jirka
 */
public class PositionalTag extends Tag<PositionalTagset> {

    public PositionalTag(PositionalTagset aTagset, String id, String descr) {
        super(aTagset, id, descr);
    }


    @Override
    public PositionalTagset getTagset() {
        return (PositionalTagset) super.getTagset();
    }

    public PositionalTag setSlot(char aSlotAbbr, char aVal) {
        final int slotIdx = getTagset().abbr2idx(aSlotAbbr);
        final String id = Strings.setCharAt(getId(), slotIdx, aVal);
        return getTagset().getTag(id);
    }


}
