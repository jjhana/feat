package org.purl.jh.feat.navigator;

import ca.odell.glazedlists.gui.TableFormat;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.util.List;
import org.purl.jh.pml.Element;

/**
 *
 * @author j
 */
@lombok.AllArgsConstructor
@lombok.Data
public class FilterView {
    private String name;
    private FilterSpec filterSpec;
    
    @XStreamOmitField
    private Filter filter;
    private List<Column> columns;

    public FilterView(String name, FilterSpec filterSpec, List<Column> columns) {
        this.name = name;
        this.filterSpec = filterSpec;
        this.columns = columns;
        this.filter = new Filter(filterSpec);
    }
    
    public FilterView copy(String aNewName) {
        return new FilterView(aNewName, new FilterSpec(filterSpec), Column.copy(columns));
    }

    TableFormat<? super Element> getTableFormat() {
        return new TableFormat<Element>() {
            @Override
            public int getColumnCount() {
                return columns.size();
            }

            @Override
            public String getColumnName(int column) {
                return columns.get(column).getTitle();
            }

            @Override
            public Object getColumnValue(Element obj, int column) {
                return columns.get(column).map( obj );
            }
                
        };
    }

    
    // hack to display it nicely in a combobox
    @Override
    public String toString() {
        return name;
    }
    
    
    
}
