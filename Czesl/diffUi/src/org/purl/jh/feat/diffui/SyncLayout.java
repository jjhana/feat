package org.purl.jh.feat.diffui;


import org.purl.jh.feat.ea.data.layerw.WForm;
import org.purl.jh.feat.ea.data.layerx.FForm;
import java.util.List;
import org.purl.jh.feat.layered.GraphLayout2;
import org.purl.jh.feat.layered.NodeLayout;
import org.purl.jh.feat.layered.ParaModel;
import org.purl.jh.util.col.List2D;


/**
 * Extension of GraphLayout2 to two LayeredGraphs (for diff), synchronizing two 
 * graphs by their wlayer.
 * 
 * @todo optimize
 * @author Jirka
 */
public class SyncLayout  {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(SyncLayout.class);

    final GraphLayout2 layout1;
    final GraphLayout2 layout2;
    
    public SyncLayout(final ParaModel model1, final ParaModel model2) {
        layout1 = new GraphLayout2(model1);
        layout2 = new GraphLayout2(model2);
    }

    public NodeLayout model1layout() {
        return new NodeLayout() {
            @Override
            public List2D<? extends FForm> getArrangedForms() {
                return layout1.getArrangedForms();
            }
            
        };
    }

    public NodeLayout model2layout() {
        return new NodeLayout() {
            @Override
            public List2D<? extends FForm> getArrangedForms() {
                return layout2.getArrangedForms();
            }
            
        };
    }
    
    public void go() {
        layout1.go();        
        layout2.go();
        
        final List2D<? extends FForm> forms1 = layout1.getArrangedForms();
        final List2D<? extends FForm> forms2 = layout2.getArrangedForms();
        
        sync(forms1, forms2);

    }

    private void sync(final List2D<? extends FForm> forms1, final List2D<? extends FForm> forms2) {
        final List<WForm> wforms1 = (List<WForm>)forms1.getRow(0);
        final List<WForm> wforms2 = (List<WForm>)forms2.getRow(0);
        
        int i1 = 0;
        int i2 = 0;
        for (; i1 < wforms1.size() && i2 < wforms2.size();) {
            final WForm wform1 = wforms1.get(i1);
            final WForm wform2 = wforms2.get(i2);
            
            if (wform1 == null && wform2 != null) {
                forms2.addEmptyColumn(i2); 
            }
            else if (wform1 != null && wform2 == null) {
                forms1.addEmptyColumn(i1); 
            }
            i1++; i2++;
        }       
    }

    
// --- sync based on edit distance - works, but unnecessary complicated
//    
//    final List<EditDistance.Op> ops = match((List<WForm>)forms1.getRow(0), (List<WForm>)forms2.getRow(0));
//    sync(ops, forms1, forms2);
//    
//    private final static EditDistance.Eq<WForm> eq = new EditDistance.Eq<WForm>() {
//        @Override
//        public boolean eq(WForm form1, WForm form2) {
//            if (form1 == form2) return true;
//            if (form1 == null || form2 == null) return false;
//            
//            return Objects.equal(form1.getId().getLocalId(), form2.getId().getLocalId());
//        }
//    };
//    
//    /** runs edit distance on wforms (using ids to test equality, substitute is not possible) */
//    private List<EditDistance.Op> match(final List<WForm> wforms1, final List<WForm> wforms2) {
//        EditDistance<WForm> ed = new EditDistance<>(0, 1000, 1, 1, wforms1, wforms2, eq);
//        ed.go();
//        return ed.ops();
//    }
//
//
//    private void sync(List<EditDistance.Op> ops, final List2D<? extends Form> forms1, final List2D<? extends Form> forms2) {
//        int i1 = 0;
//        int i2 = 0;
//
//        for (EditDistance.Op op : ops) {
//            switch (op) {
//                case ins: 
//                    forms1.addEmptyColumn(i1); break;
//                case del: 
//                    forms2.addEmptyColumn(i2); break;
//            }
//            i1++; i2++;
//        }
//    }
}
