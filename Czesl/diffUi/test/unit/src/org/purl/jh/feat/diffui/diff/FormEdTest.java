package org.purl.jh.feat.diffui.diff;

import java.util.List;
import static org.junit.Assert.*;
import org.purl.jh.feat.diffui.diff.FormEd.Op;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author j
 */
public class FormEdTest {
    
    public FormEdTest() {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception {
    }

    @org.junit.Before
    public void setUp() throws Exception {
    }
    

//        static class X implements FormEd.Matcher<Character> {
//            StringBuilder sba = new StringBuilder();
//            StringBuilder sbb = new StringBuilder();
//
//            //public void set
//            
//            @Override
//            public void del(List<Character> as, int ai, List<Character> bs, int bi) {
//                sba.append(as.get(ai));
//                sbb.append(' ');
//                System.out.println("" + as.get(ai) + ' ');
//            }
//
//            @Override
//            public void ins(List<Character> as, int ai, List<Character> bs, int bi) {
//                sba.append(' ');
//                sbb.append(bs.get(bi));
//                System.out.println("" + ' ' + bs.get(bi));
//            }
//
//            @Override
//            public void sub(List<Character> as, int ai, List<Character> bs, int bi) {
//                sba.append(as.get(ai));
//                sbb.append(bs.get(bi));
//                System.out.println("" + as.get(ai) + bs.get(bi));
//            }
//            
//            public boolean check() {
//                return sba.toString().equals(sbb.toString());
//            }
//            
//            public void print() {
//                System.out.println(sba);
//                System.out.println(sbb);
//            }
//        };
    
    @org.junit.Test
    public void testMatch() {
        FormEd.SimilarityScorer<Character> sc = new FormEd.SimilarityScorer<Character>() {
            @Override
            public int del(List<Character> as, int ai, List<Character> bs, int bi) {
                return 1;
            }

            @Override
            public int ins(List<Character> as, int ai, List<Character> bs, int bi) {
                return 1;
            }

            @Override
            public int sub(List<Character> as, int ai, List<Character> bs, int bi) {
                return as.get(ai).equals(bs.get(bi)) ? 0 : 1;
            }
          
        };

        FormEd ed = new FormEd(sc);
        test("ABCDE", "ABCDE", "=====", ed);
        test("ABDE",  "ABCDE", "==I==", ed);
        test("ABDEF", "XABCDE", "I==I==D", ed);
    
    }

    private void test(String a, String b, String expectedOps, FormEd<Character> ed) {
        FormEd.Matcher<Character> m = ed.match(Strings.toList(a), Strings.toList(b));
        m.match();
        m.printMatrices();
        List<Op> ops = m.getOps();
        System.out.println( ops );
        printStrings(a, b, ops);
        
        assertEquals(expectedOps, com.google.common.base.Joiner.on("").join(ops)  );
        
    }

    private void printStrings(String a, String b, List<Op> ops) {
        int i = 0;
        for (Op op :ops) {
            if (op == Op.ins) {
                System.out.print(" ");
            }
            else {
                System.out.print(a.charAt(i++));
            }
        }
        System.out.println();
        
        i = 0;
        for (Op op :ops) {
            if (op == Op.del) {
                System.out.print(" ");
            }
            else {
                System.out.print(b.charAt(i++));
            }
        }
        System.out.println();
    }

}
