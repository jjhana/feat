package cz.cuni.utkl.czesl.data.layerw;

import com.google.common.collect.ImmutableList;
import cz.cuni.utkl.czesl.data.layerx.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.netbeans.api.annotations.common.NonNull;

/**
 * Word at W-layer.
 */
@Getter @Setter @lombok.experimental.Accessors(chain = true)
public class WForm extends FForm  {

    
    private boolean spaceAfter;

    /** The position of the token in the html file (e.g. to allow highlighting) */
    private int docOffset;
    /** The length of the token in the html file (e.g. to allow highlighting) */
    private int len;


    /**
     * Possible alternatives to the main token.
     */
    private List<String> altTokens;

    /** Backward compatibility: Original token encoding alternatives. Just in case we did not convert it correctly */
    private String oldToken;
    
    // todo unreadable chars

    /**
     * Written (possibly partially) in a foreign script (e.g. cyrillic in a
     * document written otherwise in latin alphabet).
     */
    private boolean foreignScript = false;
// =============================================================================

    public WForm(@NonNull WLayer layer, @NonNull String locId, String token, List<String> altTokens, String oldToken, Type type, ImmutableList<org.jdom.Element> other, boolean spaceAfter, int from, int len) {
        super(layer, locId, type, token, other);

        this.altTokens = altTokens;
        this.oldToken = oldToken;
        
        this.spaceAfter = spaceAfter;
        this.docOffset = from;
        this.len = len;
    }

    public boolean hasSpaceAfter() {
        return spaceAfter;
    }

// -----------------------------------------------------------------------------
// funtions
// -----------------------------------------------------------------------------


    public String toDebugString() {
        return String.format("%s : %s : %s", token, getId(), spaceAfter);
    }


// -----------------------------------------------------------------------------
// funtions
// -----------------------------------------------------------------------------

    @Override
    public String toString() {
        return getToken();
    }

}

