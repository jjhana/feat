package org.purl.jh.util.str;

public enum Cap {
    lower, firstCap, caps, number, mixed;
    public boolean lower()    {return this == lower;}
    public boolean firstCap() {return this == firstCap;}
    public boolean caps()     {return this == caps;}
    public boolean mixed()    {return this == mixed;}
    public boolean number()   {return this == number;}

    /**
     * Order: allLower, firstCap, allCaps, mixed;
     */
    public boolean less(Cap aCap) {
        return ordinal() < aCap.ordinal();
    }
};
