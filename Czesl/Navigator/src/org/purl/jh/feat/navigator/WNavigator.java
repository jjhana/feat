//package org.purl.jh.feat.navigator;
//
//import cz.cuni.utkl.czesl.data.layerw.WLayer;
//import java.util.Collection;
//import javax.swing.JComponent;
//import org.netbeans.spi.navigator.NavigatorLookupHint;
//import org.netbeans.spi.navigator.NavigatorPanel;
//import org.openide.util.Lookup;
//import org.openide.util.LookupEvent;
//import org.openide.util.LookupListener;
//import org.openide.util.Utilities;
//import org.openide.util.lookup.AbstractLookup;
//import org.openide.util.lookup.InstanceContent;
//
///**
// *
// * @todo this is just testing the concept, the plan is to include a navigator
// * for errors, for words starting with X, for diff, etc.
// * @author j
// */
//public class WNavigator  extends JComponent implements NavigatorPanel, LookupListener {
//   private Lookup.Result<WLayer> result = null;
//   
//    final Lookup lookup;
//    InstanceContent lookupContent = new InstanceContent();
//
//     NavigatorLookupHint hint = new NavigatorLookupHint() {
//        public String getContentType() {
//            return "text/feat-w+xml";
//        }
//    };
//        
//   public WNavigator() {
//        lookup = new AbstractLookup(lookupContent);       
//      initComponents();
//   }
//   public JComponent getComponent() {
//      return this;
//   }
//   public void panelActivated(Lookup context) {
//       lookupContent.add(hint);
//       result = Utilities.actionsGlobalContext().lookupResult(WLayer.class);
//      
//       result.addLookupListener(this);
//       resultChanged(null);  
//   }
//   public void panelDeactivated() {
//      lookupContent.remove(hint);
//      result.removeLookupListener(this);
//      result = null;
//   }
//   public void resultChanged(LookupEvent event) {
//      Collection<? extends WLayer> wlayers = result.allInstances();
//      if(!wlayers.isEmpty()) {
//         WLayer wlayer = wlayers.iterator().next();
//         // search for albums of selected artist and display it
////         albumsOf.setText(mp3.getArtist());
////         DefaultListModel model = new DefaultListModel();
////         model.addElement(new String("Album 1 of " + mp3.getArtist()));
////         model.addElement(new String("Album 2 of " + mp3.getArtist()));
////         albums.setModel(model);
//      }
//   }
//}    
//
