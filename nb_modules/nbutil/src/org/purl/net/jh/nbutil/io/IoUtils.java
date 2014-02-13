/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.net.jh.nbutil.io;

import java.awt.Color;
import java.io.IOException;
import org.openide.windows.IOColorLines;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputListener;

/**
 *
 * @author jirka
 */
public class IoUtils {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(IoUtils.class);

    private static boolean colorErrorRepoted = false;
    
    /**
     * 
     * @param aIo
     * @param aText should not be null, but null is tolerated
     * @param aColor 
     */
    public static void println(InputOutput aIo, CharSequence aText, Color aColor) {
        println(aIo, aText, null, false, aColor);
    }
    
    /**
     * 
     * @param aIo
     * @param aText should not be null, but null is tolerated
     * @param aListener
     * @param aImportant
     * @param aColor 
     */
    public static void println(InputOutput aIo, CharSequence aText, OutputListener aListener, boolean aImportant, Color aColor) {
        try {
            IOColorLines.println(aIo, String.valueOf(aText), aListener, aImportant, aColor);
        } catch (IOException ex) {
            if (!colorErrorRepoted) {
                colorErrorRepoted = true;
                log.severe("Cannot print in colors.");
            }
            aIo.getOut().println(aText);
        }
    }
}
