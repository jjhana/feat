/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.speedsupport;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.windows.IOProvider;
import org.purl.jh.nbpml.DataDataObject;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.UserOut;
import org.purl.jh.util.io.Encoding;
import org.purl.jh.util.io.IO;
import org.purl.net.jh.nbutil.XDataObject;

/**
 * A basic DataObject infrastructure.
 *
 * @todo once stabilized move to XDataObject, without the specificity of pml layers in DataDataObject
 * @todo modified needs to
 * @author j
 */
public abstract class XxDataObject<L> extends XDataObject {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(DataDataObject.class);

    protected transient L data;

    protected UserOut userOut = null;

    // the cookie is added to the cookie set when needed
    protected SaveCookie saveCookie;

    public XxDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);

        //Create a new instance of our SaveCookie implementation:
        saveCookie = new SaveCookieImpl();
    }

    public L getData() {
        loadDataIfNeeded();
        return data;
    }

    // use getPrimaryFile(), getUserOut()
    protected abstract L read();

    public void write() {
        OutputStream o = null;
        Writer w = null;
        try {
            o = getPrimaryFile().getOutputStream();
            w = new OutputStreamWriter(o, Encoding.cUtf8.getId());
            write(w);
        }
        catch(Throwable ex) {
            getUserOut().severe(ex, "Error while writing a data object to file %s", getPrimaryFile());
        }
        finally {
            IO.close(w,o);
        }
    }

    public abstract void write(Writer w) throws IOException ;
    
//    protected abstract DataReader<L> getReader();
//
//    protected abstract DataWriter<L> getWriter();

    public UserOut getUserOut() {
        if (userOut == null) {
            userOut = new UserOut(IOProvider.getDefault().getIO("User Messages", false).getErr()); // todo use IOUtils to get the proper colors etc.
        }
        return userOut;
    }

    @Override
    public boolean isModified() {
        // no need to load data to check this, if data not loaded, then it is not modified
        return super.isModified(); // || (data != null && getData().isModified());
    }

    public void loadDataIfNeeded() {
        if (data == null) {
            try {
                data = read();
            } catch (Throwable e) {
                try {
                    this.setValid(false);
                }
                catch(PropertyVetoException e2) {
                    getUserOut().severe(e2, "Error invalidating dobj");
                }
                
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                else {
                    throw new RuntimeException(e);
                }
            }
            // listen to data modifications (todo: do I need to listen to both things??
//            data.addChangeListener(new DataListener() {
//                @Override
//                public void handleChange(DataEvent aE) {
//                    log.info("handleChange");
//                    onLayerModified();
//                }
//            });
        }
    }


//    public void onLayerModified() {
//        setModified(data.isModified());
//    }

    protected void save() throws IOException {
        Err.iAssert(data != null, "Cannot save data before loading it");
        if (log.fine()) {
            log.fine("save: data=%s, dobj=%s\n", getData(), this);
        }
        write();
        setModified(false);
    }

    @Override
    public void setModified(boolean aModified) {
        super.setModified(aModified);
        if (isModified()) {
            getCookieSet().assign(SaveCookie.class, saveCookie);
        }
        else {
            getCookieSet().assign(SaveCookie.class);
        }
    }

    public void setUserOut(UserOut userOut) {
        this.userOut = userOut;
    }

    private class SaveCookieImpl implements SaveCookie {
        @Override
        public void save() throws IOException {
            XxDataObject.this.save();
            setModified(false);
        }
    }
}
