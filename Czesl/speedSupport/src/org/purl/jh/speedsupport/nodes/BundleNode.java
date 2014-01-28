package org.purl.jh.speedsupport.nodes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.actions.PropertiesAction;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.purl.jh.speedsupport.data.CmdLayer;
import org.purl.jh.speedsupport.Comment;
import org.purl.jh.speedsupport.Util;
import org.purl.jh.speedsupport.data.Document;
import org.purl.jh.speedsupport.data.SBundle;
import org.purl.jh.util.err.Err;

/**
 * Node corresponding an annotation bundle (set of documents annotated at the same time).
 * The bundle is enclosed in a folder with the same name as the documents.
 * Individual documents are in numbered folders.
 * Currently, it is a leaf, and documents are not presented as nodes.
 * Currently, there is a single cmd file within the bundle folder (but speed interface assumes a singleton array).
 * 
 * When a document is created non-atomically (copied/moved from another place), 
 * first, a flag file is created within the future folder of the bundle, after
 * the operation finishes successfully, the flag-file is deleted. If the original
 * file is to be deleted, it is deleted after the flag-file is removed.
 * 
 * todo what if the flag is deleted, but the delete operation after that fails?
 * See http://platform.netbeans.org/tutorials/nbm-nodesapi.html for details on nodes.
 * 
 * @author j
 */

public class BundleNode extends AbstractNode {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(BundleNode.class);
    private final SBundle model;

//    public BundleNode(BundleNode other) {
//        this(other.getModel(), other.getLookup());      // todo copy lookup?
//    }
    
    public BundleNode(SBundle model, Lookup lookup) {
        super(Children.LEAF, lookup );      // todo use lookup from the bundle
        this.model = model;
    }

    public SBundle getModel() {
        return model;
    }
    
    @Override
    public String getDisplayName() {
        return model.getName();
    }

    public String getFromUserName() {
        final CmdLayer cmdLayer = getModel().getCmdLayer();
        if (cmdLayer == null) return "Error: No cmd file";

        return cmdLayer.getCommand().getTitle();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set ss = sheet.get(Sheet.PROPERTIES);

        final CmdLayer cmdLayer = getModel().getCmdLayer();
        if (cmdLayer == null) return sheet;
        
        //ss.put(new BoxChildFactory1.RoStrProp("Files", Cols.toString(cmdLayer.getFiles(), "", "", ",", "None")));
        Sheet.Set props;

        // identification
        props = new Sheet.Set();
        props.setName("Basics");
        props.setDisplayName("Basics");
        props.setShortDescription("Basic information about the bundle");
        props.put(new RoStrProp("mode", "Mode", "Mode this bundle should be used in (annotation, merge, ...)", getModel().getMode()));        
        props.put(new RoStrProp("docs", "Number of documents", "Number of documents in the bundle", String.valueOf(getModel().getDocuments().size())));
        sheet.put(props);

        props = new Sheet.Set();
        props.setName("Identification");
        props.setDisplayName("Identification");
        props.setShortDescription("Identification of the bundle");

        props.put(new RoStrProp("fromUserName", "From", "User that submitted this bundle", cmdLayer.getFromUserName()));
        props.put(new RoStrProp("sendTime",     "Submitted at", "The time user submitted this bundle at", cmdLayer.getSendTime()));
        sheet.put(props);

        if (cmdLayer.getCommand() != null) {
            sheet.put(props);

            props = new Sheet.Set();
            props.setName("Submission command");
            props.setDisplayName("Submission command");
            props.setShortDescription("Command this file will be submitted with at next synchronization");

            props.put(new RoStrProp("Command", cmdLayer.getCommand().getTitle()));
            props.put(new RoStrProp("My Comment", cmdLayer.getComment()));

            sheet.put(props);
        }

        int i = 1;
        for (Comment comment : cmdLayer.getComments()) {
            props = new Sheet.Set();
            String name = "Comment_" + i;
            props.setName(name);
            props.setDisplayName("Comment " + i);
            props.setShortDescription(i + "th comment associated with this document");


            props.put(new RoStrProp("Made at:", comment.getAt()));
            props.put(new RoStrProp("Made by:", comment.getUser()));
            props.put(new RoStrProp("Text", comment.getText()));

            sheet.put(props);
            i++;
        }

        return sheet;
    }

    @Override
    public boolean canDestroy() {
        return !getModel().isReadOnly() && super.canDestroy();
    }


    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            getPreferredAction(),
            null,
            SystemAction.get(DeleteAction.class),
            //SystemAction.get(RenameAction.class),
            null,
            //SystemAction.get(ToolsAction.class),
            SystemAction.get(PropertiesAction.class)
//            new AbstractAction("Properties") {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    TopComponent t = WindowManager.getDefault().findTopComponent("properties");
//                    if(null != t) t.open();
//                }
//            });
        };
    }

//    public FileObject  getFolder() {
//        return this.getModel().getFolder();
//    }
    
    private boolean checkCorrupted() {
        if (getModel().isCorrupted()) {
            String msg = String.format("Bundle %s corrupted, run cleanup (By pressing Cleanup at the Speed Panel)", getName());
            final NotifyDescriptor nd = new NotifyDescriptor.Message(msg);
            DialogDisplayer.getDefault().notify(nd);
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public Action getPreferredAction() {
        return new AbstractAction("Open Bundle") {

            @Override
            public boolean isEnabled() {
                if (checkCorrupted()) return false;
                // check if the box the bundle is in is read/write
                if ( ((BoxNode)getParentNode()).getModel().isReadOnly() ) return false;
                if (getModel().isReadOnly()) return  false;

                return true;
            }

            
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("preferrred action");
                if (! isEnabled()) return;
                
                switch (getModel().getMode()) {
                    case SBundle.cAnnotate: openForAnnotation(); break;
                    case SBundle.cMerge:    openForMerging(); break;
                    default: return;
                }
            }

            private void openForMerging() {
                if (!getModel().isReady()) {
                    getModel().prepare();   // todo catch errors
                }

                OpenCookie oc = getModel().getMergeCookie();

                if (oc != null) {
                    oc.open();
                }
                else {
                    StatusDisplayer.getDefault().setStatusText("No app found for opening this file!");
                }
            }
            
            private void openForAnnotation() {
                Document doc = getModel().getWorkOnDocument();
                log.info("doc = " + doc);
                // prepare work on document
                if (doc == null) return;
                if (doc.isReadOnly()) return;
                        
                try {
                    DataObject dobj = DataObject.find(doc.getTopFile());
                    OpenCookie oc = dobj.getCookie(OpenCookie.class);
                    log.info("oc = " + oc);
                    if (oc != null) {
                        oc.open();
                    }
                    else {
                        StatusDisplayer.getDefault().setStatusText("No app found for opening this file!");
                    }
                } catch (DataObjectNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }

    

    
// https://blogs.oracle.com/geertjan/entry/drag_a_node_from_a1

//    @Override
//    public PasteType getDropType(Transferable t, int arg1, int arg2) {
//        final Node node = NodeTransfer.node(t, arg1);
//        return new PasteType() {
//            @Override
//            public Transferable paste() throws IOException {
//                names.add(node.getDisplayName());
//                refresh(true);
//                return null;
//            }
//        };
//    }

    
}
