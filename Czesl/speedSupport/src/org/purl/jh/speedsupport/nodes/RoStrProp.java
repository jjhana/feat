package org.purl.jh.speedsupport.nodes;

import org.openide.nodes.PropertySupport;

/**
 *
 * @author j
 */
class RoStrProp extends PropertySupport.ReadOnly<String> {
    final String str;

    public RoStrProp(String name, String displayName, String shortDescription, String aValue) {
        super(name, String.class, displayName, shortDescription);
        this.str = aValue;
        setValue("suppressCustomEditor", Boolean.TRUE);
    }

    RoStrProp(String aName, String aValue) {
        this(aName, aName, aName, aValue);
    }

    @Override
    public String getValue() {
        return str;
    }
}
