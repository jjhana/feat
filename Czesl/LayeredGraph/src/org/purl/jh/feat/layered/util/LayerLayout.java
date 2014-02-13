package org.purl.jh.feat.layered.util;

import org.netbeans.api.visual.layout.Layout;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * todo listeners are not synchronized and/or coppied before processing
 * @author jirka
 */
public abstract class LayerLayout implements Layout {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerLayout.class);

    protected final Listeners<LayoutListener> listeners = new Listeners<>();

    /**
     * Layout listeners. Use its add/remove methods to add/remove listeners.
     * @return
     */
    public Listeners<LayoutListener> getListeners() {
        return listeners;
    }

    public void layout(final Widget aLayer) {
        for (LayoutListener listener : listeners.copy()) {
            listener.layoutStarted(aLayer);
        }

        layoutImpl(aLayer);

        for (LayoutListener listener : listeners.copy()) {
            listener.layoutStarted(aLayer);
        }
    }

    public abstract void layoutImpl(final Widget aLayer);
}
