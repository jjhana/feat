package org.purl.jh.feat.layered.util;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.IntInt;
import org.purl.jh.util.col.XCols;

/**
 *
 * TODO MAKE ACCESSIBLE TO ALL FEAT MODULES
 * @author jirka
 */
public class FeatDataUtil {

    /**
     * Returns all w-forms below l-forms.
     * @param aForms l-forms (they must be on the same layer, but it is not checked)
     * @return a collection of w-forms connected by a path with with l-form, possibly empty.
     * Does not have to form a contiguous sequence, the order is not defined.
     */
    public static Collection<WForm> getWForms(Collection<? extends FForm> aForms) {
        for (;;) {
            if (aForms.isEmpty() || Cols.first(aForms) instanceof WForm) return (Collection<WForm>) aForms;

            aForms = getLowerForms(aForms);
        }
    }

    /**
     * Returns all w-forms below l-form.
     * @param aForm a l-form
     * @return a collection of w-forms connected by a path with an l-form, possibly empty.
     */
    public static Collection<WForm> getWForms(FForm aForm) {
        return getWForms(Arrays.asList(aForm));     // todo use simple singleton collection
    }

    /**
     * Returns all forms on the lower level corresponding to a set of forms on the higher layer.
     * @param aForms forms on the higher layer (they must be on the same layer, but it is not checked)
     * @return
     */
    public static Collection<FForm> getLowerForms(Iterable<? extends FForm> aForms) {
        final List<FForm> lowerForms = XCols.newArrayList();

        for (FForm form : aForms) {
            for (Edge e : form.getLower()) {
                lowerForms.addAll(e.getLower());
            }
        }

        return lowerForms;
    }

    public static IntInt spread(Iterable<WForm> aForms) {
        int seqStart = Integer.MAX_VALUE;
        int seqEnd   = Integer.MIN_VALUE;

        for (WForm wform : aForms) {
            final int start = wform.getDocOffset();
            final int end   = start + wform.getLen();
            if (start < seqStart) seqStart = start;
            if (end > seqEnd) seqEnd = end;
        }

        return new IntInt(seqStart,seqEnd);
    }

}
