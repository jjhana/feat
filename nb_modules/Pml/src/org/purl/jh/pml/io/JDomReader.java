package org.purl.jh.pml.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.ErrorHandler;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.err.UserOut;
import org.purl.jh.util.io.Encoding;
import org.purl.jh.util.io.LineReader;

/**
 *
 * @author jirka
 */
public abstract class JDomReader {
    /**
     * File containing the layer read in.
     */
    protected FileObject fileObject;
    
    protected Document jdom;

    /**
     * Namespace of the layer.
     */
    public final Namespace n;


    /** todo experimental - reacts to error/warnings when processing the document */
    protected ErrorHandler err;
    
    
    public JDomReader(Namespace aNamespace) {
        n = aNamespace;
    }
    

    public FileObject getFileObject() {
        return fileObject;
    }

    public Document getJdom() {
        return jdom;
    }

    public ErrorHandler getErr() {
        if (err == null) {
           err = new UserOut(); // todo 
        }  
        return err;
    }

    public void setErr(ErrorHandler err) {
        this.err = err;
    }

    



    /**
     * Read the content of the file into the layer.
     * Use the {@link #layer} variable to refer to the read layer.
     *
     * @param aRootElement root element of the jdom
     */
    protected abstract void processJdom(Element aRootElement);



    protected Document readXml(final FileObject aFile) throws IOException {
        fileObject = aFile;
        
        // --- read in xml ---
        Reader r = new LineReader(new InputStreamReader(aFile.getInputStream(), Encoding.cUtf8.getId()));       // todo close!!!
        try {
            SAXBuilder builder = new SAXBuilder(false);
            builder.setExpandEntities(false);
            // "org.apache.xerces.parsers.SAXParser"
            //            @todo validation
            //            builder.setFeature("http://xml.org/sax/features/validation", true);
            //            builder.setFeature("http://apache.org/xml/features/validation/schema", true);
            //            builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
            //                "file://c:/Law/schemas/wdata_schema.xml");
            //            builder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            //                "file://c:/Law/schemas/pml_schema.xml");
            return builder.build(r);
        } catch (JDOMException e) {
            fatalError(e, "Error while parsing the XML structure.");
            return null;
        }
    }

// =============================================================================
// <editor-fold desc="Error reporting">
// =============================================================================

    /**
     * Passes a warning to the {@link #err} error handler and returns.
     * 
     * @param aFormat
     * @param aParams 
     */
    public void warning(String aFormat, Object... aParams) {
        err.warning(aFormat, aParams);
    }

    /**
     * Handling a recoverable error. 
     * Just passes a the info to the {@link #err} error handler and returns.
     * @param aThrown
     * @param aFormat
     * @param aParams 
     */
    public void severe(Throwable aThrown, String aFormat, Object... aParams) {
        err.severe(aThrown, aFormat, aParams);
    }

    /**
     * Handling a recoverable error. 
     * Just passes a the info to the {@link #err} error handler and returns.
     * 
     * @param aFormat
     * @param aParams 
     */
    public void severe(String aFormat, Object... aParams) {
        err.severe(aFormat, aParams);
    }

    
    /**
     * Handling a fatal error. 
     * Passes a the info to the {@link #err} error handler, then throws a FormatError.
     * 
     * @param aThrown 
     * @param aFormat
     * @param aParams 
     * @throws FormatError always (assuming the error handler does not throw its own exception). 
     */
    public void fatalError(Throwable aThrown, String aFormat, Object... aParams) {
        String msg = errorMsgHead() + String.format(aFormat, aParams);
        err.fatalError(aThrown, msg);
        throw new FormatError(aThrown, msg); 
        
    }

    /**
     * Handling a fatal error, records it and throws a FormatError.
     * 
     * @param aFormat
     * @param aParams 
     * @throws FormatError always (assuming the error handler does not throw its own exception)
    */
    public void fatalError(String aFormat, Object... aParams) {
        String msg = errorMsgHead() + String.format(aFormat, aParams);
        err.fatalError(msg);
        throw new FormatError(msg); 
    }
    
    protected String errorMsgHead() {
        return "[" +  FileUtil.getFileDisplayName(this.fileObject) + "] ";
    }
    
    
//    //protected void fAssert(href != null, "A reference requires the href attribute.");   // @todo ignore & continue
//    protected void recordProblem(String aMsg) {
//        recordProblem(aMsg, null);
//    }

// =============================================================================
// <editor-fold desc="Better jdom support">
// (this is not static to use the current namespace)
// =============================================================================

    @SuppressWarnings(value = "unchecked")
    protected List<Element> getChildren(Element aElement) {
        return (List<Element>) aElement.getChildren();
    }

