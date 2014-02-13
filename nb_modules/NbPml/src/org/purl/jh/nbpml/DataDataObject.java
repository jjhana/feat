package org.purl.jh.nbpml;

import java.io.IOException;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Lookup;
import org.openide.windows.IOProvider;
import org.purl.jh.pml.Data;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;
import org.purl.jh.pml.io.DataReader;
import org.purl.jh.pml.io.DataWriter;
import org.purl.jh.pml.io.NbLoader;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.ErrorHandler;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.err.UserOut;
import org.purl.net.jh.nbutil.XDataObject;

/**
 *
 * @author jirka
 */
public abstract class DataDataObject<L extends Data<?>> extends XDataObject {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(DataDataObject.class);

    protected transient L data;

    protected UserOut userOut = null;

    private SaveCookie saveCookie = new XSaveCookie();

    public DataDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
    }

    public L getData(ErrorHandler aErr) {
        loadDataIfNeeded(aErr);
        return data;
    }

    public L getData() {
        loadDataIfNeeded(null);
        return data;
    }

    protected abstract DataReader<L> getReader();

    protected abstract DataWriter<L> getWriter();

    public UserOut getUserOut() {
        if (userOut == null) {
            userOut = new UserOut(IOProvider.getDefault().getIO("User Messages", false).getErr()); // todo use IOUtils to get the proper colors etc.
        }
        return userOut;
    }

    @Override
    public boolean isModified() {
        // no need to load data to check this, if data not loaded, then it is not modified
        return super.isModified() || (data != null && getData().isModified());
    }

    public void loadDataIfNeeded(ErrorHandler aErr) {
        if (data == null) {
            try {
                data = loadData(aErr);
                if (!data.isReadOnly()) {
                    //log.fine("loadLayer: adding save cookie to %s", data);
                    // todo, only when modified
                    //getCookieSet().assign(SaveCookie.class, saveCookie);
                }
            } catch (FormatError e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            // listen to data modifications to learn about modifications
            data.addChangeListener(new DataListener() {
                @Override
                public void handleChange(DataEvent aE) {
                    onLayerModified();
                }
            });
        }
    }

    protected L loadData(ErrorHandler aErr) throws IOException {
        final NbLoader nbLoader = Lookup.getDefault().lookup(NbLoader.class);
        Err.iAssert(nbLoader != null, "Cannot find any NbLoader");
        //userOut.info("Loading %s", getPrimaryFile());
        L tmp = getReader().read(getPrimaryFile(), nbLoader, aErr == null ? getUserOut() : aErr);
        return tmp;
    }

    public void onLayerModified() {
        setModified(data.isModified());
    }

    protected void save() throws IOException {
        Err.iAssert(data != null, "Cannot save data before loading it");
        getWriter().save(data, getPrimaryFile());
        getData().setModified(false);
    }

    @Override
    public void setModified(boolean aModified) {
        super.setModified(aModified);
        if (isModified()) {
            if ( getCookieSet().getCookie(SaveCookie.class) == null ) {
                getCookieSet().assign(SaveCookie.class, saveCookie);
            }
        } 
        else {
            getCookieSet().assign(SaveCookie.class);    // remove cookie
        }
    }

    public void setUserOut(UserOut userOut) {
        this.userOut = userOut;
    }

    private class XSaveCookie implements SaveCookie {
        
        @Override
        public void save() throws IOException {
            DataDataObject.this.save();
            setModified(false);
        }
    }
}
