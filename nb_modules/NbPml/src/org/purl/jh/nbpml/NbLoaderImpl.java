package org.purl.jh.nbpml;

import java.io.File;
import java.util.Collection;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.purl.jh.nbpml.NbLoaderImpl.XLayerProvider;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.io.LayerReader;
import org.purl.jh.pml.io.NbLoader;
import org.purl.jh.util.err.IException;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.ErrorHandler;
import org.purl.jh.util.err.UserOut;
import org.purl.net.jh.nbutil.XDataObject;
import org.purl.jh.pml.ts.Tagset;
import org.purl.jh.pml.ts.TagsetRegistry;

/**
 * Implementation of the NbLoader for the NB platform.
 * 
 * 
 * A missing layer can be 
 * <ul>
 * <li>generated (w-layer for m-layer), 
 * <li>todo: user can be asked, 
 * <li>it can be ignored (e.g. scans)
 * </ul>
 * todo: The user should be able to somehow configure the preferences, what is done quietly and what not.
 * 
 * @author jirka
 */
@ServiceProvider(service=NbLoader.class)
public class NbLoaderImpl implements NbLoader {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(NbLoaderImpl.class);

    /**
     * Layer to be injected into DataObjects with standard support (e.g. html, image).
     */
    class XLayerProvider implements LayerProvider, Node.Cookie {
        private final Layer<?> layer;

        public XLayerProvider(Layer<?> layer) {
            this.layer = layer;
        }

        @Override
        public Layer<?> getLayer(ErrorHandler aErr) {
            return layer;
        }
    };

    /** todo experimental - reacts to error/warnings when processing the document */
    protected ErrorHandler err;


    public ErrorHandler getErr() {
        if (err == null) {
           err = new UserOut(); // todo 
        }  
        return err;
    }

    public void setErr(ErrorHandler err) {
        this.err = err;
    }

    @Override
    public Tagset<?> getTagset(LayerReader<? extends Layer<?>> curReader, String uid, String version, String desc) {
        log.info("Trying to load tagset %s", uid);
        Tagset<?> ts = null;

        // check loaded tagsets 
        ts = TagsetRegistry.getDefault().getTagset(uid, version);
        if (ts != null) return ts;
        log.info("Tagset %s not in registry", uid);

        String file = tid2file(uid);
        log.info("Trying to load from file %s", file);
        
        // check local file
        final File refFile = new File( FileUtil.toFile(curReader.getFileObject()).getParentFile(), file);
        final FileObject refFileObject = FileUtil.toFileObject(refFile);
        ///log.info("  href: %s\n refFile: %s\n  file: %s", href, refFile, refFileObject);
        if (refFileObject != null) {
            final XDataObject dobj = find(refFileObject);
            if (dobj == null) getErr().fatalError("Cannot find dobj for %s", refFileObject);
            // todo use lookup
            if ( !(dobj instanceof TagsetDataObject) ) getErr().fatalError("dobj (%s) not TagsetDataObject", dobj);
            ts = dobj.getNodeDelegate().getLookup().lookup(TagsetProvider.class).getTagset();
            
            TagsetRegistry.getDefault().addTagset(ts);
            
            return ts;
         }
        log.info("Cannot load tagset %s\nRegistry:", uid, Cols.toStringNl(TagsetRegistry.getDefault().getTagsets().entrySet()));
        

        
        // todo check std folder
        
        // todo check other places, offer the user to find it.
                
        
        // derive it (needs to read the data by curReader, and needs to know which string is a tag) - maybe return some autofilling tagset?
        
        // todo handle inconsistencies between loc file and registry
        throw new RuntimeException("Cannot find tagset.");
    }

    protected String tid2file(String uid) {
        return uid; // todo for now
    }
    
    @Override
    public Layer<?> getLayer(LayerReader<? extends Layer<?>> curReader, String id, String name, String href) {
        if (name != null && name.startsWith("tagset_")) getErr().fatalError("getLayer cannot load a tagset (name=%s)", name);
         
        final File refFile = new File( FileUtil.toFile(curReader.getFileObject().getParent()), href);
        final FileObject refFileObject = FileUtil.toFileObject(refFile);

        log.info("  href: %s\n refFile: %s\n  file: %s", href, refFile, refFileObject);

        // todo allow user to find the missing layer, allow configuring preferences
        if (refFileObject == null) {
            log.info("getLayer: href=%s", href);
            if (curReader.optionalLayer(id, name, href)) {
                return null;
            }
            else {
                Layer<?> layer = curReader.generateLayer(id, name, href);
                if (layer == null) err.severe("The file %s referenced by %s does not exist and cannot be generated",
                        href,  FileUtil.getFileDisplayName(curReader.getFileObject()));

                return layer;
            }
        }
        else  {
            return ensureLoaded(refFileObject);
        }
    }
    
    public Layer<?> ensureLoaded(FileObject fo) {
        final XDataObject dobj = find(fo);
        if (dobj == null) return null;

        log.fine("ensureLoaded.do: %s", dobj.getClass());
        // todo generalize and put back
        LayerProvider layerProvider = dobj.getLookup().lookup(LayerProvider.class);

        if (layerProvider == null) {
            final Collection<? extends Mime2Layer> provs = Lookup.getDefault().lookupAll(Mime2Layer.class);
            if (provs == null) return null;

            for (Mime2Layer mime2layer : provs) {
                log.fine("mime2layer %s", Cols.toString(provs));
                if (mime2layer.getMimeTypes().contains(fo.getMIMEType())) {
                    final Layer<?> layer = mime2layer.getLayer(dobj);   // todo id !!!

                    // inject the layer provider into the data object
                    layerProvider = new XLayerProvider( layer );
                    dobj.addToLookup(layerProvider);

                    return layer;
                }
            }

            return null;
        }


        return layerProvider.getLayer(err);
    }

    private XDataObject find(FileObject aFObj) {
        try {
            log.info("ensureLoaded.fo: %s", aFObj);
            
            return XDataObject.find(aFObj);
        }
        catch (IException e) {
            getErr().fatalError(e, "Cannot open the %s file, unrecognized format.", aFObj); // todo throw exception?
            return null;
        }
        
    }
    
}
