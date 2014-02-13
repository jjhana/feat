package org.purl.jh.pml;

/**
 * Interface of all elements with an id. The id must be globally unique.
 *
 * Equals, hashCode, compareTo must be based on the id.
 * @author Jirka
 */
public interface IdedElement extends Element {
    Id getId();
}
