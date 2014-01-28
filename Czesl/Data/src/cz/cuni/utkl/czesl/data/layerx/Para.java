package cz.cuni.utkl.czesl.data.layerx;

import java.util.List;
import org.purl.jh.pml.IdedElement;

/**
 * Paragraph element.
 *
 * @param <P> type of the document parent element
 * @author Jirka dot Hana at gmail dot com
 */
public interface Para extends IdedElement {
    
    @Override
    public Doc getParent();
    
    
    public List<? extends FForm> getFormsList();

    /**
     * All forms of this paragraph. 
     * @return 
     */
    Iterable<? extends FForm> getForms();
    
    boolean isLi(); 

    Para setLi(boolean li);
  
    /**
     * Is a form before another form in terms of word-order?
     * 
     * @param aForm1 the first form
     * @param aForm2 the second form
     * @return true if aForm1 precedes aForm2, false otherwise (includes the case 
     *    when aForm1 and aForm2 are the same object).
     */
    boolean before(FForm aForm1, FForm aForm2);

    /**
     * Collect all paragraphs on lower layers corresponding to this paragraph, 
     * which is included as well.
     *
     * @return list of paragraphs, starting with the lowest paragraph and ending
     * with this one. The list might be immutable.
     */
    List<Para> getLowerEqParas();
}
