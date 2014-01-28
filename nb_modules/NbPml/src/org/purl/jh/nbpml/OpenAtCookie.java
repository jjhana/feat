package org.purl.jh.nbpml;

import org.openide.nodes.Node;
import org.purl.jh.pml.location.Location;

/**
 * Cookie for opening an object at a particular location (e.g. a line in a text 
 * file or a node of a graph).
 * 
 * @author jirka
 */
public interface OpenAtCookie extends Node.Cookie {
    public void openAt(Location aLocation);
}
