package org.purl.jh.feat.layered.actions;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class EdgeDelete extends WAction {

    public EdgeDelete(LayeredGraph aView) {
        super(aView, "Delete Edge");
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
    }

    public void actionPerformed(Widget edgeWidget) {
        Edge edge = (Edge) view.findObject(edgeWidget);
        //RelationWidget.log.info("Deleting edge " + edge);
        //uilog.info("EdgeAddDelAction " + edge);
        edge.getLayer().edgeDel(edge, view, null);
    }

}
