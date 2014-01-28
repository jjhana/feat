package org.purl.jh.feat.iaa;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.net.jh.nbutil.io.IoUtils;
import org.purl.net.jh.nbutil.io.NbOutLogger;


/**
 * Calculates IAA on two sets of documents.
 * Docs are collected from two selected directories, 
 * considering only docs in both sets, 
 * only simple filenames are considered: i.e. if matching directoryone with directory two, 
 * one/a/xyz is matched with two/b/xyz, 
 * but if a set contains more files with the same simple name (e.g., one/a/xyz, one/b/xyz), only
 * one of them is considered (warning is reported).
 * 
 * Note: the files must have .b.xml and/or .a.xml endings, no internal inspection is performed.
 * 
 * @todo support export of collected data
 * @todo add example collection
 * @todo allow specifying format of the output (by strings, and by gui)
 * 
 * @author jirka
 */
@ActionID(category = "Tools",
id = "org.purl.jh.feat.iaa.IaaAction")
@ActionRegistration(displayName = "#CTL_IaaAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 131),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-l+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-a+xml/Actions", position = 1575),
    @ActionReference(path = "Loaders/text/feat-b+xml/Actions", position = 1575)
})
@NbBundle.Messages("CTL_IaaAction=Calculates IAA ...")
public class IaaAction extends ConsoleAction {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(org.purl.jh.feat.iaa.Project2WAction.class);    
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(IaaAction.class);
    
    private Conf conf = new Conf();
    private final Counter counter = new Counter();
    
    public IaaAction(List<DataObject> context) {
        super(context, "IAA Calculcation", "IAA Calculcation");
    }

    /**
     * Resolves shadows and removes duplicates.
     * @return 
     */
    private static List<DataObject> resolveShadows(List<DataObject> dobjs) {
        final Set<DataObject> uniqueContextSet = new HashSet<>();
        for (DataObject dobj : dobjs) {
            if (dobj instanceof DataShadow) {
                uniqueContextSet.add( ((DataShadow)dobj).getOriginal());
            }
            else {
                uniqueContextSet.add(dobj);
            }
        }
     
        return new ArrayList<>(uniqueContextSet);
    }
            
    
    // todo: remove shadows from the context (if folder x is added to favorites from within favorites, and then referenced, it is twice in context)
    private List<FileObject> getRoots() {
        final List<DataObject> uniqueContext = resolveShadows(context);

        // directly marked two items
        if (uniqueContext.size() == 2) {
            return Arrays.asList(uniqueContext.get(0).getPrimaryFile(), uniqueContext.get(1).getPrimaryFile());
        }

        // one item but is is a folder containing two items - use them
        if (uniqueContext.size() == 1) {
            FileObject rootFObj = uniqueContext.iterator().next().getPrimaryFile();
            rootFObj.refresh();      // to make sure we operate on the latest directory tree
            FileObject[] fobjs = rootFObj.getChildren();
            if (fobjs.length == 2) {
                return Arrays.asList(fobjs[0], fobjs[1]);
            }
        }
        
        return null;
    }
    
    @Override
    protected boolean beforeProcessing() {
        List<FileObject> roots = getRoots();
        if (roots == null) {
            IoUtils.println(io, "Need two directories/files to calculate IAA", NbOutLogger.finalErrColor);
            return false;
        }
        
        if (conf.getOut_exampleFile() != null) {
            counter.openExampleFile(conf.getOut_exampleFile());
        }
        
        conf.setFilter(Pattern.compile(".*\\.b\\.xml"));        // todo remember from last time
        conf.setSet1(FileUtil.toFile(roots.get(0)));
        conf.setSet2(FileUtil.toFile(roots.get(1)));

        // todo conf dialog
        final JButton ok = new JButton(bundle.getString("iaaConfDlg.OK"));
        final JButton cancel = new JButton(bundle.getString("iaaConfDlg.Cancel"));
        final IaaConfPanel p = new IaaConfPanel(ok, new Conf(conf));

        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(p, bundle.getString("iaaConfDlg.title"));
        nd.setOptions(new Object[]{ok, cancel});
        Object o = DialogDisplayer.getDefault().notify(nd);

        if (o != ok) return false;

        conf = p.getData();
        //System.out.println("Context: " + Cols.toStringNl(roots));

        return true;
    }
    
    @Override
    public void processCore() {
        
        
        processImpl();
    }

    
    public void processImpl() {
        final File root1 = conf.getSet1();
        final File root2 = conf.getSet2();

        final Map<String,FileObject> fobjsMap1 = collectFiles(FileUtil.toFileObject(root1), conf.getFilter());
        final Map<String,FileObject> fobjsMap2 = collectFiles(FileUtil.toFileObject(root2), conf.getFilter());

        // keep only files in both sets (?statistics?)
        fobjsMap1.keySet().retainAll(fobjsMap2.keySet());
        fobjsMap2.keySet().retainAll(fobjsMap1.keySet());
       
        for (Map.Entry<String,FileObject> e : fobjsMap1.entrySet()) {
            LLayerDataObject dobj1 = dobj(e.getValue());
            LLayerDataObject dobj2 = dobj(fobjsMap2.get(e.getKey()));
            
            userOutput.info("Processing %s file pair", e.getKey());
            new CalculateIaa(dobj1, dobj2, conf, counter).project();
        }
    }
    
    
    @Override
    protected void afterProcessing() {
        counter.finish();

        new Reporter(userOutput, conf, counter).print();
        
        super.afterProcessing();
    }

    
    
    
    private LLayerDataObject dobj(FileObject aFObj) {
        final String fileStr = FileUtil.getFileDisplayName(aFObj);

        LLayerDataObject dobj;
        try {
            dobj = (LLayerDataObject)DataObject.find(aFObj);
        }
        catch (DataObjectNotFoundException ex) {
            userOutput.severe(ex, "The file %s cannot be found", fileStr);
            return null;
        }
        catch (ClassCastException ex) {
            userOutput.severe(ex, "The file %s does not contain a feat a/b layer", fileStr);
            return null;
        }
        

        try {
            dobj.getData();
        } catch (Throwable ex) {
            userOutput.severe(ex, "Error loading file %s.", fileStr);
            return null;
        }
        
        return dobj;
    }

    private Map<String, FileObject> collectFiles(FileObject root, Pattern aFilter) {
        final Map<String,FileObject> map = new HashMap<>();
        
        root.refresh();      // to make sure we operate on the latest directory tree
        collectFiles(root, map, aFilter);
        
        return map;
    }
    
    private void collectFiles(FileObject aFObj, Map<String, FileObject> map, Pattern aFilter) {
        if (aFObj.isFolder()) {
            for (FileObject fo : aFObj.getChildren()) {
                collectFiles(fo, map, aFilter);
            }
        }
        else {
            if (conf.getFilter().matcher(FileUtil.toFile(aFObj).getAbsolutePath()).matches() ) {
                FileObject prev = map.put(aFObj.getName(), aFObj);
                if (prev != null) userOutput.warning("Multiple instances of %s, only one is considered.", aFObj.getName());
                aFObj.refresh(true);
            }
        }
    }
    
    /**
     * Should particular file should be included in comparison?
     */
    protected boolean isFileProcessed(FileObject aFObj) {
        return aFObj.getNameExt().endsWith(".b.xml");  // hack, todo look at its type inside
    }

    
}
