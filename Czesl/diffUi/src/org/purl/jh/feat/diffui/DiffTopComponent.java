package org.purl.jh.feat.diffui;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.spi.navigator.NavigatorLookupHint;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.Workspace;
import org.purl.jh.feat.layered.WidgetNode;

/**
 * todo under development
 * todo this is repeating a lot of code that should be encapsulate
 * 
 * Note:
 * DiffPanel is responsible for providing a WidgetNode proxying the current
 * widget in either Scene (Property Windows and other components can listen 
 * to the currently selected node to display its properties, etc).  
 * It also manages a lookup containing the widgetnode'
 * this TC includes the lookup into its lookup. It also listens to the lookup
 * and then sets the ExplorerManager and SelectedNodes accordingly. Maybe
 * there is an easier way how to achieve this.
 * 
 * Note: what if the DiffPanel's selection changes (programmatically) without 
 * this TC having focus?
 *
 * Note: Taken from org.netbeans.modules.diff.builtin.DefaultDiff
 */
public class DiffTopComponent extends TopComponent implements ExplorerManager.Provider {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(DiffTopComponent.class);
    
    //final WidgetNode widgetNode = new WidgetNode();
    /** Manager exposing the currently selected object (as the widgetNode) */
    final ExplorerManager mngr = new ExplorerManager();

    
    InstanceContent lookupContent = new InstanceContent();

    
    public DiffTopComponent(String name, DiffPanel c) {
        setLayout(new BorderLayout());
        add(c, BorderLayout.CENTER);
        getAccessibleContext().setAccessibleName("TODO"); //NbBundle.getMessage(DiffTopComponent.class, "ACSN_Diff_Top_Component")); // NOI18N
        getAccessibleContext().setAccessibleDescription("TODO1"); //NbBundle.getMessage(DiffTopComponent.class, "ACSD_Diff_Top_Component")); // NOI18N
        setName(name);

        Lookup.Result<WidgetNode> widgetNodeLookupResult = c.getLookup().lookupResult(WidgetNode.class);
        widgetNodeLookupResult.addLookupListener(widgetNodeListener);   // todo deregister when closed??
        
        associateLookup(new ProxyLookup(c.getLookup(), new AbstractLookup(lookupContent)));
    
        lookupContent.add(
            new NavigatorLookupHint() {
                public String getContentType() {
                    return "text/feat-model";
                }
            });
        
    }

    private final LookupListener widgetNodeListener = new LookupListener() {
        @Override
        public void resultChanged(final LookupEvent ev) {
            final Lookup.Result<WidgetNode> aLookupResult = ((Lookup.Result<WidgetNode>) ev.getSource());

            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    final Collection<? extends WidgetNode> widgetNodes = aLookupResult == null ? Collections.<WidgetNode>emptyList() : aLookupResult.allInstances();
   
                    if (widgetNodes.isEmpty()) {
                        try {
                            mngr.setSelectedNodes(new Node[]{});
                        } catch (PropertyVetoException ex) {
                            log.severe(ex, "Deselection with empty widget nodes did not work");
                        }
                        setActivatedNodes(new Node[]{});
                    } else {
                        final WidgetNode wnode = widgetNodes.iterator().next();

                        mngr.setRootContext(wnode);
                        setActivatedNodes(new Node[]{wnode});
                    }
                }
            });
        }
    };

    
    
    @Override
    public ExplorerManager getExplorerManager() {
        return mngr;
    }

//   public WidgetNode getWidgetNode() {
//        return widgetNode;
//    }
 
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected String preferredID() {
        return "DiffTopComponent";    //NOI18N
    }
    
    @Override
    public void open(Workspace workspace) {
        super.open(workspace);
        //diffPanel.open();
        requestActive();
    }

    @Override
    public UndoRedo getUndoRedo() {
        return null; //todo undoMngr;
    }
}