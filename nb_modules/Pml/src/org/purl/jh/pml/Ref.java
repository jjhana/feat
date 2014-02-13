package org.purl.jh.pml;

/**
 * Reference. todo to what????
 * @author Jirka
 */
public class Ref<E extends IdedElement,P extends Element> extends AbstractElement {
    private final Layer<?> layer;
    private final E element;

    /**
     * Adds back-link to this ref-id. TODO shouldn't it be to the parent of this rid?
     * @param layer
     * @param aElement
     */
    public Ref(Layer<?> layer, E aElement) {
        this.layer = layer;
        element = aElement;

        element.getId().addLink(layer, this); // todo link to this or to the parent???
    }

    public Id getId() {
        return element.getId();
    }

    @Override
    public Layer<?> getLayer() {
        return layer;
    }

    public E getElement() {
        return element;
    }


}
