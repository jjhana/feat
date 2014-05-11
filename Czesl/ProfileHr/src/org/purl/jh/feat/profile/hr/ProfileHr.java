package org.purl.jh.feat.profile.hr;

import java.util.Arrays;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;
import org.purl.jh.feat.profiles.ErrorTagset;
import org.purl.jh.feat.profiles.LayerInfo;
import org.purl.jh.feat.profiles.Profile;
import org.purl.jh.feat.profiles.Spellchecker;

@ServiceProvider(service=Profile.class)
public class ProfileHr extends Profile {
    private static List<LayerInfo> layerInfos() {
        LayerInfo w = new LayerInfo(null);
        LayerInfo a = new LayerInfo(new ErrorTagset("http://ffzg.unizg.hr/T1", "T1 Error Tags"));
        LayerInfo b = new LayerInfo(new ErrorTagset("http://ffzg.unizg.hr/T2", "T2 Error Tags"));
        
        return Arrays.asList(w, a, b);
    }
    
    public ProfileHr() {
        super("Croatian", "Default Croatian Profile", Spellchecker.init("hr"), layerInfos());
    }

}
