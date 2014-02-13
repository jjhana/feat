package org.purl.jh.pml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.purl.jh.util.col.Cols;

/**
 * Element implementing children elements as a collection.
 *
 * @param C child
 * @author Jirka
 */
public class AbstractListElement<C extends Element> 
        extends AbstractElement implements ListElement<C> { // implements Collection<C> {
    
    final protected List<C> col = new ArrayList<>();

// -----------------------------------------------------------------------------
// Override Elements method for accessing subelements
// -----------------------------------------------------------------------------    

    /**
     * Returns the number of children of this element.
     */
    @Override
    public int size() {
        return col.size();
    }

    /**
     *
     */
    @Override
    public boolean isEmpty() {
        return col.isEmpty();
    }

// -----------------------------------------------------------------------------
// Collection
// -----------------------------------------------------------------------------    

    /**
     * Returns lists of subelements of this element.
     */
    @Override
    public List<C> col() {
        return col;
    }
    
    /**
     * Adds an element as a child to this element.
     *
     * Sets child's parent to this element.
     */
    @Override
    public void add(C aEl) {
        col.add(aEl);
        ((Element)aEl).setParent(this);       
    }

    /**
     * Adds an element as a child to this element.
     *
     * Sets child's parent to this element.
     */
    public void add(int aIdx, C aEl) {
        col.add(aIdx, aEl);
        ((Element)aEl).setParent(this);       
    }
    
    /**
     * Adds elements as children to this element.
     *
     * Sets children's parent to this element.
     */
    @Override
    public void addAll(Collection<C> aCol) {
        col.addAll(aCol);
        for(C e : aCol) { 
            ((Element)e).setParent(this);       
        }
    }

    /**
     * Returns iterator over children.
     */
    @Override
    public Iterator<C> iterator() {
        return col.iterator();
    }
    
// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
    
    /**
     * Returns the selectable subelement on the specified index.
     * Synonymous with getChild(int).
     *
     * @param aIdx index of the subelement
     * @return selectable element at the given index
     */
    @Override
    public C get(int aIdx) {
        return col.get(aIdx);
    }

    
    @Override
    public String toString() {
        return Cols.toString(col);
    }


    /**
     * Optimize storage of information for memory and speed of access.
     * Use once it can be anticipated no inserts or deletions of children and 
     * related computed structures (e.g. after reading the element from a file).
     * Override this function to optimize structures in a subclass (e.g.
     * rehashing has tables, trimming growable arrays, etc.)
     * The default method is not called recursivelly on children.
     */
    @Override
    public void optimizeForAccess() {
        ((ArrayList)col).trimToSize();
    }
}
