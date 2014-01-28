package org.purl.jh.speedsupport;

import java.io.File;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 *
 * @author jirka
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        try {                        
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
