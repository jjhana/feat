package org.purl.jh.feat.layered;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.border.BorderFactory;

/**
 * Configuration of visual elements in the layered graph.
 * This should be eventually replaced by css.
 *
 * 
 * @todo under dev, very early version
 * @todo combine with LookAndFeel
 * @todo add inheritance
 * @todo background does not work, 
 * @todo precompile
 * @author j
 */
public enum Css {
    INSTANCE;
    
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Css.class);

 // See how to retrieve defaults: http://home.tiscali.nl/~bmc88/java/sbook/061.html#textarea

    public static class Style {
        public Color foreground;
        public Color background;

        // --- border ---
        // style, width, color
        //border-top-style/border-right-style/border-bottom-style/border-left-style:;

        public Color border_color;

        public int border_top_width;
        public int border_bottom_width;
        public int border_left_width;
        public int border_right_width;

        public boolean hasBorder;

        @Override
        public String toString() {
            return "Style{" + "foreground=" + foreground + ", background=" + background + ", border_color=" + border_color + ", border_top_width=" + border_top_width + ", border_bottom_width=" + border_bottom_width + ", border_left_width=" + border_left_width + ", border_right_width=" + border_right_width + ", hasBorder=" + hasBorder + '}';
        }

    }

    public static class Builder {
        final Style style = new Style();
        final String styleId;
        final Map<String,Style> styles;

        public Builder(String styleId, Map<String, Style> styles) {
            this.styleId = styleId;
            this.styles = styles;
        }

        public Builder x(String key, String val) {
            applyProperty(key, val);
            return this;
        }

        public Style x() {
            finish();
            styles.put(styleId, style);
            return style;
        }

        private void finish() {
            style.hasBorder = (style.border_top_width != 0 ||
                style.border_bottom_width != 0 ||
                style.border_left_width != 0 ||
                style.border_right_width != 0);

            if (style.hasBorder && style.border_color == null)  // use darker background if possible
                style.border_color = Color.black;
        }


        private void applyProperty(String key, String value) {
            if (foreground.equals(key)) style.foreground = color(value);
            if (background.equals(key)) style.background = color(value);

            if (key.startsWith("border_")) {
                if (border_color.equals(key))        style.border_color = color(value);
                if (border_width.equals(key)) {
                    style.border_top_width =
                    style.border_bottom_width =
                    style.border_left_width =
                    style.border_right_width   = Integer.valueOf(value);
                }
                if (border_top_width.equals(key))    style.border_top_width    = Integer.valueOf(value);
                if (border_bottom_width.equals(key)) style.border_bottom_width = Integer.valueOf(value);
                if (border_left_width.equals(key))   style.border_left_width   = Integer.valueOf(value);
                if (border_right_width.equals(key))  style.border_right_width  = Integer.valueOf(value);

                //log.fine("Compiling border: %s %s %s ", styleId, key, style);
            }
        }

        private Color color(String aColorSpec) {
            // todo check Color.getColor(comment)

            if ("black".equals(aColorSpec)) {
                return Color.black;
            }
            if ("black.d".equals(aColorSpec)) {
                return Color.black.darker();
            }
            if ("black.b".equals(aColorSpec)) {
                return Color.black.brighter();
            }
            else if ("blue".equals(aColorSpec)) {
                return Color.blue;
            }
            else if ("blue.d".equals(aColorSpec)) {
                return Color.blue.darker();
            }
            else if ("blue.b".equals(aColorSpec)) {
                return Color.blue.brighter();
            }
            else if ("cyan".equals(aColorSpec)) {
                return Color.CYAN;
            }
            else if ("cyan.d".equals(aColorSpec)) {
                return Color.CYAN.darker();
            }
            else if ("cyan.b".equals(aColorSpec)) {
                return Color.CYAN.brighter();
            }
            else if ("green".equals(aColorSpec)) {
                return Color.green;
            }
            else if ("green.d".equals(aColorSpec)) {
                return Color.green.darker();
            }
            else if ("green.b".equals(aColorSpec)) {
                return Color.green.brighter();
            }
            
            else if ("orange".equals(aColorSpec)) {
                return Color.orange;
            }
            else if ("orange.d".equals(aColorSpec)) {
                return Color.orange.darker();
            }
            else if ("orange.b".equals(aColorSpec)) {
                return Color.orange.brighter();
            }
            else if ("red".equals(aColorSpec)) {
                return Color.red;
            }
            else if ("red.d".equals(aColorSpec)) {
                return Color.red.darker();
            }
            else if ("red.b".equals(aColorSpec)) {
                return Color.red.brighter();
            }
            // todo all std colors
            // todo darker, lighter


            return null;
        }

    }

    // todo incorporate
    public final static Color selectHighlight = UIManager.getDefaults().getColor("EditorPane.selectionBackground");
    //EditorPane.selectionForeground	ColorUIResource

    // === styles ===
    public final static String corrected  = "corrected";
    public final static String errorCheck = "errorCheck";
    public final static String comment    = "comment";
    public final static String diff       = "diff";
    // todo: hover, highlight, select
    
    public final static String word            = "form";
    public final static String word_corrected  = "form_corrected";
    public final static String word_errorCheck = "form_errorCheck";
    public final static String word_comment    = "form_comment";
    public final static String word_spellCheckError = "form_spell";
    public final static String word_diff       = "form_diff";

    public final static String edge            = "edge";
    public final static String edge_corrected  = "edge_corrected";
    public final static String edge_comment    = "edge_comment";
    public final static String edge_diff       = "edge_diff";    

    public final static String err             = "err";
    public final static String err_errorCheck  = "err_errorCheck";
    public final static String err_comment     = "err_comment";
    public final static String err_diff        = "err_diff";    

    public final static String leg             = "leg";
    public final static String leg_corrected   = "leg_corrected";
    public final static String leg_diff        = "leg_diff";    
    
    // style attributes, todo add font style, line style (arrows, dashed, ...); ?? tooltip (refering to some variables)
    private final static String foreground = "foreground";
    private final static String background = "background";
    private final static String border_width = "border_width";
    private final static String border_color = "border_color";
    private final static String border_right_width = "border_right_width";
    private final static String border_left_width = "border_left_width";
    private final static String border_bottom_width = "border_bottom_width";
    private final static String border_top_width = "border_top_width";

    // todo add inheritance
    private Css() {
        x(word).x();

        x(word_corrected)
            .x(foreground, "red").x();

        x(word_errorCheck)
            .x(border_width, "1")
            .x(border_color, "orange.d").x();

        x(word_comment)
            .x(border_width, "1")
            .x(border_color, "green.d").x();

        x(word_spellCheckError)
            .x(border_color, "red")
            .x(border_width, "0")
            .x(border_bottom_width, "2").x();

        x(word_diff)
            .x(border_color, "orange.d")
            .x(border_width, "3")
            //.x(background, "orange.b")
            .x();

        x(edge).x();

        x(edge_corrected)
            .x(foreground, "red").x();

        x(edge_comment)             // central node
            .x(border_width, "1")
            .x(border_color, "green.d").x();
        
        x(edge_diff)               // central node
            .x(border_color, "orange.d")
            .x(border_width, "3")
            //.x(background, "orange.b")
            .x();

        x(err).x();


        x(err_errorCheck)
            .x(border_width, "1")
            .x(border_color, "orange.d").x();

        x(err_comment)
            .x(border_width, "1")
            .x(border_color, "green.d").x();

        x(err_diff)
            .x(border_color, "orange.d")
            .x(border_width, "3")
            //.x(background, "orange.b")
            .x();

    }

    private final Map<String,Style> styles = new HashMap<>();

    private Builder x(String aStyleId) {
        return new Builder(aStyleId, styles);
    }


    public Style getStyle(String aStyleId) {
        return styles.get(aStyleId);
    }

    // somehow determine the default, probably take into account LookAndFeel and state (selected, ...)
    public void setStyle(Widget aWidget, String ... aStyleIds) {
        for (String styleId : aStyleIds) {
            Style style = getStyle(styleId);
            if (styleId.equals(word_diff)) {
                log.info("Word diff: w=%s, style=%s", aWidget, style);
            }
            if (style == null) return;

            if (style.foreground != null) aWidget.setForeground(style.foreground);
            if (style.background != null) aWidget.setBackground(style.background);

            if (style.hasBorder) {
                aWidget.setBorder (BorderFactory.createCompositeBorder(aWidget.getBorder(),
                    // the visual library's border does not seem to support different border on diff sides
                    BorderFactory.createSwingBorder(aWidget.getScene(), javax.swing.BorderFactory.createMatteBorder(style.border_top_width, style.border_left_width, style.border_bottom_width, style.border_right_width, style.border_color)))
                );
            }
        }
    }
}
