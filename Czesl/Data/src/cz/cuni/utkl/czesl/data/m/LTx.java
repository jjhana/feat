package cz.cuni.utkl.czesl.data.m;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.experimental.Accessors;
import org.purl.jh.pml.AbstractListElement;
import org.purl.jh.util.col.pred.AbstractFilter;

/**
 * A structure containing lemmas and tags related to a form. 
 * Individual Lt's have an id, the Lts structure does not.
 *
 * @sublt (selected, ...) with some functions
 * @author Jirka
 */
@Accessors(chain=true)
public class LTx extends AbstractListElement<Lt>  {
    public LTx() {
    }
    
    /** TOdo lightweight */
    public Set<String> getLemmas() {
        Set<String> tmp = new HashSet<>();
        
        for (Lt lt : col()) {
            tmp.add( lt.getLemma() );
        }

        return tmp;
    }

    /**
     *
     * HO: map((aSelectedOnly ? filter(mCol, isSelected()) : mCol, getLemma())
     */
    public Set<String> getLemmas(boolean aSelectedOnly) {
        Set<String> tmp = new HashSet<>();

        for (Lt lt : col) {
            if (!aSelectedOnly || lt.isSelected())
                tmp.add( lt.getLemma() );
        }

        return tmp;
    }

    public List<Lt> getItems(String aLemma) {
        List<Lt> tmp = new ArrayList<>();

        for (Lt lt : col) {
            if (lt.getLemma().equals(aLemma)) tmp.add( lt );
        }

        return tmp;
    }
    
    /**
     * @todo iterator thru selected items only (in Ss)
     * HO: map(col(aSelectedOnly), getTag())
     */
    public Set<String> getTags(boolean aSelectedOnly) {
        Set<String> tmp = new HashSet<>();

        for (Lt lt : col) {
            if (!aSelectedOnly || lt.isSelected())
                tmp.add( lt.getTag() );
        }

        return tmp;
    }

    public Set<String> getTags(String aLemma, boolean aSelectedOnly) {
        Set<String> tmp = new HashSet<>();

        for (Lt lt : col) {
            if ((!aSelectedOnly || lt.isSelected()) && lt.getLemma().equals(aLemma) )
                tmp.add( lt.getTag() );
        }

        return tmp;
    }
    
    


    /**
     * Is this LT a subset of the specified lt?
     * Only selected items are considered.
     * @param aLt lt that this one should be a subset of.
     * @param consider only tags?
     */
    public boolean isSubsetOf(LTx aLt, boolean aIgnoreLemmas) {
        for (Lt lt : selectedOnly()) {
            if ( !aLt.containsSelected(lt, aIgnoreLemmas) ) return false;
        }
        return true;
    }

    /**
     * Only selected items are considered
     */
    public boolean isSupersetOf(LTx aSs, boolean aIgnoreLemmas) {
        return aSs.isSubsetOf(this, aIgnoreLemmas);
    }
    
    /**
     * @return false if the selection is the same
     */
    public boolean different(LTx aSs, boolean aIgnoreLemmas) {
        // --- select those that are selected in aSs ---
        for (Lt ae : aSs.selectedOnly()) {
            if (!containsSelected(ae, aIgnoreLemmas)) return true;
        }
        for (Lt e : selectedOnly()) {
            if (!aSs.containsSelected(e, aIgnoreLemmas)) return true;
        }
        
        return false;
    }
    
    public boolean containsSelected(Lt aLt, boolean aIgnoreLemmas) {
        for (Lt e : selectedOnly()) {
            if (e.equalsOnItem(aLt, aIgnoreLemmas)) return true;
        }
        return false;
    }
    /**
     * @todo Does not notify view, or anything like that.
     * Optimized for aSs having low number (preferably one) of items
     */
    public void applySelection(LTx aSs, boolean aIgnoreLemmas) {
        setSelection(false);    // deselect al first

        // --- select those that are selected in aSs ---
        for (Lt ae : aSs.selectedOnly()) {
            for (Lt e : col) {
                if (e.equalsOnItem(ae, aIgnoreLemmas)) e.setSelected(true);
            }
        }
    }

    /**
     * 
     */
    public Lt findByItem(Lt aElement) {
        for (Lt e : col) {
            if (e.equalsOnItem(aElement)) return e;
        }
        return null;
    }
    
   
    
// -----------------------------------------------------------------------------
// Selection/ambivalence
// -----------------------------------------------------------------------------
    
    /** 
     * More than one or none item selected.
     */
    public boolean isAmbivalent() {
        int selected = 0;
        
        for (Lt e : col) {
            if (e.isSelected()) {
                selected++;
                if (selected > 1) return true;
            }
        }

        return selected == 0;
    }

    public boolean isNotAmbivalent() {
        return !isAmbivalent();
    }
    
    /** 
     * Checks whether no item is selected
     * Computed each time.
     * @return true if no item is selected, false otherwise.
     */
    public boolean nothingSelected() {
    	for (Lt e : col) {
            if (e.isSelected()) return false;
        }

        return true;
    }

    public Iterable<Lt> col(final boolean aSelectedOnly) {
        return aSelectedOnly ? Lt.cSelectedFilter.col(col) : col;
    }

    public Iterable<Lt> selectedOnly() {
        return Lt.cSelectedFilter.col(col);
    }

    /**
     * Iterator over children.
     */
    public Iterator<Lt> iterator(boolean aSelectedOnly) {
        return aSelectedOnly ? Lt.cSelectedFilter.iterator(col) : col.iterator();
    }

    
    
    
    public Lt firstSelected() {throw new UnsupportedOperationException();}
    
    
    /**
     * @todo Does not notify view, or anything like that.
     * The calling object is responsible for marking the top Ss as dirty
     * Optimized for aSs having low number (preferably one) of items
     */
    public void applySelection(LTx aSs) {
        setSelection(false);

        for (Lt ae : aSs.selectedOnly()) {
            Lt e = findByItem(ae);
            if (e != null) e.setSelected(true);
        }
    }

    /**
     * @todo Does not notify view, or anything like that.
     */
    public void setSelection(boolean aSelected) {
        for (Lt e : col) {
            e.setSelected(aSelected);
        }
    }
    
    /**
     *
     * Note: Keep in mind that if nothing is selected, all items are removed.
     * @see #nothingSelected()
     */
    public void removeUnselected() {
        for (Iterator<Lt> it = col.iterator(); it.hasNext();) {
            if (!it.next().isSelected()) it.remove();
        }
    }

    /**
     * A filter that keeps only ambivalent structures.
     */
    public static class AmbiFilter extends AbstractFilter<LTx> {
        public boolean isOk(LTx aSs) {
            return aSs.isAmbivalent();
        }
    }
    
    /**
     * An instance of a filter that keeps only ambivalent structures.
     */
    public final static AmbiFilter cAmbiFilter = new AmbiFilter();
}

