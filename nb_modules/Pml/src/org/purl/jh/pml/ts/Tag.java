package org.purl.jh.pml.ts;

import java.util.HashMap;
import java.util.Map;
import org.purl.jh.pml.AbstractElement;
import org.purl.jh.util.col.Mapper;

/**
 *
 * @author jirka
 */
public class Tag<T extends Tagset<?>> extends AbstractElement {

    public static <Tx extends Tag<?>> Mapper<Tx,String> tag2id() {
        return (Mapper<Tx,String>) tag2id;
    }

    public final static Mapper<? extends Tag<?>,String> tag2id = new Mapper<Tag<?>,String>() {
        @Override
        public String map(Tag aOrigItem) {
            return aOrigItem.getId();
        }
    };

//    public static <AT extends Tag, Tx extends Tag> List<T> toTags(final Tagset<T> aTagset, final List<String> aIds, final List<String> aDescr) {
//        final List<Tx> tags = new ArrayList<T>();
//        for (int i = 0; i < aIds.size(); i++) {
//            tags.add(aTagset.createTag(aIds.get(i),aDescr.get(i)));
//        }
//        return tags;
//    }

//    public static <Tx extends Tag> List<T> toTags(final Tagset<T> aTagset, final String ... aIds) {
//        final List<String> ids = Arrays.asList(aIds);
//        return toTags(aTagset, ids, ids);
//    }

    private final T tagset;

    private String id;
    private String descr;
    private final Map<String,Object> properties = new HashMap<>();

    public Tag(T aTagset, String id, String descr) {
        setParent(aTagset);
        this.tagset = aTagset;
        this.id = id;
        this.descr = descr;

//        if (id.equals("?")) {
//            Exception e = new IOException();
//            e.fillInStackTrace();
//            System.out.println("Creating ??? --------------------------");
//            System.out.println(aTagset);
//            System.out.println("---");
//            e.printStackTrace(System.out);
//            System.out.println("----------------------------------------");
//        }

    }

    public Tagset<?> getTagset() {
        return tagset;
    }

    public String getId() {
        return id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Tag{" + this.getClass() + " tagset=" + id + " ( " + tagset.hashCode() + ")" +
                ", id=" + id + ", descr=" + descr + ", properties=" + properties + '}';

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag<?> other = (Tag<?>) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
