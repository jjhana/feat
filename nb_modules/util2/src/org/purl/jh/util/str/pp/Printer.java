package org.purl.jh.util.str.pp;

/**
 * @todo inherit from Mapper<T,String>
 * @author Jirka
 */
public interface Printer<T> {
    String toString(T aItem);
}
