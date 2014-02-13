package cz.cuni.utkl.czesl.html2pml.impl;

import cz.cuni.utkl.czesl.html2pml.impl.Para.MoveInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.purl.jh.util.Pair;
import org.purl.jh.util.Pairs;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.FormatError;


/**
 * Performs moves (corrected word-order) and complete deletions (partial dels are handled by {@link Para#processDeletions()}.
 *
 * Adds info about moves to para.
 * Does not handle nested moves.
 * Moves/Deletions that result in an empty paragraph are (probably?) not handled correctly.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Mover {
    private final static String cSrcAnchor = "<src-anchor>";

    private final Para para;

    public Mover(final Para aPara) {
        para = aPara;
    }

    public void go() {
        // create matching between source and target place of the movess
        final List<Pair<CodeInfo, Token>> matches = calculateMatches();


        //System.out.println("--- Moving ---");
        for (Pair<CodeInfo, Token> moveInfo : matches) {
            move(moveInfo);
        }

        // process and remove source anchors and deletions
        processSrcAnchors();
    }

    /** Move src to preceding (non-moving) token */
    private final Map<Token,Para.MoveInfo> anchor2Info = new HashMap<Token,Para.MoveInfo>();

    private void processSrcAnchors() {
        // find tokens preceding anchors
        Token prevToken = null;
        for (Token token : para.tokens) {
            if (token.token.equals(cSrcAnchor)) {
                Para.MoveInfo info = anchor2Info.get(token);
                info.srcAnchor = (prevToken == null) ? null : prevToken;
                para.anchoredInfo.add(info);
            }
            else if (token.finalAltToken.isEmpty()) {
                Para.DelInfo info = new Para.DelInfo();
                info.orig = token.token;
                info.srcAnchor = (prevToken == null) ? null : prevToken;
                para.anchoredInfo.add(info);
            }
            else {
                prevToken = token;
            }
        }

        // remove all anchors
        for (Iterator<Token> it = para.tokens.iterator(); it.hasNext();) {
            Token token = it.next();
            if (token.token.equals(cSrcAnchor) || token.finalAltToken.isEmpty()) it.remove();
        }

    }

    private void calculateSrcAnchors(List<Pair<CodeInfo,Token>> aMatches) {
        // get all moving tokens = Union_{x in aMatches} x.first.affected
//        final Set<Token> movingTokens  = new HashSet<Token>();
//        for (CodeInfo src : Pairs.firstIt(aMatches)) {
//            movingTokens.addAll(src.affected);
//        }

        final Map<Token,CodeInfo> movingTokens  = new HashMap<Token,CodeInfo>();
        for (CodeInfo src : Pairs.firstIt(aMatches)) {
            for (Token affected : src.affected) {
                movingTokens.put(affected, src);
            }
        }


    }

    /**
     * Performs a single move.
     *
     * <ul>
     * <li>moves the tokens from the src to the target
     * <li>leaves a src-anchor behind (to be processed and
     * removed later)
     * <li>creates a partial move info,
     * <li>passes info about spacing 
     * <ul>
     *    <li> from the target code to the last moved token, and
     *    <li> from the the last moved token, to the token preceding the moved tokens
     * </ul>
     * </ul>
     *
     * @param aFromTo
     */
    protected void move(Pair<CodeInfo, Token> aFromTo) {
        CodeInfo src = aFromTo.mFirst;
        Token  first = Cols.first(src.affected);
        Token   last = Cols.last(src.affected);
        Token target = aFromTo.mSecond;


        MoveInfo moveInfo = new MoveInfo();
        moveInfo.affected = src.affected;

        // copy space properties from source to the previous element
        Token prev = Cols.prev(para.tokens, first);
        if (prev != null) {
            prev.spaceAfter = last.spaceAfter;
        }
        last.spaceAfter = target.spaceAfter;

        // insert src anchor
        final int srcidx = para.tokens.indexOf(first);
        Token tmpSrcAnchor = new Token();
        tmpSrcAnchor.token = cSrcAnchor;
        para.tokens.add(srcidx, tmpSrcAnchor);
        anchor2Info.put(tmpSrcAnchor, moveInfo);


        // do the move
        para.tokens.removeAll(src.affected);
        int newPlace = para.tokens.indexOf(target);
        para.tokens.remove(newPlace);
        para.tokens.addAll(newPlace, src.affected);
    }


    /** Matches <tr-sem..> tokens with <tr-odsud..> codes */
    private List<Pair< CodeInfo, Token>> calculateMatches() {
        final List<CodeInfo> sources = para.getMoveSrcs();
        final List<Token>    targets = para.getMoveTargets();

        return match(sources, targets);
    }


    private List<Pair<CodeInfo,Token>> match(List<CodeInfo> aSrcs, List<Token> aTargets) {
        final List<Pair<CodeInfo,Token>> matches = new ArrayList<Pair<CodeInfo,Token>>();

        for(Token target : aTargets) {
            //System.out.println("target " + target);
            CodeInfo src = removeMatchingSrc(aSrcs, target);
            matches.add(new Pair<CodeInfo,Token>(src, target));
        }
        //System.out.println("matches:" + matches);
        
        return matches;
    }




    private final int trTargetPrefixLen = "<tr-sem".length();
            // <tr-sem> or <tr-sem-#>
    private CodeInfo removeMatchingSrc(List<CodeInfo> aSrcs, Token target) {
        String srcStr = "tr-odsud" + target.token.substring(trTargetPrefixLen, target.token.length()-1);
//        System.out.println("srcStr " + srcStr);
//        System.out.println("Searching: " + Cols.toStringNl(aSrcs, "  "));
        for (Iterator<CodeInfo> it = aSrcs.iterator(); it.hasNext();) {
            CodeInfo codeInfo = it.next();
            if (codeInfo.code.equals(srcStr)) {
                //System.out.println("src " + codeInfo);
                it.remove();
                return codeInfo;
            }

        }
        throw new FormatError("Moving target %s does not have any source", target);
    }




}
