package org.purl.jh.feat.layered.util;

import java.util.List;
import org.purl.jh.util.col.XCols;

/**
 * Listeners of the same type.
 *
 * Possibly in the future the structure could be synchronized or support firing
 * events.
 * 
 * @author jirka
 */
public class Listeners<T> {
    private final List<T> listeners = XCols.newArrayList();


    /**
     * Registers the given observer to begin receiving notifications
     * when changes are made to the document.
     *
     * @param aListener the observer to register
     */
    public void add(T aListener) {
        listeners.add(aListener);
    }

    /**
     * Unregisters the given observer from the notification list
     * so it will no longer receive change updates.
     *
     * @param aListener the observer to register
     */
    public void remove(T aListener) {
        listeners.remove(aListener);
    }


    public List<T> copy () {
        return XCols.newArrayList(this.listeners);
    }
}
