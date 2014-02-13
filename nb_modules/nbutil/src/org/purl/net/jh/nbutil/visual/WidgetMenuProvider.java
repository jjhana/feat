package org.purl.net.jh.nbutil.visual;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;
import org.purl.net.jh.nbutil.NbUtil;

/**
 * Menu associated with a widget.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class WidgetMenuProvider implements PopupMenuProvider {

    public static class WidgetActionEvent extends ActionEvent {
        private final Widget widget;
        private final Point location;

        public WidgetActionEvent(final ActionEvent aE, final Widget aWidget, final Point aLocation) {
            super(aE.getSource(), aE.getID(), aE.getActionCommand(), aE.getWhen(), aE.getModifiers());
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

    /**
     * Action wrapping the real action in the menu and supplying the wrapped action
     * with its the target (widget and/or location) via the event object.
     */
    public static class MenuAction implements Action {
        private final Action action;
        private final Widget widget;
        private final Point location;

        public MenuAction(Action aAction, Widget aWidget, Point aLocation) {
            action   = aAction;
            widget   = aWidget;
            location = aLocation;

            NbUtil.getOut().println("MyPopupMenuProvider.constructor " + aWidget + " " + aLocation);
        }

        public void actionPerformed(ActionEvent aE) {
            NbUtil.getOut().println("MyPopupMenuProvider.actionPerformed " + widget + " " + location);

            action.actionPerformed(new WidgetActionEvent(aE, widget, location));
        }

        // <editor-fold defaultstate="collapsed" desc="delegated methods">
        public void setEnabled(boolean b) {
            action.setEnabled(b);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            action.removePropertyChangeListener(listener);
        }

        public void putValue(String key, Object value) {
            action.putValue(key, value);
        }

        public boolean isEnabled() {
            return action.isEnabled();
        }

        public Object getValue(String key) {
            return action.getValue(key);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            action.addPropertyChangeListener(listener);
        }
        // </editor-fold>
    }

    /** 
     * Predicate determining if the menu should be displayed. 
     */
    public static interface DisplayQ {
        boolean displayQ(final Widget aWidget, final Point aPoint);
    }

    /**
     * Default implementation of the {@link #DisplayQ} predicate returning always true.
     */
    public final static DisplayQ trueDisplayQ = new DisplayQ() {
        @Override public boolean displayQ(final Widget aWidget, final Point aPoint) {
            return true;
        }
    };


    public static WidgetAction createAction(Action ... aActions) {
        return createAction(trueDisplayQ, aActions);
    }


    public static WidgetAction createAction(DisplayQ aDisplayQ, Action ... aActions) {
        return ActionFactory.createPopupMenuAction(new WidgetMenuProvider(aDisplayQ, aActions));
    }

// =============================================================================

    private final List<Action> rawActions;
    private final DisplayQ displayQ;

    public static interface SubMenuAction extends Action {
        List<Action> getActions();
    }

    public static class SubMenuActionX extends AbstractAction implements SubMenuAction {
        private final List<Action> subMenuRawActions;

        public SubMenuActionX(final String aName, final List<Action> aActions) {
            super(aName);
            subMenuRawActions = aActions;
        }

        public SubMenuActionX(final String aName, final Action ... aActions) {
            this( aName, Arrays.asList(aActions) );
        }

        public void actionPerformed(ActionEvent e) {
            throw new RuntimeException("Should not be ever called.");
        }

        public List<Action> getActions() {
            return subMenuRawActions;
        }



    }

    public WidgetMenuProvider(final DisplayQ aDisplayQ, final Action ... aActions) {
        rawActions = Arrays.asList(aActions);
        displayQ = aDisplayQ;
    }

    /**
     * Builds the actual menu on demand
     * @param aWidget
     * @param aPoint
     * @return
     */
    public JPopupMenu getPopupMenu (final Widget aWidget, final Point aPoint) {
        if (! displayQ.displayQ(aWidget, aPoint)) return null;

        final JPopupMenu menu = new JPopupMenu("Menu");

        for (Action action : rawActions) {
            if (action instanceof SubMenuAction) {
                menu.add( getMenu(aWidget, aPoint, (SubMenuAction) action) );
            }
            else {
                menu.add(new JMenuItem(new MenuAction(action, aWidget, aPoint)));
            }
        }

        return menu;
    }

    public JMenu getMenu (final Widget aWidget, final Point aPoint, final SubMenuAction aAction) {
        final JMenu menu = new JMenu(aAction);

        for (Action action : aAction.getActions()) {
            if (action instanceof SubMenuAction) {
                menu.add( getMenu(aWidget, aPoint, (SubMenuAction) action) );
            }
            else {
                menu.add(new JMenuItem(new MenuAction(action, aWidget, aPoint)));
            }
        }

        return menu;
    }

}
