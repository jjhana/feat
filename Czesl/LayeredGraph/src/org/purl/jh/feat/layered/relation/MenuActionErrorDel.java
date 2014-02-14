package org.purl.jh.feat.layered.relation;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import org.purl.jh.feat.layered.util.SubMenuAction;
import org.purl.jh.feat.layered.LayeredGraph;
import cz.cuni.utkl.czesl.data.layerx.Position;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;
import org.purl.net.jh.nbutil.NbUtil;

/**
 * TODO Do not show this if there is only one item!!
 * TODO share some insides with MenuActionErrorAdd
 * @author Jirka
 */
public class MenuActionErrorDel extends AbstractAction implements SubMenuAction {
    final LayeredGraph view;
    final Edge rel;

    public MenuActionErrorDel(final LayeredGraph aView, Edge aEdge) {
        super("Delete Error");
        view = aView;
        rel = aEdge;
    }

    public int inline() {
        return 2;
    }


    @Override
    public List<Action> getActions(final Widget aWidget, final Point aPoint, Position anchor) {
        final  List<Errorr> errors = rel.getErrorsList();
        final LLayer layer = rel.getLayer();
                
        final String labelTemplate = (errors.size() <= inline()) ? "Delete %s" : "%s";

        final List<Action> actions = new ArrayList<>();
        for (Errorr error : errors) {
            final Errorr ferror = error;

            actions.add( new AbstractAction(String.format(labelTemplate, ferror.getTag())) {
                public void actionPerformed(ActionEvent e) {
                    final Edge edge = (Edge)view.findObject( aWidget );
                    NbUtil.getOut().println("Deleting error to edge " + edge);
                    layer.errorDel(ferror, view, null);
                }
            });
        }

        return actions;
    }

    @Override
    public boolean isEnabled() {
        return !rel.getErrors().isEmpty();
    }

    public void actionPerformed(ActionEvent e) {
        throw new RuntimeException("Should not be ever called.");
    }



}
