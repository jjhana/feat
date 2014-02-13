package org.purl.jh.pml;

import org.purl.jh.pml.location.Location;

/**
 *
 * @author Jirka
 */
public abstract class AbstractElement implements Element {
    protected Element parent;

    public AbstractElement() {
    }

    @Override
    public void setParent(Element parent) {this.parent = parent;}

    @Override
    public Element getParent() {return parent;}

    @Override
    public <T> T getAncestor(Class<T> aC) {
        if (aC == null) return null;

        Element tmp = getParent();
        while ( tmp != null && !(aC.isInstance(tmp)) )
            tmp = tmp.getParent();

        return aC.cast(tmp);
    }

    @Override
    public Layer<?> getLayer() {
        return getAncestor(Layer.class);
    }

    @Override
    public Location location() {
        return null;
    }

    /**
     * Arbitrary ordering, just to support saving elements in some fixed order.
     * Override if possible.
     * @param that
     * @return 
     */
    @Override
    public int compareTo(Element that) {
        if (this == that) return 0;
        
        return this.hashCode() - that.hashCode();
    }

    
}
