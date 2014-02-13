package org.purl.jh.pml;

/**
 * Ided emement with a comment.
 * Convenience class: Ided elements are often commented.
 * 
 * @param <P> parent element of this element
 */
public class AbstractCommentedIdedElement 
        extends AbstractIdedElement implements Commented {

    private String comment;

    public AbstractCommentedIdedElement(Id id) {
        super(id);
    }
    
    @Deprecated
    public AbstractCommentedIdedElement(Layer<?> aLayer, String aLocId) {
        super(aLayer, aLocId);
    }
    
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public AbstractCommentedIdedElement setComment(String comment) {
        this.comment = comment;
        return this;
    }
}
