/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.diffui.diff;

import cz.cuni.utkl.czesl.data.layerl.Edge;

/**
 *
 * @author j
 */
class EdgeEdge {
    Edge e1;
    Edge e2;

    public EdgeEdge(Edge e1, Edge e2) {
        this.e1 = e1;
        this.e2 = e2;
    }
    
    boolean isIdentical() {
        return com.google.common.base.Objects.equal(e1, e2);
    }
    
}
