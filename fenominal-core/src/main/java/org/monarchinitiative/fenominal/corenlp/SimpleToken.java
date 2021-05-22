package org.monarchinitiative.fenominal.corenlp;

public class SimpleToken {

    private final String token;
    private final int startpos;
    private final int endpos;

    public SimpleToken(String token, int startpos, int endpos, int offset) {
        this.token = token;
        this.startpos = startpos + offset;
        this.endpos = endpos + offset;
    }

    public String getToken() {
        return token;
    }

    public String getLowerCaseToken() {
        return token.toLowerCase();
    }

    public int getStartpos() {
        return startpos;
    }

    public int getEndpos() {
        return endpos;
    }
}
