package org.purl.jh.feat.diffui.diff;

import com.google.common.collect.Lists;
import java.util.*;

/**
 * Finds matching between two lists of items, given a function comparing the individual items.
 * @author j
 */
public class FormEd<T> {

    public enum Op {
        sub, ins, del;

        @Override
        public String toString() {
            switch(this) {
                case sub: return "=";
                case ins: return "I";
                case del: return "D";
            }
            return null;
        }
    }
    
    public static interface SimilarityScorer<T> {
        int sub(List<T> as, int ai, List<T> bs, int bi);
        int ins(List<T> as, int ai, List<T> bs, int bi);
        int del(List<T> as, int ai, List<T> bs, int bi);
    }
    
    public static class SimilarityScorerAdapter<T> implements SimilarityScorer<T> {

        @Override
        public int del(List<T> as, int ai, List<T> bs, int bi) {
            return 1;
        }

        @Override
        public int ins(List<T> as, int ai, List<T> bs, int bi) {
            return 1;
        }

        @Override
        public int sub(List<T> as, int ai, List<T> bs, int bi) {
            return as.get(ai).equals(bs.get(bi)) ? 0 : 1;
        }
        
    }
    
    private final SimilarityScorer<T> sc;
    
    public FormEd(SimilarityScorer<T> aScorer) {
        sc = aScorer;
    }
    
    
    
    /**
     * Match/substitution are symetric, but insert/delete are relative to as 
     * (it is delete/insert relative to bs)
     * @param as first sequence of items (organized into rows)
     * @param bs second sequence of items (organized into columns)
     * @return 
     */
    public Matcher<T> match(final List<T> as, final List<T> bs) {
        return new Matcher<>(as, bs, sc);
    }

    
    public static class Matcher<T> {
        private final List<T> as; 
        private final List<T> bs;
        
        private final SimilarityScorer<T> sc;

        final int aLen;
        final int bLen;
        final int[][] d;
        final Op[][]  p;
                
        private Matcher(final List<T> as, final List<T> bs, final SimilarityScorer<T> sc) {
            this.as = as;
            this.bs = bs;
            this.sc = sc;
        
            aLen = as.size(); // 
            bLen = bs.size();
        
            d = new int[aLen + 1][bLen + 1]; // costs
            p = new  Op[aLen + 1][bLen + 1]; // back pointers
        }

        public int match() {
            d[0][0] = 0;
            for (int c = 1; c <= bLen; c++) {
                d[0][c] = d[0][c-1] + sc.ins(as, 0, bs, c-1);
                p[0][c] = Op.ins;
            }

            for (int r = 1; r <= aLen; r++) {
                d[r][0] = d[r-1][0] + sc.del(as, r-1, bs, 0);
                p[r][0] = Op.del;

                for (int c = 1; c <= bLen; c++) {
                    final int sub = d[r-1][c-1] + sc.sub(as, r-1, bs, c-1);
                    final int del = d[r-1][c]   + sc.del(as, r-1, bs, c-1);
                    final int ins = d[r][c-1]   + sc.ins(as, r-1, bs, c-1);

                    int cost ;
                    Op op;
                    if (sub < del) {
                        op = Op.sub; 
                        cost = sub;
                    }
                    else {
                        op = Op.del; 
                        cost = del;
                    }

                    if (ins < cost) {
                        op = Op.ins;
                        cost = ins;
                    }

                    p[r][c] = op;
                    d[r][c] = cost;
                }
            }
            
            return d[aLen][bLen];
        }
        
        public void printMatrices() {
            for (int i = 0; i <= aLen; i++) {
                for (int j = 0; j <= bLen; j++) {
                    System.out.printf("%d ", d[i][j]);
                }
                System.out.println();
            }

            System.out.print(" ");
            for (int j = 0; j <= bLen; j++) {
                System.out.printf(" " + (j == 0 ? " " : bs.get(j-1).toString()));
            }
            System.out.println("");

            for (int i = 0; i <= aLen; i++) {
                System.out.printf((i == 0 ? " " : as.get(i-1).toString()));
                for (int j = 0; j <= bLen; j++) {
                    System.out.printf(" " + (p[i][j] == null ? "0" : p[i][j].toString()) );
                }
                System.out.println();
            }
        }

        public List<Op> getOps() {
            final int aLen = as.size();
            final int bLen = bs.size();

            final List<Op> ops = new ArrayList<>(aLen+bLen);

            int r = aLen;
            int c = bLen;
            for (;;) {
//                System.out.printf("%d:%d  ", r, c);
//                System.out.println(p[r][c]);
                ops.add(p[r][c]);
                switch(p[r][c]) {
                    case sub: r--; c--; break;
                    case del: r--;      break;
                    case ins:      c--; break;
                }
                if (r == 0 && c == 0) break;
            }

            return Lists.<Op>reverse(ops);

        }
    }    
}
