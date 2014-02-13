package org.purl.jh.speedsupport.speedPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import org.netbeans.swing.etable.ETableColumn;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOp;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.purl.jh.speedsupport.Main;
import org.purl.jh.speedsupport.nodes.BoxNode;

/**
 * Panel containing all the boxes (inbox, outbox, backup)
 * 
 * @todo handle read-only differently (r/o flag on the files??)
 * @author jirka
 */
public class XBoxPanel extends javax.swing.JPanel implements ExplorerManager.Provider, Lookup.Provider, PropertyChangeListener  {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(XBoxPanel.class);

    protected ExplorerManager manager;
    protected Lookup lookup;
    protected TopComponent tc;

    //private Box inbox, outbox, bakbox;
    private BoxNode inboxNode, outboxNode, bakboxNode;

    protected final OutlineView view;


    public XBoxPanel() {
        setLayout(new BorderLayout());
        view = new OutlineView();
        view.getOutline().setRootVisible(false);

        add(view, BorderLayout.CENTER);
        view.addPropertyColumn("fromUserName", "From");
        view.addPropertyColumn("mode", "Mode");

        // we do not want the boxes to be sorted, only documents
        final ETableColumn etc = (ETableColumn) view.getOutline().getColumnModel().getColumn(0);
        final Comparator comp = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof BoxNode) {
                    // inbox < outboxNode < bakboxNode
                    int order;
                    if (o1 == inboxNode) order = -1;
                    else if (o2 == inboxNode) order = 1;
                    else if (o1 == bakboxNode) order = 1;
                    else order = -1;
                    return order * (etc.isAscending() ? 1 : -1);
                }

                log.info("o1 %s o2 %s", o1.toString(), o2.toString());

                if (o1 instanceof Node) {
                    return ((Node)o1).getDisplayName().compareTo(((Node)o2).getDisplayName());
                }

                // should not happen
                return o1.toString().compareTo(o2.toString());
            }

        };
        etc.setNestedComparator(comp);

        view.getOutline().unsetQuickFilter();

        // to hide the quick filter (it merges boxes)
        //view.getNodePopupFactory().setShowQuickFilter(false); -- does not work
        NodePopupFactory npf = new NodePopupFactory() {
            @Override
            public JPopupMenu createPopupMenu(int row, int column, Node[] selectedNodes, Component component) {
                Action[] actions = NodeOp.findActions (selectedNodes);
                return Utilities.actionsToPopup(actions, component);
            }
        };
        view.setNodePopupFactory(npf);

        manager = new ExplorerManager();
        ActionMap map = getActionMap(); // currently not used
        lookup = ExplorerUtils.createLookup(manager, map);

        manager.addPropertyChangeListener(this);
    }

    public BoxNode getInboxNode() {
        return inboxNode;
    }

    public BoxNode getOutboxNode() {
        return outboxNode;
    }

    public BoxNode getBakboxNode() {
        return bakboxNode;
    }


    public void setTc(TopComponent aTc) {
        tc = aTc;
    }


    public void setPaths(Main main) {
        inboxNode  = new BoxNode(main.getInbox());
        outboxNode = new BoxNode(main.getOutbox());
        bakboxNode = new BoxNode(main.getBackup());

        ChildFactory.Detachable<String> boxes = new ChildFactory.Detachable<String>() {
            @Override
            protected boolean createKeys(List<String> toPopulate) {
                toPopulate.add("Inbox");
                toPopulate.add("Outbox");
                //todo toPopulate.add("Backup");
                return true;
            }

            @Override
            protected Node createNodeForKey(String key) {
                if ("Inbox".equals(key)) {
                    return inboxNode;
                }
                else if ("Outbox".equals(key)) {
                    return outboxNode;
                }
                else { //if ("Backup".equals(key)) {
                    return bakboxNode;
                }
            }
        };

        Children kids = Children.create(boxes, false);
        Node rootNode = new AbstractNode(kids);

        manager.setRootContext(rootNode);

        refresh();  // should not be neede as the result changed should be called, but for some reason ...
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == manager &&
                ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
           tc.setActivatedNodes(manager.getSelectedNodes());
        }
    }

    public void refresh() {
        inboxNode.refresh();
        outboxNode.refresh();
        bakboxNode.refresh();
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

//    // ...methods as before, but replace componentActivated and componentDeactivated with e.g.:
//    @Override
//    public void addNotify() {
//        super.addNotify();
//        ExplorerUtils.activateActions(manager, true);
//    }
//
//    @Override
//    public void removeNotify() {
//        ExplorerUtils.activateActions(manager, false);
//        super.removeNotify();
//    }
}
