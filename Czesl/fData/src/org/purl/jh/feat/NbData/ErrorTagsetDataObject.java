package org.purl.jh.feat.NbData;

import cz.cuni.utkl.czesl.data.io.ErrorTagsetReader;
import cz.cuni.utkl.czesl.data.io.ErrorTagsetWriter;
import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.purl.jh.nbpml.TagsetDataObject;
import org.purl.jh.pml.ts.io.TagsetReader;
import org.purl.jh.pml.ts.io.TagsetWriter;

public class ErrorTagsetDataObject extends TagsetDataObject<ErrorTagset> {

    public ErrorTagsetDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        System.out.println("ABCD!!!!! in ErrorTagsetDataObject");
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        //cookies.add((Node.Cookie) new ErrorTagsetOpenSupport(getPrimaryEntry()));  // todo
    }

    @Override
    protected TagsetWriter<ErrorTagset> getWriter() {
        return new ErrorTagsetWriter();
    }

    @Override
    protected TagsetReader<ErrorTag,ErrorTagset> getReader() {
        return new ErrorTagsetReader();
    }

    
}
