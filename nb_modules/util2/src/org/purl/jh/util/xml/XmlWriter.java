package org.purl.jh.util.xml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.str.Strings;

/**
 * A very simple xml writer.
 * Most strings (except tags) are automatically escaped.
 *
 * @author Jirka
 */
public class XmlWriter extends Writer {
    protected final XFile file;
    protected final PrintWriter w;
    protected int depth = 0;
    
    protected int textCounter = 0;

    /**
     * When true, tags are indented and followed by a new line.
     */
    protected boolean prettyPrint = true;

    protected int indentSize = 2;
    
    public XmlWriter(XFile aFile) throws IOException {
        file = aFile;
        w = IO.openPrintWriter(aFile);
    }

    public XmlWriter(PrintWriter aWriter) throws IOException {
        file = null;
        w = aWriter;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean printePrint) {
        this.prettyPrint = printePrint;
    }

    public int getIndentSize() {
        return indentSize;
    }

    public void setIndentSize(int indentSize) {
        this.indentSize = indentSize;
    }
    
    public int getTextCounter() {
        return textCounter;
    }

    public void resetTextCounter() {
        textCounter = 0;
    }
    
    public void setTextCounter(int textCounter) {
        this.textCounter = textCounter;
    }
    
     
    
    /** Can be null when the print-writer is provided directly
     * @return file to write to
     */
    public XFile getFile() {
        return file;
    }

    
    /** Performs escaping and increases text counter */
    public XmlWriter text(String aText) {
        final String str = escape(aText);
        dPrint(str);
        textCounter += str.length();
        return this;
    }
    
    
    public XmlWriter open(String aInside) {
        dPrintf("<%s>", aInside);
        inc();
        return this;
    }

    /**
     * Write an opening tag.
     * <pre>
     *  w.open("para", "id=\"%s\" linkedpara.rf=\"w#%s\"", id, id0 );
     * </pre>
     *
     * @param aName name of the element
     * @param aTemplate template of the attribute part of the element
     * @param aVals values of the attributes
     * @return
     */
    public XmlWriter open(String aName, String aTemplate, Object ... aVals) {
        dPrintf("<" + aName + " " + aTemplate + ">", aVals);
        inc();
        return this;
    }

    public String createId(String aIdTemplate, Object ... aPars) {
        return String.format(aIdTemplate, aPars);
    }

    public XmlWriter openIded(String aName, String aId) {
        dPrintf("<%s id=\"%s\">", aName, aId);
        inc();
        return this;
    }
    
    public XmlWriter openIdedx(String aName, String aIdTemplate, Object ... aPars) {
        return openIded(aName, createId(aIdTemplate, aPars));
    }

    public XmlWriter close(String aName) {
        dec(aName);
        dPrintf("</%s>", aName);
        return this;
    }

    public XmlWriter single(String aName) {
        dPrintf("<%s />", aName);
        return this;
    }

    public XmlWriter single(String aName, String aTemplate, Object ... aVals) {
        dPrintf("<" + aName + " " + aTemplate + " />", aVals);
        return this;
    }

    /**
     * Writes tag-text-closing tag on one line, regardless of pretty printing.
     * @param aName
     * @param aText
     * @return 
     */
    public XmlWriter openClose(String aName, String aText) {
        if (aText == null) throw new IllegalArgumentException("aText is null");
        
        if (prettyPrint) ddPrintIndent();
        boolean old = prettyPrint;
        prettyPrint = false;            // to keep them all at the same line
        w.printf("<%s>", aName);
        inc();
        try {
        if (!aText.isEmpty()) text(aText);    
        }
        catch(Throwable aT) {
            System.err.printf("openClose Error %s - '%s'\n", aName, aText);
        }
        close(aName);
        
        prettyPrint =  old;
        if (prettyPrint) w.println();
        //dPrintf("<%s>%s</%s>", aName, escape(aText), aName);
        return this;
    }


    public XmlWriter inc() {
        depth++;
        return this;
    }

    public XmlWriter dec(String aElementName) {
        depth--;
        if (depth < 0) throw new IllegalStateException("Negative depth; element=" + aElementName);
        return this;
    }

    /**
     * All strings are escaped.
     * @param aFormat
     * @param aParams
     * @return 
     */
    public XmlWriter print(String aFormat, Object... aParams) {
        dPrint(escape(String.format(aFormat, aParams)));
        return this;
    }

    /**
     * The string is not escaped.
     * @param buf
     * @param off
     * @param len 
     */
    @Override
    public void write(char[] buf, int off, int len) {
        w.write(buf, off, len);
    }

    @Override
    public void flush() {
        w.flush();
    }

    @Override
    public void close() {
        w.close();
    }

    public String escape(String aString) {
        return StringEscapeUtils.escapeXml(aString);
    }

    /**
     * Direct writing of a string to the writer.
     * 
     * If prettyPrint is on, the string is printed as a single indented line.
     * 
     * Does not perform any string escaping.
     * @param aString
     */
    public void dPrint(String aString) {
        if (prettyPrint) w.print(Strings.spaces(depth * indentSize));
        w.print(aString);
        if (prettyPrint) w.println();
    }
    
    /**
     * Direct writing of an indented line to the writer.
     * 
     * If prettyPrint is on, the string is printed as a single indented line.
     * 
     * Does not perform any string escaping.
     * @param aFormat
     * @param aParams
     */
    public void dPrintf(String aFormat, Object... aParams) {
        if (prettyPrint) w.print(Strings.spaces(depth * indentSize));
        w.printf(aFormat, aParams);
        if (prettyPrint) w.println();
    }

    /**
     * Writes indent string, regardless of prettyprinting settings.
     * @return the number of spaces used for indentation.
     */
    public int ddPrintIndent() {
        final int spaces = depth * indentSize;
        w.print(Strings.spaces(spaces));
        return spaces;
    }

    public void println() {
        w.println();
    }
    
    /**
     * Direct writing of a string to the writer, no pretty printing.
     * 
     * Does not perform any string escaping.
     * @param aString
     */
    public void ddPrint(String aString) {
        w.print(aString);
    }
    
    /**
     * Direct writing of a string to the writer, no pretty printing.
     * 
     * Does not perform any string escaping.
     * @param aFormat
     * @param aParams
     */
    public void ddPrintf(String aFormat, Object... aParams) {
        w.printf(aFormat, aParams);
    }

}
