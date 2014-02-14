package cz.cuni.utkl.czesl.data.layerl;

import com.google.common.collect.Iterators;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.lang.ref.SoftReference;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.netbeans.api.annotations.common.NonNull;
import org.purl.jh.feat.util0.ByListSort;
import org.purl.jh.pml.AbstractIdedElement;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * L-level paragraph. Its children are sentences or edges.
 * TODO the children are not well synchronized with sentences/edges (e.g. removal is not done). LIght-weight children??
 * todo the final state should not allow more than on edge between the same forms.
 * 
 * @author Jirka
 */
@Getter @Setter @Accessors(chain = true)
public class LPara extends AbstractIdedElement implements Para {
    @NonNull private final Para lowerPara;
    private final List<Sentence> sentences = new ArrayList<>();;
    private final List<Edge> edges = new ArrayList<>();
    protected boolean li;

    public LPara(@NonNull Layer<?> aLayer, @NonNull String aLocId, @NonNull Para lowerPara) {
        super(aLayer, aLocId);
        this.lowerPara = lowerPara;
    }

    @Override
    public LDoc getParent() {
        return (LDoc) super.getParent(); 
    }
    

//    public @NonNull Para getLowerPara() {
//        return lowerPara;
//    }
//
//    @Override
//    public boolean isLi() {
//        return li;
//    }
//
//    @Override
//    public void setLi(boolean li) {
//        this.li = li;
//    }
    
    /**
     * 
     * Note: the list of paras is constructed each time. Todo should it be? 
     * @return list of paragraphs, starting with the lowest paragraph and ending 
     * with this one.
     */
    @Override
    public @NonNull List<Para> getLowerEqParas() {
        final List<Para> paras = new ArrayList<>();
        
        Para para = this;
        for (;;) {
            paras.add(0, para);
            if (!(para instanceof LPara)) return paras;

            para = ((LPara) para).getLowerPara();
        }
    }
    
    public WPara getWPara() {
        Para tmp = this;
        for (;;) {
            if (tmp == null || tmp instanceof WPara) return (WPara) tmp;
            tmp = ((LPara) tmp).getLowerPara();
        }
    }

//    public Set<Edge> getEdges(Form aLowerForm) {
//        // todo computer
//        return aLowerForm.getHigher();
//    }

    /**
     * Adds sentence or edge as a child to this paragraph
     * 
     * @param element 
     */
    public void add(IdedElement element) {
        if (element instanceof Sentence) {
            getSentences().add( (Sentence)element);
        }
        else if (element instanceof Edge) {
            getEdges().add( (Edge)element);
        }
        else {
            throw Err.iErr("%s is not a sentence or edge.", element);
        }
    }

//    public @NonNull List<Sentence> getSentences() {
//        return sentences;
//    }
//
//    /**
//     * The returned order of edges has no meaning.
//     * @return 
//     */
//    public @NonNull List<Edge> getEdges() {
//        return edges;
//    }


    
    
    @Deprecated
    @Override
    public List<LForm> getFormsList() {
        int count = 0;
        for (Sentence s : sentences) {
            count += s.size();
        }
        
        final List<LForm> forms = new ArrayList<>(count);
        for (Sentence s : sentences) {
            forms.addAll(s.col());
        }

        return forms;
    }

    @Override
    public Iterable<LForm> getForms() {
        return new Iterable<LForm>() {
            @Override
            public Iterator<LForm> iterator() {
                return concatIt(sentences);
            }
        };
    }

    public static Iterator<LForm> concatIt(final Iterable<Sentence> aSentences) {
        final Iterator<Sentence> sentIt = aSentences.iterator();
        
        return new Iterator<LForm>() {
            Iterator<LForm> current = Iterators.emptyIterator();

            @Override
            public boolean hasNext() {
                for(;;) {
                    if (current.hasNext()) return true;
                    if (!sentIt.hasNext()) return false;
                    current = sentIt.next().iterator();
                }
            }

            @Override
            public LForm next() {
                if (!hasNext()) throw new NoSuchElementException();
                
                return current.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    
    
    @Override
    public String toString() {
        return super.toString() + " sentences=" + Cols.toString(sentences);
    }

    /**
     * Checks the relative position of two forms.
     * Assumes (but does not check) that both forms are present in the paragraph.
     * Once one of the form is found, it is assumed the other form follows later
     * in the paragraph.
     *
     * @param aForm1 the first form
     * @param aForm2 the second form
     * @return true if Form1 precedes Form2, false otherwise.
     */
    @Override
    public boolean before(final FForm aForm1, final FForm aForm2) {
        for(Sentence s : getSentences()) {
            for(FForm f : s.col()) {
                if (aForm1 == f) return true;
                if (aForm2 == f) return false;
            }
        }

        throw new IllegalArgumentException(
                String.format("Both forms must be part of this paragraph (id=%s); neither is. (form1: id=%s, token=%s; form2: id=%s, token=%s)",
                getId(), aForm1.getId(), aForm1.getToken(), aForm2.getId(), aForm2.getToken()));
    }

// =============================================================================    

// =============================================================================    
    private SoftReference<ByListSort<LForm>> woSorterRef;

    private ByListSort<LForm> getWoSorter() {
        ByListSort<LForm> sorter = woSorterRef == null ? null : woSorterRef.get();
        if (sorter == null) {
            sorter = new ByListSort<>(getFormsList());
            woSorterRef = new SoftReference<>(sorter);
        }
        return sorter;
    }
    
    /**
     * 
     * @param aLForms list of forms to sort, is sorted in place thus must be modifiable
     * @return 
     */
    public List<LForm> sort(List<LForm> aLForms) {
        ByListSort<LForm> sorter = getWoSorter();
        sorter.sort(aLForms);
        return aLForms;
    }
    
    
    /**
     * Returns all forms in this para connected to the lower form.
     * Access from lower structures.
     * 
     * @param aLowerForm
     * @return list of forms ordered by wo
     */
    public List<LForm> getForms(FForm aLowerForm) {
        Set<LForm> forms = aLowerForm.getHigherForms(); // todo replace with some local structure
        return sort(new ArrayList<>(forms));
    }
    
}
