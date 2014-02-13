package org.purl.jh.feat.diffui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returns the operations and cost needed to change one sequence into another.
 * todo evaluate if it is better to remember m*n operations or to reconstruct them (calling eq again, but only O(n) times)
 * todo similar to FormEd (merge, or use one without subst, put into the same package) 
 * @author j
 */
public class EditDistance<T> {
    public static interface Eq<T> {
        public boolean eq(T a, T b);
    }
    
    public enum Op {
        match,
        subst,
        ins,  
        del   
    }
    
    private final int match_cost;
    private final int subst_cost;
    private final int ins_cost;       // 
    private final int del_cost;       //

    private final List<T> s;
    private final List<T> t; 
    private final Eq<T> eq;

    private final int n;
    private final int m;
    
    private final int[][] d;

    public EditDistance(int match_cost, int subst_cost, int ins_cost, int del_cost, List<T> s, List<T> t, Eq<T> eq) {
        this.match_cost = match_cost;
        this.subst_cost = subst_cost;
        this.ins_cost = ins_cost;
        this.del_cost = del_cost;
        this.s = s;
        this.t = t;
        this.eq = eq;

        this.n = s.size();
        this.m = t.size();

        this.d = new int[n + 1][m + 1];
    }
    public EditDistance(List<T> s, List<T> t, Eq<T> eq) {
        this(0, 1, 1, 1, s, t, eq);
    }
    
    public int cost() {
        return d[n][m];
    }
    
    public List<Op> ops() {
        final List<Op> operations = new ArrayList<>();
        int i = n;
        int j = m;
        
        for(;;) {
            final int cur = d[i][j];
            if (i == 0 && j == 0) break;
            
            if ( check(i-1,j-1, match_cost, cur) && eq.eq(s.get(i-1), t.get(j-1))) {
                operations.add(Op.match);
                i--; j--;
            }
            else if ( check(i-1,j-1, subst_cost, cur) ) {
                operations.add(Op.subst);
                i--; j--;
            }
            else if ( check(i,j-1, ins_cost, cur) ) {
                operations.add(Op.ins);
                j--;
            }
            else if ( check(i-1,j, del_cost, cur) ) {
                operations.add(Op.del);
                i--; 
            }
        }

        Collections.reverse(operations);
        return operations;
    }
    
    private boolean check(int i, int j, int cost, int cur) {
        if (i < 0 || j < 0) return false;
        
        return d[i][j] + cost == cur;
    }
    
    
    
    /** Assumes lower case */
    public void go() {
        for (int i = 0; i <= n; i++) {
            d[i][0] = del_cost * i;                // todo use ins/del costs
        }

        for (int j = 0; j <= m; j++) {
            d[0][j] = ins_cost * j;
        }

        for (int i = 1; i <= n; i++) {
            final T s_i = s.get(i - 1);  // ith character of s

            for (int j = 1; j <= m; j++) {
                final T t_j = t.get(j - 1);  // jth character of t

                final int ms_cost = eq.eq(s_i, t_j) ? match_cost : subst_cost;   // todo

                d[i][j] = min(d[i - 1][j] + del_cost, d[i][j - 1] + ins_cost, d[i - 1][j - 1] + ms_cost);
            }
        }
        
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                System.out.print(d[i][j] + " ");
            }
            System.out.println();
        }
    }

    /** Get minimum of three values */
    protected final static int min(int a, int b, int c) {
        int mi = a < b ? a : b;
        return (c < mi) ? c : mi;
    }    
}