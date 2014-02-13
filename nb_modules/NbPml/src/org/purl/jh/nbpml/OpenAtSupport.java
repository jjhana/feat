package org.purl.jh.nbpml;

import com.google.common.base.Preconditions;
import java.util.logging.Level;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.purl.jh.pml.location.AcceptingLocation;
import org.purl.jh.pml.location.Location;
import org.purl.jh.util.err.FormatError;

public class OpenAtSupport  extends OpenSupport implements OpenCookie, CloseCookie, OpenAtCookie {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(OpenAtSupport.class);

    private final Class<? extends ViewSupport> viewSupportClass;
    
    public OpenAtSupport(final MultiDataObject.Entry entry, final Class<? extends ViewSupport> viewSupportClass) {
        super(entry);
        this.viewSupportClass = viewSupportClass;
    }

    public ViewSupport getViewSupport() {
//        log.info("Default lookup ------------");
//        for (Object obj : Lookup.getDefault().lookupAll(Object.class)) {
//            log.info(obj.getClass() + " " + obj);
//        }
//        log.info("Done Default lookup ------------");
        
        return Preconditions.checkNotNull(Lookup.getDefault().lookup(viewSupportClass));
    }
    
    @Override
    public void openAt(Location aLocation) {
        System.out.println("OpenAtSupport.openAt: " + aLocation);
        // todo there might be multiple views of the dobj, some might accept loc, some not, look for AcceptingLocation
        final CloneableTopComponent viewer = openImpl();
        
        if (viewer instanceof AcceptingLocation) {
            System.out.println("AcceptingLocation");
            ((AcceptingLocation)viewer).goToLoc(aLocation);
        }
    }
    

    @Override
    public void open() {
        Mutex.EVENT.writeAccess(  // run in the awt thread
            new Runnable() {
                @Override public void run() {
                    openImpl();
                }
            }
        );
    }
    
    protected CloneableTopComponent openImpl() {
        try {
            CloneableTopComponent editor = openCloneableTopComponent();
            editor.requestActive();
            return editor;
        }
        catch(Throwable e) {
            String msg;
            if (e instanceof FormatError) {
                msg = e.getMessage();
            }
            else if (e.getCause() instanceof FormatError) {
                msg = e.getCause().getMessage();
            }
            else {
                // not really expected - 
                msg = e.toString();

            }

            msg = "Error opening file!\n" + msg + "\nSee log for details.";

            // see http://qbeukes.blogspot.com/2009/11/netbeans-platform-notifications.html
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));  

            log.log(Level.INFO, e, "User error: %s", msg);          // if I used severe, NB would consider it a real error and report it.
            return null;
        }
    }
    
    
    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        final ViewSupport viewSupport = getViewSupport();
        //if (viewSupport == null) throw;
        
        // todo use dobj's lookup directly??
        final LayerProvider layerProvider = entry.getDataObject().getNodeDelegate().getLookup().lookup(LayerProvider.class);
        layerProvider.getLayer(null);

        final CloneableTopComponent tc = viewSupport.getTopComponent(entry.getDataObject());

        tc.setDisplayName(entry.getDataObject().getName());
        tc.setToolTipText( FileUtil.getFileDisplayName(entry.getFile()) );

        // todo clean this
        final TopComponent propertiesTc = WindowManager.getDefault().findTopComponent("properties");
        if (propertiesTc != null) propertiesTc.open();
        final Mode mode = WindowManager.getDefault().findMode("properties");
        for(Mode modex : WindowManager.getDefault().getModes()) {
            log.fine("* %s %s", modex.getName(), modex);
        }
        log.fine("mode=" + mode);
        if (mode != null) mode.dockInto(propertiesTc);
        if (propertiesTc != null) propertiesTc.open();

        return tc;
    }



}
