package org.purl.jh.pml;

/**
 * Any non-handled element.
 * @author Jirka
 */
public class EElement extends AbstractElement {
    private final org.jdom.Element mElement;
            
    /** Creates a new instance of EElement */
    public EElement(org.jdom.Element aElement) {
        mElement = aElement;
    }

    public org.jdom.Element getElement() {
        return mElement;
    }
    
    
}
