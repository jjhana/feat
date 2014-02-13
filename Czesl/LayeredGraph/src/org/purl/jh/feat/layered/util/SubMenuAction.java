package org.purl.jh.feat.layered.util;

import org.purl.jh.feat.ea.data.layerx.Position;
import java.awt.Point;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;

/**
 * A list of actions that can be realized as a submenu or inlined.
 * @author jirka
 */
public interface SubMenuAction extends Action {
    List<Action> getActions(final Widget aWidget, final Point aPoint, Position anchor);

    /**
     * Threshold. Inline when smaller or equal.
     * @return
     */
    int inline();
}
