package org.purl.jh.pml.event;

import org.purl.jh.pml.Data;
import org.purl.jh.pml.IdedElement;

/**
 *
 * @param <L> layer this event occurred on. (If this is a multilayer event, use a
 * generic parameter)
 * 
 * @author Jirka
 */
public class DataEvent<L extends Data<?>> implements Event {
    public static final String cFileNameChange = "fileNameChange"; 
    
    final protected L layer;

    /**
     * Event type.
     *
     * The recommended pattern is to define string constants (in a subclass or a 
     * separate class).
     * The id must be passed to the constructor. Unlike enum,
     * the set of String ids is extensible.
     */
    public final String id;


    /**
     * The view originating the modification request. Can be null if the 
     * originating view is not interested in being able to identify itself as the 
     * source when handling this event.
     *
     * Note: In theory, it does not need to be a DataListener, but typing it
     * as DataListener instead of Object prevents errors in swap of
     * parameters and in fact, the vast majority of views will be
     * DataListener (the rest can just implement it using empty methods).
     */
    private final DataListener srcView;


    /**
     * Any information the originating view wants to send with the event. 
     * Can be null.
     *
     * Usually this is ignored by all views that are not srcView. The srcView
     * might use it to react to the event in a more efficient/natural way.
     * E.g. it might use it to pass exact mouse coordinates when something is moved
     * so the event handling code can move it to exactly the same place on the screen,
     * not just a logically correct place.
     */
    private final Object srcViewInfo;

    /**
     * 
     * @param aAffectedLayer
     * @param aEventId
     * @param aSrcView
     * @param aSrcViewInfo 
     */
    public DataEvent(L aAffectedLayer, String aEventId, DataListener aSrcView, Object aSrcViewInfo) {
        layer = aAffectedLayer;
        id = aEventId;
        srcView = aSrcView;
        srcViewInfo = aSrcViewInfo;
    }

    /**
     * Event Id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the affected layer. Return null if this is a multilayer even.
     *
     * @return the document
     */
    public L getData() {
        return layer;
    }

    public DataListener getSrcView() {
        return srcView;
    }

    public Object getSrcViewInfo() {
        return srcViewInfo;
    }

    /**
     * Used in toString, to print the id of ided elements.
     * @param aEl
     * @return
     */
    protected static String eIdOrNull(IdedElement aEl) {
        return aEl == null ? null : aEl.getId().getIdStr();
    }

}
