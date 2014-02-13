package org.purl.jh.util.str.pp;

/**
 *
 * @author Jirka
 */
public interface Parser<T> {
    T fromString(String aString);

}
