package org.purl.jh.feat.util0.visual;

import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

import java.awt.*;
import java.awt.event.MouseEvent;
import org.openide.util.Utilities;

/**
 * Like org.netbeans.modules.visual.action.MoveAction, but requires Ctrl to be pressed in addition to mouse dragging.
 * 
 * 
 * Note: Based on org.netbeans.modules.visual.action.MoveAction by David Kaspar
 */
public final class CtrlMoveAction extends WidgetAction.LockedAdapter {

    private MoveStrategy strategy;
    private MoveProvider provider;

    private Widget movingWidget = null;
    private Point dragSceneLocation = null;
    private Point originalSceneLocation = null;
    private Point initialMouseLocation = null;

    private long modifiers;
    private boolean macLocking;
    
    public CtrlMoveAction(MoveStrategy strategy, MoveProvider provider, long modifiers) {
        this.strategy = strategy;
        this.provider = provider;
        this.modifiers = modifiers;
    }

    public CtrlMoveAction(MoveStrategy strategy, MoveProvider provider) {
        this(strategy, provider, MouseEvent.CTRL_MASK /*| c*/);
    }

    protected boolean isLocked() {
        return movingWidget != null ||  macLocking;
    }

    @Override
    public State mousePressed(Widget widget, WidgetMouseEvent event) {
        if (isLocked()) return State.createLocked(widget, this);
        
        System.out.println("CtrlMoveAction");
        System.out.println("widget: " + widget);
        System.out.println("A1: " + event.getModifiers());
        System.out.println("AC : " + ((event.getModifiers() & MouseEvent.CTRL_MASK)));
        System.out.println("ACB: " + ((event.getModifiers() & (MouseEvent.CTRL_MASK | MouseEvent.BUTTON1_DOWN_MASK) )));
        System.out.println("A_ : " + (MouseEvent.CTRL_MASK | MouseEvent.BUTTON1_DOWN_MASK) );
        System.out.println("A==: " + ((event.getModifiers() & modifiers) == modifiers));

//        if ((event.getModifiers() & modifiers) == modifiers) {
        if ( ((event.getModifiers() & modifiers) == modifiers) && (event.getButton () == MouseEvent.BUTTON1 && event.getClickCount () == 1) ) {
            if ((Utilities.getOperatingSystem() & Utilities.OS_MAC) != 0) macLocking = true;

            movingWidget = widget;
            initialMouseLocation = event.getPoint();
            originalSceneLocation = provider.getOriginalLocation(widget);
            if (originalSceneLocation == null)
                originalSceneLocation = new Point();
            dragSceneLocation = widget.convertLocalToScene(event.getPoint());
            provider.movementStarted(widget);
            return State.createLocked(widget, this);
        }
        return State.REJECTED;
    }
    

    @Override
    public State mouseReleased(Widget widget, WidgetMouseEvent event) {
        macLocking = false;
        if (isLocked())
            return orig_mouseReleased(widget,event);
        else
            return State.REJECTED;
    }

        
    private State orig_mouseReleased(Widget widget, WidgetMouseEvent event) {
        boolean state;
        if (initialMouseLocation != null  &&  initialMouseLocation.equals(event.getPoint())) {
            state = true;
        }
        else {
            state = move(widget, event.getPoint());
        }
        
        if (state) {
            movingWidget = null;
            dragSceneLocation = null;
            originalSceneLocation = null;
            initialMouseLocation = null;
            provider.movementFinished(widget);
        }
        
        return state ? State.CONSUMED : State.REJECTED;
    }

    @Override
    public State mouseDragged(Widget widget, WidgetMouseEvent event) {
        return move(widget, event.getPoint()) ? State.createLocked(widget, this) : State.REJECTED;
    }
    
    @Override
    public State mouseMoved (Widget widget, WidgetMouseEvent event) {
        if (macLocking) return mouseDragged(widget, event);
        return super.mouseMoved(widget, event);
    }
    

    private boolean move(Widget widget, Point newLocation) {
        if (movingWidget != widget)
            return false;
        initialMouseLocation = null;
        newLocation = widget.convertLocalToScene(newLocation);
        Point location = new Point(originalSceneLocation.x + newLocation.x - dragSceneLocation.x, originalSceneLocation.y + newLocation.y - dragSceneLocation.y);
        provider.setNewLocation(widget, strategy.locationSuggested(widget, originalSceneLocation, location));
        return true;
    }

}
