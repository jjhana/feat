package org.purl.jh.util.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.LineReader;
import org.purl.jh.util.io.XFile;
import org.purl.jh.util.str.StringPair;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author Jirka Hana
 */
public /*abstract*/ class EzReader {
    String commentStr;
    XFile file;

    // Splitting todo encapsulate
    Pattern splitting_pattern;
    int splitting_parts;
    String splitting_errMsg;

    LineReader r;

//    public EzReader(Reader aR) {
//
//    }

//    public EzReader(String aStr) {
//        this(new StringReader(aStr));
//    }

    public EzReader(XFile aFile) {
        file = aFile;
    }


    public EzReader configureSplitting(String ... aNames) {
        String msg = "Required format:" + Cols.toString( Arrays.asList(aNames), "<", ">", " ", "");
        return configureSplitting(Strings.cWhitespacePattern, aNames.length, msg);
    }

    public EzReader configureSplitting(Pattern aSplittingPattern, int aParts, String aErrMessage) {
        splitting_pattern = aSplittingPattern;
        splitting_parts = aParts;
        splitting_errMsg = aErrMessage;

        return this;
    }

    public EzReader allowMoreThanReq(boolean aAllow) {
        throw new UnsupportedOperationException();
     }

    public String getCommentStr() {
        return commentStr;
    }

    public EzReader setCommentStr(String aCommentStr) {
        commentStr = aCommentStr;
        return this;
    }

    public List<String> readLines() throws IOException {
        try {
            open();

            List<String> lines = new ArrayList<String>();
            for (;;) {
                String line = r.readNonEmptyLine();
                if (line == null) break;
                lines.add(line);
            }
            return lines;
        }
        finally {
            close();
        }
    }

    public List<StringPair> readPairs() throws IOException {
        try {
            open();

            List<StringPair> pairs = new ArrayList<StringPair>();
            for (;;) {
                String[] strs = r.readSplitNonEmptyLine();
                if (strs == null) break;
                pairs.add(new StringPair(strs[0], strs[1]));
            }
            return pairs;
        }
        finally {
            close();
        }
    }

    public List<List<String>> readLists() throws IOException {
        try {
            open();

            List<List<String>> lists = new ArrayList<List<String>>();
            for (;;) {
                String[] strs = r.readSplitNonEmptyLine();
                if (strs == null) break;
                lists.add(Arrays.asList(strs));
            }
            return lists;
        }
        finally {
            close();
        }
    }


    public void open() throws IOException {
        r = IO.openLineReader(file);
        r.configureSplitting(splitting_pattern, splitting_parts, splitting_errMsg);
        if (commentStr != null) r.setCommentStr(commentStr);
    }

    public void close() throws IOException {
        IO.close(r);
    }

//    public abstract open() throws IOException;
//    public abstract close() throws IOException;
// read numbers
}
