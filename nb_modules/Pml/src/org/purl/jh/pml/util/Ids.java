package org.purl.jh.pml.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.purl.jh.pml.Id;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.util.col.Cols;
import org.purl.jh.util.col.Mapper;
import org.purl.jh.util.col.MappingIterable;
import org.purl.jh.util.col.MappingList;
import org.purl.jh.util.str.Strings;

/**
 *
 * @author jirka
 */
public class Ids {
    @SuppressWarnings("unchecked")
    private final static Mapper E_2_ID = new E2Id<>();
    
    private static class E2Id<E extends IdedElement> implements Mapper<E, Id> {
        @Override
        public Id map(E aOrigItem) {
            return aOrigItem.getId();
        }
    }

    @SuppressWarnings("unchecked")
    private final static Mapper E_2_IDSTR = new E2IdStr<>();
    
    private static class E2IdStr<E extends IdedElement> implements Mapper<E, String> {
        @Override
        public String map(E aOrigItem) {
            return aOrigItem.getId().getIdStr();
        }
    }
 
    /**
     * Creates a new id for an element to be added into a collection.
     * The id is generated in the pattern of the existing ids (precisely,
     * in the pattern of the id of the last element in aExistingElements).
     *
     * @param aExistingElements
     * @param aZeroId id to use if aExistingElements is empty
     * @return the new id
     */
    public static String createNewId(final List<? extends IdedElement> aExistingElements, String aZeroId) {
        if (aExistingElements.isEmpty()) {
            return aZeroId;
        } else {
            String lastId = Cols.getB(aExistingElements, 0).getId().getIdStr();
            List<String> ids = e2idStrs(aExistingElements);
            return Strings.findNextId(lastId, ids);
        }
    }

    /**
     * Mapper from an IdedElement to its id string
     * @param <E> type of the ided element
     * @return element-to-id-string mapper
     * @see Id#getIdStr()
     */
    public static <E extends IdedElement> Mapper<E, String> e2idstr() {
        return E_2_IDSTR;
    }

    /**
     * Maps a list of elements to their its id.
     * <p>
     * HO: aElements.select(e -> e.getId().getIdStr())
     * @param els collection of elements to map
     * @return
     */
    public static <T extends IdedElement> List<String> e2idStrs(final List<T> els) {
        return new MappingList<>(els, Ids.<T>e2idstr());
    }

    /**
     * Maps a collection of elements to their ids.
     * <p>
     * HO: aElements.select(e -> e.getId().getIdStr())
     * @param els collection of elements to map
     * @return
     */
    public static <T extends IdedElement> Iterable<String> e2idStrs(final Iterable<T> els) {
        return new MappingIterable<>(els, Ids.<T>e2idstr());
    }

    /**
     * Maps a list of elements to their its id.
     * <p>
     * HO: aElements.select(getId)
     * @param aElements
     * @return
     */
    public static <T extends IdedElement> List<Id> e2ids(final List<T> els) {
        return new MappingList<>(els, Ids.<T>e2id());
    }

    /**
     * Maps a collection of elements to their ids.
     * <p>
     * HO: aElements.select(getId)
     * @param aElements
     * @return
     */
    public static <T extends IdedElement> Iterable<Id> e2ids(final Iterable<T> els) {
        return new MappingIterable<>(els, Ids.<T>e2id());
    }

    /**
     * Mapper from an IdedElement to its id
     * @param <E> type of the ided element
     * @return element-to-id mapper
     */
    public static <E extends IdedElement> Mapper<E, Id> e2id() {
        return E_2_ID;
    }

    /**
     * Sorts a list of element in place by their ids.
     * @param <T>
     * @param els
     * @return
     */
    public static <T extends IdedElement> List<T> sortByIds(List<T> els) {
        Collections.sort(els);
        return els;
    }

    /**
     * Sorts a list of element in place by their ids.
     * @param <T>
     * @param els
     * @return
     */
    public static <T extends IdedElement> List<T> sortCopyByIds(Iterable<T> els) {
        final List<T> list = Lists.newArrayList(els);
        Collections.sort(list);
        return list;
    }
}
