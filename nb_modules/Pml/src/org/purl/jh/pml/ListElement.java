package org.purl.jh.pml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author jirka
 */
public interface ListElement<C extends Element> 
        extends Element, Iterable<C> {

    /**
     * Adds an element as a child to this element.
     *
     * Sets child's parent to this element.
     */
    void add(C aEl);

    /**
     * Adds elements as children to this element.
     *
     * Sets children's parent to this element.
     */
    void addAll(Collection<C> aCol);

    /**
     * Returns lists of subelements of this element.
     * Synonymous with getChildren.
     */
    List<C> col();

    /**
     * Returns the selectable subelement on the specified index.
     * Synonymous with getChild(int).
     *
     * @param aIdx index of the subelement
     * @return selectable element at the given index
     */
    C get(int aIdx);

    /**
     * Returns the number of children of this element.
     */
    int size();

    /**
     * Returns <tt>true</tt> if this element has no children.
     *
     * @return <tt>true</tt> if this element has no children.
     */
    boolean isEmpty();

    /**
     * Returns iterator over children.
     */
    @Override
    Iterator<C> iterator();

    /**
     * Optimize storage of information for memory and speed of access.
     * Use once it can be anticipated no inserts or deletions of children and
     * related computed structures (e.g. after reading the element from a file).
     * Override this function to optimize structures in a subclass (e.g.
     * rehashing has tables, triming growable arrays, etc.)
     * The default method is not called recursivelly on children.
     */
    void optimizeForAccess();


    @Override
    String toString();

}
