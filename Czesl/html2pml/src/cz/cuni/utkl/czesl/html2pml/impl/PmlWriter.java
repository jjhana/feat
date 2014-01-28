package cz.cuni.utkl.czesl.html2pml.impl;

import com.google.common.collect.Iterables;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.html2pml.impl.Para.ChangeInfo;
import cz.cuni.utkl.czesl.html2pml.impl.Para.SplitInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.text.BadLocationException;
import org.purl.jh.util.io.Encoding;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.sgml.SgmlTag;
import org.purl.jh.util.sgml.SgmlTags;
import org.purl.jh.util.xml.XmlWriter;

/**
 * Creates the w/a/b-layer files on the basis of a parsed html file.
 *
 *
 * @author Jirka Hana
 */
public class PmlWriter {
    //output something for XXX, pd, etc.

    private final XFile filePrefix;
    private final Doc doc;
    
    private final XmlWriter ww;
    private final XmlWriter wa;
    private final XmlWriter wb;

    private int paraIdx;
    /** Sentence counter used to create sentence idx */
    private int sIdx = 1;
    

    static class CXmlWriter extends XmlWriter {
        private final String idPrefix;

        public CXmlWriter(XFile aFilePrefix, String aLayerId) throws IOException {
            super(aFilePrefix.addExtension(aLayerId + ".xml"));

            //idPrefix = aFileBase.getNameOnly() + "-" + aLayerId + "-";
            idPrefix = aLayerId + "-" + aFilePrefix.getNameOnly() + "-";
        }

        @Override
        public String createId(String aIdTemplate, Object ... aPars) {
            return idPrefix + super.createId(aIdTemplate, aPars);
        }
    }


    public PmlWriter(final File aOutFilePrefix, final Doc aDoc) throws IOException {
        filePrefix = new XFile(aOutFilePrefix, Encoding.cUtf8);
        ww = new CXmlWriter(filePrefix, "w");
        wa = new CXmlWriter(filePrefix, "a");
        wb = new CXmlWriter(filePrefix, "b");
        doc = aDoc;
    }


    protected void printHead(final XmlWriter aW) {
        final String layerId =  aW == ww ? "w" : (aW==wa ? "a" : "b");

        aW.dPrint("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        aW.open(layerId + "data", "xmlns=\"http://utkl.cuni.cz/czesl/\"");

        aW.open("head");
            aW.single("schema", "href=\"%sdata_schema.xml\"", layerId);
        
            aW.open("references");
                if (aW == ww) {
                    aW.single("reffile", "id=\"d\" href=\"%s\"", filePrefix.addExtension("html").file().getName());
                    aW.single("reffile", "id=\"i\" href=\"%s\"", filePrefix.addExtension("jpg").file().getName());
                }
                else if (aW == wa) {
                    aW.single("reffile", "id=\"w\" name=\"wdata\" href=\"%s\"", ww.getFile().file().getName());
                    aW.single("reffile", "id=\"t\" name=\"tagset\" href=\"%s\"", "http://utkl.cuni.cz/czesl/T1/2");
                 }
                else if (aW == wb) {
                    aW.single("reffile", "id=\"a\" name=\"adata\" href=\"%s\"", wa.getFile().file().getName());
                    aW.single("reffile", "id=\"t\" name=\"tagset\" href=\"%s\"", "http://utkl.cuni.cz/czesl/T2/2");
                }
            aW.close("references");
        aW.close("head");
    }




    public void go() throws BadLocationException, IOException {
        try {
            printHead(ww);
            printHead(wa);
            printHead(wb);

            String wid = ww.createId("d1");
            String aid = wa.createId("d1");
            String bid = wb.createId("d1");

            ww.openIded("doc", wid);
            wa.open("doc", "id=\"%s\" lowerdoc.rf=\"w#%s\"", aid, wid );
            wb.open("doc", "id=\"%s\" lowerdoc.rf=\"a#%s\"", bid, aid );

            if (doc.getComment() != null) {
                ww.openClose("comment", doc.getComment());
            }
            
            paraIdx = 1;
            for (Para para : doc.getParas()) {
                if (!para.tokens.isEmpty()) printPara(para, paraIdx++);
            }

            if (false) printCorrections();      // todo !!!!! there are errors, some anchors are ommitted

            ww.close("doc");
            wa.close("doc");
            wb.close("doc");
            ww.close("wdata");
            wa.close("adata");
            wb.close("bdata");
        } finally {
            IO.close(ww, wa, wb);
        }
    }

