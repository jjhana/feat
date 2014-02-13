package cz.cuni.utkl.czesl.html2pml;

import cz.cuni.utkl.czesl.html2pml.Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.purl.jh.util.io.Files;
import org.xml.sax.SAXException;

/**
 *
 * @author jirka
 */
public class MainTest extends XMLTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    File projDir;

    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);

        System.out.println("dir: " + computeTestDataRoot(this.getClass())); // todo
        projDir = new File("c:\\data\\jprojects\\czesl\\apps\\Czesl\\html2pml\\");  // todo obtain automatically
    }

    public static File computeTestDataRoot(Class anyTestClass) {
        final String clsUri = anyTestClass.getName().replace('.', '/') + ".class";
        final URL url = anyTestClass.getClassLoader().getResource(clsUri);
        final String clsPath = url.getPath();
        final File root = new File(clsPath.substring(0, clsPath.length() - clsUri.length()));
        return new File(root, clsUri);
    }

    // todo check error reporting
    public void testForEquality() throws Exception {
        setup();

        File outDir = new File(projDir, "build\\test\\unit\\files\\");
        outDir.mkdirs();

        File inDir = new File(projDir, "test\\ok_files\\");
        //File inDir = new File(projDir, "test\\now\\");

        for (File inFile : inDir.listFiles()) {
            if (!inFile.getAbsolutePath().endsWith(".html")) {
                continue;
            }

            File outPrefix = new File(outDir, Files.removeExtension(inFile.getName()));

            File outFile = outPrefix; //new File(outDir,inFile.getName());
            System.out.println("outFile: " + outFile);
            System.out.println("intFile: " + inFile);

            new Main().translate(inFile, outFile);

            compareFile(inFile, outFile, "w.xml");
            compareFile(inFile, outFile, "a.xml");
            compareFile(inFile, outFile, "b.xml");
        }
    }

    private void compareFile(File inFile, File outFile, String aEnding) throws FileNotFoundException, SAXException, IOException {
        File okFile = Files.replaceExtension(inFile, "html", aEnding);
        Reader rok = new FileReader(okFile);
        Reader rtest = new FileReader(outFile + "." + aEnding);
        assertXMLEqual(rok, rtest);
    }
}
