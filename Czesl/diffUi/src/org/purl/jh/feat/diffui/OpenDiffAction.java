package org.purl.jh.feat.diffui;

import org.purl.jh.feat.diffui.util.Util;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.purl.jh.feat.diffui.api.Api;
import org.purl.jh.nbpml.LayerDataObject;
import org.purl.jh.util.Pair;
import org.purl.jh.util.err.XException;


/**
 * Opens diff window (used by context menu on nodes)
 * @author j
 */
@ActionID(
    category = "File",
id = "org.purl.jh.feat.diffui.OpenDiffAction")
@ActionRegistration(
    iconBase = "org/purl/jh/feat/diffui/Merge.gif",
displayName = "#CTL_OpenDiffAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 850),
    @ActionReference(path = "Toolbars/File", position = 900),
    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 100),
    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 100),
    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 100)
})
@Messages("CTL_OpenDiffAction=Compare Annotations")
public final class OpenDiffAction implements ActionListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(OpenDiffAction.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(OpenDiffAction.class);
    
    private final List<DataObject> context;

    public OpenDiffAction() {
        this(Collections.<DataObject>emptyList());
    }
    
    public OpenDiffAction(List<DataObject> context) {
        this.context = context;
    }

    // todo to util, add possibility to extend by additional panel
    //
    public Pair<DataObject,DataObject> getTwoDObjs() {
        if (context.size() != 2) {
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
        else {
            return new Pair<>(context.get(0), context.get(1));
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        Pair<DataObject,DataObject> dobjs = getTwoDObjs();
        if (dobjs == null) return;
        // todo check basic consistency
        Api.openDiff((LayerDataObject<?>)dobjs.mFirst,(LayerDataObject<?>)dobjs.mSecond);
    }
}
