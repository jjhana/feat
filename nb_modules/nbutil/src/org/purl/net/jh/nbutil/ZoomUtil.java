package org.purl.net.jh.nbutil;

import java.awt.Dimension;

/**
 *
 * @author jirka
 */
public class ZoomUtil {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ZoomUtil.class);

    public enum Type {
        pageFit, widthFit, orig, custom;
    }

    // todo feed smth like this into the zoom control
    public static double zoomFactor(final int aW, final int aH, final Type aType, final double aZoomFactor, final Dimension aWindowsSize) {
        return (scale(aW, aH, aType, aZoomFactor, aWindowsSize).width*1.0) / ( aW * 1.0);
    }

    public static Dimension scale(final int aW, final int aH, final Type aType, final double aZoomFactor, final Dimension aWindowsSize) {
        switch (aType) {
            case pageFit:  return pagefit(aW, aH, aWindowsSize);
            case widthFit: return widthfit(aW, aH, aWindowsSize);
            case orig:     return new Dimension(aW,aH);
            case custom:   return custom(aW, aH, aZoomFactor);
            default: return null;   // never happens
        }
    }

        //double z = zoomFactor(val, cSliderMax);

    public static Dimension custom(final int aW, final int aH, final double aZoomFactor) {
        log.finer("zoom: aZoomFactor=%f, aW=%d, aH=%d, \n", aZoomFactor, aW, aH);
        return new Dimension(i(aW*aZoomFactor), i(aH*aZoomFactor));
    }

    protected static Dimension pagefit(final int aW, final int aH, final Dimension aWindowsSize) {
        final double wr = (aW*1.0) / (aWindowsSize.width*1.0);
        final double hr = (aH*1.0) / (aWindowsSize.height*1.0);

        if (wr < hr) {
            log.info("scaling w < h: wr=%f, hr=%f", wr, hr);
            return new Dimension(i(aW/hr), aWindowsSize.height);
        }
        else {
            log.info("scaling w > h: wr=%f, hr=%f", wr, hr);
            return new Dimension(aWindowsSize.width, i(aH/wr));
        }
    }

    protected static Dimension widthfit(final int aW, final int aH, final Dimension aWindowsSize) {
        final double wr = (aW*1.0) / (aWindowsSize.width*1.0);
        return new Dimension(aWindowsSize.width, i(aH/wr));
    }

    protected static int i(double aD) {
        return (int)Math.round(aD);
    }


}
