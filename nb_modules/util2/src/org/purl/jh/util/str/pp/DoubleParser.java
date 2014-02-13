package org.purl.jh.util.str.pp;

public class DoubleParser extends Pp<Double> {

    public Double fromString(String aString) {
        return Double.parseDouble(aString);
    }
}
