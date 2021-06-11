package org.monarchinitiative.fenominal.core.except;

public class FenominalRunTimeException extends RuntimeException {
    public FenominalRunTimeException() { super();}
    public FenominalRunTimeException(String m) {
        super(m);
    }
}
