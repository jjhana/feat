package cz.cuni.utkl.czesl.html2pml.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;
import org.purl.jh.util.io.IO;
import org.purl.jh.util.io.XFile;

class RTFView extends JFrame {

    public RTFView(XFile aFile) {
        setTitle("RTF Text Application");
        setSize(400, 240);
        setBackground(Color.gray);
        getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.CENTER);
        // Create an RTF editor window
        RTFEditorKit rtf = new RTFEditorKit();
        JEditorPane editor = new JEditorPane();
        editor.setEditorKit(rtf);
        editor.setBackground(Color.white);
        // This text could be big so add a scroll pane
        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(editor);
        topPanel.add(scroller, BorderLayout.CENTER);
        // Load an RTF file into the editor

        try {
            Reader r = IO.openReader(aFile);
            rtf.read(r, editor.getDocument(), 0);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("I/O error");
        } catch (BadLocationException e) {
        }
    }
}
