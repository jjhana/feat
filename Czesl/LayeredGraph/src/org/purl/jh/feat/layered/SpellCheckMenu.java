package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerx.Position;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.feat.layered.util.SubMenuAction;
import org.purl.jh.feat.util0.visual.WidgetActionEvent;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;

/**
 * TODO Under dev
 * @author Jirka
 */
public class SpellCheckMenu extends AbstractAction implements SubMenuAction {
    final LayeredGraph view;

    public SpellCheckMenu(final LayeredGraph aView) {
        super("Word change suggestions");
        view = aView;
    }

    public int inline() {
        return 3;
    }


    @Override
    public List<Action> getActions(final Widget aWidget, final Point aPoint, Position anchor) {
        Dictionary spellchecker = view.getProfile().getSpellchecker();
        if (spellchecker == null) return Collections.emptyList();
        
        final String labelTemplate = (0 <= inline()) ? "Change to %s" : "%s";

        final List<Action> actions = new ArrayList<>();

        final FForm form = view.findForm(aWidget);
        if (view.getProfile().getSpellchecker().validateWord(form.getToken()) == ValidityType.VALID) return Collections.emptyList();

        // todo handle transcription ambiguity
        final List<String> proposals = spellchecker.findProposals(form.getToken());


        // filter by capitalization

        for (String str : proposals) {
            final String str1 = str;
            actions.add( new AbstractAction(String.format(labelTemplate, str)) {
                public void actionPerformed(ActionEvent e) {
                    WordWidget w = (WordWidget) ((WidgetActionEvent)e).getWidget();
                    w.getNode().getLayer().formEdit(form, str1, view, w);
                }
            });
        }

        return actions;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        throw new RuntimeException("Should not be ever called.");
    }



}
