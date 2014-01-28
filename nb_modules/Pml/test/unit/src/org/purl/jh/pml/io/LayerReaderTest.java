package org.purl.jh.pml.io;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.err.Err;
import static org.junit.Assert.*;
import org.purl.jh.util.err.XException;

/**
 *
 * @author jirka
 */
public class LayerReaderTest {

    public LayerReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadLayerErrs() {
    }

    @Test
    public void testReadLayer() throws Exception {
    }

    @Test
    public void testCreateLayer() {
    }

    @Test
    public void testProcessJdom() {
    }

    @Test
    public void testProcessHead() {
    }

    @Test
    public void testReadReferences() {
    }

    @Test
    public void testProcessTagsets() {
    }

    @Test
    public void testGetTextLM() {
    }

    @Test
    public void testGetChildrenText() {
    }

    @Test
    public void testGetReqChild() {
    }

    @Test
    public void testGetElement() {
    }

    @Test
    public void testGetAttributeValue() {
    }

    @Test
    public void testGetText() {
    }

    @Test
    public void testGetChildren_Element_String() {
    }

    @Test
    public void testGetChildren_Element() {
    }

    @Test
    public void testGetRf() {
    }

    @Test
    public void testGetRfs() {
        LayerReader r = newX();

        Element root = readXml("<w.rf>w#w-doc1p1s2w5</w.rf><w.rf>w#w-doc1p1s2w6</w.rf>");
        List<String> rfs = r.getRfs(root, "w");
        assertEquals(rfs, Arrays.asList("w#w-doc1p1s2w5", "w#w-doc1p1s2w6"));

        root = readXml("<w.rf>w#w-doc1p1s2w5</w.rf>");
        rfs = r.getRfs(root, "w");
        assertEquals(rfs, Arrays.asList("w#w-doc1p1s2w5"));

        root = readXml("<a.rf>w#w-doc1p1s2w5</a.rf>");      // empty list
        rfs = r.getRfs(root, "w");
        assertEquals(rfs, Arrays.asList());
    }

    /*
     * <pre>
     *    <w.rf>
     *      <LM>w#w-doc1p1s2w5</LM>
     *      <LM>w#w-doc1p1s2w6</LM>
     *    </w.rf>
     * </pre>
     *
     * Singleton list can be abbreviated as:
     * <pre>
     *    <w.rf>w#w-doc1p1s2w5</w.rf>
     * </pre>
     */
    @Test
    public void testGetRfLM() {
        LayerReader r = newX();

        Element root = readXml("<w.rf><LM>w#w-doc1p1s2w5</LM><LM>w#w-doc1p1s2w6</LM></w.rf>");
        List<String> rfs = r.getRfLM(root, "w");
        assertEquals(rfs, Arrays.asList("w#w-doc1p1s2w5", "w#w-doc1p1s2w6"));

        root = readXml("<w.rf>w#w-doc1p1s2w5</w.rf>");
        rfs = r.getRfLM(root, "w");
        assertEquals(rfs, Arrays.asList("w#w-doc1p1s2w5"));

        root = readXml("<w.rf><LM>w#w-doc1p1s2w5</LM></w.rf>");
        rfs = r.getRfLM(root, "w");
        assertEquals(rfs, Arrays.asList("w#w-doc1p1s2w5"));
    }

    @Test
    public void testGetLM() {
//        LayerReader r = newX();
//        Element root = readXml("<w.rf><LM>w#w-doc1p1s2w5</LM><LM>w#w-doc1p1s2w6</LM></w.rf>").getRootElement();
//        List<Element> rfEs = r.getLM(root, "w.rf");
//        System.out.println(rfEs);
    }

    @Test
    public void testResolveRfE() {
    }

    @Test
    public void testResolveRfEs() {
    }

    @Test
    public void testGetRfx() {
    }

    @Test
    public void testGetRfsx() {
    }

    @Test
    public void testResolveRfEx() {
    }

    @Test
    public void testResolveRfEsx() {
    }

    @Test
    public void testReadXml() throws Exception {
    }

    public final static Namespace n = Namespace.getNamespace("abc");

     protected org.jdom.Element readXml(String aStr) {
        aStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
               "<root xmlns=\"abc\">" +
               aStr +
               "</root>";

         try {
            SAXBuilder builder = new SAXBuilder(false);
            return builder.build(new StringReader((aStr))).getRootElement();
        }
        catch (IOException ex) {
            throw new XException(ex);
        }
        catch (org.jdom.JDOMException e) {
            throw Err.fErr(e, "Error while parsing the XML structure");
        }
    }

    public LayerReader newX() {
        return new LayerReader(n) {
            @Override
            protected Layer createLayer() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            protected void processJdom(Element aElement) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

}