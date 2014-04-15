/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.diffui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.feat.diffui.api.Api;
import org.purl.jh.feat.diffui.util.Util;
import org.purl.jh.nbpml.LayerDataObject;
import org.purl.jh.util.Pair;
import org.purl.jh.util.err.XException;

@ActionID(
        category = "File",
        id = "org.purl.jh.feat.diffui.OpenDiffAction"
)
@ActionRegistration(
        iconBase = "org/purl/jh/feat/diffui/Merge.gif",
        displayName = "#CTL_OpenDiffAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 881),
    @ActionReference(path = "Toolbars/File", position = 375),
    @ActionReference(path = "Shortcuts", name = "D-D")
})
@Messages("CTL_OpenDiffAction=Compare Annotations")
public final class OpenDiffAction implements ActionListener {
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(OpenDiffAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        Pair<DataObject,DataObject> dobjs = getTwoDObjs();
        if (dobjs == null) return;
        // todo check basic consistency
        Api.openDiff((LayerDataObject<?>)dobjs.mFirst,(LayerDataObject<?>)dobjs.mSecond);
    }

    private Pair<DataObject,DataObject> getTwoDObjs() {
        for (;;) {
            final JButton ok = new JButton(bundle.getString("OpenDiffAction.OK"));
            final JButton cancel = new JButton(bundle.getString("OpenDiffAction.Cancel"));
            final OpenPanel p = new OpenPanel(ok);

            NotifyDescriptor nd = new NotifyDescriptor.Confirmation(p, bundle.getString("OpenDiffAction.title"));
            nd.setOptions(new Object[]{ok, cancel});
            Object o = DialogDisplayer.getDefault().notify(nd);

            if (o != ok) return null;
            try {
                DataObject dobj1 = Util.dobj(p.getFile1());
                DataObject dobj2 = Util.dobj(p.getFile2());
                return new Pair<>(dobj1, dobj2);
            }
            catch(XException ex) {
                Util.message(ex, "Cannot open the documents (%s)", ex.getMessage());
            }
        }
    }


}
