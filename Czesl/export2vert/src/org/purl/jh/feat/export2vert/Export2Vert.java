package org.purl.jh.feat.export2vert;

import com.google.common.base.Joiner;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LDoc;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerl.LPara;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import cz.cuni.utkl.czesl.data.layerw.WDoc;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerw.WLayer;
import cz.cuni.utkl.czesl.data.layerw.WPara;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.util.DataUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.ListElement;
import org.purl.jh.pml.ts.Tag;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.MultiHashHashMap;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author jirka
 */
public class Export2Vert {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Export2Vert.class);

    private final LLayerDataObject dobj;

    private final List<String> tagset  = new ArrayList<>();
    private final List<String> fillers = new ArrayList<>();

    private final MultiMap<WForm,String> wform2tags = new MultiHashHashMap<>();
    private final MultiMap<WForm,LForm> wform2emend = new MultiHashHashMap<>();

//    public static class Counter {
//        int totalWForms = 0;
//        int totalEForms = 0;
//        int inserted = 0;
//    }
//    
//    final Counter counter;
    
    Export2Vert(LLayerDataObject dobj) {
        this.dobj = dobj;
        //this.counter = counter;
    }
    
    public void project() {
        final LLayer layer = dobj.getData();
        final WLayer wLayer = layer.getWLayer();

        project(layer, wLayer);
        
        
//        fillTags(layer.getTagset());
//        project(wLayer, layer);
//        writeOut(wLayer);
        
    }

    private void fillTags(ErrorTagset aTagset) {
        tagset.clear();       // todo ineffective !!
        fillers.clear();
        for (Tag tag : aTagset.getTags()) {
            tagset.add(tag.getId());
            fillers.add(Strings.spaces(tag.getId().length()));
        }
    }
    
    private boolean printTagNames = false;
    private boolean omitDt = false;
    private boolean omitEqualForms = false;
    private boolean omitEqualIcForms = false;
    private boolean useWFormPattern = false;
    private Pattern wFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
    private boolean useEFormPattern = false;
    private Pattern eFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
    private boolean omitMultiForms = false;
    private Joiner tagJoiner = Joiner.on('|');
    
    private void writeOut(final WLayer wLayer) {
        final XFile fileT = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("t2w");
        //final XFile fileEmend = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("e2w");

        try {
            PrintWriter w   = IO.openPrintWriter(fileT);
            //PrintWriter we  = IO.openPrintWriter(fileEmend);
            if (printTagNames) w.print("// " + tagJoiner.join(tagset) );
            
            for (WDoc wdoc : wLayer.col()) {
                for (WPara wpara : filterParas(wdoc.col())) { // todo filters names incorrectly added to transcriptions
                    for (WForm wform : wpara.col()) {       
                        if (omitDt && wform.getType().dt()) continue;
                        //counter.totalWForms++;
        
                        final Set<String> tags      = wform2tags.get(wform);
                        Collection<LForm> emends    = wform2emend.get(wform);
                        final List<LForm> emendList = sort(emends == null ? Collections.<LForm>emptyList() : XCols.newArrayList(emends));
                        
                        String emend = Cols.toString(emendList, "", "", " ", "");

                        if (omitMultiForms    && (emend.isEmpty() || Strings.cWhitespacePattern.matcher(emend).find() || Strings.cWhitespacePattern.matcher(wform.getToken()).find())) continue;        
                        if (omitEqualIcForms  && wform.getToken().equalsIgnoreCase(emend)) continue;
                        if (omitEqualForms    && wform.getToken().equals(emend)) continue;
                        if (useWFormPattern && wFormsToOmit.matcher(wform.getToken()).matches()) continue;  
                        if (useEFormPattern && eFormsToOmit.matcher(emend).matches()) continue;             
                        
                        w.println(line(wform, tags));
                        
                        
                        String wformStr = (wform.getType().dt() ? "dt:" : "") + wform.getToken();
                        //we.printf("%s\t%s\n", wformStr, emend);
                    }
                }
            }
            
            //IO.close(w, we);
        } catch (Throwable e) {
            log.severe(e, "writeOut");
        }
        //todo FileUtil.refreshFor(aDobj.getPrimaryFile().getParentFile());
        //todo FileUtil.refreshFor(aDobj.getPrimaryFile().getParentFile());
    }



    private Iterable<WPara> filterParas(List<WPara> paras) {
        if (paras.isEmpty()) return paras;
        
        WPara para = paras.get(0);
        
        int nUnderscores = 0;
        for(WForm form : para.getForms()) {
            if ("_".equals(form.getToken())) nUnderscores++;
        }
        if (nUnderscores > 1) return paras.subList(1, paras.size());
        
        return paras;
    }


    private List<LForm> sort(List<LForm> aLForms) {
        if (aLForms.size() <= 1) return aLForms;

        // todo
        Collections.sort(aLForms, lformComp);

        return aLForms;
    }

    // ineffective todo extract to util
    private final Comparator<LForm> lformComp = new Comparator<LForm>() {

        @Override
        public int compare(LForm o1, LForm o2) {
            if (o1.getParent() == o2.getParent()) {
                return this.<LForm>compareWithin(o1,o2);
            }
            // inserted dummy form
            else if (o1.getParent() == null || o2.getParent() == null) {
                return o1.hashCode() - o2.hashCode();
            }
            else {
                return this.<Sentence>compareWithin(o1.getParent().getParent().getSentences(), o1.getParent(), o2.getParent());
            }
        }

        private <X extends Element> int compareWithin(X o1, X o2) {
            return compareWithin(((ListElement)o1.getParent()).col(), o1, o2);
        }

        private <X extends Element> int compareWithin(List<X> aCol, X o1, X o2) {
            int idx1 = aCol.indexOf(o1);
            int idx2 = aCol.indexOf(o2);
            if (idx1==-1) Err.iAssert(false, "Element 1 not in its parent! (%s)\n%s", o1, Cols.toString(aCol));
            if (idx2==-1) Err.iAssert(false, "Element 2 not in its parent! (%s)\n%s", o2, Cols.toString(aCol));
            // todo ) throw new XException("Element not in its parent!");
            return  idx1 - idx2;
        }

    };



    private void project(WLayer wLayer, LLayer aLayer) {
            // collect words to project to
            for (WDoc wdoc : wLayer.col()) {
                for (WPara wpara : wdoc.col()) {
                    for (WForm wform : wpara.col()) {
                        wform2tags.addEmpty(wform);
                    }
                }
            }

            // todo project all the way to w layer
            // fill tags
            for (LDoc ldoc : aLayer.col()) {
                for (LPara lpara : ldoc.col ()) {
                    for (Edge edge : lpara.getEdges()) {
                        List<WForm> wforms = new ArrayList<>(DataUtil.getWForms(edge));

                        if (false) { // todo !!
                            for (FForm form : wforms) { // todo horrible, but usually these is just one iteration in each cycle
                                for (Errorr err : edge.getErrors()) {
                                    wform2tags.add((WForm)form, err.getTag());
                                }
                                wform2emend.addAll( (WForm)form, edge.getHigher() );
                            }
                        }
                        else {
                            //counter.totalEForms  += edge.getHigher().size();    
                            if (wforms.isEmpty()) {
                                // inserted form(s), i.e. form(s) without a w-layer counterpart
                                //counter.inserted += edge.getHigher().size();    
                            }
                            else {
                                for (Errorr err : edge.getErrors()) {
                                    wform2tags.add((WForm)wforms.get(0), err.getTag());
                                }
                                wform2emend.addAll( (WForm)wforms.get(0), edge.getHigher() );
                                
                                if (wforms.size() > 1) {
                                    for (FForm form : wforms.subList(1, wforms.size()) ) { // todo horrible, but usually these is just one iteration in each cycle
                                        wform2tags.add((WForm)form, "_");
                                        wform2emend.addAll((WForm)form, Arrays.asList(new LForm(aLayer, "", FForm.Type.normal, "_")));
                                    }
                                }
                            }
                            
                        }
                    }
                }
            }

    }

    private String line(WForm aForm, Set<String> aTags) {
        List<String> tags = aTags.stream().sorted().collect(Collectors.<String>toList());
        return String.format("%s\t%s", aForm.getToken(), tagJoiner.join(tags));
    }

