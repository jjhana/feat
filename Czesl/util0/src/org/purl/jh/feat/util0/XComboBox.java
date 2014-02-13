package org.purl.jh.feat.util0;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.purl.jh.util.col.Mapper;
import org.purl.jh.util.col.MappingList;

/**
 * A combobox displaying items in a configurable way via a mapper.
 * Unfortunately, some methods (such as setModel) had to be replaced by other methods.
 * todo create a list+mapper model instead
 * 
 * Cf. ValComboBox
 * @author j
 * @since 1.0
 */
public class XComboBox<I> extends JComboBox<String> {

    public class Item {
        private final I data;

        public Item(I data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return mapper.map(data);
        }

    }

    //private final List<I> ids;
    private final Mapper<I,String> mapper;
    private  List<I> data;

    public XComboBox(Mapper<I,String> aMapper) {
        this(Collections.<I>emptyList(), aMapper);
    }

    public XComboBox(List<I> aData, Mapper<I,String> aMapper) {
        mapper = aMapper;
        setData(aData);
    }

    // todo use instead of setModel
    public final void setData(List<I> aData) {
        data = aData;

        Vector<String> v = new Vector<>(new MappingList<>(aData, mapper));

        setModel(new DefaultComboBoxModel<>(v));
    }

    public I getCurItem() {
        int idx = getSelectedIndex();
        return idx == -1 ? null : data.get(idx);
    }

    public void setCurItem(I aId) {
        int idx = data.indexOf(aId);
        setSelectedIndex(idx);
    }

    /** todo to utilities */
    public static <T> String comboItemsToString(JComboBox<T> aList) {
        String str = "Combo box items:\n";
        for (int i = 0; i < aList.getModel().getSize(); i++) {
            str += "  " + aList.getModel().getElementAt(i) + "\n";
        }
        return str;
    }
}