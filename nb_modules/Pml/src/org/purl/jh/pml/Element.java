package org.purl.jh.pml;

import org.purl.jh.pml.location.Location;

/**
 * Note: Equals/hashcode should ignore parents.
 */
public interface Element extends Comparable<Element> {
    /**
     * Parent of this element.
     */
    public Element getParent();

    /**
     * Changes parent of this element.
     * @param aParent
     */
    public void setParent(Element aParent);

    /**
     * Convenience method for searching above this element in the
     * element hierarchy and returns the first element of class <code>c</code> it
     * finds. Can return {@code null}, if the class <code>c</code> cannot be found
     * or if the parameter is null.
     * @param aC
     */
    public <T> T getAncestor(Class<T> aC);

    /** Convenience method retrieving layer containing this element
     * @return
     */
    public Layer<?> getLayer();

    public Location location();

//    /**
//     * A list of child-elements.
//     * @return
//     */
//    public List<? extends Element> getChildren();
//
//    /**
//     * Child-elements with a particular index.
//     * @param aIdx
//     * @return
//     */
//    public Element getChild(int aIdx);
//
//    /**
//     * Checks if there are any children.
//     *
//     * @return true if there are no children.
//     */
//    public boolean isEmpty();
}
