package cz.cuni.utkl.czesl.data.layerx;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.purl.jh.pml.AbstractIdedElement;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.col.Mapper;

/**
 * Super-class of word-elements at any {@link FormsLayer}.
 * 
 * Consider moving things like hasSingleLowerForm, isChanged, isChanging
 */
@Getter @Setter @lombok.experimental.Accessors(chain = true)
public class FForm extends AbstractIdedElement  implements Commented, org.purl.jh.pml.Form {
    /** 
     * Type of the form. 
     */ 
    public static enum Type {
        normal, 
        img, 
        /** Token coded for privacy (e.g. personal name, address, etc.) */
        priv, 
        /** Provided text (In finish the story or fill in the blanks exercise) */
        dt;
        
        public boolean normal() {return this == normal;}
        public boolean img()    {return this == img;}
        public boolean priv()   {return this == priv;}
        public boolean dt()     {return this == dt;}
        
    }

    
    public final static Form2Token cForm2Token = new Form2Token();
    
    public static class Form2Token implements Mapper<FForm, String> {
        @Override
        public String map(FForm aOrigItem) {
            return aOrigItem.getToken();
        }
    }

    /** 
     * Type of the form. 
     * Whole form has exactly one type, despite the fact that in theory:
     * <ul>
     * <li>They could be combined (e.g. private image, or dt containing an image.
     * <li>Alternatives could have a different type
     * </ul>
     */ 
    private Type type;
    
    /** 
     * Edges going to the higher layer
     *
     * @Deprecated This should not be here as there might be multiple parallel
     * higher layer referring to this layer.
     * todo: this should be part of some computed structure of the higher layer, as multiple higher layers could link to this form's layer
     */
    protected final Set<Edge> higher = new HashSet<>(); 

    /** Edges going to the lower layer */
    protected final Set<Edge> lower = new HashSet<>();

    /**
     * The word itself
     */
    protected String token;

    /*
     * Any comment an annotator may assign to this form.
     */
    protected String comment;

// =============================================================================
//
// =============================================================================
    /**
     *
     * @param layer
     * @param id
     * @param token
     */
    public FForm(Layer<?> layer, String id, Type type, String token) {
        super(layer, id);
        this.type = type;
        this.token = token;
        
    }


// -----------------------------------------------------------------------------
// Attributes
// -----------------------------------------------------------------------------

    @Override
    public FormsLayer<?> getLayer() {
        return (FormsLayer<?>) super.getLayer();
    }

    @Override
    public String getFormStr() {
        return token;
    }

    /** Forms on the higher layer connected to this form 
     * Convenience function; the forms are collected on each call.
     *
     * @Deprecated This should not be here as there might be multiple parallel
     * higher layer referring to this layer.
     */
    @Deprecated
    public Set<LForm> getHigherForms() {
        final Set<LForm> forms = new HashSet<>();
        for (Edge e : getHigher()) {
            forms.addAll(e.getHigher());
        }

        return forms;
    }

    /**
     * Returns the forms on the higher layer connected to this form iff
     * there is a single such a form or null if there is none or more forms.
     *
     * @Deprecated This should not be here as there might be multiple parallel
     * higher layer referring to this layer.
     */
    @Deprecated
    public LForm getHigherForm() {
        LForm form = null;
        for (Edge e : getHigher()) {
            for (LForm f : e.getHigher()) {
                if (form != null) return null;  // more than one of
                form = f;
            }
        }

        return form;
    }

    public boolean hasSingleLowerForm() {
        int count = 0;
        for (Edge e : getLower()) {
            count += e.getLower().size();
        }

        return count == 1;
    }

    /** 
     * Forms on the lower layer connected to this form.
     * Convenience function; the forms are collected on each call.
     */
    public Set<FForm> getLowerForms() {
        final Set<FForm> forms = new HashSet<>();
        for (Edge e : getLower()) {
            forms.addAll(e.getLower());
        }

        return forms;
    }

    /**
     * Returns the forms on the lower layer connected to this form iff
     * there is a single such a form or null if there is none or more forms.
     */
    public FForm getLowerForm() {
        FForm form = null;
        for (Edge e : getLower()) {
            for (FForm f : e.getLower()) {
                if (form != null) return null;  // more than one of
                form = f;
            }
        }

        return form;
    }

    /** 
     * Convenience function returning a union of lower and higher connected nodes.
     * TODO lightweight set.
     */
//    public Set<Form> getNeighboringForms() {
//        final Set<Form> neighbors = new HashSet<Edge>();
//        neighbors.addAll(getLowerForms());
//        neighbors.addAll(getHigherForms());
//        return neighbors;
//    }



// -----------------------------------------------------------------------------
// funtions
// -----------------------------------------------------------------------------

    public boolean isChangingForm(boolean aImmediate) {
        if (aImmediate) {
            if ( !hasSingleLowerForm() ) return true;
            if ( !getLowerForm().getToken().equals(getToken()) ) return true;

            return false;
        }
        else {
            if (isChangingForm(true)) return true;
            
            FForm lowerForm = getLowerForm();
            if (lowerForm instanceof LForm) {
                return ((LForm)lowerForm).isChangingForm(false);
            } 
            else {
                return false;
            }
        }
    }
    
    public boolean isChangedForm(Layer<?> aLayer) {
        return false;
    }

    public String toDebugString() {
        return String.format("%s : %s", token, getId());
    }

    
// -----------------------------------------------------------------------------
// funtions
// -----------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return getToken();
    }

    /**
     * TODO Move to util:Lang.eq
     * @param <T>
     * @param aA
     * @param aB
     * @return 
     */
    @Deprecated /* use the code in util instead */
    public static <T> boolean eq(final T aA, final T aB) {
        if (aA == null) {
            return aB == null;
        }
        return aA.equals(aB);
    }
}

