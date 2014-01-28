package org.purl.jh.feat.layered.relation;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import org.purl.jh.feat.layered.util.ElementUtils;
import org.purl.jh.feat.layered.Css;
import org.purl.jh.feat.layered.LayeredGraph;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.*;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.visual.laf.LookFeel;
import org.netbeans.api.visual.model.ObjectState;
import org.purl.jh.util.Logger;
import org.purl.jh.util.col.Mapper;
import org.purl.jh.util.err.Err;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class CentralNode extends Widget {
    private final static Logger log = Logger.getLogger(CentralNode.class.getName());

//    public static class NoError extends Errorr {
//        public NoError(Edge aEdge) {super(null); setParent(aEdge); }
//    }


    private final Map<Errorr, ErrorTagWidget> error2w = new HashMap<>();
    //private static final Image IMAGE_MEMBER = Utilities.loadImage("cz/cuni/utkl/czesl/icons/blue-square.png"); // NOI18N
    //private static final Border BORDER_4 = BorderFactory.createEmptyBorder(4);
    private final LayeredGraph myscene;
    private final RelationWidget relWidget;
    private final ErrorTagWidget emptyW;

    public CentralNode(LayeredGraph aScene, RelationWidget aRelWidget) {
        super(aScene);
        myscene = aScene;

        relWidget = aRelWidget;

        setLayout(LayoutFactory.createVerticalFlowLayout());
        //setBorder(BorderFactory.createLineBorder());
        setOpaque(true);
        setCheckClipping(true);

        emptyW = new ErrorTagWidget(getScene(), this);

        drawErrors();
        updated();
    }

    /**
     *
     * WOuld be parent if RelationWidget was a real widget.
     * @return
     */
    public RelationWidget getRelWidget() {
        return relWidget;
    }

    public Collection<Errorr> getErrorInfos() {
        return error2w.keySet();
    }

    public Map<Errorr, ErrorTagWidget> getError2w() {
        return error2w;
    }

    public void updated() {
        final LookFeel lookFeel = getScene().getLookFeel();


        if (getState().isHovered() || getState().isSelected()) {
            setForeground( getScene().getLookFeel().getForeground(getState()) );
        }
        setBorder(    lookFeel.getBorder    (getState()));
        setBackground(lookFeel.getBackground(getState()));
        setOpaque(    lookFeel.getOpaque    (getState()));
        setForeground(lookFeel.getForeground(getState()));

        // error/comment - unify with other widgets
        if (ElementUtils.isCommented(getRelWidget().edge)) {
            setToolTipText(getRelWidget().edge.getComment());

            if (ElementUtils.isECCommented(getRelWidget().edge)) {
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_errorCheck);
            } else {
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_comment);
            }
        }

        setForeground(relWidget.hasError() ? Color.red : Color.black);

        for (Map.Entry<Errorr, ErrorTagWidget> entry : error2w.entrySet()) {
            entry.getValue().update();
        }

        if (error2w.isEmpty()) {
            emptyW.update();
        }

    }

    private void drawErrors() {
        final Edge edge = relWidget.edge;
        if (edge.getErrors().isEmpty()) {
            drawEmptyError();
        }
        else {
            for (Errorr e : edge.getErrors()) {
                ErrorTagWidget w = new ErrorTagWidget(getScene(), e, this);
                error2w.put(e, w);
                //todo put back myscene.addObject(aError, w);
                addChild(w);
                w.update();
            }
        }
    }

    private void drawEmptyError() {
        addChild(emptyW);
    }

    public void errorAdd(Errorr aError) {
        if (error2w.isEmpty()) {
            log.info("errorAdd: empty");
            removeChildren();   // remove X marking empty node
        }
        else {
            log.info("errorAdd: nonempty (this %s): %s", this, error2w);
        }

        Err.iAssert(!error2w.containsKey(aError), "Already there");
        ErrorTagWidget w = new ErrorTagWidget(getScene(), aError, this);
        error2w.put(aError, w);
        //todo put back myscene.addObject(aError, w);
        addChild(w);

        getScene().validate();

        this.setPreferredBounds(null);
        getPreferredBounds();
    }


    public void errorDel(Errorr aError) {
        final Widget w = error2w.remove(aError);
        if (w==null) {
            log.info("error not there: %s (%s)", aError, aError.hashCode());
            for (Errorr e : error2w.keySet()) {
                log.info("%s    (%s)      %s", e, e.hashCode(), error2w.get(e));
            }
        }

        w.removeFromParent();

        if (error2w.isEmpty()) {
            drawEmptyError();
        }

        getScene().validate();

        this.setPreferredBounds(null);
        getPreferredBounds();
        log.info("errorDel: nonempty (this %s): %s", this, error2w);
    }

    public void removeMember(Widget memberWidget) {
        removeChild(memberWidget);
    }

    final Mapper<Errorr, String> mapper = new Mapper<Errorr, String>() {

        public String map(Errorr aOrigItem) {
            return aOrigItem.getTag().getTagId();
        }
    };


    @Override
    public void notifyStateChanged(final ObjectState oldState, final ObjectState newState) {
        final LookFeel lookFeel = getScene().getLookFeel();
        setBorder(lookFeel.getBorder(newState));
        //xxrelWidget.updateStyle(this);
        relWidget.update();
    }

    /**
     * Derives a reasonable position for the central node.
     * For y-axis, derives the position from the layer ({@link LayeredGraph#layer2relY(cz.cuni.utkl.czesl.data.layerx.FormsLayer)})
     * For x-axis, uses average of x-locations of all the connected node.
     * //@todo consider average for horizontal position
     */
    public Point getBestLocation() {

        LLayer layer = relWidget.edge.getLayer();
        int y = myscene.layer2relY(layer);

//        int minX = Integer.MAX_VALUE;
//        int maxX = Integer.MIN_VALUE;

        int sumX = 0;

        for (Widget node : relWidget.nodes) {
            final Point loc = node.getLocation();
            final Rectangle bounds = node.getBounds();

            sumX += loc.x + loc.x + bounds.width;
//            minX = Math.min(minX, loc.x);
//            maxX = Math.max(maxX, loc.x + bounds.width);
        }

        int x = sumX / 2 / relWidget.nodes.size();
        //int x = (minX + maxX) / 2;

        return new Point(x, y);
    }
    
    /**
     * Use for debugging only.
     * @return 
     */
    @Override
    public String toString() {
        return super.toString() + ":" + error2w.keySet();
    }
    
}

