package org.purl.jh.nbpml;

import org.purl.jh.pml.ts.Tagset;

/**
 * Object providing a tagset
 * @author jirka
 */
public interface  TagsetProvider {
    Tagset<?> getTagset();
}