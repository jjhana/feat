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
 * Opens diff window if there are two files selected
 */
@ActionID(
    category = "File",
    id = "org.purl.jh.feat.diffui.OpenDiffCtxAction")
@ActionRegistration(
    iconBase = "org/purl/jh/feat/diffui/Merge.gif",
    displayName = "#CTL_OpenDiffCtxAction")
@ActionReferences({
    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 100),
    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 100),
    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 100)
})
@Messages("CTL_OpenDiffCtxAction=Compare Selected Annotations")
public class OpenDiffCtxAction implements ActionListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(OpenDiffCtxAction.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(OpenDiffCtxAction.class);
    
    private final List<DataObject> context;

    public OpenDiffCtxAction(List<DataObject> context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent ev) {
        if (context.size() == 2) {
            Api.openDiff((LayerDataObject<?>)context.get(0),(LayerDataObject<?>)context.get(1));
        }
    }
}
