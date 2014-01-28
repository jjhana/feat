
package org.purl.jh.pml;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Test {
    public static class ClassA {}
    
    public static class ClassB<T> {
        public Collection<? extends T> fnc() {
            return new ArrayList<>();
        }
        
    }

    public static void test() {
            ClassB<ClassA> b = new ClassB<>();

            Collection<? extends ClassA> col = b.fnc();
            for (ClassA l : b.fnc()) {}
            for (ClassA l : col) {}


            Layer<?> layer = new Layer(null,null) {};
            Collection<? extends Layer> col2 = layer.getReferencedLayers();
            for (Layer l : layer.getReferencedLayers()) {}
            for (Layer l : col2) {}
    }

}
