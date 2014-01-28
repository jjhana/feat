package org.purl.jh.pml;

import org.purl.jh.pml.NonpmlLayer;
import java.awt.image.BufferedImage;
import org.openide.filesystems.FileObject;

/**
 * ImgLayer to be injected into a ImgDataObject
 * 
 * @author Jirka dot Hana at gmail dot com
 */
public class ImgLayer extends NonpmlLayer {
    final BufferedImage img;

    public ImgLayer(FileObject aFile, String aId, BufferedImage aImg) {
        super(aFile, aId);
        img = aImg;
    }

    public BufferedImage getImg() {
        return img;
    }

}
