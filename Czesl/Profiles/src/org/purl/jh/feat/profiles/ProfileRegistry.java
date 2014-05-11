package org.purl.jh.feat.profiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.openide.util.Lookup;

@Data
public class ProfileRegistry {
    public final static String EMPTY_PROFILE_ID = "";
    
    public static Collection<? extends Profile> profiles() {
        return Lookup.getDefault().lookupAll(Profile.class);
    }
    
    // todo lambda
    public static List<String> ids() {
        List<String> ids = new ArrayList<>();
        for (Profile profile : profiles() ) {
            ids.add(profile.getId());
        }
        Collections.sort(ids);
                
        return ids;
    }
    
    public static Profile get(String id) {
        for (Profile profile : profiles() ) {
            if (profile.getId().equals(id)) return profile;
        }
        return null;
    }
}