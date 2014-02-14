package org.purl.jh.feat.layered.actions;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.util.Pair;

/**
 *
 * @author jirka
 */
public class ErrorLinkDel extends WAction {

    public ErrorLinkDel(LayeredGraph aView) {
            super(aView, "Delete");
    }

    public void actionPerformed(Widget aWidget) {
        final Pair<Errorr, Edge> p = (Pair<Errorr, Edge>) view.findObject(aWidget);
        final LLayer layer = p.mSecond.getLayer();
        //log.info("Deleting error link %s %s", aWidget, p);
        layer.errorLinkDel(p.mFirst, p.mSecond, view, null);
    }

}
