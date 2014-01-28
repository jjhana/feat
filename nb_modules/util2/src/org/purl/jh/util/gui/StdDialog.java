package org.purl.jh.util.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
 * Subclassing object must call initUi and should override createMainPanel(),
 * validateInput and possibly retrieveInput
 *
 * Usage:
 * StdDialog dlg = new StdDialog(...);
 * if (!dlg.accepted()) return;
 * // process results
 *
 *
 * @author Jirka
 */
public class StdDialog extends JDialog {
    public enum PressedButton {
        nothing, ok, cancel;
        public boolean ok()      {return this == ok;}
        public boolean cancel()  {return this == cancel;}
        public boolean nothing() {return this == nothing;}
    
    };

    protected PressedButton mPressedButton = PressedButton.nothing;
    protected JButton mOkButton, mCancelButton, mHelpButton;
    
// =============================================================================    
    

    /** Creates a new instance of WhatToDoDlg */
    public StdDialog(Frame aOwner, String aTitle, boolean aModal) {
        super(aOwner, aTitle, aModal);  // Set dialog frame and title
    }

    public StdDialog(Dialog aOwner, String aTitle, boolean aModal) {
        super(aOwner, aTitle, aModal);  // Set dialog frame and title
    }

    /**
     * Shows the dialog and checks if ok was pressed.
     * 
     * @return true if ok button was pressed, false otherwise.
     */
    public boolean accepted() {
        setVisible(true);
        return getPressedButton().ok();
    }
    
    public PressedButton getPressedButton() {return mPressedButton;}
    
// =============================================================================    
// Implementation 
// =============================================================================    

// -----------------------------------------------------------------------------    
// UI
// -----------------------------------------------------------------------------    

    protected void initUi() {
        setLayout(new BorderLayout());
        
        getContentPane().add(createButtonBox(), BorderLayout.SOUTH);
        getContentPane().add(createMainPanel(), BorderLayout.CENTER);
        
        pack();
    }

    protected Box createButtonBox() {
        final JLayeredPane layeredPane = getLayeredPane();

        // --- Actions ---
        SAction okAction     = new SAction("OK")     {public void a() {cmdOk();}};
        SAction cancelAction = new SAction("Cancel") {public void a() {cmdCancel();}};
        SAction helpAction   = new SAction("Help")   {public void a() {cmdHelp();}};

        ActionMap actionMap = layeredPane.getActionMap();
        actionMap.put("actOk",     okAction);
        actionMap.put("actCancel", cancelAction);
        actionMap.put("actHelp",   helpAction);

        // --- Keybindings ---
        InputMap inputMap = layeredPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),  "actOk");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "actCancel");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),     "actHelp");
        
        // --- Buttons ---
        mOkButton     = new JButton( okAction );
        mCancelButton = new JButton( cancelAction );
        mHelpButton   = new JButton( helpAction );
        
        Box buttonbox = Box.createHorizontalBox();
        buttonbox.add(Box.createHorizontalGlue());     // stretchy space
        buttonbox.add(mOkButton); 
        buttonbox.add(Box.createHorizontalStrut(10)); 
        buttonbox.add(mCancelButton);
        buttonbox.add(Box.createHorizontalStrut(10)); 
        buttonbox.add(mHelpButton);
        buttonbox.add(Box.createHorizontalStrut(10)); 

        
        return buttonbox;
    }

    /**
     * Override this
     */
    protected JPanel createMainPanel() {
        return new JPanel();
    }

    protected JComponent helpPanel(String aHelp) {
        JTextArea textArea = new JTextArea(aHelp);
        textArea.setEditable(false);
        textArea.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        return textArea;
    }
    
// -----------------------------------------------------------------------------    
// 
// -----------------------------------------------------------------------------    

    /**
     * Validates input, and if it is okay, closes the dialog.
     */
    protected void cmdOk() {
        if (! validateInput()) return;
        mPressedButton = PressedButton.ok;
        retrieveInput();
        setVisible(false);
    }

    /**
     * Closes the dialog (without any validation).
     */
    protected void cmdCancel() {
        mPressedButton = PressedButton.cancel;
        setVisible(false);
    }

    protected void cmdHelp() {
    }
    
    /**
     * Checks the input, displaying error message if it is incorrect. 
     * By default, does nothing and returns true.
     *
     * @return true if the input is correct, false otherwise
     */
    protected boolean validateInput() {
        return true;
    }

    /**
     * By default does nothing.
     */
    protected void retrieveInput() {
    }

}
