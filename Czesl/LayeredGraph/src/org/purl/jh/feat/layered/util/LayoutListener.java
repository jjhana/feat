/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.feat.layered.util;

import org.netbeans.api.visual.widget.Widget;

/**
 * Layout listener.
 *
 * @author jirka
 */
public interface LayoutListener  {

    /**
     * Called when the layer layout is started.
     * @param aLayer layer that will be laid-out
     */
    void layoutStarted(Widget aLayer);

    /**
     * Called when the layer layout is finished.
     * @param aLayer layer that was laid-out
     */
    void layoutFinished(Widget aLayer);
}
