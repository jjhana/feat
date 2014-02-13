package cz.cuni.utkl.czesl.html2pml.impl;

import java.util.Arrays;
import java.util.List;

public class Format {

    public enum Type {
        italic, underline, bold, strike, red;
        public boolean italic()    {return this == italic;}
        public boolean underline() {return this == underline;}
        public boolean bold()      {return this == bold;}
        public boolean strike()    {return this == strike;}
        public boolean red()       {return this == red;}

        /**
         * 
         * @param aTagCore must be in lowercase
         * @return 
         */
        public static Type fromTag(final String aTagCore) {
            if (aTagCore.equals("i")) {
                return italic;
            }
            else if (aTagCore.equals("u")) {
                return underline;
            }
            else if (aTagCore.equals("b")) {
                return bold;
            }
            else if (aTagCore.equals("s") || aTagCore.equals("strike")) {
                return strike;
            }
            else if (aTagCore.equals("foreground")) {
                return red;
            }

            throw new IllegalArgumentException("Unknown format tag " + aTagCore);
        }
    }

    private final static List<String> consideredFormats = Arrays.asList("i", "u", "b", "s", "strike", "foreground");

    public static List<String> recognized() {
        return consideredFormats;
    }


    private final Type name;
    private final int from;
    private final int len;

    public Format(Format.Type aName) {
        name = aName;
        from = len = -1;
    }

    public Format(Format.Type aName, int aFrom, int aLen) {
        name = aName;
        from = aFrom;
        len = aLen;
    }

    public Type getType() {
        return name;
    }

    /** Relative to the token */
    public int getFrom() {
        return from;
    }

    public int getLen() {
        return len;
    }

//    public String getName() {
//        return name;
//    }
}
