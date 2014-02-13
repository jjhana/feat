package org.purl.jh.util.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.purl.jh.util.err.IException;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.translit.TranslitWriter;
import org.purl.jh.util.str.Strings;
//import lombok.Cleanup;


/**
 *
 * @author jirka
 */
public final class IO {
    private IO() {}
    private static XFiler mXFiler = new XFiler();


    /**
     * Closes the supplied object if it is not null.
     * Any IO exceptions are written to System.err but otherwise ignored.
     *
     * @see #quietlyClose(aCloseable)
     */
    public static void close(Closeable aCloseable) {
        if (aCloseable != null)  {
            try {
                aCloseable.close();
            }
            catch (IOException ex) {
                System.err.println("Cannot close " + aCloseable + ": " + ex);
            }
        }
    }

    /**
     * Closes the supplied objects if it is not null.
     * Any IO exceptions are written to System.err but otherwise ignored.
     *
     * @see #quietlyClose(aCloseable)
     */
    public static void close(Closeable ... aCloseables) {
        if (aCloseables == null) return;
        for (Closeable closeable : aCloseables) {
            close(closeable);
        }
    }

    public static void close(Iterable<? extends Closeable> aCloseables) {
        if (aCloseables == null) return;
        for (Closeable closeable : aCloseables) {
            close(closeable);
        }
    }


    /**
     * Closes the supplied object if it is not null.
     * Any IO exceptions are ignored.
     * @see #close(aCloseable)
     */
    public static void quietlyClose(Closeable aCloseable) {
        if (aCloseable != null)  {
            try {
                aCloseable.close();
            }
            catch (IOException ex) {
            }
        }
    }

// -----------------------------------------------------------------------------
// system wide defaults
// -----------------------------------------------------------------------------

    private static Encoding mDefEnc;
    private static Format mDefFormat;

// -----------------------------------------------------------------------------
// Registries
// -----------------------------------------------------------------------------

    public static FormatRegistry formats() {
        return mXFiler.formats();
    }

    public static void setFormats(FormatRegistry aFormats) {
        mXFiler.setFormats(aFormats);
    }

    public static TranslitRegistry translits() {
        return mXFiler.translits();
    }

    public static void setTranslit(TranslitRegistry aTranslits) {
        mXFiler.setTranslit(aTranslits);
    }

// -----------------------------------------------------------------------------

    public static void setFilesByExt(boolean aFilesByExt) {
        mXFiler.setFilesByExt(aFilesByExt);
    }

    @Deprecated
    public static Encoding defEnc() {
        return mDefEnc;
    }

    @Deprecated
    public static void setDefEnc(Encoding aEnc) {
        mDefEnc = aEnc;
    }

    @Deprecated
    public static Format defFormat() {
        return mDefFormat;
    }

    @Deprecated
    public static void defFormat(Format aFormat) {
        mDefFormat = aFormat;
    }

// -----------------------------------------------------------------------------
// writers
// -----------------------------------------------------------------------------

    // @todo catch and close if error in the meantime necessary
    public static Writer openWriter(XFile aFile) throws java.io.IOException  {
        OutputStream os = new FileOutputStream(aFile.file());

        // --- compression ---
        if (!aFile.compression().none()) {
            if (aFile.compression().gz()) {
                os = new GZIPOutputStream(os);
            }
            else if (aFile.compression().zip()) {
                throw Err.iErr("Zip is not supported yet.");
            }
        }

        // if (aFile.enc().equals(Encoding.cUtf8)) {  // @todo make configurable
//            os.write(Bom.UTF8BOMBYTES);
//        }


        Writer w = new OutputStreamWriter(os, aFile.enc().getId());


        // --- transliteration ---
        if (! aFile.translit().isNone()) {
            w = new TranslitWriter(w, aFile.translit());
        }
        return w;
    }

    /**
     * Does not do automatic flushing.
     * Automatically supports GZip compression (not yet zip).
     */
    public static PrintWriter openPrintWriter(XFile aFile) throws java.io.IOException  {
        return new java.io.PrintWriter(openWriter(aFile));
    }

