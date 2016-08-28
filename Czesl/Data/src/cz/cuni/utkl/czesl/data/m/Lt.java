package cz.cuni.utkl.czesl.data.m;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.purl.jh.pml.AbstractElement;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.col.pred.AbstractFilter;

/**
 * Selectable Lemma+tag pair.
 */
@Getter @Setter @Accessors(chain=true)
public class Lt extends AbstractElement implements Commented {
    /**
     * Only to be used by a data object.
     */
    protected boolean selected;
    /** Source (manual, automatic, etc.) */
    protected String src;
    protected String comment; 
    protected String lemma;
    protected String tag;

    public Lt() {
    }

    
    /**
     * Creates a duplicate of this Lt object.
     */
    public Lt duplicate(Layer layer) {
        return new Lt()
                .setSelected(selected)
                .setLemma(lemma)
                .setTag(tag)
                .setComment(comment)
                .setSrc(src);
    }

    @Override
    public LTx getParent() {
        return (LTx)super.getParent(); 
    }
    

    @Override
    public String toString() {
        return lemma + " : " + tag; /// @todo better
    }
    
    
    /**
     * A filter that keeps only selected items.
     */
    public static class SelectedFilter extends AbstractFilter<Lt> {
        public boolean isOk(Lt aItem) {
            return aItem.isSelected();
        }
    }
    
    /**
     * An instance of a filter that keeps only selected items.
     */
    public final static SelectedFilter cSelectedFilter = new SelectedFilter();
    
    /**
     * Compare only contentfull item(s), (i.e. ignores layer, selection, supporting info, etc
     */
    public boolean equalsOnItem(Lt aE) {
        return lemma.equals(aE.lemma) && tag.equals(aE.tag);
    }

    public boolean equalsOnItem(Lt aE, boolean aIgnoreLemmas) {
        return (aIgnoreLemmas || lemma.equals(aE.lemma)) && tag.equals(aE.tag);
    }
}
