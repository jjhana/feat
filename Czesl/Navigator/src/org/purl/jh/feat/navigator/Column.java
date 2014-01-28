package org.purl.jh.feat.navigator;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.ArrayList;
import java.util.List;
import org.purl.jh.pml.Element;

/**
 *
 * @author j
 */
@lombok.Data
@lombok.AllArgsConstructor
public class Column {
    /** Column title */
    private String title;
    /** Are the values in this column considered by the quick matcher? */
    private boolean filtered;
    /** Not used yet. Prefix used for searching values in this column */
    private String prefix;
    /** Printer responsible for printing objects which are forms to the cells in this column */
    //@XStreamAlias("formPrinter")
    @XStreamConverter(ColumnPrinterConverter.class)
    private ColumnPrinter<? super FForm> formPrinter;  
    /** Printer responsible for printing objects which are edges to the cells in this column */
    //@XStreamAlias("edgePrinter")
    @XStreamConverter(ColumnPrinterConverter.class)
    private ColumnPrinter<? super Edge> edgePrinter;  

    /**
     * Deep copy of a list of columns
     * @param cols list of columns to copy
     * @return a new list of columns, each column was duplicated
     */
    public static List<Column> copy(final List<Column> cols) {
        final List<Column> newColumns = new ArrayList<>();
        for (Column col : cols) {
            newColumns.add(new Column(col));
        }
        return newColumns;
    }
    
    
    public Column(Column aOrig) {
        this(aOrig.title, aOrig.filtered, aOrig.prefix, aOrig.formPrinter, aOrig.edgePrinter);
    }
    
    String map(final Element obj) {
        return (obj instanceof FForm) ? 
            formPrinter.map( (FForm)obj ) : 
            edgePrinter.map( (Edge)obj );
    }

    
    public static class ColumnPrinterConverter implements Converter {

        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            ColumnPrinter printer = (ColumnPrinter) source;
            writer.setValue(printer.getId());
        }

        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            String id = (String) reader.getValue();
            
            ColumnPrinter printer = ColumnPrinters.getDef().getPrinter(id);
            if (printer == null) {
                //todo warning
                printer = ColumnPrinters.cErr;
            }
                    
            return printer;
        }

        @Override
        public boolean canConvert(Class type) {
            return ColumnPrinter.class.isAssignableFrom(type);
        }
    }    
    
}
