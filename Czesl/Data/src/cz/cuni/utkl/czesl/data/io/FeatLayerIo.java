package cz.cuni.utkl.czesl.data.io;

import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import org.purl.jh.pml.io.LayerReader;

public class FeatLayerIo {
    public static FForm.Type getTokenType(LayerReader<?> r, final org.jdom.Element aFormE) {
        String type = aFormE.getChildText("type", r.n);
        if (type != null) return WForm.Type.valueOf(type);

        // --- for backward compatibility ---
        String tmp = aFormE.getChildText("dt", r.n);
        if ("1".equals(tmp)) return WForm.Type.dt;
        
        tmp = aFormE.getChildText("img", r.n);
        if ("1".equals(tmp)) return WForm.Type.img;
    
        tmp = aFormE.getChildText("priv", r.n);
        if ("1".equals(tmp)) return WForm.Type.priv;
        
        // --- default ---
        return WForm.Type.normal;
    }    

}