    /**
     * Writes list of strings as lines to a file.
     * @param outFile file to write to
     * @param aList object to write (using PrintWriter's println method)
     * @throws java.io.IOException
     */
    public static <T> void writeLines(XFile outFile, List<T> aList) throws IOException {
        PrintWriter w = null;
        try {
            w = IO.openPrintWriter(outFile);
            for (T e : aList) {
                w.println(e);
            }
        } finally {
            IO.close(w);
        }
    }

// -----------------------------------------------------------------------------
// Battery
// -----------------------------------------------------------------------------

    /**
     *
     * @param aFiles
     * @return
     * @throws java.io.IOException
     * @deprecated Use Battery.openPrintWriter
     */
    @Deprecated
    public static PrintWriter[] openPrintWriterBattery(XFile[] aFiles) throws java.io.IOException  {
        final PrintWriter[] outs = new PrintWriter[aFiles.length];
        for (int i = 0; i < aFiles.length; i++)
            outs[i] = openPrintWriter(aFiles[i]);
        return outs;
    }

    /**
     * Closes a battery (an array of closeables - readers, writers).
     *
     */
    @Deprecated
    public static void closeBattery(Closeable[] aCloseables) throws IOException {
        close(aCloseables);
    }

    /**
     * Closes a battery (an array of closeables - readers, writers).
     *
     */
    @Deprecated
    public static void closeBattery(List<? extends Closeable> aCloseables) throws IOException {
        close(aCloseables);
    }

// -----------------------------------------------------------------------------
// readers
// -----------------------------------------------------------------------------
    /**
     * Automatically supports GZip compression & multiple files in a directory.
     * @todo add filters (e.g. *.txt), list of files (e.g. a.txt, b.txt, c.txt)
     * @todo multi stream should set the file as well
     */
    public static Reader openReader(final XFile aFile)   throws java.io.IOException  {
        InputStream is = openInputStream(aFile);    // either a file or a chain of files

        return new InputStreamReader(is, aFile.enc().getId());
    }

    /**
     * Automatically supports GZip compression & multiple files in a directory.
     * @todo add filters (e.g. *.txt), list of files (e.g. a.txt, b.txt, c.txt)
     * @todo multi stream should set the file as well
     * must be non-empty
     */
    public static Reader openReader(final Iterable<XFile> aFiles)   throws java.io.IOException  {
        InputStream is = openInputStream(aFiles);    // either a file or a chain of files

        return new InputStreamReader(is, aFiles.iterator().next().enc().getId());
    }


    /**
     * Automatically supports GZip compression & multiple files in a directory.
     * @todo add filters (e.g. *.txt), list of files (e.g. a.txt, b.txt, c.txt)
     * @todo multi stream should set the file as well
     */
    public static LineReader openLineReader(final XFile aFile)   throws java.io.IOException  {
        return new LineReader( openReader(aFile) ).setFile(aFile);
    }

    /**
     * Automatically supports GZip compression & multiple files in a directory.
     * @todo add filters (e.g. *.txt), list of files (e.g. a.txt, b.txt, c.txt)
     * @todo multi stream should set the file as well
     */
    public static LineReader openLineReader(final Iterable<XFile> aFiles)   throws java.io.IOException  {
        return new LineReader( openReader(aFiles) );
    }

    public static InputStream openInputStream(Iterable<XFile> aFiles) throws IOException {
        return new MultiFileInputStream(aFiles);
    }

    /**
     * Automatically handles gz compression and and treats files in a directory as
     * one virtual file (even recursively; the order is determined by the system).
     *
     * @param aFile a file or directory.
     * @return
     * @throws IOException
     * @todo optional callback function called when opening a new file
     */
    public static InputStream openInputStream(XFile aFile) throws IOException {
        if (aFile.file().isDirectory()) {
            return openInputStream(aFile.listFiles());
        }
        else {
            FileInputStream is;
            if (aFile.enc().equals(Encoding.cUtf8)) {
                is = Bom.getFileInputStream(aFile.file(), aFile.enc().getId() );
            }
            else {
                is = new FileInputStream(aFile.file());
            }

            if (aFile.compression().none()) {
                return is;
            }
            else if (aFile.compression().gz()) {
                return new GZIPInputStream(is);
            }
            throw Err.iErr("The %s compression is not supported yet.", aFile.compression());
        }
    }


// -----------------------------------------------------------------------------
// String -> XFile
// -----------------------------------------------------------------------------

