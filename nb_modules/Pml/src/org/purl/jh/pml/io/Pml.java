package org.purl.jh.pml.io;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Namespace;

/**
 * General Pml utilities.
 *
 * @author Jirka Hana (jirka ddot hana aat gmail ddot com)
 */
public class Pml {
    public final static String cNamespaceStr = "http://ufal.mff.cuni.cz/pdt/pml/";
    public final static Namespace n = Namespace.getNamespace("http://ufal.mff.cuni.cz/pdt/pml/");
    
    private Pml() {}
    
    /**
     *
     * @param aLocalIds ids to localized (the items in this list are not changed) 
     */
    public static List<String> localizeIds(List<String> aIds) {
        List<String> localIds = new ArrayList<String>(aIds.size());
        for (String id : aIds) {
            int hashIdx = id.indexOf('#');
            localIds.add( hashIdx == -1 ? id : id.substring(hashIdx+1) ); 
        }
        return localIds;
    }


    /**
     *
     * @param aLocalIds ids to globalize (the items in this list are not changed) 
     */
    public static List<String> globalizeIds(final List<String> aIds, String aPrefix) {
        List<String> globalIds = new ArrayList<String>(aIds.size());
        for (String id : aIds) {
            globalIds.add(aPrefix + '#' + id); 
        }
        return globalIds;
    }
    

    
// =============================================================================
// <editor-fold desc="Implementation" defaultstate="collapsed">
// =============================================================================
    
    
// </editor-fold>
}
