package org.purl.jh.feat.iaa;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.purl.jh.util.CountingLogger;
import org.purl.jh.util.str.Strings;

/**
 * @todo create table object, fill it, then print it
 * @todo allow output to general stream/writer, not just a logger or maybe return strings?
 * @author j
 */
public class Reporter {
    private final CountingLogger userOutput;
    private final Conf conf;
    private final Counter counter;

    public Reporter(CountingLogger userOutput, Conf conf, Counter counter) {
        this.userOutput = userOutput;
        this.conf = conf;
        this.counter = counter;
    }
    
    public void print() {
        userOutput.info(Strings.repeatChar('=', 60));
        userOutput.info("Set1: %s", conf.getSet1());
        userOutput.info("Set2: %s", conf.getSet2());
        userOutput.info("Conf: pattern=%s, lodz=%s", conf.getFilter(), conf.lodzCompatibility);
        userOutput.info("Total wforms: %d", counter.totalWForms);
        userOutput.info("Total docs:   %d", counter.totalDocs);
        
        final Set<String> groups = Sets.newHashSet(counter.tag2groups.allValues());
        
//        // first simple tags, then groups, otherwise alphabetically
//        final Comparator<String> comp = new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                boolean o1IsGroup = groups.contains(o1);
//                boolean o2IsGroup = groups.contains(o2);
//                if (o1IsGroup && !o2IsGroup) {
//                    return 1;
//                }
//                else if (!o1IsGroup && o2IsGroup) {
//                    return -1;
//                }
//                else {
//                    return o1.compareToIgnoreCase(o2);
//                }
//            }
//        };

//        final Comparator<String> comp = new ByListSort<String>(conf.getReportTagOrder()).getComparator();
//        final Set<String> tags = new TreeSet<String>(comp);
//        tags.addAll(counter.tag2square.keySet());
        final List<String> tags = conf.getOut_tagOrder();

        printKappas(tags);
        
        userOutput.info("Confusion matrix:");
        userOutput.info(counter.confusionMatrix.toString());
        //userOutput.info("Not counted for: %d w-forms with n-m projected tags", counter.multipleTags);
        
        // COntingency tables
        if (false) {
            for (String tag : tags) {
                Square sq = counter.tag2square.get(tag);
                if (sq == null) continue;   // todo report
                String text = String.format("Tag %s - kappa=%f  (avg tags: %d)\n", tag, sq.getKappa(), (sq.sum() - sq.no_no) / 2)
                    + String.format(" %4d %4d | %4d \n", sq.no_no,  sq.yes_no, sq.sumRow1())
                    + String.format(" %4d %4d | %4d \n", sq.no_yes, sq.yes_yes, sq.sumRow2())
                    + String.format(" ---- ---- | ---- \n")
                    + String.format(" %4d %4d | %4d \n", sq.sumCol1(), sq.sumCol2(), sq.sum());
                userOutput.info(text);
            }
            userOutput.info(Strings.repeatChar('=', 60));
        }
        
    }
    
    private void printKappas(final Iterable<String> tags) {
        // first simple table
        final int maxLen = Util.maxLen(tags);
        userOutput.info("%" + maxLen +"s & %s & %s\\\\", "Tag", "kappa", "avg tags"); //, "A+B", "A only", "B only", "none"); 
        for (String tag : tags) {
            //if (counter.tag2square.get(tag) == null) continue;   // todo report
            userOutput.info(lineString(tag, maxLen, counter.tag2square.get(tag)) + "\\\\");
        }    
        

//        userOutput.info("Tag  & Kappa &  avg tags  & A+B &  A only &  B only & none & "
//                +"== Kappa &  avg tags &  A+B &  A only &  B only & none &" 
//                +"!= Kappa &  avg tags &  A+B &  A only &  B only & none \\"
//                ); 

        userOutput.info("\nTag  & Kappa &  avg tags & "
                +"== Kappa &  avg tags &" 
                +"!= Kappa &  avg tags \\\\"
                ); 
        
        for (String tag : tags) {
            //if (counter.tag2square.get(tag) == null) continue;   // todo report
            
            userOutput.info(lineString(tag, maxLen, counter.tag2square.get(tag)) 
            + lineString("", 1, counter.tag2square_emendEq.get(tag)) 
            + lineString("", 1, counter.tag2square_emendNEq.get(tag)) + "\\\\"); 
        }
    }

    private String lineString(String aLbl, int maxLen, Square sq) {
        if (sq == null) sq = new Square();
        //String formatString = "%" + maxLen + "s" + " & % 1.2f & %4d & %4d & %4d & %4d & %4d "; 
        String formatString = "%" + maxLen + "s" + " & % 1.2f & %4d ";
        if (true) formatString += "& [ %4d & %4d & %4d & %4d ]"; 
        
        return String.format(formatString, 
                aLbl,
                sq.getKappa(), 
                sq.yes_yes + (sq.yes_no + sq.no_yes ) / 2,
                sq.yes_yes,
                sq.yes_no,
                sq.no_yes,
                sq.no_no);
    }
    
    private void printSq(String aLbl, Square sq) {
        userOutput.info("%s kappa=% 1.3f  (avg tags: %4d, A&B: %4d, A only: %4d, B only: %4d, none: %4d)", 
                aLbl,
                sq.getKappa(), 
                sq.yes_yes + (sq.yes_no + sq.no_yes ) / 2,
                sq.yes_yes,
                sq.yes_no,
                sq.no_yes,
                sq.no_no
        );
    }
    
    
}
