package org.purl.jh.util.str.pp;

import java.awt.Rectangle;

public class RectangleParser extends Pp<Rectangle> {

    public Rectangle fromString(String aString) {
        String[] strs = aString.split("\\:");
        return new Rectangle(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
    }

    public String toString(Rectangle aObject) {
        Rectangle r = (Rectangle) aObject;
        return r.x + ":" + r.y + ':' + r.width + ':' + r.height;
    }
}
