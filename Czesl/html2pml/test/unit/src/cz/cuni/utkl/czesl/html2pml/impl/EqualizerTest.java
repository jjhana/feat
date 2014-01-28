package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.Equalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 *
 * @author jirka
 */
public class EqualizerTest {
    
    public EqualizerTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void testAdd_GenericType() {
    }

    @org.junit.Test
    public void testAdd_Collection() {
        Equalizer eq = new Equalizer();
        eq.add("a", "b");
        eq.add("b", "c");
        eq.add("e", "f");
        eq.add("e", "g");

        Set<String> x = new HashSet<String>(Arrays.asList("a", "b", "c"));
        assertEquals(x,  eq.getClass("a"));
    }

    @org.junit.Test
    public void testGetClass() {
    }

    @org.junit.Test
    public void testGetClasses() {
    }
}
