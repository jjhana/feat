package org.purl.jh.feat.layered.relation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.widget.ConnectionWidget;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public enum ErrorLinkRouter implements Router {
    INSTANCE;


    public List<Point> routeConnection(ConnectionWidget widget) {
        final ArrayList<Point> list = new ArrayList<>();

        final Anchor sourceAnchor = widget.getSourceAnchor();
        final Anchor targetAnchor = widget.getTargetAnchor();
        if (sourceAnchor == null || targetAnchor == null) {
            return Collections.emptyList();
        }

        final Point srcLoc = sourceAnchor.compute(widget.getSourceAnchorEntry()).getAnchorSceneLocation();
        final Point targetLoc = targetAnchor.compute(widget.getTargetAnchorEntry()).getAnchorSceneLocation();

        final int y = srcLoc.y - 10;
        final int dx = (srcLoc.x < targetLoc.x) ? 10 : -10;

        list.add(srcLoc);
        list.add(new Point(srcLoc.x + dx, y));
        list.add(new Point(targetLoc.x - dx, y));
        list.add(targetLoc);

        return list;
    }
}
