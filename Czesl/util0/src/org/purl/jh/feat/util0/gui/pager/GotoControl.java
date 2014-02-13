package org.purl.jh.feat.util0.gui.pager;

import java.util.ArrayList;
import java.util.List;

/**
 * This composite zoom control:
 * <ul>
 *   <li>Listens to changes of individual controls
 *   <li>Modifies the model directly
 *   <li>listens to the model's changes
 * </ul>
 *
 * @author jirka
 */
public class GotoControl extends javax.swing.JToolBar implements CurListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(GotoControl.class);
    private final static java.util.ResourceBundle bundle = org.openide.util.NbBundle.getBundle(GotoControl.class);

    private int cur = -1;
    private int size = -1;

    public GotoControl() {
        initComponents();
        setRollover(true);
        setBorderPainted(false);
        //this.firstParaButton.setBorderPainted(false);
    }

    public void curChanged(int aIdx) {
        if (aIdx != cur) {
            cur = aIdx;
            updateUi();
        }
    }

    public void sizeChanged(int aNewSize) {
        if (aNewSize != size) {
            size = aNewSize;
            updateUi();
        }
    }

    private void updateUi() {
        boolean nonEmpty = size > 0;
        // assert size <= 0 => cur = -1

        firstParaButton.setEnabled(cur > 0);
        prevParaButton.setEnabled(cur > 0);
        nextParaButton.setEnabled(cur+1 < size && nonEmpty);
        lastParaButton.setEnabled(cur+1 < size && nonEmpty);
        this.curParaText.setText("" + (cur+1));
        this.totalParasLabel.setText("/" + size);
    }

    private void initComponents() {
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        //setLayout(new FlowLayout(FlowLayout.LEADING));
        //setBorder(null);
    
        firstParaButton = new javax.swing.JButton();
        prevParaButton = new javax.swing.JButton();
        curParaText = new javax.swing.JTextField();
        totalParasLabel = new javax.swing.JLabel();
        nextParaButton = new javax.swing.JButton();
        lastParaButton = new javax.swing.JButton();

        //setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        //setMaximumSize(new java.awt.Dimension(202, 24));
        //setMinimumSize(new java.awt.Dimension(202, 24));
        //setPreferredSize(new java.awt.Dimension(202, 24));

        firstParaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/purl/jh/feat/util0/gui/pager/go-first.png"))); // NOI18N
        firstParaButton.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.firstParaButton.text")); // NOI18N
        firstParaButton.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.firstParaButton.toolTipText")); // NOI18N
        firstParaButton.setFocusable(false);
        firstParaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        firstParaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        firstParaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstParaButtonActionPerformed(evt);
            }
        });
        add(firstParaButton);

        prevParaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/purl/jh/feat/util0/gui/pager/go-previous.png"))); // NOI18N
        prevParaButton.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.prevParaButton.text")); // NOI18N
        prevParaButton.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.prevParaButton.toolTipText")); // NOI18N
        prevParaButton.setFocusable(false);
        prevParaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        prevParaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        prevParaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevParaButtonActionPerformed(evt);
            }
        });
        add(prevParaButton);

        curParaText.setEditable(false);
        curParaText.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        curParaText.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.curParaText.text")); // NOI18N
        curParaText.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.curParaText.toolTipText")); // NOI18N
        curParaText.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        curParaText.setFocusable(false);
        curParaText.setMaximumSize(new java.awt.Dimension(30, 20));
        curParaText.setMinimumSize(new java.awt.Dimension(30, 20));
        curParaText.setPreferredSize(new java.awt.Dimension(30, 20));
        curParaText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                curParaTextActionPerformed(evt);
            }
        });
        add(curParaText);

        totalParasLabel.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.totalParasLabel.text")); // NOI18N
        totalParasLabel.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.totalParasLabel.toolTipText")); // NOI18N
        totalParasLabel.setMaximumSize(new java.awt.Dimension(40, 20));
        totalParasLabel.setMinimumSize(new java.awt.Dimension(40, 20));
        totalParasLabel.setPreferredSize(new java.awt.Dimension(40, 20));
        add(totalParasLabel);

        nextParaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/purl/jh/feat/util0/gui/pager/go-next.png"))); // NOI18N
        nextParaButton.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.nextParaButton.text")); // NOI18N
        nextParaButton.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.nextParaButton.toolTipText")); // NOI18N
        nextParaButton.setFocusable(false);
        nextParaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextParaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextParaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextParaButtonActionPerformed(evt);
            }
        });
        add(nextParaButton);

        lastParaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/purl/jh/feat/util0/gui/pager/go-last.png"))); // NOI18N
        lastParaButton.setText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.lastParaButton.text")); // NOI18N
        lastParaButton.setToolTipText(org.openide.util.NbBundle.getMessage(GotoControl.class, "GotoControl.lastParaButton.toolTipText")); // NOI18N
        lastParaButton.setFocusable(false);
        lastParaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lastParaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lastParaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastParaButtonActionPerformed(evt);
            }
        });
        add(lastParaButton);
    }
    

    private void firstParaButtonActionPerformed(java.awt.event.ActionEvent evt) {
        requestCur(0);
	}

    private void prevParaButtonActionPerformed(java.awt.event.ActionEvent evt) {
        requestCur(cur - 1);
	}

    private void curParaTextActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
	}

    private void nextParaButtonActionPerformed(java.awt.event.ActionEvent evt) {
        requestCur(cur + 1);
	}

    private void lastParaButtonActionPerformed(java.awt.event.ActionEvent evt) {
        requestCur(size-1);
	}


    private void requestCur(int aIdx) {
        if (aIdx < 0 || aIdx >= size) return;

        cur = aIdx;

        updateUi();

        fireCurChanged(cur);
    }


    private final List<CurListener> listeners = new ArrayList<CurListener>();

    /**
     * Adds a listener that's notified each the user requests a different item.
     * @param l the <code>T</code> to be removed
     */
    public void addListener(CurListener aListener) {
        assert !listeners.contains(aListener);
        listeners.add(aListener);
    }

    /**
     * Removes a listener that's notified each the user requests a different item.
     * @param l the <code>T</code> to be removed
     */
    public void removeListener(CurListener aListener) {
        final boolean wasThere = listeners.remove(aListener);
        assert wasThere;
    }



    public void fireCurChanged(final int aCur) {
        for (CurListener listener : listeners) {
            listener.curChanged(aCur);
        }
    }


    private javax.swing.JTextField curParaText;
    private javax.swing.JButton firstParaButton;
    private javax.swing.JButton lastParaButton;
    private javax.swing.JButton nextParaButton;
    private javax.swing.JButton prevParaButton;
    private javax.swing.JLabel totalParasLabel;
}
