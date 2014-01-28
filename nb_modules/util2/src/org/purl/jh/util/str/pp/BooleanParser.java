package org.purl.jh.util.str.pp;

public class BooleanParser extends Pp<Boolean> {

    public Boolean fromString(String aString) {
        return Boolean.parseBoolean(aString);
    }
}
