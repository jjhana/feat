package org.purl.jh.util.gui;

import java.awt.event.ActionEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import org.purl.jh.util.err.Err;

/**
 * Action object making it easy to define custom actions inline via anonymous
 * subclasses.
 * 
 * @author Jirka
 */
public class SAction extends AbstractAction {
    int mType = 0;
    
    public SAction() {}

    public SAction(String aName) {
        super(aName);
    }

    public SAction(String aName, boolean aEnabled) {
        super(aName);
        setEnabled(aEnabled);
    }

    public SAction(int aType) {
        mType = aType;
        setEnabled(false);
    }

    public SAction(String aName, int aType) {
        super(aName);
        setEnabled(false);
    }


    public SAction setAccel(int aKeyCode) {
        return setAccel(aKeyCode, ActionEvent.CTRL_MASK);
    }

    public SAction setAccel(int aKeyCode, int aModifiers) {
        KeyStroke ks = KeyStroke.getKeyStroke(aKeyCode, aModifiers);
        putValue(Action.ACCELERATOR_KEY, ks);
        return this;
    }
    
    public int getType() {
        return mType;
    }
    
// -----------------------------------------------------------------------------
// Performing the action
// -----------------------------------------------------------------------------
    
    /**
     * If your action does not need the ActionEvent obj, redefine this fnc.
     * The form of this function is such that action definitions are short (single letter name, no params).
     */
    public void a() { throw new UnsupportedOperationException("");}

    /**
     * @todo how about doint this via ActionEvent 
     */
    public void a(Object aObject) {throw new UnsupportedOperationException("");}

    public void a(Object aObject1, Object aObject2) {throw new UnsupportedOperationException("");}

    public void a(Object ... aObjects) {throw new UnsupportedOperationException("");}
    
    /**
     * If your action does need the ActionEvent obj, redefine this fnc.
     */
    public void actionPerformed(ActionEvent e) {a();}

// -----------------------------------------------------------------------------
// 
// -----------------------------------------------------------------------------
    
    /**
     * Should the toolbar text be shown?
     */
    public boolean showTBText() {
        return (Boolean) getValue("showTBText") == Boolean.TRUE;
    }

    public SAction setIcon(String aFileName) {
        URL url = ClassLoader.getSystemResource(aFileName);
        Err.assertE(url != null, "No url found for icon %s", aFileName);
        putValue(Action.SMALL_ICON, new ImageIcon(url));
        return this;
    }
    
    public SAction setEnabledX(boolean aEnabled) {
        super.setEnabled(aEnabled);
        return this;
    }    
}



//public class EnablingAction extends AbstractAction {
//    Action mMainAction;
//
//    public EnablingAction(Action aMainAction) {
//        super("Enable " + aMainAction.getValue(Action.NAME) , null);
//        putValue(SHORT_DESCRIPTION, "Enables " + aMainAction.getValue(Action.NAME) );
//        mMainAction = aMainAction;
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        JCheckBoxMenuItem mi = (JCheckBoxMenuItem)(e.getSource());
//        boolean selected = mi.isSelected();
//        mMainAction.setEnabled(selected);
//    }
//}

//protected JMenu createAbleMenu() {
//    JMenu ableMenu = new JMenu("Action State");
//
//    for (Action mainAction : mActions.values()) {
//        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem( new EnablingAction(mainAction) );
//        menuItem.setSelected(true);
//        ableMenu.add(menuItem);
//    }
//
//    return ableMenu;
//}
//
//
