package cz.cuni.utkl.czesl.data.layerw;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerx.ChangeEvent;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import cz.cuni.utkl.czesl.data.layerx.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.openide.filesystems.FileObject;
import org.purl.jh.pml.event.DataListener;

/**
 * Note: There can be multiple documents in a single WLayer; PDT 2 allows only one.
 *
 * parent document is null for a detached layer;
 *    if not detached the layer must be also added to the parent
 *
 * @author Jirka
 */
public class WLayer extends FormsLayer<WDoc>{
            
    /**
     * Creates a new Layer.
     *
     * @param aId a unique identifier of the layer
     * @todo unique among layers or generaly 
     */
    public WLayer(FileObject aFile, String aId) {
        super(aFile, aId);
        lowerLayer = null;
        layerIdx = 0;
    }

    // todo make lightweight or cache the forms
    @Override
    public Iterable<WForm> getForms() {
        final List<WForm> forms = new ArrayList<>();
        for (WDoc doc : col()) {
            for (WPara para : doc.col()) {
                forms.addAll(para.getForms());
            }
        }
        return forms;
    }

    /**
     * 
     * @return 
     * @throws NoSuchElementException if there is no form at this layer
     */
    @Override
    public WForm getFirstForm() {
        return col().iterator().next().iterator().next().iterator().next();
    }
    
    @Override
    public WForm getLastForm() {
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * Finds a form in the document containing a give character position.
     * 
     * @param aPos position in the document (the position is based on the html model, 
     * not its view).
     * 
     * @return the form containing the given position or null of there is no such form.
     */
    public WForm findForm(int aPos) {
        // todo use binary search
        for (WDoc doc : col()) {
            for (WPara para : doc.col()) {
                for (WForm form : para.getForms()) {
                    if (form.getDocOffset() <= aPos && aPos <= form.getDocOffset() + form.getLen()) return form;
                }
            }
        }
        return null;
    }

    @Override
    public boolean formAdd(String aStr, Position aAnchor, DataListener aSrcView, Object aSrcInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean formDel(FForm aForm, DataListener aSrcView, Object aSrcInfo) {
        if (readOnly) return false;

        final WForm form = (WForm) aForm;
        final WPara para = (WPara) form.getParent();

        //final Position anchor = Position.of(form);       position works only for lforms

        // --- delete all legs ending in this form ---
        for (Edge e : form.getHigher()) {
            e.getLayer().legDel(e, form, null, null);
        }

        // --- delete the form (keep its para even if it endsup being empty) ---
        para.col().remove(form);

        removeIdedElement(form);

        final ChangeEvent<WLayer> event = new ChangeEvent<>(this, ChangeEvent.cRefresh, aSrcView, aSrcInfo);
        //event.anchor = anchor;
        //event.form = aForm;
        fireEvents(event);

        return true;
    }

    @Override
    public boolean formMove(FForm aMovedForm, Position aAnchor, DataListener aSrcView, Object aSrcInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean formMove(List<? extends FForm> aMovedForms, Position aAnchor, DataListener aSrcView, Object aSrcInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
    
}

