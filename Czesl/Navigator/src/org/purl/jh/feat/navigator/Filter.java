package org.purl.jh.feat.navigator;

import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import org.purl.jh.pml.Layer;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import org.purl.jh.feat.layered.util.ElementUtils;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.Element;

/**
 * todo precompile the filter somehow (at least make the variables final)
 * Currently all conditions are and
 * @author j
 */
@Getter
public class Filter {
    private final FilterSpec spec;

    private final boolean all;
    private final boolean hasLegRestriction;
    
    public Filter(final FilterSpec spec) {
        this.spec = spec.compile();

        hasLegRestriction = spec.edgeLMin != -1 || spec.edgeLMax != -1 || spec.edgeHMin != -1 || spec.edgeHMax != -1;
        all = !spec.withComments && !spec.incorrect && 
               spec.formPattern == null && !spec.isChanging() && !spec.isChanged() &&
              !spec.withError && !hasLegRestriction && !spec.withLinks ;
    }
    
    /**
     * 
     * @param pseudoModel input
     * @param items result to show in the navigator
     */
    public void filter(final Layer<?> aTopLayer, final List<Element> items) {
        if (!(aTopLayer instanceof FormsLayer<?>)) return;
        
        final FormsLayer<?> topLayer = (FormsLayer<?>)aTopLayer;
        
        
        for (FormsLayer<?> layer : topLayer.getLowerFormsLayersEq()) {
            if (layer.getLayerIdx() == 0 && !spec.layer0 || layer.getLayerIdx() == 1 && !spec.layer1 || layer.getLayerIdx() == 2 && !spec.layer2) continue;
   
            if (spec.forms) {
                for (FForm form : layer.getForms()) {
                    if (satisfies(form)) 
                        items.add(form);
                }
            }
            
            if (spec.edges && layer instanceof LLayer) {
                LLayer llayer = (LLayer) layer;
                
                for (LDoc ldoc : llayer.col()) {
                    for (LPara lpara : ldoc.col()) {
                        for (Edge e : lpara.getEdges()) {
                            if (satisfies(e)) items.add(e);
                        }
                    }
                }
            
            }
        }
    }
    
    private static boolean hasMatchingComment(final Commented aElement, Pattern pattern) {
        return ElementUtils.isCommented(aElement) && matches(pattern, aElement.getComment());
    }

    private static boolean matches(final Pattern aPattern, final String aStr) {
        if (aPattern == null || aPattern.pattern().isEmpty()) return true;
        
        return aPattern.matcher(aStr).matches();
    }        
    
    private boolean satisfies(final FForm aForm) {
        if (all) return true;

        if (spec.withComments) {
            if (!hasMatchingComment(aForm, spec.commentPattern)) return false;
        }

        if (spec.incorrect) {
            // todo
        }
        
        //System.out.printf("satisfies %s %s %s", spec.changing, aForm instanceof LForm, aForm.getClass());
        if (spec.changing && aForm instanceof LForm) {
            //System.out.println(aForm.getToken() + " changing: " + ((LForm)aForm).isChangingForm(true));
            if (! ((LForm)aForm).isChangingForm(spec.changingImmediately) ) return false;
        }
        

        if (! matches(spec.formPattern, aForm.getToken()) ) return false;

        return true;
    }


    private boolean hasMatchingComment(final Edge aEdge) {
        if (hasMatchingComment(aEdge, spec.commentPattern)) return true;

        for (Errorr err : aEdge.getErrors()) {
            if (hasMatchingComment(err, spec.commentPattern)) return true;
        }
        return false;
    }
    
    private boolean satisfies(final Edge aEdge) {
        if (all) return true;

        if (spec.withComments) {
            if (!hasMatchingComment(aEdge)) return false;
        }

        // todo something missing here?
        
        if (spec.withError) {
            if (spec.errorPattern == null) {
                if (aEdge.getErrors().isEmpty()) return false;
            }
            else {
                if (!matchesTag(aEdge)) return false;
            }
        }

        if (hasLegRestriction) {
            final int low = aEdge.getLower().size();
            if (low < spec.edgeLMin) return false;
            if (spec.edgeLMax != -1 && spec.edgeLMax < low) return false;
            
            final int high = aEdge.getHigher().size();
            if (high < spec.edgeHMin) return false;
            if (spec.edgeHMax != -1 && spec.edgeHMax < high) return false;
        }
        
        if (spec.withLinks) {
            if (!hasLinks(aEdge)) return false;
        }
        
        return true;
    }


    private boolean matchesTag(final Edge aEdge) {
        for (Errorr err : aEdge.getErrors()) {
            if (spec.errorPattern.matcher(err.getTag()).matches()) return true;
        }
        
        return false;
    }
    
    private boolean hasLinks(final Edge aEdge) {
        for (Errorr err : aEdge.getErrors()) {
            if (err.getLinks().size() > 0) return true;
        }
        
        return false;
    }
}
    

