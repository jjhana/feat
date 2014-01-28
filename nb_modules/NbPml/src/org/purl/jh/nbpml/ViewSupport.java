package org.purl.jh.nbpml;

import org.openide.loaders.DataObject;
import org.openide.windows.CloneableTopComponent;

/**
 *
 * @author jirka
 */
public interface ViewSupport {
    CloneableTopComponent getTopComponent(DataObject aDObj);
}
