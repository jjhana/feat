package org.purl.jh.util.gui.list;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Jirka
 */
public class SubList<T> {
    List<T> mFullList;
    List<Integer> mFull2Shown;          // same size as mFullList       // replace by fast intList
    List<Integer> mShown2Full;          // smaller or equal than mFullList
    Integer cMinusOne = new Integer(-1);
    
    public SubList(List<T> aList) {
        mFullList = aList;
    }

    public void hide(BitSet aBitSet) {
        initArrays();
        for (int i = 0; i < mFullList.size(); i++) {
            if (aBitSet.get(i)) {
                mFull2Shown.add(mShown2Full.size());
                mShown2Full.add(i);
            }
            else {
                mFull2Shown.add(cMinusOne);
            }
        }
    }
    
    public void hide(int aFullIdx) {
        int showIdx = full2Shown(aFullIdx);
        
        mFull2Shown.set(aFullIdx, -1);
        mShown2Full.remove(showIdx);
    }
    
    public void show(int aFullIdx) {
        
    }

    public void showAll() {
        initArrays();
        for (int i = 0; i < mFullList.size(); i++) {
            mFull2Shown.add(i);
            mShown2Full.add(i);
        }
    }


    private void initArrays() {
        mFull2Shown = new ArrayList<Integer>(mFullList.size()); 
        mShown2Full = new ArrayList<Integer>(mFullList.size()); 
    }
    
// =============================================================================
// Indexes
// =============================================================================    

    public int full2Shown(int aFullIdx) {
        return mFull2Shown.get(aFullIdx); 
    }

    public int shown2Full(int aShownIdx) {
        return mShown2Full.get(aShownIdx); 
    }

    public boolean isShown(int aFullIdx) {
        return shown2Full(aFullIdx) != -1; 
    }

    /**
     * 
     * @return visible index
     */
    public int nextShown(int aFullIdx) {
        int shownIdx = full2Shown(aFullIdx);
        if (shownIdx == -1) {
            // @todo
            return -1;
        }
        else {
            shownIdx++;
            return (shownIdx < mShown2Full.size()) ? shownIdx : -1;
        }
    }

    /**
     * 
     * @return visible index
     */
    public int prevShown(int aFullIdx) {
        int shownIdx = full2Shown(aFullIdx);
        if (shownIdx == -1) {
            // @todo
            return -1;
        }
        else {
            return shownIdx;        // -1 or a real idx
        }
    }

    public int fullIdxOf(T aElement) {
        return mFullList.indexOf(aElement);
    }

    public int fullIdxOf(T aElement, int aFullStartingIdx) {
        return mFullList.subList(aFullStartingIdx, mFullList.size()).indexOf(aElement);
    }

    public int shownIdxOf(T aElement) {
        return -1;
    }

    public int shownIdxOf(T aElement, int aShownStartingIdx) {
        return -1;
    }
    
// =============================================================================
// Modifications
// =============================================================================    
    
    public void add(T aElement) {}
    public void addAll(Collection<T> aElement) {}
    public void remove(T aElement) {}
    public void removeAll(Collection<T> aElement) {}
    
}
