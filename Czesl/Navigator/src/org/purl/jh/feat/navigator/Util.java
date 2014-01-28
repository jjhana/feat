/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.navigator;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author j
 */
public class Util {
    // from http://stackoverflow.com/questions/139076/how-to-pretty-print-xml-from-java
    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer(); 
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    public static String prettyFormat(String input) {
        return prettyFormat(input, 2);
    }    
    
    public  static <T> boolean isSwappable(final List<T> aList, final int aIdx1, final int aIdx2) {
        return 
                aIdx1 != -1 &&
                aIdx2 != -1 &&
                aIdx1 != aIdx2 &&
                aIdx1 < aList.size() &&
                aIdx2 < aList.size();
    }
    
    public  static <T> void swapElement(List<T> aList, int aIdx1, int aIdx2) {
        T a = aList.get(aIdx1);
        aList.set(aIdx1, aList.get(aIdx2));
        aList.set(aIdx2, a);
    }
 
}
