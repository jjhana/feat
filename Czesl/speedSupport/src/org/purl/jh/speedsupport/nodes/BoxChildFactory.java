//package org.purl.jh.speedsupport.nodes;
//
//import org.purl.jh.speedsupport.nodes.BundleNode;
//import java.io.File;
//import java.util.Arrays;
//import java.util.List;
//import org.openide.awt.StatusDisplayer;
//import org.openide.filesystems.FileObject;
//import org.openide.filesystems.FileUtil;
//import org.openide.loaders.DataObject;
//import org.openide.loaders.DataObjectNotFoundException;
//import org.openide.nodes.ChildFactory;
//import org.openide.nodes.Node;
//import org.openide.nodes.PropertySupport;
//import org.openide.util.Exceptions;
//import org.openide.util.Lookup.Result;
//import org.openide.util.LookupEvent;
//import org.openide.util.LookupListener;
//import org.openide.util.Utilities;
//import org.openide.util.lookup.InstanceContent;
//import org.purl.jh.speedsupport.Util;
//
///**
// *
// * @author j
// */
//public class BoxChildFactory extends ChildFactory.Detachable<DataObject> implements LookupListener {
//    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(BoxChildFactory.class);
//    
//    private String rootPath;
//    private final boolean readOnly;
//
//    private Result<File> stringResult;
//
//    public BoxChildFactory(String rootPath, boolean aReadOnly) {
//        this.rootPath = rootPath;
//        this.readOnly = aReadOnly;
//    }
//
//
//
//    @Override
//    protected void addNotify() {
//        stringResult = Utilities.actionsGlobalContext().lookupResult(File.class);
//        stringResult.addLookupListener(this);
//    }
//
//    @Override
//    protected void removeNotify() {
//        stringResult.removeLookupListener(this);
//    }
//
//    @Override
//    protected boolean createKeys(final List list) {
//        final File root = new File(rootPath);
//        
//        if (root.exists()) {
//            final File normalizedPath = FileUtil.normalizeFile(new File(rootPath));
//            final FileObject rootFO = FileUtil.toFileObject(normalizedPath);
//            rootFO.refresh();
//            final FileObject[] fos = rootFO.getChildren();
//
//            //if (fos != null) {
//                Arrays.sort(fos, Util.cFileObjNameComparator);
//                for (FileObject fo : fos) {
//                    if ( fo.isFolder() ) {
//                        try {
//                            DataObject dobj = DataObject.find(fo);      
//                            list.add(dobj);
//                        } catch (DataObjectNotFoundException ex) {
//                            Exceptions.printStackTrace(ex);
//                        }
//  //                  }
//                    }
//                }
//            //}
//        }
//        else {
//            StatusDisplayer.getDefault().setStatusText(root + " does not exist.");
//        }
//
//        return true;
//    }
//
//
//
//    private String getDocName(FileObject aFObj) {
//        if (aFObj.getNameExt().endsWith(".xml")) {
//            final int lastDot = aFObj.getName().indexOf('.');
//            if (lastDot != -1) {
//                return aFObj.getName().substring(0, lastDot);
//            }
//        }
//        return aFObj.getName();
//    }
//
//    @Override
//    protected Node createNodeForKey(DataObject key) {
//        //InstanceContent instanceContent = new InstanceContent();
//        return new BundleNode(key, readOnly, null);
//    }
//
//    @Override
//    public void resultChanged(LookupEvent ev) {
//        log.info("Result changed");
//        Result<File> foundStrings = (Result<File>) ev.getSource();
//        if (foundStrings.allInstances().iterator().hasNext()) {
//            rootPath = foundStrings.allInstances().iterator().next().getPath();
//            System.err.println("rootPath " + rootPath);
//        }
//        refresh(true);
//    }
//
//    public void refresh() {
//        System.err.println("Called Refresh on " + (readOnly ? "outbox" : "inbox"));
//        refresh(true);
//    }
//
//    static class RoStrProp extends PropertySupport.ReadOnly<String> {
//        final String str;
//
//        public RoStrProp(String name, String displayName, String shortDescription, String aValue) {
//            super(name, String.class, displayName, shortDescription);
//            this.str = aValue;
//            setValue("suppressCustomEditor", Boolean.TRUE);
//        }
//
//        RoStrProp(String aName, String aValue) {
//            this(aName, aName, aName, aValue);
//        }
//
//        @Override
//        public String getValue() {
//            return str;
//        }
//    }
//
//}
