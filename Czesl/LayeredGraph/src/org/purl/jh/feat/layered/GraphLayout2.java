package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.util.col.BoolMatrix;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.purl.jh.feat.layered.util.Util;
import org.purl.jh.util.Pair;
import org.purl.jh.util.col.List2D;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.str.Strings;
import org.purl.jh.util.str.pp.Printer;

/**
 * Lays out form-nodes so that nodes connected by a simple edge are below each 
 * other (unless there are crossing edges).
 * 
 * @todo optimize
 * @author Jirka
 */
public class GraphLayout2 implements NodeLayout {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(GraphLayout2.class);

    private final List2D<? extends FForm> forms;
   
    /** TODO: Drop once the algorithm is stabilized and clearly working */
    private final int safetynet;

    public GraphLayout2(final ParaModel aModel) {
        forms  = new List2D<>(aModel.getNodes());
        safetynet =  10 * forms.getLongestRowSize();
    }

    public void go() {
        getCrossing();

        for (int row = 1; row < forms.size(); row++) {
            for (int col = 0; col < forms.getRow(row).size(); col++) {
                handle(row, col);
                
                if (col > safetynet) {
                    log.severe("Layout too large\n%s", printList2D());
                    // todo layout the rest just by putting it there
                    break;
                }
            }
        }
    }

	/**
	 * Forms arranged into a table. All table cells have the same width, some cells might be empty (null). 
	 */
    @Override
    public List2D<? extends FForm> getArrangedForms() {
        return forms;
    }
    

    /**
     * Pairs of forms connected by edges which are crossed/crossing other edges
     * Only forward going and vertical edges are considered (each backward crossing
     * edge is crossed by some forward/vertical edge(s)).
     */
    private final Set<Pair<FForm,FForm>> crossings = new HashSet<>();

    private boolean isCrossing(FForm aForm1, FForm aForm2) {
        return crossings.contains( new Pair<>(aForm1, aForm2) );
    }

    /** Calculate crossings for all layers */	
    private void getCrossing() {
        for (int row = 1; row < forms.size(); row++) {
            getCrossing(forms.getRow(row-1), forms.getRow(row));
        }
    }

    /** Get crossing branches between two particular rows */
    private void getCrossing(final List<? extends FForm> aRow1, final List<? extends FForm> aRow2) {
        final BoolMatrix matrix = buildMatrix(aRow1, aRow2);

        // find forward-crossings -- todo more effective
        for (int r = 0; r < matrix.getRowDimension(); r++) {
            for (int c = 0; c < matrix.getColumnDimension(); c++) {
                if (matrix.get(r, c) && !matrix.allFalse(r+1,matrix.getRowDimension(),0,c)) {
                    crossings.add( new Pair<>(aRow1.get(r), aRow2.get(c) ));
                }
            }
        }
    }
	
    /**
     * Builds a connection matrix (this is a generalization of the standard adjacency 
     * matrix to a (bipartite) graph with hyper-edges.)
     * A[i,j] = 1 iff i-th form at aRow1 is connected via a hyper-edge to j-th form at aRow2
     * 
     * @param aRow1
     * @param aRow2
     * @return 
     */
    private BoolMatrix buildMatrix(final List<? extends FForm> aRow1, final List<? extends FForm> aRow2) {
        final BoolMatrix matrix = new BoolMatrix(aRow1.size(), aRow2.size());

        if (aRow1.isEmpty() || aRow2.isEmpty()) return matrix;
        
        final LPara para = ((LForm)aRow2.iterator().next()).getParent().getParent();    // todo pass it as a parameter
        
        // form -> idx 
        final Map<FForm,Integer> form2idxLo = Util.getObj2IdxMap(aRow1);
        final Map<FForm,Integer> form2idxHi = Util.getObj2IdxMap(aRow2);
        
        for(Edge edge : para.getEdges()) {
            for (FForm formLo : edge.getLower()) {
                final int loIdx = form2idxLo.get(formLo);
                for (FForm formHi : edge.getHigher()) {
                    final int hiIdx = form2idxHi.get(formHi);
                    matrix.set(loIdx, hiIdx, true);
                }
            }
        }

        return matrix;
    }
    
    

    private void handle(int aRow, int aCol) {
        final FForm form2 = forms.get(aRow, aCol);
        
        if (form2 != null) {
            final FForm form1 = form2.getLowerForm();
            if (form1 == null) return;

            if (isCrossing(form1, form2)) return;

            int idx1 = forms.getRow(aRow-1).indexOf(form1); // todo use biased search
            if (idx1 == -1) throw Err.iErr( "Form1 %s (id=%s), Form2 %s (id=%s)", form1.getToken(), form1.getId(), form2.getToken(), form2.getId() );

            if (idx1 < aCol) {
                for (int r = 0; r < aRow; r++) {
                    for (int j = 0; j < aCol-idx1; j++) {
                        forms.getRow(r).add(idx1, null);
                    }
                }
            }
            else if (idx1 > aCol) {
                for (int j = 0; j < idx1-aCol; j++) {
                    forms.getRow(aRow).add(aCol, null);
                }
            }

        }
    }


    private String printList2D() {
        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < forms.size(); row++) {
            for (int col = 0; col < forms.getRow(row).size(); col++) {
                String formStr = String.valueOf( forms.get(row, col) );
                sb.append( Strings.format(formStr, 5) );
                sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();

    }


    private String formStr(FForm aForm) {
        return aForm.getToken() + "(" + aForm.getId() + ")";
    }


    private final Printer<?> hashPrinter = (aItem) -> String.valueOf(aItem.hashCode());




}
