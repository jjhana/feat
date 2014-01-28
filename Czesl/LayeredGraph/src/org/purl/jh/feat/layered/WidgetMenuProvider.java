package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerx.Position;
import org.purl.jh.feat.util0.visual.ExpandingMenuAction;
import org.purl.jh.feat.layered.util.SubMenuAction;
import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import org.purl.jh.feat.layered.actions.WAction;
import org.purl.jh.feat.layered.actions.AnchorAction;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Widget;

/**
 * Menu associated with a widget.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class WidgetMenuProvider implements PopupMenuProvider {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(WidgetMenuProvider.class);

    /**
     * Action wrapping the real action in the menu and supplying the wrapped action
     * with its the target (widget and/or location) via the event object.
     */
    protected static class MenuAction implements Action {
        private final Action action;
        private final Widget widget;
        private final Point location;
        private final Position anchor;

        public MenuAction(Action aAction, Widget aWidget, Point aLocation, Position aAnchor) {
            action   = aAction;
            widget   = aWidget;
            location = aLocation;
            anchor = aAnchor;
        }

        public boolean isEnabled() {
            if (action instanceof WAction) {
                return action.isEnabled() && ((WAction)action).isEnabled(widget);
            }
            else if (action instanceof AnchorAction) {      // probably should not be called
                // todo dermine anchor for all of these anctions once
                return action.isEnabled() && ((AnchorAction)action).isEnabled(widget, location, anchor);
            }
            else {
                return action.isEnabled();
            }
        }


        public void actionPerformed(ActionEvent aE) {
            log.fine("action=%s, w=%s, loc=%s", action, widget, location);

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
     *
     * @todo This is probably an over-kill; it would be better to determine it
     * for each action independently and then display nothing if none actions
     * should be active.
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


    public static WidgetAction createAction(LayeredGraph aScene, Action ... aActions) {
        return createAction(aScene, trueDisplayQ, aActions);
    }


    public static WidgetAction createAction(LayeredGraph aScene, DisplayQ aDisplayQ, Action ... aActions) {
        return ActionFactory.createPopupMenuAction(new WidgetMenuProvider(aScene, aDisplayQ, aActions));
    }

// =============================================================================

    private final LayeredGraph graph;
    private final List<Action> rawActions;
    /** Should the menu be displayed? E.g. an editing menu is not displayed for read-only layer. */
    private final DisplayQ displayQ;

//    public static class SubMenuActionX extends AbstractAction implements SubMenuAction {
//        private final List<Action> subMenuRawActions;
//
//        public SubMenuActionX(final String aName, final List<Action> aActions) {
//            super(aName);
//            subMenuRawActions = aActions;
//        }
//
//        public SubMenuActionX(final String aName, final Action ... aActions) {
//            this( aName, Arrays.asList(aActions) );
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            throw new RuntimeException("Should not be ever called.");
//        }
//
//        public List<Action> getActions() {
//            return subMenuRawActions;
//        }
//    }

    public WidgetMenuProvider(final LayeredGraph aGraph, final DisplayQ aDisplayQ, final Action ... aActions) {
        rawActions = Arrays.asList(aActions);
        displayQ = aDisplayQ;
        graph = aGraph;
    }

    /**
     * Builds the actual menu on demand
     * @param aWidget the widget the menu was invoked on
     * @param aPoint point of the mouse; null when invoked by keyboard
     * @return the menu to display filled with appropriate actions
     */
    public JPopupMenu getPopupMenu (final Widget aWidget, final Point aPoint) {
        if (! displayQ.displayQ(aWidget, aPoint)) return null;

        final Point scenePoint = (aPoint == null) ? null : aWidget.convertLocalToScene(aPoint);
        final Position anchor  = (aPoint == null) ? null : graph.place2anchor(scenePoint);

        final JPopupMenu menu = new JPopupMenu("Menu");
        fillMenu(aWidget, aPoint, anchor, menu, rawActions);
        return menu;
    }

    /**
     *
     * @param aWidget
     * @param aPoint point of the mouse; null when invoked by keyboard
     * @param anchor null when aPoint is null
     * @param aMenu
     * @param aActions
     */
    private void fillMenu(final Widget aWidget, final Point aPoint, Position anchor, JComponent aMenu, final List<Action> aActions) {
        for (Action action : aActions) {

            if (action instanceof SubMenuAction) {
                final SubMenuAction subMenuAction = (SubMenuAction)action;
                final List<Action> subActions = subMenuAction.getActions(aWidget, aPoint, anchor);

                if (subActions.size() > subMenuAction.inline() ) {
                    final JMenu menu = new JMenu(action);
                    fillMenu(aWidget, aPoint,  anchor, menu, subActions);
                    aMenu.add( menu );
                }
                else {
                    for (Action oneAction : subActions) {
                        aMenu.add(new JMenuItem(new MenuAction(oneAction, aWidget, aPoint, anchor)));
                    }
                }
            }
            else {
                aMenu.add(new JMenuItem(new MenuAction(action, aWidget, aPoint, anchor)));
            }
        }
    }

}
