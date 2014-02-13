package org.purl.jh.feat.diffui.diff;

import org.purl.jh.feat.ea.data.layerl.Edge;
import org.purl.jh.feat.ea.data.layerl.Errorr;
import org.purl.jh.feat.ea.data.layerl.LForm;
import org.purl.jh.feat.ea.data.layerl.Sentence;
import org.purl.jh.feat.ea.data.layerx.FForm;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.err.Err;

/**
 * Calculates diff once matching between items is established.
 * @author j
 */
public class LayerEq {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(LayerEq.class);
    
    // todo more, make configurable
    private boolean optComments = false;
    private boolean optTokens = true;
    
    final Matching loMatching; 
    /** Matching at this level */
    final Matching hiMatching;

    final FormEd<LForm> fed;

    public LayerEq(final Matching loMatching, final Matching hiMatching, boolean compareComments) {
        this.loMatching = loMatching;
        this.hiMatching = hiMatching;
        
        optComments = compareComments;
        
        fed = new FormEd<>(createScorer(loMatching));
    }

    public <X extends Element> void eq(final X a, final X b) {
        if (a != null && b == null) {
            if (a instanceof IdedElement) {
                hiMatching.addDifference1((IdedElement)a);
            }
            else {
                log.severe("Null non-IdedElement: %s, %s", a, b);
            }
        }
        else if (a == null && b != null) {
            if (b instanceof IdedElement) {
                hiMatching.addDifference2((IdedElement)b);
            }
            else {
                log.severe("Null non-IdedElement: %s, %s", a, b);
            }
        }
        else if (a instanceof LForm) {
            eq_LForm((LForm)a,(LForm)b);
        }
        else if (a instanceof Edge) {
            eq_Edge((Edge)a,(Edge)b);
        }
        else if (a instanceof Sentence) {
            throw new UnsupportedOperationException("Sentence eq not supported yet");
        }
        else {
            Err.iAssert(a != null || b != null, "Both elements passed to eq are null");
            throw new UnsupportedOperationException("eq on " + a.getClass() + " not supported yet!");
        }
    }

    private void eq_LForm(final FForm a, final FForm b) {
        eqx_Comment(a,b);
                
        if ( optTokens && !com.google.common.base.Objects.equal(a.getToken(), b.getToken()) ) {
            hiMatching.addDifference1(a);
            hiMatching.addDifference2(b);
        }
        // todo other attributes
    }    

    private void eq_Edge(final Edge eA, final Edge eB) {
        eqx_Comment(eA,eB);

        eq_Errors(eA.getErrors(),eB.getErrors());

        // --- shape (forms it is connected to) (only basic shapes are handled so far) ---
        eq_Edge_forms(hiMatching, eA, eB, eA.getHigher(), eB.getHigher());
        eq_Edge_forms(loMatching, eA, eB, eA.getLower(),  eB.getLower());
    }
    
    private <F extends FForm> void eq_Edge_forms(final Matching matching, final Edge eA, final Edge eB, final Set<F> aFormsA, final Set<F> aFormsB) {
        for (F fA : aFormsA) {
            final F fB = (F) matching.getForms().getMatching2(fA);
            if (fB == null) {   // the leg is not matched
                matching.addDifference1( eA.location(fA) );
            }
        }
        for (F fB : aFormsB) {
            final F fA = (F) matching.getForms().getMatching1(fB);
            if (fA == null) {   // the leg is not matched
                matching.addDifference2( eB.location(fB) );
            }
        }
    }
    
    private void eq_Errors(final Set<Errorr> aErrorsA, final Set<Errorr> aErrorsB) {
        if (!aErrorsB.isEmpty() || !aErrorsB.isEmpty()) log.info("Comparing errors:\n   %s\n   %s", aErrorsA, aErrorsB);
        final Set<Errorr> errorsB = new HashSet<>(aErrorsB);    // at the end contains unmatched b-errors
        
        for (Errorr errA : aErrorsA) {
            Errorr errB = findError(errorsB, errA.getTag());  // todo assumes only one error per tag
            if (errB != null) {
                eq_Error(errA, errB);
                errorsB.remove(errB);   
            }
            else {
                log.info("   errB=null");
                hiMatching.addDifference1(errA.location());
            }
        }

        // mark as different un-matched b-errors
        for (Errorr errB : errorsB) {
            log.info("   unmatched errB = %s", errB);
            hiMatching.addDifference2(errB.location());
        }    
    }

