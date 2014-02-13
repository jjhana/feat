/*
 * Transliteration.java
 *
 * Created on February 4, 2006, 11:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.purl.jh.util.io;

/**
 *
 * @author Jirka
 */
public class Transliteration extends Preformat<Transliteration> {
    /* identity transliteration */
    public static Transliteration cNone = new Transliteration("none") {
        public String to(String aStr) {return aStr;}
        public String to(int aChar)   {return String.valueOf((char)aChar);}
        public String from(String aStr) {return aStr;}
    };
    
    /** Creates a new instance of Encoding */
    public Transliteration(String aId) {
        this(aId, aId);
    }

    public Transliteration(String aId, String aDesc) {
        super(aId, aDesc);
    }
    
    public boolean isNone() {return this == cNone;}

    public String to(String aStr)   {throw new UnsupportedOperationException();}
    public String to(int aChar)   {throw new UnsupportedOperationException();}
    public String from(String aStr) {throw new UnsupportedOperationException();}
    
}
