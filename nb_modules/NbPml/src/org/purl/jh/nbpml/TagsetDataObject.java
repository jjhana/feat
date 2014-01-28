package org.purl.jh.nbpml;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.purl.jh.pml.ts.Tagset;

/**
 * An abstract dataobject for a data, subclasses support a particular data's mime type (atomic, positional, ..)
 * 
 * Save/open cookies should be added in subclasses
 * todo could open/save cookies be here?
 * todo addint listener for changes
 * @author jirka
 */
public abstract class TagsetDataObject<T extends Tagset<?>> extends DataDataObject<T> {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerDataObject.class);

    public TagsetDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        cookies.assign(TagsetProvider.class, new TagsetProvider() {
            @Override public T getTagset() {
                return TagsetDataObject.this.getData();
            }
        });
    }
}