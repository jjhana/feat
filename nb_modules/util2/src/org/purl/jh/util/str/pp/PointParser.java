package org.purl.jh.util.str.pp;

import java.awt.Point;
import org.purl.jh.util.str.StringPair;
import org.purl.jh.util.str.Strings;

public class PointParser extends Pp<Point> {

    public Point fromString(String aString) {
        StringPair pair = Strings.splitIntoTwo(aString, ':');
        return new Point(Integer.parseInt(pair.mFirst), Integer.parseInt(pair.mSecond));
    }

    public String toString(Point aObject) {
        Point p = (Point) aObject;
        return p.x + ":" + p.y;
    }
}
