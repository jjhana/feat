package org.purl.jh.util.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A simple DocumentListener adapter routing all responses to a single function.
 */
public abstract class SimpleDocListener implements DocumentListener {
    public abstract void textUpdated();

    @Override
    public void insertUpdate(DocumentEvent e) {
        textUpdated();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textUpdated();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        textUpdated();
    }
}
