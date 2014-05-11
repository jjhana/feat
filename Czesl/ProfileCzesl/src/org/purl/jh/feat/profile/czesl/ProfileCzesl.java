package org.purl.jh.feat.profile.czesl;

import java.util.Arrays;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.purl.jh.feat.profiles.LayerInfo;
import org.purl.jh.feat.profiles.Profile;
import org.purl.jh.feat.profiles.Spellchecker;

@ServiceProvider(service=Profile.class)
public class ProfileCzesl extends Profile {
    private static List<LayerInfo> layerInfos() {
        LayerInfo w = new LayerInfo(null);
        LayerInfo a = new LayerInfo(ErrorSpecs.INSTANCE.aTagset);
        LayerInfo b = new LayerInfo(ErrorSpecs.INSTANCE.bTagset);
        
        return Arrays.asList(w, a, b);
    }
    
    public ProfileCzesl() {
        super("Czesl", "Czech Czesl Profile", Spellchecker.init("cs"), layerInfos());
    }

}
