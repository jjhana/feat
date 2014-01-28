package cz.cuni.utkl.czesl.data.layerx;

import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import java.util.Arrays;
import java.util.List;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;

/**
 * todo Eventually replace by DataEvent
 * @author Jirka dot Hana at gmail dot com
 */
public class ChangeEvent<L extends Layer<?>> extends DataEvent<L> {
    public static final String cRefresh = "refresh";     // it is just to hard to capture or repaint the change

    public static final String cFormAdd = "formAdd" ;
    public static final String cFormDel = "formDel";
    public static final String cFormMove = "formMove";
    public static final String cFormsMove = "formsMove";
    public static final String cFormEdit = "formEdit";         // change of the actual form;
    public static final String cFormChange = "formChange";       // change of other properties of the form object (currently only comment);
    public static final String cEdgeAdd = "edgeAdd" ;
    public static final String cEdgeDel = "edgeDel";
    public static final String cLegAdd = "legAdd" ;
    public static final String cLegDel = "legDel";
    public static final String cErrorAdd = "errorAdd" ;
    public static final String cErrorDel = "errorDel";
    public static final String cErrorAttrChange = "errorAttrChange"; // change of tag" comment;
    public static final String cEdgeChange = "edgeChange";      // change of other properties of the edge object (currently only comment);
    public static final String cErrorLinkAdd = "errorLinkAdd";
    public static final String cErrorLinkDel = "errorLinkDel";
    public static final String cSentenceAdd = "sentenceAdd"; // todo experimental;
    public static final String cSentenceDel = "sentenceDel";
    public static final String cSentenceMerge = "sentenceMerge";
    public static final String cSentenceSplit = "sentenceSplit";
    public static final String cSentenceCopy = "sentenceCopy";

    // --- not really used so far (except non-gui transformations)
    public static final String cParaAdd = "paraAdd"; 
    public static final String cParaDel = "paraDel";
    public static final String cParaMerge = "paraMerge";
    public static final String cParaSplit = "paraSplit";
    public static final String cParaCopy = "paraCopy";
    
    
 
    public ChangeEvent(L aLayer, String id, DataListener src, Object srcInfo) {
        super(aLayer, id, src, srcInfo);
    }
    
    public Position anchor;
    public FForm form;
    public List<? extends FForm> forms;
    public Edge edge;
    public Errorr error;
    public Sentence sentence1, sentence2;

    public Element element;
    public String property;

    public Object old;

    /**
     *
     * @return
     */
    public L getLayer() {
        return layer;
    }

    private final static List<String> formEvents = Arrays.asList(cFormEdit, cFormAdd,cFormDel,cFormMove, cFormChange);
    private final static List<String> formsEvents = Arrays.asList(cFormsMove);
    private final static List<String> edgeEvents = Arrays.asList(cEdgeAdd,cEdgeDel,cEdgeChange,cLegAdd,cLegDel);
    private final static List<String> errorEvents = Arrays.asList(cErrorAttrChange,cErrorAdd,cErrorDel,cErrorLinkAdd,cErrorLinkDel);
    private final static List<String> sentenceEvents = Arrays.asList(cSentenceMerge,cSentenceSplit,cSentenceDel,cSentenceAdd,cSentenceCopy);

    public Para getPara() {
        if (formEvents.contains(id))     return form.getAncestor(Para.class);
        if (formsEvents.contains(id))    return forms.iterator().next().getAncestor(Para.class);
        if (edgeEvents.contains(id))     return edge.getAncestor(Para.class);
        if (errorEvents.contains(id))    return error.getAncestor(Para.class);
        if (sentenceEvents.contains(id)) return sentence1.getAncestor(Para.class);

        throw new RuntimeException("Unknown id code");
    }
    
    // todo use super
    @Override
    public String toString() {
        return String.format("Event: %s; form=%s, anchor=%s, edge=%s, error=%s, (element=%s, property=%s), s1=%s, s2=%s; other=%s", id,
            idOrNull(form),
            anchor,
            idOrNull(edge),
            error == null ? null : error.getTag(),
            element instanceof IdedElement ? ((IdedElement)element).getId().getIdStr() : String.valueOf(element),
            property,
            idOrNull(sentence1),
            idOrNull(sentence2),
            old
        );
    }

    private String idOrNull(IdedElement aEl) {
        return aEl == null ? null : aEl.getId().getIdStr();
    }



}
