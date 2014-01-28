package org.purl.jh.feat.layered.actions;


import org.purl.jh.feat.layered.LayeredGraph;
import org.purl.jh.feat.layered.WordWidget;
import org.purl.jh.feat.layered.relation.CentralNode;
import java.awt.Component;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

/**
 * When the scene is clicked, sets the the focus to scene's component.
 * @author jirka
 */
public class SceneClickAction  extends WidgetAction.Adapter {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(SceneClickAction.class);
    
    private final Component component;
    private final LayeredGraph scene;

    public SceneClickAction(LayeredGraph aGraphScene) {
        scene = aGraphScene;
        component = aGraphScene.getView();
    }

    @Override
    public State mouseClicked(Widget widget, WidgetMouseEvent event) {
        log.fine("setting focus");
        component.requestFocus();
        if (widget instanceof WordWidget || widget instanceof CentralNode) { // todo does not work, move to individual widgets?
            scene.setCursor(widget);
        }
        else {
            log.fine("mouseClicked - 2 %s", widget.getClass());
        }
        return State.REJECTED;
    }


//    public State mousePressed (Widget widget, WidgetMouseEvent event) {
//        // todo
//    }

}
