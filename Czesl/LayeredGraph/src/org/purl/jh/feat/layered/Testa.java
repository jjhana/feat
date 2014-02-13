package org.purl.jh.feat.layered;

import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.CloneableTopComponent;
import org.purl.jh.feat.NbData.WLayerDataObject;
import org.purl.jh.feat.NbData.view.WLayerView;
import org.purl.jh.nbpml.LayerDataObject;

/**
 *
 * @author j
 */
//@ServiceProvider(service=WLayerView.class)
public class Testa implements WLayerView {
        public CloneableTopComponent getTopComponent(DataObject aDObj) {
            return (aDObj == null || !(aDObj instanceof WLayerDataObject)) ?
                null : new org.purl.jh.feat.layered.LayeredViewTopComponent( (LayerDataObject<?>)aDObj );
        }
    }
