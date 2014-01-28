package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import org.purl.jh.feat.util0.visual.XAction;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.event.ActionEvent;
import org.netbeans.api.visual.widget.Widget;

/**
 * Action performed on a widget.
 *
 * @author jirka
 */
public abstract class WAction extends GraphAction implements XAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(WAction.class);

    public WAction(LayeredGraph view, String text, boolean readOnly) {
        super(view, text, readOnly);
    }

    protected WAction(LayeredGraph view, String text) {
        super(view, text);
    }


    public void actionPerformed(ActionEvent e) {
        log.info("WAction.actionPerformed %s", e);
        actionPerformed( ((WidgetActionEvent)e).getWidget() );
    }

    public boolean isEnabled(Widget aWidget) {
        return isEnabled();
    }

    public abstract void actionPerformed(Widget aWidget);
}
