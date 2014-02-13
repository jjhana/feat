package org.purl.jh.speedsupport.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.purl.jh.speedsupport.data.Box;

/**
 * Node corresponding to an inbox/outbox/backup model.
 * Currently it includes speed support, in the future it should be separated into 
 * a subclass (another subclass would support normal disk based bundles/documents).
 *
 * @author j
 */
public class BoxNode extends AbstractNode {
    //protected final BoxChildFactory factory;
    protected final Box model;

    final String displayName;

    // todo add listener to the model
    public BoxNode(Box model) {
        super(Children.create(new BoxNodeChildFactory(model), true));

        this.model = model;
        this.displayName = model.getDefDisplayName();
    }

    public Box getModel() {
        return model;
    }

    
    
    @Override
    public String getDisplayName() {
        return displayName;
    }

    
    
    private Box.BoxListener boxListener = new Box.BoxListenerAdapter() {

        @Override
        public void changed() {
            throw new UnsupportedOperationException();
        }
        
    }; 

    public void refresh() {
        model.refresh();
    }
    
};
