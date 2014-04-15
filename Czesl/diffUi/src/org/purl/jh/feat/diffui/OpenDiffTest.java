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

@ActionID(category = "File", id = "org.purl.jh.feat.diffui.OpenDiffTest")
@ActionRegistration(iconBase = "org/purl/jh/feat/diffui/Merge.gif",
    displayName = "#CTL_OpenDiffTest")
@ActionReferences({
    @ActionReference(path = "Shortcuts", name = "DS-D")
})
@Messages("CTL_OpenDiffTest=Compare Files (Test)")
public final class OpenDiffTest implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        final FileObject fobj1 = FileUtil.toFileObject(new File("c:/testfiles/feat/diff/a/HRD_IE_109_t_1.b.xml"));
        final FileObject fobj2 = FileUtil.toFileObject(new File("c:/testfiles/feat/diff/b/HRD_IE_109_t_1.b.xml"));
        
        Api.openDiff(fobj1, fobj2);
   }
}
