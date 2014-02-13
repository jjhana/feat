package org.purl.jh.feat.NbData.view;

import org.openide.loaders.DataObject;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jirka
 */
public interface ViewSupport {
    CloneableTopComponent getTopComponent(DataObject aDObj);
}
