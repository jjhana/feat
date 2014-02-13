package org.purl.jh.util.col;

/**
 * Maps objects of one type to objects of another type (the types need not
 * be necessary different).
 * 
 * @param <D> domain type, i.e, the type of objects to map from
 * @param <R> range type, i.e, the type of objects to map to
 * @author Jirka Hana
 */
public interface Mapper<D,R> {
    R map(D aOrigItem);
}
