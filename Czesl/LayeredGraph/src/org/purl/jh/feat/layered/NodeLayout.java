/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.util.col.List2D;

/**
 *
 * @author j
 */
public interface NodeLayout {
    List2D<? extends FForm> getArrangedForms();
}
