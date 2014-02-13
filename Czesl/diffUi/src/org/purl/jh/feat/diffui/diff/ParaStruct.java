package org.purl.jh.feat.diffui.diff;

import org.purl.jh.feat.ea.data.layerl.LPara;
import org.purl.jh.feat.ea.data.layerw.WPara;
import org.purl.jh.feat.ea.data.layerx.Para;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author j
 */
public class ParaStruct {
    private final List<Para> paras;
    private final Para topPara;
    private final WPara wpara;
    private final List<LPara> lparas;

    public ParaStruct(LPara aTopPara) {
        topPara = aTopPara;
        paras = topPara.getLowerEqParas();
        wpara = (WPara)paras.get(0);
        
        lparas = new ArrayList<>(paras.size());
        for (Para para : paras) {
            if (para instanceof LPara) {
                lparas.add((LPara)para);
            }
        }
    }

    public List<Para> getParas() {
        return paras;
    }

    public Para getTopPara() {
        return topPara;
    }

    public WPara getWpara() {
        return wpara;
    }

    public List<LPara> getLparas() {
        return lparas;
    }
}
