package org.purl.jh.feat.navigator;

import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.RegexPatternConverter;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * Object specifying properties of desired elements (forms/edges) in search, etc.
 * 
 * @author j
 * @todo use lombok
 */
@Getter
@Setter
public class FilterSpec {
    String name;
    
    // layers ; todo keep indexes
    boolean layer0;
    boolean layer1;
    boolean layer2;
    // obj types
    boolean forms;
    boolean edges;
    // properties
    boolean withComments;
    
    Pattern commentPattern;
    boolean incorrect;
    Pattern formPattern;
    boolean changing;
    boolean changingImmediately;
    boolean changed;
    boolean changedImmediately;

    boolean withError;
    Pattern errorPattern;
    int edgeLMin = -1;
    int edgeLMax = -1;
    int edgeHMin = -1;
    int edgeHMax = -1;
    boolean withLinks;
    
    

    public FilterSpec() {
    }

    public FilterSpec(FilterSpec aData) {
        this.name = aData.name;
        this.layer0 = aData.layer0;
        this.layer1 = aData.layer1;
        this.layer2 = aData.layer2;

        this.forms = aData.forms;
        this.edges = aData.edges;
        
        this.withComments = aData.withComments;
        this.commentPattern = aData.commentPattern;
        
        this.incorrect = aData.incorrect;
        this.formPattern = aData.formPattern;

        this.changing = aData.changing;
        this.changingImmediately = aData.changingImmediately;
        this.changed = aData.changed;
        this.changedImmediately = aData.changedImmediately;
        
        this.withError = aData.withError;
        this.errorPattern = aData.errorPattern;
        
        this.edgeLMin = aData.edgeLMin;
        this.edgeLMax = aData.edgeLMax;
        this.edgeHMin = aData.edgeHMin;
        this.edgeHMax = aData.edgeHMax;
        this.withLinks = aData.withLinks;
    }

    public FilterSpec compile() {
        return this;
    }
    
    
    @Override
    public String toString() {
        return name;
    }

    public String toConfString() {
        return "ConfBean{" + "name=" + name + ", layer0=" + layer0 + ", layer1=" + layer1 + ", layer2=" + layer2 + ", forms=" + forms + ", edges=" + edges + ", withComments=" + withComments + ", commentPattern=" + commentPattern + ", incorrect=" + incorrect + ", formPattern=" + formPattern + ", withError=" + withError + ", errorPattern=" + errorPattern + ", edgeLMin=" + edgeLMin + ", edgeLMax=" + edgeLMax + ", edgeHMin=" + edgeHMin + ", edgeHMax=" + edgeHMax + ", withLinks=" + withLinks + '}';
    }
   
}
