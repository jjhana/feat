package org.purl.jh.util.gui;

import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import org.purl.jh.util.err.Err;


/**
 * A la ButtonGroup.
 * Each button can be referenced by its index or key.
 * 
 *
 * @todo do better
 * @todo should work with models not with buttons ??
 * @todo a single action? But then how to specify label, ..... But what if model shared anyway (e.g. toolbar & menu)
 *
 * @author Jiri
 *
 * Buttons' model has to work with ButtonGroup (JToggleButton or JRadioButtonMenuItem both have such models)
 * 
 */
public class Radios<T> implements ItemListener {
    ButtonGroup mGroup = new ButtonGroup();     // cannot be dropped, because buttons work with it 
    
    protected List<AbstractButton> mButtons = new ArrayList<AbstractButton>();      // duplication of mGroup.buttons
    protected Map<T, AbstractButton> mId2Button = new HashMap<T, AbstractButton>();
    protected Map<T, ButtonModel> mId2Model = new HashMap<T, ButtonModel>();
    protected Map<ButtonModel, T> mModel2Id = new HashMap<ButtonModel,T>();

    // --- Card support ---
    protected CardLayout mCards = null;     // optional cards, each connected with one button
    protected JComponent mParent = null;

// =============================================================================
//
// =============================================================================    

    /**
     * Creates a new instance of Radios.
     * Associates each radio with a card.
     * @param aComponent component with cards with the same ids as radios
     */
    public Radios(JComponent aComponent) {
        mParent = aComponent;
        mCards = (CardLayout) mParent.getLayout();
    }

    /**
     * Creates a new instance of Radios.
     * Does not associate radios with a cards.
     */
    public Radios() {
    }

    /**
     * The order of ids determines the numeric index given to each button.
     * For enums use values(): toggleButtons(enumType.values())
     */
    public static <X> Radios<X> toggleButtons(X ... aIds) {
        Radios<X> radios = new Radios<X>();
        
        for (X id : aIds) {
            radios.add(id, new JToggleButton());
        }
        return radios;
    }

    
// =============================================================================
//
// =============================================================================    

    /**
     * @param aActionKeys the order must correspond to the numeric ordering of buttons
     * @throws IException if the number of actions and buttons is not equal
     */
    public void setActions(Map<String,? extends Action> aActions, String ... aActionKeys) {
        Err.assertE(aActionKeys.length == getButtonCount(), "The # of actions must correspond to the # of buttons");

        for (int i = 0; i < aActionKeys.length; i++) {
            Action act = aActions.get(aActionKeys[i]);
            Err.assertE(act != null,  "Wrong action id (%s)", aActionKeys[i]);

            mButtons.get(i).setAction(act);
        }
    }

    public void setMargin(Insets aInsets) {
        for (AbstractButton b : mId2Button.values()) {
            b.setMargin(aInsets);
        }
    }
    
    public void itemStateChanged(ItemEvent aEv) {
        if (aEv.getStateChange() != ItemEvent.SELECTED) return;

        ButtonModel model = (ButtonModel) aEv.getItemSelectable();
        T id = mModel2Id.get(model);

        if (mCards != null) mCards.show(mParent, id.toString());
    }
    
    // @todo esc = cancel
    
    public AbstractButton add(T aId, AbstractButton aButton) {
        mGroup.add(aButton);
        mButtons.add(aButton);
        mId2Button.put(aId, aButton);
        ButtonModel model = aButton.getModel();
        mId2Model.put(aId, model);
        mModel2Id.put(model, aId);
        model.addItemListener(this);
        return aButton;
    }

    public AbstractButton get(int aIdx) {
        return mButtons.get(aIdx);
    }

    public AbstractButton get(T aId) {
        return mId2Button.get(aId);
    }
    
    /**
     * Enables only the buttons with the specified ids.
     */
    public void enableOnly(T ... aIds) {
        enableOnly(Arrays.asList(aIds));
    }
    
    /**
     * Enables only the buttons with the specified ids.
     */
    public void enableOnly(Iterable<T> aIds) {
        for (ButtonModel model : mId2Model.values()) 
            model.setEnabled(false);

        for (T id : aIds) 
            mId2Model.get(id).setEnabled(true);
    }

    public void select(T ... aIds) {select(Arrays.asList(aIds));}

    /**
     * Select the first enabled button.
     */
    public void select(Iterable<T> aIds) {
        for (T id : aIds) {
            ButtonModel model = mId2Model.get(id);
            if (model.isEnabled()) {
                model.setSelected(true);
                break;
            }
        }
    }
    
    public void remove(String aId) {
        // @todo
    }

    /**
     * Returns all the buttons that are participating in
     * this group.
     * @return an <code>Enumeration</code> of the buttons in this group
     */
    public Enumeration<AbstractButton> getElements() {
        return mGroup.getElements();
    }

//    /**
//     * Sets the selected value for the <code>ButtonModel</code>.
//     * Only one button in the group may be selected at a time.
//     * @param m the <code>ButtonModel</code>
//     * @param b <code>true</code> if this button is to be
//     *   selected, otherwise <code>false</code>
//     */
//    public void setSelected(T aId, boolean aSelection) {
//        mGroup.setSelected(mId2Model.get(aId), aSelection);
//    }

    /**
     * Returns whether a <code>ButtonModel</code> is selected.
     * @return <code>true</code> if the button is selected,
     *   otherwise returns <code>false</code>
     */
    public boolean isSelected(T aId) {
        return mId2Model.get(aId).isSelected();
    }

    public T getSelectedButtonId() {
        // @todo improve
        for (Map.Entry<T,ButtonModel> e : mId2Model.entrySet()) {
            if (e.getValue().isSelected()) return e.getKey();
        }
        return null;
    }
    
    
    /**
     * Returns the number of buttons in the group.
     * @return the button count
     */
    public int getButtonCount() {
        return mId2Model.size();
    }
}
