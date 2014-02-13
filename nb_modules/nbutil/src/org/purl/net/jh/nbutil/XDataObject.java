package org.purl.net.jh.nbutil;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.purl.jh.util.err.IException;

/**
 * Data object with cookie injection (plus basic overridable functions).
 * @todo merge with XxDataObject
 * @author jirka
 */
public class XDataObject extends MultiDataObject {

    public static XDataObject find(FileObject fo) {
        try {
            return (XDataObject)DataObject.find(fo);
        }
        catch(DataObjectNotFoundException e) {
            throw new IException(e, "Cannot find dataobject");
        }
        catch(ClassCastException e) {
            throw new IException(e, "Dataobject is not XDataObject (fileobj=%s, mime=%s)", fo, fo.getMIMEType());
        }
    }

    protected final Lookup lookup;    
    protected final InstanceContent lookupContent = new InstanceContent();    
    
    public XDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo,loader);
        
        lookup = new ProxyLookup( new AbstractLookup(lookupContent), getCookieSet().getLookup() );
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    /**
     * Lookup including cookie-set and custom lookup.
     * @return
     */
    public @Override Lookup getLookup() {
        return lookup;
    }

    public void addToLookup(Object obj) {
        lookupContent.add(obj);
    }

    public void removeFromLookup(Object obj) {
        lookupContent.remove(obj);
    }
}
