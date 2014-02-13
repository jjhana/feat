package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerl.LForm;
import org.purl.jh.feat.ea.data.layerx.FForm;
import org.purl.jh.feat.ea.data.layerx.FormsLayer;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;

/**
 * Object responding to user drawing an edge.
 * 
 * Connection drawn from a form to an edge or (todo) directly to another form
 *
 * @author
 */
public class Form2XConnectProvider<N,E>  extends AbstractConnectProvider implements ConnectProvider {
    private FForm srcForm = null;

    private Edge targetEdge = null;
    private FForm targetForm = null;
    private FormsLayer<?> srcLayer = null;
    private FormsLayer<?> targetLayer = null;

    public Form2XConnectProvider(LayeredGraph scene) {
        super(scene);
    }

    private void reset() {
        targetEdge = null;
        targetForm = null;
        srcLayer = null;
        targetLayer = null;
    }

    @Override
    public boolean isSourceWidget(Widget sourceWidget) {
        Object object = scene.findObject(sourceWidget);
        if (!(object instanceof FForm) ) return false;       // should not happen

        srcForm = (FForm)object;

        return true;
    }

    @Override
    public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
        reset();

        Object object = scene.findObject(targetWidget);

        if (object instanceof Edge || object instanceof FForm) {   // i.e. Multi-edge center node => adding new leg
             srcLayer = srcForm.getAncestor(FormsLayer.class);
             int srcLayerIdx = srcLayer.getLayerIdx();

            if (object instanceof Edge) {   // i.e. Multi-edge center node => adding new leg
                //out.println("isTargetWidget: E");
                targetEdge = (Edge)object;
                targetLayer = targetEdge.getAncestor(FormsLayer.class);

//                io.getOut().println("Target Layer " + targetLayer);
                int targetLayerIdx = targetLayer.getLayerIdx();
//                io.getOut().println("Target Layer " + targetLayerIdx + " dif=" + Math.abs(srcLayerIdx - targetLayerIdx));
                return (srcLayerIdx == targetLayerIdx || srcLayerIdx + 1 == targetLayerIdx) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
            }
            else {   // => adding new edge
                //out.println("isTargetWidget: V");
                targetForm = (FForm)object;
                targetLayer = targetForm.getAncestor(FormsLayer.class);
                int targetLayerIdx = targetLayer.getLayerIdx();

//                return (Math.abs(srcLayerIdx - targetLayerIdx) == 1) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
                ConnectorState s = (Math.abs(srcLayerIdx - targetLayerIdx) == 1) ? ConnectorState.ACCEPT : ConnectorState.REJECT_AND_STOP;
//                io.getOut().println("  State " + s);
//                io.getOut().println( srcLayerIdx + " " + targetLayerIdx);

                return s;
            }
        }
        else {  // sentence
            //out.println("isTargetWidget: not E/V");
            return ConnectorState.REJECT; 
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
        //out.println("Creating connection");
        if (targetEdge != null) {
            //out.println("  Creating leg");
            RelationWidget edgeWidget = scene.findRelWidget(targetEdge);
            scene.addLeg(targetEdge, edgeWidget, srcForm, scene.findFormWidget(srcForm));      // todo in model
        }
        else {
            //out.println("  Creating edge");
            final boolean srcLayerLower = srcLayer.getLayerIdx() < targetLayer.getLayerIdx();

            FForm lowerForm  =          srcLayerLower ?    srcForm : targetForm;
            LForm  higherForm  = (LForm) (srcLayerLower ? targetForm :    srcForm);

            scene.addEdge(lowerForm, higherForm);
        }
    }

}
