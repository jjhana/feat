/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.purl.jh.feat.layered;

/**
 *
 * @author jirka
 */
public class LayeredXGraph {
//    private LayeredGraph graph;
//
//    private void showParaGraph() {
//        if (curPara < 0 || curPara >= paras.size()) return;
//
//        // totod remove from all views
//        if (graph != null) {
//            model.removeDataListener(widgetNode);
//            graph.removeObjectSceneListener(objListener, ObjectSceneEventType.values());
//            graph.getView().removeMouseWheelListener(mouseWheelListener);   
//            WindowManager.getDefault().getMainWindow().removeKeyListener((KeyListener)graph.getView());  
//        }
//
//        curParaText.setText("" + (curPara+1));
//
//        int splitPos = jSplitPane1.getDividerLocation();
//        int extraSplitPos = (jSplitPane1.getTopComponent() instanceof JSplitPane) ?
//                ((JSplitPane)jSplitPane1.getTopComponent()).getDividerLocation() : (splitPos/2);
//        
//        // remove undo history (currently model is tied to the current paragraph and view)
//        undoMngr.discardAllEdits();
//        model = new LayeredGraphModel(layers, paras, paras.get(curPara), undoMngr);
//        
//        graph = createView();
//        graphComponent = graph.getView();
//        
//        graphScrollPane = new JScrollPane(graphComponent);
//        // todo temporary
//
//        model.addDataListener(widgetNode);
//
//        // --- initialize controls according to saved preferences (post component) ---
//        //final String tooltip = org.openide.util.NbBundle.getMessage(LayeredViewTopComponent.class, "LayeredViewTopComponent.zoomSlider.toolTipText");
//        // todo zoom ZoomModel.initZoom(zoomSlider, 50, 10, tooltip, getPrefs(), "graphZoom", cSliderMax );
//
//        if (getPrefs().getBoolean("twoViews", false)) {
//            JScrollPane graphScrollPane2 = new JScrollPane(createView().getView());
//
//            final JSplitPane extraPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//            
//            extraPanel.setTopComponent(graphScrollPane);
//            extraPanel.setBottomComponent(graphScrollPane2);
//            extraPanel.setDividerLocation(extraSplitPos);
//            jSplitPane1.setTopComponent(extraPanel);
//        }
//        else {
//            jSplitPane1.setTopComponent(graphScrollPane);
//        }
//        jSplitPane1.setDividerLocation(splitPos);
//
//        //log.info("updateZoom (curPara=" +curPara+")");
//        //setZoomFactor();      // todo keep zoom persistent across paragraphs, currently does not work
//    }
//
//
//
//    /** Under construction */
//    private LayeredGraph createView() {
//        final LayeredGraph view = new LayeredGraph(model, widgetNode, new LookAndFeel());
//
//        // --- initialize graph values according to saved preferences (pre drawing/component); must be the same as above ---
//        view.setXSpace(getPrefs().getInt("xSpacing", 100));
//        view.setYSpace(getPrefs().getInt("ySpacing", 100));
//        view.setZoomFactor(getPrefs().getDouble("zoomFactor", 1.0));
//
//        view.initLayout();
//        view.draw();
//
//        final JComponent component = view.createView();
//
//
//        // --- initialize controls according to saved preferences (post component) ---
//        //final String tooltip = org.openide.util.NbBundle.getMessage(LayeredViewTopComponent.class, "LayeredViewTopComponent.zoomSlider.toolTipText");
//        // todo zoom ZoomModel.initZoom(zoomSlider, 50, 10, tooltip, getPrefs(), "graphZoom", cSliderMax );
//
//
//        
////        WindowManager.getDefault().getMainWindow().addKeyListener((KeyListener)component);  // needed for some reason, otherwise teh scene does not get keyboard events
//        component.addFocusListener(new FocusListener() {
//            public void focusGained(FocusEvent e) {
//                log.info("Focus gained");
//                component.setBorder(BorderFactory.createLineBorder(Color.black, 3));
//                view.addObjectSceneListener(objListener, ObjectSceneEventType.OBJECT_FOCUS_CHANGED);
//                view.addObjectSceneListener(objListener, ObjectSceneEventType.OBJECT_SELECTION_CHANGED);
//                component.addMouseWheelListener(mouseWheelListener);   
//
//                cursorChanged(view.getFocusedObject());
//                //WindowManager.getDefault().getMainWindow().addKeyListener((KeyListener)component);  // needed for some reason, otherwise teh scene does not get keyboard events
//            }
//
//            public void focusLost(FocusEvent e) {
//                log.info("Focus lost");
//                component.setBorder(BorderFactory.createLineBorder(Color.black, 0));
//                view.removeObjectSceneListener(objListener, ObjectSceneEventType.values());     
//                // todo why do I remove it? Properties are not updated when changed from other view. The TC should probably have its own current object 
//                component.removeMouseWheelListener(mouseWheelListener);
//                //WindowManager.getDefault().getMainWindow().removeKeyListener((KeyListener)component);  // needed for some reason, otherwise teh scene does not get keyboard events
//            }
//        });
//        
//        return view;
//    }
    
}
