package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.layered.LayeredGraph;
import org.purl.jh.feat.layered.WordWidget;
import org.purl.jh.feat.layered.relation.CentralNode;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;


/**
 *
 * @author jirka
 */
public class WidgetClickAction extends WidgetAction.Adapter {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(WidgetClickAction.class);
    
    private final LayeredGraph scene;

    public WidgetClickAction(LayeredGraph aGraphScene) {
        scene = aGraphScene;
    }

    @Override
    public State mouseClicked(Widget widget, WidgetMouseEvent event) {
        log.info("mouseClicked setting focus");
        scene.getView().requestFocus();
        if (widget instanceof WordWidget || widget instanceof CentralNode) { // todo does not work, move to individual widgets?
            scene.setCursor(scene.findObject(widget));
        }
        return State.REJECTED;
    }
}
