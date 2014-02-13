package org.purl.jh.feat.diffui.diff;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerx.FForm;
import java.util.Set;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.location.Location;

/**
 *
 * @author j
 */
public interface IMatching {

    Set<Location> getDifferences1();

    Set<Location> getDifferences2();

//    Dic<Edge> getEdges();
//
//    Dic<Form> getForms();
    
    boolean isInDoc1(Element el);
    
}
