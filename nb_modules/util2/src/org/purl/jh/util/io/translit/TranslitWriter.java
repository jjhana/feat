package org.purl.jh.util.io.translit;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import org.purl.jh.util.io.Transliteration;

/**
 * Unicode -> transliteration
 * @todo pass transliterator or subclass ???
 * @author Jiri
 */
public class TranslitWriter extends FilterWriter {
    private Transliteration mTransliterator;
            
    /**
     * Create a new filtered writer.
     *
     * @param out  a Writer object to provide the underlying stream.
     * @param
     * @throws NullPointerException if <code>out</code> is <code>null</code>
     */
    public TranslitWriter(Writer aOut, Transliteration aTransliterator) {
        super(aOut);
        mTransliterator = aTransliterator;
    }
    
    /**
     * Write a single character.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        out.write( mTransliterator.to(c) );
    }
    
    /**
     * Write a portion of an array of characters.
     *
     * @param  aCBuf  Buffer of characters to be written
     * @param  aOff   Offset from which to start reading characters
     * @param  aLen   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char aCBuf[], int aOff, int aLen) throws IOException {
        for (int i = 0; i < aLen; i++) 
            write(aCBuf[aOff+i]);
    }
    
    /**
     * Write a portion of a string.
     *
     * @param  aStr  String to be written
     * @param  aOff  Offset from which to start reading characters
     * @param  aLen  Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String aStr, int aOff, int aLen) throws IOException {
        for (int i = 0; i < aLen; i++) 
            write(aStr.charAt(aOff+i));
    }
    
}