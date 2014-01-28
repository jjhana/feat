//package org.purl.jh.feat.diffui.util;
//
//import com.google.common.collect.Iterables;
//import cz.cuni.utkl.czesl.data.layerx.Form;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Set;
//import org.purl.jh.pml.IdedElement;
//import org.purl.jh.pml.location.Location;
//import org.purl.jh.util.col.IntInt;
//import org.purl.jh.util.col.List2D;
//import org.purl.jh.util.col.MultiHashHashMap;
//import org.purl.jh.util.col.MultiMap;
//
///**
// *
// * @author j
// */
//public class Locations {
//    private final List<Location> locs = new ArrayList<>();
//    private final MultiMap<? extends IdedElement,Location> element2loc = new MultiHashHashMap<>();
//    private final List2D<? extends Form> grid;
//
//    
//    private final List2D<Set<Location>> locgrid;
//    
//    public Locations(Iterable<Location> locs, final List2D<? extends Form> grid) {
//        Iterables.addAll(this.locs, locs);
//
//        for (Location loc : locs) {
//            element2loc.add(loc.getElement(), loc);
//        }
//        
//        this.grid = grid;
//        this.locgrid = createLocGrid();
//    }
//
//    private final List2D<Set<Location>> createLocGrid() {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//    
//    public Location move(Location cur, int offset) {
//        int curLocIdx = findClosest(cur, offset >= 0);
//        curLocIdx += offset;
//        
//        return 0 <= curLocIdx && curLocIdx < locs.size() ? locs.get(curLocIdx) : null;
//    }
//    
//    public int findClosest(Location loc /*, boolean forward*/) {
//        int realIdx = locs.indexOf(loc);
//        if (realIdx > -1) return realIdx;
//        
//        IdedElement el = loc.getElement();
//        
//        Set<Location> tmp = element2loc.get(el);                            // todo search in order ?
//        if (!tmp.isEmpty()) return locs.indexOf(tmp.iterator().next()); 
//        
//        
//        IntInt pos = null;
//        if (el instanceof Form) {
//            pos = grid.indexOf(el);
//        }
//        else {
//            pos = null; // todo search but some chosen form of the enge
//        }
//        // todo could use interval trees
//        
//        // find closest recorded location
//        
//        return findClosest(pos, cur);
//    }
//
//    public IntInt findClosest(IntInt start, Location what) {
//        int zeroRow = start.mSecond;
//        
//        for (int c = start.mFirst, c < this.locgrid.columns(); c++) {
//            for (int r = zeroRow, r < this.locgrid.rows; r++) {
//                if (locgrid.get(r, c) != null) return   locgrid.get(r, c);
//            }
//            zeroRow = 0;
//        }
//        return null;
//    }
//    
//    public Locations sort() {
//        final Comparator<Location> comp = new Comparator<Location>() {
//            @Override
//            public int compare(Location o1, Location o2) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        };
//        
//        Collections.sort(locs, comp);
//        
//        return this;
//    } 
//
//    
//}
