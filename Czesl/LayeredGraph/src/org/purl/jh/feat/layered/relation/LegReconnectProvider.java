package org.purl.jh.feat.layered.relation;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.Point;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.util.Pair;

/**
 *
 * @author Jirka
 */
public class LegReconnectProvider extends AbstractConnectProvider implements org.netbeans.api.visual.action.ReconnectProvider {

    public LegReconnectProvider(final LayeredGraph aScene) {
        super(aScene);
    }

    public void reconnectingStarted(ConnectionWidget connectionWidget, boolean reconnectingSource) {
    }

    public void reconnectingFinished(ConnectionWidget connectionWidget, boolean reconnectingSource) {
    }

    public boolean isSourceReconnectable(ConnectionWidget connectionWidget) {
        return true;
        //return false;
//        Object object = findObject(connectionWidget);
//        edge = isEdge(object) ? (String) object : null;
//        originalNode = edge != null ? getEdgeSource(edge) : null;
//        return originalNode != null;
    }

    public boolean isTargetReconnectable(ConnectionWidget connectionWidget) {
        return true;
//        Object object = findObject(connectionWidget);
//        edge = isEdge(object) ? (String) object : null;
//        originalNode = edge != null ? getEdgeTarget(edge) : null;
//        return originalNode != null;
    }

    public ConnectorState isReplacementWidget(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
//        Object object = findObject(replacementWidget);
//        replacementNode = isNode(object) ? (String) object : null;
//        if (replacementNode != null) {
//            return ConnectorState.ACCEPT;
//        }
//        return object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;

        final Object object = scene.findObject(replacementWidget);

        if (object instanceof FForm) {   // i.e. Multi-edge center node => adding new leg
            final Pair<Edge,FForm> ef = scene.getLeg2objs().get(connectionWidget);
            final int srcLayerIdx = ef.mFirst.getLayer().getLayerIdx();

            final FForm targetForm = (FForm)object;
            final int targetLayerIdx = ((LLayer)targetForm.getLayer()).getLayerIdx();

            return (srcLayerIdx == targetLayerIdx || srcLayerIdx - 1 == targetLayerIdx) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
        }
        else {
            return  ConnectorState.REJECT;  //object != null ? ConnectorState.REJECT_AND_STOP : ConnectorState.REJECT;
        }

    }

    public boolean hasCustomReplacementWidgetResolver(Scene scene) {
        return false;
    }

    public Widget resolveReplacementWidget(Scene scene, Point sceneLocation) {
        return null;
    }

    public void reconnect(ConnectionWidget connectionWidget, Widget replacementWidget, boolean reconnectingSource) {
        //scene.removeLeg(edge);

        final Pair<Edge,FForm> ef = scene.getLeg2objs().get(connectionWidget);
        final FForm newForm = scene.findForm(replacementWidget);
        scene.addLeg(ef.mFirst, null, newForm, null);
    }
}
