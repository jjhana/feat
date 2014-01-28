/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.diffui.util;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.purl.jh.feat.diffui.util.EditDistance.Eq;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author j
 */
public class EditDistanceTest {
    
    public EditDistanceTest() {
    }

    Eq eq = new Eq() {

        @Override
        public boolean eq(Object a, Object b) {
            return Objects.equal(a, b);
        }
        
    };
    
    @Test
    public void testX() {
        testOne("isnt", "isnt", "mmmm");
        testOne("isnt", "ismt", "mmsm");
        testOne("isnt", "isntt", "mmmmi/mmmim");
        testOne("isntt", "isnt", "mmmmd/mmmdm");
        testOne("isnnt", "isnt", "mmmdm/mmdmm");
        testOne("isnt", "isnnt", "mmmim/mmimm");
        testOne("isnntt", "isnt", "??");

        testOne("spake", "park", "?mmmm");
    }

    public void testOne(String a, String b, String ops) {
        EditDistance ed = new EditDistance(Strings.toList(a), Strings.toList(b), eq);
        ed.go();
        System.out.printf("%s x %s -> %s\n", a, b, ed.ops());   // todo check ops
        
    }
    
    
    @Test
    public void testMin() {
        assertEquals(0, EditDistance.min(0,1,2));
        assertEquals(0, EditDistance.min(1,0,2));
        assertEquals(0, EditDistance.min(1,2,0));
        
    }
}
