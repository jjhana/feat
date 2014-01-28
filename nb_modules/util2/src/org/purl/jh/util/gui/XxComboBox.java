package org.purl.jh.util.gui;

import java.util.List;
import org.purl.jh.util.col.Mapper;
import org.purl.jh.util.col.MappingList;

/**
 * @todo display
 *
 * @author jirka
 */
public class XxComboBox<X> extends ValComboBox<X,String> {
    public XxComboBox(final List<X> aVals, final Mapper<X,String> aPrinter, final Mapper<X,String> aHelpPrinter) {
        super(aVals, new MappingList<X,String>(aVals, aPrinter) );
    }
}
