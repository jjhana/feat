package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.Logger;



/**
 * Layouts all edges' central nodes in the graph. This is called after all forms nodes
 * have been laid-out but before the legs are.
 *
 * TODO EFF: the algorithm is very inefficient. Currently it compares all widgets with
 * all placed widgets and then again when the position is adjusted.
 * Consider sorting widgets or putting them into buckets location
 *
 * @author Jirka
 */
public final class EdgeLayout implements Layout {
    private static final Logger log = Logger.getLogger(EdgeLayout.class);

    private final LayeredGraph graph;

    public EdgeLayout(LayeredGraph aGraph) {
        graph = aGraph;
    }

    public void layout(final Widget aParentWidget) {
        log.fine("EdgeLayout: laying out " + aParentWidget);
        long start = System.currentTimeMillis();
        new OneTimeLayout(aParentWidget.getChildren()).layout();
        log.fine("Edge layout in %s ms", (System.currentTimeMillis() - start));
    }

    public void layout(final Iterable<? extends Widget> aWidgets) {
        //log.fine("EdgeLayout: laying out list of widgets. First:" + aWidgets.iterator().next());
        log.fine("EdgeLayout: laying out list of widgets. First:" + aWidgets.iterator().next());
        long start = System.currentTimeMillis();
        new OneTimeLayout(aWidgets).layout();
        log.fine("Edge layout in %s ms", (System.currentTimeMillis() - start));
    }

    public boolean requiresJustification(Widget widget) {
        return false;
    }

    public void justify(Widget widget) {
    }

    class OneTimeLayout {
        private final Iterable<? extends Widget> widgets;
        private final List<Widget> placedWidgets = new ArrayList<>();

        public OneTimeLayout(final Iterable<? extends Widget> aWidgets) {
            widgets = aWidgets;
        }

        public OneTimeLayout(final Widget aWidgetLayer) {
            widgets = aWidgetLayer.getChildren();
        }

        public void layout() {
            for (Widget widget : widgets) {
                Err.iAssert(widget instanceof CentralNode, "Unknown widget %s", widget);
                Point loc = ((CentralNode) widget).getBestLocation();
                loc = adjustLocation(loc);

                widget.resolveBounds(loc, null);
                Rectangle bounds = widget.getBounds();
                loc = new Point(loc.x - bounds.width / 2, loc.y - bounds.height / 2);
                widget.resolveBounds(loc, bounds);
                widget.setPreferredLocation(loc);

                addPlacedWidget(widget);
            }
        }

        private Point adjustLocation(final Point aPoint) {
            for (Widget w : placedWidgets) {
                if (isWithin(w, aPoint)) {
                    aPoint.translate(w.getBounds().width+10, 0);
                    return adjustLocation(aPoint);
                }
            }

            return aPoint;
        }

        private boolean isWithin(final Widget aW, final Point aPoint) {
            final Rectangle r = aW.getBounds();
            r.translate(aW.getLocation().x, aW.getLocation().y);

            return r.contains(aPoint);
        }

        private void addPlacedWidget(Widget aCentral ) {
            placedWidgets.add(aCentral);
        }
    }

}
