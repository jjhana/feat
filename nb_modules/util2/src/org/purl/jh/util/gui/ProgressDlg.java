package org.purl.jh.util.gui;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 *
 * @author Jiri
 */
public class ProgressDlg extends JDialog {
    JProgressBar mProgressBar;
    
   
    /** Creates a new instance of ProgressDlg */
    public ProgressDlg(Frame aParent, String aMsg) {
        super(aParent, aMsg, false);
        //setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    
        mProgressBar = new JProgressBar();
        mProgressBar.setIndeterminate(true);
        JLabel label = new JLabel(aMsg);
        
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(mProgressBar);
        add(panel);
        pack();

        Dimension  paneSize = getSize();
        Dimension  screenSize = getToolkit().getScreenSize();

        setLocation( (screenSize.width - paneSize.width) / 2,
                               (screenSize.height - paneSize.height) / 2);
    }

    @Override
    public void setVisible(boolean aVisible) {
        getParent().setEnabled(!aVisible);
        super.setVisible(aVisible);
    }
    
}
