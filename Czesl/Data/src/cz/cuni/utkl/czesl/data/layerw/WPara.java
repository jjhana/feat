package cz.cuni.utkl.czesl.data.layerw;

import com.google.common.collect.ImmutableList;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.Para;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.netbeans.api.annotations.common.NonNull;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.IdedListElement;

/**
 * W-level element. Its children are mostly forms but possibly also other markup.
 *
 * @author Jirka
 */
@Getter @Setter @Accessors(chain = true)
public class WPara extends IdedListElement<WForm> implements Para, Commented {

    /*
     * Any comment an annotator may assign to this paragraph.
     */
    protected String comment;
    protected boolean li;
    
    public WPara(final WLayer aLayer, final String aLocId) {
        super(aLayer, aLocId);
    }
    
    @Override
    public WDoc getParent() {
        return (WDoc) super.getParent(); 
    }

    
// -----------------------------------------------------------------------------
//
// -----------------------------------------------------------------------------    
    @Override
    public @NonNull List<Para> getLowerEqParas() {
        return ImmutableList.<Para>of(this);    
    }

    @Override
    public List<WForm> getFormsList() {
        return getForms();
    }

    @Override
    public List<WForm> getForms() {
        //System.out.println("WP:getForms");
        return col();
    }

    @Override
    public boolean before(final FForm aForm1, final FForm aForm2) {
        for(FForm f : col()) {
            if (aForm1 == f) return true;
            if (aForm2 == f) return false;
        }

        throw new IllegalArgumentException(
                String.format("Both forms must be part of this paragraph (id=%s); neither is. (form1: id=%s, token=%s; form2: id=%s, token=%s)",
                getId(), aForm1.getId(), aForm1.getToken(), aForm2.getId(), aForm2.getToken()));
    }


//
//    @Override
//    public void add(Element aEl) {
//        super.add(aEl);
//        if (aEl instanceof WForm) {
//            addMChild((WForm)aEl);
//            getParent().getParent().addForm((WForm)aEl);
//        }
//    }



//    public Form getForm(String aId) {
//        return mId2Forms.get(aId);
//    }
}
