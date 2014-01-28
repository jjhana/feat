package org.purl.jh.speedsupport.nodes;

import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;
import org.purl.jh.speedsupport.data.Box;
import org.purl.jh.speedsupport.data.SBundle;

/**
 *
 * @author j
 */
public class BoxNodeChildFactory extends ChildFactory.Detachable<SBundle> implements ChangeListener, /*LookupListener,*/ Box.BoxListener {
    private final static org.purl.jh.util.Logger log = org.purl.jh.util.Logger.getLogger(BoxNodeChildFactory.class);
    
    private final Box model;
//    private Lookup.Result<File> stringResult;

    public BoxNodeChildFactory(Box model) {
        this.model = model;
    }

    @Override
    protected boolean createKeys(List<SBundle> list) {
        list.addAll(model.getBundles());
        return true;
    }

    @Override
    protected Node createNodeForKey(SBundle bundle) {
        return new BundleNode(bundle, Lookups.fixed(bundle));
    }

    @Override
    protected void addNotify() {
        model.addBoxListener(this);
    }

    @Override
    protected void removeNotify() {
        model.removeBoxListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(false);
    }
    
    @Override
    public void changed() {
        refresh(false);
    }
    
}
