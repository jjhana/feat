package cz.cuni.utkl.czesl.data.layerx;

import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerl.Sentence;
import org.purl.jh.util.Logger;

/**
 * Position in a structure of forms, e.g. for insertion of another form.
 *
 * Note the anchor should be constructed in a way that the form and the relevant place
 * are within the same sentence (i.e form.getParent() == sentence).
 * However, when the anchor is supposed to be in a sentence without forms,
 *
 * after:     .. form | .......
 * before:    ....... | form ..
 *
 * @author Jirka dot Hana at gmail dot com
 */
public final class Position {
    private final static Logger log = Logger.getLogger(Position.class.getName());

    /** Anchoring form, if possible it is within the {@link #sentence} */
    private final LForm form;

    /** This position precedes the {@link #form}. */
    private final boolean posBeforeForm;

    /** Sentence containing the point */
    private final Sentence sentence;

    private Position(LForm aForm, boolean aPosBeforeForm, Sentence aSentence) {
        form = aForm;
        posBeforeForm = aPosBeforeForm;
        sentence = aSentence;
    }

    /**
     * The position of a form.
     *
     * todo this is inconsistent with the rest. Here it is form xor sentence. Choose one or the other approach.
     */
    public static Position of(final LForm aForm) {
        final Sentence s = aForm.getParent();

        if (s.size() > 1) {
            final int idx = s.col().indexOf(aForm);
            //log.log(Level.FINER, "Position.find: form={0}, idx={1}, s={2}", aForm, idx, s.getChildren());

            if (idx > 0) {
                return Position.after(s.col().get(idx-1));
            }
            else {
                return Position.before( s.col().get(1));
            }
        }
        else {
            return new Position(null, true, s);
        }
    }

    /**
     * Position is before the form.
     *
     * @param aForm the form form following the position
     */
    public static Position before(LForm aForm) {
        return before(aForm, aForm.getParent());
    }

    /**
     * Position is before the form.
     *
     * @param aForm the form form following the position
     */
    public static Position before(LForm aForm, Sentence aSentence) {
        return new Position(aForm, true, aSentence);
    }

    /**
     * Position is after the form.
     *
     * @param aForm the form form preceding the position
     */
    public static Position after(LForm aForm) {
        return after(aForm, aForm.getParent());
    }

    /**
     * Position is after the form.
     *
     * @param aForm the form form preceding the position
     */
    public static Position after(LForm aForm, Sentence aSentence) {
        return new Position(aForm, false, aSentence);
    }

    /**
     * Is the position before the linked form?
     * <pre> .. | getForm() .. </pre>
     */
    public boolean isBeforeForm() {
        return posBeforeForm;
    }

    /**
     * Is the position after the linked form?
     * <pre> .. getForm() | .. </pre>
     */
    public boolean isAfterForm() {
        return !posBeforeForm;
    }

    public LForm getForm() {
        return form;
    }

    public Sentence getSentence() {
        return sentence;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Position other = (Position) obj;
        if (this.form != other.form && (this.form == null || !this.form.equals(other.form))) {
            return false;
        }

        return this.posBeforeForm == other.posBeforeForm && ( this.sentence == null || !this.sentence.equals(other.sentence));
    }

    /**
     * Convenience method checking if the anchor's layer is read-only.
     * @return
     */
    public boolean layerRO() {
        return form.getLayer().isReadOnly();
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.form != null ? this.form.hashCode() : 0);
        hash = 19 * hash + (this.posBeforeForm ? 1 : 0);
        hash = 19 * hash + (this.sentence != null ? this.sentence.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        if (posBeforeForm) {
            return "X " + form + "(" + (sentence == null ? "null" : sentence.getId() ) + ")" ;
        }
        else {
            return form + " X (" + (sentence == null ? "null" : sentence.getId() ) + ")";
        }
    }
}
