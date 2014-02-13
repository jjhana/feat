package org.purl.jh.pml;

import java.util.ArrayList;
import java.util.List;

public class Section {
//    Data mData;
//    List<Line> mLines;  // goes thru == alllines.sublist(from, to)
//
//    // called by Data only
//    public Section(Data aData, List<Line> aLines) {
//        mData = aData;
//        mLines = aLines;
//    }
//
//    /**
//     * Empty section, not (yet) part of the document
//     */ 
//    public Section(Data aData) {
//        mData = aData;
//        mLines = aData.getLines();
//    }
//
//    public List<Line> lines() {return mLines;}
//    
//    /**
//     * Returns a new section removing the specified number of lines from each side.
//     *
//     * @param aL  nr of lines to remove left, i.e. the beginning index (inclusive)
//     * @param aR  nr of lines to remove right, i.e. the end index when counting from 
//     *      the back (inclusive)
//     * @return     the specified subsection.
//     * @exception  IndexOutOfBoundsException  if <code>aL</code> or 
//     * <code>aR</code> are inappropriate
//     */
//    public Section subsectionLR(int aL, int aR) {
//        return new Section(mData, mLines.subList(aL, mLines.size() - aR));
//    }
//
//
//    public void add(String aString) {
//        mLines.add( new Line(mData, aString, null) );
//    }
//
//    /**
//     * Requires 
//     */
//    public void add(int aIdx, String aString) {
//        Line line = new Line(mData, aString, null);
//        mLines.add( aIdx, line );
//    }
//
//
//
//    public Section findSection(String aTag, String aLineStr) {
//        
//        for (int start = 0; start < mLines.size(); ) {
//            Section section = findSection(aTag, start);
//            if (section == null) return null;
//            if (section.find(aLineStr,0) != -1) return section;
//            start += section.lines().size();
//        }
//        // @todo
//        return null;
//    }
//
//    public Section findSection(String aTag, int aFrom) {
//        int from = findByTag(aTag, aFrom);
//        if (from == -1) return null;
//        int to   = findByTag('/' + aTag, from);
//        if (to == -1) to = mLines.size(); 
//        
//        return new Section(mData, mLines.subList(from,to+1));
//    }
//    
//    public int find(String aLineStr, int aFromIdx) {
//        for (int i = aFromIdx; i < mLines.size(); i++) {
//            if (mLines.get(i).line().equals(aLineStr)) return i;
//        }
//        return -1;
//    }
//    
//    public int findByTag(String aTag, int aFromIdx) {
//        for (int i=aFromIdx; i < mLines.size(); i++) {
//            if ( Sgml.startsWithTag( mLines.get(i).line(), aTag) )
//                return i;
//        }
//        return -1;
//    }
//    
}

//    /**
//     * @todo tag -> path, 
//     */
//    public Section findSection(String aTag, int aFrom) {
//        int from, to;
//        if (aTag == null) {
//            from = aFrom;
//            to = mLines.size();
//        }
//        else {
//            from = findLine(aTag, aFrom);
//            if (from == -1) return null;
//            to   = findLine('\\' + aTag, from);
//            if (to == -1) to = mLines.size(); 
//        }
//        
//        return new Section(this, mLines.subList(from,to));
//    }
//    
