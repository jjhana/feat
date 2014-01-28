package org.purl.jh.pml.location;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.purl.jh.pml.IdedElement;

/**
 * Under development.
 * XPath-like object identifying objects and their parts within a layer.
 * 
 * The path must be rooted in an element with an id.
 * 
 * When in memory the root element and thus the layer is part of the 
 * identification, but once serialized
 * into a string path, it must be interpreted relative to a layer.
 * 
 * @author j
 */
public class Location {    
    
    /** 
     * Location expressed as an xpath-like seq of strings. 
     * The first string is the id of {@link #getElement()} 
     */
    private final List<String> location;   

    /** 
     * Root of the path, 
     * Valid only when loaded into memory.
     * Todo: consider lazy initialization.
     */
    private final IdedElement element;  
    
    
    public static Location of(IdedElement aElement) {
        return new Location(aElement);
    }

    public static Location of(IdedElement aElement, String ... aPath) {
        return new Location(aElement, aPath);
    }
    
    private Location(IdedElement aElement) {
        location = ImmutableList.of(aElement.getId().getIdStr());
        element = aElement;
    }

    private Location(IdedElement aElement, String ... aPath) {
        element = aElement;
        
        location = ImmutableList.<String>builder()
            .add(aElement.getId().getIdStr())
            .add(aPath)
            .build();
    }
    
    public List<String> getLocPath() {
        return location;
    }

    public IdedElement getElement() {
        return element;
    }
    
    /* Tmp hack */
    public boolean contains(String aItem) {
        for (String str : location) {
            if (str.equals(aItem)) return true;
        }
        return false;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        return equals( (Location) obj );
    }

    public boolean equals(Location obj) {
        if ( !(obj instanceof Location) ) return false;
        if ( !Objects.equal(element, obj.element)) return false;
        if ( element != obj.element && (element == null || !element.equals(obj.element)) ) return false;

        if (location.size() != obj.location.size()) return false;
        
        for (int i = 0; i < location.size(); i++) {
            if (! Objects.equal(location.get(i), obj.location.get(i)) ) return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(element, location);
    }
    
    @Override
    public String toString() {
        return "Location{" + "location=" + location + "element=" + element + '}';
    }

    
}
