package org.purl.jh.util.col.pred;

import javax.swing.event.EventListenerList;

/**
 *
 * @author Jirka
 */
public abstract class AbstractFilter<T> extends AbstractPredicate<T> implements Filter<T> {
    //protected Listeners<FilterListener> mListeners = new Listeners<FilterListener>();
    protected EventListenerList mxListeners = new EventListenerList();

    /**
     * Adds a listener to the list that's notified each time a change in the 
     * filter occurs.
     *
     * @param l the <code>FilterListener</code> to be added
     */  
    public void addFilterListener(FilterListener l) {
	//mListeners.add(l);
	mxListeners.add(FilterListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a change in the
     * filter occurs.
     *
     * @param l the <code>FilterListener</code> to be removed
     */  
    public void removeFilterListener(FilterListener l) {
	//mListeners.remove(l);
        mxListeners.remove(FilterListener.class, l);
    }

    /**
     * Call this method when the filter is updated to notify listeners.
     * @todo allow event detaily specifying the chagne
     */
    protected void updated() {
//        System.out.printf("AbstractFilter.updated (this: %s, listeners %s)\n", getClass().getName(), mxListeners.toString());
//	FilterListener[] listeners = (FilterListener[]) mListeners.getListeners();
//
//	for (int i = listeners.length - 1; i >= 0; i --) {
//            listeners[i].filterUpdated();       // @todo okay? does not send event, but the filter directly
//	}

        Object[] listeners = mxListeners.getListenerList();
        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == FilterListener.class) {
                ((FilterListener)listeners[i+1]).filterUpdated();
            }
        }
    }
}

        