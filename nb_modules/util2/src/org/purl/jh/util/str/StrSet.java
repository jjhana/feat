
package org.purl.jh.util.str;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Immutable low memory footprint set of characters.
 *
 * Note: Do not mix this class with other sets if relying on hashcode().
 * @todo sort
 * @todo two implementations for small and large sets (sorted, fast searching)
 * @author Jirka Hana
 */
public class StrSet extends AbstractSet<Character> implements Set<Character>, Comparable<StrSet> {
    /** string of sorted characters */
    private final String chars;
    //private final int size;

    public String getString() {
        return chars;
    }

    private static SortedSet<Character> toSortedSet(String aChars) {
        SortedSet<Character> set = new TreeSet<Character>();;
        for (Character c : aChars.toCharArray()) {
            set.add(c);
        }
//        System.out.println("toSet: " + aChars + " -> " + set);

        return set;


    }

    private static String toString(SortedSet<Character> aChars) {
        StringBuilder sb = new StringBuilder(aChars.size());
        for (Character val : aChars) {
            sb.append(val);
        }

        return  sb.toString();
    }

    public StrSet(String aChars) {
        this(aChars, false);
    }

    public StrSet(String aChars, boolean aPrepared) {
        if (aPrepared) {
            chars = aChars;
        }
        else {
            chars = toString(toSortedSet(aChars));
        }
    }



    public StrSet(SortedSet<Character> aChars) {
        chars = toString(aChars);
    }

    public StrSet(Collection<Character> aChars) {
        this(new TreeSet<Character>(aChars));
    }


    public @Override int size() {
        return chars.length();
    }

    public @Override boolean isEmpty() {
        return chars.isEmpty();
    }

    public @Override boolean contains(Object aChar) {
        if (aChar instanceof Character)
            return contains( ((Character)aChar).charValue() );

        return false;
    }

    public boolean contains(char aChar) {
        for (int i=0; i<chars.length();i++) {
            if (chars.charAt(i) == aChar) return true;
        }
        return false;
    }

    public @Override Iterator<Character> iterator() {
	    return new Iterator<Character>() {
            int idx = -1;
    		public boolean hasNext()     {return idx+1 < chars.length();}
        	public Character next()		     {idx ++; return chars.charAt(idx);}

            public void remove() {
                throw new UnsupportedOperationException(cROMsg);
            }
        };
	}

    private final static String cROMsg = "Read only collection.";

    @Override
    public boolean equals(Object o) {
        if (o instanceof StrSet) return chars.equals( ((StrSet)o).chars );

        return super.equals(o);
    }

    
    /**
     * Do not mix this class with other sets if relying on hashcode.
     * becuase a.equals(b) does not guarantee a.hashCode() == b.hashCode() if
     * one of a and b is a set of different class.
     */
    public @Override int hashCode() {
        return chars.hashCode();
    }

    public int compareTo(StrSet o) {
        return chars.compareTo(o.chars);
    }

    public StrSet inters(StrSet aSet2) {
        final StringBuilder result = new StringBuilder();

        final int m = chars.length();
        final int n = aSet2.chars.length();
        int i = 0;
        int j = 0;

        for (; i < m && j < n;) {
            char a = chars.charAt(i);
            char b = aSet2.chars.charAt(j);

            if (a == b) {
                result.append(a);
                i++; j++;
            }
            else if (a < b) {
                i++;
//                a = chars.charAt(i);
            }
            else {
                j++;
//                b = chars.charAt(j);
            }
        }
        return new StrSet(result.toString(),true);
    }

// --- Read only ---
    public @Override boolean add(Character e) {
        throw new UnsupportedOperationException(cROMsg);
    }

    public @Override boolean remove(Object o) {
        throw new UnsupportedOperationException(cROMsg);
    }


    public @Override boolean addAll(Collection<? extends Character> c) {
        throw new UnsupportedOperationException(cROMsg);
    }

    public @Override boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException(cROMsg);
    }

    public @Override boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(cROMsg);
    }

    public @Override void clear() {
        throw new UnsupportedOperationException(cROMsg);
    }

}
