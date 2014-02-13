import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/* Binary tree for storing arbitrary objects  */
public class BinaryTree<T> implements Collection<T> {
   /* Node of the binary tree containing one value and pointers to children */
    public static class Node<T> {
        public Node(T val) {
            this.val = val;
        }
        
        T val;
        Node<T> left;
        Node<T> right;
    }

    private int size = 0;
    private Node<T> root;

    /* Comparator used to compare values in the tree */
    private final Comparator<T> comp;

    /**
     * Creates an empty binary tree with a specified comparator.
     */
    public BinaryTree(Comparator<T> comp) {
        this.comp = comp;
    }

    
    /** Adds a new value to the tree */
    @Override
    public boolean add(T e) {
        if (root == null) {
           root = new Node<>(e);
           size++;
        }
        else {
            add(root, e);
        }
        return true;
    }
    
    private void add(Node<T> aNode, T e) {
        int c = comp.compare(e, aNode.val);
        if (c < 0) {
            if (aNode.left == null) {
                aNode.left= new Node<>(e);
                size++;
            }
            else {
                add(aNode.left, e);
            }
        }
        else {
            if (aNode.right == null) {
                aNode.right= new Node<>(e);
                size++;
            }
            else {
                add(aNode.right, e);
            }
        }
    }
    
    

    /** Adds new values to the tree */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T o : c) {
            add(o);
        };
        return true;
    }

    /** Removes all values from the tree */
    @Override
    public void clear() {
        root = null;
        size  = 0;
    }

    /** Checks if the tree contains a given value */
    @Override
    public boolean contains(Object o) {
        return find((T)o) != null;
        
    }

    /** Checks if the tree contains all given value */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) return false;
        }
        return true;
    }

    /* Checks if the tree is empty */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<T> iterator() {  
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Removes given value; returns true if was found and removed */
    @Override
    public boolean remove(Object o) {
        Node<T> node = find((T)o);
        if (node == null) return false;
        remove(node);
        return true;
    }

    public boolean remove(Node<T> aNode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Removes all given values */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Removes all but the given values */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Returns the number of values in the tree */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Prints values of the tree in-order.  
     */
    public void printInOrder() {
        // might call System.out.println(toString());
       printInOrder(root);
    }

    /**
     * Prints values of the subtree rooted at the specified node in-order.  
     */
    private void printInOrder(Node<T> aNode) {
       if (aNode != null) {
           printInOrder(aNode.left);
           System.out.println(aNode.val + ", ");
           printInOrder(aNode.right);
       }
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Returns in order string */
    public String toString() {
        return toString(root);
    }
    
    private String toString(Node<T> aNode) {
        if (aNode == null) return "";
        return "[ " + toString(aNode.left) + " " + aNode.val + " " + toString(aNode.right) + " ]" ;
    }
    
    
    /** Finds a node with the given value */
    private Node<T> find(T o) {
        return find(root, o);
    }
    
    /** Recursively looks for a given value in a subtree rooted in aNode */
    private Node<T> find(Node<T> aNode, T o) {
        int c = comp.compare(o, aNode.val);
        if (c == 0) {
            return aNode;
        }
        else if (c < 0) {
            return aNode.left == null ? null : find(aNode.left, o);
        }
        else {
            return aNode.right == null ? null : find(aNode.right, o);
        }
    }
} 