//    private String formatTags(Set<String> aTags) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < tagset.size(); i++) {
//            String tag = tagset.get(i);
//            sb.append( aTags.contains(tag) ? tag : fillers.get(i) ).append(" ");
//        }
//        return sb.toString();
//    }

    private String line01(WForm aForm, Set<String> aTags) {
        return String.format("%s\t%s", aForm.getToken(), formatTags01(aTags));
    }

    private String formatTags01(Set<String> aTags) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tagset.size(); i++) {
            String tag = tagset.get(i);
            sb.append( aTags.contains(tag) ? '1' : '0'  ).append("\t");
        }
        //sb.append(" |");
        if (tagset.size() == 13) {
           addMerged(sb, aTags, 0,1);     // "incor: 1 incorInfl, 2 incorStem",
           addMerged(sb, aTags, 2,3,4);   // "fw: 3 fw,  4 fw:fwFab,  5 fw:fwNc",
           addMerged(sb, aTags, 5,6,7);   // "wbd: 6 wbd, 7 wbd:wbdPre, 8 wbd:wbdComp",
           addMerged(sb, aTags, 9,10,11); // "styl: 10 styl, 11 styl:stylColl, 12 styl:stylOther"))
        }
        else {
           addMerged(sb, aTags, 7,8);   // "miss: 8 miss:missPred, 9 miss:missObj",
           addMerged(sb, aTags, 12,13,14); // "styl: 13 styl, 14 styl:stylColl, 15 styl:stylOther"))
        }

        return sb.toString();
    }

    private void addMerged(StringBuilder aSb, Set<String> aTags, Integer ... aMergedTags) {
        boolean x = merged(aTags, aMergedTags);
        aSb.append( x ? '1' : '0'  ).append("\t");
    }

    private boolean merged(Set<String> aTags, Integer ... aMergedTags) {
        for (Integer t : aMergedTags) {
            if ( aTags.contains(tagset.get(t)) ) return true;
        }
        return false;
    }

    private void project(LLayer layer, WLayer wLayer) {
        final XFile fileT = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("t2w");

        try(PrintWriter w   = IO.openPrintWriter(fileT)) {
            for (LDoc doc : layer.col()) {
                for (LPara para : doc.col()) { 
                    WPara wpara = para.getWPara();
                    for (Sentence s : para.getSentences()) {       
                        for (LForm form : s) {
                            Collection<String> tags = new ArrayList<>();
                            Set<Edge> edges = form.getLower();
                            for (Edge e : edges) {
                                for (Errorr err : e.getErrors()) {
                                    tags.add(err.getTag());
                                }
                            }
                            String tagsStr = Joiner.on("|").join(tags);

                            // project to w-layer, sorted by wordorder
                            String wformsStr = DataUtil.getWForms(form).stream()
                                    .sorted((u,v) -> wpara.getForms().indexOf(u) - wpara.getForms().indexOf(v))
                                    .map(WForm::getFormStr)
                                    .map(str -> str.isEmpty() ? "_" : str)
                                    //(wform.getType().dt() ? "dt:" : "") + wform.getToken();
                                    .collect(Collectors.joining("|"));
                            
                            w.printf("%s\t%s\t%s\n", form.getFormStr(), tagsStr, wformsStr);
                        }
                        w.println();
                    }
                }
            }
        } catch (IOException ex) {
            log.severe(ex, "project");
        }
    }

    

    
    
}
