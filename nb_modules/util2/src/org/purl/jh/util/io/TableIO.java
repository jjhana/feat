package org.purl.jh.util.io;

import java.io.IOException;
import java.io.Reader;


//def test():
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.purl.jh.util.col.ConstTable;
import org.purl.jh.util.col.Table;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.str.pp.Pp;
import org.purl.jh.util.str.Strings;
import org.purl.jh.util.str.pp.DoubleParser;

/**
 *
 * @author Administrator
 * @todo TableReader/Writer
 * @todo dif patterns 
 * @todo induce size
 */
public class TableIO {
    
    
    
    public Table<Double> readDoubleTable(Reader aR) throws IOException {
        return readTable(aR, new DoubleParser());
    }

    
    public <T> Table<T> readTable(Reader aR, Pp<T> aParser) throws IOException {
        LineReader r = new LineReader(aR);

        int noOfCol = -1;

        // -- read in lists of doubles ---
        List<T> items = new ArrayList<T>();
        for (;;) {
            if (r.readLine() != null) break; // done
        
            String line = r.getLine();
            List<String> strs = Strings.splitL(line);
            if (noOfCol == -1) noOfCol = items.size();
            
            Err.fAssert(noOfCol == items.size(), "No of col = %d, but line %d has %d items", noOfCol, r.getLineNumber(), items.size());
            
            for (String str : strs) {
                items.add( aParser.fromString(str) );
            }
        }

        return new ConstTable<T>(noOfCol, items);
    }
    
    public static void writeTable(Writer aW, Table aTbl) {
        
    }
    
}

