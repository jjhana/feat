package org.purl.jh.feat.layered.actions;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class ErrorAdd extends WAction {

    public ErrorAdd(LayeredGraph aView) {
            super(aView, "Add Error");
    }

    public void actionPerformed(Widget edgeWidget) {
        Edge edge = (Edge) view.findObject(edgeWidget);
        //RelationWidget.log.info("Adding error to edge " + edge);
        edge.getLayer().errorAdd(edge, view, null);
    }

}
