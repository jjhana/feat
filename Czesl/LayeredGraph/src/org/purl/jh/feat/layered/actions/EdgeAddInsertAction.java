package org.purl.jh.feat.layered.actions;

import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class EdgeAddInsertAction extends WAction {

    public EdgeAddInsertAction(LayeredGraph aView) {
        super(aView, "Add Unary Insert Relation");
    }

    public void actionPerformed(Widget nodeWidget) {
        final LForm form = (LForm) view.findObject(nodeWidget);
        final LLayer layer = form.getLayer();
        if (layer.isReadOnly()) {
            //RelationWidget.log.info("  Read only layer: " + layer);
            return;
        }
        layer.edgeAddInsertWord(form, view, null);
    }

}
