package org.purl.jh.pml;

import lombok.Getter;
import org.purl.jh.pml.location.Location;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class IdedListElement<C extends Element> extends AbstractListElement<C> implements IdedElement {
    @Getter private final Id id;

    /**
     * Creates a new instance of Document.
     */
    public IdedListElement(/*@NonNull*/ Layer<?> aLayer, /*@NonNull*/ String aLocId) {
        id = new Id(aLayer, aLocId);
    }

    /** Convenience method */
    public void addLink(Layer<?> aLayer, Element aSrc) {
        id.addLink(aLayer, aSrc);
    }

    @Override
    public Location location() {
        return Location.of(this);
    }


    @Override
    public final boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final IdedListElement<C> other = (IdedListElement<C>) obj;

        return id.equals( other.id );
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString() + " " + super.toString();
    }

}
