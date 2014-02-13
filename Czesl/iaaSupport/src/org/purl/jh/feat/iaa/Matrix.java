package org.purl.jh.feat.iaa;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author j
 */
public class Matrix {
    private final Map<String, Integer> data = new HashMap<>();
    private final SortedSet<String> labels = new TreeSet<>();
    
    public int get(String rowId, String colId) {
        String id = rowId + "." + colId;
        Integer nr = data.get(id);
        return nr == null ? 0 : nr.intValue();
    }
    
    public void inc(String rowId, String colId) {
        String id = rowId + "." + colId;
        Integer nr = data.get(id);
        if (nr == null) {
            labels.add(rowId);
            data.put(id, 1);
        } else {
            data.put(id, nr + 1);
        }
    }

    @Override
    public String toString() {
        return toLatexString();
        //return toString(true, "%d", "%d", "", "\n", "&");
    }    

    public String toLatexString() {
        return toString(true, "%d", "\\textbf{%d}", "", "\\\\\n", " & ", true);
    }    
    
    
    /** calculate the size of the cell (the maximum string in values, possibly labels */
    private int maxCellSize(boolean header, String format,  List<String> printedLables) {
        // calculate the size of the cell (the maximum string in values, possibly labels 
        int max = 0;
        for (Integer val : data.values()) {
            int len = String.format(format, val.intValue()).length();
            if (len > max) max = len;
        }
        if (header) {
            for (String label : printedLables) {
                int len = label.length();
                if (len > max) max = len;
            }
        }
        return max;
    }
    
    public String toString(boolean header, String format, String diagFormat, String lineStart, String lineEnd, String sep, boolean totals) {
        List<String> printedLables;
        // hack
        if (labels.contains("incorInfl")) {
            printedLables = Arrays.asList("incorBase", "incorInfl", "wbdPre", "wbdOther", "wbdComp", "fwNc", "fwFab", "stylColl", "?");
        }
        else {
            printedLables = Arrays.asList("agr", "dep", "rflx", "lex", "neg", "ref", "sec", "stylColl", "use", "vbx", "?");
        }
        
        final int max = maxCellSize(header, format, printedLables);
        
        final StringBuilder sb = new StringBuilder(labels.size() * labels.size() * max);

        // print header row
        if (header) {
            sb.append(Strings.repeat(" ", max));
            for (String label : printedLables) {
                sb.append(sep);
                sb.append( Strings.padStart(label, max, ' ') );
            }
            if (totals) sb.append(sep).append("Total");
            sb.append(lineEnd);

            sb.append(Strings.repeat("-", max*(1+printedLables.size()) ));
            sb.append(lineEnd);
        }
        

        // print the values, possibly with row labels
        for (String row : printedLables) {
            sb.append(lineStart);

            boolean first = true;
            if (header) {
                sb.append( Strings.padStart(row, max, ' ') );
                first = false;
            }
            
            for (String col : printedLables) {
                if (!first) {
                    first = false;
                    sb.append(sep);
                }
                int val = get(row, col);
                
                String fmt = row == col ? diagFormat : format;
                String valStr = String.format(fmt, val);
                sb.append( Strings.padStart(valStr, max, ' ') );
            }
            if (totals) {
                sb.append(sep);
                int val = sumRow(row);
                String valStr = String.format(format, val);
                sb.append( Strings.padStart(valStr, max, ' ') );
            }
        sb.append(lineEnd);
        }
        
        if (totals) {
            sb.append(lineStart);

            boolean first = true;
            if (header) {
                sb.append("Total");
                first = false;
            }

            
            for (String col : printedLables) {
                if (!first) {
                    first = false;
                    sb.append(sep);
                }
                int val = sumCol(col);
                
                String valStr = String.format(format, val);
                sb.append( Strings.padStart(valStr, max, ' ') );
            }
            
        }
        
        return sb.toString();
    }
    
    public int sumCol(String col) {
        int sum = 0;
        for (String row : labels) {
            sum += get(row, col);
        }
        return sum;
    }
        
    public int sumRow(String row) {
        int sum = 0;
        for (String col : labels) {
            sum += get(row, col);
        }
        return sum;
    }
}
