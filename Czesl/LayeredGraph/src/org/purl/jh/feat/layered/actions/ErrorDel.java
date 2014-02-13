package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerl.Errorr;
import org.purl.jh.feat.ea.data.layerl.LLayer;
import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class ErrorDel extends GraphAction {

    public ErrorDel(LayeredGraph aView) {
            super(aView, "Delete error");
    }

    public void actionPerformed(ActionEvent aE) {
        final WidgetActionEvent e = (WidgetActionEvent) aE;
        final Widget edgeWidget = e.getWidget();
        final Edge edge = (Edge) view.findObject(edgeWidget);
        final LLayer layer = edge.getLayer();
        
//        RelationWidget.log.info("Delete error from edge " + edge);
        final Set<Errorr> errors = new HashSet<>(edge.getErrors());
        if (errors.isEmpty()) {
            return; // no revalidation needed, but it does not hurt
        } else if (errors.size() == 1) {
            layer.errorDel(errors.iterator().next(), view, null);
        } else {
            final JPopupMenu menu = new JPopupMenu("Menu");
            for (Errorr error : errors) {
                final Errorr errorf = error;
                final Action a = new AbstractAction(error.getTag()) {

                    public void actionPerformed(ActionEvent x) {
                        layer.errorDel(errorf, view, null);
                    }
                };
                menu.add(new JMenuItem(a));
            }
            //final Point sceneLoc = edgeWidget.convertLocalToScene(edgeWidget.getLocation());
            menu.show(view.getView(), e.getLocation().x, e.getLocation().y);
        }
    }

}