    public static File plainFileFromString(String aString, File aPath)  {
        return (aPath == null) ? new File(aString) : new File(aPath, aString);
    }

    /**
     * Default format is Format.def.
     */
    public static XFile fileFromString(String aString, File aPath)  {
        return fileFromString(aString, aPath, Compression.none, mDefEnc, Transliteration.cNone, Format.cDef);
    }

    /**
     * Default format is Format.def.
     */
    public static XFile fileFromString(String aString, File aPath, Encoding aEncDef)  {
        return fileFromString(aString, aPath, Compression.none, aEncDef, Transliteration.cNone, Format.cDef);
    }

    /**
     */
    public static XFile fileFromString(String aString, File aPath, Encoding aEncDef, Format aFormatDef)  {
        return fileFromString(aString, aPath, Compression.none, aEncDef, Transliteration.cNone, aFormatDef);
    }

    /*
     * @para aPath null means no path
     * @todo add compression
     */
    public static XFile fileFromString(String aString, File aPath, Compression aCompDef, Encoding aEncDef, Transliteration aTranslitDef, Format aFormatDef)  {
        return mXFiler.fileFromString(aString, aPath, aCompDef, aEncDef, aTranslitDef, aFormatDef);
    }


// -----------------------------------------------------------------------------
// Reading streams
// -----------------------------------------------------------------------------

    /**
     * read an InputStream into a byte[].
     * @param in the input stream to read
     * @return the input stream data as a byte[]
     * @throws IOException
     */
    public static byte[] readStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead = 0;
        while (true) {
            int byteReadThisTurn = in.read(buffer, bytesRead, buffer.length - bytesRead);
            if (byteReadThisTurn < 0)
                break;

            bytesRead += byteReadThisTurn;

            if (bytesRead >= buffer.length - 256) {
                byte[] newBuffer = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuffer, 0, bytesRead);
                buffer = newBuffer;
            }
        }

        if (buffer.length == bytesRead) {
            return buffer;
        } else {
            byte[] response = new byte[bytesRead];
            System.arraycopy(buffer, 0, response, 0, bytesRead);

            return response;
        }
    }

    /**
     * read the specified number of bytes from an input stream into a byte[].
     * @param in the input stream to read
     * @param size the number of bytes to read
     * @return the input data stream as a byte[]
     * @throws IOException
     */
    public static byte[] readStream(InputStream in, int size) throws IOException {
        if (in == null) return null;
        if (size == 0) return new byte[0];
        int currentTotal = 0;
        int bytesRead;
        byte[] data = new byte[size];
        while (currentTotal < data.length && (bytesRead = in.read(data, currentTotal, data.length - currentTotal)) >= 0)
            currentTotal += bytesRead;
        return data;
    }

// -----------------------------------------------------------------------------
// File operations
// -----------------------------------------------------------------------------

    /**
     * copy a file (uses java.nio).
     * @param src the source File
     * @param dest the destination File
     * @throws IOException
     */
    public static void copyFile(File src, File dest) throws IOException {
        FileChannel srcChannel = null;
        FileChannel destChannel = null;
        try {
            srcChannel = new FileInputStream(src).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
        }
        finally {
            quietlyClose(srcChannel);
            quietlyClose(destChannel);
        }
    }


    /**
     * clear a directory's contents or delete a file.  If the input dir is a file, it is
     * deleted. If the input dir is a directory, then the contents are recursively deleted
     * but the directory itself remains.
     *
     * @param dir the directory to purge
     * @return true if the directory was cleared or does not exist; false otherwise
     */
    public static boolean clearDirectory(File dir)  {
        if ((dir == null) || (!dir.exists())) return true;
        if (dir.isFile()) return dir.delete();

        for (File f : dir.listFiles()) {
            if (!clearDirectory(f)) return false;
        }

        return true;
    }

