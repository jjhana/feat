
package org.purl.jh.feat.layered.relation;

import java.awt.Color;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class ErrorLinkWidget extends ConnectionWidget {

//    private final Router errorLinkRouter = new ErrorLinkRouter();

    public ErrorLinkWidget(Scene scene) {
        super(scene);

        setEndPointShape(PointShape.SQUARE_FILLED_BIG);
        setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
        setRouter(ErrorLinkRouter.INSTANCE);
        setForeground(Color.red);

        getActions().addAction(scene.createWidgetHoverAction/*createObjectHoverAction*/ ());

//        getActions().addAction(errorLinkPopupAction);
    }

        @Override
        public void notifyStateChanged(final ObjectState oldState, final ObjectState newState) {
            if (newState.isHovered() || newState.isSelected()) {
                super.notifyStateChanged(oldState, newState);
            }
            else {
                setForeground(Color.red);
            }
        }

    }



//    protected void addErrorLink(final Widget aSrcEdgeW, final Edge aTargetEdge) {
//        ConnectionWidget arrow = new ConnectionWidget(this);
//        errorLinkLayer.addChild(arrow);
//
//        Widget edge2NodeW = findWidget(aTargetEdge);
//        arrow.setSourceAnchor(AnchorFactory.createRectangularAnchor(aSrcEdgeW));
//        arrow.setTargetAnchor(AnchorFactory.createRectangularAnchor(edge2NodeW));
//        arrow.setEndPointShape(PointShape.SQUARE_FILLED_BIG);
//        arrow.setTargetAnchorShape(AnchorShape.TRIANGLE_FILLED);
//        arrow.setLineColor(Color.red);
//        arrow.setRouter(errorLinkRouter);
//
//        arrow.getActions().addAction(errorLinkPopupAction);
//    }
//
//    private final Router errorLinkRouter = new ErrorLinkRouter();
