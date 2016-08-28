package org.purl.jh.feat.export2vert;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.openide.filesystems.FileUtil;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.feat.util0.ByListSort;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;

/**
 *
 * @author jirka
 */
public class ExportA2Vert {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ExportA2Vert.class);

    private final LLayerDataObject dobj;

//    private final MultiMap<WForm,LForm> wform2emend = new MultiHashHashMap<>();
    
    ExportA2Vert(LLayerDataObject dobj) {
        this.dobj = dobj;
        //this.counter = counter;
    }
    
    public void project() {
        final LLayer layer = dobj.getData();
        if (layer.getLayerIdx() != 1) return;       // Exporting only Layer A (Tier 1), not Layer B (Tier 2)
        
        final WLayer wLayer = layer.getWLayer();
        
        project(layer, wLayer);
    }


    /**
     * We assume there are only three types of edges:
     * <ul>
     * <li>one-to-one
     * <li>splitting
     * <li>merging
     * </ul>
     * In theory, there could be:
     * <ul>
     * <li>insertions
     * <li>deletions
     * <li>splitting mixed with merging
     * </ul>
     * We ignore those. todo detect and issue warning
     * 
     * @param alayer
     * @param wLayer 
     */
    private void project(LLayer alayer, WLayer wLayer) {
        final XFile fileT = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("t2w");

        try(PrintWriter w   = IO.openPrintWriter(fileT)) {
            for (LDoc doc : alayer.col()) {
                for (LPara para : doc.col()) { 
                    WPara wpara = para.getWPara();
                    ByListSort<FForm> wLayerSorter = new ByListSort<>(wpara.getFormsList());
                    
                    for (LForm form : para.getForms()) {
                        Set<Edge> edges = form.getLower();

                        Multimap<Edge.Type,Edge> type2edges = ArrayListMultimap.create();
                        for (Edge e : edges) {
                            type2edges.put(e.getType(), e);
                        }
                                
                        if (type2edges.containsKey(Edge.Type.MERGE)) { // multiple on T0, one on T1; each form on T0 can have simple edge to T1
                            // Error check: only one Merge and splits
                            // TODO might be combined with simple -> extend tagString
                            if (edges.size() > 1) warning(w, form, "MERGE+ Not supported yet");
                            check(w, type2edges, Edge.Type.MERGE, 1);
                            check(w, type2edges, Edge.Type.SPLIT, 0);
                            check(w, type2edges, Edge.Type.INSERT, 0);
                            check(w, type2edges, Edge.Type.DELETE, 0);

                            Edge mergeEdge = type2edges.get(Edge.Type.MERGE).iterator().next();

                            List<FForm> lowerFormsList = new ArrayList<>((Set<FForm>)mergeEdge.getLower());
                            wLayerSorter.sort(lowerFormsList);
                            String tagString = tagString(mergeEdge);

                            for (int i = 0; i < lowerFormsList.size(); i++) {
                                FForm lowerForm = lowerFormsList.get(i);
                                // todo simple edges
                                if (i == 0) {
                                    printLine(w, lowerForm.getFormStr(), form.getFormStr(), tagString);
                                }
                                else {
                                    printLine(w, lowerForm.getFormStr(), "^", "^" + tagString);
                                }
                            }
                        }
                        else if (type2edges.containsKey(Edge.Type.SPLIT)) { // one on T0, multiple on T1
                            if (edges.size() > 1) warning(w, form, "SPLIT+ Not supported yet");
                            // Error: edges.size() = 1 || (edges.size() == 2 && the other 
                            // Max one SIMPLE

                            Edge e = edges.iterator().next();
                            // print the first T1 form
                            if (e.getOneHigher() == form) {
                                printLine(w, e.getOneLower().getFormStr(), form.getFormStr(), tagString(e));
                                break;
                            }
                            // print a later T1 forms
                            else {
                                printLine(w, "^", form.getFormStr(), "^");
                                break;
                            }
                            
                        }
                        else if (type2edges.containsKey(Edge.Type.SIMPLE)) {
                            if (edges.size() != 1) warning(w, form, "SIMPLE+ is wrong");
                            
                            Edge e = edges.iterator().next();
                            printLine(w, e.getOneLower().getFormStr(), form.getFormStr(), tagString(e));
                        }
                        else if (type2edges.containsKey(Edge.Type.INSERT)) {
                            if (edges.size() > 1) error(w, form, "INSERT cannot have any other edges attached");

                            Edge e = edges.iterator().next();
                            printLine(w, "0", form.getFormStr(), tagString(e));
                        }
                        else if (type2edges.containsKey(Edge.Type.DELETE)) {
                            if (edges.size() > 1) error(w, form, "DELETE cannot have any other edges attached");
                            // todo how to retrieve
                            
                            Edge e = edges.iterator().next();
                            printLine(w, "DELETE-TODO", form.getFormStr(), tagString(e));
                        }
                    }
                }
            }
//                        EdgesType edgesType = edgesType(edges);
//                        // todo check there are no crossing branches
//                        // todo check the higher and lower forms are continuous
//                        // todo check there are no word-order changes (collect, then check)
//                        if (edgesType == EdgesType.OTHER) {
//                            String str1 = "ERROR: " + edgesType.name() + " " + edges.size();
//                            for (Edge e : edges) {
//                                str1 += " " + e.getType();
//                                str1 += " " + tagString(e);
//                            }
//                            
//                            printLine(w, str1, form.getFormStr(), "");
//                            continue;
//                        }
//
//                        Edge e = edges.iterator().next();
//                        
//                        switch (edgesType) {
//                            case SIMPLE: 
//                                printLine(w, e.getOneLower().getFormStr(), form.getFormStr(), tagString(e));
//                                break;
//                            case MERGE: // + opt: SIMPLE(s)
//                                List<FForm> lowerFormsList = new ArrayList<>((Set<FForm>)e.getLower());
//                                wLayerSorter.sort(lowerFormsList);
//                                String tagString = tagString(e);
//                                // TODO might be combined with simple -> extend tagString
//                                
//                                for (int i = 0; i < lowerFormsList.size(); i++) {
//                                    FForm lowerForm = lowerFormsList.get(i);
//                                    if (i == 0) {
//                                        printLine(w, lowerForm.getFormStr(), form.getFormStr(), tagString);
//                                    }
//                                    else {
//                                        printLine(w, lowerForm.getFormStr(), "^", "^" + tagString);
//                                    }
//                                }
//                                break;
//                            case SPLIT: // + opt: SIMPLE
//                                // TODO might be combined with simple -> extend tagString
//                                // print the first T1 form
//                                if (e.getOneHigher() == form) {
//                                    printLine(w, e.getOneLower().getFormStr(), form.getFormStr(), tagString(e));
//                                    break;
//                                }
//                                // print a later T1 forms
//                                else {
//                                    printLine(w, "^", form.getFormStr(), "^");
//                                    break;
//                                }
//                            default:
//                                String str1 = "ERROR: B" + edgesType.name();
//                                str1 += e.getLower().size() + ":" + e.getHigher().size();
//
//                                printLine(w, str1, form.getFormStr(), "");
//                        }
//                    }
//                    w.println();
//                }
//            }
        } catch (IOException ex) {
            log.severe(ex, "project");
        }
    }

    private void printLine(PrintWriter w, String lower, String higher, String tags) {
        w.printf("%s\t%s\t%s\n", lower, higher, tags);
    }

    private void warning(PrintWriter w, LForm form, String string) {
            w.printf("TODO WARNING: " + string);
    }

    private void error(PrintWriter w, LForm form, String string) {
            w.printf("TODO ERROR: " + string);
    }

    private void check(PrintWriter w, Multimap<Edge.Type, Edge> type2edges, Edge.Type type, int i) {
        if (type2edges.get(type).size() > i) {
            w.printf("TODO ERROR: only %d edged of type %s are possible\n", i, type.name());
        }
    }
    
    enum EdgesType {
        SIMPLE,
        SPLIT,
        MERGE,
        INSERT,
        DELETE,
        OTHER
    }
    
    private EdgesType edgesType(Collection<Edge> edges) {
        if (edges.size() != 1) return EdgesType.OTHER;
        final Edge e = edges.iterator().next();
        
        for (FForm form : e.getLower()) {
            if (form.getHigher().size() > 1) return EdgesType.OTHER;
        }
        
        if (e.isSimple()) {
            return EdgesType.SIMPLE;
        }
        if (e.isInsert()) {
            return EdgesType.INSERT;
        }
        if (e.isDelete()) {
            return EdgesType.DELETE;
        }
        if (e.getHigher().size() > 1) {
            return EdgesType.SPLIT;
        }
        if (e.getLower().size() > 1) {
            return EdgesType.MERGE;
        }
        throw Err.iErr();
    }

    private String tagString(Edge e) {
        return e.getErrors().stream()
                .map(err -> err.getTag())
                .collect(Collectors.joining("|"));
    }
    
}
