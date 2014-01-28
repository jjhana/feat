package org.purl.net.jh.nbutil.visual;


import java.io.File;
import java.io.IOException;
import org.netbeans.api.visual.widget.Scene;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.purl.jh.util.err.Err;
import org.purl.net.jh.nbutil.ExportImgPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jirka
 */
public class SceneSupport {
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(SceneSupport.class);

    public static void exportImg(Scene aScene) {
        final ExportImgPanel panel = new ExportImgPanel();
        final DialogDescriptor dd = new DialogDescriptor(panel, bundle.getString("SceneSupport.exportImg.title"));
        DialogDisplayer.getDefault().notify(dd);

        if (dd.getValue() == DialogDescriptor.OK_OPTION) {
            String file = panel.getFile();
            
            if (panel.getAddEnding()) {
                if (panel.getImageType() == SceneExporter.ImageType.PNG) {
                    if (!file.endsWith(".png")) {
                        file += ".png";
                    }
                }
                else if (panel.getImageType() == SceneExporter.ImageType.JPG) {
                    if (!file.endsWith(".jpg") && !file.endsWith(".jpeg")) {
                        file += ".jpg";
                    }
                }
            }

            try {
                SceneExporter.createImage(aScene, new File(file), panel.getImageType(),
                        panel.getZoomLevel(), panel.getVisibleOnly(), false,
                        100, panel.getW(), panel.getH());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex); // todo
            }
        }

    }
}
