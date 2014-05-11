package org.purl.jh.feat.profiles;

import java.util.Arrays;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=Profile.class)
public class EmptyProfile extends Profile {
    public EmptyProfile() {
        super(ProfileRegistry.EMPTY_PROFILE_ID, "Empty profile", Spellchecker.EMPTY_DICTIONARY, Arrays.<LayerInfo>asList());
    }
}    
