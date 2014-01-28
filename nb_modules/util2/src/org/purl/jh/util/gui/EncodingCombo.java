package org.purl.jh.util.gui;

import javax.swing.JComboBox;
import org.purl.jh.util.io.Encoding;

/**
 *
 * @author Jiri
 */
public class EncodingCombo extends JComboBox {
    
    /** Creates a new instance of EncodingCombo */
    public EncodingCombo() {
        this(Encoding.cUsuEncodings);
    }
    
    public EncodingCombo(Encoding[] aEncodings) {
        super(aEncodings);
        setEditable(true);
    }

    @Override
    public Encoding getSelectedItem() {
        Object curItem = super.getSelectedItem();
        if (curItem instanceof Encoding)
            return (Encoding) curItem;
        else
            return Encoding.fromString((String) curItem);
    }
}
