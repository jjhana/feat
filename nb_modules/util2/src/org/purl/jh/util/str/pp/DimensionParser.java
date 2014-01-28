package org.purl.jh.util.str.pp;

import java.awt.Dimension;
import org.purl.jh.util.str.StringPair;
import org.purl.jh.util.str.Strings;

public class DimensionParser extends Pp<Dimension> {

    public Dimension fromString(String aString) {
        StringPair pair = Strings.splitIntoTwo(aString, ':');
        return new Dimension(Integer.parseInt(pair.mFirst), Integer.parseInt(pair.mSecond));
    }

    public String toString(Dimension aObject) {
        Dimension dim = (Dimension) aObject;
        return dim.width + ":" + dim.height;
    }
}
