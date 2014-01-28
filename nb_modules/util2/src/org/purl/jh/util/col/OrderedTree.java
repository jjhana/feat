package org.purl.jh.util.col;

import java.util.*;

/**
 * Ordered OrderedTree.
 * @todo make it a collection (this would be a node inside of some class)
 *
 * @author Jiri
 */
public class OrderedTree<T> {
    private OrderedTree<T> mParent = null;
    private List<OrderedTree<T>> mDtrs = null;
    private T mVal;             
    
// -----------------------------------------------------------------------------
// Construction
// -----------------------------------------------------------------------------

    public OrderedTree(T aVal) {
        mVal = aVal;
    }

    public void addDtr(OrderedTree<T> aDtr) {
        if (mDtrs == null) mDtrs = new ArrayList<OrderedTree<T>>(2);            // @todo 1??? or trim 
        aDtr.mParent = this;
        mDtrs.add(aDtr);
    }

    // @todo add various construction methods (collection plus order & dominance picker/comparator, etc.

// -----------------------------------------------------------------------------
// Storing Data
// -----------------------------------------------------------------------------

    public T getVal()          {return mVal;}
    public void setVal(T aVal) {mVal = aVal;}

// -----------------------------------------------------------------------------
// OrderedTree structure (direct/simple functions)
// -----------------------------------------------------------------------------
    
    /**
     */
    public List<OrderedTree<T>> getDtrs() { return mDtrs; }

    public OrderedTree<T> getParent()     { return mParent; }

    public boolean isRoot()     {return mParent == null;}

    public boolean isLeaf()     {return mDtrs == null;}
    
// -----------------------------------------------------------------------------
// OrderedTree structure (computed functions)
// -----------------------------------------------------------------------------

    public Collection<OrderedTree<T>> nodes() {
        List<OrderedTree<T>> nodes = new ArrayList<OrderedTree<T>>();
        
        nodes.add(this);
        if (mDtrs != null) {
            for (OrderedTree<T> dtr : mDtrs) {
                nodes.addAll(dtr.nodes());
            }
        }
        
        return nodes;
    }
    
    /**
     * The maximal number of nodes on a path from the root to a leave.
     * Calculated.
     */
    public int depth() {
        int max = 0;
        
        if (mDtrs != null) {
            for (OrderedTree<T> dtr : mDtrs) {
                int dtrDepth = dtr.depth();
                if (dtrDepth > max) max = dtrDepth;
            }
        }
        
        return max + 1;
    }

    /**
     * Depth of the current node (root's depth is 1).
     * Calculated.
     */
    public int myDepth() {
        return (mParent == null) ? 1 : (mParent.myDepth() + 1);
    }
    
    

    /**
     * Constructed each time again. 
     */
    public List<T> dtrValues() {
        ArrayList<T> tmp = new ArrayList<T>(mDtrs.size());
        
        for (OrderedTree<T> dtr : mDtrs)
            tmp.add(dtr.mVal);
        
        return tmp;
    }
    
    
// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------
    
    // @todo add toString with printer
    // @todo eff optionaly pass buffer
    public String toString() {
        return (mDtrs == null) ? mVal.toString() : ( mVal + Cols.toString(mDtrs) );
    }

    public String toString(ColPrinter<T> aPrinter) {
        if (mDtrs == null) {
            return aPrinter.toString(mVal);
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(aPrinter.toString(mVal));
            sb.append('(');
            for (OrderedTree<T> d : mDtrs)
                sb.append( d.toString(aPrinter) ).append(' ');
            sb.append(')');
            
            return sb.toString();
        }
    }
    
}
