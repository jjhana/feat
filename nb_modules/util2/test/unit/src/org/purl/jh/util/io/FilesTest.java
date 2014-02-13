/*
 * FilesTest.java
 * JUnit based test
 *
 * Created on February 27, 2006, 2:34 PM
 */

package org.purl.jh.util.io;

//import com.sun.java_cup.internal.assoc;
import junit.framework.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Jirka
 */
public class FilesTest extends TestCase {
    
    public FilesTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FilesTest.class);
        
        return suite;
    }

    public void testRemoveExtension() {
    }

    public void testRemovePossExtension() {
    }

    public void testGetExtensions() {
        assertTrue(Files.getExtensions(new File("file.ab.cd.ef")).equals(Arrays.asList("ef", "cd", "ab")));
        assertTrue(Files.getExtensions(new File("file")).equals(Arrays.asList()));
        assertTrue(Files.getExtensions(new File("file.ab..ef")).equals(Arrays.asList("ef", "", "ab")));
        assertTrue(Files.getExtensions(new File("file.ab..ef.")).equals(Arrays.asList("", "ef", "", "ab")));
        assertTrue(Files.getExtensions(new File(".ab.cd.ef")).equals(Arrays.asList("ef", "cd", "ab")));
    }

    public void testGetExtension() {
    }

    public void testAddBeforeExtension() {
    }

    public void testAddExtension() {
    }

    public void testReplaceExtension() {
    }

    public void testReplaceDir() {
    }

    public void testFilesToXFiles() {
    }

    public void testXFiles() {
    }
    
}
