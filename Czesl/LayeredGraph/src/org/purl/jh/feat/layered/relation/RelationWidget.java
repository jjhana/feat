package org.purl.jh.feat.layered.relation;
import org.purl.jh.feat.layered.LayeredGraph;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.purl.jh.feat.layered.Css;
import org.purl.jh.feat.layered.util.ElementUtils;
import org.purl.net.jh.nbutil.NbUtil;
import org.purl.jh.util.Pair;
import org.purl.jh.util.col.MultiMap;
import org.purl.jh.util.col.XCols;
import org.purl.jh.util.err.Err;

/**
 * Spider like multi-edge. Not really a widget, just an object aggregating several
 * widgets (Note that the central node and the legs are on different scene layers).
 *
 * Consists of:
 * <ul>
 *  <li>center node - widget containing X (if there is no error) or error tags in labels
 *  <li>legs - edges connection the center node with forms
 *  <li>error links - edges connection center node (in the future it should start at an error tag) with other center nodes
 * </ul>
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class RelationWidget { //extends Widget {
    private final static Logger log = Logger.getLogger(RelationWidget.class.getName());

    private final static PrintWriter out = NbUtil.getOut();

    // --- Environment, todo: could be aggregated into one object shared by all widgets
    private final LayeredGraph scene;

    private final LayerWidget conNodeLayer;
    private final LayerWidget connectionLayer;
    private final LayerWidget errorLinkLayer;


    /** Model - The relation represented by this widget */
    final Edge edge;

    // --- Parts of this "widget" ---
    
    private final @NonNull CentralNode centerNode;

    /**
     * Legs connecting the center node with form nodes.
     * A leg and the corresponding node have the same indexes in the respective arrays
     */
    private final List<LegWidget> legs = new ArrayList<>();

    private final MultiMap<Errorr,ErrorLinkWidget> error2links = XCols.newMultiHashHashMap();

    // ---

    /** Form nodes connected by the legs */
    final List<Widget> nodes = new ArrayList<>();


    /**
     * Actions that assigned to each leg. They are added to any new leg.
     */
    private final List<WidgetAction> legActions = new ArrayList<>();

    /**
     * Actions that assigned to each leg. They are added to any new leg.
     */
    private final List<WidgetAction> errorLinkActions = new ArrayList<>();



    public class LegWidget extends ConnectionWidget {
        public LegWidget(Scene scene) {
            super(scene);

            setStroke(new BasicStroke(1.5f));
            //xxupdateStyle(this);
            update();
        }

        @Override
        public void notifyStateChanged(final ObjectState oldState, final ObjectState newState) {
            //xxupdateStyle(this);
            update();
        }
    }




// =============================================================================
//
// =============================================================================

    public RelationWidget(final @NonNull Edge aEdge, final @NonNull LayeredGraph aScene, final @NonNull LayerWidget aConNodeLayer, final @NonNull LayerWidget aConLegLayer, final @NonNull LayerWidget aErrorLinkLayer) {
        edge = aEdge;

        scene = aScene;
        conNodeLayer    = aConNodeLayer;
        connectionLayer = aConLegLayer;
        errorLinkLayer  = aErrorLinkLayer;

        centerNode = new CentralNode(scene, this);
        conNodeLayer.addChild(centerNode);
        //reconnectAction = ActionFactory.createReconnectAction(new SceneReconnectProvider(getScene()));

        // note error links cannot be added now, because their target may not exist yet
    }

    public ObjectScene getScene() {
        return scene;
    }

    public CentralNode getCenterNode() {
        return centerNode;
    }

    public List<LegWidget> getLegs() {
        return legs;
    }

    protected boolean hasError() {
        return ! edge.getErrors().isEmpty();
    }

    public MultiMap<Errorr, ErrorLinkWidget> getError2links() {
        return error2links;
    }

    public void removeFromParent() {
        for (ErrorLinkWidget w : error2links.allValues()) {
            Pair<Errorr, Edge> p = (Pair<Errorr, Edge>) getScene().findObject(w);
            getScene().removeObject(p);
            w.removeFromParent();
        }

        for (Widget leg : legs) {
            leg.removeFromParent();
        }

        centerNode.removeFromParent();
    }

// =============================================================================
// Action support
// =============================================================================

    public void addAction(final WidgetAction aAction) {
        centerNode.getActions().addAction(aAction);
        addLegAction(aAction);
    }

    public void addCenterAction(final WidgetAction aAction) {
        centerNode.getActions().addAction(aAction);
    }

    public void addLegAction(final WidgetAction aAction) {
        legActions.add(aAction);
        for (Widget leg : legs) {
            leg.getActions().addAction(aAction);
        }
    }

    public void addErrorLinkAction(final WidgetAction aAction) {
        errorLinkActions.add(aAction);
        for (Widget errorlink : error2links.allValues()) {
            errorlink.getActions().addAction(aAction);
        }
    }

