package org.purl.jh.feat.layered;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Test extends Scene {

    public static class CWidget extends Widget {

        Widget label;
        ConnectionWidget leg;

        public CWidget(Scene aScene) {
            super(aScene);

            label = new LabelWidget(getScene(), "O") {
                @Override public String toString() {
                    return getLabel();
                }
            };

            addChild(label);
        }

        public void addLeg(Widget aNode) {
            ConnectionWidget leg = new ConnectionWidget(getScene());
            addChild(leg);
            leg.setSourceAnchor(AnchorFactory.createRectangularAnchor(label));
            leg.setTargetAnchor(AnchorFactory.createRectangularAnchor(aNode));
        }
    }

    public Test() {
        super();
        
        LabelWidget node = new LabelWidget(this);
        addChild(node);


        CWidget w = new CWidget(this);
        addChild(w);
        w.addLeg(node);
    }

    public static void main (String[] args) {
        Test scene = new Test (); // create a scene

        JFrame frame = new JFrame ();//new JDialog (), true);
        frame.add(scene.createView (), BorderLayout.CENTER);
        frame.setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible (true);
    }


}
