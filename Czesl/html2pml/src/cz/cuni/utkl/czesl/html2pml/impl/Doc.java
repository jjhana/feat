/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import org.purl.jh.util.col.Cols;

/**
 * Object representing the document, the whole result of the conversion. 
 * It covers all layers of the resulting pml structure.
 */
public class Doc {
    private final List<String> comments = new ArrayList<String>();
    private final List<Para> paras = new ArrayList<Para>();

    public void addPara(Para aPara) {
        paras.add(aPara);
    }

    public List<Para> getParas() {
        return paras;
    }
    
    public String getComment() {
        return comments.isEmpty() ? null : Cols.toStringNl(comments);
    }

    public void addComment(String aComment) {
        comments.add(aComment);
    }
    
    public void infuseFormat(final Map<Format.Type, BitSet> aFormatMap) {
        // infuse format info into paragraphs,
        for (Para para : paras) {
            para.applyFormat(aFormatMap);
            para.processDeletions();
        }
    }
    
    /** Resove alternatives, ?delete nonsense paragraphs */
    public void finalProcessing() {
        for (Para para : paras) {
            para.finalProcessing();
        }
    }
    
    
}
