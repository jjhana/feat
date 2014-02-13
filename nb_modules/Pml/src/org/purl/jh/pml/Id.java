package org.purl.jh.pml;

import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An id of an element. Cannot be used as an id of a layer.
 *
 * Uniqueness:
 * Local part of the id must be unique within a layer. It can but does not have to be unique globally
 * (e.g. PDT 2.0 uses ids unique across the whole corpus).
 * 
 *
 * Check/rework
 * Structure of the id (@todo Check this):
 * layer(subLayer)-data-locId
 * Example: m-ln94208-137-p1s1, mta-ln94208-137-p1s1, 
 * layer: a single character: w/m/a/...
 * subLayer: whatever (no -)
 * data (can contain -)
 * locId (no -) 
 *
 * M Sublayer conventions:
 * a - annotated
 * m - ma
 * t - tagger
 * Any can be followed by a more detailed 
 *
 * @todo if not following this convention, use only mLocId
 * @todo optimize for small amounts of links (an array replacing MultiMap - 
 *    create a general MultiMap that does it. + allow optimizing Multimaps (rehashing, etc)
 *
 * The id itself is immutable, the backlinks can be changed.
 * @author Jirka
 */
public final class Id implements Comparable<Id>  {
    private final /*@NonNull*/ Layer<?> layer;

    private final /*@NonNull*/ String localId;

    private /*@NonNull*/ Map<Layer<?>,List<Element>> backLinks = cEmptyBackLinks;

    private final static Map<Layer<?>,List<Element>> cEmptyBackLinks = Collections.emptyMap();



    /**
     * 
     * @param aId 
     */
    public Id(/*@NonNull*/ Layer<?> aLayer, /*@NonNull*/ String aLocalId) {
        if (aLocalId == null) throw new NullPointerException();
        localId = aLocalId;
        layer = aLayer;
    }
    
// -----------------------------------------------------------------------------
// Properties
// -----------------------------------------------------------------------------

    @Deprecated
    public String getFullIdStr() {
        return layer.getId() + "#" + localId;
    }

    /**
     * 
     * @return 
     */
    public String getIdStr() {
        return localId;
    }

    public Layer<?> getLayer() {
        return layer;
    }

    public String getLocalId() {
        return localId;
    }



// -----------------------------------------------------------------------------
// Structured ids support (used in PML)
// -----------------------------------------------------------------------------
    
    /**
     * 0 (int) if no layer id present
     * @return 
     */
    public char getLayerId() {
        return (localId.length() > 0) ? localId.charAt(0) : 0;
    }

    /**
     * @return ....; empty string if sublayer not specified
     */
    public String getSubLayerId() {
        int firstDash = localId.indexOf('-');
        return (1 < firstDash) ? localId.substring(1,firstDash) : "";
    }

    /**
     * 
     * @return 
     */
    public String getDataId() {
        int firstDash = localId.indexOf('-');
        int lastDash  =  localId.lastIndexOf('-');
        return (-1 < firstDash && firstDash < lastDash) ? localId.substring(firstDash+1, lastDash) : "";
    }

    /**
     * 
     * @return 
     */
    @Deprecated
    public String getLocId() {
        int lastDash  =  localId.lastIndexOf('-');
        return (-1 < lastDash) ? localId.substring(lastDash+1) : "";
    }

    /**
     * Returns the id string without the layer prefix.
     * <ul>
     * <li>w-xxx -> xxx
     * <li>ww-xxx -> xxx
     * <li>xxx -> xxx
     * </ul>
     * @return the string behind the first dash; or the whole id if there is no dash.
     */
    public String noPrefStr() {
        if (localId.length() > 1) {
            int firstDash = localId.indexOf('-');
            return firstDash == -1 ? localId : localId.substring(firstDash+1);
        }
        else
            return localId;
    }
    

// -----------------------------------------------------------------------------
// Links
// -----------------------------------------------------------------------------

    /**
     * A single element should be added only once (no checks)
     * @param aLayer 
     * @param aElement 
     */
    public void addLink(Layer<?> aLayer, Element aElement) {
        if (backLinks == cEmptyBackLinks) backLinks = new HashMap<>();       

        List<Element> elements = backLinks.get(aLayer);
        if (elements == null) {
            elements = new ArrayList<>(1);
            backLinks.put(aLayer, elements);
        }
        elements.add(aElement);
    }

    
    /**
     * Removes the backlink from the specified element.
     *
     * @param aLayer  layer of <tt>aElement</tt>
     * @param aElement element that was pointing to this id and whose back link should be removed.
     * @return <tt>true</tt> if the the element pointed to this id.
     */
    public boolean removeLink(Layer<?> aLayer, Element aElement) {
        List<Element> elements = backLinks.get(aLayer);
        if (elements == null) return false;
        
        return elements.remove(aElement);
    }
    
    /**
     * 
     * @return 
     */
    public Map<Layer<?>,List<Element>> getBackLinks() {
        return backLinks;
    }
    
    /**
     * Returns the list of elements refering to the this id from the specified layer.
     * 
     * 
     * @return the set of elements refering to the this id from the specified layer;
     *   null if there are no such elements
     * @param aLayer 
     */
    public List<? extends Element> getBackLinks(Layer<?> aLayer) {
        return backLinks.get(aLayer);
    }
    
    /**
     * 
     * 
     * @return element or elements of the specified layer and class
     * @param aLayer 
     * @param aClass 
     */
    public <E extends Element> List<? extends E> getBackLinks(Layer<?> aLayer, Class<E> aClass) {
        List<? extends Element> allElements = getBackLinks(aLayer);
        
        // @todo try not to copy anything if they are all of the desired same class (or if there are none?)
        List<E> filteredElements = new ArrayList<>(allElements.size());
        for (Element p : allElements) {
            if (aClass.isInstance(p)) filteredElements.add((E)p);
        }
        
        return filteredElements;
    }
    
    // @todo as printer
    /**
     * 
     * @return 
     */
    public String backLinksToString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Layer<?>,List<Element>> p : backLinks.entrySet() ) {
            sb.append(p.getKey().getId());
            sb.append(": ");
            sb.append(p.getValue());    //?? just
            sb.append(", ");
        }
        return sb.toString();
    }

// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------
    
    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return getFullIdStr();
    }

    @Override
    public int compareTo(final Id that) {
        return ComparisonChain.start()
                .compare(this.layer, that.layer)
                .compare(this.localId, that.localId)
                .result();
    }
    
    @Override
    public boolean equals(Object that) {
        if (! (that instanceof Id) ) return false;

        Id thatId = (Id)that;
        return thatId.layer.equals(layer) && thatId.localId.equals(localId);
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + layer.hashCode();
        hash = 11 * hash + localId.hashCode();
        return hash;
    }

}