    private void eq_Error(final Errorr aErrA, final Errorr aErrB) {
        eqx_Comment(aErrA,aErrB);
        
        if ( !aErrA.getTag().equals(aErrB.getTag()) ) {
            log.info("   eq_Error:tags   %s != %s", aErrA.getTag(), aErrB.getTag());
            hiMatching.addDifference1(aErrA.location());
            hiMatching.addDifference2(aErrB.location());
        }

//        if ( !aErrA.getLinks().isEmpty() || !aErrB.getLinks().isEmpty()) {
//            log.info("   eq_Error:links %s / %s", aErrA.getTag(), aErrB.getTag());
//            hiMatching.addDifference1(aErrA.location()); // todo 
//            hiMatching.addDifference2(aErrB.location()); // todo 
//        }

        if ( aErrA.getLinks().size() != aErrB.getLinks().size()) {
            log.info("   eq_Error:links (size) %s / %s (%d / %d), lower-forms1=%s", 
                    aErrA.getTag(), aErrB.getTag(), 
                    aErrA.getLinks().size(), aErrB.getLinks().size(),
                    Cols.toString(aErrA.getParent().getLower())
            );
            hiMatching.addDifference1(aErrA.location()); // todo 
            hiMatching.addDifference2(aErrB.location()); // todo 
        }
        else if (aErrA.getLinks().isEmpty()) {
        }
        else if (aErrA.getLinks().size() == 1) {
            final Edge edge1 = aErrA.getLinks().iterator().next();
            final Edge edge2 = aErrB.getLinks().iterator().next();
            
            if (hiMatching.getEdges().getMatching2(edge1) != edge2) {
                log.info("   eq_Error:links 1 nonmatch %s / %s", aErrA.getTag(), aErrB.getTag());
                hiMatching.addDifference1(aErrA.location()); // todo 
                hiMatching.addDifference2(aErrB.location()); // todo 
            }
        }
        else {
            /// todo !!! match them by errors
            
            log.info("   eq_Error:links else %s / %s ", aErrA.getTag(), aErrB.getTag());
            hiMatching.addDifference1(aErrA.location()); // todo 
            hiMatching.addDifference2(aErrB.location()); // todo 
        }
    
    }
    
    /**
     * Finds an error with a given tag.
     * 
     * @param aErrors errors to search through
     * @param aTag tag to look for
     * @return  the first error having aTag as its tag or null if there is none
     */
    private static Errorr findError(final Set<Errorr> aErrors, final String aTag) {
        for (Errorr error : aErrors) {
            if (error.getTag().equals(aTag)) return error;
        }
        return null;
    }
    

    
    /**
     * If comments are supposed to be checked, checks if two object have equal 
     * comment (or none).
     * 
     * Unfortunately empty comments are sometimes null, sometimes empty.
     * 
     * @param a
     * @param b
     * @return 
     */
    private boolean eqx_Comment(final Commented a, final Commented b) {
        if ( optComments && !com.google.common.base.Objects.equal(a.getComment(), b.getComment()) ) {
            hiMatching.addDifference1(a.location());
            hiMatching.addDifference2(b.location());
            return false;
        }

        return true;
    }    

    private static FormEd.SimilarityScorer<LForm> createScorer(final Matching loMatching) {
        return new FormEd.SimilarityScorerAdapter<LForm>() {

            @Override
            public int sub(List<LForm> as, int ai, List<LForm> bs, int bi) {
                final LForm a = as.get(ai);
                final LForm b = bs.get(bi);

                if (com.google.common.base.Objects.equal(a.getToken(), b.getToken())) {
                    return 0;
                }

                // todo - check if srcs are matching, not if they have the same id
                Set<FForm> srcs1 = a.getLowerForms();        
                Set<FForm> srcs2 = b.getLowerForms();

                if (srcs1.isEmpty() && srcs2.isEmpty()) {
                    return 0;
                }
                else if (srcs1.size() == 1 && srcs2.size() == 1) {
                    if ( loMatching.getForms().areMatching(srcs1.iterator().next(), srcs2.iterator().next()) ) {
                        return 0;  // todo 0.1 or something (more than 0 less then ins/del)
                    }
                    else {
                        return 1;   
                    }
                }
                // todo multiple forms
                
//                // todo - check if srcs are matching, not if they have the same id
//                Form srcA = a.getLowerForm();        
//                Form srcB = b.getLowerForm();
//                if (srcA != null && srcB != null) {
//                    if (srcA.getId().getIdStr().equals(srcB.getId().getIdStr())) {
//                        return 0;   // todo 0.1 or something (more than 0 less then ins/del)
//                    }
//                }
//                else if (srcA == null && srcB == null) {     
//                    return 0;
//                }

                //return formsMatch(a.getLowerForms(), b.getLowerForms()) ? 0 : 1;

                return 1;
            }
        };
    }
    
}
