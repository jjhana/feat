package org.purl.jh.feat.util0.visual;

import java.util.List;
import javax.swing.Action;

/**
 *
 * @author jirka
 */
public interface ExpandingMenuAction extends Action {
    List<Action> getActions();
}
