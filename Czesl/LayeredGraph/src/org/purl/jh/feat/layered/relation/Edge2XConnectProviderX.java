package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.layered.LayeredGraph;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.io.PrintWriter;
import org.netbeans.api.visual.widget.LabelWidget;
import org.openide.windows.IOProvider;

/**
 * Connection drawn from an edge's central node or error tag
 * to a form or to another edge (error link).
 *
 * @author Jirka Hana
 */
public class Edge2XConnectProviderX implements ConnectProvider {
    private final static PrintWriter out = IOProvider.getDefault().getIO("Hello", false).getOut();;

    private final LayeredGraph scene;

    private Edge srcEdge = null;
    private Errorr srcErrorInfo = null;

    private Edge targetEdge = null;
    private FForm targetForm = null;

    private FormsLayer<?> srcLayer = null;
    private FormsLayer<?> targetLayer = null;

    public Edge2XConnectProviderX(LayeredGraph aScene) {
        scene=aScene;
    }

    private void reset() {
        targetEdge = null;
        targetForm = null;
        targetLayer = null;
    }

    @Override
    public boolean isSourceWidget(Widget sourceWidget) {
        Object object = scene.findObject(sourceWidget);
        out.println("isSourceWidget" + object);
        if (!(object instanceof Errorr) ) return false;       // should not happen, log error

        srcErrorInfo = (Errorr)object;
        srcEdge = srcErrorInfo.getParent();
        srcLayer = srcEdge.getAncestor(FormsLayer.class);

        return true;
    }

    @Override
    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        reset();

        Object object = scene.findObject(targetWidget);

        if (object instanceof Edge || object instanceof FForm) {   // i.e. Multi-edge center node => adding new leg
             int srcLayerIdx = srcLayer.getLayerIdx();

            if (object instanceof Edge) {   // edge -> edge => error link
                targetEdge = (Edge)object;
                targetLayer = targetEdge.getAncestor(FormsLayer.class);
                if (targetLayer.getLayerIdx() != srcLayerIdx) return ConnectorState.REJECT_AND_STOP;

                // todo check if the error allows it
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
        out.println("Creating connection");
        if (targetEdge != null) {
            out.println("Creating error link");
            scene.addErrorLink(srcErrorInfo, targetEdge);
        }
        else {
            out.println("Creating leg");

            RelationWidget edgeWidget = scene.findRelWidget(srcEdge);

            scene.addLeg(srcEdge, edgeWidget, targetForm, (LabelWidget)targetWidget);
        }
        scene.validate();
    }

}
