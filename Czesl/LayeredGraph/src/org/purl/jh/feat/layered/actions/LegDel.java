package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerl.LLayer;
import org.purl.jh.feat.ea.data.layerx.FForm;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.util.Pair;

/**
 *
 * @author jirka
 */
public class LegDel extends WAction {

    public LegDel(LayeredGraph aView) {
            super(aView, "Delete Leg");
    }

    @Override
    public void actionPerformed(Widget aWidget) {
        final Pair<Edge, FForm> ef = view.getLeg2objs().get(aWidget);
        final LLayer layer = ef.mFirst.getLayer();
        //log.info("Deleting leg %s", ef);
        layer.legDel(ef.mFirst, ef.mSecond, view, null);
    }

}
