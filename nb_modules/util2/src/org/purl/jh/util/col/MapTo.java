package org.purl.jh.util.col;

/**
 * An object used to map create a new collection from 
 * another collection by transforming the items of the latter.
 *
 * @author Jirka
 */
public interface MapTo<From,To> {
    public To mapTo(From aFrom);
}
