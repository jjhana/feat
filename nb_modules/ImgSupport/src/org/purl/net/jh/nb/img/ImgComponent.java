package org.purl.net.jh.nb.img;

import javax.swing.event.ChangeEvent;
import org.purl.net.jh.nbutil.ZoomModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.event.ChangeListener;
import org.purl.net.jh.nbutil.ZoomUtil;
import org.purl.jh.util.Logger;

/**
 * An AWT component displaying a buffered image. Supports zooming
 * 
 * @author Jirka dot Hana at gmail dot com
 */
public class ImgComponent extends Component {
    private final static Logger log = Logger.getLogger(ImgComponent.class);

    private final BufferedImage img;
    private final ZoomModel zoomModel;

    public ChangeListener zoomListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            repaint();
        }
    };


    public ImgComponent(BufferedImage aImg, ZoomModel aZoomModel) {
        img = aImg;
        zoomModel = aZoomModel;
        zoomModel.addChangeListener(zoomListener);
    }

    // svycarsky prasky a francouzky hole a jde do prace
    @Override
    public void paint(Graphics g) {
        Dimension zoomedSize = ZoomUtil.scale(img.getWidth(), img.getHeight(), zoomModel.getType(), zoomModel.getVal(), getParent().getSize());
        g.drawImage(img, 0, 0, zoomedSize.width, zoomedSize.height, null);
    }

//    public void setZoomType(ZoomModel.Type aZoomType) {
//        model.setType(aZoomType);
//    }
//
//    public void setZoom(int aVal) {
//        model.setVal(aVal);
//    }
//
    @Override
    public Dimension getPreferredSize() {
        if (img == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(img.getWidth(null), img.getHeight(null));
        }
    }
}
