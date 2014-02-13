package org.purl.jh.feat.layered;


import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.laf.LookFeel;

import java.awt.*;

/**
 *
 * @todo make user configurable
 * @author jirka
 */
public class LookAndFeel extends LookFeel {

    private static final Color COLOR_SELECTED = new Color(0x447BCD);
    private static final Color COLOR_FOCUSED = COLOR_SELECTED.darker();
    private static final Color COLOR_HIGHLIGHTED = COLOR_SELECTED.brighter();   // ;
    private static final Color COLOR_HOVERED = COLOR_SELECTED.brighter();
    private static final int MARGIN = 3;
    private static final int ARC = 10;
    private static final int MINI_THICKNESS = 1;

    private static final Border BORDER_NORMAL      = BorderFactory.createEmptyBorder(MARGIN, MARGIN);
    private static final Border BORDER_HOVERED     = BorderFactory.createRoundedBorder(ARC, ARC, MARGIN, MARGIN, COLOR_HOVERED, COLOR_HOVERED.darker());
    //private static final Border BORDER_FOCUSED  = BorderFactory.createRoundedBorder(ARC, ARC, MARGIN, MARGIN, null, Color.GREEN);
    private static final Border BORDER_FOCUSED     = BorderFactory.createLineBorder(3, Color.BLUE.darker());
    private static final Border BORDER_SELECTED    = BorderFactory.createRoundedBorder(ARC, ARC, MARGIN, MARGIN, COLOR_SELECTED, COLOR_SELECTED.darker());
    private static final Border BORDER_HIGHLIGHTED = BorderFactory.createRoundedBorder(25, 25, MARGIN+2, MARGIN+2, COLOR_HIGHLIGHTED, COLOR_HIGHLIGHTED.darker());

    //private static final Border BORDER_FOCUSED_SELECTED  = BorderFactory.createRoundedBorder(ARC, ARC, MARGIN, MARGIN, COLOR_SELECTED, Color.GREEN);
    private static final Border BORDER_FOCUSED_SELECTED  = BorderFactory.createLineBorder(3, Color.BLUE.darker());

    private static final Border MINI_BORDER_NORMAL   = BorderFactory.createEmptyBorder(MINI_THICKNESS);
    private static final Border MINI_BORDER_HOVERED  = BorderFactory.createRoundedBorder(MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, COLOR_HOVERED, COLOR_HOVERED.darker());
    private static final Border MINI_BORDER_FOCUSED  = BorderFactory.createRoundedBorder(MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, null, Color.GREEN);
    private static final Border MINI_BORDER_SELECTED = BorderFactory.createRoundedBorder(MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, MINI_THICKNESS, COLOR_SELECTED, COLOR_SELECTED.darker());
    private static final Border MINI_BORDER_FOCUSED_SELECTED  = BorderFactory.createRoundedBorder(ARC, ARC, MARGIN, MARGIN, COLOR_SELECTED, Color.GREEN);

    public Paint getBackground() {
        return Color.WHITE;
    }

    public Color getForeground() {
        return Color.BLACK;
    }

    public Border getBorder(ObjectState state) {
        if (state.isFocused() && state.isSelected()) 
            return BORDER_FOCUSED_SELECTED;
        if(state.isFocused())
            return BORDER_FOCUSED;
        if(state.isHovered())
            return BORDER_HOVERED;
        if(state.isSelected())
            return BORDER_SELECTED;
        if(state.isHighlighted())
            return BORDER_HIGHLIGHTED;
        return BORDER_NORMAL;
    }

    public Border getMiniBorder(ObjectState state) {
        if (state.isFocused() && state.isSelected()) 
            return MINI_BORDER_FOCUSED_SELECTED;
        if(state.isFocused())
            return MINI_BORDER_FOCUSED;
        if(state.isHovered())
            return MINI_BORDER_HOVERED;
        if(state.isSelected())
            return MINI_BORDER_SELECTED;
        return MINI_BORDER_NORMAL;
    }

    public boolean getOpaque(ObjectState state) {
        return state.isHovered()  ||  state.isSelected();
    }

    public Color getLineColor(ObjectState state) {
        if(state.isHovered())
            return COLOR_HOVERED;
        if(state.isSelected())
            return COLOR_SELECTED;
        if(state.isFocused())
            return COLOR_FOCUSED;
        if(state.isHighlighted())
            return COLOR_HIGHLIGHTED;
        return Color.BLACK;
    }

    public Paint getBackground(ObjectState state) {
        if(state.isHovered())
            return COLOR_HOVERED;
        if(state.isSelected())
            return COLOR_SELECTED;
        if(state.isFocused())
            return COLOR_FOCUSED;
        if(state.isHighlighted())
            return COLOR_HIGHLIGHTED;
        return Color.WHITE;
    }

    public Color getForeground(ObjectState state) {
        return state.isSelected() ? Color.WHITE : Color.BLACK;
    }

    public int getMargin() {
        return MARGIN;
    }

}
