package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.ea.data.layerl.LLayer;
import org.purl.jh.feat.ea.data.layerw.WForm;
import org.purl.jh.feat.layered.LayeredGraph;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class EdgeAddDelAction extends WAction {

    public EdgeAddDelAction(LayeredGraph aView) {
        super(aView, "Add Unary Delete Relation");
    }

    public void actionPerformed(Widget nodeWidget) {
        //uilog.info("EdgeAddDelAction");
        final WForm form = (WForm) view.findObject(nodeWidget);
        // roso should check if higher layer is not read only
        LLayer hiLayer = view.getParaModel().getPseudoModel().getLayerAbove(form.getLayer());
        hiLayer.edgeAddRemoveWord(form, view, null);
    }

}
