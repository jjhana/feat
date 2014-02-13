package org.purl.jh.pml.io;

import java.util.List;
import org.jdom.Attribute;
import org.purl.jh.util.err.Err;

/**
 * Utilities for manipulating jdom.
 * @author Jirka
 */
public class JDomUtil {
    public static String getId(org.jdom.Element aE) {
        return aE.getAttributeValue("id");
    }

    @SuppressWarnings("unchecked")
    public static List<org.jdom.Element> getChildren(org.jdom.Element aElement) {
        return (List<org.jdom.Element>) aElement.getChildren();
    }

    public static boolean getBoolAttribute(org.jdom.Element aE, String attr, boolean def) {
        String attrValue = aE.getAttributeValue(attr);
        if ("true".equals(attrValue)) return true;
        if ("false".equals(attrValue)) return false;
        Err.fAssert(attrValue == null, "Unknown attribute value");
        return def;
    }

    public static void setAttribute(org.jdom.Element aE, String attrName, boolean attrVal, boolean defVal) {
        if (attrVal == defVal) return;
        
        aE.getAttributes().add( new Attribute( attrName, attrVal ? "true" : "false") );
    }
    

}