    int wordsInSentence;

    protected void printPara(final Para para, final int paraIdxP) throws BadLocationException, IOException {
        final String paraIdTemplate = "d1p%d";
        sIdx = 1;
        wordsInSentence = 0;

        final Iterable<Token> tokens = para.tokens;
        
        final String wid = ww.createId(paraIdTemplate, paraIdxP);
        final String aid = wa.createId(paraIdTemplate, paraIdxP);
        final String bid = wb.createId(paraIdTemplate, paraIdxP);
        
        final boolean li = tokens.iterator().next().isLi();
        final String liStr = li ? " li=\"true\"" : "";
        
        ww.open("para", "id=\"%s\"" + liStr, wid );
        wa.open("para", "id=\"%s\" lowerpara.rf=\"w#%s\"" + liStr, aid, wid );
        wb.open("para", "id=\"%s\" lowerpara.rf=\"a#%s\"" + liStr, bid, aid );

        if (para.getComment() != null) {
            ww.openClose("comment", para.getComment());
        }
        

        // todo edges (all are simple!)
        int tokenIdx = 1;
        for (Token token : Iterables.skip(para.tokens, li ? 1 : 0)) {
            printWord(paraIdxP, para.from, tokenIdx++, token);
        }

        if (wordsInSentence > 0) wa.close("s"); // in case it was sentence end was not marked

        ww.close("para");
        wa.close("para");
        wb.close("para");
    }

    private void printWord(final int aParaIdxP, final int aParaOffset, final int aTokenIdxP, final Token aToken) {
        String finalAltToken = aToken.finalAltToken;
        if (finalAltToken.isEmpty()) return;

        String idbase = String.format("d1p%dw%d", aParaIdxP, aTokenIdxP);
        aToken.id = ww.createId(idbase);
        ww.openIded("w", aToken.id);

        ww.openClose("token", unXQuote(aToken.getToken()));        
        
        for (String alternative : aToken.getAltTokens() )  {
            ww.openClose("alt", unXQuote(alternative));        
        }
  
        if (aToken.finalTokens.size() != 1 || !aToken.finalTokens.iterator().next().equals(finalAltToken)) {
            ww.openClose("oldToken", unXQuote(finalAltToken));      
        }
        
        ww.single("original_position", "from=\"%d\" len=\"%d\"", /*aParaOffset+*/aToken.from, aToken.len);

        if (aToken.comment != null) {
            ww.openClose("comment", aToken.comment);
        }

        printFormats(aToken);
        
        FForm.Type type = aToken.getType();
        if (!type.normal()) {
            ww.openClose("type", type.name());
        }
        
        tagToFlag(aToken, "gr", "gr");
        tagToFlag(aToken, "st", "st");
        
    
        if (aToken.spaceAfter == 0) {
            ww.openClose("no_space_after", "1");
        }

        ww.close("w");

        // print to the A layer
        if (SgmlTags.oneWithCore(aToken.flags, Pattern.compile("dt")) == null && !"<li>".equals(aToken.token)) {
            // open sentence if necessary
            if (wordsInSentence == 0) {
                wa.openIdedx("s", "d1p%ds%d", aParaIdxP, sIdx++);
            }

            wa.openIdedx("w", idbase);
                wa.openClose("token", unXQuote(aToken.getToken()));
                wordsInSentence++;

                wa.openIdedx("edge", "d1p%de%d", aParaIdxP, aTokenIdxP);
                    wa.openClose("from", "w#" + aToken.id);
                wa.close("edge");
            wa.close("w");

            // close sentence when marked
            if (aToken.spaceAfter == 2) {
                wa.close("s");
                wordsInSentence = 0;
            }

        }
    }
    
