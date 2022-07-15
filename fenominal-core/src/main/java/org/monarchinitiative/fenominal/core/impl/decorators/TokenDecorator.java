package org.monarchinitiative.fenominal.core.impl.decorators;

import org.monarchinitiative.fenominal.core.impl.corenlp.SimpleToken;

public interface TokenDecorator {
    SimpleToken decorate(SimpleToken simpleToken);
}
