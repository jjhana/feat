package cz.cuni.utkl.czesl.main;

import org.purl.jh.feat.ea.data.layerl.LForm;
import org.purl.jh.feat.ea.data.layerl.LPara;
import org.purl.jh.feat.ea.data.layerl.Sentence;
import org.purl.jh.feat.layered.ParaModel;
import org.purl.jh.feat.layered.PseudoModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.purl.jh.nbpml.LayerProvider;
import org.purl.jh.pml.Layer;
import org.xml.sax.SAXException;

/**
 * todo under development
 * @author jirka
 */
public class IOModelTest extends XMLTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    File projDir;

    public void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);

        System.out.println("dir: " + computeTestDataRoot(this.getClass())); // todo
        projDir = new File("c:\\data\\jprojects\\czesl\\apps\\Czesl\\Main\\");  // todo obtain automatically
    }

    public static File computeTestDataRoot(Class anyTestClass) {
        final String clsUri = anyTestClass.getName().replace('.', '/') + ".class";
        final URL url = anyTestClass.getClassLoader().getResource(clsUri);
        final String clsPath = url.getPath();
        final File root = new File(clsPath.substring(0, clsPath.length() - clsUri.length()));
        return new File(root, clsUri);
    }

    // todo under development
    public void testForEquality() throws Exception {
        setup();
        FileObject inDir  = FileUtil.toFileObject(new File(projDir, "test\\iomodel\\in"));
        FileObject okDir  = FileUtil.toFileObject(new File(projDir, "test\\iomodel\\ok"));
        FileObject testDir = FileUtil.toFileObject(new File(projDir, "build\\test\\unit\\iomodel"));
        
        
        
        FileObject test1Dir = inDir.copy(testDir, "io", null);


        for (FileObject file : test1Dir.getChildren()) {
            if (!file.getNameExt().endsWith(".b.xml")) continue;

            manipulate(file);
            
            //compare(file, okDir);
        }

//        File srcDir  = new File(projDir, "test\\iomodel\\");
//        File testDir = new File(projDir, "build\\test\\unit\\iomodel");
//
//        copyTestDir(new File(srcDir, "in"), testDir);
//
//
//        for (File inFile : testDir.listFiles()) {
//            if (!inFile.getAbsolutePath().endsWith(".b.xml")) continue;
//
//            manipulate(inFile);
//            
//            compare(inFile, new File(srcDir, "ok"));
//        }
    }
    
    private void manipulate(FileObject aFile) throws Exception {
//        System.out.printf("file=%s, %s, fo=%s\n", aFile, aFile.exists(), FileUtil.toFileObject(aFile));
        DataObject dobj = DataObject.find(aFile);
        
        LayerProvider layerProvider = dobj.getNodeDelegate().getLookup().lookup(LayerProvider.class);
        Layer<?> layer = layerProvider.getLayer();

        final UndoRedo.Manager undoMngr = new UndoRedo.Manager();
        final PseudoModel model = new PseudoModel(layer, undoMngr);
        
        // some editing
        final int size = model.getParas().size();
        int curPara = 0;
        
        ParaModel paraModel = model.getParaModel(curPara);

        // change the first form
        List<LForm> forms = paraModel.getNodes(1);
        paraModel.formEdit(forms.get(0), "test", null, null);
        
        paraModel.formDel(forms.get(2), null, null);
        LPara para = (LPara) paraModel.getParas().get(1);
        Sentence s = para.getSentences().get(0);
        
        paraModel.sentenceCopyHigher(s, null, null);
        //paraModel.formAdd(2, null, null);

        model.save();
    }
    
    

    protected void copyTestDir(File aStoredFiles, File aTestDir) throws IOException {
        aTestDir.mkdirs();
        
        for (File src : aStoredFiles.listFiles()) {
            org.purl.jh.util.io.IO.copyFile(src, new File(aTestDir, src.getName()));
        }
    }
    
    
    
    private void compare(File outFile, File okFile) throws FileNotFoundException, SAXException, IOException {
        Reader rok   = new FileReader(okFile);
        Reader rtest = new FileReader(outFile);
        assertXMLEqual(rok, rtest);
    }
}
