package org.purl.jh.feat.navigator;

import org.purl.jh.pml.Element;
import org.purl.jh.util.col.Mapper;

/**
 *
 * @author j
 */
@lombok.Getter
public abstract class ColumnPrinter<T extends Element> implements Mapper<T,String> {
    /** Unique id */
    private final String id;
    /** Type of elements to be printed, Class<T> would be sufficient, but it works only for types without parameters, not for Element<?>, etc */
    private final Class<? super T> clazz;
    /** User readable name */
    private final String name;
    
    /** User readable description */
    private final String desc;

    public ColumnPrinter(String id, Class<? super T> clazz, String name) {
        this(id, clazz, name, "");
    }
    
    public ColumnPrinter(String id, Class<? super T> clazz, String name, String desc) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