    /** 
     * Removes doubling of characters used for qouting some special chars during
     * transcription ([[,{{, ..)
     * @param aStr
     * @return 
     * todo temporary, should be handled earlier during quoting/unquoting in ParaParser
     */
    private String unXQuote(String aStr) {
       return aStr
            .replaceAll("\\Q{{\\E", "{")
            .replaceAll("\\Q}}\\E", "}")
            .replaceAll("\\Q[[\\E", "[")
            .replaceAll("\\Q]]\\E", "]")
            .replaceAll("\\Q<<\\E", "<")
            .replaceAll("\\Q>>\\E", ">");
    }
    
    

    private void tagToFlag(final Token aToken, String aCoreRegex, String aFlag) {
        SgmlTag dtTag = SgmlTags.oneWithCore(aToken.flags, Pattern.compile(aCoreRegex));    // precompile?
        if (dtTag != null) {
            String attrStr = dtTag.getAttributesStr();
            ww.openClose(aFlag, attrStr.isEmpty() ? "1" : attrStr);
        }
    }

    private void printFormats(Token aToken) {
        for (Format format : aToken.formats) {
            String name = format.getType().name();
     
            if (format.getFrom() == -1) {
                ww.single("format", "name=\"%s\"", name);
            }
            else {
                ww.single("format", "name=\"%s\" from=\"%d\" len=\"%d\"", name, format.getFrom(), format.getLen());
            }
        }
    }

    protected void printCorrections() {
        int i = 1; // idcounter
        ww.open("corrections");

//        // print codes
//        i = 1;
//        for (Para para : paras) {
//            for (CodeInfo code : para.codes) {
//                ww.open("code", "name=%s", code.code);
//                for (Token token : code.affected) {
//                    ww.openClose("affected", token.id);
//                }
//                ww.close("code");
//            }
//        }

        // print moves and deletes
        i = 1;
        for (Para para : doc.getParas()) {
            for (Para.AnchoredInfo info : para.anchoredInfo) {
                if (info instanceof Para.MoveInfo) {
                    ww.openIdedx("moved", "cor-mv%d", i++);
                    if (info.srcAnchor != null) {
                        ww.openClose("from", info.srcAnchor.id);
                    }
                    printTokens(ww, "token", ((Para.MoveInfo)info).affected);

                    ww.close("moved");
                }
                else {
                    ww.openIdedx("deleted", "cor-del%d", i++);
                    if (info.srcAnchor != null) {
                        ww.openClose("from", info.srcAnchor.id);
                    }
                    ww.openClose("orig", ((Para.DelInfo)info).orig);

                    ww.close("deleted");
                }
            }
        }

        // print changes // { .. -> ...}
        i = 1;
        for (Para para : doc.getParas()) {
            for (ChangeInfo info : para.changeInfos) {
                ww.openIdedx("change", "cor-change%d", i++);
                    ww.openClose("old", info.orig);
                    printTokens(ww, "token", info.tokens);
                ww.close("change");
            }
        }

        // print splits
        i = 1;
        for (Para para : doc.getParas()) {
            for (SplitInfo split : para.splitInfos) {
                ww.openIdedx("split", "cor-split%d", i++);
                    printTokens(ww, "token", Util.sortByPos(split.tokens));
                ww.close("split");
            }
        }

        // print inserts
        i = 1;
        for (Para para : doc.getParas()) {
            for (CodeInfo code : para.codes) {
                if (! code.type().in() ) continue;

                ww.openIdedx("insert", "cor-in%d", i++);
                    printTokens(ww, "token", code.affected);
                ww.close("insert");
            }
        }


        // print teacher something
        ww.close("corrections");

    }

    private void printTokens(XmlWriter aW, String aTag, List<Token> tokens) {
        for (Token token : tokens) {
            aW.openClose(aTag, token.id);
        }
    }
}
