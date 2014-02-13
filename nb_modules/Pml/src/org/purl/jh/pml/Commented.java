package org.purl.jh.pml;

/**
 * Element providing a comment.
 * 
 * @author Jirka
 */
public interface Commented extends Element {
    String getComment();
    Commented setComment(String comment);
}
