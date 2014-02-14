package org.purl.jh.feat.layered.actions;

import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.netbeans.api.visual.widget.Widget;

/**
 * todo not finished yet, not used
 * @author jirka
 */
public class FormSplit extends GraphAction {

    public FormSplit(LayeredGraph aView) {
            super(aView, "Split Word");
    }

    @Override
    public void actionPerformed(ActionEvent aE) {
//        final WidgetActionEvent e = (WidgetActionEvent) aE;
//        final Widget formWidget = e.getWidget();
//        final Form form = (Form) view.findObject(formWidget);
//        
//        final Set<LForm> oldHiForms = getHiForms(form);   // usually a singleton
//        
//        LForm anchor = oldHiForms.iterator().next();
//
//        List<String> newHiFormStrs = getSplits(form);
//        List<LForm> newHiForms = getSplits(form);
//        
//        for (String str : newHiFormStrs) { 
//            anchor = hiLayer.formAdd( "?", anchor, view, null);      // todo formAdd etc should return a set of created objs
//            newHiForms.add(anchor);
//        }
//
//        // connect with edges
//        hiLayer.edgeAdd(Arrays.asList(form), newHiForms, view, null);
//        
//        // delete old forms (removes edges as well)
//        for (LForm oldForm : oldHiForms) {
//            hiLayer.formDel(oldForm);
//        }
    }

}
