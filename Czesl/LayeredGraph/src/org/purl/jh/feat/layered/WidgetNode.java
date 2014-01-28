package org.purl.jh.feat.layered;

import cz.cuni.utkl.czesl.data.layerx.ChangeEvent;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.ErrorTag;
import cz.cuni.utkl.czesl.data.layerl.ErrorTagset;
import cz.cuni.utkl.czesl.data.layerl.LLayer;
import cz.cuni.utkl.czesl.data.layerw.WForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import cz.cuni.utkl.czesl.data.layerx.FormsLayer;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.pml.Layer;
import org.purl.jh.pml.event.DataEvent;
import org.purl.jh.pml.event.DataListener;
import org.purl.net.jh.nbutil.NbUtil;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.Mapper;

/**
 * A (practically) singleton node standing for any widget currently selected in the graph.
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class WidgetNode extends AbstractNode implements DataListener {
    private final static PrintWriter out = NbUtil.getOut();

    private final Sheet sheet;

    /** Object corresponding to the object behind the currently selected widget (i.e. Form, Edge) */
    private Object object;

    /** Maps this node to the encapsulated object */
    public static class ObjMapper implements Mapper<WidgetNode,Object> {
        @Override
        public Object map(WidgetNode aOrigItem) {
            return aOrigItem.object;
        }
    }

 
    public WidgetNode() {
        super(Children.LEAF);
        sheet = super.createSheet();
        Sheet.Set props = Sheet.createPropertiesSet();
        sheet.put(props);
    }

    /** Object corresponding to the object behind the currently selected widget (i.e. Form, Edge) */
    public Object getObject() {
        return object;
    }

    public Layer<?> getLayer() {
        return object instanceof Element ? ((Element)object).getLayer() : null;
    }

    @Override
    public Sheet createSheet() {
        return sheet;
    }
    
    

    /** 
     * Changes that do not affect properties, so we do not need any refresh. 
     * todo consider adding more, but better to be defensive than wrong
     */
    private final Set<String> ignoredChanges = com.google.common.collect.ImmutableSet.of(ChangeEvent.cFormMove);

    @Override
    public void handleChange(final DataEvent<?> aE) {
        if (aE.getSrcView() == this) return;
        if (ignoredChanges.contains(aE.getId())) return;

        out.printf("WidgetNode.handleChange - refreshing\n");

        setProp(object); // reload properties
        firePropertySetsChange(null, this.getPropertySets());
    }



    public void setProp(final Object aObject) {
        removeProps();
        object = aObject;

        if (object instanceof FForm) {
            setFormProps((FForm) object);
        } else if (object instanceof Edge) {
            setEdgeProps((Edge) object);
        } else {
            out.println("Unknown object to set properties for " + aObject);
        }
    }

    private void removeProps() {
        for (PropertySet s : this.getPropertySets()) {
            sheet.remove(s.getName());
        }
    }

    protected void setFormProps(final FForm form) {
        Sheet.Set props = Sheet.createPropertiesSet();
        fillFormProps(props, form);
        sheet.put(props);
    }
    
    protected void fillFormProps(Sheet.Set props, final FForm form) {
        props.put(new IdProp(form));
        props.put(new FormTextProp(form));
        
        if (form instanceof WForm) {
            WForm wform = (WForm)form;
            int i=1;
            for (String alt : wform.getAltTokens()) {
                props.put(new RoStrProp("alternative_" + i++, alt ));
            }
            if (wform.getOldToken() != null) {
                props.put(new RoStrProp("oldToken", wform.getOldToken() ));
            }
        }
        
        props.put(new FormCommentProp(form));
        props.put(new RoStrProp("type", form.getType().name() ));
    }
    

    protected void setEdgeProps(final Edge aEdge) {
        Sheet.Set props;
        props = Sheet.createPropertiesSet();
        props.put(new IdProp(aEdge));
        props.put(new EdgeCommentProp(aEdge));
        sheet.put(props);

        int i = 1;
        for (Errorr error : aEdge.getErrors()) {
            props = new Sheet.Set();
            String name = "Error_" + i;
            props.setName(name);

            props.setDisplayName("Error " + i);
            props.setShortDescription(i + "th Error associated with this edge");


            props.put(new TagProp(error));
            props.put(new ErrorCommentProp(error));

            sheet.put(props);
            i++;
        }


    }

