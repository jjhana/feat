/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.util0.visual;

import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public interface XAction extends Action {
        public boolean isEnabled(Widget aWidget);
    
}
