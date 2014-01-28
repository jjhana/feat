package org.purl.jh.feat.navigator;

import com.google.common.base.Joiner;
import cz.cuni.utkl.czesl.data.layerl.Edge;
import cz.cuni.utkl.czesl.data.layerl.Errorr;
import cz.cuni.utkl.czesl.data.layerl.LForm;
import cz.cuni.utkl.czesl.data.layerx.FForm;
import java.util.*;
import org.purl.jh.pml.Commented;
import org.purl.jh.pml.Element;
import org.purl.jh.pml.IdedElement;
import org.purl.jh.util.col.MappingSet;

/**
 *
 * @author j
 */
public class ColumnPrinters {
    private final Map<String,ColumnPrinter<?>> id2printer = new HashMap<>();
    
    public static ColumnPrinters getDef() {
        return instance;
    }

    public static Comparator<ColumnPrinter<?>> cNameComparator = new Comparator<ColumnPrinter<?>>() {
        @Override
        public int compare(ColumnPrinter<?> o1, ColumnPrinter<?> o2) {
            return o1.getName().compareTo(o1.getName());
        }
    };
    
    
    private ColumnPrinters() {
        register(
            cEmpty,
            //cErr,
            cForm2token,
            cElement2id,
            cElement2comment,
            cEdge2str,
            cEdge2errs,
            cEdge2lower,
            cEdge2upper
            );
    }
    
    public ColumnPrinter getPrinter(String aId) {
        return id2printer.get(aId);
    }

    public <T extends Element> List<ColumnPrinter<? super T>> getPrinters(Class<T> aClass) {
        final List<ColumnPrinter<? super T>> printers = new ArrayList<>();
        for (ColumnPrinter<?> printer : id2printer.values()) {
            // printer.getClazz() isSuperclassOf aClass
            if (printer.getClazz().isAssignableFrom(aClass)) {
                printers.add( (ColumnPrinter<? super T>) printer );
            }
        }
        return printers;
    }

    public <T extends Element> List<ColumnPrinter<? super T>> getPrintersNameSorted(Class<T> aClass) {
        final List<ColumnPrinter<? super T>> printers = getPrinters(aClass);
        Collections.sort(printers, cNameComparator);
        return printers;
    }

    
    public void register(final ColumnPrinter<? extends Element> ... aPrinters) {
        System.out.println("id2printer " + id2printer);
        for (ColumnPrinter<?> printer : aPrinters) {
        System.out.println("id " + printer.getId());
            id2printer.put(printer.getId(), printer);
        }
    }
    
    
    
    
    public static ColumnPrinter<Element> cEmpty = new ColumnPrinter<Element>("basic.empty", Element.class, "empty") {
        @Override
        public String map(final Element aOrigItem) {
            return "";
        }
    };
    
    public static ColumnPrinter<Element> cErr = new ColumnPrinter<Element>("basic.err", Element.class, "Error", "The original printer could not be found") {
        @Override
        public String map(final Element aOrigItem) {
            return "?";
        }
    };
    
    public static ColumnPrinter<FForm> cForm2token = new ColumnPrinter<FForm>("basic.token", FForm.class, "token") {
        @Override
        public String map(final FForm aOrigItem) {
            return aOrigItem.getToken();
        }
    };

    public static ColumnPrinter<Element> cElement2id = new ColumnPrinter<Element>("basic.id", Element.class, "id") {
        @Override
        public String map(final Element aElement) {
            return (aElement instanceof IdedElement) ? ((IdedElement)aElement).getId().getIdStr() : "";
        }
    };
    
    public static ColumnPrinter<Element> cElement2comment = new ColumnPrinter<Element>("basic.comment", Element.class, "comment") {
        @Override
        public String map(final Element aElement) {
            return (aElement instanceof Commented) ? ((Commented)aElement).getComment() : "";
        }
    };

    public static ColumnPrinter<Edge> cEdge2str = new ColumnPrinter<Edge>("basic.str", Edge.class, "str") {
        @Override
        public String map(final Edge aEdge) {
            return aEdge.getId().getIdStr();
        }
    };

    public static ColumnPrinter<Edge> cEdge2errs = new ColumnPrinter<Edge>("basic.errs", Edge.class, "errs") {
        @Override
        public String map(final Edge aEdge) {
            return getErrString(aEdge);
        }
        
            private String getErrString(Edge aEdge) {
                if (aEdge.getErrors().isEmpty()) {
                    return "";
                }
                else {
                    return Joiner.on(',').join(
                        new MappingSet<>(aEdge.getErrors(), Errorr.cErrorInfo2TagStr));
                }
            }
        
    };

    public static ColumnPrinter<Edge> cEdge2lower = new ColumnPrinter<Edge>("basic.lowerForms", Edge.class, "lowerForms") {
        @Override
        public String map(final Edge aEdge) {
            return getLowerString(aEdge);
        }
        
        private String getLowerString(Edge aEdge) {
            if (aEdge.getErrors().isEmpty()) {
                return "";
            }
            else {
                return Joiner.on(',').join(
                    new MappingSet<FForm, String>(aEdge.getLower(), FForm.cForm2Token));
            }
        }
    };
    
    public static ColumnPrinter<Edge> cEdge2upper = new ColumnPrinter<Edge>("basic.higherForms", Edge.class, "higherForms") {
        @Override
        public String map(final Edge aEdge) {
            return getUpperStrings(aEdge);
        }
        
        private String getUpperStrings(Edge aEdge) {
            if (aEdge.getErrors().isEmpty()) {
                return "";
            }
            else {
                return Joiner.on(',').join(
                    new MappingSet<>(aEdge.getHigher(), LForm.cForm2Token));
            }
        }
    };

    // must be at the end, so that individual regiestered printers are already initialized
    private final static ColumnPrinters instance = new ColumnPrinters();
}
