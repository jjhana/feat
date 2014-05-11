package org.purl.jh.feat.profiles;

import org.purl.jh.pml.ts.AtomicTagset;

/**
 *
 * @todo Should inherit from some kind of abstract atomic tagset
 * @author jirka
 */
public class ErrorTagset extends AtomicTagset<ErrorTag> {

    public ErrorTagset() {
    }
    
    public ErrorTagset(String id, String descr) {
        super(id, descr, "errorAnnotation", null);
    }


    @Override
    public ErrorTag createTag(String id, String descr, Object ... aParam) {
        return createTagI(id, descr,  (Boolean)aParam[0],  (Boolean)aParam[1], (String)aParam[2], (Integer)aParam[3], (Integer)aParam[4], (Integer)aParam[5], (Integer)aParam[6], (Integer)aParam[7], (Integer)aParam[8]);
    }

    public ErrorTag createTagI(String tag, String comment, boolean deprecated, boolean auto, String menu, int minLowerLegs, int maxLowerLegs, int minUpperLegs, int maxUpperLegs, int minLinks, int maxLinks) {
        return new ErrorTag(this, tag, comment, deprecated, auto, menu, minLowerLegs, maxLowerLegs, minUpperLegs, maxUpperLegs, minLinks, maxLinks);
    }
}
