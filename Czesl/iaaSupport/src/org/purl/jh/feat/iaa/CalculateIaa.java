package org.purl.jh.feat.iaa;

import com.google.common.collect.Maps;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.feat.util0.ByListSort;
import org.purl.jh.util.err.Err;


/**
 *
 * @author jirka
 */
public class CalculateIaa  {
    private final LLayerDataObject dobj1;
    private final LLayerDataObject dobj2;

    private final Conf conf;
    
    private final LLayer aLayer1;
    private final LLayer aLayer2;
    private final WLayer wLayer;
    
    private final Map<String,LStruct> wform2proj1 = Maps.newHashMap();
    private final Map<String,LStruct> wform2proj2 = Maps.newHashMap();
    
//    private final MultiMap<WForm,LForm> wform2emend = MultiHashHashMap.neww();
    
    final Counter counter;
    
    public CalculateIaa(final LLayerDataObject aDobj1, final LLayerDataObject aDobj2, Conf conf, final Counter counter) {
        this.dobj1 = aDobj1;
        this.dobj2 = aDobj2;
        this.counter = counter;
        this.conf = conf;

        this.aLayer1 = dobj1.getData();
        this.aLayer2 = dobj2.getData();
        this.wLayer = Util.getWLayer(aLayer1);
    
    }

    public void project() {
        Err.fAssert(aLayer1.getTagset().equals(aLayer2.getTagset()), "Layers have different tagset");
        
        counter.docStarted();
        boolean isALayer = aLayer1.getLowerLayer() instanceof WLayer;
        counter.initTags(isALayer, aLayer1.getTagset());
        project2wlayer();
        counter.docEnded();
        //writeOut(wLayer);
        
        // todo this is just a temporary hack - hijacking this code to produce examples for each tag
//        for (Tag tag : aLayer.getTagset().getTags()) {
//            examplesForTag(tag, aLayer);
//        }
        
    }
    

    private void project2wlayer() {
        project2wforms(wform2proj1, aLayer1);
        project2wforms(wform2proj2, aLayer2);
        
        for (WDoc wdoc : wLayer.col()) {
            for (WPara wpara : wdoc.col()) {
                for (WForm wform : wpara.col()) {
                    LStruct lstruct1 = wform2proj1.get(wform.getId().getIdStr());
                    LStruct lstruct2 = wform2proj2.get(wform.getId().getIdStr());
                    if (lstruct1 == null) lstruct1 = new LStruct();                    
                    if (lstruct2 == null) lstruct2 = new LStruct();                    

                    counter.tick(wform, lstruct1, lstruct2);    
                }
            }
        } 
    }

    /**
     * All projections are merged. Thus if w is split into b1 and b2 and 
     * the 1st annotation has b1 with tag1,tag2, b2 with tag3, while
     * the 2nd annotation has b1 with tag1, b2 with tag2,tag3,
     * these two end up being projected the same way.
     * Todo distinguish this: we would need to sort the edges
     * 
     * 
     * @param wform2tags
     * @param aLayer 
     */
    private void project2wforms(Map<String,LStruct> wform2tags, LLayer aLayer) {
        // add tags+emendations to w forms
        for (LDoc ldoc : aLayer.col()) {
            for (LPara lpara : ldoc.col()) {
                for (Edge edge : lpara.getEdges()) {
                    final List<WForm> wforms = new ArrayList<>(Util.getWForms(edge));

                    counter.totalEForms  += edge.getHigher().size();    
                    if (wforms.isEmpty()) {
                        // inserted form(s), i.e. form(s) without a w-layer counterpart
                        counter.inserted += edge.getHigher().size();    // todo usually ok, but there might be multiple edges leading to a single lform
                    }
                    else {
                        for (WForm wform : conf.lodzCompatibility ? wforms : Arrays.asList(wforms.get(0)) ) {
                            LStruct lstruct = wform2tags.get(wform.getId().getIdStr());
                            if (lstruct == null) {
                                lstruct = new LStruct();
                                wform2tags.put(wform.getId().getIdStr(), lstruct);
                            }
                            
                            lstruct.lforms.addAll(edge.getHigher());
                            for (Errorr err : edge.getErrors()) {
                                lstruct.errorTags.add(err.getTag());
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static List<LForm> sortByWo(final ByListSort aSorter, final Collection<LForm> aHiForms) {
        final List<LForm> forms = new ArrayList<>(aHiForms);
        aSorter.sort(forms);
        return forms;    // todo
    }
    
}