// =============================================================================
// Property classes
// TODO consolidate
// =============================================================================

    static class IdProp extends PropertySupport.ReadOnly<String> {
        final IdedElement element;

        IdProp(IdedElement aElement) {
            super("id", String.class, "id", "Element's id. Layer's local id + element's unique withing its layer.");
            element = aElement;
        }

        @Override
        public String getValue() {
            return element.getId().getIdStr();
        }
    }


    class TagProp extends PropertySupport<Integer> {
        private final Errorr error;
        private final ErrorTagset tagset;
        //private final List<String> tags;
        private Integer value;

        // todo better names tags::String, error.getTag()::ErrorSpec
        public TagProp(Errorr aError) {
            super("tag", Integer.class, "tag", "Tag (name) of the error type", true, !aError.getLayer().isReadOnly());

            error = aError;

            LLayer layer = (LLayer)error.getLayer();
            tagset = layer.getTagset(); // ErrorSpecs.INSTANCE.getErrorSpecs(layer);

            value =  tagset.getTags().indexOf( error.getTag() );
//            out.println("tag #1: " + tags.get(0) + " @ " + tags.get(0).hashCode());
//            out.println("tag #2: " + tags.get(1) + " @ " + tags.get(1).hashCode());
//            out.printf("tag=%s @ %s, val=%d\n", error.getTag(),  error.getTag().hashCode(), value);
            if (value == -1) value = 0;

            setValue("intValues", Cols.range(0, tagset.getTags().size()) );
            setValue("stringKeys", tagset.getStrTags().toArray(new String[0]) );
        }

        @Override
        public Integer getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            value = val;

            ErrorTag tag = tagset.getTags().get(val);
            out.printf("setValue: val=%d => tag=%s @ %s\n", val, tag, tag.hashCode());
            //error.setTag(tag);

            ((LLayer)getLayer()).errorTagChange(error, tag, WidgetNode.this, null);
        }
    }


    public static class RoStrProp extends PropertySupport.ReadOnly<String> {
        final String str;

        public RoStrProp(String aName, String aValue) {
            super(aName, String.class, aName, "dd");
            this.str = aValue;
        }

        @Override
        public String getValue() {
            return str;
        }
    }

    public static class StrProp extends PropertySupport.ReadWrite<String> {
        String str;

        public StrProp(String aName, String aValue) {
            super(aName, String.class, aName, "");
            this.str = aValue;
        }

        @Override
        public String getValue() {
            return str;
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            str = val;
            out.println("setting " + val);
        }
    }

    public class FormTextProp extends PropertySupport<String> {
        private final FForm form;

        FormTextProp(FForm aForm) {
            super("word", String.class, "word", "The actual text of the form", true, !aForm.getLayer().isReadOnly());
            form = aForm;
        }


        @Override
        public String getValue() {
            return form.getToken();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            //form.setComment(val); -- TODO + should respond
            ((FormsLayer<?>)getLayer()).formEdit(form, val, WidgetNode.this, null);
        }
    }

    class FormCommentProp extends PropertySupport<String> {
        private final FForm form;

        FormCommentProp(FForm aForm) {
            super("comment", String.class, "comment", "Any comment for the element", true, !aForm.getLayer().isReadOnly());
            form = aForm;
        }

        @Override
        public String getValue() {
            return form.getComment() != null ? form.getComment() : "";
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            form.setComment(val);
            ((FormsLayer<?>)getLayer()).formChange(form, WidgetNode.this, null);
        }
    }

    class EdgeCommentProp extends PropertySupport<String> {
        private final Edge edge;

        EdgeCommentProp(Edge aEdge) {
            super("comment", String.class, "comment", "Any comment for the element", true, !aEdge.getLayer().isReadOnly());
            edge = aEdge;
        }

        @Override
        public String getValue() {
            return edge.getComment() != null ? edge.getComment() : "";
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            edge.setComment(val);
            ((LLayer)getLayer()).edgeChange(edge, WidgetNode.this, null);
        }
    }

    public class ErrorCommentProp extends PropertySupport<String> {
        private final Errorr error;

        ErrorCommentProp(Errorr aError) {
            super("comment", String.class, "comment", "Any comment for the element", true, !aError.getLayer().isReadOnly());
            error = aError;
        }

        @Override
        public String getValue() {
            return error.getComment();
        }

        @Override
        public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            error.setComment(val);
            ((LLayer)getLayer()).errorAttrChange(error, WidgetNode.this, null);
        }
    }

}
