
package org.purl.jh.util.col;

import java.util.Collections;

/**
 *
 * @author Jirka dot Hana at gmail dot com
 */
public class Mappers {


    public final static Mapper<?,String> cToString = new Mapper<Object, String>() {
        public String map(Object aOrigItem) {
            return String.valueOf(aOrigItem);
        }
    };

    public static <T> Mapper<T,String> toStringg() {
        return (Mapper<T,String>) cToString;
    }

//    // map google mappers to our mappers
//    public static <D,R> Mapper<D,R> google( ) {
//        return (Mapper<T,String>) ;
//    }

}
