package org.purl.jh.feat.util0.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author jirka
 */
public class VisualUtils {

    /**
     * Converts a collection of objects to the corresponding list of widgets.
     *
     * @param aObjects
     * @return
     */
    public static List<Widget> objs2widgets(final ObjectScene aScene, final Iterable<?> aObjects) {
        final List<Widget> ws = new ArrayList<>();
        for (Object o : aObjects) {
            ws.add(aScene.findWidget(o));
        }

        return ws;
    }

    public static <T> List<T> sortObjsByLocation(final List<T> aObjs, final ObjectScene aScene) {
        final List<Widget> widgets = VisualUtils.objs2widgets(aScene, aObjs);
        Collections.sort(widgets, locComparator);
        return widgets2objs(widgets, aScene);
    }

    public final static Comparator<Widget> prefLocComparator = new Comparator<Widget>() {
        public int compare(Widget w1, Widget w2) {
            return w1.getPreferredLocation().x - w2.getPreferredLocation().x;
        }
    };

    public final static Comparator<Widget> locComparator = new Comparator<Widget>() {
        public int compare(Widget w1, Widget w2) {
            return w1.getLocation().x - w2.getLocation().x;
        }
    };

    /**
     * TOdo make a utility method
     * @param aWidgets
     * @return
     */
    public static <T> List<T> widgets2objs(Collection<? extends Widget> aWidgets, final ObjectScene aScene) {
        final List<T> objs = new ArrayList<T>();
        for (Widget w : aWidgets) {
            objs.add((T)aScene.findObject(w));
        }

        return objs;
    }

    /** List of widgets ordered by their original position */
    private List<Widget> sortByOrigX(List<Widget> aWs) {
        final Comparator<Widget> c = new Comparator<Widget>() {
            public int compare(Widget w1, Widget w2) {
                return w1.getPreferredLocation().x - w2.getPreferredLocation().x;
            }
        };
        Collections.sort(aWs, c);
        return aWs;
    }

}
