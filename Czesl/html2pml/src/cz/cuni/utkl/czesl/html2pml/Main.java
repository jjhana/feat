package cz.cuni.utkl.czesl.html2pml;

import cz.cuni.utkl.czesl.html2pml.impl.DocumentParser;
import cz.cuni.utkl.czesl.html2pml.impl.Para;
import cz.cuni.utkl.czesl.html2pml.impl.Doc;
import cz.cuni.utkl.czesl.html2pml.impl.PmlWriter;
import cz.cuni.utkl.czesl.html2pml.impl.CollectFormat;
import cz.cuni.utkl.czesl.html2pml.impl.Format;
import cz.cuni.utkl.czesl.html2pml.impl.Mover;
import java.io.File;
import javax.swing.text.StyledDocument;
import java.util.BitSet;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.text.BadLocationException;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.io.Files;
import org.purl.jh.util.io.Html;

/**
 * Converts an html document to 2 pml layer files (wlayer + default la-layer)*
 * 
 * @author Jirka Hana
 */
public class Main {
    /** Logger to report conversion errors to (provided from outside or a simple defalut logger is created) */
    private static CountingLogger errLog;

    public static CountingLogger getErrLog() {
        return errLog;
    }

    /** When called from the command line */
    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2 || "--help".equals(args[0]))  {
            System.err.println("Usage: cz.cuni.utkl.czesl.html2pml.Main inFile [outFilesPrefix]");
            System.err.println("Converts an html file to the feat format");
            System.err.println("inFile - html file to be converted");
            System.err.println("outFilesPrefix - prefix of the output files' name");
            System.err.println("   It is assumed to be inFile without extension when omitted");
            
            System.exit(-1);
        }
        
        final File inFile = new File(args[0]);
        final File outFilePrefix = (args.length > 1) ? 
                new File(args[1]) : 
                new File(inFile.getParent(), Files.removeExtension(inFile.getPath()));

        Main main = new Main();
        
        if (!main.translate(inFile, outFilePrefix)) System.exit(-1);
    }


    public boolean translate(final File aInFile, final File aOutFilePrefix) {
        CountingLogger logger = CountingLogger.getLogger("generic");
        logger.getLg().setUseParentHandlers(false);

        Handler h = new java.util.logging.ConsoleHandler();
        h.setFormatter(new VerySimpleFormatter());
        logger.getLg().addHandler(h);
        
        return translate(aInFile, aOutFilePrefix, logger);
    }

    private static class VerySimpleFormatter extends Formatter {
        private final String lineSeparator = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));

        @Override
        public synchronized String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            sb.append(record.getLevel().getLocalizedName());
            sb.append(": ");
            sb.append(formatMessage(record));
            sb.append(lineSeparator);
            return sb.toString();
        }
    }
    

    public boolean translate(final File aInFile, final File aOutFilePrefix, final CountingLogger aErrorLog) {
        errLog = aErrorLog;
        errLog.info("Converting %s -> %s", aInFile, aOutFilePrefix);
        int initialNrOfErrs = errLog.getErrorCount();

        final StyledDocument htmlDoc;
        try {
            htmlDoc = Html.load(aInFile);
        } catch (Exception ex) {
            errLog.severe(ex, "Error reading the html file %s (does it exist? Do you have read rights to it?)", aInFile);
            return false;
        }

        final Doc doc = process(htmlDoc, aInFile);
        
        if (errLog.getErrorCount() > initialNrOfErrs) {
            errLog.info("The file %s was not converted due to errors", aInFile);         // todo should not increase err count
            return false;
        }

        // --- save into pml format ---
        try {
            new PmlWriter(aOutFilePrefix, doc).go();
        } catch (Exception ex) {
            errLog.severe(ex, "Error saving the result to file %s (does the directory exist?, Do you have write rights to it?, Is there file by the same file locked by a different application?)", aInFile);
            return false;
        }

        return true;
    }

    private Doc process(final StyledDocument doc, final File aInFile) {
        try {
            return process(doc);
        }
        catch (Throwable ex) {
            errLog.severe(ex, "Error processing file %s", aInFile);
            System.out.println("-----------");      // into the report
            System.out.println(ex);
            ex.printStackTrace(System.out);
            System.out.println("-----------");
        }
        return null;
    }

    private Doc process(final StyledDocument aHtmlDoc) throws BadLocationException {
        final Map<Format.Type, BitSet> formatMap = new CollectFormat(aHtmlDoc).go();
        //System.out.println(Cols.toStringNl(formatMap.entrySet()));

        // parse the plain text of the document, process codes, etc
        final DocumentParser p = new DocumentParser(aHtmlDoc.getRootElements()[0], errLog);
        final Doc doc = p.go();

        doc.infuseFormat(formatMap);
        
        // move text marked for move and do full-word deletion
        for (Para para : doc.getParas()) {
            new Mover(para).go();
        }
        
        doc.finalProcessing();

        return doc;
    }

}
