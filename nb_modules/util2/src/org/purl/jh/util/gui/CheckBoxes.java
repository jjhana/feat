package org.purl.jh.util.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;



/**
 *
 * @author Jiri
 */
public class CheckBoxes extends JPanel {
    protected Map<String, JCheckBox> mButtons = new HashMap<String, JCheckBox>();
    
    /**
     * Creates a new instance of Radios 
     */
    public CheckBoxes() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    public CheckBoxes(Iterable<String> aIds) {
        addAll(aIds);
    }

// -----------------------------------------------------------------------------    
//
// -----------------------------------------------------------------------------    
    
    // @todo esc = cancel
    public CheckBoxes add(String aId) {
        return add(aId, aId);
    }
    
    public CheckBoxes add(String aId, String aLabel) {
        JCheckBox checkbox = new JCheckBox(aId);
        mButtons.put(aId,  checkbox);
        add(checkbox);
        return this;
    }
    
    public CheckBoxes addAll(Iterable<String> aIds) {
        for (String id : aIds)
            add(id);
        return this;
    }
    

    public void remove(String aId) {
        // @todo
    }

// -----------------------------------------------------------------------------    
// Selection
// -----------------------------------------------------------------------------    
    
    /**
     * @return <code>true</code> if the button is selected; <code>false</code> otherwise.
     */
    public boolean isSelected(String aId) {
        return mButtons.get(aId).isSelected();
    }

    public String getSelectedButtonIds() {
        for (Map.Entry<String,JCheckBox> e : mButtons.entrySet()) {
            if (e.getValue().isSelected()) return e.getKey();
        }
        return null;
    }

    
    /**
     * @param aId 
     * @param aSelection <code>true</code> if this button is to be
     *   selected, otherwise <code>false</code>
     */
    public void setSelected(String aId, boolean aSelection) {
        mButtons.get(aId).setSelected(aSelection);
    }

    public void select(String ... aIds) {select(Arrays.asList(aIds));}

    /**
     * Select the specified buttons.
     */
    public void select(Iterable<String> aIds) {
        for (String id : aIds) {
            AbstractButton button = mButtons.get(id);
            if (button.isEnabled()) 
                button.setSelected(true);
        }
    }


    
    
// -----------------------------------------------------------------------------    
//
// -----------------------------------------------------------------------------    

    /**
     * Returns all the buttons that are participating in this group.
     * @return an <code>Collection</code> of the buttons in this group
     */
    public Collection<JCheckBox> getButtons() {
        return mButtons.values();
    }

    
// =============================================================================
// Getu into parent
// =============================================================================
    
    /**
     * Returns the number of buttons in the group.
     * @return the button count
     */
    public int getButtonCount() {
        return mButtons.size();
    }

    public void setEnabled(boolean enabled) {
        // @todo if checkboxes distinguish enabling, they must remember it
        for (AbstractButton button : mButtons.values()) 
            button.setEnabled(enabled);
    }

    // as in radios
    public void enableOnly(Iterable<String> aIds) {
        for (AbstractButton button : mButtons.values()) 
            button.setEnabled(false);

        for (String id : aIds) 
            mButtons.get(id).setEnabled(true);
    }
    
}
