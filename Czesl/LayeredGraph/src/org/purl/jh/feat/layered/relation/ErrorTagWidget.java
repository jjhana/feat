package org.purl.jh.feat.layered.relation;

import cz.cuni.utkl.czesl.data.layerl.Errorr;
import org.purl.jh.feat.layered.util.ElementUtils;
import org.purl.jh.feat.layered.Css;
import org.netbeans.api.visual.laf.LookFeel;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;

/**
 *
 * @author j
 */
public class ErrorTagWidget extends LabelWidget {
    private final Errorr errorInfo;
    private final CentralNode centralNode;

    public ErrorTagWidget(Scene scene, CentralNode centralNode) {
        super(scene, "X");
        this.centralNode = centralNode;
        errorInfo = null;
        //getActions().addAction(  ((LayeredGraph)scene).getRel2XConnectAction() );
        //getActions().addAction(  ((LayeredGraph)scene).getRel2XConnectAction() );
        //getActions().addAction(  ((LayeredGraph)scene).getRel2XConnectAction() );
    }

    public ErrorTagWidget(Scene scene, Errorr aErrorInfo, CentralNode centralNode) {
        super(scene, aErrorInfo.getTag());
        this.centralNode = centralNode;
        errorInfo = aErrorInfo;
    }

    public void update() {
        final LookFeel lookFeel = getScene().getLookFeel();

        setBorder(    lookFeel.getBorder    (getState()));
        setOpaque(    lookFeel.getOpaque    (getState()));
        setBackground(lookFeel.getBackground(getState()));
        setForeground(lookFeel.getForeground(getState()));
        
        // setToolTipText(node.getToken()); comment/ description ???
        
        //setForeground(Color.black);
        if (errorInfo != null) {
            setLabel(errorInfo.getTag());

            if (ElementUtils.isECCommented(errorInfo)) {
                setToolTipText(errorInfo.getComment());
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_errorCheck);
            }
            else if (ElementUtils.isCommented(errorInfo)) {
                setToolTipText(errorInfo.getComment());
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_comment);
            }
            else {
                setBorder(org.netbeans.api.visual.border.BorderFactory.createEmptyBorder());    // via setStyle
            }
        }
    }

}
