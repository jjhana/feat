package cz.cuni.utkl.czesl.data.layerl;

import java.util.List;
import org.netbeans.api.annotations.common.NonNull;
import org.purl.jh.pml.IdedListElement;
import org.purl.jh.util.str.Ids;

/**
 * La-level element
 *
 * @author Jirka
 */
public class Sentence extends IdedListElement<LForm> {

    final List<String> ids = org.purl.jh.pml.util.Ids.e2idStrs(col);

    public Sentence(@NonNull LLayer aLayer, @NonNull String aLocId) {
        super(aLayer, aLocId);
    }

    @Override
    public LPara getParent() {
        return (LPara) super.getParent(); 
    }
    
    
    
    @Override
    public LLayer getLayer() {
        return (LLayer) super.getLayer();
    }



//    /**
//     * Adds form to as an child to this sentence and directly to the w-layer.
//     */
//    @Override
//    public void add(LForm aForm) {
//        super.add(aForm);
//        LLayer layer = (LLayer)getLayer();
//        layer.addForm(aForm);
//    }

    /** 
     * TODO not sure if this is the best idea.
     * Interface IdProvider (aggregated object?)
     *
     */
    public String getNewId(LForm aPrev) {
        //  todo something better
        String suggested = aPrev.getId().getIdStr();

        return Ids.findUniqueId(suggested, ids, "-");
    }





}
