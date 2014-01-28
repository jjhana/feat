/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.diffui;

import com.google.common.base.Preconditions;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.purl.jh.feat.diffui.diff.CombinedMatching;
import org.purl.jh.feat.layered.WidgetNode;

/**
 *
 * @author j
 */
public class DiffWidgetNode extends WidgetNode {

    public DiffWidgetNode() {
    }

    CombinedMatching matching;
    int docIdx;
    
    public void setMatching(CombinedMatching matching, int docIdx) {
        this.matching = matching;
        this.docIdx = docIdx;
    }
    
    @Override
    protected void setFormProps(final FForm form) {
        super.setFormProps(form);
        
        if (matching != null) {
            Sheet.Set props = Sheet.createPropertiesSet();
            props.setName("Matching form");
            props.setDisplayName("Matching form");
            fillMatchingFormProps(props, form);
            getSheet().put(props);
        }
    }

    private void fillMatchingFormProps(Set props, FForm form) {
        FForm thatForm = matching.getMatching(form, docIdx);
        if (thatForm == null) {
            props.put(new WidgetNode.RoStrProp("matching form", "<none>"));
        }
        else {
            props.put(new WidgetNode.RoStrProp("id", thatForm.getId().getIdStr()));
            props.put(new WidgetNode.RoStrProp("form", thatForm.getToken()));
            props.put(new WidgetNode.RoStrProp("comment", thatForm.getComment()));
            
        }
    }
}
