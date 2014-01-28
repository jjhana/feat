package org.purl.jh.feat.iaa;

import com.google.common.collect.Sets;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.purl.jh.feat.util0.ByListSort;

/**
 *
 * @author j
 */
public class LStruct {
    Set<String> errorTags = Sets.newHashSet();
    List<LForm> lforms = new ArrayList<>(); 

    public List<LForm> getSortedLForms() {
        if (lforms.size() < 2) {
            return lforms;
        } 
        else {
            final ByListSort<LForm> woSorter = new ByListSort<>(lforms.iterator().next().getParent().getParent().getFormsList());
            return woSorter.sort(lforms);
        }
    }
    
}
