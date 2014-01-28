package org.purl.jh.feat.layered.util;

import org.purl.jh.pml.Commented;
import org.purl.jh.util.Util;

/**
 *
 * @author jirka
 */
public class ElementUtils {
    /**
     * Does the element have a comment by the error checking tool?
     * The comments by the error checking tool all start with the 'EC:' prefix.
     */
    public static boolean isECCommented(final Commented aElement) {
        return isCommented(aElement) && aElement.getComment().startsWith("EC:");
    }

    /**
     * Does the element have a comment?
     */
    public static boolean isCommented(final Commented aElement) {
        return ! Util.isEmpty( aElement.getComment() );
    }


}

