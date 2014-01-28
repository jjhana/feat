package org.purl.jh.util.str.pp;

public class IntegerParser extends Pp<Integer> {

    public Integer fromString(String aString) {
        return Integer.parseInt(aString);
    }
}
