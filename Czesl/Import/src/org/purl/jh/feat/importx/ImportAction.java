package org.purl.jh.feat.importx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.purl.net.jh.nbutil.Notify;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionReference;
import org.purl.jh.util.CountingLogger;

@ActionID(category = "File",
id = "org.purl.jh.feat.importx.Import")
@ActionRegistration(
        iconBase = "org/purl/jh/feat/importx/document_import.gif",
        displayName = "#CTL_Import")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 850),
    @ActionReference(path = "Toolbars/File", position = 350)
})
//@Messages("CTL_Import=Import")
public final class ImportAction implements ActionListener {

    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ImportAction.class);
    //private final static org.purl.net.jh.nbutilxx.ResourceBundle bundle = org.purl.net.jh.nbutilxx.ResourceBundle.getBundle(Import.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(ImportAction.class);

    public void actionPerformed(ActionEvent e) {
        final JButton ok = new JButton(bundle.getString("import.OK"));
        final JButton cancel = new JButton(bundle.getString("import.Cancel"));

        cancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) {
                //close whole application
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent arg0) {
                log.info("OK!!!!");
                //authenicate username and password
            }
        });

        final ImportPanel p = new ImportPanel(ok);

        //for (;;) {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(p, bundle.getString("import.title"));
        nd.setOptions(new Object[]{ok, cancel});
        Object o = DialogDisplayer.getDefault().notify(nd);

        if (o != ok) return;


        log.info("data=%s", p.getData());
        if (!convert(p.getData().getInFile(), p.getData().getOutPrefix(), p.getData().getCopyInFile())) return;

        final File newBFile = new File(p.getData().getOutPrefix() + ".b.xml");
        final FileObject newBFileObj = FileUtil.toFileObject(newBFile);

        DataObject dobj = null;
        try {
            dobj = DataObject.find(newBFileObj);
        } catch (DataObjectNotFoundException ex) {
            String msg = String.format("Cannot open %s -- the result of importing %s.",  newBFile, p.getData().getInFile());
            Notify.error(ex, msg);
            log.severe(ex, msg);
            return; // todo
        }

        OpenCookie open = dobj.getCookie(OpenCookie.class);
        Notify.eAssert(open != null, "Imported file %s (%s) cannot be open", newBFile, dobj);
        // todo add logging
        open.open();
        //}
    }

    private boolean convert(final File aInFile, final File aOutPrefix, final boolean aCopyInFile ) {
        final FileObject inFileFo = FileUtil.toFileObject(aInFile);

        // --- output directory ---
        final File outDir = aOutPrefix.getParentFile();

        final FileObject outDirFo;
        try {
            outDirFo = FileUtil.createFolder(outDir); // todo catch exceptions
            log.info("FileUtil.createFolder - ok (%s) %s", outDir, outDirFo );
        } catch (IOException ex) {
            log.info("Import not successfull: Cannot create destination directory %s", outDir);
            Notify.error(ex, "Import not successfull: Cannot create destination directory %s", outDir);
            return false;
        }
//        // workaround for a FileUtil.createFolder bug
//        if (!outDirFo.isFolder()) {
//            Notify.error("Import not successfull: Cannot create destination directory %s, there is a file with that name.", outDir);
//            return false;
//        }

        final String outName = aOutPrefix.getName();

        // copy html/jpg if prefix different

        //final File base = new File(inFile.getParent(), new XFile(inFile).getNameOnly());
        //final XFile outFileBase = new XFile(base, Encoding.cUtf8);

        FileObject outHtmlFileFo;
        if (aCopyInFile) {
            try {
                outHtmlFileFo = FileUtil.copyFile(inFileFo, outDirFo, outName, "html");
            } catch (IOException ex) {
                log.info("Import not successfull: Cannot copy imported file %s ot %s %s.html", inFileFo, FileUtil.getFileDisplayName(outDirFo), outName);
                Notify.error(ex, "Import not successfull: Cannot copy imported file %s ot %s %s.html", inFileFo, FileUtil.getFileDisplayName(outDirFo), outName);
                return false;
            }
        }
        else {
            outHtmlFileFo = inFileFo;
        }

        // todo ? copy jpg?

        final File outPrefix = new File(outDir,outName);
        if (!new cz.cuni.utkl.czesl.html2pml.Main().translate(FileUtil.toFile(outHtmlFileFo), outPrefix, new CountingLogger(log.getLg())) ) { // todo log to some log window
            Notify.error("Import not successfull: There were errors during html to pml conversion.");
            // todo report some error
            return false;
        }
        return true;
    }



}
