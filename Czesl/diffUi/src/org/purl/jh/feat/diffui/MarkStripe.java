package org.purl.jh.feat.diffui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.openide.util.Utilities;
import org.purl.jh.pml.location.AcceptingLocation;
import org.purl.jh.pml.location.Location;
import org.purl.jh.util.Pair;
import org.purl.jh.util.col.Cols;

/**
 * @todo display tooltip 
 * @author j
 */
public class MarkStripe extends JComponent implements MouseListener, MouseMotionListener {  // todo listen to doc/mark provider changes
    //NB: , DocumentListener, PropertyChangeListener, Accessible {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(MarkStripe.class);
    
    private int maxX;
    private List<Pair<Location,Point>> marks;
    private List<Integer> stripeXs;

    public MarkStripe() {
        addMouseListener(this);
        addMouseMotionListener(this);
        
        setOpaque(true);
        setSize(getSize().width, 300);
        
        //setToolTipText(NbBundle.getMessage(AnnotationView.class,"TP_ErrorStripe"));
    }

    AcceptingLocation editor;
    
    /**
     * todo rename
     * @todo listen to location change
     * @param marks
     * @param maxX 
     */
    public void setMarks(AcceptingLocation editor, List<Pair<Location,Point>> marks, int maxX) {
        log.info("setMarks maxX=%d - marks:%s", maxX, Cols.toStringNl(marks));
        
        this.editor = editor;
        this.marks = marks;
        this.stripeXs = null;
        this.maxX = maxX;
        if (this.maxX == 0) {    // todo tmp
            log.warning("maxX=0");
            this.maxX = 10000;
        }
        
    }
    
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, HEIGHT);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(Integer.MIN_VALUE, HEIGHT);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Integer.MAX_VALUE, HEIGHT);
    }     
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
       resetCursor();
        
        Pair<Location,Point> mark = getMark(e.getPoint().x);
        
        if (mark !=  null) {
            editor.goToLoc(mark.mFirst);
            //pane.setCaretPosition(Utilities.getRowStartFromLineOffset(doc, mark.getAssignedLines()[0]));
        }
    } 
    
    public void mouseReleased(MouseEvent e) {
        //NOTHING:
        resetCursor();
    }

    public void mousePressed(MouseEvent e) {
        resetCursor();
    }

    public void mouseMoved(MouseEvent e) {
        checkCursor(e);
    }

    public void mouseExited(MouseEvent e) {
        resetCursor();
    }

    public void mouseEntered(MouseEvent e) {
        checkCursor(e);
    }

    public void mouseDragged(MouseEvent e) {
    }
    
    private void resetCursor() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void checkCursor(MouseEvent e) {
        Pair<Location,Point> mark = getMark(e.getPoint().x);
        
        if (mark == null) {
            resetCursor();
        }
        else {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
        }
    }
    
    final int cDelta = 20;
    
    private Pair<Location,Point> getMark(int stripeX) {
        final int idx  = Collections.binarySearch(this.stripeXs, stripeX);
        
        // todo check surrounding
        if (idx < 0) {
            final int idxx = -idx;
            final int delta1 = getDelta(stripeX, idxx-1);
            final int delta2 = getDelta(stripeX, idxx);
            
            if (delta1 < delta2) {
                return delta1 < cDelta ? this.marks.get(idxx-1) : null;
            }
            else {
                return delta2 < cDelta ? this.marks.get(idxx) : null;
            }
        }
        else {
            return this.marks.get(idx);
        }
    }
    
    private int getDelta(int stripeX, int idx) {
        if (idx < 0 || idx >= this.marks.size()) return Integer.MAX_VALUE;
        
        Pair<Location,Point> mark = this.marks.get(idx);

        return Math.abs( mark.mSecond.x - stripeX );
    }
    

    
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Color oldColor = g.getColor();
        Color backColor = UIManager.getColor("Panel.background"); // NOI18N 

        g.setColor(backColor);
        g.fillRect(0, 0, getWidth(), getHeight());     
        // show curr view with ligher/darker  gray

        if (marks != null) {
            stripeXs = new ArrayList<>();
            
            g.setColor(Color.ORANGE.darker());
            for (Pair<Location,Point>  mark : marks) {
                int stripeX = scene2stripe(mark.mSecond.x);
                stripeXs.add(stripeX);
                drawMark(g, stripeX); 
            }
        }
        drawMark(g, (int) maxX/2); 

        g.setColor(oldColor);
    }

    private static final int THICKNESS = 2;
    private static final int HEIGHT = 10;
    
//    private static final int STATUS_BOX_SIZE = 7;
//    private static final int THICKNESS = 4; //STATUS_BOX_SIZE + 6;
//    /*package private*/ static final int PIXELS_FOR_LINE = 3/*height / lines*/;
//    /*package private*/ static final int LINE_SEPARATOR_SIZE = 1/*2*/;
//    /*package private*/ static final int HEIGHT_OFFSET = 20;
    
    private void drawMark(Graphics g, int stripeX) {

        //log.info("Drawing mark %d, start = %d (maxX=%d, w=%d)", x, stripeX, maxX, getSize().width);
        
//        g.drawLine(2, start + PIXELS_FOR_LINE / 2, THICKNESS - 3, start + PIXELS_FOR_LINE / 2 );        
//        g.fillRect( THICKNESS / 2 - PIXELS_FOR_LINE / 2, start, PIXELS_FOR_LINE, PIXELS_FOR_LINE );
//        g.draw3DRect( THICKNESS / 2 - PIXELS_FOR_LINE / 2, start, PIXELS_FOR_LINE - 1, PIXELS_FOR_LINE - 1, true );

        int h = Math.max(getSize().height, 20);
        
        //g.fillRect(stripeX - THICKNESS/2, 0, stripeX + THICKNESS/2, h );
        g.fillRect(stripeX - THICKNESS/2, 0, THICKNESS, h );
        
//        g.drawLine  (stripeX + PIXELS_FOR_LINE / 2, 2, stripeX + PIXELS_FOR_LINE / 2, THICKNESS - 3 );        
//        g.fillRect  (stripeX, THICKNESS / 2 - PIXELS_FOR_LINE / 2, PIXELS_FOR_LINE, PIXELS_FOR_LINE );
//        g.draw3DRect(stripeX, THICKNESS / 2 - PIXELS_FOR_LINE / 2, PIXELS_FOR_LINE - 1, PIXELS_FOR_LINE - 1, true );
        
    }
    
    private int scene2stripe(int sceneX) {
        return (int) Math.round( ((1.0 * sceneX )/ (1.0*maxX)) * getSize().width );
    }

}
