package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.layered.LayeredGraph;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.awt.Point;
import java.awt.event.ActionEvent;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.widget.LabelWidget;
import org.purl.jh.feat.profiles.ErrorTagset;

/**
 * Connection drawn from an edge to a form or to another edge (error link)
 *
 * @author Jirka Hana
 */
public class Edge2XConnectProvider extends AbstractConnectProvider implements ConnectProvider {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Edge2XConnectProvider.class);    
    
    private Edge srcEdge = null;

    private Edge targetEdge = null;
    private FForm targetForm = null;

    private FormsLayer<?> srcLayer = null;
    private FormsLayer<?> targetLayer = null;

    public Edge2XConnectProvider(final LayeredGraph aScene) {
        super(aScene);
    }

    private void reset() {
        targetEdge = null;
        targetForm = null;
        targetLayer = null;
    }

    @Override
    public boolean isSourceWidget(Widget sourceWidget) {
        Object object = scene.findObject(sourceWidget);
        log.fine("isSourceWidget %s", object);
        if (!(object instanceof Edge) ) return false;       // should not happen, log error

        srcEdge = (Edge)object;
        srcLayer = srcEdge.getAncestor(FormsLayer.class);

        return true;
    }

    @Override
    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        if (sourceWidget == targetWidget) return ConnectorState.REJECT_AND_STOP;

        reset();

        final Object object = scene.findObject(targetWidget);

        if (object instanceof Edge || object instanceof FForm) {   // i.e. Multi-edge center node => adding new leg
             int srcLayerIdx = srcLayer.getLayerIdx();

            if (object instanceof Edge) {   // edge -> edge => error link
                targetEdge = (Edge)object;
                targetLayer = targetEdge.getAncestor(FormsLayer.class);
                if (targetLayer.getLayerIdx() != srcLayerIdx) return ConnectorState.REJECT_AND_STOP;

                // todo check if the error allows it
                if (srcEdge.getErrors().isEmpty()) return ConnectorState.REJECT_AND_STOP;

                return ConnectorState.ACCEPT;
            }
            else {   // edge -> form => adding new leg
                targetForm = (FForm)object;
                targetLayer = targetForm.getAncestor(FormsLayer.class);
                int targetLayerIdx = targetLayer.getLayerIdx();

                return (srcLayerIdx == targetLayerIdx || srcLayerIdx - 1 == targetLayerIdx) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            }
        }
        else {
            return  ConnectorState.REJECT;  //object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }
    }

    @Override
    public boolean hasCustomTargetWidgetResolver(Scene scene) {
        return false;
    }

    @Override
    public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    @Override
    public void createConnection(Widget sourceWidget, Widget targetWidget) {
        if (targetEdge != null) {
            addErrorLink(targetWidget);
        }
        else {
            createEdge(targetWidget);
        }

        scene.validate();
    }

    protected void addErrorLink(Widget targetWidget) {
        log.info("Creating error link srcEdge=%s, target=%s", srcEdge, targetWidget);

        final ErrorTagset tagset = scene.getProfile().getTagset(srcEdge.getLayer());
        
                
        // --- Determine the possible sources ---
        final Set<Errorr> errors = new HashSet<>(srcEdge.getErrors());
        // Remove sources that are not possible ---
        for (Iterator<Errorr> it = errors.iterator(); it.hasNext();) {
            final Errorr ei = it.next();
            
            // Does not allow any link
            if ( tagset.getTag( ei.getTag() ).getMaxLinks() == 0) {
                it.remove();
            }

            // link to this target already exists
            if (ei.getLinks().contains(targetEdge)) {
                it.remove();
            }
        }

        if (errors.isEmpty()) {
            return;     // no revalidation needed, but it does not hurt
        } else if (errors.size() == 1) {
            scene.addErrorLink(errors.iterator().next(), targetEdge);
        } else {
            // display menu with errors to choose from
            final JPopupMenu menu = new JPopupMenu("Menu");
            menu.add(new JMenuItem(new AbstractAction("Choose link source:") {
                public void actionPerformed(ActionEvent e) {} 

                @Override
                public boolean isEnabled() {
                    return false;
                }
            }));

            for (Errorr error : errors) {
                final Errorr errorf = error;

                final Action a = new AbstractAction(error.getTag()) {
                    public void actionPerformed(ActionEvent x) {
                        scene.addErrorLink(errorf, targetEdge);
                    }
                };

                menu.add(new JMenuItem(a));
            }

            final Point sceneLoc = scene.convertSceneToView(targetWidget.convertLocalToScene(new Point(0,0)));
            ///*final Point */sceneLoc = scene.convertSceneToView(targetWidget.convertLocalToScene(targetWidget.getLocation()));
            menu.show(scene.getView(), sceneLoc.x, sceneLoc.y);
        }
    }

    protected void createEdge(Widget targetWidget) {
        log.info("Creating leg srcEdge=%s, target=%s", srcEdge, targetWidget);
        RelationWidget edgeWidget = scene.findRelWidget(srcEdge);
        scene.addLeg(srcEdge, edgeWidget, targetForm, (LabelWidget) targetWidget);
    }

}
