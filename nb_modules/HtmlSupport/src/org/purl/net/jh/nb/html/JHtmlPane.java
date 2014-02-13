package org.purl.net.jh.nb.html;

import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.text.Position;
import javax.swing.text.html.HTMLDocument;

/**
 *
 * @author jirka
 */
public class JHtmlPane extends javax.swing.JEditorPane {

    public JHtmlPane(String type, String text) {
        super(type, text);
    }

    public JHtmlPane(String url) throws IOException {
        super(url);
    }

    public JHtmlPane(URL initialPage) throws IOException {
        super(initialPage);
    }

    public JHtmlPane() {
//            for(Action act : getActions()) {
//                System.out.println("act: " + act.getValue(Action.NAME) + " " + act.getValue(Action.ACCELERATOR_KEY));
//            }
//            
//            for(Action act : getEditorKit().getActions()) {
//                System.out.println("ek.act: " + act.getValue(Action.NAME) + " " + act.getValue(Action.ACCELERATOR_KEY));
//            }
            initKeys();
            initMouseListening();
    }

    
    
    
    public static interface CursorListener {
        void cursorChanged(int aPos);
    }

    private final List<CursorListener> cursorListeners = new ArrayList<CursorListener>();

    public void addCursorListener(CursorListener aView) {
        assert !cursorListeners.contains(aView);
        cursorListeners.add(aView);
    }

    /**
     * Removes a listener that's notified each time a new word is selected.
     * @param l the <code>GraphDataListener</code> to be removed
     */
    public void removeCursorListener(CursorListener aListener) {
        final boolean wasThere = cursorListeners.remove(aListener);
        assert wasThere;
    }


    // todo under development (click in the view should be reported as position in the html
    private void initMouseListening() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() < 2) return;
                
                final int pos = pointToOffset(new Point(e.getX(), e.getY()));
                
                for (CursorListener cursorListener : cursorListeners) {
                    cursorListener.cursorChanged(pos);
                }
            }

        });
        
    }
    
    /** 
     * Translates the component coordinate to the corresponding offset in the 
     * html text.
     * 
     * @param aPoint component coordinate
     * @return 
     */
    protected int pointToOffset(Point aPoint) {
        Position.Bias[] biasRet = new Position.Bias[1];
        int pos = getUI().viewToModel(JHtmlPane.this, aPoint, biasRet);
        if(biasRet[0] == null) biasRet[0] = Position.Bias.Forward;
        return pos;
    }
    
    
    
    public void initKeys() {
        // does snot work
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        
        getInputMap().put(key, "xxxa1");
        getActionMap().put("xxxa1", new FontSizeAction());

        KeyStroke key2 = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
        getInputMap().put(key2, "xxxa2");
        getActionMap().put("xxxa2", new FontSizeAction());
    }
    
    public class FontSizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            Font font = getFont();
            javax.swing.text.html.HTMLEditorKit a;
            setBodyFont(font.getFamily(), font.getSize()+10);
            System.out.println("FontSizeAction");
	}
   }
    
   
    

    public void incFontSize(int aStep) {
        Font font = getFont();
        javax.swing.text.html.HTMLEditorKit a;
        setBodyFont(font.getFamily(), font.getSize()+aStep);
    }
    
    
    public void setBodyFont(String aFamily, int aSize) {
        ((HTMLDocument)getDocument()).getStyleSheet().removeStyle("body");        
        String bodyRule = "body { font-family: " + aFamily + "; " + "font-size: " + aSize + "pt; }";
        ((HTMLDocument)getDocument()).getStyleSheet().addRule(bodyRule);        
    }
}
