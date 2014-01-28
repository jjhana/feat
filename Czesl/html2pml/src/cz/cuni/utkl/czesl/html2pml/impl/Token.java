package cz.cuni.utkl.czesl.html2pml.impl;

import com.google.common.collect.Iterables;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.html2pml.AlternativeParser;
import cz.cuni.utkl.czesl.html2pml.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.err.FormatError;
import org.purl.jh.util.sgml.SgmlTag;
import org.purl.jh.util.sgml.SgmlTags;

public class Token {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(Token.class);
    
    public final static String flagGr  = "gr";
    public final static String flagImg  = "img";
    public final static String flagDt   = "dt";
    public final static String flagPriv = "priv";
    
    /** Alternative tokens without deleted (strike-through) characters */
    List<String> finalTokens = new ArrayList<>();

    /** 
     * Token without deleted (strike-through) characters, but still containing alternatives
     * Note that formats and corrections are relative to this string
     */
    String finalAltToken;
    
    /** Token with deleted (strike-through) characters */
    String token;

    /** Token including ignored characters */
    String origToken;

    /** position relative to the whole document */
    int from;  
    
    /** length of the origToken @todo needed? */
    int len;  

    int spaceAfter = 1; // 0/1/2. (2+ -> 2), end of sentence is 2, end of para/doc is 1.
    String id;
    
    
    final List<Format> formats = XCols.newArrayList();
    final Set<SgmlTag> flags   = XCols.newHashSet();
    
    String comment;
    
    private final static Pattern imgPattern = Pattern.compile("\\<img (.*)\\>");
    
    public String getToken() {
        String str = finalTokens.get(0);
        Matcher m = imgPattern.matcher(str);
        return  m.matches() ? m.group(1) : str;
    }

    public Iterable<String> getAltTokens() {
        return Iterables.skip(finalTokens, 1);
    }

    public boolean isLi() {
        return "<li>".equals(token);
    }
    
    public boolean isDt() {
        return SgmlTags.oneWithCore(flags, "dt") != null;
    }

    public boolean isPriv() {
        return SgmlTags.oneWithCore(flags, Pattern.compile("pr|priv")) != null;
    }
    
    public FForm.Type getType() {
        if ( imgPattern.matcher(finalTokens.get(0)).matches() ) {
            return FForm.Type.img;
        }
        
        if (isDt()) {
            Err.fAssert(!isPriv(), "Token cannot be dt and priv at the same time %s", token);
            return FForm.Type.dt;    
        }

        if (isPriv()) {
            return FForm.Type.priv;    
        }
        
        return FForm.Type.normal;
    }    
//    // tod consider using a set of flags (image, dt, gr, 
//    /** Is this an image token?*/
//    boolean image;
//    /** Is this a dt type token? (token provided by the teacher, etc) */
//    boolean dt = false;
//    /** Is this token written in non-latin script (possibly partially)?*/
//    boolean gr = false;
//    /** Is this token written in non-latin script (possibly partially)?*/
//    boolean priv = false;
    
    public void addFlagCode(SgmlTag aTags) {
        flags.add(aTags); // tje tag should be standardized? Or maybe even parsed?
    }
    
   
    public void applyFormats(final Map<Format.Type, BitSet> aFormatMap) {
        //System.out.println("Format map: " + aFormatMap);
        // get local delete map
        BitSet delBits = aFormatMap.get(Format.Type.strike);
        delBits = (delBits == null) ? new BitSet() : delBits.get(from, from + len);
        finalAltToken = finalToken(delBits);
        //System.out.println("     token " + token +"'");
        //System.out.println("finalToken " + finalToken+"'");
        
        applyCleanedFormats( removeDeletions(aFormatMap, delBits) );
    }
    
    /**
     * Removes deleted parts from the token.
     * @param delBits
     * @return 
     */
    protected String finalToken(BitSet delBits) {
        if (delBits.isEmpty()) return token;

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < token.length(); i++) {
            if (!delBits.get(i)) {
                sb.append(token.charAt(i));
            }
        }
        return sb.toString();
    }

    public void finalProcessing() {
        resolveAlternatives();
    }
        
    /**
     * Localizes the format map and removes all deleted portions.
     *
     * @param aFormatMap global map of formats
     * @param aDelBits bits to delete
     * @return
     */
    private Map<Format.Type, BitSet> removeDeletions(final Map<Format.Type, BitSet> aFormatMap, final BitSet aDelBits) {
        final Map<Format.Type, BitSet> localMap = new EnumMap<>(Format.Type.class);

        for (Map.Entry<Format.Type, BitSet> format : aFormatMap.entrySet()) {
            if (format.getKey().strike()) continue;

            BitSet formatMap = format.getValue().get(from, from+len);
            localMap.put(format.getKey(), removeMarked(formatMap, aDelBits) );
        }

        return localMap;
    }

    private void applyCleanedFormats(final Map<Format.Type, BitSet> aFormatMap) {
        for (Map.Entry<Format.Type, BitSet> format : aFormatMap.entrySet()) {
            BitSet bitset = format.getValue();
            Format.Type name = format.getKey();
            int card = bitset.cardinality();
            //System.out.println("Token " + token);
            if (card == token.length()) {
                formats.add(new Format(name));
            }
            else if (card > 0) {
                //System.out.printf("Token %s - format %s  (%d)\n", token, name, card);
                int start = 0;
                int end = 0;
                for (;;) {
                    start = bitset.nextSetBit(end);
                    if (start == -1) {
                        break;
                    }
                    end = bitset.nextClearBit(start);
                    if (end == -1) {
                        end = bitset.size();
                    }
                    formats.add(new Format(name, start, end - start));
                    //System.out.printf("   Token %s - Adding format \n", token, name);
                }
            }
        }
    }

//    public String woStrikeThru() {
//        if (formats.isEmpty()) return token;
//
//        // mark all struck characters
//        final BitSet strike = new BitSet(token.length());
//        for (Format format : formats) {
//            if (format.getType().strike()) {    // the whole token is struck
//                if (format.getLen() == -1) return "";
//
//                strike.set(format.getFrom(), format.getFrom()+format.getLen());
//            }
//        }
//
//        // collect non-struck characters
//        final StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < token.length(); i++) {
//            if (!strike.get(i)) sb.append(token.charAt(i));
//        }
//        return sb.toString();
//    }


    @Override
    public String toString() {
        String spaceStr = "";
        if (spaceAfter == 0) spaceStr = " 0";
        if (spaceAfter == 2) spaceStr = " <s>";
        return String.format("%s (%d)", token, from) + spaceStr;
    }


    BitSet removeMarked(BitSet aBitSet, BitSet aMarking) {
        if (aMarking.isEmpty()) return aBitSet;

        BitSet tmp = new BitSet(aBitSet.length());

        int tmpIdx = 0;
        for (int i=0; i < aMarking.length(); i++) {
            if (!aMarking.get(i)) {
                tmp.set(i, aBitSet.get(tmpIdx));
                tmpIdx++;
            }
        }
        return tmp;
    }

    AlternativeParser altParser = new AlternativeParser();
    
    private void resolveAlternatives() {
        try {
            finalTokens = altParser.parseAlternatives(token);
        }
        catch (FormatError ex) {
            if (false) throw ex;

            Main.getErrLog().severe(ex, token);
            finalTokens = Arrays.asList("ERROR:" + token);
        }
        catch (UnsupportedOperationException e) {
            Main.getErrLog().warning("Cannot yet parse alternative string %s. Left unmodified.", token);  // todo
            finalTokens = Arrays.asList(token);
        }
    }

}
