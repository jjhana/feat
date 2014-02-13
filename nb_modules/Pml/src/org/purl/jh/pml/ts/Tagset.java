package org.purl.jh.pml.ts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.purl.jh.pml.Data;
import org.purl.jh.util.err.Err;
import org.purl.jh.util.str.StringPair;
import org.purl.jh.util.str.Strings;

/**
 *
 * @todo inject StringId -> Tag map, add StringSpec <--> Tag reader/writer 
 *
 * @author jirka
 */
public abstract class Tagset<T extends Tag<?>> extends Data<T> {
    // standard properties
    public final static String cDomain = "domain";
    public final static String cLg     = "lg";        /* Use ISO 2 character values, if more lgs are required, use , as a separator */
    public final static String cType   = "type";      /* domain specific type (e.g. atomic, positional, ..) for domain morph */
    public final static String cDescr  = "descr";     /* domain specific type (e.g. atomic, positional, ..) for domain morph */
    // todo compatibility


    // standard domain property values (used as ids)
    public final static String cDom_Morph  = "morph";
    public final static String cDom_Syntax = "syntax";

    // standard type property values
    public final static String cAtomic     = "atomic";
    public final static String cPositional = "positional";
    public final static String cMap        = "map";        // category -> atomic value
    public final static String cAvm        = "avm";        // category -> atomic value/avm

// -----------------------------------------------------------------------------
// Meta properties
// -----------------------------------------------------------------------------

    private String id;      // uri or uuid, version can be added - separated by ":"
    private String descr;

    
    /** id without version */
    private String justId;
    
    /** tagset version (if any) */
    private String version;

    /** Fields for standard properties for fast access */
//    /** Can be null */
//    private String domain;
//    /** Can be null */
//    private String type;
//    /** Can be null */
//    private String lg;

    /** Tagset properties */
    private final Map<String,String> properties = new HashMap<String,String>();
    /** Unmodifiable view of tagset properties */
    private final Map<String,String> propertiesConst;


    public Tagset() {
        propertiesConst = Collections.unmodifiableMap(properties);
    }

    public Tagset(String id, String descr, String domain, String type, String lg) {
        this();
        
        this.id = id;
        this.descr = descr;

        final StringPair sp = Strings.splitIntoTwoB(id, ':');
        justId  = sp.mFirst;
        version = sp.mSecond;

//        this.domain = domain;
//        this.type = type;
//        this.lg = lg;
        
        if (domain != null) properties.put(cDomain, domain);
        if (type   != null) properties.put(cType,  type);
        if (lg     != null) properties.put(cLg,    lg);

    }

    /**
     * Override if an object different than Tag is used.
     *
     * @param id
     * @param descr
     * @return
     *
     * @todo what if the tag object requires more parameters during creation?
     * Should we pass some generic Object ... aParams?
     */
    public abstract T createTag(String id, String descr, Object ... aParam);

// -----------------------------------------------------------------------------
// Meta properties
// -----------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    /** Id without version suffix (if any) and the colon separator */
    public String getJustId() {
        return justId;
    }

    /** Version suffix (if any). Empty string if not version was specified as part of the id. */
    public String getVersion() {
        return version;
    }

    public String getDescr() {
        return descr;
    }

    public String getDomain() {
        return properties.get(cDomain);
    }

    public String getType() {
        return properties.get(cType);
    }

    public boolean isPositional() {
        return cPositional.equals(getType());
    }

    public PositionalTagset asPositional() {
        return (PositionalTagset)this;
    }

    public boolean isAtomic() {
        return cAtomic.equals(getType());
    }

    public AtomicTagset<T> asAtomic() {
        return (AtomicTagset<T>)this;
    }

    public String getLg() {
        return properties.get(cLg);
    }

    public String getProperty(String aKey) {
        return properties.get(aKey);
    }

    public String setProperty(String aKey, String aValue) {
        return properties.get(aKey);
    }

    /**
     * Returns unmodifiable collection
     * @return
     */
    public Map<String,String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Tagset{id=" + id + '}';
    }



// --- -------------------------------------------------------------------------
    public abstract T getTag(String aId);

    public abstract List<String> getStrTags();

    public abstract List<T> getTags();

//    public abstract String getUnknownTag();

    public abstract T getUnknownTag();

    public boolean isLegal(String aTag) {
        return getTags().contains(aTag);
    }

    /**
     * 
     * @param aTag
     * @param aLg
     * @param aMaxLength
     * @return
     */
    public String describe(String aTag, String aLg, int aMaxLength) {
        return "test " + aTag ;
    }

    /**
     * Adds a tag to this tagset.
     * The default implementation directly adds the tag to the list
     * returned by {@link #getTag()}. Override to change this behavior.
     *
     * @param aTag tag to add
     * @return this tagset to allow operation chaining
     */
    @Override
    public void add(T aTag) {
        super.add(aTag);
        Err.iAssert(aTag.getTagset() == this, "Tag %s does not belong to this tagset (%s)", aTag, getId());
        getTags().add(aTag);
        //return this;
    }

    /**
     * Adds tags to this tagset.
     * The default implementation directly adds the tags to the list
     * returned by {@link #getTag()}. Override to change this behavior.
     *
     * @param aTags tags to add
     * @return this tagset to allow operation chaining
     */
    public Tagset<T> addAll(List<? extends T> aTags) {
        getTags().addAll(aTags);
        return this;
    }

    /**
     * Convenience method creating a tag with given id and description
     * and adding it to the set of tags of this tagset.
     *
     * @param aTagIds
     * @return this tagset to allow operation chaining
     * @see #createTag(id, descr)
     */
    public Tagset<T> add(String aTagId, String aTagDescr) {
        add( createTag(aTagId, aTagDescr) );
        return this;
    }

    /**
     * Convenience method creating a tag with given id and description
     * and adding it to the set of tags of this tagset.
     *
     * @param aTagIds
     * @return this tagset to allow operation chaining
     * @see #createTag(id, descr)
     */
    public Tagset<T> add(String aTagId, String aTagDescr, Object ... aParams) {
        add( createTag(aTagId, aTagDescr, aParams) );
        return this;
    }

    /**
     * Convenience method adding tags directly specified only as ids.
     * Each tag's description is identical to its id.
     *
     * @param aTagIds
     * @return this tagset to allow operation chaining
     * @see #createTag(id, descr)
     */
    public Tagset<T> addAll(String ... aTagIds) {
        for (String tagId : aTagIds) {
            add( createTag(tagId, tagId) );
        }
        return this;
    }


    /**
     * Removes a tag from the tagset.
     * The default implementation directly removes the tag from the list
     * returned by {@link #getTag()}. Override to change this behavior.
     *
     * @param aTag tag to remove
     * @return this tagset to allow operation chaining
     */
    public Tagset<T> remove(T aTag) {
        getTags().remove(aTag);
        return this;
    }



}