// =============================================================================
// Modifications
// =============================================================================

    /**
     * Other change (e.g. edges or error's comment). Guaranteed not to be a change in legs/links/number of errors.
     */
    public void otherChange() {

        update();
    }

    public void errorAdd(@NonNull Errorr aError) {
        centerNode.errorAdd(aError);
        update();
    }

    public void errorDel(@NonNull Errorr aError) {
        removeErrorLinks(aError);
        centerNode.errorDel(aError);
        update();
    }


    public LegWidget addLeg(@NonNull Widget aNode) {
//        log.info("  Adding leg: %s", aNode);
        final LegWidget leg = new LegWidget(getScene());
        legs.add(leg);
        nodes.add(aNode);
        connectionLayer.addChild(leg);

        leg.setStroke(new BasicStroke(2.0f));
        leg.setSourceAnchor(AnchorFactory.createRectangularAnchor(centerNode));
        leg.setTargetAnchor(AnchorFactory.createRectangularAnchor(aNode));
//        legReflectErrorStatus(leg, hasError());

        for (WidgetAction action : legActions) {
            leg.getActions().addAction(action);
        }
        return leg;
    }

    public void removeLeg(@NonNull Widget aNode) {
        int idx = nodes.indexOf(aNode);
        if (idx == -1) throw new IllegalArgumentException("Node " + aNode + " is not connected to any leg.");

        nodes.remove(idx);
        ConnectionWidget leg = legs.remove(idx);

        leg.removeFromParent();
    }

    public void errorLinkDel(@NonNull Errorr aError, @NonNull Edge aTargetEdge) {
        final Pair<Errorr,Edge> p = new Pair<>(aError, aTargetEdge);
        final ErrorLinkWidget errorLinkW = (ErrorLinkWidget) getScene().findWidget(p);

        getScene().removeObject(p);
        errorLinkW.removeFromParent();

        error2links.get(aError).remove(errorLinkW);
    }

// =============================================================================
//
// =============================================================================

    public void repaint() {
        centerNode.repaint();
        for (Widget legw : getLegs()) {
            legw.repaint();
        }
    }

    public void revalidate() {
        centerNode.revalidate();
        for (Widget legw : getLegs()) {
            legw.revalidate();
        }
    }

    /**
     * Reflect changes in error/comment/etc status in color etc. (??and tags in bounds.??)
     * 
     * @todo this is messy; distribute the code reasonably between this method and centerNode.updated
     */
    public void update() {
        boolean flagCorrected = false;
        boolean flagCommented = false;
        boolean flagHighlight = false;

        if (ElementUtils.isCommented(edge)) {
            flagCommented = true;
        }
        
        if ( ((LayeredGraph)getScene()).getXHighlights().contains(this.edge) ) {           // todo push directly to the state of the widget
            //log.info("highliting %s", this.node);
            flagHighlight = true;
        }
        
        centerNode.updated();

        

        // todo priv
        if (flagCorrected) Css.INSTANCE.setStyle(this.centerNode, Css.INSTANCE.word_corrected);;

        if (flagCommented) {
            if (ElementUtils.isECCommented(edge)) {
                Css.INSTANCE.setStyle(this.centerNode, Css.INSTANCE.word_errorCheck);   // todo 
            }
            else {
                Css.INSTANCE.setStyle(this.centerNode, Css.INSTANCE.edge_comment);
            }
            centerNode.setToolTipText(edge.getComment());
        }
        
        if (flagHighlight) Css.INSTANCE.setStyle(this.centerNode, Css.INSTANCE.edge_diff); 
        
        for (Widget legw : getLegs()) {
            legw.setForeground(hasError() ? Color.red : Color.black);
            // todo highlight
        }

        if (getScene() != null && getScene().getSceneAnimator() != null && centerNode.getBounds() != null) {
            getScene().getSceneAnimator().animatePreferredBounds(centerNode, null);
        }
    }

    private void manageSelectionStyle(final Widget aWidget) {
        out.println("RelW.manageSelectionStyle");

        if (aWidget.getState().isHovered() || aWidget.getState().isSelected()) {
            aWidget.setForeground( getScene().getLookFeel().getForeground(aWidget.getState()) );
        }

    }

    public void drawErrorLinks(WidgetAction aErrorLinkPopupAction) {
        for (Errorr error : edge.getErrors()) {
            for (Edge targetEdge : error.getLinks()) {
                drawErrorLink(error, targetEdge, errorLinkLayer, aErrorLinkPopupAction);
            }
        }
    }



    public void drawErrorLink(final Errorr aError, final Edge aTargetEdge, final LayerWidget aErrorLinkLayer, final WidgetAction aErrorLinkPopupAction) {
        final ErrorLinkWidget arrow = new ErrorLinkWidget(getScene());
        aErrorLinkLayer.addChild(arrow);

        final Widget srcErrorLabel = centerNode.getError2w().get(aError);
        arrow.setSourceAnchor(AnchorFactory.createRectangularAnchor(srcErrorLabel));

        final Widget edge2NodeW = getScene().findWidget(aTargetEdge);
        arrow.setTargetAnchor(AnchorFactory.createRectangularAnchor(edge2NodeW));

        final Object p = new Pair<>(aError, aTargetEdge);
        Err.iAssert(!getScene().getObjects().contains(p), "%s is already there!", p);
        getScene().addObject(p, arrow);
        error2links.add(aError, arrow);

        if (aErrorLinkPopupAction != null) {
            arrow.getActions().addAction(aErrorLinkPopupAction);
        }
    }

    /** Remove error links if any.
     *
     * @param aError
     */
    private void removeErrorLinks(Errorr aError) {
        final Set<ErrorLinkWidget> errorLinkWs = error2links.remove(aError);
        if (errorLinkWs != null) {
            for (ErrorLinkWidget w : errorLinkWs) {
                Pair<Errorr, Edge> p = (Pair<Errorr, Edge>) getScene().findObject(w);
                getScene().removeObject(p);
                w.removeFromParent();
            }
        }
    }

    private static final WidgetAction.Adapter bringToFrontAction = new WidgetAction.Adapter() {
    //  @Override
    //  public State mouseClicked (final Widget widget, final WidgetMouseEvent event)
    //  {
    //    if (event.getButton() == MouseEvent.BUTTON1) {
    //      widget.bringToFront();
    //      return State.CONSUMED;
    //    }
    //    return State.REJECTED;
    //  }

      @Override
        public State keyTyped(Widget widget, WidgetKeyEvent event) {
          // todo send to scene
          return State.REJECTED;
        }

    };

}
