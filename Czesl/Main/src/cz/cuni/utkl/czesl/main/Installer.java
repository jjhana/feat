package cz.cuni.utkl.czesl.main;

import java.io.File;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 * Manages a module's lifecycle.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // opening testing file (used during debugging, ignored when the file does not exist)
        //if (true) return;       // debug property
        try {

            
            File refFile = new File("c:\\j\\jprojects\\czesl.new\\files.xml\\dk_ac_039_00_t_1.b.xml");
            FileObject refFileObject = FileUtil.toFileObject(refFile);
            if (refFileObject != null) {
                DataObject dobj = DataObject.find(refFileObject);
                OpenCookie open = dobj.getLookup().lookup(OpenCookie.class);
                if (open != null) open.open();
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
