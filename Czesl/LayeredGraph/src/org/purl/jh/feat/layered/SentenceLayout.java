
package org.purl.jh.feat.layered;

import org.purl.jh.feat.layered.util.LayerLayout;
import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;

/**
 * @todo Extract to a common layout abstract class
 * @author Jirka dot Hana at gmail dot com
 */
public class SentenceLayout extends LayerLayout {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(SentenceLayout.class);


    public void layoutImpl(final Widget aLayer) {
        log.fine("SentenceLayout: lying out ");

        for (Widget widget : aLayer.getChildren()) {
            if (widget instanceof SentenceWidget) {
                positionSentenceWidget((SentenceWidget) widget);
            }
        }
//
//        if (infoFlag) {
//        }
    }

//    boolean infoFlag = false;

    private void positionSentenceWidget(SentenceWidget aSw) {
//        int minX = Integer.MAX_VALUE;
//        int maxX = Integer.MIN_VALUE;
//
//        for (Widget formW : aSw.getFormWidgets()) {
//            int x1 = formW.getLocation().x;
//            int x2 = x1 + formW.getBounds().width;
//            if (x1 < minX) minX = x1;
//            if (x2 > maxX) maxX = x1;
//        }
//
//        Widget w = Cols.getFirstElement( aSw.getFormWidgets() );
//        if (w == null) {
//            return;
//            //todo
//        }
//        int y1 = w.getLocation().y;
//        int h  = w.getBounds().height;
//
//        aSw.resolveBounds(new Point(minX, y1), new Rectangle(0,0, maxX-minX, h));

        log.fine("Sentence layout: s=%s", ((ObjectScene)aSw.getScene()).findObject(aSw) );

        //Insets insets = border.getInsets ();
        Rectangle rectangle = null;
        for (Widget child : aSw.getFormWidgets()) {
            if (! child.isVisible()) continue;

            Point location = child.getLocation ();
            Rectangle bounds = child.getBounds();
            bounds.translate(location.x, location.y);

            if (rectangle == null) {
                rectangle = bounds;
            }
            else {
                rectangle.add(bounds);
            }
        }

        if (rectangle == null) rectangle = new Rectangle();  // todo empty sentence

        //log.finest("  r1=" + rectangle );

        rectangle.grow(15, 10);
        //log.finest("  r2=" + rectangle );
        final Point loc = rectangle.getLocation();
        rectangle.setLocation(0,0);

//        rectangle.x -= insets.left;
//        rectangle.y -= insets.top;
//        rectangle.width += insets.left + insets.right;
//        rectangle.height += insets.top + insets.bottom;
        aSw.resolveBounds(loc, rectangle);
        aSw.setPreferredLocation(loc);
        aSw.setPreferredBounds(rectangle);
    }

    public boolean requiresJustification(Widget widget) {
        return false;
    }

    public void justify(Widget widget) {
    }
}
