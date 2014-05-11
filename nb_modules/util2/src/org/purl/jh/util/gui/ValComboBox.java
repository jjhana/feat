package org.purl.jh.util.gui;

import java.util.List;
import java.util.Vector;
import javax.swing.JComboBox;
import org.purl.jh.util.Pair;
import org.purl.jh.util.Pairs;
import org.purl.jh.util.col.Cols;

/**
 *
 * @author jirka
 * @param <I> type of item indexes/tags used by the code
 * @param <D> type of items displayed to the user
 *
 * @todo in many cases this could probably be replaced by a printer wrapping the real objects
 */
public class ValComboBox<I, D> extends JComboBox {

    private final List<I> ids;

    public ValComboBox(final List<Pair<I, D>> aVals) {
        this(Pairs.firsts(aVals), Pairs.seconds(aVals));
    }

    public ValComboBox(final List<I> aIds, final List<D> aDisplayVals) {
        super( new Vector<>(aDisplayVals) );
        ids = aIds;
    }

    public I getCurItem() {
        int idx = getSelectedIndex();
        return idx == -1 ? null : ids.get(idx);
    }

    public void setCurItem(I aId) {
        int idx = ids.indexOf(aId);
        if (idx == -1) {
            String msg = "Index not found: " + aId + "\n";
            msg += "Indexes: \n" + Cols.toStringNl(ids, "   ");
            msg += comboItemsToString(this);

            throw new IllegalArgumentException(msg);
        }
//        if (idx == 0) {
//            String msg = "Index zero: " + aId + "\n";
//            msg += "Indexes: " + Cols.toString(ids);
//            msg += comboItemsToString(this);
//            System.out.println("MMM" + msg);
//        }
        setSelectedIndex(idx);
    }

    /** todo to utilities */
    public static String comboItemsToString(JComboBox aList) {
        String str = "Combo box items:\n";
        for (int i = 0; i < aList.getModel().getSize(); i++) {
            str += "  " + aList.getModel().getElementAt(i) + "\n";
        }
        return str;
    }
}
