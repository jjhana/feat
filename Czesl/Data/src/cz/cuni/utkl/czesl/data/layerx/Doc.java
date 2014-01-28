
package cz.cuni.utkl.czesl.data.layerx;

import java.util.List;
import org.purl.jh.pml.IdedElement;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public interface Doc extends IdedElement {

    public List<? extends Para> getParas();

}
