
package org.purl.jh.pml;

import org.openide.filesystems.FileObject;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public abstract class NonpmlLayer extends Layer<Element> {
    protected NonpmlLayer(FileObject aFile, String aId) {
        super(aFile, aId);
    }
}
