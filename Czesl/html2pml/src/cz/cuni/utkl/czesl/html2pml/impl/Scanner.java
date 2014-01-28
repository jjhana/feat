package cz.cuni.utkl.czesl.html2pml.impl;

/**
 * Wraps text scanner to handle ignored characters.
 * 
 * @author Jirka
 */
public class Scanner {
    
    public static class Token extends Scanner1.Token {
        public Token(Scanner1.Token aCleanToken) {
            type = aCleanToken.type;
            text = aCleanToken.text;
            code = aCleanToken.code;
            codes = aCleanToken.codes;
        }
        
        /** Includes deleted characters. */
        String dirtyText;

        private void setText(DelString aStr, int aFrom, int aToExcl) {
            dirtyText = aStr.getChars().subSequence(aFrom, aToExcl).toString();
            from      = aFrom;
            len       = aToExcl - aFrom;
            //System.out.printf("token2.setText: %s %d %d\n", text, aFrom, aToExcl);
        }

        @Override
        public String toString() {
            return "Token{" + "text=" + text + ", from=" + from + ", len=" + len + ", type=" + type + ", code=" + code + ", codes=" + codes + '}';
        }
        
    }

    /** Deletable String to scan */
    private final DelString text;

    private final Scanner1 cleanScanner;
    
    /** Current position of the scanner */
    int pos;

    public Scanner(DelString aText) {
        //System.out.println("Skanning " + aText.toString());
        text = aText;
        cleanScanner = new Scanner1(aText.clean());
        pos = 0;
    }
    
    public Token next() {
        Token tok = nextI();
        //System.out.println("token2: " + tok);
        return tok;
    }

    public Token nextI() {
        final Scanner1.Token cleanToken = cleanScanner.next();
        if (cleanToken == null) return null;
        //System.out.printf("nextI: pos %d\n", pos);
        
        final Token token = new Token(cleanToken);
        pos = text.skipIgnored(pos); // skip ignored characters

        final int start = pos;
        
        for (int i = 0; i < cleanToken.totLen; i++, pos++) {
            //System.out.printf("[i=%d, pos %d] ", i, pos);
            // assert the 
            //Err.iAssert(text.charAt(pos) == cleanToken.text.charAt(i), "Clean x dirty mismatch [%d] %s", pos, token.text);

            pos = text.skipIgnored(pos); // skip ignored characters
        }
        //System.out.printf("\npost-for: pos %d\n", pos);
        
        token.setText(text, start, pos);
        
        return token;
   }
}
