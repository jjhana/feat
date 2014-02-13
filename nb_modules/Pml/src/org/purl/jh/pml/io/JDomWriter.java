package org.purl.jh.pml.io;

import java.util.List;
import java.util.Map;
import org.jdom.Element;
import org.jdom.Namespace;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.IdedElement;

/**
 *
 * @author jirka
 */
public abstract class JDomWriter {
    protected final Namespace n;

    public JDomWriter(Namespace n) {
        this.n = n;
    }
    
    protected abstract Element createJdom();

// =============================================================================
// <editor-fold desc="Better jdom support">
// (this is not static to use the current namespace) 
// =============================================================================

    /**
     * Adds an element with textual content.
     *
     * @param aParent
     * @param aElementName
     * @param aText text added as content to the element, it is automatically escaped
     */
    public void addContent(Element aParent, String aElementName, String aText) {
        aParent.addContent(el(aElementName).addContent(aText == null ? null : aText));
    }

    /**
     * Adds an element with textual content.
     *
     * @param aParent
     * @param aElementName
     * @param aText text added as content to the element, it is automatically escaped; if
     *  when null, the element is not added
     */
    public void addNonNullContent(Element aParent, String aElementName, String aText) {
        if (aText != null) {
            addContent(aParent, aElementName, aText);
        }
    }

    /**
     * Adds an element with boolean content.
     * True is represented as '1', false as '0'.
     *
     * @param aParent
     * @param aElementName
     * @param aValue
     */
    public void addContent(Element aParent, String aElementName, boolean aValue) {
        aParent.addContent(el(aElementName).addContent(aValue ? "1" : "0"));
    }

    /**
     * Adds an element with integer content.
     *
     * @param aParent
     * @param aElementName
     * @param aValue
     */
    public void addContent(Element aParent, String aElementName, int aValue) {
        aParent.addContent(el(aElementName).addContent(String.valueOf(aValue)));
    }
    

    /**
     * Creates a jdom element within the default namespace.
     *
     * @param aName name (core) of the element
     * @return the created element
     */
    protected Element el(String aName) {
        return new Element(aName, n);
    }

    /**
     * Creates a jdom element with an id. The element has the default namespace.
     *
     * @param aName name (core) of the element
     * @param aEl element whose id should be used
     * @return the created element
     */
    protected Element el(String aName, IdedElement aEl) {
        Element e = new Element(aName, n);
        e.setAttribute("id", aEl.getId().getIdStr());
        return e;
    }

// </editor-fold>

// =============================================================================
// <editor-fold desc="Writing std structures">
// (this is not static to use the current namespace, 
//  todo: but shouldn't this use some other, standard namespace???)
// =============================================================================

    /**
     * Adds property element for each map entry.
     * @param aElement element to add properties to
     * @param aMap map containing properties
     */
    protected void addProperites(Element aElement, Map<String, String> aMap) {
        for (Map.Entry<String, String> e : aMap.entrySet()) {
            final org.jdom.Element propE = el("property");
            addContent(propE, "key", e.getKey());
            addContent(propE, "val", e.getValue());
        }
    }
    
    protected void addComment(Element aElement, Commented aCommented) {
        if (aCommented.getComment() != null && !aCommented.getComment().isEmpty()) {
            addContent(aElement, "comment", aCommented.getComment());
        }
    }

    //    // todo this is wrong
    //    protected void addContentLM(org.jdom.Element aRoot, List<org.jdom.Element> aChildren) {
    //        if (aChildren.size() == 1) {
    //            aRoot.addContent(aChildren);
    //        }
    //        else if (aChildren.size() > 1) {
    //            org.jdom.Element e = el(aName);
    //            aRoot.addContent(e);
    //            for (String string : aStrings) {
    //                addContent(e, "LM", string);
    //            }
    //        }
    //    }
    protected void addTextLM(Element aElement, String aName, List<String> aStrings) {
        if (aStrings.size() == 1) {
            addContent(aElement, aName, aStrings.get(0));
        } else if (aStrings.size() > 1) {
            Element e = el(aName);
            aElement.addContent(e);
            for (String string : aStrings) {
                addContent(e, "LM", string);
            }
        }
    }

// </editor-fold>
    
}
