package org.purl.jh.feat.iaa;

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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.openide.filesystems.FileUtil;
import org.purl.jh.feat.NbData.LLayerDataObject;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.ListElement;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.IntInt;
import org.purl.jh.util.col.MultiHashHashMap;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.str.Strings;
import org.purl.jh.pml.ts.Tag;


/**
 *
 * @author jirka
 */
public class Project2W  {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Project2W.class);

    private final List<String> tagset    = XCols.newArrayList();
    private final List<String> fillers = XCols.newArrayList();

    private final LLayerDataObject dobj;

    private final MultiMap<WForm,String> wform2tags = MultiHashHashMap.neww();
    private final MultiMap<WForm,LForm> wform2emend = MultiHashHashMap.neww();

    public static class Counter {
        int totalWForms = 0;
        int totalEForms = 0;
        int inserted = 0;
    }
    
    final Counter counter;
    
    
    public Project2W(LLayerDataObject aDobj, Counter counter) {
        this.dobj = aDobj;
        this.counter = counter;
    }

    public void project() {
        final LLayer aLayer = dobj.getData();
        final WLayer wLayer = aLayer.getWLayer();

        fillTags(aLayer.getTagset());
        project(wLayer, aLayer);
        writeOut(wLayer);
        
    }

    private void fillTags(ErrorTagset aTagset) {
        tagset.clear();       // todo ineffective !!
        fillers.clear();
        for (Tag tag : aTagset.getTags()) {
            tagset.add(tag.getId());
            fillers.add(Strings.spaces(tag.getId().length()));
        }
//        System.out.println("tags: " + Cols.toStringNl(tags, "  "));
    }
    
    private final static List<String> puncts = Arrays.asList(".", ",", ";", "!", "?");
    private static boolean isPunct(String aStr) {
        return aStr.length() == 1 && puncts.contains(aStr);
    }

    // todo make configurable
//    private boolean omitEqualForms = true;
//    private boolean omitEqualIcForms = true;
//    private boolean useWFormPattern = true;
//    private Pattern wFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
//    private boolean useEFormPattern = true;
//    private Pattern eFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
//    private boolean omitMultiForms = true;
    
    private boolean printTagNames = true;
    private boolean omitDt = false;
    private boolean omitEqualForms = false;
    private boolean omitEqualIcForms = false;
    private boolean useWFormPattern = false;
    private Pattern wFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
    private boolean useEFormPattern = false;
    private Pattern eFormsToOmit = Pattern.compile(".*(\\<|\\>|\\{|\\}|\\|).*|\\p{Punct}");
    private boolean omitMultiForms = false;
    
    private void writeOut(final WLayer wLayer) {
        final XFile file = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("t2w");
        final XFile file01 = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("t2w01");
        final XFile fileEmend = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension("e2w");

        try {
            PrintWriter w   = IO.openPrintWriter(file);
            PrintWriter w01 = IO.openPrintWriter(file01);
            PrintWriter we  = IO.openPrintWriter(fileEmend);
            if (printTagNames) w.print("// " + Cols.toString(tagset, "", "", ", ", "") );
            
            for (WDoc wdoc : wLayer.col()) {
                for (WPara wpara : filterParas(wdoc.col())) { // todo filters names incorrectly added to transcriptions
                    for (WForm wform : wpara.col()) {       
                        if (omitDt && wform.getType().dt()) continue;
                        counter.totalWForms++;
        
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
                        w01.println(line01(wform, tags));
                        
                        
                        String wformStr = (wform.getType().dt() ? "dt:" : "") + wform.getToken();
                        we.printf("%s\t%s\n", wformStr, emend);
                    }
                }
            }
            
            IO.close(w, w01, we);
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
                                    wform2tags.add((WForm)form, err.getTag().getTagId());
                                }
                                wform2emend.addAll( (WForm)form, edge.getHigher() );
                            }
                        }
                        else {
                            counter.totalEForms  += edge.getHigher().size();    
                            if (wforms.isEmpty()) {
                                // inserted form(s), i.e. form(s) without a w-layer counterpart
                                counter.inserted += edge.getHigher().size();    
                            }
                            else {
                                for (Errorr err : edge.getErrors()) {
                                    wform2tags.add((WForm)wforms.get(0), err.getTag().getTagId());
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
        return String.format("%s\t%s", aForm.getToken(), formatTags(aTags));
    }

    private String formatTags(Set<String> aTags) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tagset.size(); i++) {
            String tag = tagset.get(i);
            sb.append( aTags.contains(tag) ? tag : fillers.get(i) ).append(" ");
        }
        return sb.toString();
    }

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

    

    
    
}
