
package org.purl.jh.util.gui.list;


import java.io.*;
import java.util.*;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * Contains only listeners of a single class.
 */
public final class Listeners<L extends EventListener> implements Serializable {
    /* A null array to be shared by all empty listener lists*/
    private final static EventListener[] NULL_ARRAY = new EventListener[0];

    /* The list of ListenerType - Listener pairs */
    protected transient EventListener[] listenerList = NULL_ARRAY;

    /**
     * Do not modify the results
     */
    @SuppressWarnings("unchecked")
    public L[] getListeners() {
	return (L[]) listenerList;
    }

    /**
     * @return unmodifiable list of listeners
     */
    @SuppressWarnings("unchecked")
    public List<L> getListenerList() {
	return  Collections.unmodifiableList(Arrays.asList((L[])listenerList));
    }
    
    /**
     * Returns the total number of listeners for this listener list.
     */
    public int getListenerCount() {
	return listenerList.length;
    }

    /**
     * Adds the listener as a listener of the specified type.
     * @param t the type of the listener to be added
     * @param l the listener to be added
     */
    public synchronized void add(L aListener) {
	Err.iAssert(aListener!=null, "null listener");

	if (listenerList == NULL_ARRAY) {
	    // if this is the first listener added, initialize the lists
	    listenerList = new EventListener[] { aListener };
	} 
        else {
	    // Otherwise copy the array and add the new listener
	    int i = listenerList.length;
	    EventListener[] tmp = new EventListener[i+1];
	    System.arraycopy(listenerList, 0, tmp, 0, i);

	    tmp[i] = aListener;

	    listenerList = tmp;
	}
    }

    /**
     * Removes the listener as a listener of the specified type.
     * @param t the type of the listener to be removed
     * @param l the listener to be removed
     */
    public synchronized void remove(L aListener) {
	Err.iAssert(aListener!=null, "null listener");

        // --- find aListener on the list? ---
	int index = -1;
	for (int i = listenerList.length-1; i >= 0; i--) {
	    if (listenerList[i].equals(aListener)) {
		index = i;
		break;
	    }
	}
	
	// --- If so, remove it ---
	if (index != -1) {
	    EventListener[] tmp = new EventListener[listenerList.length-1];
	    // Copy the list up to index
	    System.arraycopy(listenerList, 0, tmp, 0, index);
	    // Copy from one past the index, up to the end of tmp 
	    if (index < tmp.length)
		System.arraycopy(listenerList, index+1, tmp, index, tmp.length - index);
	    // set the listener array to the new array or null
	    listenerList = (tmp.length == 0) ? NULL_ARRAY : tmp;
        }
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
	EventListener[] lList = listenerList;
	s.defaultWriteObject();
	
	// Save the non-null event listeners:
	for (int i = 0; i < lList.length; i++) {
	    EventListener l = lList[i];
	    if ((l!=null) && (l instanceof Serializable)) {
		s.writeObject(l);
	    }
	}
	
	s.writeObject(null);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        listenerList = NULL_ARRAY;
	s.defaultReadObject();
        Object listenerTypeOrNull;

        
	for (;;) {
	    EventListener l = (EventListener)s.readObject();
            if (l == null) break;
	    add((L)l);
	}	    
    }

    /**
     * Returns a string representation of the EventListenerList.
     */
    public String toString() {
	return String.format("EventListenerList: %d listeners %s", listenerList.length, Cols.toString(getListenerList()));
    }
}
