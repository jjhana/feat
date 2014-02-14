package cz.cuni.utkl.czesl.data.layerl;

import java.util.Arrays;
import java.util.List;
import org.purl.jh.util.err.Err;


// todo replace by two tagsets

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public enum ErrorSpecs {
    INSTANCE;

    final ErrorTagset aTagset = new ErrorTagset("http://utkl.cuni.cz/czesl/L1/2", "L1 Error Tags V2");
    final ErrorTagset bTagset = new ErrorTagset("http://utkl.cuni.cz/czesl/L2/2", "L2 Error Tags V2");;


    private void readData() {
        List<String> data = data();

        Err.fAssert(data.size() % lineLen == 0, "Wrong format len=%d\n%s", data.size(), data);
        for (int i = 0; i < data.size(); i+=lineLen) {
            List<String> oneTag = data.subList(i, i+lineLen);
            process(oneTag);
        }

        // todo incorproate into the file
        aTagset.old2new.put("incorStem", "incorBase");
        aTagset.old2new.put("incor:incorInfl", "incorInfl");
        aTagset.old2new.put("incor:incorStem", "incorBase");
        aTagset.old2new.put("incor:incorBase", "incorBase");
        aTagset.old2new.put("fw:fwFab", "fwFab");
        aTagset.old2new.put("fw:fwNc", "fwNc");
        aTagset.old2new.put("wbd:wbdComp", "wbdComp");
        aTagset.old2new.put("wbd:wbdPre", "wbdPre");
        aTagset.old2new.put("wbd:wbdOther", "wbdOther");
        aTagset.old2new.put("styl:stylColl", "stylColl");
        bTagset.old2new.put("styl:stylColl", "stylColl");
        aTagset.old2new.put("styl:stylMark", "stylMark");
        bTagset.old2new.put("styl:stylMark", "stylMark");
        aTagset.old2new.put("styl:stylOther", "stylOther");
        bTagset.old2new.put("styl:stylOther", "stylOther");

        //?
        aTagset.old2new.put("odd:oddObj", "oddObj");        //?
        aTagset.old2new.put("miss:missObj", "missObj");
        aTagset.old2new.put("miss:missPred", "missPred");
                
    }


    private String tagId(String aTag, String aSubTag) {
            if (aTag.isEmpty()) {
                return aSubTag;
            }
            else if (aSubTag.isEmpty()) {
                return aTag;
            }
            else {
                return aTag+ ":" + aSubTag;
            }
    }

    private void process(List<String> strs) {
        ErrorTag e = null;
        try {
            String tagId = tagId(strs.get(0), strs.get(1));

            final ErrorTag tag = new ErrorTag(aTagset,
                tagId,
                strs.get(5),    // comment
                Boolean.parseBoolean(strs.get(2)),      // deprecated
                "A".equalsIgnoreCase(strs.get(3)),      // auto
                strs.get(4),    // menu
                Integer.parseInt(strs.get(6)),  // edges up min
                Integer.parseInt(strs.get(7)),
                Integer.parseInt(strs.get(8)),  // edges down min
                Integer.parseInt(strs.get(9)),
                Integer.parseInt(strs.get(10)), // error links min
                Integer.parseInt(strs.get(11))
                );


            if (strs.get(12).equals("R1,R2")) {
                aTagset.add( tag );
                bTagset.add( tag );
            }
            else if (strs.get(12).equals("R1")) {
                aTagset.add( tag );
            }
            else if ( strs.get(12).equals("R2")) {
                bTagset.add( tag );
            }
            else {
                Err.fAssert(false, "Unknown layer: %s", strs);
            }
        }
        catch(Exception ex) {
            Err.fAssert(false, "Wrong format strs.len=%d, %s\n%s", strs.size(), strs, ex.toString());
            return;
        }
    }

    private ErrorSpecs() {
        readData();
    }

    public ErrorSpecs getDefault() {
        return INSTANCE;
    }

    public Iterable<ErrorTagset> getErrorSpecs() {
        return Arrays.asList(aTagset, bTagset);
    }

    public ErrorTagset getErrorSpecs(LLayer aLayer) {
        if (aLayer.getLayerIdx() == 1) {
            return aTagset;
        }
        else if (aLayer.getLayerIdx() == 2) {
            return bTagset;
        }

        return null;
    }


    private final static int lineLen = 13;

    private List<String> data() {
        return Arrays.asList(
//"Tag",	"subtag",	"deprecated",	"manual/auto",	"menu",	"description",	"min lower leg",	"max upper leg",	"min upper leg", 	"max upper leg",	"min links",	"max links",	"layers",
"incor",	"",	"true",	"M",	"incor",	"nesprávný tvar (obecná kategorie pro neanotované případy oprav)",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"incorInfl",	"",	"M",	"incor&Infl",	"nesprávná flexe",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"incorStem",	"true",	"M",	"incorStem",	"nesprávný kmen",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"incorBase",	"",	"M",	"incor&Base",	"nesprávný kmen",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"incorOther",	"",	"A",	"incorOther",	"ostatní nesprávné tvary",	"1",	"-1",	"1",	"-1",	"0",	"0",	"R1",
"fw",	"",	"true",	"M",	"fw",	"neemendovatelné slovo",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"fwFab",	"",	"M",	"fwFab",	"neemendovatelné slovo",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"",	"fwNc",	"",	"M",	"fwNc",	"slovo z jiného jazyka",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"flex",	"",	"",	"M",	"flex",	"flexe u výrazu fw",	"1",	"1",	"1",	"1",	"0",	"0",	"R1",
"wbd",	"",	"true",	"M",	"wbd",	"chybná hranice slov ",	"1",	"-1",	"1",	"-1",	"0",	"0",	"R1",
"",	"wbdPre",	"",	"M",	"wbdPre",	"prefix oddělený mezerou a předložka bez mezery ",	"1",	"2",	"1",	"2",	"0",	"0",	"R1",
"",	"wbdComp",	"",	"M",	"wbdComp",	"neoprávněně rozdělená kompozita",	"2",	"-1",	"1",	"1",	"0",	"0",	"R1",
"",	"wbdOther",	"",	"M",	"wbdOther",	"jiná chyba týkající se hranice slova",	"1",	"-1",	"1",	"-1",	"0",	"0",	"R1",
"agr",	"",	"",	"M",	"agr",	"narušení shody",	"1",	"1",	"1",	"1",	"0",	"-1",	"R2",
"dep",	"",	"",	"M",	"dep",	"chyba ve vyjádření syntaktické závislosti",	"0",	"1",	"0",	"1",	"0",	"-1",	"R2",
"ref",	"",	"",	"M",	"ref",	"chyba v zájmenném odkazu",	"1",	"1",	"1",	"1",	"0",	"1",	"R2",
"vbx",	"",	"",	"M",	"vbx",	"chyba v analytickém slovesném tvaru a složeném přísudku",	"0",	"-1",	"0",	"-1",	"0",	"1",	"R2",
"",	"cvf",	"",	"A",	"cvf",	"chyba v analytickém slovesném tvaru",	"1",	"-1",	"1",	"-1",	"0",	"1",	"R2",
"",	"mod",	"",	"A",	"mod",	"chyba v konstrukci s modálním nebo fázovým slovesem",	"1",	"-1",	"1",	"-1",	"0",	"1",	"R2",
"",	"vnp",	"",	"A",	"vnp",	"chyba ve sponově-jmenném přísudku (vč pas a rez)",	"1",	"-1",	"1",	"-1",	"0",	"1",	"R2",
"rflx",	"",	"",	"M",	"rflx",	"chyba v reflexivním výrazu",	"0",	"-1",	"0",	"-1",	"0",	"-1",	"R2",
"neg",	"",	"",	"M",	"neg",	"chyba v negaci",	"1",	"-1",	"1",	"-1",	"0",	"1",	"R2",
"odd",	"",	"",	"A",	"odd",	"nadbytečné slovo",	"1",	"1",	"0",	"0",	"0",	"0",	"R2",
"miss",	"",	"",	"A",	"miss",	"chybějící slovo",	"0",	"0",	"1",	"1",	"0",	"0",	"R2",
"wo",	"",	"",	"A",	"wo",	"chybný slovosled",	"1",	"-1",	"1",	"-1",	"0",	"0",	"R2",
"lex",	"",	"",	"M",	"lex",	"chyba v lexiku a frazeologii",	"0",	"-1",	"0",	"-1",	"0",	"1",	"R2",
"use",	"",	"",	"M",	"use",	"chyba v užití gramatické kategorie",	"1",	"-1",	"1",	"-1",	"0",	"1",	"R2",
"sec",	"",	"",	"M",	"sec",	"sekundární, \"zavlečená\" chyba",	"1",	"-1",	"1",	"-1",	"0",	"-1",	"R2",
"styl",	"",	"true",	"M",	"styl",	"obecněčeský,	knižní,	nářeční tvar",	"0",	"-1",	"0",	"-1",	"0",	"-1",	"R1,R2",
"",	"stylColl",	"",	"M",	"stylColl",	"potenciální obecněčeský tvar",	"0",	"-1",	"0",	"-1",	"0",	"-1",	"R1,R2",
"",	"stylMark",	"",	"M",	"stylMark",	"výplňkové slovo",	"1",	"-1",	"0",	"0",	"0",	"0",	"R2",
"",	"stylOther",	"",	"M",	"stylOther",	"knižní,	nářeční,	slangový ap tvar/výraz",	"0",	"-1",	"0",	"-1",	"0",	"-1",	"R1,R2",
"disr",	"",	"",	"M",	"disr",	"rozvrácená konstrukce",	"-1",	"-1",	"0",	"-1",	"0",	"0",	"R2",
"problem",	"",	"",	"M",	"problem",	"problémová chyba",	"0",	"-1",	"0",	"-1",	"0",	"-1",	"R1,R2"
                );
    }

}
