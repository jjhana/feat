package org.purl.net.jh.nbutil;

import java.io.PrintWriter;
import org.openide.windows.IOProvider;

/**
 *
 * @author jirka
 */
public final class NbUtil {
    private NbUtil() {}

    public static PrintWriter getOut() {
        return IOProvider.getDefault().getIO("Log", false).getOut();
    }

}
