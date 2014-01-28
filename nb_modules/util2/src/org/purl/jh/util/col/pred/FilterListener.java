package org.purl.jh.util.col.pred;

import java.util.EventListener;

/**
 * Usually implemented by a list model or composite filters.
 *
 * @author Jirka
 */
public interface FilterListener extends EventListener {
    void filterUpdated();
}
