package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.awt.Font;
import java.awt.Rectangle;
import org.netbeans.api.visual.laf.LookFeel;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.purl.jh.feat.layered.util.ElementUtils;
import org.purl.jh.util.str.Strings;

/**
 * Widget for the main nodes (forms).
 *
 * @author jirka
 */
public class WordWidget extends LabelWidget {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(WordWidget.class);
    
    private final FForm node;


    public WordWidget(final LayeredGraph scene, final FForm aNode) {
        super(scene);
        node = aNode;

        update();
    }

    public FForm getNode() {
        return node;
    }

    /**
     * Use for debugging only.
     * @return 
     */
    @Override
    public String toString() {
        return super.toString() + ":" + getLabel();
    }

    @Override
    public void notifyStateChanged(ObjectState previousState, ObjectState state) {
        update();
    }

    /**
     * Calculates a client area for the label, adds extra space to display the hacek of ď.s
     * @return the client area
     */
    @Override
    protected Rectangle calculateClientArea () {
        final Rectangle r = super.calculateClientArea();
        r.grow(1, 1); // workaround to display ď
        return r;
    }

    public void update() {
        final LookFeel lookFeel = getScene().getLookFeel();

        if (node.getType().dt()) {
            setLabel(Strings.dotsLimit(node.getToken(), 10));
            setToolTipText(node.getToken());
        }
        else {
            setLabel(node.getToken());

            // todo configurable
            if (node.getType().priv() && getFont() != null) setFont(getFont().deriveFont(Font.ITALIC));
        }


        setBorder(    lookFeel.getBorder    (getState()));
        setOpaque(    lookFeel.getOpaque    (getState()));
        setBackground(lookFeel.getBackground(getState()));
        setForeground(lookFeel.getForeground(getState()));

        boolean flagIncorrect = false; //Is the spelling incorrect according to the spell-checker?
        boolean flagCorrected = false;
        boolean flagCommented = false;
        boolean flagHighlight = false;

        if (node instanceof LForm) {
            LForm form = (LForm)node;
            if (form.isChangingForm(true)) {
            //if (!form.hasSingleLowerForm() || ! form.getLowerForm().getToken().equals(node.getToken())) {
                flagCorrected = true;
            }
        }

        if (ElementUtils.isCommented(node)) {
            flagCommented = true;
        }

        Dictionary spellchecker = ((LayeredGraph)getScene()).getProfile().getSpellchecker();
        if ( spellchecker != null && node.getToken() != null) {
            ValidityType validity = spellchecker.validateWord(node.getToken());
            if (validity != ValidityType.VALID) {
                flagIncorrect = true;
            }
        }
        
        if ( ((LayeredGraph)getScene()).getXHighlights().contains(this.node) ) {    // todo push directly to the state of the widget
            //log.info("highliting %s", this.node);
            flagHighlight = true;
        }
        // --- set the style etc --

        // todo priv
        if (flagCorrected) Css.INSTANCE.setStyle(this, Css.INSTANCE.word_corrected);;
        if (flagIncorrect) Css.INSTANCE.setStyle(this, Css.INSTANCE.word_spellCheckError);

        if (flagCommented) {
            if (ElementUtils.isECCommented(node)) {
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_errorCheck);
            }
            else {
                Css.INSTANCE.setStyle(this, Css.INSTANCE.word_comment);
            }
            setToolTipText(node.getComment());
        }
        
        if (flagHighlight) Css.INSTANCE.setStyle(this, Css.INSTANCE.word_diff); 
    }

};


//        label.getActions().addAction(createSelectAction());
//        label.getActions().addAction(createWidgetHoverAction/*createObjectHoverAction*/ ());
//
//        label.getActions().addAction(form2XConnectAction);
//
//        if (aForm.getLayer().isReadOnly()) {
//            label.getActions().addAction(wFormPopupAction);
//        }
//        else {
//            label.getActions().addAction(formPopupAction);
//            label.getActions().addAction(formMoveAction);
//            label.getActions().addAction(formEditorAction);
//        }



