package org.purl.jh.speedsupport.data;

import com.google.common.base.Joiner;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFile;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileContent;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileFolder;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import org.purl.jh.feat.diffui.api.Api;
import org.purl.jh.speedsupport.Util;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.FormatError;

/**
 * Note: there is only one work-on document.
 * Note: currently we do not allow multiple documents with the same name (say two different annotations from different annotators)
 * Note: each bundle has a cmd and meta file.
 * 
 * @author j
 */
public class SBundle implements Lookup.Provider {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(SBundle.class);
    private FileObject folder;
    private boolean readOnly;
    private Properties properties;
    
    private final List<Document> documents = new ArrayList<Document>();
    private Document workOnDocument;

    public SBundle(FileObject folder) {
        log.info("Creating bundle %s", FileUtil.toFile(folder));
        this.folder = folder;
        load();     // todo lazyly?
    }

    public final Pattern getFolderPattern() {
        return Pattern.compile( String.format("(\\Q%s.\\E)(\\d+)", getName() ) );
    }
    
    // load from disk
    final private void load() {
        // there might be other folders (e.g. .svn)
        try {
            // todo use normal dataobject for meta
            final FileObject propFObj = folder.getFileObject(getName() + ".meta.xml");
            if (propFObj == null) {
                properties = new Properties();          // todo tmp
                properties.put(getName() + ".1.workOn", String.valueOf(true));
            }
            else {
                properties = Util.loadProperties(propFObj);
                readOnly = Boolean.parseBoolean(properties.getProperty("readonly"));
            }
        }
        catch(Throwable ex) {
            //userOutput.severe("Cannot load document properties");
            throw new RuntimeException("Cannot load document properties", ex);
        }
        
        documents.clear();
        final Pattern folderPattern = getFolderPattern();
        for (FileObject fobj : getFolder().getChildren()) {
            log.info("load: %s", FileUtil.toFile(fobj));
            if ( fobj.isFolder() && folderPattern.matcher(fobj.getNameExt()).matches()) {
                log.info("  load: Adding %s", FileUtil.toFile(fobj));
                Document doc = Document.load(this, fobj, properties);
                documents.add(doc);
                
                boolean isWorkOnFile = Boolean.valueOf( properties.getProperty(fobj.getNameExt() + ".workOn", null) );
                if (isWorkOnFile) {
                    Err.fAssert(workOnDocument == null, "Multiple work-on documents");
                    workOnDocument = doc;
                }
            }
        }
    }
    
    /**
     * Local base name of the bundle.
     * @return
     */
    public String getName() {
        return folder.getNameExt();
    }
    
    
    // move, copy, delete?
    public boolean isCorrupted() {
        return Util.isMarked(this);
    }
    
    /**
     * Note that this refers to the bundle as a whole, in addition, the 
     * documents have their own read-only flag.
     * @return 
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readonly) {
        this.readOnly = readonly;
    }

    public FileObject getFolder() {
        return folder;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void refresh() {
        load();
    }

    public FileObject getCmdFile() {
        return folder.getFileObject(getName() + ".cmd", "xml");
    }
    
    
    // note cmdLayer is not the usual layer (cannot use nbloader, layerprovider, etc)
    public CmdLayer getCmdLayer() {
        FileObject cmdFile = getCmdFile();
        if (cmdFile == null) return null;  // not finished downloading yet
        //Err.fAssert(cmdFile != null, "Cannot find cmd file for %s", FileUtil.toFile(getFolder()));
        try {
            final DataObject dobj = DataObject.find(cmdFile);
            final CmdLayer cmdLayer = dobj.getLookup().lookup(CmdLayer.class);
            Err.fAssert(cmdLayer != null, "cmd file %s has wrong format", FileUtil.toFile(cmdFile));
            
            return cmdLayer;
        } catch (DataObjectNotFoundException ex) {
            throw new FormatError("Cannot load cmd file %s", FileUtil.toFile(cmdFile));
        }
    }

    public Document getWorkOnDocument() {
        return workOnDocument;
    }

    public final static String cError = "error";
    public final static String cAnnotate = "normal";
    public final static String cMerge = "merge";
    
    public String getMode() {
        // temporary solution
        switch(getDocuments().size()) {
            case 0: return cError;
            case 1: return cAnnotate;
            case 2: 
            case 3: return cMerge; 
            default: return cError; 
        }
    } 

    public boolean isReady() {
        switch(getMode()) {
            case cAnnotate: return true;
            case cMerge:  return getDocuments().size() == 3;
            default: return false;
        }
    }
    
    public void prepare() {
        switch(getMode()) {
            case cMerge:  prepareForMerge();
            default: return;
        }
    }

    private void prepareForMerge() {
        // copy document 0 to 2
        // todo should not be open Util.saveBundle(bundle, true); // todo move out
        // todo ensure atomicity copy to a tmp dir then move

        log.info("prepareForMerge");
        log.info("Children:");
        log.info( Joiner.on("\n").join(folder.getChildren()) );
        log.info("Docs:");
        log.info( Joiner.on("\n").join(getDocuments()) );
        
        final Document srcDoc = getDocuments().get(0);
        final String newName = folder.getName() + "." + String.valueOf(getDocuments().size());
        log.info("Name:" + newName);
        
        
        try {
            srcDoc.getFolder().copy(getFolder(), newName, null);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        
        refresh();      // todo that should work automatically
    }
    
    /** 
     * Provides an interface to speed.
     */
    public ISynchronizedFile getSynchronizedFile() {
        // todo lock
        return new SynchronizedBundle();
    }

    @Override
    public Lookup getLookup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OpenCookie getMergeCookie() {
        return new OpenCookie() {
            @Override
            public void open() {
                if (!isReady())  prepare();
                
                Document doc1 = getDocuments().get(1);                  // todo this is stupid
                Document doc2 = getDocuments().get(2);
                
                Api.openDiff(doc1.getTopFile(), doc2.getTopFile());     // todo checks
            }
        };
    }

    
    
    
    
    private class SynchronizedBundle implements ISynchronizedFile {
        @Override
        public String getFileName() {
            return getName();
        }

        @Override
        public ISynchronizedFileContent[] getHead() {
            final CmdLayer cmdLayer  = getCmdLayer();
            final StringWriter w = new StringWriter();
            try {
                cmdLayer.getDobj().write(w);            // writes from memory, todo save first?
            } catch (IOException ex) {
                throw new RuntimeException("Error writing cmd file", ex);
            }
            
            final String cmdLayerString = w.toString();
            
            final ISynchronizedFileContent fileContent = new ISynchronizedFileContent() {
                @Override
                public String getFileExtension() {
                    return "cmd.xml";
                }

                @Override
                public int getFileType() {
                    return ISynchronizedFileContent.txtFileType;
                }

                @Override
                public String getTxtBasedFileContent() {
                    return cmdLayerString;
                }

                @Override
                public byte[] getBinFileContent() {
                    throw new UnsupportedOperationException("Not a binary file.");
                }
                
            };
                    
            return new ISynchronizedFileContent[] {fileContent};        
        }

        @Override
        public ISynchronizedFileFolder[] getFolders() {
            final List<ISynchronizedFileFolder> sdocs = new ArrayList<>();
            for (Document doc : getDocuments()) {
                sdocs.add(doc.getSynchronizedFolder());
            } 
            
            return sdocs.toArray(new ISynchronizedFileFolder[]{});
        }
        
//        String getFile2string(File file) {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
    }

}
