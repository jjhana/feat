package org.purl.jh.feat.profiles;

import lombok.Data;

@Data 
public class LayerInfo {
    /** Can be null (e.g. for w-layer) */
    private final ErrorTagset tagset;
}
