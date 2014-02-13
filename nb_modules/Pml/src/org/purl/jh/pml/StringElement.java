package org.purl.jh.pml;

/**
 * 
 * @author Jirka
 */
public class StringElement extends AbstractElement {
    private String str;
    
    /** Creates a new instance of StringElement */
    public StringElement(String aStr) {
        str = aStr;
    }

    public String getString() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
    
    @Override
    public String toString() {
        return str;
    }
}
