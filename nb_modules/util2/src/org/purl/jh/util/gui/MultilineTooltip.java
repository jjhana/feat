package org.purl.jh.util.gui;

/* Taken from http://www.codeguru.com/java/articles/144.shtml 
 *  Author: Nobuo Tamemasa
 */

import java.awt.*;
import javax.swing.*;

public class MultilineTooltip extends JToolTip {
    public MultilineTooltip() {  
        setUI(new MultilineTooltipUI());
    }

    public MultilineTooltip(JComponent aComponent) {  
        this();
        setComponent(aComponent);
    }
    
    /**
     * Returns a textfield with a multiline tooltip.
     */
    public static JTextField jTextField() {
        return new JTextField() {
            public JToolTip createToolTip() {
                return new MultilineTooltip(this);
            }    
        };
    }
}





