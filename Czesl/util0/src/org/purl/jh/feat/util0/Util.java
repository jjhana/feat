/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.util0;

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 *
 * @author j
 */
public class Util {
    public static interface Eq<A,B> { // BinPred<T,T,Boolean>
        boolean equals(A a, B b);
    }



    public static <T> List<T> sortByVals(final List<T> list, final List<T> order) {
        int unsortedSuffixIdx = 0;
        for (int i = 0; i < order.size(); i++) {
            int idx = list.indexOf(order.get(i));           // would be nice to call indexOf(unsortedSuffixIdx, i), but it does not exist, and calling sublist is cosstly
            if (idx != -1) {
                if (idx != unsortedSuffixIdx) {
                    swap(list, idx, unsortedSuffixIdx);
                }
                unsortedSuffixIdx++;
            }
        }
        return list;
    }

    public static <A,B> List<A> sortByVals(final List<A> list, final List<B> order, final Eq<A,B> eq) {
        int unsortedSuffixIdx = 0;
        for (int i = 0; i < order.size(); i++) {
            int idx = indexOf(list,order.get(i), eq);           // would be nice to call indexOf(unsortedSuffixIdx, i), but it does not exist, and calling sublist is cosstly
            if (idx != -1) {
                if (idx != unsortedSuffixIdx) {
                    swap(list, idx, unsortedSuffixIdx);
                }
                unsortedSuffixIdx++;
            }
        }
        return list;
    }

    public static <A,B> A maxByVals(final Collection<A> list, final List<B> order, final Eq<A,B> eq) {
        for (int i = order.size()-1; i >=0; i--) {
            final B b = order.get(i);
            for (A a : list) {
                if (eq.equals(a, b)) return a;
            }
        }
        return null;
    }

    public static <A,B> int maxIdxByVals(final List<A> list, final List<B> order, final Eq<A,B> eq) {
        for (int i = order.size()-1; i >=0; i--) {
            int idx = indexOf(list,order.get(i), eq);           // would be nice to call indexOf(unsortedSuffixIdx, i), but it does not exist, and calling sublist is cosstly
            if (idx != -1) return i;
        }
        return -1;
    }



    public static <A,B> int indexOf(final List<A> list, final B aItem, final Eq<A,B> eq) {
        if (list instanceof RandomAccess) {
            for (int i = 0; i < list.size(); i++) {
                if (eq.equals(list.get(i), aItem)) return i;
            }
        }
        else {
            int i = 0;
            for (A x : list) {
                if (eq.equals(x, aItem)) return i;
                i++;
            }
        }
        return -1;
    }

    // to util
    public static <T> void swap(final List<T> list, int a, int b) {
        final T tmp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, tmp);
    }

//    static class ListComparator<T> implements Comparator<T> {
//        List<T> vals
//
//        @Override
//        public int compare(T o1, T o2) {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//    }

}
