package org.purl.jh.speedsupport.data;

import com.google.common.base.Preconditions;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileContent;
import cz.jager.uk.mff.ufal.Feat2Speedver2.ISynchronizedFileFolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import org.apache.commons.io.FileUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.purl.jh.util.str.Strings;

/**
 * A document in a folder, the folder has the name of the document, individual 
 * files have a name consisting of a basename followed either an ending or by the 
 * layer id and ".xml". 
 * 
 * @author j
 */
public class Document {
    
    /** Loads document from disk */
    static Document load(SBundle bundle, FileObject fobj, Properties prop) {
        final String name = fobj.getNameExt();
        
        final Matcher m = bundle.getFolderPattern().matcher(name);
        Preconditions.checkArgument(m.matches());

        final int idx = Integer.parseInt( m.group(2) );
        final boolean readOnly = Boolean.valueOf(prop.getProperty(name + ".readOnly"));
        final String author    = prop.getProperty(name + ".author");
        
        Document doc = new Document(bundle, idx, name, readOnly, author);

        return doc;
    }
    
    private final SBundle bundle;
    private final int idx;
    private boolean readOnly;
    private String author;      // todo should be in the layer file

    public Document(SBundle bundle, int idx, String name, boolean readOnly, String author) {
        this.bundle = bundle;
        this.idx = idx;
        this.readOnly = readOnly;
        this.author = author;
    }

    public SBundle getBundle() {
        return bundle;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return bundle.getFolder().getNameExt() + "." + Integer.toString(idx);
    }
    
    public FileObject getFolder() {
        return bundle.getFolder().getFileObject(getName()); // todo
    }
    
    
    // todo cmd file should contain a reference, an we should collect or reffered files, but it does not
    // so we just coppy all the files with the same name
    public FileObject getTopFile() {
        // todo temporary, perform some analysis or have a header file
        for (String tail : Arrays.asList(".b.xml", ".a.xml", ".w.xml", ".html", ".jpg")) {
            for (FileObject fobj : getFolder().getChildren()) {
                if (fobj.getNameExt().endsWith(tail)) return fobj;
            }
        }
        return null;
    }

    public List<FileObject> documentFiles() throws IOException {
        // todo? refresh getParent() ???
        return Arrays.asList(getFolder().getChildren());
    }

    @Override
    public String toString() {
        if (getFolder() == null) {
            return "Null doc folder";
        }
        else {
            return FileUtil.toFile(getFolder()).getAbsolutePath();
        }
    }

    // todo, ? move outside?
    public ISynchronizedFileFolder getSynchronizedFolder() {
        return new SynchronizedDocument();
    }
    
    public class SynchronizedDocument implements ISynchronizedFileFolder {

        @Override
        public boolean isReadOnly() {
            return Document.this.isReadOnly();
        }

        @Override
        public boolean isWorkOnFile() {
            return Document.this.bundle.getWorkOnDocument() == Document.this;
        }

        @Override
        public String getAuthor() {
            return Document.this.getAuthor();
        }

        @Override
        public ISynchronizedFileContent[] getContent() {
            try {
                List<ISynchronizedFileContent> fileContents = new ArrayList<>();
                for (final FileObject fobj : Document.this.documentFiles()) {
                    // todo text vs binary
                    
                    final String fileStr = FileUtils.readFileToString(FileUtil.toFile(fobj), "utf8");
                    final ISynchronizedFileContent fileContent = new ISynchronizedFileContent() {
                        @Override
                        public String getFileExtension() {
                            // e.g. a.xml
                            return Strings.removeHead(fobj.getNameExt(), "." + Document.this.getBundle().getName());
                        }

                        @Override
                        public int getFileType() {
                            return ISynchronizedFileContent.txtFileType;
                        }

                        @Override
                        public String getTxtBasedFileContent() {
                            return fileStr;
                        }

                        @Override
                        public byte[] getBinFileContent() {
                            throw new UnsupportedOperationException("Not supported for text files.");
                        }
                    };
                    
                }
                return fileContents.toArray(new ISynchronizedFileContent[]{}) ;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        protected String getFileString(String aExt) throws IOException {
            final FileObject fobj = Document.this.getFolder().getFileObject(Document.this.getBundle().getName(), aExt);
            if (fobj == null) return null;

            return FileUtils.readFileToString(FileUtil.toFile(fobj), "utf8");
        }
        
    }

    
    
}
