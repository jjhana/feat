package org.purl.jh.util.gui.list;

import java.util.Collection;
import java.util.Vector;
import javax.swing.JComboBox;
import org.purl.jh.util.io.Format;

/**
 *
 * @author Jirka
 */
public class FormatCombo extends JComboBox {
    public FormatCombo() {
    }

    public FormatCombo(Format[] aFormats) {
        super(aFormats);
    }

    public FormatCombo(Collection<Format> aFormats) {
        super(new Vector<Format>(aFormats));
    }
    
    
    @Override
    public Format getSelectedItem() {
        return (Format) super.getSelectedItem();
    }
}
