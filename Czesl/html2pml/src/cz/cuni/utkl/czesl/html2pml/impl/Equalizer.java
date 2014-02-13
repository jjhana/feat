
package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Merges collections sharing some items.
 * 
 * @author Jirka dot Hana at gmail dot com
 * TODO Optimize
 */
public class Equalizer<T> {
    private final List<Set<T>> classes = new ArrayList<Set<T>>();

        public void add(T ... aCol) {
            add(Arrays.asList(aCol));
        }

        public void add(Collection<T> aCol) {
            Set<T> eq = new HashSet<T>();
            for (T e : aCol) {
                eq.add(e);

                for (Iterator<Set<T>> it = classes.iterator(); it.hasNext();) {
                    Set<T> q = it.next();
                    if (q.contains(e)) {
                        it.remove();
                        eq.addAll(q);
                    }
                }
            }

            classes.add(eq);
        }

        public Set<T> getClass(T aEl) {
            for (Set<T> set : classes) {
                if (set.contains(aEl)) return set;
            }
            return null;
        }

        public Collection<Set<T>> getClasses() {
            return classes;
        }

}
