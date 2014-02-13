package org.purl.net.jh.nbutil;

import java.awt.Dimension;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.purl.net.jh.nbutil.ZoomUtil.Type;

/**
 * TODO Distribute code between control and model.
 *
 * Support for zooming the Visual Library canvas.
 * @author Jirka dot Hana at gmail dot com
 */
public class ZoomModel {

// -------------
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(ZoomModel.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(ZoomModel.class);

    /**
     * Only one <code>ChangeEvent</code> is needed per model instance since the
     * event's only (read-only) state is the source property.  The source
     * of events generated here is always "this".
     */
    protected final transient ChangeEvent changeEvent = new ChangeEvent(this);

    /** The listeners waiting for model changes. */
    protected EventListenerList listenerList = new EventListenerList();




    private Type type = Type.orig;
    private double val = 1.0;     // 1.0 is no zoom

    private String prefPrefix;
    private String bundlePrefix;

    private Preferences preferences;
//    private JComponent treeComponent;
//    private Scene scene;

    public ZoomModel() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        fireStateChanged();
    }

    public double getVal() {
        return val;
    }

    public void setVal(double val) {
        this.val = val;
        fireStateChanged();
    }

    public void loadPrefs() {
        type = Type.valueOf(preferences.get(prefPrefix + ".zoom.type", Type.orig.name()) );
        val  = preferences.getDouble(prefPrefix + ".zoom.val", 1.0);
    }

    public void savePrefs() {
        preferences.put(      prefPrefix + ".zoom.type", type.name());
        preferences.putDouble(prefPrefix + ".zoom.val", val);
    }


//    protected void zoomSliderStateChanged(boolean aRepaint) {
//        final double z = zoomFactor(zoomSlider.getValue(), cSliderMax);
//        log.fine("Zoom2: %s", preferences.getDouble("graphZoom", z) );
//
//        scene.setZoomFactor(z);
//
//        if (aRepaint) {
//            treeComponent.repaint();
//        }
//    }



    /**
     * Sets the properties of the zoom.
     * A <code>ChangeEvent</code> is generated.
     *
     * @see #setType
     * @see #setValue
     */
    public void setValues(Type aType, double aValue) {
        type = aType;
        val = aValue;
        fireStateChanged();
    }


    /**
     * Adds a <code>ChangeListener</code>.  The change listeners are notified
     * each the zoom's type or custom value changes
     *
     * @param l the ChangeListener to add
     * @see #removeChangeListener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }


    /**
     * Removes a <code>ChangeListener</code>.
     *
     * @param l the <code>ChangeListener</code> to remove
     * @see #addChangeListener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Runs each <code>ChangeListener</code>'s <code>stateChanged</code> method.
     *
     * @see #setRangeProperties
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        final Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -=2 ) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }


    /**
     * Returns a string that displays all of the properties.
     */
    @Override
    public String toString()  {
        return String.format("%s [%s, %f]", getClass().getName(), getType(), getVal());
    }
}
