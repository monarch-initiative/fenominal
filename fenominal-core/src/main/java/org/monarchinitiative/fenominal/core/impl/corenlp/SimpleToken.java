package org.monarchinitiative.fenominal.core.impl.corenlp;

import java.util.ArrayList;
import java.util.List;

public class SimpleToken {

    private final String token;
    private final String originalToken;
    private final int startpos;
    private final int endpos;
    private final List<String> decorations;

    public SimpleToken(String token, int startpos, int endpos, int offset) {
        this.originalToken = token;
        this.token = token;
        this.startpos = startpos + offset;
        this.endpos = endpos + offset;
        this.decorations = new ArrayList<>();
    }

    public SimpleToken(String token, String originalToken, int startpos, int endpos) {
        this.token = token;
        this.originalToken = originalToken;
        this.startpos = startpos;
        this.endpos = endpos;
        this.decorations = new ArrayList<>();
    }

    public String getToken() {
        return token;
    }

    public String getOriginalToken() {
        return originalToken;
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

    public void addDecoration(String decoration) {
        this.decorations.add(decoration);
    }

    public boolean hasDecoration(String decoration) {
        return this.decorations.contains(decoration);
    }
}
