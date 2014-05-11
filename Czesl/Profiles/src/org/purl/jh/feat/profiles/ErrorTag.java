package org.purl.jh.feat.profiles;

import lombok.Data;
import org.purl.jh.pml.ts.Tag;

/**
 * todo add some code registering/reporting changes
 * @author Jirka dot Hana at gmail dot com
 */
@Data
@lombok.EqualsAndHashCode(callSuper = true)
public class ErrorTag extends Tag<ErrorTagset> {
    private boolean auto;         // todo to Tag
    private boolean deprecated;
    private String menuLabel;     // todo move lg dependent things out (maybe keep English as a default)
    private int minLowerLegs;
    private int maxLowerLegs;
    private int minUpperLegs;
    private int maxUpperLegs;
    private int minLinks;
    private int maxLinks;

    public ErrorTag(ErrorTagset aTagset, String tag, String desc, boolean deprecated, boolean auto, String menuLabel, int minLowerLegs, int maxLowerLegs, int minUpperLegs, int maxUpperLegs, int minLinks, int maxLinks) {
        super(aTagset, tag, desc);
        this.auto = auto;
        this.deprecated = deprecated;
        this.menuLabel = menuLabel;
        this.minLowerLegs = minLowerLegs;
        this.maxLowerLegs = maxLowerLegs;
        this.minUpperLegs = minUpperLegs;
        this.maxUpperLegs = maxUpperLegs;
        this.minLinks = minLinks;
        this.maxLinks = maxLinks;
    }

    @Deprecated
    public String getTagId() {
        return getId();
    }
}
