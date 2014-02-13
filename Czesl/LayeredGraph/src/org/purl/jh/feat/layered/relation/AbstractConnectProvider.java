package org.purl.jh.feat.layered.relation;

import org.purl.jh.feat.layered.LayeredGraph;
import java.io.PrintWriter;
import org.purl.net.jh.nbutil.NbUtil;

/**
 *
 * @author Jirka
 */
public abstract class AbstractConnectProvider {

    protected final LayeredGraph scene;

    public AbstractConnectProvider(final LayeredGraph scene){
        this.scene=scene;
    }


}