// -----------------------------------------------------------------------------
// Simple utilities
// -----------------------------------------------------------------------------

    /**
     * Very basic utility reading in all non-empty lines, using the default
     * configuration of the LineReader.
     *
     * @param aFile file to read in
     * @return non-empty lines in the file (in the order they are in the file)
     * @throws java.io.IOException
     * @todo create variants allowing more configuration (whole line or specify column (via from-to char or via regex separator)
     */
    public static List<String> readInLines(XFile aFile) throws IOException {
        LineReader r = null;
        try {
            r = IO.openLineReader(aFile);
            final List<String> list = new ArrayList<String>();
            for (;;) {
                String line = r.readNonEmptyLine();
                if (line == null) break;
                list.add(line);
            }
            return list;
        }
        finally {
            close(r);
        }
    }



    public static List<String> readInColumn(XFile aFile, int aIdx) throws IOException {
        return readInColumn(aFile, Strings.cWhitespacePattern, aIdx);
    }

    public static List<String> readInColumn(final XFile aFile, final Pattern aPattern, final int aIdx) throws IOException {
        LineReader r = null;
        try {
            r = IO.openLineReader(aFile);
            r.configureSplitting(aPattern, aIdx+1, "Line does not contain at least " + aIdx+1 + " columns");
            final List<String> list = new ArrayList<String>();
            for (;;) {
                String[] line = r.readSplitNonEmptyLine();
                if (line == null) break;
                list.add(line[aIdx]);
            }

            return list;
        }
        finally {
            close(r);
        }
    }





    /**
     * Reads in multimap from a file.
     * The first token is the key the remaining tokens are the values.
     *
     * @param aFile
     * @param aSeparator
     * @return
     * @throws IOException
     * @todo allow a more general source
     */
    public static MultiMap<String,String> readInMultiMap(final XFile aFile, final Pattern aSeparator) throws IOException {
        final MultiMap<String,String> map = XCols.newMultiHashHashMap(); //  XCols.newMultiHashHashMap<String,String>();

        /* TODO @Cleanup */ final LineReader r = IO.openLineReader(aFile);
        r.configureSplitting(aSeparator, 2, "Incorrect format - requires: key values");

        for(;;) {
            final List<String> tokens = Cols.asList(r.readSplitNonEmptyLine());
            if (tokens == null) break;

            map.addAll(tokens.get(0), org.purl.jh.util.col.Cols.subListB(tokens, 1, 0));
        }

        return map;
    }

    /**
     * Reads in multimap from a file.
     * Each line contains a single key and one or more associated values.
     *
     * @param aFile
     * @param aKeyPattern pattern to extract keys
     * @param aValsPattern pattern to extract values
     * @param aValSep pattern to split values (extracted with aValsPattern)
     * @return
     * @throws IOException
     * @todo allow a more general source
     */
    public static MultiMap<String,String> readInMultiMap(final XFile aFile, final Pattern aKeyPattern, final Pattern aValsPattern, final Pattern aValSep) throws IOException {
        final MultiMap<String,String> map =  XCols.newMultiHashHashMap();

        /* TODO @Cleanup */  final LineReader r = org.purl.jh.util.io.IO.openLineReader(aFile);

        for(;;) {
            final String line = r.readNonEmptyLine();
            if (line == null) break;

            final Matcher keyMatcher = aKeyPattern.matcher(line);
            Err.fAssert(keyMatcher.find(), r, "No key");   // todo pas line
            final String key = keyMatcher.group(1);
            Err.fAssert(key != null,  r, "Null key: pattern='%s', group='%s'\n%s", aKeyPattern, keyMatcher.group(), line);

            final Matcher valsMatcher = aValsPattern.matcher(line);
            Err.fAssert(valsMatcher.find(),  r, "No values: pattern='%s'\n%s", aValsPattern, line);
            final String vals = valsMatcher.group(1);
            Err.fAssert(vals != null,  r, "Null vals: pattern='%s', group='%s'\n%s", aValsPattern, valsMatcher.group(), line);

            final List<String> tokens = Cols.asList(aValSep.split(vals));
            Err.fAssert(tokens != null && !tokens.isEmpty(),  r, "No vals: %s\n%s", valsMatcher.group(), line);

            map.addAll(key, tokens);
        }

        return map;
    }


}