    /**
     * Returns a <code>List</code> of all the child elements directly
     * under a specified element that have a specified local name and belong
     * to the {@link #n} namespace.
     *
     * If there are no such elements, an empty List is returned.
     * The returned list is backed by the jdom document.
     *
     * @param aElement the parent element of the potential children
     * @param aName local name for the children to match
     * @return all matching child elements
     */
    @SuppressWarnings(value = "unchecked")
    public List<Element> getChildren(Element aElement, String aName) {
        return (List<Element>) aElement.getChildren(aName, n);
    }

    /**
     * @throws FormatError
     */
    protected Element getReqChild(Element aElement, String aName) {
        Element dtr = aElement.getChild(aName, n);
        if (dtr == null) fatalError("Required element %s not found under %s\nFound elements: %s", aName, aElement.getName(), Cols.toStringNl(aElement.getChildren(), "   "));
        return dtr;
    }

    /**
     * Text is normalized.
     */
    @SuppressWarnings(value = "unchecked")
    protected List<String> getChildrenText(Element aElement, String aName) {
        final List<Element> children = getChildren(aElement, aName);
        if (children.isEmpty()) return Collections.emptyList();

        // --- map(chilren, element -> text) ---
        final List<String> texts = new ArrayList<String>(children.size());
        for (Element child : children) {
            texts.add(child.getTextNormalize());
        }

        return texts;
    }

    public Element getElement(Element aRoot, String... aPath) {
        Element cur = aRoot;
        for (String name : aPath) {
            cur = cur.getChild(name, n);
            if (cur == null) return null;
        }
        return cur;
    }

    /**
     * Reads text from under an element.
     * 
     * Note: all escapes are unescaped
     * 
     * @param aRoot
     * @param aPath
     * @return 
     */
    public String getText(Element aRoot, String... aPath) {
        Element cur = aRoot;
        for (String name : aPath) {
            cur = cur.getChild(name, n);
            if (cur == null) return null;
        }
        return cur.getTextNormalize();
    }

    public Boolean getBoolean(Element aRoot, String... aPath) {
        String str = getText(aRoot, aPath);
    
        return (str == null) ? null : str.equals("1");  
    }

    public Integer getInteger(Element aRoot, String... aPath) {
        return Integer.valueOf( getText(aRoot, aPath) );
    }
    
// </editor-fold>

// =============================================================================
// <editor-fold desc="Reading std structures">
// (this is not static to use the current namespace, 
//  todo: but shouldn't this use some other, standard namespace???)
// =============================================================================
    
    protected Map<String, String> readProperites(Element aElement) {
        final Map<String, String> map = new HashMap<String, String>();
        if (aElement == null) return map;                               // the root element of properties is not present
        
        final List<Element> els = getChildren(aElement, "property");
        for (Element e : els) {
            String attr = getText(e, "attr");
            String val = getText(e, "val");
            map.put(attr, val);
        }
        
        return map;
    }
    
    protected String getAttributeValue(Element aRoot, String... aPath) {
        //??? String[] strs = Cols.<String>subArrayB(aPath, 0, 1);
        Element el = getElement(aRoot, Cols.<String>subArrayB(aPath, 0, 1));
        return (el != null) ? el.getAttributeValue(aPath[aPath.length - 1]) : null;
    }

    /**
     * Ensures that a potential list is uniformly a list. Maps an abbreviated
     * notation to a singleton list.
     *
     * @param aRoot
     * @param aName name of the element
     * @return
     */
    public List<Element> getLM(Element aRoot, String aName) {
        final Element e = aRoot.getChild(aName, n);
        final List<Element> list = getChildren(e, "LM");
        
        return list.isEmpty() ? Cols.asList(e) : list;
    }
    
    /**
     * A single text or a list of texts under LM
     *
     * @return 
     * <ul>
     *  <li>empty list if there is no aName element
     *  <li>a singleton list with text under the aName element
     *  <li>a list with text under the LM elements under the aName element
     * </ul>
     */
    protected List<String> getTextLM(org.jdom.Element aElement, String aName) {
        org.jdom.Element e = aElement.getChild(aName, n);
        //log.info("getTextLM: " + e);
        if (e == null) return Collections.emptyList();
        
        List<org.jdom.Element> lms = getChildren(e, "LM");
        //log.info("  lms: " + lms);
        if (lms.isEmpty()) {
            //log.info("  direct: " + e.getTextTrim());
            return Arrays.asList(e.getTextTrim());
        }
        else {
            List<String> texts = new ArrayList<String>(lms.size());
            for (org.jdom.Element lm : lms) {
                texts.add(lm.getTextTrim());
            }
            return texts;
        }
        
    }
    
// </editor-fold>


}
