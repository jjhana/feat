package org.purl.jh.feat.layered;

import org.openide.loaders.DataObject;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.CloneableTopComponent;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.feat.NbData.view.LLayerView;
import org.purl.jh.nbpml.LayerDataObject;

/**
 *
 * @author j
 */
@ServiceProvider(service=LLayerView.class)
public class Testb implements LLayerView {
        public CloneableTopComponent getTopComponent(DataObject aDObj) {
            return (aDObj == null || !(aDObj instanceof LLayerDataObject)) ?
                null : new org.purl.jh.feat.layered.LayeredViewTopComponent( (LayerDataObject<?>)aDObj );
        }
    }
