package org.purl.jh.util.sgml;

import java.util.BitSet;
import java.util.Vector;
import java.io.*;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Add iterable interface (all tokens, tags only, text only)
 *
 */
public class SgmlTokenizer  {
    public enum TokenType {
        tag, text, bof, eof;
        
        public boolean tag()  {return this == tag;}
        public boolean text()  {return this == text;}
        public boolean bof()  {return this == bof;}
        public boolean eof()  {return this == eof;}
    };

    /** 
     * The current token type.
     */
    protected TokenType mTokenType;
    
    /** 
     * The current token. 
     * Includes angle brackets for tags.
     */
    protected String mToken;

    /** 
     * The parsed string.
     */
    protected String mString;

    /*
     * The current position in the parsed string 
     * (the first char following the current token)
     */
    protected int mCur = 0;

    /*
     * The first character of the current token. 
     */
    protected int mStart = -1;

    /*
     * Length of the parsed string. Used to avoid length() calls.
     */
    protected int mLen = 0;       
    
    public SgmlTokenizer(String aString) {
        mString = aString;
        mLen = mString.length();
        mCur = 0;
        mTokenType = TokenType.bof;
    }

    /*
     * The current position in the parsed string 
     * (the first char following the current token)
     */
    public int cur() {return mCur;}
    
    /*
     * The first character of the current token. 
     */
    public int start() {return mStart;};

    /**
     * Returns the current token type.
     * 
     * @see #TokenType 
     */
    public TokenType tokenType() {return mTokenType;}

    /**
     * Returns the current token.
     */
    public String token()     {return mToken;}
    
    /**
     * Returns the integer corresponding to the current token.
     * No checks are performed.
     */
    public int tokenInt()     {return Integer.parseInt(mToken);}
    
    /**
     * Note: constructed each time again (@todo cache?)
     */
    public SgmlTag tag() {
        return new SgmlTag(mToken);
    }
    
    
    
    
    /** 
     * Moves to the next token. The next token becomes the current token.
     * 
     * @return type of the new token
     * @see #TokenType
     */
    public TokenType next() {
        if (mCur >= mLen) {
            mToken = null;
            mTokenType = TokenType.eof;
        }
        else if (mString.charAt(mCur) == '<') {
            //mCur++;
            mToken = scan('>') + '>'; mCur++;
            mTokenType = TokenType.tag;
        }
        else {    
            mToken = StringEscapeUtils.unescapeHtml4(scan('<'));
            mTokenType = TokenType.text;
        }
        return mTokenType;
    }
    

    /**
     * A convenience method, abbreviating next() followed byt token().
     */ 
    public String getNextToken() {
        next();
        return token();
    }
    
// -----------------------------------------------------------------------------    
// Positioning
// -----------------------------------------------------------------------------    
    
    /**
     * Looks for the specified string (its first occurence) and makes it the current token.
     */
    public void positionAt(String aToken) {
        mStart = mString.indexOf(aToken);
        if (mStart == -1) {mCur = mLen; return;}

        mCur = mStart + aToken.length();
    }

    
    /**
     * Makes the token following the specified string the current token.
     * @see positionAt
     */
    public void positionAfter(String aToken) {
        positionAt(aToken);
        next();
    }


    /**
     * Looks for the specified tag (its first occurence) and makes it the current token.
     *
     * @aTagCoreL the core of the desired tag (preceded by '<', e.g. "<l"). 
     *   The parameter is not checked.
     * @see #positionAtTag(String)
     * @todo 
     */
    public void positionAtTagL(String aTagCoreL) {
        mCur = mStart = org.purl.jh.util.str.Strings.indexOfPlus2(mString, aTagCoreL, '>', ' ');    // @todo ?? can be copied over here for performance reasons
        if (mStart == -1) {mCur = mLen; return;}
        next();
    }    
    

    /**
     * Makes the token following the specified tag the current token.
     *
     * @param aTagCoreL the core of the desired tag (preceded by '<', e.g. "<l").
     *   The parameter is not checked.
     * @see #positionAtTagL(String)
     * @see #positionAfterTag(String)
     * @todo
     */
    public void positionAfterTagL(String aTagCoreL) {
        positionAtTagL(aTagCoreL);
        next();
    }


    /**
     * Looks for the specified tag (its first occurence) and makes it the current token.
     *
     * @param aTagCoreL the core of the desired tag (e.g. "l").
     *   The parameter is not checked.
     * @see #positionAtTagL(String)
     * @todo
     */
    public void positionAtTag(String aTagCore) {
        positionAtTagL('<' + aTagCore);
    }

    /**
     * Makes the token following the specified tag the current token.
     *
     * @aTagCoreL the core of the desired tag (e.g. "l"). 
     *   The parameter is not checked.
     * @see #positionAtTag(String)
     * @see #positionAfterTagL(String)
     * @todo
     */
    public  void positionAfterTag(String aTagCore) {
        positionAtTag(aTagCore);
        next();
    }

    /**
     * Looks for the specified tag (its first occurence) and makes it the current token.
     *
     * @param aTagCoreL the core of the desired tag (e.g. "l").
     *   The parameter is not checked.
     * @see #positionAtTagL(String)
     * @todo
     */
    public void positionAtLastTag(String aTagCore) {
        mCur = mStart = mString.lastIndexOf('<' + aTagCore);
        if (mStart == -1) {mCur = mLen; return;}
        next();
    }
    
// =============================================================================
// Only fncs (does not get get the token if not asked)    
// NOT WORKING    
// TODO Drop or finish
// =============================================================================
    

    public String getToken()  {
        if (mCur < mLen) 
            mToken = (mTokenType == TokenType.tag) ? mString.substring(mStart, mCur-1) : mString.substring(mStart, mCur);
        else
            mToken = mString.substring(mStart);
        return mToken;
    }

    public TokenType nextOnly() {
        if (mCur >= mLen) {
            mTokenType = TokenType.eof;
        }
        else if (mString.charAt(mCur) == '<') {
            mCur++;
            scanOnly('>'); mCur++;
            mTokenType = TokenType.tag;
        }
        else {    
            scanOnly('<');
            mTokenType = TokenType.text;
        }
        return mTokenType;
    }

    private void scanOnly(char aDelim) {
        mStart = mCur;
        for(;mCur < mLen; mCur++) {
            if (mString.charAt(mCur) == aDelim) break;
        }
    }

    public boolean tokenEquals(String aToken) {
        System.out.println("start: " + mStart);
        System.out.println("cur:   " + mCur);
        System.out.println("x:     " + mString.substring(mStart,mCur));
        System.out.println("c-s:   " + (mCur-mStart));
        System.out.println("len:   " + aToken + " " + aToken.length());
        return ((mCur-mStart) == aToken.length()) && mString.startsWith(aToken,mStart);
    }

// =============================================================================
// Implementation
// =============================================================================
    /**
     * Scans the token preceding the delimiter or end of the string.
     * Includes the current char, excludes the delimiter.
     */
    private String scan(char aDelim) {
        mStart = mCur;
        for(;mCur < mLen; mCur++) {
            if (mString.charAt(mCur) == aDelim) return mString.substring(mStart, mCur);
        }
        return mString.substring(mStart);
    }
}



