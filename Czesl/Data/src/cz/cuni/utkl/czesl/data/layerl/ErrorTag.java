package cz.cuni.utkl.czesl.data.layerl;

import org.purl.jh.pml.ts.Tag;

/**
 * todo add some code registering/reporting changes
 * @author Jirka dot Hana at gmail dot com
 */
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

    public ErrorTag(ErrorTagset aTagset, String tag, String comment, boolean deprecated, boolean auto, String menuLabel, int minLowerLegs, int maxLowerLegs, int minUpperLegs, int maxUpperLegs, int minLinks, int maxLinks) {
        super(aTagset, tag, comment);
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

    @Deprecated
    public String getComment() {
        return getDescr();
    }

    public boolean isAuto() {
        return auto;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    
    public String getMenuLabel() {
        return menuLabel;
    }
    
    public int getMaxLinks() {
        return maxLinks;
    }

    public int getMaxLowerLegs() {
        return maxLowerLegs;
    }

    public int getMaxUpperLegs() {
        return maxUpperLegs;
    }

    public int getMinLinks() {
        return minLinks;
    }

    public int getMinLowerLegs() {
        return minLowerLegs;
    }

    public int getMinUpperLegs() {
        return minUpperLegs;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setMaxLinks(int maxLinks) {
        this.maxLinks = maxLinks;
    }

    public void setMaxLowerLegs(int maxLowerLegs) {
        this.maxLowerLegs = maxLowerLegs;
    }

    public void setMaxUpperLegs(int maxUpperLegs) {
        this.maxUpperLegs = maxUpperLegs;
    }

    public void setMenuLabel(String menuLabel) {
        this.menuLabel = menuLabel;
    }

    public void setMinLinks(int minLinks) {
        this.minLinks = minLinks;
    }

    public void setMinLowerLegs(int minLowerLegs) {
        this.minLowerLegs = minLowerLegs;
    }

    public void setMinUpperLegs(int minUpperLegs) {
        this.minUpperLegs = minUpperLegs;
    }
    
    
    @Override
    public String toString() {
        return "ErrorSpec{" + super.toString() + "; minLowerLegs=" + minLowerLegs + "maxLowerLegs=" + maxLowerLegs + "minUpperLegs=" + minUpperLegs + "maxUpperLegs=" + maxUpperLegs + "minLinks=" + minLinks + "maxLinks=" + maxLinks + "deprecated=" + deprecated + '}';
    }


}
