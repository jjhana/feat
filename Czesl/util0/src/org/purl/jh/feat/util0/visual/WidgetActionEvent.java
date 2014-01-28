package org.purl.jh.feat.util0.visual;

import java.awt.Point;
import java.awt.event.ActionEvent;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class WidgetActionEvent extends ActionEvent {
    private final Widget widget;
    private final Point location;

    public WidgetActionEvent(final ActionEvent aE, final Widget aWidget, final Point aLocation) {
        super(aE.getSource(), aE.getID(), aE.getActionCommand());
        widget = aWidget;
        location = aLocation;
    }

    public Widget getWidget() {
        return widget;
    }

    public Point getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "WidgetActionEvent{" + "widget=" + widget + "location=" + location + '}';
    }
    
}
