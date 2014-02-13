package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerl.Errorr;
import org.purl.jh.feat.ea.data.layerl.LLayer;
import org.purl.jh.feat.layered.util.SubMenuAction;
import org.purl.jh.feat.layered.LayeredGraph;
import org.purl.jh.feat.ea.data.layerx.Position;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.feat.sconfig.ErrorSpecs;
import org.purl.jh.feat.sconfig.ErrorTag;

/**
 * todo org/openide/awt/Mnemonics.java
 * @author Jirka
 */
public class MenuActionErrorAdd extends AbstractAction implements SubMenuAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(MenuActionErrorAdd.class);
    
    private final LayeredGraph view;
    private final Edge rel;

    public MenuActionErrorAdd(final LayeredGraph aView, Edge aEdge) {
        super("Add Error");
        view = aView;
        rel = aEdge;
    }

    public int inline() {
        return 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new RuntimeException("Should not be ever called.");
    }

    @Override
    public List<Action> getActions(final Widget aWidget, final Point aPoint, Position anchor) {
        log.fine("MenuActionErrorAdd.getActions");
        final LLayer layer = rel.getLayer();
        final List<Action> actions = new ArrayList<>();
        
        for ( ErrorTag tag : ErrorSpecs.INSTANCE.getErrorSpecs(layer.getLayerIdx()).getTags() ) {
            if (hasError(rel, tag.getId()) || tag.isDeprecated() || tag.isAuto()) {
                continue;
            }

            final ErrorTag ftag = tag;

            final Action act = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    log.fine("MenuActionErrorAdd...actionPerformed etag=%s, rel=%s", ftag, rel);
                    layer.errorAdd(ftag.getId(), rel, view, null);
                }
            };

            setLabel(act, ftag.getMenuLabel());

            actions.add(act);
        }

        return actions;
    }

    // todo do via mapping and contains
    private static boolean hasError(final Edge aEdge, final String errorTag) {
        for (Errorr error : aEdge.getErrors()) {
            if (error.getTag() == errorTag) {
                return true;
            }
        }
        return false;
    }

    private void setLabel(Action action, String text) {
        int idx = org.openide.awt.Mnemonics.findMnemonicAmpersand(text);
        if (idx < 0) {
            action.putValue(NAME, text);
            action.putValue(MNEMONIC_KEY, 0);
            action.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, -1);
        } else {
//            if (Utilities.isMac()) return;
            char ch = text.charAt(idx + 1);
            if (text.startsWith("<html>")) { // NOI18N
                // Workaround for JDK bug #6510775
                action.putValue(NAME, text.substring(0, idx) + "<u>" + ch + "</u>" + text.substring(idx + 2)); // NOI18N
                idx += 3; // just in case it gets fixed
            }

            if ((('A' <= ch) && (ch <= 'Z')) || (('0' <= ch) && (ch <= '9'))) {
                action.putValue(MNEMONIC_KEY, (int)ch); ;
            }
            else if (('a' <= ch) && (ch <= 'z')) {
                action.putValue(MNEMONIC_KEY, (int) (ch + ('A' - 'a')) );
            }
            else {
                // it's non-latin, getting the latin correspondance, ignore for now
            }

            action.putValue(NAME, text.substring(0, idx) + text.substring(idx + 1));
            action.putValue(DISPLAYED_MNEMONIC_INDEX_KEY, idx);
        }
    }
}

