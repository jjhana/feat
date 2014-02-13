package org.purl.jh.feat.diffui.diff;

import com.google.common.base.Preconditions;
import org.purl.jh.pml.location.Location;
import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerx.FForm;
import org.purl.jh.feat.ea.data.layerx.FormsLayer;
import java.util.*;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;

/**
 * Matching between objects (form, edges) in two annoations and differences 
 * between the annotations.
 * 
 * @author j
 * not thread safe
 */
public class Matching implements IMatching {
    private final FormsLayer<?> layer1; 
    private final FormsLayer<?> layer2;
    
    private final Dic<FForm> forms;
    private final Dic<Edge> edges;

    private LayerEq eq;
    
    private final Set<Location> differences1 = new HashSet<>();
    private final Set<Location> differences2 = new HashSet<>();
    
    public Matching(FormsLayer<?> layer1, FormsLayer<?> layer2) {
        this.layer1 = layer1;
        this.layer2 = layer2;
        
        forms = new Dic<>();
        edges = new Dic<>();
    }
    
    /** Shallow copy */
    public Matching(Matching matching) {
        layer1 = matching.layer1;
        layer2 = matching.layer2;

        forms = matching.forms;
        edges = matching.edges;
    }

    
    public Dic<Edge> getEdges() {
        return edges;
    }

    public Dic<FForm> getForms() {
        return forms;
    }

    public FormsLayer<?> getLayer1() {
        return layer1;
    }

    public FormsLayer<?> getLayer2() {
        return layer2;
    }

    public LayerEq getEq() {
        return eq;
    }

    public void setEq(LayerEq eq) {
        this.eq = eq;
    }
    
    

    public void addDifference1(IdedElement a) {
        addDifference1(Location.of(a));
    }
    
    public void addDifference2(IdedElement a) {
        addDifference2(Location.of(a));
    }
    
    public void addDifference1(Iterable<? extends IdedElement> as) {
        for (IdedElement a : as) {
            addDifference1(Location.of(a));
        }
    }
    
    public void addDifference2(Iterable<? extends IdedElement> as) {
        for (IdedElement a : as) {
            addDifference2(Location.of(a));
        }
    }
    
    public void addDifference1(Location a) {
        differences1.add( Preconditions.checkNotNull(a) );
    }
    
    public void addDifference2(Location a) {
        differences2.add( Preconditions.checkNotNull(a) );
    }
    
    @Override
    public Set<Location> getDifferences1() {
        return differences1;
    }

    @Override
    public Set<Location> getDifferences2() {
        return differences2;
    }

    @Override
    public boolean isInDoc1(Element el) {
        return el.getLayer() == layer1;
    }

    
}
