package org.purl.jh.feat.iaa;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.Exceptions;
import org.purl.jh.pml.ts.Tag;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.MappingCollection;
import org.purl.jh.util.col.MultiHashHashMap;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;

/**
 * Collects IAA data across multiple documents.
 * @author j
 */
public class Counter {
    int totalDocs = 0;
    int totalWForms = 0;
    int totalEForms = 0;
    int inserted = 0;
    Map<String, Square> tag2square = null;
    Map<String, Square> tag2square_emendEq = null;
    Map<String, Square> tag2square_emendNEq = null;
    
    Matrix confusionMatrix  = new Matrix();
    int multipleTags = 0;

    // todo allow multiple groups
    final MultiMap<String, String> tag2groups = new MultiHashHashMap<>();

    private PrintWriter exampleW;
    
    
    public void initTags(boolean aIsLayerA, ErrorTagset aTagset) {
        if (tag2square == null) {
            addGroups(aIsLayerA);
            tag2square          = createTag2Square(aTagset);
            tag2square_emendEq  = createTag2Square(aTagset);
            tag2square_emendNEq = createTag2Square(aTagset);
        } else {
            // todo check that they are the same tagsets
        }
    }

    public void openExampleFile(File file) {
        try {
            exampleW = IO.openPrintWriter(new XFile(file));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private Map<String,Square> createTag2Square(ErrorTagset aTagset) {
        final Map<String,Square> tag2sq = new HashMap<>();
        for (Tag tag : aTagset.getTags()) {
            tag2sq.put(tag.getId(), new Square());
        }
        for (String groupTag : Sets.newHashSet(tag2groups.allValues())) {
            tag2sq.put(groupTag, new Square());
        }
    
        return tag2sq;
    }

    /**
     * 
     * 
     * @param wform wform the projection is relative to
     * @param aLStruct1 projected structure of the first annotation (can be empty if there is no projection)
     * @param aLStruct2 projected structure of the second annotation (can be empty if there is no projection)
     */
    public void tick(WForm wform, LStruct aLStruct1, LStruct aLStruct2) {
        totalWForms++;
        
        final Set<String> tagsA = tagsPlusGroups(aLStruct1.errorTags);
        final Set<String> tagsB = tagsPlusGroups(aLStruct2.errorTags);
        final Set<String> allTags = com.google.common.collect.Sets.union(tagsA, tagsB);
        for (String tag : allTags) {
            tick(tag2square, tag, tagsA, tagsB);
            tick(eqEmend(aLStruct1,aLStruct2) ? tag2square_emendEq : tag2square_emendNEq, tag, tagsA, tagsB);
        }
        reportExample(wform, aLStruct1, aLStruct2);
        
        if (aLStruct1.errorTags.size() <= 1 && aLStruct2.errorTags.size() <= 1) {
            String tagA = aLStruct1.errorTags.isEmpty() ? "" : aLStruct1.errorTags.iterator().next();
            String tagB = aLStruct2.errorTags.isEmpty() ? "" : aLStruct2.errorTags.iterator().next();
            confusionMatrix.inc(tagA, tagB);
        }
        else {
            final Set<String> allSimpleTags = com.google.common.collect.Sets.union(aLStruct1.errorTags, aLStruct2.errorTags);
            for (String tag : allSimpleTags) {
                confusionMatrix.inc(aLStruct1.errorTags.contains(tag) ? tag : "?", aLStruct2.errorTags.contains(tag) ? tag : "?");
            }
        }
        
    }
    
    private boolean eqEmend(LStruct aStruct1, LStruct aStruct2) {
        final List<LForm> lforms1 = aStruct1.getSortedLForms();
        final List<LForm> lforms2 = aStruct2.getSortedLForms();
       
        if (lforms1.size() != lforms2.size()) return false;
        
        for (int i = 0; i < lforms1.size(); i++) {
            final LForm lForm1 = lforms1.get(i);
            final LForm lForm2 = lforms2.get(i);
            
            if (! lForm1.getToken().equals(lForm2.getToken())) return false;
        }

        return true;
    }

    private void tick(Map<String, Square> tag2sq, String aTag, Set<String> aTagsA, Set<String> aTagsB) {
        Square sq = tag2sq.get(aTag);
        if (sq == null) {
            sq = new Square();
            tag2sq.put(aTag, sq);
        }
        sq.tick(aTagsA.contains(aTag), aTagsB.contains(aTag));
    }
    
    public Set<String> getTags() {
        return tag2square.keySet();
    }

    public void finish() {
        IO.close(exampleW);
        for (Square sq : tag2square.values()) {
            sq.setNoNo(totalWForms - sq.yes_yes - sq.yes_no - sq.no_yes);
        }
        for (Square sq : tag2square_emendEq.values()) {
            sq.setNoNo(totalWForms - sq.yes_yes - sq.yes_no - sq.no_yes);
        }
        for (Square sq : tag2square_emendNEq.values()) {
            sq.setNoNo(totalWForms - sq.yes_yes - sq.yes_no - sq.no_yes);
        }
    }

    void docStarted() {
        totalDocs++;
    }

    void docEnded() {
    }

    /** Add groups of tags */
    private void addGroups(boolean aIsLayerA) {
    // todo add combined (this is a hack, allow specifying in a gui)
        if (aIsLayerA) {
            tag2groups.add("incorInfl", "incor*");
            tag2groups.add("incorBase", "incor*");
            tag2groups.add("fw", "fw*");
            tag2groups.add("fwFab", "fw*");
            tag2groups.add("fwNc", "fw*");
            tag2groups.add("fwNc", "wbd*");
            tag2groups.add("wbdPre", "wbd*");
            tag2groups.add("wbdComp", "wbd*");
            tag2groups.add("styl", "styl*");
            tag2groups.add("stylColl", "styl*");
            tag2groups.add("stylOther", "styl*");
        }
        else {
            tag2groups.add("missPred", "miss*");
            tag2groups.add("missObj", "miss*");
            tag2groups.add("styl", "styl*");
            tag2groups.add("stylColl", "styl*");
            tag2groups.add("stylOther", "styl*");
        }
        
    }

    /**
     * Extend a set of tags with all appropriate groups (i.e. groups
     * containing any of the tags in the set).
     *
     * @param aTags set of tags (the set is not modified)
     * @return combined set
     */
    private Set<String> tagsPlusGroups(Set<String> aTags) {
        final Set<String> tags = new HashSet<>();
        tags.addAll(aTags);
        for (String tag : aTags) {
            if (tag2groups.containsKey(tag)) {
                tags.addAll(tag2groups.get(tag));
            }
        }
        return tags;
    }


    private <F extends FForm> String toText(Collection<F> aForms) {
        Collection<String> tokens = new MappingCollection<>(aForms, FForm.cForm2Token);
        return Cols.toString(tokens, "", "", " ", ""); 
    }
    
    private String toSentence(LStruct aLStruct) {
        if (aLStruct.lforms.isEmpty()) {
            return "";
        }
        else {
            return toText(aLStruct.lforms.iterator().next().getParent().col());
        }
    }

    private String toWSentence(LStruct aLStruct) {
        if (aLStruct.lforms.isEmpty()) {
            return "";
        }
        else {
            Sentence lsent = aLStruct.lforms.iterator().next().getParent();
            return toText( Util.getWForms(lsent.col()) );
        }
    }
    
    private final static Joiner tagJoiner = Joiner.on(":");
    
    
    
    
    private void reportExample(WForm wform, LStruct aLStruct1, LStruct aLStruct2) {
        final Set<String> allTags = com.google.common.collect.Sets.union(aLStruct1.errorTags, aLStruct2.errorTags);
//        final Set<String> extra1 =  com.google.common.collect.Sets.difference(aLStruct1.errorTags, aLStruct2.errorTags);
//        final Set<String> extra2 =  com.google.common.collect.Sets.difference(aLStruct2.errorTags, aLStruct1.errorTags);
        
        for (String tag : allTags) {
            if (aLStruct1.errorTags.contains(tag) == aLStruct2.errorTags.contains(tag))  continue;
                        
            
            String pref = String.format("%s %s %s", tag, wform.getId().getIdStr(), wform.getToken());
            
            
            if (exampleW != null) {
                exampleW.printf("%s - %s / %s - %s / %s\n", pref, 
                        tagJoiner.join(aLStruct1.errorTags), tagJoiner.join(aLStruct2.errorTags),
                        toText(aLStruct1.lforms), toText(aLStruct2.lforms));

                exampleW.printf("%s.0 - %s \n", pref, toWSentence(aLStruct1.lforms.isEmpty() ? aLStruct2 : aLStruct1)); 
                exampleW.printf("%s.1 - %s \n", pref, toSentence(aLStruct1));
                exampleW.printf("%s.2 - %s \n", pref, toSentence(aLStruct2));
            }            
        }
//        for (String tag : allTags{
//            if (!aTagsA.contains(aTag) != aTagsB.contains(aTag));
//
//            tick(tag2square, tag, tagsA, tagsB);
//            tick(eqEmend(aLStruct1,aLStruct2) ? tag2square_emendEq : tag2square_emendNEq, tag, tagsA, tagsB);
//            reportExample(wform, aLStruct1, aLStruct2);
//        }
    }

    
}
