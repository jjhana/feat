package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * Object representing a single paragraph. It covers all layers of the resulting 
 * pml structure.
 */
public class Para {
    /** Info about removed tokens (moved or completely deleted. */
    public static class AnchoredInfo {
        /** 
         * Position of the removed tokens -- token preceding the removed token(s)
         * (e.g. source of the movement) (null if para initial)  todo BUG but it is printed for the whole doc!, put there the id of the para instead        
         */
        Token srcAnchor;
    }

    /** Info about moved tokens  */
    public static class MoveInfo extends AnchoredInfo {
        /** Moved tokens */
        List<Token> affected;
    }

    /** Info about completely deleted token  */
    public static class DelInfo extends AnchoredInfo {
        /** Deleted text */
        String orig;
    }


    /** Info about split tokens  */
    public static class SplitInfo {
        final List<Token> tokens;
        public SplitInfo(Collection<Token> aTokens) {
            tokens = new ArrayList<Token>(aTokens);
        }
        
    }


    /** Info about modified tokens (partially deleted, modified via { -> } */
    public static class ChangeInfo {
        String orig;
        final List<Token> tokens = new ArrayList<Token>();
    }

    /** The converted document containing this paragraph */
    final Doc doc;
    
    /** Index of the first character relative to the whole document */
    final int from;
    /** Index of the last character relative to the whole document */
    final int to;

    final List<Token>   tokens = new ArrayList<Token>();
    final List<CodeInfo> codes = new ArrayList<CodeInfo>();

    /**
     * Info about removed tokens (moved or completely deleted.
     * Order is important!
     */
    final List<AnchoredInfo> anchoredInfo = new ArrayList<AnchoredInfo>();
    final List<SplitInfo>      splitInfos = new ArrayList<SplitInfo>();
    final List<ChangeInfo>    changeInfos = new ArrayList<ChangeInfo>();

    /** Comments for the whole paragraph (token-comments are attached to individual tokens) */
    final List<String>    comments = new ArrayList<String>();           

    public Para(Doc doc, int from, int to) {
        this.doc = doc;
        this.from = from;
        this.to = to;
    }
    
    public String getComment() {
        return comments.isEmpty() ? null : Cols.toStringNl(comments);
    }

    public void applyFormat(final Map<Format.Type,BitSet> aFormatMap) {
        for (Token token : tokens) {
            token.applyFormats(aFormatMap);
        }
    }

    /**
     * Processes partial deletions.
     * Full deletions are handled by {@link Mover}.
     */
    public void processDeletions() {
        for (Token token : tokens) {
            String cleanedToken = token.finalAltToken;
            String orig         = token.token;

            if (!cleanedToken.equals(orig) && !cleanedToken.isEmpty()) {
                addChangeInfo(Collections.singletonList(token), orig);
                //System.out.println("del part:" + token + " <- " + orig);
            }
        }
    }

    public void finalProcessing() {
        for (Token token : tokens) {
            token.finalProcessing();
        }
    }    
    
    /**
     * Moves token comment info to individual tokens.
     * Marks dt tokens as such.
     * Other codes get info about affected tokens.
     */
    public void applyCodes() {
        for (Iterator<CodeInfo> it = codes.iterator(); it.hasNext();) {
            final CodeInfo code = it.next();

            if (code.type().co()) {
                it.remove();
                List<String> flags = code.flags();
                if (flags.isEmpty()) {
                    final Token affected = findToken(code.from);
                    if (affected == null) Err.fAssert(false, "comment must be attached to exactly one token.\n%s" + Cols.toString(tokens, "", "", " ", ""), code.toString());
                    affected.comment = code.comment;
                }
                else {
                    if (flags.contains("doc")) {
                        doc.addComment(code.comment);
                    }
                    else if (flags.contains("para")) {
                        comments.add(code.comment);
                    }
                    else {
                       // todo warning
                    }
                }
            }
// handled in scanner/para parser
//            else if(code.type().dt()) {
//                it.remove();
//                final Token affected = findToken(code.from);
//                Err.fAssert(affected != null, "dt must be attached to exactly one token.\n%s", code.toString());
//                affected.dt = true;
//            }
            else {
                code.affected = findTokens(code.from, code.len);
            }
        }
    }

    public List<CodeInfo> getMoveSrcs() {
        //System.out.println("------------- getMoveSrcs ---------------");
        final List<CodeInfo> sources = new ArrayList<CodeInfo>();

        for (CodeInfo code : codes) {
            //System.out.println("  Code: " + code);
            if (code.isMove()) {
                sources.add(code);
            }
        }

        return sources;
    }

    private final Pattern semPattern = Pattern.compile("\\<(tr\\-sem(\\-\\d+)?)\\>");

    public List<Token> getMoveTargets() {
        //System.out.println("------------- getMoveTargets ---------------");
        final List<Token> targets = new ArrayList<Token>();
        for (Token token : tokens) {
            if (semPattern.matcher(token.token).matches()) {
                //System.out.println("  sem   : " + token);
                targets.add(token);
            }
        }        
        return targets;
    }

    public void addMoveInfo(AnchoredInfo aInfo) {
        anchoredInfo.add(aInfo);
    }

    public void addSplitInfo(SplitInfo aInfo) {
        splitInfos.add(aInfo);
    }

    public void addDelInfo(List<Token> aTokens, String aOrigText) {
        final ChangeInfo info = new ChangeInfo();
        info.tokens.addAll(aTokens);
        info.orig = aOrigText;
        changeInfos.add(info);
    }

    public void addChangeInfo(List<Token> aTokens, String aOrigText) {
        final ChangeInfo info = new ChangeInfo();
        info.tokens.addAll(aTokens);
        info.orig = aOrigText;
        changeInfos.add(info);
    }


    /**
     * Token containing a particular character.
     * @param pos position relative to the start of the paragraph
     * @return a token covering the relevant text
     */
    public Token findToken(final int pos) {
        List<Token> affected = findTokens(pos, -1);
        return affected.isEmpty() ? null : affected.get(0);
    }

    /**
     * Tokens containing a particular sequence of characters
     * @param pos position relative to the start of the paragraph
     * @paragraph len number of characters in the sequence
     * @return a copy of a sublist containing tokens covering the relevant sequence
     * of characters.
     */
    public List<Token> findTokens(final int pos, final int len) {
        final int globalFrom = pos + from;
        final int globalTo   = globalFrom + (len == -1 ? 0 : len);

        // todo make more effective - use binary search
        final List<Token> affected = new ArrayList<Token>();
        for (Token token : tokens) {
            if (intersect(token, globalFrom, globalTo)) affected.add(token);
        }
        return affected;
    }




    /**
     * @param aCodeFrom position relative to the start of the paragraph
     */
    private boolean intersect(final Token token, final int aCodeFrom, final int aCodeTo) {
        final int tokenFrom = token.from;
        final int tokenTo   = token.from + token.len;

        return aCodeFrom <= tokenTo && tokenFrom <= aCodeTo;
    }
}
