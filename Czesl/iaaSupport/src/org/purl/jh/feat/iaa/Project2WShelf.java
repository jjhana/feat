///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.purl.jh.feat.iaa;
//
//import cz.cuni.utkl.czesl.data.layerl.Edge;
//import cz.cuni.utkl.czesl.data.layerl.ErrorInfo;
//import cz.cuni.utkl.czesl.data.layerl.LDoc;
//import cz.cuni.utkl.czesl.data.layerl.LLayer;
//import cz.cuni.utkl.czesl.data.layerl.LPara;
//import cz.cuni.utkl.czesl.data.layerl.Sentence;
//import cz.cuni.utkl.czesl.data.layerw.WForm;
//import java.io.IOException;
//import java.util.Collection;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.openide.filesystems.FileUtil;
//import org.purl.jh.pml.ts.Tag;
//import org.purl.jh.util.col.XCols;
//import org.purl.jh.util.io.IO;
//import org.purl.jh.util.io.XFile;
//
///**
// *
// * @author j
// */
//public class Project2WShelf {
//        // todo this is just a temporary hack - hijacking this code to produce examples for each tag
////        for (Tag tag : aLayer.getTagset().getTags()) {
////            examplesForTag(tag, aLayer);
////        }
//    
//    private void examplesForTag(Tag aTag, LLayer aLayer) {
//        final List<String> outStrs = XCols.newArrayList();
//        
//        for (LDoc ldoc : aLayer.col()) {
//            for (LPara lpara : ldoc.col()) {
//                for (Edge edge : lpara.getEdges()) {
//                    for(ErrorInfo ei : edge.getErrors()) {
//                        if (ei.getTag() == aTag) {
//                            outStrs.add( exampleForTag(edge) );
//                        }
//                    }
//                }
//            }
//        }
//
//        // write out
//        final XFile file = new XFile(FileUtil.toFile(dobj.getPrimaryFile())).addExtension(aTag.getId());
//        try {
//            IO.writeLines(file, outStrs);
//        } catch (IOException ex) {
//            Logger.getLogger(Project2W.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        
//    }
//
//    private String exampleForTag(Edge aEdge) {
//        // find w layer string (note this might be an unnary edge)
//        if (aEdge.getHigher().isEmpty()) {
//             return "todo: deleted edge, no obvious forms.";
//        }
//        else {
//            Sentence sentence = aEdge.getHigher().iterator().next().getParent();
//            final Collection<WForm> wforms = getWForms(sentence.getChildren());
//            final Collection<WForm> wrongWforms = getWForms(aEdge.getLower());
//            
//            final StringBuilder sStr = new StringBuilder();;
//            
//            for (WForm wform : wforms) {
//                if ( !isPunct(wform.getToken()) && sStr.length() > 0) {
//                    sStr.append(" ");
//                }
//
//                String str = wform.getToken();
//                if (wrongWforms.contains(wform)) {
//                    sStr.append("*").append(str).append("*");
//                }
//                else {
//                    sStr.append(str);
//                }
//            }
//            return sentence.getId().getIdStr() + "\t" + sStr.toString();
//        }
//    }    
//    
//}
