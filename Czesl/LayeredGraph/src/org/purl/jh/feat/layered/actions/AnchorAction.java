package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import org.purl.jh.feat.layered.LayeredGraph;
import cz.cuni.utkl.czesl.data.layerx.Position;
import java.awt.Point;
import java.awt.event.ActionEvent;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public abstract class AnchorAction extends GraphAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(AnchorAction.class);

    protected AnchorAction(LayeredGraph aView, String aText) {
        super(aView, aText);
    }

    /**
     *
     * @param aWidget
     * @param aLoc location of the mouse invoking the action; may be null (e.g. when invoked by the keyboard)
     * @param anchor
     * @return
     */
    public boolean isEnabled(Widget aWidget, Point aLoc, Position anchor) {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        view.validate();     // todo still needed?
        Point point =   ((WidgetActionEvent)e).getLocation();
        Widget widget = ((WidgetActionEvent)e).getWidget();  // TODO for some reason this is the whole graph
//            Sentence s = (Sentence) findObject(widget);       // DO
//            log.info("AnchorAction: w=%s. s=%s", widget, s);
        Point scenePoint = widget.convertLocalToScene(point);


        final Position anchor = view.place2anchor(scenePoint);     // todo do not calculate sentence again
        log.info("AnchorAction.actionPerformed: %s", anchor);
        if (anchor == null || anchor.layerRO()) return;

        actionPerformed(anchor, scenePoint);
    }

    protected abstract void actionPerformed(Position aAnchor, Point aPoint);
}
