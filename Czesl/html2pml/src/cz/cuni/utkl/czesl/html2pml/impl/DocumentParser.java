package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.Main;
import java.util.Arrays;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.str.Strings;

/**
 * Parses an swing's html structure into a list of paragraphs.
 *
 * @author Jirka
 */
public class DocumentParser {
    private final CountingLogger userLog;
            
    // Input
    private final Element rootElement;

    // result
    private final Doc doc = new Doc();

    /** For testing only */
    DocumentParser(int len) {
        this(null, CountingLogger.getLogger("generic"));
    }

    public DocumentParser(Element aRootElement, final CountingLogger aUserLog) {
        rootElement = aRootElement;
        userLog = aUserLog;
    }

    public Doc go() throws BadLocationException {
        //printStructure( 0, rootElement );
        goToParagraphs( rootElement );

        return doc;
    }

    private void printStructure(int aDepth, final Element aElement) throws BadLocationException {
        if (aElement.isLeaf()) {
            System.out.printf(Strings.repeatChar(' ', aDepth*3) + "Leaf: %s - %d - %d\n", aElement.getName(), aElement.getStartOffset(), aElement.getEndOffset());
        }
        else {
            int from = aElement.getStartOffset();
            int to = aElement.getEndOffset();
                    
            System.out.printf(Strings.repeatChar(' ', aDepth*3) + "%s - %d - %d\n", aElement.getName(), from, to);
            System.out.printf(Strings.repeatChar(' ', aDepth*3+1) + aElement.getDocument().getText(from, to-from));
                
            for (int i = 0; i < aElement.getElementCount(); i++) {
                printStructure(aDepth+1, aElement.getElement(i));
            }
        }   
    }

    /*
     * Goes thru the element trees skipping everything except paragraph elements,
     * which it converts to Para objects and adds them to the list of paragraphs.
     */
    private final List<String> paraLike = Arrays.asList("p", "p-implied", 
        "h1", "h2", "h3", "h4", "h5", "h6");
           
            
    private void goToParagraphs(final Element aElement) throws BadLocationException {
        if (aElement.isLeaf()) return;

        // todo ignore p within tables??
        if (paraLike.contains(aElement.getName())) {
            try {
                parsePara(aElement);
            }
            catch (FormatError ex) {
                userLog.severe(ex, ex.getMessage()); // report and go to the next para
            }
        }
        else {
            for (int i = 0; i < aElement.getElementCount(); i++) {
                goToParagraphs(aElement.getElement(i));
            }
        }
    }

    /** 
     * Takes a single swing's para element, and if its not empty converts it to
     * our Para object and adds it to the list of paragraphs 
     */
    private void parsePara(final Element aPara) throws BadLocationException {
        final int from = aPara.getStartOffset();
        final int to   = aPara.getEndOffset();
        final String text = aPara.getDocument().getText(from, to-from);
        if (Strings.cWhitespacePattern.matcher(text).matches()) return; // todo 1 para -> ignore; 2 paras -> 1 para

        if (text.matches("(\\w+_)+\\w")) {  // todo only for initial non-empty paragraph
            Main.getErrLog().warning("Ignoring id paragraph %s. Instruct transcribers to read the manual!", text);
            return;
        }
        
        //System.out.println("--- Parsing another para --- " + Strings.trim(text, 8));
        final ParaParser seg = new ParaParser(doc, text, from, to, userLog);
        final Para para = seg.parse();
        doc.getParas().add(para);
    }

}
