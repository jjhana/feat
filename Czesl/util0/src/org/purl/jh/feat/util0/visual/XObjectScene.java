package org.purl.jh.feat.util0.visual;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import javax.swing.JViewport;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.Logger;

/**
 * Adds cursor and scrolling and few other things to the scene.
 * 
 * @author jirka
 */
public class XObjectScene extends ObjectScene {

    private static final Logger log = Logger.getLogger(XObjectScene.class);
    /**
     * The current object (todo under development) 
     * Cursor points to exactly one widget (except empty graph, where it is null).
     */
    //private Widget cursor = null;
    // --- visual options ---
    private final int cXStart = 50;
    private final int cYStart = 50;
    private int ySpace = 200;
    private int xSpace = 100;

    public XObjectScene() {
    }

    /**
     * Returns location of the widget associated with an object.
     * @param aObj object associated with the widget
     * @return location of the widget (the point object is a copy of the widget's
     *    location object)
     */
    protected Point getLocation(final Object aObj) {
        final Widget widget = findWidget(aObj);
        Err.iAssert(widget != null, "No widget associated with object ", aObj);

        return widget.getLocation();
    }

    public static boolean contains(Widget aWidget, Point aPoint) {
        Point loc = aWidget.getLocation();
        Rectangle bounds = aWidget.getBounds();
        bounds.translate(loc.x, loc.y);

        log.finer("  contains: point %s in w %s (%s)?", aPoint, bounds, bounds.contains(aPoint));


        return bounds.contains(aPoint);
    }

    /**
     * Repaints the view if it exists.
     */
    public void repaintIfPossible() {
        if (getView() != null) getView().repaint();
    }
// =============================================================================
// Cursor
// =============================================================================

    /**
     * Sets the currently focused object and scrolls the corresponding widget to the view if necessary.
     * Resets selection if requested.
     * 
     * Note: The object must be part of the current graph, i.e. it must be a form
     * or an edge in the current paragraph. Currently, we are unable to set cursor
     * to an object in another paragraph.
     * 
     * Note: if {@link ObjectScene.setFocusedObject(Object)} was not final, would 
     * override it.
     * 
     * todo: for some reason it does not update view if focus is elsewhere and 
     * no scrollIntoView is needed
     */
    public void setCursor(Object aObject, boolean aResetSelection) {
        log.info("setCursor: Object " + aObject + " " + aObject.getClass());
        Err.iAssert(findWidget(aObject) != null, "Unknown object %s", aObject);
        
        //cursor = aObject;
        setFocusedObject(aObject);
        
        scrollIntoView(aObject);

        if (aResetSelection) {
            setSelectedObjects(Collections.singleton(aObject));
        }
        
        repaintIfPossible();
    }
    
    /**
     * Sets the currently focused object and scrolls the corresponding widget to the view if necessary.
     * Resets selection.
     * 
     * Note: if {@link ObjectScene.setFocusedObject(Object)} was not final, would 
     * override it.
     */
    public void setCursor(Object aObject) {
        setCursor(aObject, true);
    }
    
// =============================================================================    
// Scrolling
// =============================================================================    

    /**
     * Scrolls the widget corresponding to an object to the view if necessary.
     */
    public void scrollIntoView(Object aObject) {
        Widget w = findWidget(aObject);
        scrollIntoView(w);
    }

    public void scrollIntoView(Widget aW) {
        //if (getView() != null) log.info("getView()" + getView().getClass());

        if  (getView() != null && getView().getParent() != null) {
            Rectangle r = new Rectangle(aW.getLocation(), aW.getBounds() != null ? aW.getBounds().getSize() : new Dimension(5,5));
            log.info("scrollIntoView: %s  - scene: %s", r, convertSceneToView( r ));
            scrollIntoView( convertSceneToView( r ) );
        }
    }
    
    public void scrollTo(Point aLoc) {
        getViewPort().setViewPosition(aLoc);
    }

    public void scrollToX(int aX) {
        scrollTo(new Point(aX, getViewPos().y));
    }

    public void scrollToY(int aY) {
        scrollTo(new Point(getViewPos().x, aY));
    }
    
    private JViewport getViewPort() {
        return (JViewport) getView().getParent();
    }
    
    private Point getViewPos() {
        return getViewPort().getViewPosition();
    }
    
    public void scrollIntoView(Rectangle aRec) {  // todo H/W
        // todo if isRightToLeft then reverse
        revealRightEdge(aRec.x, aRec.width);
        revealLeftEdge(aRec.x);
        revealBottomEdge(aRec.y, aRec.height);
        revealTopEdge(aRec.y);
    }

    
    private void revealLeftEdge(int x) {
        if (x < getViewPos().getX()) {
            scrollToX(x-5);
        }
    }

    private void revealRightEdge(int x, int width) {
        if ( (x + width) > (getViewPos().x + getViewPort().getWidth()) ) {
            scrollToX(x + width - getViewPort().getWidth() + 5);
        }
    }

    private void revealTopEdge(int y) {
        if (y < getViewPos().getY()) {
            scrollToY(y-5);
        }
    }

    private void revealBottomEdge(int y, int height) {
        if ( (y + height) > (getViewPos().y + getViewPort().getHeight()) ) {
            scrollToY(y + height - getViewPort().getHeight() + 5);
        }
    }
    
    
            
            
}
