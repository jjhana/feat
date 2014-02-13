
package org.purl.jh.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.BadLocationException;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Html {

    /**
     * Reads in an html document from a file determining the correct encoding.
     *
     * @param aInFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static HTMLDocument load(final File aInFile) throws FileNotFoundException, IOException {
        // note opens the file twice, first to get the encoding, then the content,
        Reader r = null;
        InputStream is = null;
        try {
            is = new FileInputStream(aInFile);
            String enc = getEncoding(is);

            r = IO.openReader(new XFile(aInFile, enc));

            return load(r);
        }
        finally {
            IO.close(is,r);
        }
    }

    /**
     * Reads in an html document from a reader.
     *
     * @param aR reader to use to read the html document in.
     * Use {@link #getEncoding(java.io.InputStream)} to obtain the correct character
     * encoding.
     *
     * @return html document
     * @throws IOException
     */
    public static HTMLDocument load(final Reader aR) throws IOException {
        final HTMLEditorKit kit = new HTMLEditorKit();
        final HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        doc.putProperty("IgnoreCharsetDirective", true);

        try {
            kit.read(aR, doc, 0);
            return doc;
        } catch (BadLocationException e) {  // should not happen
            throw new RuntimeException(e);
        }
    }




    public static String getEncoding(final InputStream aIs) throws IOException {
        //String enc = "TIS620"; // some unlikely encoding so that it fails early -- not supported by all JRE's
        String enc = "KOI8_R"; // some standard (contained in all JRE's in rt.jar), but less likely encoding so that it fails early

        final HTMLEditorKit kit = new HTMLEditorKit();
        // todo use the most likely encoding and restart reading only if it fails

        try {
            final Document doc = kit.createDefaultDocument();
            Reader r = new InputStreamReader(aIs, enc);
            kit.read(r, doc, 0);
        } catch (BadLocationException e) { // should not happen
            throw new RuntimeException(e);
        } catch (ChangedCharSetException ex) {
            final String tmp = ex.getCharSetSpec();
            final int idx = tmp.indexOf("charset=");
            enc = idx > 0 ? tmp.substring(idx + 8).trim() : "utf8";
        }

        return enc;
    }

}
