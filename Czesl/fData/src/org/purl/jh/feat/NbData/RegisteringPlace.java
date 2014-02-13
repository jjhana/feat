/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.purl.jh.feat.NbData;

import org.openide.util.lookup.ServiceProvider;
import org.purl.jh.nbpml.Mime2Layer;
import org.purl.jh.pml.HtmlLayer;
import org.purl.jh.pml.ImgLayer;
import org.purl.jh.pml.Layer;
import org.purl.net.jh.nb.html.HtmlDataObject;
import org.purl.net.jh.nb.img.ImgDataObject;
import org.purl.net.jh.nbutil.XDataObject;

/**
 *
 * @author jirka
 */
public class RegisteringPlace {
    @ServiceProvider(service=Mime2Layer.class)
    public static class HtmlMime2Layer extends Mime2Layer {
        public HtmlMime2Layer() {
            super("text/html");
        }

        @Override
        public Layer<?> getLayer(XDataObject aDObj) {
            final HtmlDataObject dobj = (HtmlDataObject)aDObj;
            return new HtmlLayer(aDObj.getPrimaryFile(), "html", dobj.getDoc(), dobj.getKit() );
        }
    }


    @ServiceProvider(service=Mime2Layer.class)
    public static class ImgMime2Layer extends Mime2Layer {
        public ImgMime2Layer() {
            super("image/jpeg", "image/png");
        }

        @Override
        public Layer<?> getLayer(XDataObject aDObj) {
            final ImgDataObject dobj = (ImgDataObject)aDObj;
            return new ImgLayer(aDObj.getPrimaryFile(), "img", dobj.getImg());
        }
    }
}
