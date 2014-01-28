package org.purl.jh.feat.diffui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.feat.diffui.api.Api;

// todo remove
@ActionID(category = "File",
id = "org.purl.jh.feat.diffui.OpenDiff")
@ActionRegistration(iconBase = "org/purl/jh/feat/diffui/Merge.gif",
displayName = "#CTL_OpenDiff")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 875, separatorAfter = 887),
    @ActionReference(path = "Toolbars/File", position = 1099),
    @ActionReference(path = "Shortcuts", name = "D-D")
})
@Messages("CTL_OpenDiff=Compare Files")
public final class OpenDiff implements ActionListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(OpenDiff.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(OpenDiff.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        final FileObject fobj1 = FileUtil.toFileObject(new File("c:/j/jprojects/czesl.new/files.xml/diff/a/HRD_IE_109_t_1.b.xml"));
        final FileObject fobj2 = FileUtil.toFileObject(new File("c:/j/jprojects/czesl.new/files.xml/diff/b/HRD_IE_109_t_1.b.xml"));
        
        Api.openDiff(fobj1, fobj2);
   }



}
