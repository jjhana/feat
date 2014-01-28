package org.purl.jh.pml.ts;

import java.util.List;
import org.purl.jh.util.Pair;

/**
 *
 * @author jirka
 */
public class PositionalTagset extends Tagset<PositionalTag> {


    public PositionalTagset(String id, String aDescr, String aDomain, String lg) {
        super(id, aDescr, aDomain, cPositional, lg);
    }

    @Override
    public PositionalTag createTag(String id, String descr, Object ... aObjs) {
        return new PositionalTag(this, id, descr);
    }

    @Override
    public List<String> getStrTags() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PositionalTag getTag(String aId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PositionalTag> getTags() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PositionalTag getUnknownTag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }




    public String getSlotAbbrs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int abbr2idx(char aSlotAbbr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Pair<Character, String>> getSlotVals(char aSlotAbbr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Character> getSlots(char aSubPos) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public Tag setSlot(Tag aTag, char aSlotAbbr, char aVal) {
//        int slotIdx = 0;
//        return
//        ;
//    }

    public String getSlotName(char aSlotCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> getSlotValNames(char aSlotCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
