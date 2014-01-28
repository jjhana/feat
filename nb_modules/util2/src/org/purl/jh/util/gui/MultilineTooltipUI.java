package org.purl.jh.util.gui;

import java.io.*;
import java.util.*;
import java.                                                                                                                                                                                                                                                                                                                                                                                                                           awt.*;
import javax.swing.*;
import javax.swing.plaf.metal.*;

/**
 */
public class MultilineTooltipUI extends MetalToolTipUI {
    private String[] mStrs;
    private int maxWidth = 0;
    
    @SuppressWarnings("deprecation")
    public void paint(Graphics g, JComponent c) {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(g.getFont());
        Dimension size = c.getSize();
        g.setColor(c.getBackground());
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(c.getForeground());
        if (mStrs != null) {
            for (int i=0; i<mStrs.length; i++) {
                g.drawString(mStrs[i], 3, (metrics.getHeight()) * (i+1));
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    public Dimension getPreferredSize(JComponent c) {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(c.getFont());

        String tipText = ((JToolTip)c).getTipText();
        if (tipText == null) tipText = "";
        
        BufferedReader br = new BufferedReader(new StringReader(tipText));
        String line;
        int maxWidth = 0;
        java.util.List<String> v = new ArrayList<String>();
        try {
            while ((line = br.readLine()) != null) {
                int width = SwingUtilities.computeStringWidth(metrics,line);
                maxWidth = (maxWidth < width) ? width : maxWidth;
                v.add(line);
            }
        } 
        catch (IOException ex) {
            ex.printStackTrace();
        }
        int lines = v.size();
        if (lines < 1) {
            mStrs = null;
            lines = 1;
        } 
        else {
            mStrs = v.toArray(new String[lines]);
        }
        int height = metrics.getHeight() * lines;
        this.maxWidth = maxWidth;
        return new Dimension(maxWidth + 6, height + 4);
    }
}


