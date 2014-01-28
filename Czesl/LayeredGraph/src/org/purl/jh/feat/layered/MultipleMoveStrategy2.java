package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerx.Position;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import org.purl.jh.feat.util0.visual.VisualUtils;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.util.col.Cols;

/**
 * Moves all selected widget with the primary moved one.
 *
 * @author jirka
 */
public class MultipleMoveStrategy2 implements MoveStrategy, MoveProvider {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(MultipleMoveStrategy2.class);

    //private final HashMap<Widget,Point> originalLocations = new HashMap<Widget, Point> ();
    private final LayeredGraph scene;
    private Collection<Object> objects;
    private List<FForm> sortedForms;
    private Point origLoc;

    /** Flag not allowing illegal moves. Currently only prevents moving objects from different layers */
    private boolean noMove = false;

    public MultipleMoveStrategy2(LayeredGraph scene) {
        this.scene = scene;
    }

    // words cannot be moved between layers
    @Override
    public Point locationSuggested(Widget widget, Point originalLocation, Point suggestedLocation) {
        return noMove ? originalLocation : new Point(suggestedLocation.x, originalLocation.y);
    }

    private void log(String aMethod, Widget widget) {
        Object o = scene.findObject(widget);
        String str = (o instanceof IdedElement) ? ((IdedElement)o).getId().toString() : o.toString();
        log.info("MMStrategy %s, o=%s", aMethod, str);
    }

    @Override
    public void movementStarted(Widget widget) {
        // todo what if widget is not selected? -- cannot happen
        // todo do not allow move of widgets on different layers 
        objects = new HashSet<>(scene.getSelectedObjects());

        // forms corresponding to the selected form-widges, sorted by their location
        sortedForms = VisualUtils.sortObjsByLocation(filter(objects, (Class<FForm>)(Object)FForm.class), scene);
        log.info("movementStarted: sortedforms %s", sortedForms);

        origLoc = widget.getLocation();

        noMove = !sameLayer(sortedForms);
    }


    @Override
    public void movementFinished(Widget widget) {
        final Point newLoc = widget.getLocation();
        //if (newLoc.equals(origLoc)) {log.info("locs == ");  return;}        // no movement at all

        // derive anchor based on the primary widget
        final FForm form = scene.findForm(widget);
        final Position anchor = scene.place2anchor(newLoc);
        log.info("movementFinished: %s; anchor=%s", widget, anchor);

        if (anchor == null) {log.info("anchor == null");  return;}
        if (anchor.getForm() == form) {log.info("a.forms ==");  return;};  // did not move ????

        final Position origAnchor = scene.place2anchor(origLoc);
        if (origAnchor == anchor) {log.info("anchors ==");  return;}

        final FormsLayer<?> layer = sortedForms.iterator().next().getLayer();
        
        // todo tmp (the multi move method is still problematic)
        if (sortedForms.size() == 1) {
            layer.formMove(Cols.one(sortedForms), anchor, scene, newLoc);
        }
        else {
            layer.formMove(sortedForms, anchor, scene, newLoc);
        }
    }


    @Override
    public Point getOriginalLocation(Widget widget) {
        log("getOriginalLocation", widget);
        return widget.getLocation();
    }

    @Override
    public void setNewLocation(Widget widget, Point location) {
        if (scene.place2sentence(location) == null) return;   // to prevent moving into inter-sentential space
        widget.setPreferredLocation(location);
    }

// ============================================================================
// To util
// ============================================================================

    public static boolean sameLayer(final Iterable<? extends Element> aObjs) {
        final Layer<?> layer =  Cols.first(aObjs).getLayer();

        // check if they are all from the same layer (todo utility method)
        for (Element form : aObjs) {
            if (layer != form.getLayer()) return false;
        }

        return true;
    }

    /**
     * Returns all objects in a list of a particular type.
     *
     * @param <T>
     * @param aObjs iterable objects to filter
     * @param aClass type of the objects in the result
     * @return all objects in aObjs of the aClass type (order is dermined by aObjs' iterator)
     */
    private <T> List<T> filter(final Iterable<Object> aObjs, final Class<T> aClass) {
        final List<T> filtered = new ArrayList<>();
        for (Object obj : objects) {
            if (aClass.isInstance(obj)) {
                filtered.add((T) obj);
            }
        }
        return filtered;
    }



}
