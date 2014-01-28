package org.purl.jh.feat.layered.actions;

import org.purl.jh.feat.layered.LayeredGraph;
import org.purl.jh.feat.layered.ParaModel;
import javax.swing.AbstractAction;
import org.purl.jh.feat.layered.VModel;

/**
 *
 * @author jirka
 */
public abstract class GraphAction extends AbstractAction {
    protected final LayeredGraph view;
    protected final boolean readOnly;

    /**
     * Editing action (not read-only)
     * @param view
     * @param text
     */
    protected GraphAction(LayeredGraph view, String text) {
        this(view, text, false);
    }

    /**
     *
     * @param view
     * @param text
     * @param readOnly does the action modify anything?
     */
    protected GraphAction(LayeredGraph view, String text, boolean readOnly) {
        putValue(NAME, text);
        this.view = view;
        this.readOnly = readOnly;
        setEnabled(readOnly || !view.isReadonly());  // todo but this means that change in view.isReadOnly flag is not reflected
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && (readOnly || !view.isReadonly());
    }



    protected ParaModel getParaModel() {
        return view.getParaModel();
    }

    protected VModel getModel() {
        return view.getParaModel().getPseudoModel();
    }
}
