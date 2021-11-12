package org.monarchinitiative.fenominal.core.decorators;

import org.monarchinitiative.fenominal.core.corenlp.SimpleToken;

public interface TokenDecorator {
    SimpleToken decorate(SimpleToken simpleToken);
}
