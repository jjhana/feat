package org.purl.jh.util.col;

import java.util.*;
import java.util.regex.Pattern;


/**
 * Labeled tree, dtrs are ordered by their labels.
 * 
 * @author Jiri
 */
public class Tree<T>  {
    Tree<T> mParent;
    SortedMap<String, Tree<T>> mDtrs;

    private String mName;       // Path element
    private T mVal;             
    
    
    public Tree(Tree<T> aParent) {
        mParent = aParent;
        mDtrs = null;
    }

// -----------------------------------------------------------------------------
    
    public Tree<T> getNode(String aPathStr) {
        return getNode(split(aPathStr));
    }

    public Tree<T> getNode(final List<String> aPath) {
        if (aPath.isEmpty()) return this;
        
        String first = aPath.get(0);
        List<String> rest = aPath.subList(1, aPath.size());

        Tree<T> node = (mDtrs != null) ? mDtrs.get(first) : null;

        if (node == null) {
            if (mDtrs == null) mDtrs = new TreeMap<String, Tree<T>>();
            
            node = new Tree<T>(this);
            node.mName = first;
            mDtrs.put(first, node);
        }

        return node.getNode(rest);
    }

// -----------------------------------------------------------------------------
    
    /**
     * Do not modify the set;
     * Sorted.
     */
    public Collection<Tree<T>> dtrs() {
        return mDtrs.values();
    }

    /**
     * Do not modify the set;
     * Sorted.
     */
    public Collection<Map.Entry<String,Tree<T>>> dtrEntries() {
        return mDtrs.entrySet();
    }
    
    
    public boolean isRoot() {
        return mParent == null;
    }

    public boolean isLeaf() {
        return mDtrs == null;
    }
    
// -----------------------------------------------------------------------------

    public T getVal()          {return mVal;}
    public void setVal(T aVal) {mVal = aVal;}

    public String getName()          {return mName;}
    public void setName(String aName) {mName = aName;}
    
    private static final Pattern dotPattern = Pattern.compile("\\.");
    
    private static final List<String> split(String aPathStr) {
        return Arrays.asList( dotPattern.split(aPathStr) );
    }
}   
    
 